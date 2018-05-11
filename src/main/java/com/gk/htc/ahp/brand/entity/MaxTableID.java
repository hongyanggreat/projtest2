/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.ahp.brand.entity;

/**
 *
 * @author Private
 */
public class MaxTableID {

    int id;
    String tableId;
    int maxId;
    long nextRun;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public int getMaxId() {
        return maxId;
    }

    public void setMaxId(int maxId) {
        this.maxId = maxId;
    }

    public long getNextRun() {
        return nextRun;
    }

    public void setNextRun(long nextRun) {
        this.nextRun = nextRun;
    }

}
