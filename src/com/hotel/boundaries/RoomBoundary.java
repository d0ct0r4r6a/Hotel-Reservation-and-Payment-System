package com.hotel.boundaries;

import com.hotel.core.Boundary;
import com.hotel.entities.Room;

/**
 * Room Boundary Object
 * Manages the Input/Output for Rooms Manipulation
 * @author  Tan Jun Xiang
 * @version 1.0
 */
public class RoomBoundary extends Boundary{

    public Room chooseRoom(Room[] roomList) {
        if (roomList.length == 0) {
            noResultsFound();
            return null;
        }

        String[] results = new String[roomList.length + 1];
        results[0] = "Choose Room:";
        int i = 1;
        for (Room room : roomList)
            results[i++] = String.format("%s - %s", room.getRoomId(), room.getAttributeLabel(room.getRoomType().toString()));

        int input = processMenu(results, false, true);
        if (input == 0)
            return null;
        return roomList[input - 1];
    }
}
