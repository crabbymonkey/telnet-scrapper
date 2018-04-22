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

    public static Map<String, List<Integer>> scrapAllPortsForIpAddress(String ipAddress, int numThreads) throws IPAddress.InvalidIPAddressValue {
        IPAddress ip = new IPAddress(ipAddress);
        return ip.scrapPortsMultiThread(numThreads);
    }

    public static Map<String, List<Integer>> scrapPortsForIpAddress(String ipAddress, int startingPort, int endingPort, int numThreads) throws IPAddress.InvalidIPAddressValue {
        IPAddress ip = new IPAddress(ipAddress, startingPort, endingPort);
        return ip.scrapPortsMultiThread(numThreads);
    }

}