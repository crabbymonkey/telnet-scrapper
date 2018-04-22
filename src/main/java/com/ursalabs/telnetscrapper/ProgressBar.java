package com.ursalabs.telnetscrapper;

class ProgressBar extends Thread {
    private boolean showProgress = true;

    public void run() {
        String anim = "|/-\\";
        int x = 0;
        while (showProgress) {
            System.out.print("\r[" + anim.charAt(x++ % anim.length()) + "] Scanning Ports");
            try {
                Thread.sleep(100);
            } catch (Exception e) {
            }
            ;
        }
    }

    public void stopShowingProgressSuccess() {
        showProgress = false;
        System.out.println("\r[+] Scanning Ports (successful)");
    }

    public void stopShowingProgressError() {
        showProgress = false;
        System.out.println("\r[x] Scanning Ports (error)");
    }
}
