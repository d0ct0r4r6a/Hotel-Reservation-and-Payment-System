package com.hotel.boundaries;

import com.hotel.core.Boundary;
import com.hotel.core.Entity;
import com.hotel.entities.Guest;

import java.util.HashMap;

/**
 * Guest Boundary Object
 * Manages the Input/Output for GuestControl
 * @author  Tan Jun Xiang
 * @version 1.0
 */
public class GuestBoundary extends Boundary {
    public boolean getConfirmation(Entity entity) {
        return super.checkRecord(entity, "Please Confirm the information.", true);
    }

    public boolean updateRecord(Entity entity) {
        return super.checkRecord(entity, "Please select the fields to update.", true);
    }

    public Guest chooseGuest(Guest[] gList) {
        if (gList.length == 0) {
            noResultsFound();
            return null;
        }

        String[] results = new String[gList.length + 1];
        results[0] = "Choose Guest:";
        int i = 1;
        for (Guest g : gList)
            results[i++] = g.getId();

        int input = processMenu(results, false, true);
        if (input == 0)
            return null;
        return gList[input - 1];
    }
}
