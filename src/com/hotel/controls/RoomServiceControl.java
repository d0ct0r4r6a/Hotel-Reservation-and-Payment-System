package com.hotel.controls;

import com.hotel.core.Boundary;
import com.hotel.core.Control;
import com.hotel.entities.*;

import java.util.ArrayList;

/**
 * Room Service Control Object
 * Fetch information from Room Service Entity and pass on to Room Service Boundary
 * @author  Tan Jun Xiang
 * @version 1.0
 */
public class RoomServiceControl implements Control {
    private Boundary rsb;
    private static final String menu[] = {
            "Room Service System",
            "View Menu",
            "Select Room",
            "Select Guest",
            "Mark Order Preparing",
            "Mark Order Delivered",
    };

    private static final String viewMenu[] = {
            "Room Service System (View Menu)",
            "Select Room",
            "Select Guest",
    };

    public RoomServiceControl() {
        rsb = new Boundary();
    }

    @Override
    public void process() {
        int input = rsb.process(menu);
        switch(input) {
            case 1:
                viewMenu();
                break;
            case 2:
                selectRoom();
                break;
            case 3:
                selectGuest();
                break;
            case 4:
                markOrder(RoomService.OrderStatus.PREPARING);
                break;
            case 5:
                markOrder(RoomService.OrderStatus.DELIVERED);
                break;
            case 0:
                break;
            default:
                rsb.functionNotDeployed();
        }

        // Easier Recursive
        if (input != 0)
            process();
    }

    private void viewMenu() {
        MenuItem mi = new MenuItemControl().viewMenu();
        if (mi == null)
            return;

        // Sub Menu
        int input = rsb.process(viewMenu);
        switch(input) {
            case 1:
                selectRoom();
                break;
            case 2:
                selectGuest();
                break;
            case 0:
                break;
            default:
                rsb.functionNotDeployed();
        }

        // Easier Recursive
        if (input != 0)
            process();
    }

    private void selectRoom() {
        Room r = new RoomControl().search();
        if (r == null || r.getReservation() == null) {
            rsb.printInvalidInput();
            return;
        }

        // Guest Details
        if (!rsb.checkRecord(r.getReservation().getGuest(), "Guest Details", false))
            return;

        RoomService rs = selectMenu(r.getReservation());
        saveRoomServiceOrder(rs);
    }

    private void selectGuest() {
        Guest g = new GuestControl().search();
        if (g == null)
            return;

        if (g.getReservation() != null && g.getReservation().getReservationStatus() != Reservation.ReservationStatus.CHECKED_IN) {
            rsb.println("Either Reservation has not been created or Reservation has not been checked in.");
            rsb.getString();
            return;
        }

        RoomService rs = selectMenu(g.getReservation());
        saveRoomServiceOrder(rs);
    }

    private RoomService selectMenu(Reservation rsv) {
        RoomService rs = new RoomService();
        rs.setReservation(rsv);

        String cond;
        do {
            chooseMenuItem(rs);

            do {
                System.out.println("Add more items? (Y / N):");
                cond = rsb.getString();
            } while(!cond.toUpperCase().equals("Y") && !cond.toUpperCase().equals("N"));
        } while(cond.toUpperCase().equals("Y"));

        System.out.println("Remarks");
        rs.setRemarks(rsb.getString());

        return rs;
    }

    public void saveRoomServiceOrder(RoomService rs) {
        // Confirm Order
        do {
            printOrder(rs);
            rsb.println("Press Y to confirm, N to discard and (No.) to edit a field and (No.) to edit a field.");
            String input = rsb.getString();

            if (input.toUpperCase().equals("Y") || input.toUpperCase().equals("N")) {
                if (input.toUpperCase().equals("Y")) {
                    if (rs.save())
                        System.out.println("Order saved successfully.");
                    else
                        System.out.println("Order failed to be saved.");
                } else
                    System.out.println("Order Discarded");
                rsb.getString();
                return;
            }

            try {
                int ipt = Integer.parseInt(input);
                if (ipt > 0 && ipt <= rs.getMenuItems().length)
                    updateRecord(rs, rs.getMenuItems()[ipt - 1]);
                else
                    System.out.println("Invalid Input");
            } catch(NumberFormatException e) {}
        } while(true);
    }

    public void chooseMenuItem(RoomService rs) {
        OrderMenuItem omi = updateRecord(rs, null);
        if (omi != null)
            rs.addMenuItem(omi);
    }

    public OrderMenuItem updateRecord(RoomService rs, OrderMenuItem omi) {
        MenuItem mi = new MenuItemControl().viewMenu();
        if (mi == null)
            return null;
        if (omi == null)
            omi = new OrderMenuItem();

        omi.setMenuItem(mi);
        omi.setRoomService(rs);
        omi = rsb.processModel(omi, "", omi.toHashMap());
        return omi;
    }

    public void printOrder(RoomService rs) {
        System.out.println("Order Details");
        int i = 1;
        for (OrderMenuItem omi : rs.getMenuItems())
            System.out.printf("%d. %s x %s: %s\n", i++, omi.getName(), omi.getQty(), rsb.toCurrency(omi.getPrice()));
        System.out.println();
        if (!rs.getRemarks().equals(""))
            System.out.println(String.format("Remarks: %s", rs.getRemarks()));

        System.out.printf("Total Price: %s\n", rsb.toCurrency(rs.getTotal()));
    }

    private void markOrder(RoomService.OrderStatus orderStatus) {
        // Get Room Service Order
        RoomService rsList[];
        if (orderStatus == RoomService.OrderStatus.PREPARING)
            rsList = new RoomService().findAll("status", RoomService.OrderStatus.CONFIRMED.toString());
        else
            rsList =new RoomService().findAll("status", RoomService.OrderStatus.PREPARING.toString());

        if (rsList.length == 0) {
            System.out.println("No Room Service Orders to be processed.");
            rsb.getString();
            return;
        }

        ArrayList<String> al = new ArrayList<>();
        al.add("Choose Order:");
        for (RoomService rs : rsList)
            al.add(String.format("%s - %s", rs.getReservation().getRoom().getRoomId(), rs.getReservation().getGuest().getName()));

        int input = rsb.processMenu(al.toArray(new String[al.size()]), false);
        if (input == 0)
            return;

        RoomService rs = rsList[input - 1];

        // Print Order
        do {
            printOrder(rs);
            System.out.println(String.format("Press Y to mark as %s. N to return to previous menu", rs.getAttributeLabel(orderStatus.toString())));
            String ipt = rsb.getString();
            if (ipt.toUpperCase().equals("Y"))
                break;
            else if (ipt.toUpperCase().equals("N"))
                return;
        } while(true);

        // Process Y
        rs.setStatus(orderStatus);
        if (rs.save())
            System.out.println(String.format("Order has been set to %s.", rs.getAttributeLabel(orderStatus.toString())));
        else
            System.out.println("Order failed to be updated.");

        rsb.getString();
    }
}
