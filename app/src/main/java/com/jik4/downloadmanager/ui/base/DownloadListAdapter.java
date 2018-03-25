package com.jik4.downloadmanager.ui.base;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jik4.downloadmanager.R;
import com.jik4.downloadmanager.database.model.Download;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

public class DownloadListAdapter extends RecyclerView.Adapter<DownloadListAdapter.RecyclerViewHolder> {

    private List<Download> list;

    public DownloadListAdapter() {
        this.list = new ArrayList<>();
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.download_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, int position) {
        Download download = list.get(position);
        holder.txtUrl.setText(download.getUrl());
        if (download.getStatus() == Download.Status.INACTIVE) {
            holder.imgStatus.setImageResource(android.R.drawable.presence_invisible);
        } else if (download.getStatus() == Download.Status.ACTIVE) {
            holder.imgStatus.setImageResource(android.R.drawable.presence_away);
        } else if (download.getStatus() == Download.Status.COMPLETED) {
            holder.imgStatus.setImageResource(android.R.drawable.presence_online);
        }
        String date = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(download.getCreatedAt());
        holder.txtCreatedAt.setText(date);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void update(List<Download> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgStatus;
        private TextView txtUrl;
        private TextView txtCreatedAt;

        RecyclerViewHolder(View view) {
            super(view);
            imgStatus = view.findViewById(R.id.img_status);
            txtUrl = view.findViewById(R.id.txt_url);
            txtCreatedAt = view.findViewById(R.id.txt_created_at);
        }
    }
}
