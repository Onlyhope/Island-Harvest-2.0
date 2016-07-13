package com.example.aaron.islandharvest;

/**
 * Created by Aaron on 7/8/2016.
 */
public class Route {

    private int ID;
    private int userID;
    private int agencyID;
    private int donorID;

    private String agencyAddress;
    private String donorAddress;

    private boolean isComplete;

    public Route(int initID, int initUserID, int initAgencyID, int initDonorID) {
        ID = initID;
        userID = initUserID;
        agencyID = initAgencyID;
        donorID = initDonorID;
        agencyAddress = "";
        donorAddress = "";
        isComplete = false;
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

    public int getAgencyID() {
        return agencyID;
    }

    public void setAgencyID(int agencyID) {
        this.agencyID = agencyID;
    }

    public boolean isComplete() {
        return isComplete;
    }

    // Setters

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getAgencyAddress() {
        return agencyAddress;
    }

    public void setAgencyAddress(String agencyAddress) {
        this.agencyAddress = agencyAddress;
    }

    public String getDonorAddress() {
        return donorAddress;
    }

    public void setDonorAddress(String donorAddress) {
        this.donorAddress = donorAddress;
    }
}
