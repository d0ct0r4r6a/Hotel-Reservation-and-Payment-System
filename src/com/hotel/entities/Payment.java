package com.hotel.entities;

import com.hotel.core.DateManager;

import java.util.Date;

/**
 * Entity of Payment Object
 * Handles Payment
 * @author  Tan Jun Xiang
 * @version 1.0
 */
public class Payment {
    private Reservation reservation;
    private int serviceChargePercent = 7;

    public Payment(Reservation reservation) {
        this.reservation = reservation;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public double getRoomServiceTotal() {
        RoomService rsList[] = new RoomService().findAll("reservationId", getReservation().getReservationCode());

        double total = 0.0;
        for (RoomService rs : rsList)
            total += rs.getTotal();

        return total;
    }

    public double getRoomTotal() {
        Room r = getReservation().getRoom();
        Date checkIn = getReservation().getCheckInDate();
        Date checkOut = getReservation().getCheckOutDate();

        int totalDays = DateManager.getTotalDays(checkIn, checkOut);
        int totalWeekdays = DateManager.getTotalWeekdays(checkIn, checkOut);

        double weekdayTotal = totalWeekdays * r.getRoomRate();
        double weekendTotal = (totalDays - totalWeekdays) * r.getRoomWeekendRate();

        return weekdayTotal + weekendTotal;
    }

    public int getServiceCharge() {
        return serviceChargePercent;
    }

    public double getServiceChargeTotal() {
        return (getRoomServiceTotal() + getRoomTotal()) * (serviceChargePercent / 100.0);
    }

    public double getTotal() {
        return getRoomTotal() + getRoomServiceTotal() + getServiceChargeTotal();
    }
}
