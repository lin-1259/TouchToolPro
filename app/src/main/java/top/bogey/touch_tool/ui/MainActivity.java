package top.bogey.touch_tool.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.TaskRepository;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.databinding.ActivityMainBinding;
import top.bogey.touch_tool.utils.AppUtils;
import top.bogey.touch_tool.utils.GsonUtils;
import top.bogey.touch_tool.utils.SettingSave;

public class MainActivity extends BaseActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolBar);

        handleIntent(getIntent());
        runFirstTimes();
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

    @Override
    protected void onStart() {
        super.onStart();
        NavController controller = Navigation.findNavController(this, R.id.conView);
        NavigationUI.setupWithNavController(binding.menuView, controller);
        AppBarConfiguration configuration = new AppBarConfiguration.Builder(R.id.home, R.id.task, R.id.setting).build();
        NavigationUI.setupActionBarWithNavController(this, controller, configuration);
        controller.addOnDestinationChangedListener((navController, navDestination, bundle) -> {
            int id = navDestination.getId();
            if (id == R.id.home || id == R.id.task || id == R.id.setting) {
                showBottomNavigation();
            } else {
                hideBottomNavigation();
            }
        });

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

    private void copyError(String error) {
        ClipboardManager manager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(getString(R.string.app_name), error);
        manager.setPrimaryClip(clipData);
        Toast.makeText(this, R.string.report_running_error_copied, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        WorldState.getInstance().resetAppMap(this);
    }

    public void showBottomNavigation() {
        binding.menuView.setVisibility(View.VISIBLE);
    }

    public void hideBottomNavigation() {
        binding.menuView.setVisibility(View.GONE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController controller = Navigation.findNavController(this, R.id.conView);
        return controller.navigateUp() || super.onSupportNavigateUp();
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
            setIntent(null);
        }
    }

    public void saveTasksByFile(Uri uri) {
        try (InputStream inputStream = getContentResolver().openInputStream(uri)) {
            if (inputStream == null) return;
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