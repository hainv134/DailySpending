package com.example.dailyspending.entity;

import java.io.Serializable;

public class Category {
    String id;

    String name;
    boolean spend;
    String budget;

    public Category() {
    }

    public Category(String name, boolean spend, String budget) {
        this.name = name;
        this.spend = spend;
        this.budget = budget;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSpend() {
        return spend;
    }

    public void setSpend(boolean spend) {
        this.spend = spend;
    }

    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }

}
