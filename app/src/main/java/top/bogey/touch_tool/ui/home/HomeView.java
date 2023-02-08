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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;

import top.bogey.touch_tool.MainAccessibilityService;
import top.bogey.touch_tool.MainActivity;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.TaskRepository;
import top.bogey.touch_tool.databinding.ViewHomeBinding;
import top.bogey.touch_tool.utils.AppUtils;
import top.bogey.touch_tool.utils.SettingSave;

public class HomeView extends Fragment {
    private ViewHomeBinding binding;
    private TaskRecyclerViewAdapter adapter;

    private float lastX, lastY;

    @Override
    public void onResume() {
        super.onResume();
        binding.ignoreBatteryBox.setVisibility(AppUtils.isIgnoredBattery(requireContext()) ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.tasksBox.setAdapter(null);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

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
                            Toast.makeText(getContext(), R.string.accessibility_service_off_tips, Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.tutorial:
                        AppUtils.gotoUrl(getContext(), "https://docs.qq.com/doc/p/0f4de9e03534db3780876b90965e9373e4af93f0");
                        break;
                    case R.id.importTask:
                        MainActivity activity = MainApplication.getActivity();
                        if (activity != null) {
                            activity.launcherContent((code, intent) -> {
                                if (code == Activity.RESULT_OK) {
                                    Uri uri = intent.getData();
                                    if (uri != null) {
                                        activity.saveTasksByFile(uri);
                                    }
                                }
                            });
                        }
                        break;
                }
                return true;
            }
        }, getViewLifecycleOwner());

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


        adapter = new TaskRecyclerViewAdapter(this);
        binding.tasksBox.setAdapter(adapter);
        binding.tasksBox.setOnTouchListener((v, event) -> {
            float x = event.getX();
            float y = event.getY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastX = x;
                    lastY = y;
                    break;
                case MotionEvent.ACTION_UP:
                    float dx = lastX - x;
                    float dy = lastY - y;
                    // 横向滑动更多
                    if (Math.abs(dx) > Math.abs(dy)) {
                        // 向左划，显示下一个
                        if (dx > 0) {
                            selectTab(binding.tabBox.getSelectedTabPosition() + 1);
                        } else {
                            selectTab(binding.tabBox.getSelectedTabPosition() - 1);
                        }
                    }
                    break;
            }
            return false;
        });

        for (String tag : SettingSave.getInstance().getTags(requireContext())) {
            binding.tabBox.addTab(binding.tabBox.newTab().setText(tag));
        }
        binding.tabBox.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                CharSequence text = tab.getText();
                if (text == null) return;
                adapter.showTasksByTag(text.toString());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        selectTab(0);

        binding.folderButton.setOnClickListener(v -> showTabView());

        binding.selectAllButton.setOnClickListener(v -> adapter.selectAll());
        binding.deleteButton.setOnClickListener(v -> AppUtils.showDialog(requireContext(), R.string.delete_task_tips, result -> {
            if (result) {
                adapter.deleteSelectTasks();
                hideBottomBar();
            }
        }));

        binding.exportButton.setOnClickListener(v -> {
            adapter.exportSelectTasks();
            hideBottomBar();
        });

        binding.moveButton.setOnClickListener(v -> showTabView());
        binding.cancelButton.setOnClickListener(v -> {
            adapter.unSelectAll();
            hideBottomBar();
        });

        binding.addButton.setOnClickListener(v -> AppUtils.showEditDialog(requireContext(), R.string.task_add, null, result -> {
            if (result != null && result.length() > 0) {
                Task task = new Task();
                task.setTitle(result.toString());
                TaskRepository.getInstance().saveTask(task);
            }
        }));

        return binding.getRoot();
    }

    public void selectTab(int index) {
        TabLayout.Tab tab = binding.tabBox.getTabAt(index);
        if (tab != null) {
            CharSequence text = tab.getText();
            if (adapter.isCheck() && text != null && text.length() > 0) {
                adapter.setSelectTasksTag(text.toString());
                hideBottomBar();
            }
            binding.tabBox.selectTab(tab);
        }
    }

    public void addTab(String tag) {
        binding.tabBox.addTab(binding.tabBox.newTab().setText(tag), binding.tabBox.getTabCount() - 1);
    }

    public void removeTab(int index) {
        binding.tabBox.removeTabAt(index);
    }

    public void showBottomBar() {
        binding.addButton.hide();
        binding.bottomBar.setVisibility(View.VISIBLE);
        adapter.setCheck(true);
    }

    public void hideBottomBar() {
        binding.addButton.show();
        binding.bottomBar.setVisibility(View.GONE);
        adapter.setCheck(false);
    }

    private void showTabView() {
        TagView tagView = new TagView(this);
        tagView.show(requireActivity().getSupportFragmentManager(), null);
    }
}
