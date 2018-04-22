package com.ursalabs.telnetscrapper;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class CheckPorts extends Thread {

    private final String ip;
    private final int startPort;
    private final int endPort;
    private List<Integer> openPorts = new ArrayList<Integer>();
    private List<Integer> closedPorts = new ArrayList<Integer>();
    private Status currentStatus;

    enum Status {
        INITIALIZED, RUNNING, FINISHED;
    }

    public CheckPorts(String ip, int startPort, int endPort) {
        this.ip = ip;
        this.endPort = endPort;
        this.startPort = startPort;
        this.currentStatus = Status.INITIALIZED;
    }

    @Override
    public void run() {
        this.currentStatus = Status.RUNNING;

        for (int port = startPort; port < endPort; port++) {
            if (deviceListening(ip, port)) {
                openPorts.add(port);
//                System.out.println("Port " + port + ": " + "OPEN!!!");
            } else {
                closedPorts.add(port);
//                System.out.println("Port " + port + ": " + "Closed");
            }
        }

        this.currentStatus = Status.FINISHED;
    }

    /**
     * Checks if a device is listening to a given port
     *
     * @param host the IP address of the device being checked
     * @param port the port to check
     * @return True if the host is listening to the port
     */
    private static boolean deviceListening(String host, int port) {
        // Assume no connection is possible.
        try {
            (new Socket(host, port)).close();
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    public int getEndPort() {
        return endPort;
    }

    public int getStartPort() {
        return startPort;
    }

    public List<Integer> getOpenPorts() {
        return openPorts;
    }

    public List<Integer> getClosedPorts() {
        return closedPorts;
    }

    public String getIp() {
        return ip;
    }

    public Status getCurrentStatus() {
        return currentStatus;
    }
}