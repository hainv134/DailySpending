package com.example.dailyspending.entity;

import java.io.Serializable;

public class Budget{

    private  String id;
    private String category;
    private String startDate;
    private String endDate;
    private double target;

    public Budget() {
    }

    public Budget(String category, String startDate, String endDate, double target) {
        this.category = category;
        this.startDate = startDate;
        this.endDate = endDate;
        this.target = target;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public double getTarget() {
        return target;
    }

    public void setTarget(double target) {
        this.target = target;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
