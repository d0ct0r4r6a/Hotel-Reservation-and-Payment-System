package com.hotel.boundaries;

import com.hotel.core.Boundary;
import com.hotel.entities.MenuItem;

/**
 * Menu Item Boundary Object
 * Manages the Input/Output for Updating MenuItemControl
 * @author  Tan Jun Xiang
 * @version 1.0
 */
public class MenuItemBoundary extends Boundary {
    public MenuItem chooseMenu(MenuItem[] miList) {
        if (miList.length == 0) {
            noResultsFound();
            return null;
        }

        String[] results = new String[miList.length + 1];
        results[0] = "Choose Menu Item:";
        int i = 1;
        for (MenuItem mi : miList)
            results[i++] = mi.getName();

        int input = processMenu(results, false, false);
        if (input == 0)
            return null;
        return miList[input - 1];
    }
}
