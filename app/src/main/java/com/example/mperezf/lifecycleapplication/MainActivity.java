package com.example.mperezf.lifecycleapplication;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.example.mperezf.lifecycleapplication.databinding.ActivityMainBinding;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        final UserViewModel userViewModel = getViewModel();
        getLifecycle().addObserver(userViewModel);
        mBinding.setUser(userViewModel);

        mBinding.listUsers.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        userViewModel.mUserList.observe(this, new Observer<List<ItemViewModel>>() {
            @Override
            public void onChanged(@Nullable List<ItemViewModel> itemViewModels) {
                mBinding.listUsers.setAdapter(new UserAdapter(itemViewModels));
            }
        });

        userViewModel.mMsg.observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String msg) {
                showSnackbar(msg);
            }
        });
    }

    private void showSnackbar(String msg) {
        //Close keyboard before show snackbar
        closeKeyboard();
        Snackbar.make(mBinding.baseLayout, msg, Snackbar.LENGTH_LONG).show();
    }

    private void closeKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view == null) return;
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) return;
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public UserViewModel getViewModel() {
        return ViewModelProviders.of(this).get(UserViewModel.class);
    }
}
