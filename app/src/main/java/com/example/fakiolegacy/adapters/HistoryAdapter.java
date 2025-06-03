package com.example.fakiolegacy.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fakiolegacy.R;
import com.example.fakiolegacy.models.HistoryItem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<HistoryItem> historyItems;
    private final Context context;
    private final SimpleDateFormat dateFormat;

    public HistoryAdapter(Context context) {
        this.context = context;
        this.dateFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
    }

    public void setHistoryItems(List<HistoryItem> historyItems) {
        this.historyItems = historyItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        HistoryItem item = historyItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return historyItems != null ? historyItems.size() : 0;
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView filenameText;
        private final TextView timestampText;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.history_image);
            filenameText = itemView.findViewById(R.id.history_filename);
            timestampText = itemView.findViewById(R.id.history_timestamp);
        }

        void bind(HistoryItem item) {
            filenameText.setText(item.getFilename());
            timestampText.setText(dateFormat.format(new Date(item.getTimestamp())));

            //todo add proper placeholder and error images
            Glide.with(context)
                    .load(Uri.parse(item.getImagePath()))
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_menu_history)
                    .into(imageView);
        }
    }
}