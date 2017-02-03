package com.hotel.controls;

import com.hotel.boundaries.ReportsBoundary;
import com.hotel.core.Control;
import com.hotel.entities.Report;
import com.hotel.entities.Room;
import com.hotel.entities.RoomService;

import java.util.HashMap;

/**
 * Reports Control Object
 * Fetch information from Reports Entity and pass on to Reports Boundary
 * @author  Tan Jun Xiang
 * @version 1.0
 */
public class ReportsControl implements Control {
    private ReportsBoundary rb;
    private static final String menu[] = {
        "Reports System",
        "Room Type Occupancy Rate",
        "Room Status",
    };

    /**
     * Default Constructor of ReportsControl
     */
    public ReportsControl() {
        rb = new ReportsBoundary();
    }

    @Override
    public void process() {
        int input = rb.process(menu);
        switch(input) {
            case 1:
                reportRoomTypeOccupancy();
                break;
            case 2:
                reportRoomStatus();
                break;
            case 0:
                break;
            default:
                rb.functionNotDeployed();
        }

        // Easier Recursive
        if (input != 0)
            process();
    }


    /**
     * Print Room Type Occupancy Report
     */
    private void reportRoomTypeOccupancy() {
        Room r = new Room();
        Enum roomStatus = rb.processEnum("Choose Room Type", Room.RoomStatus.values(), r);
        HashMap result = new Report().genRoomTypeOccupancy(Room.RoomStatus.valueOf(roomStatus.toString()));
        rb.printRoomTypeOccupancy(result, r);
    }

    /**
     * Print Room Status Report
     */
    private void reportRoomStatus() {
        rb.printReportRoomStatus(new Report().genRoomStatus(), new Room());
    }


}
