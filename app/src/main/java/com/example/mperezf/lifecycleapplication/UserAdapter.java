package com.example.mperezf.lifecycleapplication;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.mperezf.lifecycleapplication.databinding.RowUserItemBinding;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private List<ItemViewModel> mUsers = new ArrayList<>();

    UserAdapter(@NonNull List<ItemViewModel> users) {
        mUsers.addAll(users);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RowUserItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.row_user_item, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ItemViewModel user = mUsers.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private RowUserItemBinding mBinding;

        ViewHolder(RowUserItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        void bind(@NonNull ItemViewModel user) {
            mBinding.setUser(user);
            mBinding.executePendingBindings();
        }
    }
}