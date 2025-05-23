package com.example.fakiolegacy.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.fakiolegacy.models.FolderItem;

import java.util.ArrayList;
import java.util.List;

public class FolderAdapter extends BaseAdapter {
    private List<FolderItem> folders = new ArrayList<>();
    private final Context context;

    public FolderAdapter(Context context) {
        this.context = context;
    }

    public void setFolders(List<FolderItem> folders) {
        this.folders = folders;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return folders.size();
    }

    @Override
    public FolderItem getItem(int position) {
        return folders.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView;

        if (convertView == null) {
            textView = new TextView(context);
            textView.setPadding(16, 16, 16, 16);
            textView.setTextSize(16);
        } else {
            textView = (TextView) convertView;
        }

        FolderItem folder = getItem(position);
        textView.setText(folder.getName() + " (" + folder.getImages().size() + ")");

        return textView;
    }
}