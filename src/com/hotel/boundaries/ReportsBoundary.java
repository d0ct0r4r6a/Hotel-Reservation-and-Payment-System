package com.hotel.boundaries;

import com.hotel.core.Boundary;
import com.hotel.entities.Room;

import java.util.HashMap;

/**
 * Reports Boundary Object
 * Manages the Input/Output for ReportControl
 * Formats and prints the report to user
 * @author  Tan Jun Xiang
 * @version 1.0
 */
public class ReportsBoundary extends Boundary {

    public void printRoomTypeOccupancy(HashMap<String, HashMap<String, Room[]>> result, Room r) {
        System.out.println("Room Type Occupancy Report");
        for(Object o : result.keySet()) {
            Room matched[] = result.get(o).get("matched");
            Room unmatched[] = result.get(o).get("unmatched");
            System.out.println(String.format("%s\t: %d out of %d", r.getAttributeLabel(o.toString()), matched.length, matched.length + unmatched.length));

            if (matched.length == 0){
                System.out.println();
                continue;
            }


            String roomStr = "";
            for (Room room : matched)
                roomStr += room.getRoomId() + ", ";

            roomStr = roomStr.substring(0, roomStr.length() - 2);

            System.out.println(String.format("Rooms : %s", roomStr));
            System.out.println();

        }
        getString();
    }

    public void printReportRoomStatus(HashMap<String, Room[]> result, Room r) {
        System.out.println("Room Status Report");
        for(Object o : result.keySet()) {
            Room matched[] = result.get(o);
            System.out.println(String.format("%s\t: %d Rooms", r.getAttributeLabel(o.toString()), matched.length));

            if (matched.length == 0) {
                System.out.println();
                continue;
            }


            String roomStr = "";
            for (Room room : matched)
                roomStr += room.getRoomId() + ", ";

            roomStr = roomStr.substring(0, roomStr.length() - 2);

            System.out.println(String.format("Rooms : %s", roomStr));
            System.out.println();

        }
        getString();
    }
}
