package com.example.fakiolegacy.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.fakiolegacy.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private SettingsViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        SwitchCompat wifiSwitch = binding.switchWifiOnly;
        viewModel.getWifiOnlyEnabled().observe(getViewLifecycleOwner(), wifiSwitch::setChecked);
        wifiSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            viewModel.setWifiOnlyEnabled(isChecked);
        });

        viewModel.getAppVersion().observe(getViewLifecycleOwner(), version -> {
            binding.textVersionDesc.setText(version);
        });

        binding.cardFaq.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "FAQ clicked", Toast.LENGTH_SHORT).show();
        });

        binding.cardSupport.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Contact Support clicked", Toast.LENGTH_SHORT).show();
        });

        binding.cardPrivacy.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Privacy Policy clicked", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}