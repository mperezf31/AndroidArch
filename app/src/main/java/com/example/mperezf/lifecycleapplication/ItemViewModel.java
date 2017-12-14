package com.example.mperezf.lifecycleapplication;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.example.mperezf.lifecycleapplication.db.UserModel;

public class ItemViewModel extends ViewModel {

    @NonNull
    public final String mUserName;
    @NonNull
    public final String mLastName;

    ItemViewModel(@NonNull final UserModel userModel) {
        this.mUserName = userModel.getName();
        this.mLastName = userModel.getLastName();
    }

}
