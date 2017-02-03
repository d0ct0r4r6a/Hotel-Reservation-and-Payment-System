package com.hotel.boundaries;

import com.hotel.core.Boundary;
import com.hotel.core.DateManager;
import com.hotel.entities.*;

import java.text.SimpleDateFormat;

/**
 * Created by junxiang92 on 13/4/16.
 */
public class ReservationBoundary extends Boundary {

    public boolean processRecords(Reservation rsv) {
        Guest g = rsv.getGuest();
        Room r = rsv.getRoom();

        printRecord(g, "Guest Record", false);
        println("");
        printRecord(r, "Room Record", false);
        println("");
        return checkRecord(rsv, "Reservation Record", true);
    }

    public boolean confirmCheckIn(Reservation rsv) {
        do {
            printDetails(rsv);
            
            println("Do you want to check in? (Y / N):");
            String input = getString();
            if (input.toUpperCase().equals("Y") || input.toUpperCase().equals("N"))
                return input.toUpperCase().equals("Y");

            printInvalidReservation(rsv);
        } while (true);

    }

    private void printDetails(Reservation rsv) {
        SimpleDateFormat sdf = new SimpleDateFormat(Reservation.dateFormat);

        println("Reservation Details");
        println(String.format("Reservation Code: %s", rsv.getReservationCode()));
        println(String.format("Guest ID: %s", rsv.getGuest().getId()));
        println(String.format("Check In Time: %s", sdf.format(rsv.getCheckInDate())));
        println(String.format("Check Out Time: %s", sdf.format(rsv.getCheckOutDate())));
        println("");
    }

    public void checkInSuccessful(Reservation rsv) {
        println(String.format("You have been checked in successfully. Room ID: %s", rsv.getRoom().getRoomId()));
        getString();
    }

    public void checkInUnsuccessful(String[] errors) {
        for (String error : errors)
            println(String.format("- %s", error));

        getString();
    }

    public void printInvalidReservation(Reservation rsv) {
        println(String.format("Your reservation is invalid because it has been %s.", rsv.getAttributeLabel(rsv.getReservationStatus().toString())));
        getString();
    }

    public Reservation chooseReservation(Reservation[] rsvList) {
        if (rsvList.length == 0) {
            noResultsFound();
            return null;
        }

        String[] results = new String[rsvList.length + 1];
        results[0] = "Choose Reservation:";
        int i = 1;

        SimpleDateFormat sdf = new SimpleDateFormat(Reservation.dateFormat);
        for (Reservation rsv : rsvList)
            results[i++] = String.format("%s - %s", sdf.format(rsv.getCheckInDate()), rsv.getReservationCode());

        int input = processMenu(results, false, true);
        if (input == 0)
            return null;
        return rsvList[input - 1];
    }

    public void checkOutSuccessful() {
        println("You have been checked out successfully.");
        getString();
    }

    public void checkOutUnsuccessful(String[] errors) {
        for (String error : errors)
            println(String.format("- %s", error));

        getString();
    }

    public boolean printPayment(Payment p) {
        Reservation rsv = p.getReservation();
        RoomService rs = rsv.getRoomService();
        int totalDays = DateManager.getTotalDays(rsv.getCheckInDate(), rsv.getCheckOutDate());
        int weekdays = DateManager.getTotalWeekdays(rsv.getCheckInDate(), rsv.getCheckOutDate());
        SimpleDateFormat sdf = new SimpleDateFormat(Reservation.dateFormat);
        String input;

        do {
            System.out.println(String.format("Guest ID: %s", rsv.getGuest().getId()));
            System.out.println(String.format("Guest Name: %s", rsv.getGuest().getName()));
            System.out.println();
            System.out.println("Days of Stay");
            System.out.println(String.format("%s to %s", sdf.format(rsv.getCheckInDate()), sdf.format(rsv.getCheckOutDate())));
            System.out.println(String.format("Weekdays: %d x %s", weekdays, toCurrency(p.getReservation().getRoom().getRoomRate())));
            System.out.println(String.format("Weekends: %d x %s", totalDays - weekdays, toCurrency(p.getReservation().getRoom().getRoomWeekendRate())));
            System.out.println(String.format("Room Total: %s", toCurrency(p.getRoomTotal())));

            if (rs != null) {
                System.out.println();
                System.out.println("Room Service");
                for (OrderMenuItem omi : rs.getMenuItems())
                    System.out.println(String.format("- %s x %d: %s", omi.getName(), omi.getQty(), toCurrency(omi.getTotalPrice())));
                System.out.println(String.format("Room Service Total: %s", toCurrency(p.getRoomServiceTotal())));
            }

            System.out.println();
            System.out.println(String.format("Service Charge (%d%%): %s", p.getServiceCharge(), toCurrency(p.getServiceChargeTotal())));
            System.out.println(String.format("Total Cost: %s", toCurrency(p.getTotal())));

            System.out.println("Press Y to pay with credit card, N to cancel");
            input = getString();

            if (input.toUpperCase().equals("Y")) {
                System.out.println("Payment has been successful.");
                return true;
            } else if (input.toUpperCase().equals("N")) {
                return false;
            }
        } while (true);
    }

    public void printRoomsFull() {
        System.out.println("All our rooms are currently filled.");
    }
}
