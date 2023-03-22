package top.bogey.touch_tool.ui.home;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.MenuProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import top.bogey.touch_tool.MainAccessibilityService;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.TaskRepository;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.databinding.ActivityHomeBinding;
import top.bogey.touch_tool.ui.BaseActivity;
import top.bogey.touch_tool.ui.custom.KeepAliveFloatView;
import top.bogey.touch_tool.utils.AppUtils;
import top.bogey.touch_tool.utils.GsonUtils;
import top.bogey.touch_tool.utils.SettingSave;
import top.bogey.touch_tool.utils.easy_float.EasyFloat;

public class HomeActivity extends BaseActivity {
    private ActivityHomeBinding binding;
    private TaskRecyclerViewAdapter adapter;
    private float lastX, lastY;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolBar);
        addMenuProvider(new MenuProvider() {
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
                        AppUtils.gotoAppDetailSetting(HomeActivity.this);
                        break;
                    case R.id.lockApp:
                        MainAccessibilityService service = MainApplication.getInstance().getService();
                        if (service != null && service.isServiceConnected()) {
                            service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
                        } else {
                            Toast.makeText(HomeActivity.this, R.string.accessibility_service_off_tips, Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.tutorial:
                        AppUtils.gotoUrl(HomeActivity.this, "https://docs.qq.com/doc/p/24efb1da5ef37c58c3687606bd8c169fe73c52d0");
                        break;
                    case R.id.importTask:
                        launcherContent((code, intent) -> {
                            if (code == Activity.RESULT_OK) {
                                Uri uri = intent.getData();
                                if (uri != null) {
                                    saveTasksByFile(uri);
                                }
                            }
                        });
                        break;
                }
                return true;
            }
        });

        binding.accessibilityButton.setOnClickListener(v -> {
            MainAccessibilityService service = MainApplication.getInstance().getService();
            if (service == null) {
                AppUtils.showDialog(this, getString(R.string.accessibility_service_on_tips, getString(R.string.app_name)), result -> {
                    if (result) {
                        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                        startActivity(intent);
                    }
                });
                binding.accessibilityButton.setChecked(false);
            } else {
                binding.accessibilityButton.setChecked(true);
            }
        });
        MainAccessibilityService.serviceConnected.observe(this, aBoolean -> {
            binding.accessibilityButton.setChecked(aBoolean);
            binding.accessibilityButton.setText(aBoolean ? R.string.accessibility_service_on : R.string.accessibility_service_off);
        });

        binding.serviceButton.setOnClickListener(v -> {
            MainAccessibilityService service = MainApplication.getInstance().getService();
            if (service != null && service.isServiceConnected()) {
                if (service.isServiceEnabled()) {
                    service.setServiceEnabled(false);
                } else {
                    AppUtils.showDialog(this, R.string.service_des, result -> {
                        if (result) {
                            service.setServiceEnabled(true);
                        }
                    });
                }
            } else {
                Toast.makeText(this, R.string.accessibility_service_off, Toast.LENGTH_SHORT).show();
            }
            binding.serviceButton.setChecked(false);
        });
        MainAccessibilityService.serviceEnabled.observe(this, aBoolean -> {
            if (MainApplication.getInstance().getService() == null) return;
            if (aBoolean) {
                KeepAliveFloatView floatView = new KeepAliveFloatView(this);
                floatView.show();
            } else {
                EasyFloat.dismiss(KeepAliveFloatView.class.getCanonicalName());
            }
            binding.serviceButton.setChecked(aBoolean);
            binding.serviceButton.setText(aBoolean ? R.string.service_on : R.string.service_off);
        });

        binding.captureButton.setOnClickListener(v -> {
            launchNotification((code, intent) -> {
                if (code == Activity.RESULT_OK) {
                    MainAccessibilityService service = MainApplication.getInstance().getService();
                    if (service != null && service.isServiceConnected()) {
                        if (service.isCaptureEnabled()) {
                            service.stopCaptureService();
                        } else {
                            AppUtils.showDialog(this, R.string.capture_service_des, result -> {
                                if (result) {
                                    service.startCaptureService(false, null);
                                }
                            });
                        }
                    } else {
                        Toast.makeText(this, R.string.accessibility_service_off, Toast.LENGTH_SHORT).show();
                    }
                }
            });
            binding.captureButton.setChecked(false);
        });
        MainAccessibilityService.captureEnabled.observe(this, aBoolean -> {
            binding.captureButton.setChecked(aBoolean);
            binding.captureButton.setText(aBoolean ? R.string.capture_service_on : R.string.capture_service_off);
        });

        binding.ignoreBatteryBox.setVisibility(AppUtils.isIgnoredBattery(this) ? View.GONE : View.VISIBLE);
        binding.ignoreBatteryButton.setOnClickListener(v -> AppUtils.gotoBatterySetting(this));

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
                        return true;
                    }
                    break;
            }
            return false;
        });

        for (String tag : SettingSave.getInstance().getTags(this)) {
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
        binding.deleteButton.setOnClickListener(v -> AppUtils.showDialog(this, R.string.delete_task_tips, result -> {
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

        binding.addButton.setOnClickListener(v -> AppUtils.showEditDialog(this, R.string.task_add, null, result -> {
            if (result != null && result.length() > 0) {
                Task task = new Task();
                task.setTitle(result.toString());
                task.setTag(adapter.getTag());
                task.save();
            }
        }));

        handleIntent(getIntent());
        runFirstTimes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        WorldState.getInstance().resetAppMap(this);

        binding.ignoreBatteryBox.setVisibility(AppUtils.isIgnoredBattery(this) ? View.GONE : View.VISIBLE);

        String runningError = SettingSave.getInstance().getRunningError();
        if (runningError != null && !runningError.isEmpty()) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.dialog_title)
                    .setMessage(R.string.report_running_error_tips)
                    .setPositiveButton(R.string.report_running_error_copy_and_join, (dialog, which) -> {
                        dialog.dismiss();
                        AppUtils.gotoUrl(this, "https://jq.qq.com/?_wv=1027&k=c1HOe3Gk");
                        copyError(runningError);
                    })
                    .setNegativeButton(R.string.report_running_error_copy, (dialog, which) -> {
                        dialog.dismiss();
                        copyError(runningError);
                    })
                    .setNeutralButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                    .show();

            SettingSave.getInstance().setRunningError(null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.tasksBox.setAdapter(null);
    }

    private void runFirstTimes() {
        if (SettingSave.getInstance().getRunTimes() == 1) {
            SettingSave.getInstance().addRunTimes();
            try (InputStream inputStream = getAssets().open("default")) {
                byte[] bytes = new byte[inputStream.available()];
                if (inputStream.read(bytes) > 0) saveTasks(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
        tagView.show(getSupportFragmentManager(), null);
    }

    public View getRoot() {
        return binding.getRoot();
    }


    private void copyError(String error) {
        ClipboardManager manager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(getString(R.string.app_name), error);
        manager.setPrimaryClip(clipData);
        Toast.makeText(this, R.string.report_running_error_copied, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
        setIntent(null);
    }

    public void handleIntent(Intent intent) {
        if (intent == null) return;

        if (Intent.ACTION_SEND.equals(intent.getAction()) && intent.getType() != null) {
            Uri uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            if (uri != null) {
                saveTasksByFile(uri);
            }
        }
    }

    public void saveTasksByFile(Uri uri) {
        try (InputStream inputStream = getContentResolver().openInputStream(uri)) {
            byte[] bytes = new byte[inputStream.available()];
            int read = inputStream.read(bytes);
            if (read > 0)
                saveTasks(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveTasks(byte[] bytes) {
        if (bytes == null || bytes.length == 0) return;

        ArrayList<Task> tasks = GsonUtils.getAsType(new String(bytes), new TypeToken<ArrayList<Task>>() {}.getType(), new ArrayList<>());
        if (tasks != null) {
            for (Task task : tasks) {
                if (TaskRepository.getInstance().getTaskById(task.getId()) != null) {
                    task.setId(null);
                }
                task.save();
            }
        }
    }
}