package com.example.dailyspending.entity;

import java.io.Serializable;

public class Transaction {
    String id;

    private double money;
    private String category;
    private String note;
    private String date;
    private String with;
    private String event;
    private String remind;
    private boolean onReport;
    private boolean spending;

    public Transaction() {
    }

    public Transaction(double money, String category, String note, String date, String with, String event, String remind, boolean onReport) {
        this.money = money;
        this.category = category;
        this.note = note;
        this.date = date;
        this.with = with;
        this.event = event;
        this.remind = remind;
        this.onReport = onReport;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWith() {
        return with;
    }

    public void setWith(String with) {
        this.with = with;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getRemind() {
        return remind;
    }

    public void setRemind(String remind) {
        this.remind = remind;
    }

    public boolean isOnReport() {
        return onReport;
    }

    public void setOnReport(boolean onReport) {
        this.onReport = onReport;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isSpending() {
        return spending;
    }

    public void setSpending(boolean spending) {
        this.spending = spending;
    }
}
