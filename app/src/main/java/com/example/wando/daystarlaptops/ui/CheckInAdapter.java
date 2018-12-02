package com.example.wando.daystarlaptops.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.wando.daystarlaptops.R;
import com.example.wando.daystarlaptops.database.entity.CheckIn;
import com.example.wando.daystarlaptops.viewmodel.CheckInViewModel;

import java.text.DateFormat;
import java.util.List;

public class CheckInAdapter extends RecyclerView.Adapter<CheckInAdapter.CheckInViewHolder> {

    class CheckInViewHolder extends RecyclerView.ViewHolder {
        private final TextView itemType;
        private final TextView timeIn;
        private final TextView timeOut;
        private final TextView ownerName;
        private final TextView checkDate;

        private CheckInViewHolder(View itemView) {
            super(itemView);
            itemType = itemView.findViewById(R.id.tv_type_of_item);
            timeIn = itemView.findViewById(R.id.tv_check_in_time);
            timeOut = itemView.findViewById(R.id.tv_check_out_time);
            ownerName = itemView.findViewById(R.id.tv_name_of_owner);
            checkDate = itemView.findViewById(R.id.tv_checkDate);
        }
    }

    private final LayoutInflater mInflater;
    private List<CheckIn> checkInList; // Cached copy of checkIns
    private CheckInViewModel checkInViewModel;

    CheckInAdapter(Context context)
    {
        mInflater = LayoutInflater.from(context);
        checkInViewModel = ViewModelProviders.of((CheckInActivity) context).get(CheckInViewModel.class);
    }

    @Override
    public CheckInViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new CheckInViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CheckInViewHolder holder, int position) {
        if (checkInList != null) {
            CheckIn current = checkInList.get(position);
            holder.itemType.setText(checkInViewModel.getItemType(current));
            holder.timeIn.setText(current.getTime_in());
            holder.timeOut.setText(current.getTime_out());
            holder.ownerName.setText(checkInViewModel.getOwnerName(current));
            holder.checkDate.setText(current.getCheckin_date());
        } else {
            // Covers the case of data not being ready yet.
            holder.itemType.setText("No Item yet");
            holder.timeIn.setText("No time in");
            holder.timeOut.setText("No time out");
            holder.ownerName.setText("No owner yet");
            holder.checkDate.setText("No owner yet");
        }
    }

    void setCheckInList(List<CheckIn> checkInList){
        this.checkInList = checkInList;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // checkInList has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (checkInList!= null)
            return checkInList.size();
        else return 0;
    }
}
