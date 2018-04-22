package com.ursalabs.telnetscrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class TelnetScrapper {

//    static int MIN_PORT_NUMBER = 0;       // Full range start
//    static int MAX_PORT_NUMBER = 65535;   // Full range end

    static int MIN_PORT_NUMBER = 50;    // Test sample start
    static int MAX_PORT_NUMBER = 100;   // Test sample end

    public static void main(String[] args) {
        Scanner reader = new Scanner(System.in);
        List<Integer> openPorts;

        System.out.print("Please provide the IP address you would like to scrape: ");

        try {
            IPAddress ip = new IPAddress(reader.nextLine(), MIN_PORT_NUMBER, MAX_PORT_NUMBER);

            System.out.println("\n\nScrapping the ports between " + MIN_PORT_NUMBER + " and " + MAX_PORT_NUMBER + " for " + ip.getAddress() + " this may take some time...");

            openPorts = ip.scrapPorts();

            if (openPorts == null || openPorts.size() == 0) {
                System.out.println("No ports are being used for the device with IP address " + ip.getAddress());
            } else {
                System.out.println("\nThe following ports are in use for the device with IP address " + ip.getAddress() + ":");
                for (int port : openPorts) {
                    System.out.println(port);
                }
            }
        } catch (IPAddress.InvalidIPAddressValue invalidIPAddressValue) {
            invalidIPAddressValue.printStackTrace();
        }


    }


}