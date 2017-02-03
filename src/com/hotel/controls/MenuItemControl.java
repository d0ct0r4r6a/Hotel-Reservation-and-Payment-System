package com.hotel.controls;

import com.hotel.boundaries.MenuItemBoundary;
import com.hotel.core.Control;
import com.hotel.entities.MenuItem;

import java.util.HashMap;

/**
 * Menu Item Control Object
 * Fetch information from Menu Item Entity and pass on to Menu Item Boundary
 * @author  Tan Jun Xiang
 * @version 1.0
 */
public class MenuItemControl implements Control {
    private MenuItemBoundary mib;

    private static final String menu[] = {
            "Menu System",
            "View All Menu Item",
            "Add Menu Item",
            "Update Menu Item",
            "Delete Menu Item",
    };

    public MenuItemControl() {
        this.mib = new MenuItemBoundary();
    }

    @Override
    public void process() {
        int input = mib.process(menu);
        switch(input) {
            case 1:
                viewMenu();
                break;
            case 2:
                addMenu();
                break;
            case 3:
                updateMenu();
                break;
            case 4:
                deleteMenu();
                break;
            case 0:
                break;
            default:
                mib.functionNotDeployed();
        }

        if (input != 0)
            process();
    }

    protected MenuItem viewMenu() {
        MenuItem miList[] = new MenuItem().findAll("name", "", false);
        MenuItem item = mib.chooseMenu(miList);
        if (item == null)
            return null;

        if (mib.checkRecord(item, "Menu Information", false))
            return item;
        else
            return null;
    }

    private void updateMenu() {
        MenuItem miList[] = new MenuItem().findAll("name", "", false);
        MenuItem item = mib.chooseMenu(miList);
        if (item == null)
            return;

        boolean update = mib.checkRecord(item, "Menu Information", true);
        if (update) {
            if (item.save())
                mib.saveSuccessful();
            else
                mib.printErrors(item);
        } else
            item.revertOldData();
    }

    private void deleteMenu() {
        MenuItem miList[] = new MenuItem().findAll("name", "", false);
        MenuItem item = mib.chooseMenu(miList);
        if (item == null)
            return;

        boolean delete = mib.checkRecord(item, "Menu Information", false);
        if (delete) {
            if (item.delete())
                mib.println("Menu item successfully deleted.");
            else
                mib.printErrors(item);
        }
    }

    private void addMenu() {
        MenuItem item = new MenuItem();
        item = mib.processModel(item, "Menu Item", new HashMap());

        boolean save = mib.checkRecord(item, "Menu Information", true);
        if (save) {
            if (item.save())
                mib.saveSuccessful();
            else
                mib.printErrors(item);
        }
    }
}
