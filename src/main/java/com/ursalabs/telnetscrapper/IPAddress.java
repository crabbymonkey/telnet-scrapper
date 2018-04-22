package com.ursalabs.telnetscrapper;

import java.io.IOException;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class IPAddress {

    private String address;
    private final int minPort;
    private final int maxPort;

    IPAddress(String address) throws InvalidIPAddressValue {
        validateAddress(address);
        this.address = address;
        minPort = 0;    // Default Value that covers all ports
//        maxPort = 65535;// Default Value that covers all ports
        maxPort = 1000;
    }

    IPAddress(String address, int minPort, int maxPort) throws InvalidIPAddressValue {
        validateAddress(address);
        this.address = address;
        this.minPort = minPort;
        this.maxPort = maxPort;
    }

    public Map<String, List<Integer>> scrapPortsMultiThread(int numThreads) {
        int subrange_length = ((maxPort + 1) - minPort) / (numThreads - 1);  // Get values to make numThreads threads (The -1 is to account for the thread that does the overhang values)
        int subrange_length_extra = ((maxPort + 1) - minPort) % numThreads;
        List<CheckPorts> allCheckers = new ArrayList<CheckPorts>(); // Get values to make numThreads threads this will be a list of all the treads
        ProgressBar bar = new ProgressBar();

        int current_start = minPort;


        System.out.println("\nScrapping the ports between " + minPort + " and " + maxPort + " for " + address + " this may take some time...");
        bar.start();

        //TODO: Unit test that makes sure it does all outputs
        //TODO: I think there is an issue with making an extra thread because of the numThreads - 1 used in calculation above
        for (int index = 0; index < numThreads; index++) {
            CheckPorts portChecker = new CheckPorts(this.address, current_start, (current_start + subrange_length));
            allCheckers.add(portChecker);
            portChecker.start();

            current_start += subrange_length;
        }

        // Start that last thread
        if (subrange_length_extra > 0) {
            CheckPorts portChecker = new CheckPorts(this.address, current_start, (current_start + subrange_length_extra));
            allCheckers.add(portChecker);
            portChecker.start();
        }

        // Join the threads so we can wait for all of them to end
        // Note: this has to be in a loop separate to the start or you will just get one at a time
        for (CheckPorts portChecker : allCheckers) {
            try {
                portChecker.join();
            } catch (InterruptedException e) {
                bar.stopShowingProgressError();
                e.printStackTrace();
            }
        }

        bar.stopShowingProgressSuccess();

        return this.getPortsFromThreads(allCheckers);
    }

    public Map<String, List<Integer>> scrapPortsSingleThread() {
        ProgressBar bar = new ProgressBar();
        CheckPorts portChecker = new CheckPorts(this.address, minPort, maxPort);

        bar.start();
        portChecker.start();

        try {
            portChecker.join();
        } catch (InterruptedException e) {
            bar.stopShowingProgressError();
            e.printStackTrace();
        }

        bar.stopShowingProgressSuccess();

        return getPortsFromThreads(Collections.singletonList(portChecker));
    }

    private void validateAddress(String address) throws InvalidIPAddressValue {
        if (!validIP(address)) {
            throw new InvalidIPAddressValue("Invalid address format");
        }

        if (!deviceIsReachable(address)) {
            throw new InvalidIPAddressValue("Connection timed out address is most likely not reachable");
        }
    }

    /**
     * Checks if the given value is a valid IP address.
     *
     * @param address the value to check if it is am IP address.
     * @return True if the value is an IP False if not
     */
    private static boolean validIP(String address) {
        try {
            if (address == null || address.isEmpty()) {
                return false;
            }

            if (address.equals("localhost")) {
                return true;
            }

            String[] parts = address.split("\\.");
            if (parts.length != 4) {
                return false;
            }

            for (String s : parts) {
                int i = Integer.parseInt(s);
                if ((i < 0) || (i > 255)) {
                    return false;
                }
            }
            if (address.endsWith(".")) {
                return false;
            }

            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    /**
     * Checks if a device is reachable from the current host
     *
     * @param address the IP address of the device being checked
     * @return True if the device is reachable
     */
    private static boolean deviceIsReachable(String address) {
        try {
            // Try to connect to the device with a 5 second timeout
            return InetAddress.getByName(address).isReachable(5000);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Map<String, List<Integer>> getPortsFromThreads(List<CheckPorts> allCheckers) {
        Map<String, List<Integer>> checkedPorts = new HashMap<String, List<Integer>>();

        // Added the open and closed ports
        for (CheckPorts portChecker : allCheckers) {
            if (checkedPorts.isEmpty()) {
                checkedPorts.put("Open", portChecker.getOpenPorts());
                checkedPorts.put("Closed", portChecker.getClosedPorts());
            } else {
                checkedPorts.get("Open").addAll(portChecker.getOpenPorts());
                checkedPorts.get("Closed").addAll(portChecker.getClosedPorts());
            }
        }

        // Find if any ports are missing
        List<Integer> missingPorts = new ArrayList<Integer>();
        for (int index = minPort; index >= maxPort; index++) {
            if (!checkedPorts.get("Open").contains(index) && !checkedPorts.get("Closed").contains(index)) {
                missingPorts.add(index);
            }
        }
        checkedPorts.put("Missing", missingPorts);

        return checkedPorts;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    class InvalidIPAddressValue extends Exception {
        // Parameterless Constructor
        public InvalidIPAddressValue() {
        }

        // Constructor that accepts a message
        InvalidIPAddressValue(String message) {
            super(message);
        }
    }
}