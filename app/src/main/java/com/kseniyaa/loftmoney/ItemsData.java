package com.kseniyaa.loftmoney;

import java.util.List;

public class ItemsData {
    private String status;
    private List<Item> data = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Item> getData() {
        return data;
    }

    public void setData(List<Item> data) {
        this.data = data;
    }
}

