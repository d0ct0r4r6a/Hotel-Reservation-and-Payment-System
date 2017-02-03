package com.hotel.controls;

import com.hotel.boundaries.ReservationBoundary;
import com.hotel.core.Control;
import com.hotel.entities.Guest;
import com.hotel.entities.Payment;
import com.hotel.entities.Reservation;
import com.hotel.entities.Room;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Reservation Control Object
 * Fetch information from Reservation Entity and pass on to Reservation Boundary
 * @author  Tan Jun Xiang
 * @version 1.0
 */
public class ReservationControl implements Control {
    private ReservationBoundary rb;

    private static final String menu[] = {
            "Reservation System",
            "Create Reservation",
            "Print All Reservation",
            "Check In",
    };

    private static final String createMenu[] = {
            "Reservation System",
            "Create Guest",
            "Search Guest",
    };

    private static final String checkInMenu[] = {
            "Reservation System (Check In)",
            "Search Guest",
            "Search Room Number",
            "Search Reservation Code",
    };

    private static final String checkOutMenu[] = {
            "Reservation System (Check Out)",
            "Search Guest",
            "Search Room Number",
            "Search Reservation Code",
    };


    /**
     * Default constructor of ReservationControl
     */
    public ReservationControl() {
        rb = new ReservationBoundary();
    }

    @Override
    public void process() {
        int input = rb.process(menu);
        switch(input) {
            case 1:
                createReservation(false);
                break;
            case 2:
                printAllReservation();
                break;
            case 3:
                checkIn();
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
     * Print all reservations with CONFIRMED status
     */
    private void printAllReservation() {
        new Reservation().checkExpiredReservations();

        Reservation[] rsvList = new Reservation().findAll("reservationStatus", Reservation.ReservationStatus.CONFIRMED.toString());
        if (rsvList.length == 0) {
            rb.noResultsFound();
            return;
        }

        String rsvString[] = new String[rsvList.length + 1];

        rsvString[0] = "Reservations List";
        int i = 1;
        for (Reservation rsv : rsvList)
            rsvString[i++] = String.format("%s - %s - %s", rsv.getReservationCode(), rsv.getRoom().getRoomId(), rsv.getGuest().getName());

        int input = rb.processMenu(rsvString, false);
        if (input == 0)
            return;

        Reservation rsv = rsvList[input - 1];
        rb.checkRecord(rsv, "Reservation Record", false);
    }


    /**
     * Create a reservation
     * @param isWalkIn walk in or reservation
     */
    private void createReservation(boolean isWalkIn) {
        new Reservation().checkExpiredReservations();

        int input = rb.process(createMenu);
        switch (input) {
            case 1:
                createGuest(isWalkIn);
                break;
            case 2:
                searchGuest(isWalkIn);
                break;
            case 0:
                break;
            default:
                rb.functionNotDeployed();
        }
    }

    /**
     * Call GuestControl to create a Guest Object
     * @param isWalkIn walk in or reservation
     */
    private void createGuest(boolean isWalkIn) {
        GuestControl gc = new GuestControl();
        Guest guest = gc.createGuest();
        createReservation(guest, isWalkIn);

    }

    /**
     * Call GuestControl to find a Guest Object
     * @param isWalkIn walk in or reservation
     */
    private void searchGuest(boolean isWalkIn) {// Search for Guest
        GuestControl gc = new GuestControl();
        Guest g = gc.search();
        if (g == null)
            return;
        createReservation(g, isWalkIn);
    }

    /**
     * Create a reservation
     * @param guest Guest Entity
     * @param isWalkIn walk in or reservation
     */
    private void createReservation(Guest guest, boolean isWalkIn){
        Reservation rsv = new Reservation();

        if (new Room().findAll("status", Room.RoomStatus.VACANT.toString()).length <= 0) {
            rb.printRoomsFull();
            return;
        }

        // Search for Room
        RoomControl rc = new RoomControl();
        Room room;
        do {
            room = rc.selectRoom();
        } while (room == null);

        // Get Reservation Attributes
        rsv.setGuest(guest);
        rsv.setRoom(room);
        rsv.setReservationStatus(Reservation.ReservationStatus.CONFIRMED);

        String blacklist[] = {"checkInDate"};
        HashMap<String, String> results = new HashMap<>();

        if (!isWalkIn) {
            blacklist = new String[0];
        } else {
            results.put("checkInDate", new SimpleDateFormat(Reservation.dateFormat).format(new Date()));
            rsv.setWalkIn(true);
        }

        rb.processModel(rsv, "", results, blacklist);


        boolean success;
        do {
            success = true;
            boolean response = rb.processRecords(rsv);
            if (response) {
                success = rsv.save();

                if (success && isWalkIn) {
                    confirmCheckIn(rsv);
                } else if (success) {
                    rb.println(String.format("Reservation is successful. Reservation Code: %s", rsv.getReservationCode()));
                } else {
                    rb.printErrors(rsv);
                }

            }
        } while (!success);
    }

    public void checkIn() {
        int input = rb.process(checkInMenu);
        switch (input) {
            case 1:
                checkInGuestSearch();
                break;
            case 2:
                checkInRoomSearch();
                break;
            case 3:
                checkInReservationCode();
                break;
            case 0:
                break;
            default:
                rb.functionNotDeployed();
        }
    }

    /**
     * Find Reservation by reservation Code
     */
    private void checkInReservationCode() {
        Reservation rsv = search(Reservation.ReservationStatus.CONFIRMED);
        if (rsv == null)
            return;
        processCheckIn(rsv);
    }

    /**
     * Search for a reservation list
     * @param rStatus current status
     * @return Reservation Entity
     */
    protected Reservation search(Reservation.ReservationStatus rStatus) {
        System.out.println("Reservation Code:");
        Reservation rsvList[] = new Reservation().findAll(false, -1, "reservationCode", rb.getString(), "reservationStatus", rStatus.toString());
        if (rsvList.length == 0) {
            rb.noResultsFound();
            return null;
        }

        Reservation rsv = rb.chooseReservation(rsvList);
        return rsv;
    }

    /**
     * Search for Room when checking in
     */
    private void checkInRoomSearch() {
        RoomControl rc = new RoomControl();
        Room r = rc.search();
        if (r == null)
            return;

        // Check if Reservation Exists
        Reservation rsv = new Reservation().find("roomId", r.getRoomId(), "reservationStatus", Reservation.ReservationStatus.CONFIRMED.toString());
        if (rsv == null) {
            rb.noResultsFound();
            return;
        }

        processCheckIn(rsv);

    }

    /**
     * Ask the user which reservation to check in
     * @param rsvList Reservation Entity Array
     */
    private void processCheckIn(Reservation[] rsvList) {
        Reservation rsv = rb.chooseReservation(rsvList);
        if (rsv == null)
            return;
        processCheckIn(rsv);
    }

    /**
     * Confirm Check in Process
     * @param rsv Reservation Entity
     */
    private void processCheckIn(Reservation rsv) {
        // Confirm check in and check in user
        if (rb.confirmCheckIn(rsv))
            confirmCheckIn(rsv);
    }

    /**
     * Check in Reservation
     * @param rsv Reservation Entity
     */
    private void confirmCheckIn(Reservation rsv) {
        if (rsv.checkIn())
            rb.checkInSuccessful(rsv);
        else
            rb.checkInUnsuccessful(rsv.getErrors());
    }

    /**
     * Check in by searching for Guest. Pass to GuestControl.
     */
    private void checkInGuestSearch() {
        GuestControl gc = new GuestControl();
        Guest g = gc.search();
        if (g == null)
            return;

        // Check if Reservation Exists
        Reservation rsvList[] = new Reservation().findAll("guestId", g.getId(), "reservationStatus", Reservation.ReservationStatus.CONFIRMED.toString());
        processCheckIn(rsvList);
    }

    /**
     * Check out process
     */
    public void checkOut() {
        int input = rb.process(checkOutMenu);
        switch (input) {
            case 1:
                checkOutGuestSearch();
                break;
            case 2:
                checkOutRoomSearch();
                break;
            case 3:
                checkOutRsvSearch();
                break;
            case 0:
                break;
            default:
                rb.functionNotDeployed();
        }
    }

    /**
     * Check out search by Room. Pass to RoomControl
     */
    private void checkOutRoomSearch() {
        RoomControl rc = new RoomControl();
        Room r = rc.search();
        if (r == null)
            return;

        // Check if Reservation Exists
        Reservation rsv = new Reservation().find("roomId", r.getRoomId(), "reservationStatus", Reservation.ReservationStatus.CHECKED_IN.toString());
        if (rsv == null) {
            rb.noResultsFound();
            return;
        }

        processCheckOut(rsv);
    }

    /**
     * Check out search by Reservation Code.
     */
    private void checkOutRsvSearch() {
        Reservation rsv = search(Reservation.ReservationStatus.CHECKED_IN);
        if (rsv == null)
            return;

        processCheckOut(rsv);
    }

    /**
     * Check out search by Guest
     */
    private void checkOutGuestSearch() {
        GuestControl gc = new GuestControl();
        Guest g = gc.search();
        if (g == null)
            return;

        Reservation rsvList[] = new Reservation().findAll("guestId", g.getId(), "reservationStatus", Reservation.ReservationStatus.CHECKED_IN.toString());
        Reservation rsv = rb.chooseReservation(rsvList);
        if (rsv == null)
            return;

        processCheckOut(rsv);
    }

    /**
     * Check out after Payment paid.
     * @param rsv Reservation Entity
     */
    private void processCheckOut(Reservation rsv) {
        Payment p = new Payment(rsv);
        if (rb.printPayment(p)) {
            if (rsv.checkOut())
                rb.checkOutSuccessful();
            else
                rb.checkOutUnsuccessful(rsv.getErrors());
        }
    }

    /**
     * Walk in Requests. Create reservation on same day.
     */
    public void walkIn() {
        createReservation(true);
    }
}
