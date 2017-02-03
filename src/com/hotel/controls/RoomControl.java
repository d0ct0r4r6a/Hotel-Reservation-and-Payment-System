package com.hotel.controls;

import com.hotel.boundaries.RoomBoundary;
import com.hotel.core.Control;
import com.hotel.entities.Guest;
import com.hotel.entities.Room;

/**
 * Room Control Object
 * Fetch information from Room Entity and pass on to Room Boundary
 * @author  Tan Jun Xiang
 * @version 1.0
 */
public class RoomControl implements Control {
    RoomBoundary rb;

    public RoomControl(){
        rb = new RoomBoundary();
    }

    @Override
    public void process() {}

    protected Room selectRoom() {
        Room r = new Room();
        Enum e = rb.processEnum("Select Room Type", Room.RoomType.values(), r);

        Room rList[] = r.findAll("roomType", e.toString(), "status", Room.RoomStatus.VACANT.toString());
        if (rList.length == 0) {
            rb.noResultsFound();
            return null;
        }

        return rList[0];
    }

    protected Room search() {
        rb.println(new Room().getAttributeLabel("roomId"));
        String roomId = rb.getString();
        Room room = new Room().find("roomId", roomId);

        return room;
    }

    public void setRoomStatus(Room.RoomStatus roomStatus, Room.RoomStatus prevStatus) {
        System.out.println("Room ID:");
        String roomId = rb.getString();

        Room r = new Room().find("roomId", roomId);
        if (r == null) {
            rb.printInvalidInput();
            return;
        }

        if (r.getStatus() != prevStatus) {
            System.out.println(String.format("Room is currently %s. Cannot be updated.", r.getAttributeLabel(r.getStatus().toString())));
            return;
        }

        boolean update = rb.checkRecord(r, "Room Details", false);

        if (!update)
            return;

        // Update Room Status
        r.setStatus(roomStatus);
        if (r.save())
            System.out.println(String.format("Room has been set %s.", r.getAttributeLabel(roomStatus.toString())));
        else
            System.out.println("Failed to updated Room");

    }


}
