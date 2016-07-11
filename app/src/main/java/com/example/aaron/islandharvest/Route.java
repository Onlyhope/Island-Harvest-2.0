package com.example.aaron.islandharvest;

/**
 * Created by Aaron on 7/8/2016.
 */
public class Route {

    private int ID;
    private int userID;
    private int agencyID;
    private int donorID;

    public Route(int initID, int initUserID, int initAgencyID, int initDonorID) {
        ID = initID;
        userID = initUserID;
        agencyID = initAgencyID;
        donorID = initDonorID;
    }

    // Getters

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getDonorID() {
        return donorID;
    }

    public void setDonorID(int donorID) {
        this.donorID = donorID;
    }

    // Setters

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getAgencyID() {
        return agencyID;
    }

    public void setAgencyID(int agencyID) {
        this.agencyID = agencyID;
    }
}
