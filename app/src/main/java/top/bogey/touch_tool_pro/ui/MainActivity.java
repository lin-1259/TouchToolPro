package top.bogey.touch_tool_pro.ui;

import static top.bogey.touch_tool_pro.ui.InstantActivity.ACTION_ID;
import static top.bogey.touch_tool_pro.ui.InstantActivity.TASK_ID;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.start.OuterStartAction;
import top.bogey.touch_tool_pro.bean.base.SaveRepository;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.task.Task;
import top.bogey.touch_tool_pro.bean.task.WorldState;
import top.bogey.touch_tool_pro.databinding.ActivityMainBinding;
import top.bogey.touch_tool_pro.ui.setting.HandleFunctionContextView;
import top.bogey.touch_tool_pro.utils.AppUtils;
import top.bogey.touch_tool_pro.utils.GsonUtils;
import top.bogey.touch_tool_pro.utils.SettingSave;

public class MainActivity extends BaseActivity {
    private ActivityMainBinding binding;
    private boolean firstShowTask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApplication.getInstance().setMainActivity(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolBar);

        runFirstTimes();
    }

    @Override
    protected void onStart() {
        super.onStart();

        NavController controller = Navigation.findNavController(this, R.id.conView);
        NavigationUI.setupWithNavController(binding.menuView, controller);
        AppBarConfiguration configuration = new AppBarConfiguration.Builder(R.id.home, R.id.task, R.id.function, R.id.setting).build();
        NavigationUI.setupActionBarWithNavController(this, controller, configuration);
        controller.addOnDestinationChangedListener((navController, navDestination, bundle) -> {
            int id = navDestination.getId();
            if (id == R.id.home || id == R.id.task || id == R.id.function || id == R.id.setting) {
                showBottomNavigation();
            } else {
                hideBottomNavigation();
            }
        });

        if (firstShowTask && SettingSave.getInstance().isFirstShowTask()) {
            controller.getGraph().setStartDestination(R.id.task);
            controller.navigate(R.id.task);
            firstShowTask = false;
        }

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

        SettingSave.getInstance().init(this);
        WorldState.getInstance().resetAppMap(this);
        handleIntent(getIntent());
        sendShortcuts();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainApplication.getInstance().setMainActivity(null);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController controller = Navigation.findNavController(this, R.id.conView);
        return controller.navigateUp() || super.onSupportNavigateUp();
    }

    private void runFirstTimes() {
        if (SettingSave.getInstance().getRunTimes() == 1) {
            SettingSave.getInstance().addRunTimes();
            try (InputStream inputStream = getAssets().open("default")) {
                byte[] bytes = new byte[inputStream.available()];
                if (inputStream.read(bytes) > 0) {
                    ArrayList<FunctionContext> functionContexts = GsonUtils.getAsObject(new String(bytes), new TypeToken<ArrayList<FunctionContext>>() {
                    }.getType(), new ArrayList<>());
                    functionContexts.forEach(FunctionContext::save);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void showBottomNavigation() {
        binding.menuView.setVisibility(View.VISIBLE);
    }

    public void hideBottomNavigation() {
        binding.menuView.setVisibility(View.GONE);
    }

    private void copyError(String error) {
        ClipboardManager manager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(getString(R.string.app_name), error);
        manager.setPrimaryClip(clipData);
        Toast.makeText(this, R.string.report_running_error_copied, Toast.LENGTH_SHORT).show();
    }

    public void handleIntent(Intent intent) {
        if (intent == null) return;

        Uri uri = null;
        if (Intent.ACTION_SEND.equals(intent.getAction())) {
            uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            uri = intent.getData();
        }
        if (uri != null) {
            saveTasks(uri);
        }

        setIntent(null);
    }

    public void saveTasks(Uri uri) {
        ArrayList<FunctionContext> functionContexts = AppUtils.importFunctionContexts(this, uri);
        HandleFunctionContextView view = new HandleFunctionContextView(this, functionContexts);
        if (view.getShowActionContext().size() == 0) return;

        new MaterialAlertDialogBuilder(this)
                .setPositiveButton(R.string.enter, (dialog, which) -> {
                    view.getSelectActionContext().forEach(FunctionContext::save);
                    dialog.dismiss();
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .setView(view)
                .setTitle(R.string.task_setting_import)
                .show();
    }

    public void sendShortcuts() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N_MR1) {
            ArrayList<ShortcutInfo> shortcuts = new ArrayList<>();
            ArrayList<Task> tasks = SaveRepository.getInstance().getTasksByTag(SaveRepository.SHORTCUT_TAG);
            int count = 0;
            for (Task task : tasks) {
                Intent intent = new Intent(this, InstantActivity.class);
                intent.setAction(Intent.ACTION_VIEW);

                intent.putExtra(InstantActivity.INTENT_KEY_DO_ACTION, true);

                intent.putExtra(TASK_ID, task.getId());
                ArrayList<Action> actions = task.getActionsByClass(OuterStartAction.class);

                if (actions.isEmpty()) continue;
                Action action = actions.get(0);
                intent.putExtra(ACTION_ID, action.getId());

                ShortcutInfo shortcut = new ShortcutInfo.Builder(this, task.getId())
                        .setShortLabel(task.getTitle())
                        .setIcon(Icon.createWithResource(this, R.drawable.icon_shortcut))
                        .setIntent(intent)
                        .build();

                shortcuts.add(shortcut);
                count++;
                if (count >= 4) break;
            }

            ShortcutManager manager = (ShortcutManager) getSystemService(Context.SHORTCUT_SERVICE);
            if (shortcuts.isEmpty()) {
                manager.removeAllDynamicShortcuts();
            } else {
                manager.setDynamicShortcuts(shortcuts);
            }
        }
    }
}
