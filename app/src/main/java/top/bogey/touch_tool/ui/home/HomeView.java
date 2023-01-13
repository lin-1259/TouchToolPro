package top.bogey.touch_tool.ui.home;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;

import top.bogey.touch_tool.MainAccessibilityService;
import top.bogey.touch_tool.MainActivity;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.databinding.ViewHomeBinding;
import top.bogey.touch_tool.utils.AppUtils;

public class HomeView extends Fragment {
    private ViewHomeBinding binding;

    @Override
    public void onResume() {
        super.onResume();
        binding.ignoreBatteryBox.setVisibility(AppUtils.isIgnoredBattery(requireContext()) ? View.GONE : View.VISIBLE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ViewHomeBinding.inflate(inflater, container, false);

        binding.accessibilityButton.setOnClickListener(v -> {
            MainAccessibilityService service = MainApplication.getService();
            if (service == null) {
                AppUtils.showDialog(requireContext(), getString(R.string.accessibility_service_on_tips, getString(R.string.app_name)), result -> {
                    if (result) {
                        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                        requireActivity().startActivity(intent);
                    }
                });
                binding.accessibilityButton.setChecked(false);
            } else {
                binding.accessibilityButton.setChecked(true);
            }
        });
        MainAccessibilityService.serviceConnected.observe(getViewLifecycleOwner(), aBoolean -> {
            binding.accessibilityButton.setChecked(aBoolean);
            binding.accessibilityButton.setText(aBoolean ? R.string.accessibility_service_on : R.string.accessibility_service_off);
        });

        binding.serviceButton.setOnClickListener(v -> {
            MainAccessibilityService service = MainApplication.getService();
            if (service != null && service.isServiceConnected()) {
                if (service.isServiceEnabled()) {
                    service.setServiceEnabled(false);
                } else {
                    AppUtils.showDialog(requireContext(), R.string.service_des, result -> {
                        if (result) {
                            service.setServiceEnabled(true);
                        }
                    });
                }
            } else {
                Toast.makeText(getContext(), R.string.accessibility_service_off, Toast.LENGTH_SHORT).show();
            }
            binding.serviceButton.setChecked(false);
        });
        MainAccessibilityService.serviceEnabled.observe(getViewLifecycleOwner(), aBoolean -> {
            binding.serviceButton.setChecked(aBoolean);
            binding.serviceButton.setText(aBoolean ? R.string.service_on : R.string.service_off);
        });

        binding.captureButton.setOnClickListener(v -> {
            MainActivity activity = MainApplication.getActivity();
            activity.launchNotification((code, intent) -> {
                if (code == Activity.RESULT_OK) {
                    MainAccessibilityService service = MainApplication.getService();
                    if (service != null && service.isServiceConnected()) {
                        if (service.isCaptureEnabled()) {
                            service.stopCaptureService();
                        } else {
                            AppUtils.showDialog(requireContext(), R.string.capture_service_des, result -> {
                                if (result) {
                                    service.startCaptureService(false, null);
                                }
                            });
                        }
                    } else {
                        Toast.makeText(getContext(), R.string.accessibility_service_off, Toast.LENGTH_SHORT).show();
                    }
                }
            });
            binding.captureButton.setChecked(false);
        });
        MainAccessibilityService.captureEnabled.observe(getViewLifecycleOwner(), aBoolean -> {
            binding.captureButton.setChecked(aBoolean);
            binding.captureButton.setText(aBoolean ? R.string.capture_service_on : R.string.capture_service_off);
        });

        binding.ignoreBatteryBox.setVisibility(AppUtils.isIgnoredBattery(requireContext()) ? View.GONE : View.VISIBLE);
        binding.ignoreBatteryButton.setOnClickListener(v -> AppUtils.gotoBatterySetting(requireContext()));

        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.menu_main, menu);
            }

            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.autoStart:
                    case R.id.popOnBackground:
                        AppUtils.gotoAppDetailSetting(requireActivity());
                        break;
                    case R.id.lockApp:
                        MainAccessibilityService service = MainApplication.getService();
                        if (service != null && service.isServiceConnected()) {
                            service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
                        } else {
                            Toast.makeText(service, R.string.accessibility_service_off_tips, Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.tutorial:
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.qq.com/doc/p/0f4de9e03534db3780876b90965e9373e4af93f0"));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } catch (Exception ignored) {
                        }
                        break;
                }
                return true;
            }
        });

        return binding.getRoot();
    }
}
