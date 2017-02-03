package com.hotel.core;

import com.hotel.Config;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles Interaction with FileManager.
 * Superclass for all Entity Objects
 * @author  Tan Jun Xiang
 * @version 1.0
 */
public abstract class Entity<T extends Entity<T>> {
    public String dateFormat;
    private static HashMap<Class, ArrayList<Entity>> entities = null;
    protected ArrayList<String> errors;
    private Class subClass;
    private HashMap<String, String> oldData = null;
    private boolean newRecord = true;
    public enum Types {
        ENUM, STRING, GENDER, BOOLEAN, INT, DOUBLE, CURRENCY, DATE
    }


    /**
     * Constructor of Entity
     * @param subClass Subclass that initializes this class
     */
    public Entity(Class subClass) {
        this.subClass = subClass;
        this.errors = new ArrayList<>();
        // Load all data from text files if not loaded. ONE TIME ONLY.
        if (entities == null)
            loadEntities();
    }

    /**
     * Converts a Guest object into a HashMap
     * @return HashMap Strng of Guest Data
     */
    public abstract HashMap<String, String> toHashMap();

    /**
     * Retrieves Hashmap from FileManager
     * @param guestData String of guestData
     * @return Guest Returns a Guest object
     */
    public abstract void loadHashMap(HashMap<String, String> guestData);

    /**
     * Validate all variables in the class
     * @return true if validated, false if validated with errors
     */
    public boolean validate() {
        if (errors.size() != 0)
            revertOldData();

        return errors.size() == 0;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    /**
     * Set Date Format used for verification in Boundary
     * @param dateFormat format of date
     */
    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    /**
     * Fetch the variable name to be presented to the user
     * @return String array containing the names of each variable
     */
    public abstract HashMap<String, String> getAttributeLabels();

    /**
     * Same as getAttributeLabels();
     * @param attrib variable name
     * @return name of the variable
     */
    public String getAttributeLabel(String attrib) {
        HashMap<String, String> hashMap =  getAttributeLabels();
        String result = hashMap.get(attrib);

        return result == null ? attrib : result;
    }

    /**
     * Get the variable list from getAttributeLabels(). keys in Attribute Labels should be editable
     * @return String array of variable names
     */
    public abstract String[][] getEditableList();
    public abstract HashMap<String, Enum[]> getEnumList();

    public boolean isNewRecord() {
        return newRecord;
    }

    protected void setNewRecord(boolean newRecord) {
        this.newRecord = newRecord;
        if (!newRecord)
            oldData = toHashMap();
    }

    /**
     * Dependant on validate(). Fetch errors generated
     * @return String of errors
     */
    public String[] getErrors() {
        return errors.toArray(new String[errors.size()]);
    }

    /**
     * Saves or update the object to the database
     * @return true if success, false otherwise
     */
    public boolean save() {
        if (!this.validate())
            return false;

        if (isNewRecord())
            return createNewRecord();
        else
            return updateRecord();
    }


    /**
     * Creates a new object and save to database
     * @return true if success, false otherwise
     */
    private boolean createNewRecord() {
        boolean success = writeToFileManager();
        if (!success)
            System.out.println("Failed to save file!!!");
        else
            setNewRecord(false);
        return success;
    }

    /**
     * Sends records to filemanager to be written to a file
     * @return success or fail to write
     */
    private boolean writeToFileManager() {
        FileManager fm = new FileManager(Config.files.get(subClass));
        ArrayList<Entity> entity = entities.get(subClass);
        if (isNewRecord()) {
            try {
                entity.add(this);
            } catch (NullPointerException e) {
                System.out.println("Has the file been defined in Config.java?");
            }
        }

        return fm.write(entity);
    }

    /**
     * Load all files using FileManager into the variable entities
     * @return Success of the function.
     */
    private boolean loadEntities() {
        // Initialize for entry
        entities = new HashMap<>();

        // Loop through the data
        for (Map.Entry<Class, String> o : Config.files.entrySet()) {
            // Set up FileManager
            Class c = o.getKey();
            FileManager fm = new FileManager(Config.files.get(c));

            // Add to Entity static variable
            ArrayList entityList;
            try {
                entityList = fm.load(c.newInstance());
            } catch (Exception fe) {
                entityList = new ArrayList();
            }
            entities.put(c, entityList);
        }

        // Set all objects in false to be false
        for (Object o : entities.keySet()) {
            for (Entity e : entities.get(o))
                e.setNewRecord(false);
        }

        return true;
    }

    /**
     * Fetch all Entities of a particular Class
     * @param T Entity Class to be extracted
     * @return ArrayList of Entities
     */
    private ArrayList getEntities(Class T) {
        return entities.get(T);
    }


    private boolean updateRecord() {
        return writeToFileManager();
    }

    /**
     * Delete this Object from the Database
     * @return true if success, false otherwise
     */
    public boolean delete() {
        // Call FileManager to save and add to Array
        FileManager fm = new FileManager(Config.files.get(subClass));
        ArrayList<Entity> entity = entities.get(subClass);
        entity.remove(this);

        boolean success = fm.write(entity);

        if (!success)
            System.out.println("Failed to save file!!!");

        return success;
    }

    /**
     * Find a particular object
     * @param var Variable name
     * @param content Content to search for
     * @return null if not found, Object if found
     */
    public T find(String var, String content) {
        return find(var, content, true);
    }

    public T find(String... args) {
        try {
            return findAll(true, 1, args)[0];
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Find a particular object
     * @param var Variable name
     * @param content Content to search for
     * @param matchAll matchAll or matchPartial
     * @return null if not found, Object if found
     */
    public T find(String var, String content, boolean matchAll) {
        try {
            return findAll(var, content, matchAll, 1)[0];
        } catch (Exception e) {
            return null;
        }
    }



    /**
     * Find a Array of objects
     * @param var Variable name
     * @param content Content to search for
     * @param matchAll matchAll or matchPartial
     * @return null if not found, Object Array if found
     */
    public T[] findAll(String var, String content, boolean matchAll) {
        return findAll(var, content, matchAll, -1);
    }

    /**
     * Find a Array of objects with limited results
     * @param var Variable name
     * @param content Content to search for
     * @param matchAll matchAll or matchPartial
     * @param results Number of objects to be returned. -1 for all results to be returned
     * @return null if not found, Object Array of size results if found
     */
    public T[] findAll(String var, String content, boolean matchAll, int results) {
        return findAll(matchAll, results, var, content);
    }

    /**
     * Find a Array of objects with limited results
     * @param args Variable args for more than one field matching
     * @return null if not found, Object Array of size results if found
     */
    public T[] findAll(String... args) {
        return findAll(true, -1, args);
    }

    /**
     * Find a Array of objects with limited results
     * @param matchAll matchAll or matchPartial
     * @param results Number of objects to be returned. -1 for all results to be returned
     * @param args Variable args for more than one field matching
     * @return null if not found, Object Array of size results if found
     */
    public T[] findAll(boolean matchAll, int results, String... args) {
        if (args.length % 2 != 0) {
            System.out.println("Invalid Arguments");
            return null;
        }

        // Get Objects of Entities and create a resultList
        ArrayList<T> resultList = getEntities(subClass);
        ArrayList<T> bufferList = new ArrayList<>();

        // Loop through all records to find objects that matches
        for (int i = 0; i < args.length; i += 2) {
            String var = args[i];
            String content = args[i + 1].toLowerCase();

            for (T t : resultList) {
                // Condition checking to determine whether to add to resultList
                String test = t.toHashMap().get(var).toLowerCase();

                if (test.equals(content) || (!matchAll && test.indexOf(content) != -1))
                    bufferList.add(t);

                // Break if sufficient results are found
                if (resultList.size() == results && results != -1)
                    break;
            }

            // change resultList to bufferList and reset bufferList
            resultList = bufferList;
            bufferList = new ArrayList<>();
        }

        // Nothing was found
        if (resultList.size() == 0)
            return (T[]) Array.newInstance(subClass, 0);

        // Convert ArrayList to Array
        T[] tArr  = (T[]) Array.newInstance(subClass, resultList.size());
        tArr = resultList.toArray(tArr);

        return tArr;
    }

    /**
     * Check if an id is unique
     * @param var Variable in Entity Object
     * @param text text to search
     * @return true if unique
     */
    public boolean checkIdUnique(String var, String text) {
        Entity entityList[] = findAll(var, text, true);
        for (Entity e : entityList)
            if (!e.equals(this))
                return false;

        return true;
    }

    /**
     * Revert data back to saved state in file
     */
    public void revertOldData() {
        if (!isNewRecord())
            loadHashMap(oldData);
    }

}
