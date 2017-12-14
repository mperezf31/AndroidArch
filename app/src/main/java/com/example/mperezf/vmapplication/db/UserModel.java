package com.example.mperezf.vmapplication.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class UserModel {

    @PrimaryKey(autoGenerate = true)
    public int mId;
    private String mName;
    private String mLastName;

    public UserModel(String name, String lastName) {
        this.mName = name;
        this.mLastName = lastName;
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getLastName() {
        return mLastName;
    }
}
