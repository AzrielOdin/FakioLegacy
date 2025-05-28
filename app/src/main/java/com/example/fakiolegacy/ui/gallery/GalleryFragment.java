package com.example.fakiolegacy.ui.gallery;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
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
import com.example.fakiolegacy.repositories.ImageRepository;
import com.example.fakiolegacy.utils.GalleryViewModelFactory;
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 100);
                return;
            }
        } else {
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                return;
            }
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
                // Do nothing
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Reload the fragment
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment_content_main, new GalleryFragment())
                        .commit();
            } else {
                Toast.makeText(requireContext(), "Storage permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}