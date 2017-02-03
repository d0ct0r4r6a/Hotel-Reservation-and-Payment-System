package com.hotel.entities;

import com.hotel.core.Entity;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Entity of MenuItem Object
 * @author  Tan Jun Xiang
 * @version 1.0
 */
public class MenuItem extends Entity<MenuItem> {
    private String name;
    private String description;
    private String prepSteps;
    private double price;

    public MenuItem() {
        super(MenuItem.class);
    }

    public MenuItem(HashMap<String, String> data) {
        super(MenuItem.class);
        loadHashMap(data);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrepSteps() {
        return prepSteps;
    }

    public void setPrepSteps(String prepSteps) {
        this.prepSteps = prepSteps;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public HashMap<String, String> toHashMap() {
        LinkedHashMap<String, String> results = new LinkedHashMap<>();
        results.put("name", getName());
        results.put("description", getDescription());
        results.put("prepSteps", getPrepSteps());
        results.put("price", Double.toString(getPrice()));

        return results;
    }

    @Override
    public void loadHashMap(HashMap<String, String> results) {
        setName(results.get("name"));
        setDescription(results.get("description"));
        setPrepSteps(results.get("prepSteps"));
        setPrice(Double.parseDouble(results.get("price")));
    }

    @Override
    public boolean validate() {
        errors.clear();

        if (!checkIdUnique("name", getName()))
            errors.add("Name has already been used.");


        if (getPrice() <= 0)
            errors.add("Price is invalid.");

        return errors.size() == 0;
    }

    @Override
    public HashMap<String, String> getAttributeLabels() {
        return new HashMap<String, String>(){{
            put("name", "Name");
            put("description", "Description");
            put("prepSteps", "Preparation Steps");
            put("price", "Price");
        }};
    }

    @Override
    public String[][] getEditableList() {
        String[][] list = {
            {"name", Types.STRING.toString()},
            {"description", Types.STRING.toString()},
            {"prepSteps", Types.STRING.toString()},
            {"price", Types.CURRENCY.toString()}
        };
        return list;
    }

    @Override
    public HashMap<String, Enum[]> getEnumList() {
        return null;
    }
}
