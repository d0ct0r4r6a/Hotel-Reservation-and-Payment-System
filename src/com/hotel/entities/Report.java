package com.hotel.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Entity of Report Object
 * Generates Reports
 * @author  Tan Jun Xiang
 * @version 1.0
 */
public class Report {
    private Room[] roomList;

    public Report() {
        this.roomList = new Room().findAll();
    }

    public HashMap genRoomTypeOccupancy(Room.RoomStatus roomStatus) {
        HashMap<String, HashMap<String, Room[]>> results = new LinkedHashMap<>();

        // Loop through all Room Types
        for (Room.RoomType rt : Room.RoomType.values()) {
            HashMap<String, Room[]> rooms = new HashMap<>();
            ArrayList matched = new ArrayList();
            ArrayList unmatched = new ArrayList();
            // Loop through all Rooms
            for (Room r : new Room().findAll("roomType", rt.toString())) {
                if (r.getStatus() == roomStatus)
                    matched.add(r);
                else
                    unmatched.add(r);
            }

            rooms.put("matched", (Room[]) matched.toArray(new Room[matched.size()]));
            rooms.put("unmatched", (Room[]) unmatched.toArray(new Room[unmatched.size()]));
            results.put(rt.toString(), rooms);
        }
        return results;
    }

    public HashMap genRoomStatus() {
        HashMap<String, Room[]> results = new LinkedHashMap<>();

        // Loop through all Room Types
        for (Room.RoomStatus status : Room.RoomStatus.values()) {
            Room rooms[] = new Room().findAll("status", status.toString());
            results.put(status.toString(), rooms);
        }
        return results;
    }
}
