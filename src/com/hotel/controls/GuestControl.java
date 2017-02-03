package com.hotel.controls;

import com.hotel.core.Control;
import com.hotel.entities.Guest;
import com.hotel.core.Boundary;
import com.hotel.boundaries.GuestBoundary;

import java.util.HashMap;

/**
 * Guest Control Object
 * Fetch information from Guest Entity and pass on to Guest Boundary
 * @author  Tan Jun Xiang
 * @version 1.0
 */
public class GuestControl implements Control {
    private GuestBoundary gb;

    private static final String menu[] = {
        "Guest System",
        "Create Guest",
        "Update Guest",
        "Find Guest"
    };

    private static final String findMenu[] = {
        "Guest System (Find)",
        "Find by ID",
        "Find by Name",
    };

    /**
     * Default Constructor for Guest Control
     */
    public GuestControl() {
        gb = new GuestBoundary();
    }

    @Override
    public void process() {
        int input = gb.process(menu);
        switch(input) {
            case 1:
                createGuest();
                break;
            case 2:
                updateGuest();
                break;
            case 3:
                search();
            case 0:
                break;
            default:
                gb.functionNotDeployed();
        }

        // Easier Recursive
        if (input != 0)
            process();
    }

    /**
     * Interacts with Guest Boundary
     * @return Guest Entity
     */
    protected Guest createGuest() {
        Guest g = new Guest();
        g = gb.processModel(g, "Create Guest", new HashMap<String, String>());
        if (g == null)
            return null;

        boolean success;
        do {
            success = true;
            boolean response = gb.getConfirmation(g);
            if (response) {
                success = g.save();
                if (success)
                    gb.saveSuccessful();
            }
        } while (!success);

        return g;
    }

    /**
     * Interacts with Guest Boundary
     * @return Guest Entity
     */
    private Guest updateGuest() {
        Guest g = search();
        if (g == null)
            return null;

        boolean response = gb.updateRecord(g);
        if (response)
            g.save();
        else
            g.revertOldData();

        return g;
    }

    /**
     * Search for a Guest Entity from Guest Boundary
     * @return Guest Entity
     */
    protected Guest search() {
        int input = gb.process(findMenu);
        Guest g = null;
        switch(input) {
            case 1:
                g = findById();
                break;
            case 2:
                g = findByName();
                break;
            case 0:
                break;
            default:
                gb.functionNotDeployed();
        }

        return g;
    }

    /**
     * Interacts with Guest Boundary to find user by id
     * @return Guest Entity
     */
    private Guest findById() {
        gb.println(new Guest().getAttributeLabel("id"));
        String id = gb.getString();

        Guest gList[] = new Guest().findAll("id", id, false);



        if (gList.length == 0) {
            gb.noResultsFound();
            return null;
        }

        Guest g;
        String[] results = new String[gList.length + 1];
        results[0] = "Choose Guest:";
        int i = 1;
        for (Guest guest : gList)
            results[i++] = guest.getId();

        int input = gb.processMenu(results, false, true);
        if (input == 0)
            return null;
        g = gList[input - 1];


        if (g != null) {
            if (!gb.checkRecord(g, "Guest Information", false))
                return null;
        }

        return g;
    }

    /**
     * Interacts with Guest Boundary to find user by name
     * @return Guest Entity
     */
    private Guest findByName() {
        gb.println(new Guest().getAttributeLabel("name"));
        String name = gb.getString();

        Guest gList[] = new Guest().findAll("name", name, false);


        if (gList.length == 0) {
            gb.noResultsFound();
            return null;
        }

        Guest g;
        String[] results = new String[gList.length + 1];
        results[0] = "Choose Guest:";
        int i = 1;
        for (Guest guest : gList)
            results[i++] = guest.getName();

        int input = gb.processMenu(results, false, true);
        if (input == 0)
            return null;
        g = gList[input - 1];


        if (g != null) {
            if (!gb.checkRecord(g, "Guest Information", false))
                return null;
        }

        return g;
    }
}
