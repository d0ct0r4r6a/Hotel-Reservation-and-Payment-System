package com.hotel.controls;

import com.hotel.core.Boundary;
import com.hotel.core.Control;
import com.hotel.entities.Room;

/**
 * Maintenance Control Object
 * Fetch information from Maintenance Entity and pass on to Maintenance Boundary
 * @author  Tan Jun Xiang
 * @version 1.0
 */
public class MaintenanceControl implements Control {
    private Boundary mb;

    private static final String menu[] = {
        "Maintenance Menu",
        "Rooms",
        "Menu Items",
    };

    private static final String roomMenu[] = {
        "Maintenance Menu (Room)",
        "Set Room for Maintenance",
        "Set Room for Vacant",
    };

    public MaintenanceControl() {
        mb = new Boundary();
    }

    @Override
    public void process() {
        int input = mb.process(menu);
        switch(input) {
            case 1:
                roomProcess();
                break;
            case 2:
                new MenuItemControl().process();
                break;
            case 0:
                return;
            default:
                mb.functionNotDeployed();
        }

        // Easier Recursive
        if (input != 0)
            process();
    }

    public void roomProcess() {
        int input = mb.process(roomMenu);
        switch(input) {
            case 1:
                new RoomControl().setRoomStatus(Room.RoomStatus.UNDER_MAINTENANCE, Room.RoomStatus.VACANT);
                break;
            case 2:
                new RoomControl().setRoomStatus(Room.RoomStatus.VACANT, Room.RoomStatus.UNDER_MAINTENANCE);
                break;
            case 0:
                break;
            default:
                mb.functionNotDeployed();
        }

        // Easier Recursive
        if (input != 0)
            process();
    }
}
