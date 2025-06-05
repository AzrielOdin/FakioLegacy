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
import com.example.fakiolegacy.utils.FileHelper;
import com.example.fakiolegacy.utils.GalleryViewModelFactory;
import com.example.fakiolegacy.utils.Logger;
import com.example.fakiolegacy.utils.PermissionsHandler;
import com.google.android.material.snackbar.Snackbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (isStoragePermissionGranted()) return;

        FileHelper fileHelper = new FileHelper(requireContext());
        uploadRepository = new UploadRepository(fileHelper);

        initViewModel();
        setupFab();
        setupGalleryComponents();
        setupObservers();
        viewModel.loadMedia();
        setupScrollAnimation();
    }

    private boolean isStoragePermissionGranted() {
        if (!PermissionsHandler.hasStoragePermission(requireContext())) {
            PermissionsHandler.requestStoragePermission(this, new PermissionsHandler.PermissionCallback() {
                @Override
                public void onPermissionGranted() {
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.nav_host_fragment_content_main, new GalleryFragment())
                            .commit();
                }

                @Override
                public void onPermissionDenied() {
                    PermissionsHandler.showPermissionDeniedMessage(requireContext(),
                            getString(R.string.error_storage_permission_required));
                    requireActivity().onBackPressed();
                }
            });
            return true;
        }
        return false;
    }


    private void initViewModel() {
        ImageRepository repository = new ImageRepository(requireContext().getContentResolver());
        viewModel = new ViewModelProvider(this, new GalleryViewModelFactory(repository))
                .get(GalleryViewModel.class);
    }

    private void setupFab() {
        binding.fabUpload.setOnClickListener(v -> {
            if (viewModel.getSelectedImage().getValue() != null) {
                uploadSelectedImage();
            } else {
                Toast.makeText(requireContext(), getString(R.string.error_no_image_selected), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setupGalleryComponents() {
        imageAdapter = new ImageAdapter(item -> viewModel.selectImage(item));
        binding.imageGrid.setLayoutManager(new GridLayoutManager(getContext(), 4));
        binding.imageGrid.setAdapter(imageAdapter);

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
    }


    private void setupObservers() {
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
    }

    private void setupScrollAnimation() {
        binding.appBarLayout.addOnOffsetChangedListener((appBarLayout1, verticalOffset) -> {
            binding.imagePreview.setAlpha(1.0f);
        });
    }

    private void uploadSelectedImage() {
        binding.progressBar.setVisibility(View.VISIBLE);

        Uri imageUri = viewModel.getSelectedImage().getValue().getUri();

        uploadRepository.uploadImage(imageUri, new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<UploadResponse> call, @NonNull Response<UploadResponse> response) {
                requireActivity().runOnUiThread(() -> binding.progressBar.setVisibility(View.GONE));

                if (response.isSuccessful() && response.body() != null) {
                    String message = "Upload successful: " + response.body().getFilename();
                    requireActivity().runOnUiThread(() ->
                            Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT).show()
                    );

                    HistoryRepository historyRepository = new HistoryRepository(requireContext());
                    historyRepository.addToHistory(response.body(), imageUri);
                } else {
                    requireActivity().runOnUiThread(() ->
                            Snackbar.make(binding.getRoot(), getString(R.string.error_upload_failed), Snackbar.LENGTH_SHORT).show()
                    );
                    Logger.logError("Upload failed: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<UploadResponse> call, @NonNull Throwable t) {
                requireActivity().runOnUiThread(() -> binding.progressBar.setVisibility(View.GONE));

                requireActivity().runOnUiThread(() ->
                        Snackbar.make(binding.getRoot(), "Error: " + t.getMessage(), Snackbar.LENGTH_SHORT).show()
                );
                Logger.logError("Upload failed: " + t.getMessage(), t);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}