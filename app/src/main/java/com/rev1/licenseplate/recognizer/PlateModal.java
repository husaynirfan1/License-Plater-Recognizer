package com.rev1.licenseplate.recognizer;/*
 * *
 *  * Created by Husayn on 22/10/2021, 5:04 PM
 *  * Copyright (c) 2021 . All rights reserved.
 *  * Last modified 22/10/2021, 2:29 PM
 *
 */

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "plate_table")
public class PlateModal {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String no_plate;
    private boolean isRegistered;

    public PlateModal(String no_plate, boolean isRegistered) {
        this.no_plate = no_plate;
        this.isRegistered = isRegistered;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNo_plate() {
        return no_plate;
    }

    public void setNo_plate(String no_plate) {
        this.no_plate = no_plate;
    }

    public boolean isRegistered() {
        return isRegistered;
    }

    public void setRegistered(boolean registered) {
        isRegistered = registered;
    }
}
