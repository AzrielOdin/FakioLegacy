package com.example.fakiolegacy.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fakiolegacy.databinding.ItemImageBinding;
import com.example.fakiolegacy.models.ImageItem;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private List<ImageItem> images = new ArrayList<>();
    private final OnImageClickListener listener;

    public interface OnImageClickListener {
        void onImageClick(ImageItem item);
    }

    public ImageAdapter(OnImageClickListener listener) {
        this.listener = listener;
    }

    //Could be optimized using specific item diffing (DiffUtil) depending on performance usage
    //Galleries can become very large
    public void setImages(List<ImageItem> images) {
        this.images = images;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemImageBinding binding = ItemImageBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ImageViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        holder.bind(images.get(position));
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {
        private final ItemImageBinding binding;

        public ImageViewHolder(ItemImageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ImageItem item) {
            Glide.with(binding.getRoot().getContext())
                    .load(item.getUri())
                    .into(binding.imageThumbnail);

            binding.getRoot().setOnClickListener(v -> listener.onImageClick(item));
        }
    }
}
