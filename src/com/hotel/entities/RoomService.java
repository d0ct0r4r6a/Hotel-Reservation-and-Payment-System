package com.hotel.entities;

import com.hotel.core.Entity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Entity of RoomService Object
 * @author Aubrey
 * @version 1.0
 */
public class RoomService extends Entity<RoomService> {
    public enum OrderStatus {
        CONFIRMED, PREPARING, DELIVERED
    }

    private static String dateFormat = "yyyy-MM-dd-H-m";
    
    private String remarks;
    private Date created;
    private OrderStatus status;

    private ArrayList<OrderMenuItem> menuItems;

    private String reservationId;
    private Reservation reservation;

    public RoomService() {
        super(RoomService.class);
        menuItems = new ArrayList<>();
    }

    public RoomService(HashMap<String, String> data) {
        super(RoomService.class);
        loadHashMap(data);
        menuItems = new ArrayList<>();
    }

    public void generateOrderId() {

    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Date getCreated() {
        if (created == null)
            setCreated(new Date());
        return created;
    }

    private void setCreated(Date created) {
        this.created = created;
    }


    public OrderMenuItem[] getMenuItems() {
        if (menuItems.size() == 0) {
            OrderMenuItem omiArr[] = new OrderMenuItem().findAll("rsId", getId());
            for (OrderMenuItem omi : omiArr)
                menuItems.add(omi);

            return omiArr;
        }

        return menuItems.toArray(new OrderMenuItem[menuItems.size()]);
    }

    private void setMenuItems(ArrayList<OrderMenuItem> menuItems) {
        this.menuItems = menuItems;
    }

    public void addMenuItem(OrderMenuItem menuItem) {
        menuItem.setRoomService(this);
        menuItems.add(menuItem);
    }

    public Reservation getReservation() {
        if (reservation == null)
            reservation = new Reservation().find("reservationCode", reservationId);
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public String getId() {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return String.format("%s-%s", sdf.format(getCreated()), getReservation().getRoom().getRoomId());
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public double getTotal() {
        double total = 0.0;
        for (OrderMenuItem omi : getMenuItems())
            total += omi.getTotalPrice();
        return total;
    }

    @Override
    public HashMap<String, String> toHashMap() {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        LinkedHashMap<String, String> results = new LinkedHashMap<>();

        results.put("id", getId());
        results.put("remarks", getRemarks());
        results.put("status", getStatus().toString());
        results.put("created", sdf.format(getCreated()));
        results.put("reservationId", getReservation().getReservationCode());
        return results;
    }

    @Override
    public void loadHashMap(HashMap<String, String> guestData) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);

            setRemarks(guestData.get("remarks"));
            setStatus(OrderStatus.valueOf(guestData.get("status")));
            setCreated(sdf.parse(guestData.get("created")));
            setReservationId(guestData.get("reservationId"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean validate() {
        errors.clear();

        if (getMenuItems().length == 0)
            errors.add("No menu items have been added to the order.");

        return errors.size() == 0;
    }

    @Override
    public boolean save() {
        if (isNewRecord())
            setStatus(OrderStatus.CONFIRMED);

        boolean success = super.save();
        if (!success)
            return false;

        // Save all Menu RoomService Items
        for (OrderMenuItem mi : getMenuItems())
            mi.save();

        return true;
    }

    @Override
    public HashMap<String, String> getAttributeLabels() {
        return new HashMap<String, String>(){{
            put("remarks", "Remarks");
            put("created", "Created On");
            put("status", "Order Status");
            put("CONFIRMED", "CONFIRMED");
            put("PREPARING", "Preparing");
            put("DELIVERED", "Delivered");
        }};

    }

    @Override
    public String[][] getEditableList() {
        String[][] list = {
            {"remarks", Types.STRING.toString()},
        };
        return list;
    }

    @Override
    public HashMap<String, Enum[]> getEnumList() {
        return new HashMap() {{
            put("status", OrderStatus.values());
        }};
    }
}
