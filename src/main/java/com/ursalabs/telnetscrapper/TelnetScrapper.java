package com.ursalabs.telnetscrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class TelnetScrapper {

    static int MIN_PORT_NUMBER = 0;
    static int MAX_PORT_NUMBER = 65535;

    public static void main(String[] args) {
        Scanner reader = new Scanner(System.in);
        List<Integer> openPorts = new ArrayList<Integer>();

        System.out.print("Please provide the IP address you would like to scrape: ");

        try {
            IPAddress ip = new IPAddress(reader.nextLine());
            
            System.out.println("\n\nScrapping the ports between " + MIN_PORT_NUMBER + " and " + MAX_PORT_NUMBER + " for " + ip + " this may take some time...");
            
            
            
            

            if (openPorts.size() == 0) {
                System.out.println("No ports are being used for the device with IP address " + ip);
            } else {
                System.out.println("The following ports are in use for the device with IP address " + ip + ":");
                for (int port : openPorts) {
                    System.out.println(port);
                }
            }
        } catch (IPAddress.InvalidIPAddressValue invalidIPAddressValue) {
            invalidIPAddressValue.printStackTrace();
        }
        


    }


}