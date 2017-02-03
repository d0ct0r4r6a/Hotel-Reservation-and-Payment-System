package com.hotel;

import com.hotel.controls.MainControl;

/**
 * Main function to start the program
 * @author  Tan Jun Xiang
 * @version 1.0
 */
public class Main {
    /**
     * Main Method
     * @param args Arguments
     */
    public static void main(String[] args) {
        MainControl mc = new MainControl();
        mc.process();
    }
}
