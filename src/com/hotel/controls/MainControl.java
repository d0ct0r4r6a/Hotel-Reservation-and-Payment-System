package com.hotel.controls;

import com.hotel.core.Boundary;
import com.hotel.core.Control;

/**
 * Main Control Object
 * Fetch information from Main Entity and pass on to Boundary
 * @author  Tan Jun Xiang
 * @version 1.0
 */
public class MainControl implements Control {
    private Boundary mc;

    private static final String mainMenu[] = {
        "Hotel Reservation System (HRS)",
        "Guest",
        "Reservation",
        "Walk In",
        "Check Out",
        "Room Service",
        "Reports",
        "Maintenance"
    };

    /**
     * Default Constructor of MainControl
     */
    public MainControl() {
        this.mc = new Boundary();
    }

    @Override
    public void process() {
        int input = mc.process(mainMenu, true);
        switch(input) {
            case 1:
                new GuestControl().process();
                break;
            case 2:
                new ReservationControl().process();
                break;
            case 3:
                new ReservationControl().walkIn();
                break;
            case 4:
                new ReservationControl().checkOut();
                break;
            case 5:
                new RoomServiceControl().process();
                break;
            case 6:
                new ReportsControl().process();
                break;
            case 7:
                new MaintenanceControl().process();
                break;
            case 0:
                break;
            default:
                mc.functionNotDeployed();
        }

        // Easier Recursive
        if (input != 0)
            process();
    }
}
