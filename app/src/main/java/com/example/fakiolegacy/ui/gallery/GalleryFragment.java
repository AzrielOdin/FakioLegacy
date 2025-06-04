package com.example.fakiolegacy.ui.gallery;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.example.fakiolegacy.R;
import com.example.fakiolegacy.adapters.FolderAdapter;
import com.example.fakiolegacy.adapters.ImageAdapter;
import com.example.fakiolegacy.databinding.FragmentGalleryBinding;
import com.example.fakiolegacy.models.FolderItem;
import com.example.fakiolegacy.network.UploadRepository;
import com.example.fakiolegacy.network.UploadResponse;
import com.example.fakiolegacy.repositories.HistoryRepository;
import com.example.fakiolegacy.repositories.ImageRepository;
import com.example.fakiolegacy.utils.GalleryViewModelFactory;
import com.example.fakiolegacy.utils.PermissionsHandler;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//TODO Gallery animation and optimization
public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;
    private GalleryViewModel viewModel;
    private ImageAdapter imageAdapter;
    private FolderAdapter folderAdapter;
    private UploadRepository uploadRepository;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    //TODO Gallery fragment segment code
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!PermissionsHandler.hasStoragePermission(requireContext())) {
            PermissionsHandler.requestStoragePermission(this, new PermissionsHandler.PermissionCallback() {
                @Override
                public void onPermissionGranted() {
                    // Reload the fragment once permission is granted
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.nav_host_fragment_content_main, new GalleryFragment())
                            .commit();
                }

                @Override
                public void onPermissionDenied() {
                    PermissionsHandler.showPermissionDeniedMessage(requireContext(),
                            "Storage permission is required to view gallery");
                    // Navigate back or show empty state
                    requireActivity().onBackPressed();
                }
            });
            return;
        }
        uploadRepository = new UploadRepository(requireContext());

        // Setup the upload FAB
        binding.fabUpload.setOnClickListener(v -> {
            if (viewModel.getSelectedImage().getValue() != null) {
                uploadSelectedImage();
            } else {
                Toast.makeText(requireContext(), "Please select an image first", Toast.LENGTH_SHORT).show();
            }
        });


        // Initialize ViewModel
        ImageRepository repository = new ImageRepository(requireContext().getContentResolver());
        viewModel = new ViewModelProvider(this, new GalleryViewModelFactory(repository))
                .get(GalleryViewModel.class);

        // Setup RecyclerView
        imageAdapter = new ImageAdapter(item -> viewModel.selectImage(item));
        binding.imageGrid.setLayoutManager(new GridLayoutManager(getContext(), 4));
        binding.imageGrid.setAdapter(imageAdapter);

        // Setup Spinner
        folderAdapter = new FolderAdapter(requireContext());
        binding.folderSpinner.setAdapter(folderAdapter);
        binding.folderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                FolderItem folder = folderAdapter.getItem(position);
                viewModel.selectFolder(folder);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Observe LiveData
        viewModel.getFolders().observe(getViewLifecycleOwner(), folders -> {
            folderAdapter.setFolders(folders);
        });

        viewModel.getSelectedFolder().observe(getViewLifecycleOwner(), folder -> {
            if (folder != null) {
                imageAdapter.setImages(folder.getImages());
            }
        });

        viewModel.getSelectedImage().observe(getViewLifecycleOwner(), image -> {
            if (image != null) {
                Glide.with(requireContext())
                        .load(image.getUri())
                        .into(binding.imagePreview);
            }
        });

        // Load folders
        viewModel.loadFolders();

        setupScrollAnimation();
    }

    //TODO
private void setupScrollAnimation() {
    // Get references to the views
    AppBarLayout appBarLayout = binding.appBarLayout;

    appBarLayout.addOnOffsetChangedListener(new com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener() {
        @Override
        public void onOffsetChanged(com.google.android.material.appbar.AppBarLayout appBarLayout, int verticalOffset) {
            // Calculate the progress (0: fully expanded, 1: fully collapsed)
            float progress = Math.abs(verticalOffset) / (float) appBarLayout.getTotalScrollRange();

            // Adjust the alpha of the image based on scroll position
            float imageAlpha = 1.0f - progress * 1.5f;
            binding.imagePreview.setAlpha(1.0f);

            // Show/hide TABBAR based on scroll position
            if (progress > 0.8) {
            } else if (progress < 0.2) {
            }
        }
    });
}

    //TODO Gallery fragment make sure this code is optimized and maybe move somewher else
    private void uploadSelectedImage() {
        // Show loading indicator
        binding.progressBar.setVisibility(View.VISIBLE);

        // Get the selected image URI
        Uri imageUri = viewModel.getSelectedImage().getValue().getUri();

        // Upload the image
        uploadRepository.uploadImage(imageUri, new Callback<UploadResponse>() {
            @Override
            public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                // Hide loading indicator
                requireActivity().runOnUiThread(() -> binding.progressBar.setVisibility(View.GONE));

                if (response.isSuccessful() && response.body() != null) {
                    // Show success message
                    String message = "Upload successful: " + response.body().getFilename();
                    requireActivity().runOnUiThread(() ->
                            Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT).show()
                    );

                    HistoryRepository historyRepository = new HistoryRepository(requireContext());
                    historyRepository.addToHistory(response.body(), imageUri);
                } else {
                    // Show error message
                    requireActivity().runOnUiThread(() ->
                            Snackbar.make(binding.getRoot(), "Upload failed", Snackbar.LENGTH_SHORT).show()
                    );
                }
            }

            @Override
            public void onFailure(Call<UploadResponse> call, Throwable t) {
                // Hide loading indicator
                requireActivity().runOnUiThread(() -> binding.progressBar.setVisibility(View.GONE));

                // Show error message
                requireActivity().runOnUiThread(() ->
                        Snackbar.make(binding.getRoot(), "Error: " + t.getMessage(), Snackbar.LENGTH_SHORT).show()
                );
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}