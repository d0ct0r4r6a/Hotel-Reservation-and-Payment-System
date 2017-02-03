package com.hotel;

import com.hotel.entities.OrderMenuItem;
import com.hotel.entities.RoomService;
import com.hotel.entities.*;

import java.text.SimpleDateFormat;
import java.util.HashMap;

/**
 * Configuration file for all Entities
 * @author  Tan Jun Xiang
 * @version 1.0
 */
public class Config {
    public static final HashMap<Class, String> files = new HashMap(){{
        put(Guest.class, "guest.txt");
        put(Room.class, "room.txt");
        put(Reservation.class, "reservation.txt");
        put(MenuItem.class, "menuitem.txt");
        put(OrderMenuItem.class, "omenuitem.txt");
        put(RoomService.class, "roomservice.txt");
    }};

    public static final String currency = "S$";
    public static final int expireHours = 1;

}
