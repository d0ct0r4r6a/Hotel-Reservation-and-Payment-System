package com.hotel.core;

import com.hotel.Config;
import com.hotel.entities.Guest;
import com.hotel.entities.Room;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Handles Input/Output of Application. Validation done in this class.
 * Superclass for all Boundary Objects
 * @author  Tan Jun Xiang
 * @version 1.0
 */
public class Boundary {
    private static Scanner s = null;
    private Entity.Types Types;

    /**
     * Default Constructor
     */
    public Boundary() {
        if (s == null)
            s = new Scanner(System.in);
    }

    /**
     * Look into processMenu function
     * @param menu
     * @return User choice on menu.
     */
    public int process(String[] menu) {
        return processMenu(menu, false);
    }

    /**
     * Look into processMenu function
     * @param menu
     * @return User choice on menu.
     */
    public int process(String[] menu, boolean main) {
        return processMenu(menu, main);
    }

    /**
     * Look into processModel function
     * @param t
     * @param title
     * @param results
     * @param <T>
     * @return Entity Object
     */
    public <T> T processModel(T t, String title, HashMap<String, String> results) {
        return processModel(t, title, results, new String[0]);
    }

    /**
     * Look into processModel function
     * @param t
     * @param title
     * @param results
     * @param blacklist
     * @param <T>
     * @return Entity Object
     */
    public <T> T processModel(T t, String title, HashMap<String, String> results, String[] blacklist) {
        Entity e = (Entity) t;
        return (T) processModel(e.getEditableList(), e.getEnumList(), e, title, results, blacklist);
    }

    /**
     * Look into processModel function
     * @param editableList
     * @param enumList
     * @param t
     * @param <T>
     * @return Entity Object
     */
    public <T> T processModel(String[][] editableList, HashMap<String, Enum[]> enumList, T t, String title, HashMap<String, String> results) {
        return processModel(editableList, enumList, t, title, results, new String[0]);
    }

    /**
     * Takes in a list of variables to ask user for input
     * @param editableList list of variables
     * @param enumList enumerations that are defined in the object class
     * @param t Entity Object
     * @param <T> Entity Class
     * @return Entity Object
     */
    public <T> T processModel(String[][] editableList, HashMap<String, Enum[]> enumList, T t, String title, HashMap<String, String> results, String[] blacklist) {
        if (title != "")
            println(title);
        Entity entity = (Entity) t;

        for (String[] vars : editableList) {
            if (Arrays.asList(blacklist).contains(vars[0]))
                continue;

            if (!processVar(vars, enumList, entity, results)) {
                entity.revertOldData();
                return null;
            }
        }

        entity.loadHashMap(results);
        return (T) entity;
    }

    /**
     * Error checking for Types defined in Entity. Checks for various input errors
     * @param vars list of variables to be checked and their types
     * @param enumList Enum List from Class object
     * @param entity Entity Object
     * @param results results to be returned
     * @return success or with errors
     */
    public boolean processVar(String[] vars, HashMap<String, Enum[]> enumList, Entity entity, HashMap<String, String> results) {
        String var = vars[0];
        String text = entity.getAttributeLabel(var);
        Enum type = Types.valueOf(vars[1]);

        boolean invalid;
        do {
            invalid = false;
            // Enum is special case. error is handled in processEnum
            if (type == Types.ENUM) {
                Enum e = processEnum(text + ":", enumList.get(var), entity);
                results.put(var, e.toString());
            } else {
                String input = "";
                if (type == Types.STRING) {
                    input = getString(text + ":");
                    invalid = input.equals("");
                    if (!invalid)
                        results.put(var, input);
                } else if (type == Types.BOOLEAN) {
                    input = getString(text + " (Y / N):").toUpperCase();
                    invalid = !(input.equals("Y") || input.equals("N"));
                    if (!invalid)
                        results.put(var, Boolean.toString(input.equals("Y")));

                } else if (type == Types.GENDER) {
                    input = getString(text + " (M / F):").toUpperCase();
                    invalid = !(input.equals("M") || input.equals("F"));
                    if (!invalid)
                        results.put(var, input);

                } else if (type == Types.INT) {
                    try {
                        input = getString(text + ":");
                        results.put(var, Integer.toString(Integer.parseInt(input)));
                    } catch (NumberFormatException e) {
                        invalid = true;
                    }
                } else if (type == Types.DOUBLE || type == Types.CURRENCY) {
                    try {
                        input = getString(text + ":");
                        results.put(var, Double.toString(Double.parseDouble(input)));
                    } catch (NumberFormatException e) {
                        invalid = true;
                    }
                } else if (type == Types.DATE) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat(entity.getDateFormat());
                        input = getString(String.format("%s (%s):", text, entity.getDateFormat()));
                        results.put(var, sdf.format(sdf.parse(input)));
                    } catch (ParseException e) {
                        invalid = true;
                    }
                }
            }
            if (invalid)
                printInvalidInput();
        } while (invalid);

        return true;
    }

    /**
     * Look at function printMenu
     * @param menu
     * @param isMainMenu
     * @param printReturn
     */
    public void printMenu(String[] menu, boolean isMainMenu, boolean printReturn) {
        printMenu(menu, isMainMenu, printReturn, false);
    }

    /**
     * Handles printing menu to screen
     * @param menu
     * @param isMainMenu
     * @param printReturn
     */
    public void printMenu(String[] menu, boolean isMainMenu, boolean printReturn, boolean printDashes) {
        System.out.println(menu[0]);
        for (int i = 1; i < menu.length; i++)
            System.out.printf("%s %s\n", printDashes ? "-" : i + ".", menu[i]);

        if (printReturn)
            System.out.println(isMainMenu ? "0. Quit Application" : "0. Return to Main Menu");
    }


    /**
     * Look at function processMenu
     * @param menu
     * @param isMainMenu
     * @return user input choice
     */
    public int processMenu(String[] menu, boolean isMainMenu) {
        return processMenu(menu, isMainMenu, true);
    }

    /**
     * Prints out all enums from a Entity Class and ask user for input
     * @param text Title for the user
     * @param eList Enum Array
     * @param entity Entity Object
     * @return user enum choice
     */
    public Enum processEnum(String text, Enum[] eList, Entity entity) {
        String menuList[] = new String[eList.length + 1];
        menuList[0] = text;
        int i = 1;
        for (Enum e : eList)
            menuList[i++] = entity.getAttributeLabel(e.toString());

        int input = processMenu(menuList, false, false);
        return eList[input - 1];
    }

    /**
     * Prints a menu for user navigation
     * @param menu String of menu object. menu[0] is title
     * @param isMainMenu whether it is the first page.
     * @param printReturn whether to allow the user to return to previous page
     * @return user selection of menu
     */
    public int processMenu(String[] menu, boolean isMainMenu, boolean printReturn)
    {
        // Checks if menu does return
        int minInt = printReturn ? 0 : 1;
        boolean invalid = true;
        int input = -1;
        do {
            try {
                printMenu(menu, isMainMenu, printReturn);
                input = Integer.parseInt(getString());

                invalid = input < minInt || input >= menu.length;
            } catch (NumberFormatException nfe) {}

            if (invalid)
                printInvalidInput();
        } while(invalid);

        return input;
    }

    /**
     * Prints an invalid input to user's screen
     */
    public void printInvalidInput() {
        System.out.println("Invalid Input.");
    }

    /**
     * Get a string from the user
     * @param text the title to prompt the user
     * @return input from user
     */
    public String getString(String text) {
        System.out.println(text);
        return getString();
    }

    /**
     * Get a string from the user
     * @return input from user
     */
    public String getString() {
        System.out.print(">> ");
        String input = s.nextLine();
        System.out.println();

        return input;
    }

    /**
     * Print function not deployed
     */
    public void functionNotDeployed() {
        System.out.println("This function has not been deployed yet.");
    }

    /**
     * Used for viewing or updating Entity Objects
     * @param entity Entity Object
     * @param title Title to prompt User
     * @param allowUpdate Viewing or update purposes
     * @return
     */
    public boolean checkRecord(Entity entity, String title, boolean allowUpdate) {
        boolean invalid, update;
        do {
            invalid = true;
            update = false;
            String[][] editableList = entity.getEditableList();

            printRecord(entity, title, allowUpdate);

            // Print errors if exists
            boolean entityValidated = entity.validate();
            if (!entityValidated) {
                println("");
                printErrors(entity);
            }

            // Update the user on what can be done
            println("");
            if (allowUpdate)
                println("Press Y to confirm, N to discard and (No.) to edit a field and (No.) to edit a field.");
            else
                println("Press Y to continue, N to return to previous menu.");

            String input = getString();
            // If Input is Y or N, return to Controller
            if (input.toUpperCase().equals("Y") || input.toUpperCase().equals("N")) {
                boolean isYes = input.toUpperCase().equals("Y");
                if (allowUpdate)
                    println(isYes ? "Saving Record..." : "Operation has been cancelled.");
                return isYes;

            // Code cannot advanced into update
            } else if (!allowUpdate) {
                continue;
            }

            int intInput;
            try {
                intInput = Integer.parseInt(input);
                invalid = intInput <= 0 || intInput >= editableList.length + 1;

                HashMap entityMap = entity.toHashMap();
                processVar(editableList[intInput - 1], entity.getEnumList(), entity, entityMap);
                entity.loadHashMap(entityMap);
                update = true;
            } catch (Exception e) {}

            if (invalid && !update)
                printInvalidInput();
        } while (invalid || update);

        return false;
    }

    /**
     * Print the entity variables and content to screen
     * @param entity Entity Object
     * @param title Title to print on screen
     * @param allowUpdate Viewing or update purposes
     */
    public void printRecord(Entity entity, String title, boolean allowUpdate) {
        String[][] editableList = entity.getEditableList();
        String[] menu = new String[editableList.length + 1];
        HashMap<String, String> entityList = entity.toHashMap();

        int i = 1;
        menu[0] = title;
        for (String[] vars : editableList) {
            String var = vars[0];
            if (Entity.Types.valueOf(vars[1]) == Entity.Types.CURRENCY)
                menu[i++] = String.format("%s: %s", entity.getAttributeLabel(var), toCurrency(Double.parseDouble(entityList.get(var))));
            else
                menu[i++] = String.format("%s: %s", entity.getAttributeLabel(var), entity.getAttributeLabel(entityList.get(var)));
        }

        printMenu(menu, false, false, !allowUpdate);
    }

    /**
     * Print save successful message
     */
    public void saveSuccessful() {
        System.out.println("Operation successfully completed.");
        getString();
    }

    /**
     * Print errors from Entity object
     * @param entity Entity Object
     */
    public void printErrors(Entity entity) {
        System.out.println("Please fix the following errors.");
        for (String error : entity.getErrors())
            System.out.println("- " + error);
    }

    /**
     * Same as System.out.println(text)
     * @param text
     */
    public void println(String text) {
        System.out.println(text);
    }

    /**
     * Change double to currency format
     * @param price
     * @return
     */
    public String toCurrency(double price) {
        return String.format(Config.currency + "%.02f", price);
    }

    /**
     * Print no results found
     */
    public void noResultsFound() {
        System.out.println("No Results Found.");
        getString();
    }
}
