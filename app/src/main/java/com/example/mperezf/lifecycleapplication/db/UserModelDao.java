package com.example.mperezf.lifecycleapplication.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Flowable;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface UserModelDao {

    @Query("SELECT * FROM UserModel ORDER BY mId DESC ")
    Flowable<List<UserModel>> getAllUserItems();

    @Insert(onConflict = REPLACE)
    void addUser(UserModel userModel);
}
