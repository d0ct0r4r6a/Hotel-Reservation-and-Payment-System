package com.hotel.entities;

import com.hotel.core.Entity;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Entity of Guest Object
 * @author  Tan Jun Xiang
 * @version 1.0
 */
public class Guest extends Entity<Guest> {

    // Variable Declaration
    private String id;
    private IdType idType;
    private String name;
    private String address;
    private String country;
    private String gender;
    private String nationality;
    private String contact;
    private String creditCard;

    // Dynamic Relationship
    private Reservation reservation = null;
    private Room room = null;

    // Enumerations
    public enum IdType {
        PASSPORT, DRIVING_LICENSE
    }


    /**
     * Constructor of Guest()
     */
    public Guest() {
        super(Guest.class);
    }

    /**
     * Constructor of Guest(HashMap). Receives a HashMap parameter with all data
     */
    public Guest(HashMap<String, String> data) {
        super(Guest.class);
        this.loadHashMap(data);
    }



    /**
     * Get the value of contact
     *
     * @return the value of contact
     */
    public String getContact() {
        return contact;
    }

    /**
     * Set the value of contact
     *
     * @param contact new value of contact
     */
    public void setContact(String contact) {
        this.contact = contact;
    }


    /**
     * Get the value of nationality
     *
     * @return the value of nationality
     */
    public String getNationality() {
        return nationality;
    }

    /**
     * Set the value of nationality
     *
     * @param nationality new value of nationality
     */
    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    /**
     * Get the value of gender
     *
     * @return the value of gender
     */
    public String getGender() {
        return gender;
    }

    /**
     * Set the value of gender
     *
     * @param gender new value of gender
     */
    public void setGender(String gender) {
        this.gender = gender;
    }


    /**
     * Get the value of counGtry
     *
     * @return the value of country
     */
    public String getCountry() {
        return country;
    }

    /**
     * Set the value of country
     *
     * @param country new value of country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Get the value of address
     *
     * @return the value of address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Set the value of address
     *
     * @param address new value of address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Get the value of name
     *
     * @return the value of name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the value of name
     *
     * @param name new value of name
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(String creditCard) {
        this.creditCard = creditCard;
    }

    /**
     * Get Room Object if exists from Reservation
     * @return Room Object if exists, null otherwise
     */
    public Room getRoom() {
        Reservation rsv = getReservation();
        // Return only when user checked in
        if (rsv.getReservationStatus() == Reservation.ReservationStatus.CHECKED_IN)
            return rsv.getRoom();

        return null;
    }

    /**
     * Get Reservation object if exists
     * @return Reservation Object
     */
    public Reservation getReservation() {
        if (reservation == null)
            setReservation(new Reservation().find("guestId", getId()));

        return reservation;
    }

    /**
     * Set Reservation object linked to Guest
     * @param reservation Reservation Object
     */
    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    /**
     * Get the value of id
     * @return id Guest id
     */
    public String getId() {
        return id;
    }

    /**
     * Set the value of id
     * @param id Guest id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get the value of idType enum
     * @return value of idType
     */
    public IdType getIdType() {
        return idType;
    }


    /**
     * Set the value of idType
     * @param idType set enum value
     */
    public void setIdType(IdType idType) {
        this.idType = idType;
    }

    @Override
    public HashMap<String, String> toHashMap() {
        LinkedHashMap<String, String> results = new LinkedHashMap<>();
        results.put("id", getId());
        results.put("idType", getIdType().toString());
        results.put("name", getName());
        results.put("address", getAddress());
        results.put("country", getCountry());
        results.put("gender", getGender());
        results.put("nationality", getNationality());
        results.put("contact", getContact());
        results.put("creditCard", getCreditCard());

        return results;
    }

    @Override
    public void loadHashMap(HashMap<String, String> results) {
        setId(results.get("id"));
        setIdType(IdType.valueOf(results.get("idType")));
        setName(results.get("name"));
        setAddress(results.get("address"));
        setCountry(results.get("country"));
        setGender(results.get("gender"));
        setNationality(results.get("nationality"));
        setContact(results.get("contact"));
        setCreditCard(results.get("creditCard"));
    }

    @Override
    public HashMap<String, String> getAttributeLabels() {
        return new HashMap<String, String>(){{
            put("idType", "Type of ID");
            put("id", "ID Number");
            put("name", "Name");
            put("address", "Address");
            put("country", "Country");
            put("gender", "Gender");
            put("nationality", "Nationality");
            put("contact", "Contact");
            put("creditCard", "Credit Card");
            put("PASSPORT", "Passport");
            put("DRIVING_LICENSE", "Driving License");
        }};
    }

    public String[][] getEditableList() {
        String[][] list = {
                {"idType",  Types.ENUM.toString()},
                {"id",      Types.STRING.toString()},
                {"name",    Types.STRING.toString()},
                {"address", Types.STRING.toString()},
                {"country", Types.STRING.toString()},
                {"gender",  Types.GENDER.toString()},
                {"nationality",Types.STRING.toString()},
                {"contact", Types.INT.toString()},
                {"creditCard", Types.STRING.toString()},
        };
        return list;
    }

    public HashMap<String, Enum[]> getEnumList() {
        return new HashMap() {{
            put("idType", IdType.values());
        }};
    }

    @Override
    public boolean validate() {
        errors.clear();

        if (!checkIdUnique("id", getId()))
            errors.add("ID has already been used.");
        if (!(getGender().equals("M") || getGender().equals("F")))
            errors.add("Gender can only be Male or Female");

        if (getContact().length() != 8)
            errors.add("Phone number is invalid. It should have 8 characters.");

        if (getCreditCard().length() != 16)
            errors.add("Credit Card Number is invalid. It should have 16 characters.");

        return super.validate();
    }
}
