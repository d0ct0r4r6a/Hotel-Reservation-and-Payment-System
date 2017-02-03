package com.hotel.entities;

import com.hotel.core.Entity;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Entity of Room Object
 * @author Angel
 * @version 1.0
 */
public class Room extends Entity<Room> {
    // Enumerations
    public enum RoomStatus {
        VACANT, OCCUPIED, RESERVED, UNDER_MAINTENANCE
    }

    public enum RoomType {
        SINGLE, STANDARD, DELUXE, VIP
    }

    public enum BedType {
        SINGLE_BED, DOUBLE_BED, MASTER_BED
    }

    public enum FacingType {
        SEA_VIEW, CITY_VIEW, MOUNTAIN_VIEW, NO_VIEW
    }

    // Variable Declaration
    private RoomType 	roomType;
    private double 		roomRate;
    private double      roomWeekendRate;
    private int         roomFloor;
    private int 		roomNumber;
    private boolean 	wifiEnabled;
    private boolean 	smokingAllowed;
    private BedType 	bedType;
    private FacingType 	facing;
    private RoomStatus 	status;

    // Dynamic Relationship
    private Reservation reservation = null;



    public Room() {
        super(Room.class);
    }

    public Room(HashMap<String, String> data) {
        super(Room.class);
        this.loadHashMap(data);
    }

    public RoomType getRoomType() {return roomType;}
    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public double getRoomRate() {return roomRate;}
    public void setRoomRate(double roomRate) {
        this.roomRate = roomRate;
    }

    public double getRoomWeekendRate() {return roomWeekendRate;}
    public void setRoomWeekendRate(double roomWeekendRate) {
        this.roomWeekendRate = roomWeekendRate;
    }


    public int getRoomNumber() {return roomNumber;}
    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public BedType getBedType() {return bedType;}
    public void setBedType(BedType bedType) {this.bedType = bedType;}

    public boolean isWifiEnabled() {return wifiEnabled;}
    public void setWifiEnabled(boolean wifiEnabled) {this.wifiEnabled = wifiEnabled;}

    public FacingType getFacing() {return facing;}
    public void setFacing(FacingType facing) {this.facing = facing;}

    public boolean isSmokingAllowed() {return smokingAllowed;}
    public void setSmokingAllowed(boolean smokingAllowed) {this.smokingAllowed = smokingAllowed;}

    public RoomStatus getStatus() {return status;}
    public void setStatus(RoomStatus status) {this.status = status;}

    public int getRoomFloor() {
        return roomFloor;
    }
    public void setRoomFloor(int roomFloor) {
        this.roomFloor = roomFloor;
    }

    public String getRoomId() {
        return String.format("%02d", getRoomFloor()) + String.format("%02d", getRoomNumber());
    }

    public Reservation getReservation() {
        if (reservation == null)
            setReservation(new Reservation().find("roomId", getRoomId()));

        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    @Override
    public HashMap<String, String> toHashMap() {
        LinkedHashMap<String, String> result = new LinkedHashMap<>();
        result.put("roomId", getRoomId());
        result.put("roomType", getRoomType().toString());
        result.put("roomRate", Double.toString(getRoomRate()));
        result.put("roomWeekendRate", Double.toString(getRoomWeekendRate()));
        result.put("roomFloor", Integer.toString(getRoomFloor()));
        result.put("roomNumber", Integer.toString(getRoomNumber()));
        result.put("wifiEnabled", Boolean.toString(isWifiEnabled()));
        result.put("smokingAllowed", Boolean.toString(isSmokingAllowed()));
        result.put("bedType", getBedType().toString());
        result.put("facing", getFacing().toString());
        result.put("status", getStatus().toString());

        return result;
    }

    @Override
    public void loadHashMap(HashMap<String, String> result) {
        setRoomType(RoomType.valueOf(result.get("roomType")));
        setRoomRate(Double.parseDouble(result.get("roomRate")));
        setRoomWeekendRate(Double.parseDouble(result.get("roomWeekendRate")));
        setRoomFloor(Integer.parseInt(result.get("roomFloor")));
        setRoomNumber(Integer.parseInt(result.get("roomNumber")));
        setWifiEnabled(Boolean.parseBoolean(result.get("wifiEnabled")));
        setSmokingAllowed(Boolean.parseBoolean(result.get("smokingAllowed")));
        setBedType(BedType.valueOf(result.get("bedType")));
        setFacing(FacingType.valueOf(result.get("facing")));
        setStatus(RoomStatus.valueOf(result.get("status")));
    }

    @Override
    public boolean validate() {
        errors.clear();
        if (this.getRoomFloor() < 2 || this.getRoomFloor() > 99)
            errors.add("Invalid Room Floor. Floor number can only be from 2 - 99.");
        if (this.getRoomNumber() < 1 || this.getRoomNumber() > 99)
            errors.add("Invalid Room Number. Room Number can only be from 1 - 99.");

        if (!checkIdUnique("roomId", getRoomId()))
            errors.add("Duplicate Room Id found. Please update Room Floor or Room Unit Number");

        return errors.size() == 0;
    }

    @Override
    public HashMap<String, String> getAttributeLabels() {
        return new HashMap<String, String>(){{
            put("roomId", "Room ID");
            put("roomType", "Room Type");
            put("roomRate", "Room Rate");
            put("roomWeekendRate", "Room Weekend Rate");
            put("roomFloor", "Room Floor");
            put("wifiEnabled", "Wifi Enabled");
            put("smokingAllowed", "Smoking Allowed");
            put("bedType", "Bed Type");
            put("facing", "Direction Facing");
            put("status", "Room Status");

            put("VACANT", "Vacant");
            put("OCCUPIED", "Occupied");
            put("RESERVED", "Reserved");
            put("UNDER_MAINTENANCE", "Under Maintenance");


            put("SINGLE", "Single Room");
            put("STANDARD", "Standard Room");
            put("VIP", "VIP Room");
            put("DELUXE", "Deluxe Room");

            put("SINGLE_BED", "Single Bed");
            put("DOUBLE_BED", "Double Bed");
            put("MASTER_BED", "Master Bed");

            put("SEA_VIEW", "Sea View");
            put("CITY_VIEW", "City View");
            put("MOUNTAIN_VIEW", "Mountain View");
            put("NO_VIEW", "No View");
        }};
    }

    @Override
    public String[][] getEditableList() {
        String[][] list = {
            {"roomType",  Types.ENUM.toString()},
            {"roomRate",      Types.CURRENCY.toString()},
            {"roomWeekendRate",    Types.CURRENCY.toString()},
            {"roomFloor", Types.INT.toString()},
            {"roomNumber", Types.INT.toString()},
            {"wifiEnabled",  Types.BOOLEAN.toString()},
            {"smokingAllowed",Types.BOOLEAN.toString()},
            {"bedType", Types.ENUM.toString()},
            {"facing", Types.ENUM.toString()},
        };
        return list;
    }

    @Override
    public HashMap<String, Enum[]> getEnumList() {
        return new HashMap() {{
            put("status", RoomStatus.values());
            put("roomType", RoomType.values());
            put("bedType", BedType.values());
            put("facing", FacingType.values());
        }};
    }
}
