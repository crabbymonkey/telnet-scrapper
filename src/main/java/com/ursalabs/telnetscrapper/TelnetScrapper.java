package com.ursalabs.telnetscrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class TelnetScrapper {

    public static void main(String[] args) {
        Map<String, List<Integer>> checkedPorts;
        int startPort = 0;
        int endPort = 1000;

        // Get IP address from user
        System.out.print("Please provide the IP address you would like to scrape: ");
        String address = new Scanner(System.in).nextLine();


        ProgressBar bar = new ProgressBar();
        try {
            // Check the ports
            System.out.println("\nScrapping the ports between " + startPort + " and " + endPort + " for " + address + " this may take some time...");
            bar.start();
            //checkedPorts = scrapAllPortsForIpAddress(address, 100);
            checkedPorts = scrapPortsForIpAddress(address, startPort, endPort, 100);
            bar.stopShowingProgressSuccess();

            // Indicate if any ports where not scanned (This shouldn't happen)
            if (!checkedPorts.get("Missing").isEmpty()) {
                System.out.println("\nThe following ports where skipped for the device with IP address " + address + ":");
                for (int port : checkedPorts.get("Missing")) {
                    System.out.println(port);
                }
            }

            // Display to user the open ports
            if (checkedPorts.get("Open") == null || checkedPorts.get("Open").isEmpty()) {
                System.out.println("No ports are being used for the device with IP address " + address);
            } else {
                System.out.println("\nThe following ports are in use for the device with IP address " + address + ":");
                for (int port : checkedPorts.get("Open")) {
                    System.out.println(port);
                }
            }
        } catch (IPAddress.InvalidIPAddressValue invalidIPAddressValue) {
            bar.stopShowingProgressError();
            invalidIPAddressValue.printStackTrace();
        }

    }

    /**
     * Runs the port scrapper for all ports against a single device. This will run it from port 0 to port 65535.
     *
     * @param ipAddress the value to check if it is am IP address.
     * @param numThreads the number of threads you would like to create. If you would like to run it without using threads you can provide null or a value less than or equal to 1 to this field.
     * @return A Hashmap<String, List<Integer>> that will have the keys "Open", "Closed" and "Missing". The Open list will be all the ports that are Open for the device, Closed will be all the closed ports and the Missing list is all the ports that were not checked (this should be empty).
     *
     * @throws IPAddress.InvalidIPAddressValue error if there is something wrong with the given parameters check the message to see what went wrong
     */
    public static Map<String, List<Integer>> scrapAllPortsForIpAddress(String ipAddress, Integer numThreads) throws IPAddress.InvalidIPAddressValue {
        IPAddress ip = new IPAddress(ipAddress);
        return runScrapper(ip, numThreads);
    }

    /**
     * Runs the port scrapper for the given starting and ending ports against a single device.
     *
     * @param ipAddress the value to check if it is am IP address.
     * @param startingPort the port you would like to start scanning at.
     * @param endingPort the port you would like to stop scanning at.
     * @param numThreads the number of threads you would like to create. If you would like to run it without using threads you can provide null or a value less than or equal to 1 to this field.
     * @return A Hashmap<String, List<Integer>> that will have the keys "Open", "Closed" and "Missing". The Open list will be all the ports that are Open for the device, Closed will be all the closed ports and the Missing list is all the ports that were not checked (this should be empty).
     *
     * @throws IPAddress.InvalidIPAddressValue error if there is something wrong with the given parameters check the message to see what went wrong
     */
    public static Map<String, List<Integer>> scrapPortsForIpAddress(String ipAddress, int startingPort, int endingPort, Integer numThreads) throws IPAddress.InvalidIPAddressValue {
        IPAddress ip = new IPAddress(ipAddress, startingPort, endingPort);
        return runScrapper(ip, numThreads);
    }

    private static Map<String, List<Integer>> runScrapper(IPAddress ip, Integer numThreads) {
        if (numThreads == null || numThreads <= 1) {
            return ip.scrapPortsSingleThread();
        } else {
            return ip.scrapPortsMultiThread(numThreads);
        }
    }

}