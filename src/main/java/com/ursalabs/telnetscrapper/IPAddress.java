package com.ursalabs.telnetscrapper;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.ListIterator;

public class IPAddress {

    private String address;
    private final int minPort;
    private final int maxPort;

    IPAddress(String address) throws InvalidIPAddressValue {
        validateAddress(address);
        this.address = address;
        minPort = 0;    // Default Value
        maxPort = 65535;// Default Value
    }

    IPAddress(String address, int minPort, int maxPort) throws InvalidIPAddressValue {
        validateAddress(address);
        this.address = address;
        this.minPort = minPort;
        this.maxPort = maxPort;
    }

    public List<Integer> scrapPorts() {
        CheckPorts portChecker = new CheckPorts(this.address, this.minPort, this.maxPort);

        portChecker.run();

        while (portChecker.getCurrentStatus() != CheckPorts.Status.FINISHED){}

        return portChecker.getOpenPorts();
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
