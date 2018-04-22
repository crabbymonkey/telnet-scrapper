package com.ursalabs.telnetscrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class TelnetScrapper {

    public static void main(String[] args) {
        Map<String, List<Integer>> checkedPorts;

        System.out.print("Please provide the IP address you would like to scrape: ");
        String address = new Scanner(System.in).nextLine();


        try {
            checkedPorts = scrapAllPortsForIpAddress(address);

            if (!checkedPorts.get("Missing").isEmpty()) {
                System.out.println("\nThe following ports where skipped for the device with IP address " + address + ":");
                for (int port : checkedPorts.get("Missing")) {
                    System.out.println(port);
                }
            }

            if (checkedPorts.get("Open") == null || checkedPorts.get("Open").isEmpty()) {
                System.out.println("No ports are being used for the device with IP address " + address);
            } else {
                System.out.println("\nThe following ports are in use for the device with IP address " + address + ":");
                for (int port : checkedPorts.get("Open")) {
                    System.out.println(port);
                }
            }
        } catch (IPAddress.InvalidIPAddressValue invalidIPAddressValue) {
            invalidIPAddressValue.printStackTrace();
        }

    }

    public static Map<String, List<Integer>> scrapAllPortsForIpAddress(String ipAddress) throws IPAddress.InvalidIPAddressValue {
        IPAddress ip = new IPAddress(ipAddress);
        return ip.scrapPortsMultiThread(100);
    }

    public static Map<String, List<Integer>> scrapPortsForIpAddress(String ipAddress, int startingPort, int endingPort) throws IPAddress.InvalidIPAddressValue {
        IPAddress ip = new IPAddress(ipAddress, startingPort, endingPort);
        return ip.scrapPortsMultiThread(100);
    }

}