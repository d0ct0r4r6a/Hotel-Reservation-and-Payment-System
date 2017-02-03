package com.hotel.entities;

import com.hotel.Config;
import com.hotel.core.DateManager;
import com.hotel.core.Entity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Entity of Reservation Object
 * @author Arga Roh Sahrijal Saragih
 * @version 1.0
 */
public class Reservation extends Entity<Reservation> {

    public static String dateFormat = "yyyy-MM-dd HHmm";

    public enum ReservationStatus {
        CONFIRMED, WAITLIST, CHECKED_IN, CHECKED_OUT, EXPIRED
    }

    private ReservationStatus reservationStatus;
    private String reservationCode = null;
    private int numberOfChildren;
    private int numberOfAdults;
    private Date checkInDate;
    private Date checkOutDate;

    private String guestId;
    private String roomId;
    private Guest guest = null;
    private Room room = null;
    private boolean walkIn = false;
    private RoomService roomService;

    public Reservation() {
        super(Reservation.class);
        defautInit();
    }

    public Reservation(HashMap<String, String> data) {
        super(Reservation.class);
        defautInit();
        this.loadHashMap(data);
    }

    public void defautInit() {
        setDateFormat(dateFormat);
        setReservationStatus(ReservationStatus.CONFIRMED);
    }

    /**
     * Creates a unique reservation code
     */
    private void generateReservationCode() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-Hm");
        reservationCode = String.format("%s-%s", sdf.format(new Date()), getGuest().getId());
    }

    public ReservationStatus getReservationStatus() {
        return reservationStatus;
    }


    public void setReservationStatus(ReservationStatus reservationStatus) {
        this.reservationStatus = reservationStatus;
    }

    /**
     * Get the value of reservationCode
     *
     * @return the value of reservationCode
     */
    public String getReservationCode() {
        if (reservationCode == null)
            generateReservationCode();
        return reservationCode;
    }

    /**
     * Set Reservation Identifier
     * @param reservationCode
     */
    public void setReservationCode(String reservationCode) {
        this.reservationCode = reservationCode;
    }

    /**
     * Checks if Reservation can be checked in
     * @return boolean
     */
    public boolean isCheckInDateValid() {
        Date today = new Date();

        if (isNewRecord())
            return getCheckInDate().after(today);

        return true;
    }

    /**
     * Get the value of checkInDate
     *
     * @return the value of checkInDate
     */
    public Date getCheckInDate() {
        return checkInDate;
    }

    /**
     * Set the value of checkInDate
     *
     * @param checkInDate new value of checkInDate
     */
    public void setCheckInDate(Date checkInDate) {
        this.checkInDate = checkInDate;
    }


    /**
     * Get the value of checkOutDate
     *
     * @return the value of checkOutDate
     */
    public Date getCheckOutDate() {
        return checkOutDate;
    }

    /**
     * Set the value of checkOutDate
     *
     * @param checkOutDate new value of checkOutDate
     */
    public void setCheckOutDate(Date checkOutDate) {
        this.checkOutDate = checkOutDate;
    }


    /**
     * Get the value of numberOfAdults
     *
     * @return the value of numberOfAdults
     */
    public int getNumberOfAdults() {
        return numberOfAdults;
    }

    /**
     * Set the value of numberOfAdults
     *
     * @param numberOfAdults new value of numberOfAdults
     */
    public void setNumberOfAdults(int numberOfAdults) {
        this.numberOfAdults = numberOfAdults;
    }

    /**
     * Get the value of numberOfChildren
     *
     * @return the value of numberOfChildren
     */
    public int getNumberOfChildren() {
        return numberOfChildren;
    }

    /**
     * Set the value of numberOfChildren
     *
     * @param numberOfChildren new value of numberOfChildren
     */
    public void setNumberOfChildren(int numberOfChildren) {
        this.numberOfChildren = numberOfChildren;
    }


    public boolean isWalkIn() {
        return walkIn;
    }

    public void setWalkIn(boolean walkIn) {
        this.walkIn = walkIn;
    }

    public Guest getGuest() {
        if (guest == null)
            guest = new Guest().find("id", guestId);

        return guest;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    public Room getRoom() {
        if (room == null)
            room = new Room().find("roomId", roomId);

        return room;
    }

    public RoomService getRoomService() {
        if (roomService == null)
            roomService = new RoomService().find("reservationId", getReservationCode());
        return roomService;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    private void setGuestId(String guestId) {
        this.guestId = guestId;
    }

    private void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    @Override
    public HashMap<String, String> toHashMap() {
        LinkedHashMap<String, String> results = new LinkedHashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);

        results.put("reservationCode", getReservationCode().toString());
        results.put("reservationStatus", getReservationStatus().toString());
        results.put("numberOfChildren", Integer.toString(getNumberOfChildren()));
        results.put("numberOfAdult", Integer.toString(getNumberOfAdults()));
        results.put("checkInDate", sdf.format(getCheckInDate()));
        results.put("checkOutDate", sdf.format(getCheckOutDate()));
        results.put("guestId", getGuest().getId());
        results.put("roomId", getRoom().getRoomId());

        return results;
    }


    @Override
    public void loadHashMap(HashMap<String, String> results) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);

        try {
            setNumberOfChildren(Integer.parseInt(results.get("numberOfChildren")));
            setNumberOfAdults(Integer.parseInt(results.get("numberOfAdult")));
            setCheckInDate(sdf.parse(results.get("checkInDate")));
            setCheckOutDate(sdf.parse(results.get("checkOutDate")));

            // Caught Exception as this are not user entered data
            try {
                setReservationCode(results.get("reservationCode"));
                setReservationStatus(ReservationStatus.valueOf(results.get("reservationStatus")));
                setGuestId(results.get("guestId"));
                setRoomId(results.get("roomId"));
            } catch (Exception e) {}
        } catch (ParseException e) {
            System.out.println("Error loading Check in or Check out dates");
        }
    }

    @Override
    public boolean validate() {
        errors.clear();

        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Date today = null;
        try {
            today = sdf.parse(sdf.format(new Date())); // Parse date to remove all the time
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (!isCheckInDateValid() && !isWalkIn())
            errors.add("Check in date has already past.");

        if (DateManager.getTotalDays(getCheckInDate(), getCheckOutDate()) < 1)
            errors.add("Check out Date cannot be before Check in date or the day itself");

        if (getRoom().getStatus() != Room.RoomStatus.VACANT && isNewRecord())
            errors.add("Invalid Room selected. Room Status: " + getRoom().getAttributeLabel(getRoom().getStatus().toString()));

        return errors.size() == 0;
    }

    @Override
    public HashMap<String, String> getAttributeLabels() {
        return new HashMap<String, String>(){{
            put("reservationCode", "Reservation Code");
            put("reservationStatus", "Reservation Status");
            put("numberOfChildren", "Number of Children");
            put("numberOfAdult", "Number of Adult");
            put("checkInDate", "Check in Date");
            put("checkOutDate", "Check Out Date");

            put("CONFIRMED", "Confirmed");
            put("WAITLIST", "Wait List");
            put("CHECKED_IN", "Checked In");
            put("CHECKED_OUT", "Checked OUt");
            put("EXPIRED", "Expired");
        }};
    }

    @Override
    public String[][] getEditableList() {
        String[][] list = {
            {"numberOfChildren",      Types.INT.toString()},
            {"numberOfAdult",    Types.INT.toString()},
            {"checkInDate", Types.DATE.toString()},
            {"checkOutDate", Types.DATE.toString()},
        };

        return list;
    }

    @Override
    public HashMap<String, Enum[]> getEnumList() {
        return new HashMap() {{
            put("reservationStatus", ReservationStatus.values());
        }};
    }

    @Override
    public boolean save() {
        boolean newRecord = isNewRecord();
        boolean success = super.save();

        // Update Room to be Occupied
        if (success && newRecord) {
            Room r = getRoom();
            r.setStatus(Room.RoomStatus.RESERVED);
            if(!r.save()) {
                for (String error : getErrors())
                    System.out.println(error);
            }
        }

        return success;
    }

    public void checkExpiredReservations() {
        // Check Expired Reservations
        Reservation rsvList[] = super.findAll("reservationStatus", ReservationStatus.CONFIRMED.toString());
        for (Reservation rsv : rsvList) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(rsv.getCheckInDate());
            calendar.add(Calendar.HOUR_OF_DAY, Config.expireHours);

            // Reservation has expired
            if (!calendar.getTime().after(new Date())) {
                rsv.setReservationStatus(ReservationStatus.EXPIRED);
                Room r = rsv.getRoom();
                r.setStatus(Room.RoomStatus.VACANT);

                // Save the records
                rsv.save();
                r.save();
            }
        }
    }

    public boolean checkIn() {
        errors.clear();
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);

        // Checks if past check in timing
        if (!getCheckInDate().before(now))
            errors.add(String.format("You can only check in after %s.", sdf.format(getCheckInDate())));

        // Do Check in
        if (errors.size() == 0) {
            setReservationStatus(ReservationStatus.CHECKED_IN);
            Room room = getRoom();
            room.setStatus(Room.RoomStatus.OCCUPIED);

            if (!(save() && room.save()))
                errors.add("Failed to update Reservation or Room.");

            for (String error : room.getErrors())
                System.out.println(error);
            for (String error : getErrors())
                System.out.println(error);
        }

        return errors.size() == 0;
    }

    public boolean checkOut() {
        if (getReservationStatus() == ReservationStatus.CHECKED_IN) {
            setReservationStatus(ReservationStatus.CHECKED_OUT);
            Room r = getRoom();

            r.setStatus(Room.RoomStatus.VACANT);
            save();
            r.save();
            return true;
        }

        return false;
    }
}
