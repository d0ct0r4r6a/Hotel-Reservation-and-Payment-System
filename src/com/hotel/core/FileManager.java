package com.hotel.core;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Handles writing of Arrays into Files
 * Writes an Array of HashMap into a file
 * @author  Tan Jun Xiang
 * @version 1.0
 */
public class FileManager {

    private String filename;

    /**
     * Constructor of FileManager
     * @param filename File to be written to
     */
    public FileManager(String filename) {
        this.filename = filename;
    }

    /**
     * Loads the file and converted into an Array of HashMap objects
     * @return ArrayList of HashMap.
     */
    private ArrayList<HashMap<String, String>> loadFile()
    {
        try {
            File f = new File(filename);
            if (!f.exists())
                f.createNewFile();

            // Open the File for reading
            Scanner s = new Scanner(f);


            // Create an ArrayList to store the HashMap & HashMap to be a temporary buffer
            ArrayList<HashMap<String, String>> data = new ArrayList<>();
            HashMap<String, String> hashMap = new HashMap<>();

            // Loops to file EOL
            while (s.hasNextLine())
            {
                String line = s.nextLine();

                // Detection for the next Object
                if (line.length() == 0) {
                    data.add(hashMap);
                    hashMap = new HashMap<>();
                    continue;
                }

                String arr[] = line.trim().split("\t", 2);
                hashMap.put(arr[0], arr.length == 1 ? "" : arr[1]);
            }
            if (hashMap.keySet().size() != 0)
                data.add(hashMap);

            s.close();

            return data;
        } catch (Exception e) {
            System.out.printf("File is invalid: %s\n", filename);
        }

        return null;
    }


    /**
     * Write ArrayList of HashMap objects to a File
     * @param data ArrayList of HashMap objects
     * @return Success of the function
     */
    private boolean writeFile(ArrayList<HashMap<String, String>> data)
    {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(new File(filename)));
            for (int i = 0; i < data.size(); i++) {
                if (i > 0)
                    out.print("\n\n");

                HashMap<String, String> hashMap = data.get(i);
                String keys[] = hashMap.keySet().toArray(new String[hashMap.size()]);
                String result = "";

                for (String key : keys)
                    result += String.format("%s\t%s\n", key.toString(), hashMap.get(key));

                out.print(result.trim());
            }

            out.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }


    /**
     * Convert a HashMap into an Object
     * @param t takes in a object e.g. new Guest()
     * @param <T> class to be processed.
     * @return ArrayList with object casting
     */
    public <T> ArrayList<T> load(T t)
    {
        try {
            ArrayList<HashMap<String, String>> hashMapList = loadFile();
            ArrayList<T> entityList = new ArrayList<>();

            for (int i = 0; i < hashMapList.size(); i++) {
                Entity g = (Entity) t.getClass().newInstance();
                g.loadHashMap(hashMapList.get(i));
                entityList.add((T) g);
            }

            return entityList;
        } catch (Exception e) {
            e.printStackTrace();
            //System.out.printf("Error loading File to Object. \nFilename: %s\nClass: %s\n", filename, t.getClass());
        }

        return null;
    }

    /**
     * Convert an Object into HashMap to begin writing to file
     * @param entityList Entity Object Array
     * @param <T> Entity Class
     * @return
     */
    public <T> boolean write(ArrayList<T> entityList)
    {
        ArrayList<HashMap<String, String>> hashMapList = new ArrayList<>();

        for (int i = 0; i < entityList.size(); i++) {
            Entity id = (Entity) entityList.get(i);
            hashMapList.add(id.toHashMap());
        }

        return writeFile(hashMapList);
    }

}
