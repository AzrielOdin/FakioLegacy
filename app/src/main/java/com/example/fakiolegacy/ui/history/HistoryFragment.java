package com.example.fakiolegacy.ui.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.fakiolegacy.adapters.HistoryAdapter;
import com.example.fakiolegacy.databinding.FragmentHistoryBinding;
import com.example.fakiolegacy.repositories.HistoryRepository;
import com.example.fakiolegacy.utils.HistoryViewModelFactory;

public class HistoryFragment extends Fragment {

    private FragmentHistoryBinding binding;
    private HistoryViewModel viewModel;
    private HistoryAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize repository and ViewModel
        HistoryRepository repository = new HistoryRepository(requireContext());
        viewModel = new ViewModelProvider(this, new HistoryViewModelFactory(repository))
                .get(HistoryViewModel.class);

        // Initialize RecyclerView
        adapter = new HistoryAdapter(requireContext());
        binding.historyRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.historyRecyclerView.setAdapter(adapter);

        // Setup clear history button
        binding.clearHistoryButton.setOnClickListener(v -> viewModel.clearHistory());

        // Observe LiveData
        viewModel.getHistoryItems().observe(getViewLifecycleOwner(), historyItems -> {
            adapter.setHistoryItems(historyItems);
            binding.historyEmptyView.setVisibility(historyItems.isEmpty() ? View.VISIBLE : View.GONE);
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading ->
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE));

        // Load history data
        viewModel.loadHistory();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}