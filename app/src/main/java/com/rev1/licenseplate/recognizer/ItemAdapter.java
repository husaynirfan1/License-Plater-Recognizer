package com.rev1.licenseplate.recognizer;/*
 * *
 *  * Created by Husayn on 22/10/2021, 5:04 PM
 *  * Copyright (c) 2021 . All rights reserved.
 *  * Last modified 22/10/2021, 2:29 PM
 *
 */

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private OnItemClickListener listener;
    private AsyncListDiffer<PlateModal> mAsyncListDiffer;
    public ItemAdapter(){
        DiffUtil.ItemCallback<PlateModal> DIFF_CALLBACK = new DiffUtil.ItemCallback<PlateModal>() {
            @Override
            public boolean areItemsTheSame(PlateModal oldItem, PlateModal newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(PlateModal oldItem, PlateModal newItem) {
                // below line is to check the course name, description and course duration.
                return oldItem.getNo_plate().equals(newItem.getNo_plate()) &&
                        oldItem.isRegistered() == (newItem.isRegistered());
            }
        };
        mAsyncListDiffer = new AsyncListDiffer<>(this, DIFF_CALLBACK);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // below line is use to inflate our layout
        // file for each item of our recycler view.
        View item = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rv, parent, false);
        return new ViewHolder(item);
    }

    public PlateModal getitem(int positionl) {
        return mAsyncListDiffer.getCurrentList().get(positionl);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // below line of code is use to set data to
        // each item of our recycler view.
        PlateModal model = mAsyncListDiffer.getCurrentList().get(position);

        holder.plateNombor.setText(model.getNo_plate());
        holder.idItem.setText(String.format(Integer.toString(model.getId())));
    }

    @Override
    public int getItemCount() {
        return mAsyncListDiffer.getCurrentList().size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        // view holder class to create a variable for each view.
        TextView plateNombor, idItem;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing each view of our recycler view.
            plateNombor = itemView.findViewById(R.id.plateTV);
            idItem = itemView.findViewById(R.id.idTV);


            // adding on click listener for each item of recycler view.
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // inside on click listener we are passing
                    // position to our item of recycler view.
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(mAsyncListDiffer.getCurrentList().get(position));
                    }
                }
            });
        }

    }
    public interface OnItemClickListener {
        void onItemClick(PlateModal model);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    public void updateList(List<PlateModal> modals) {
        mAsyncListDiffer.submitList(modals);
    }
}
