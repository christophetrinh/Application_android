package com.example.moneyapps.data;

/**
 * Data object representing a single expense.
 */

public class Expense {

    private String _id;
    private String retail;
    private String date;
    private String place;
    private String category;
    private String tag;
    private double amount;


    public Expense( String id, String retail, String date, String place, String category, String tag, double amount) {
        this._id = id;
        this.retail = retail;
        this.date = date;
        this.place = place;
        this.category = category;
        this.tag = tag;
        this.amount = amount;
    }

    public String getId() {
        return _id;
    }

    public String getRetail() {
        return retail;
    }

    public String getDate() {
        return date;
    }

    public String getPlace() {
        return place;
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public String getTag() {
        return tag;
    }

    @Override
    public String toString() {
        return retail;
    }

}
