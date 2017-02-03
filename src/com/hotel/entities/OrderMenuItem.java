package com.hotel.entities;

import com.hotel.core.Entity;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Entity of OrderMenuItem Object
 * @author  Tan Jun Xiang
 * @version 1.0
 */
public class OrderMenuItem extends Entity<OrderMenuItem> {
    private int qty;

    private String roomServiceId;
    private RoomService roomService;

    private double price;
    private String name;

    public OrderMenuItem() {
        super(OrderMenuItem.class);
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setMenuItem(MenuItem menuItem) {
        setName(menuItem.getName());
        setPrice(menuItem.getPrice());
    }

    public RoomService getRoomService() {
        if (roomService == null)
            roomService = new RoomService().find("id", roomServiceId);

        return roomService;
    }

    public void setRoomService(RoomService roomService) {
        this.roomService = roomService;
    }

    public double getTotalPrice() {
        return qty * getPrice();
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    private void setRoomServiceId(String roomServiceId) {
        this.roomServiceId = roomServiceId;
    }

    @Override
    public HashMap<String, String> toHashMap() {
        LinkedHashMap<String, String> results = new LinkedHashMap<>();
        results.put("name", getName());
        results.put("price", Double.toString(getPrice()));
        results.put("rsId", getRoomService().getId());
        results.put("qty", Integer.toString(getQty()));
        return results;
    }

    @Override
    public void loadHashMap(HashMap<String, String> results) {
        setName(results.get("name"));
        setPrice(Double.parseDouble(results.get("price")));
        setRoomServiceId(results.get("rsId"));
        setQty(Integer.parseInt(results.get("qty")));
    }

    @Override
    public boolean validate() {
        errors.clear();

        if (getQty() <= 0)
            errors.add("Quantity is invalid.");

        return errors.size() == 0;
    }

    @Override
    public HashMap<String, String> getAttributeLabels() {
        return new HashMap<String, String>(){{
            put("qty", "Quantity");
        }};
    }

    @Override
    public String[][] getEditableList() {
        String[][] list = {
            {"qty",  Types.INT.toString()}
        };
        return list;
    }

    @Override
    public HashMap<String, Enum[]> getEnumList() {
        return null;
    }
}
