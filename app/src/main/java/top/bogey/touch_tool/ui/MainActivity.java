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
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.databinding.ActivityMainBinding;
import top.bogey.touch_tool.ui.setting.HandleActionContextView;
import top.bogey.touch_tool.utils.AppUtils;
import top.bogey.touch_tool.utils.GsonUtils;
import top.bogey.touch_tool.utils.SettingSave;

public class MainActivity extends BaseActivity {
    private ActivityMainBinding binding;
    private boolean firstShowTask = true;

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
                if (inputStream.read(bytes) > 0) {
                    ArrayList<ActionContext> actionContexts = GsonUtils.getAsType(new String(bytes), new TypeToken<ArrayList<ActionContext>>() {}.getType(), new ArrayList<>());
                    actionContexts.forEach(ActionContext::save);
                }
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
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    public void handleIntent(Intent intent) {
        if (intent == null) return;

        if (intent.getType() != null) {
            Uri uri = null;
            if (Intent.ACTION_SEND.equals(intent.getAction())) {
                uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
                uri = intent.getData();
            }
            if (uri != null) {
                saveTasks(uri);
            }
        }
        setIntent(null);
    }

    public void saveTasks(Uri uri) {
        ArrayList<ActionContext> actionContexts = AppUtils.importActionContexts(this, uri);
        HandleActionContextView view = new HandleActionContextView(this, actionContexts);
        if (view.getShowActionContext().size() == 0) return;

        new MaterialAlertDialogBuilder(this)
                .setPositiveButton(R.string.enter, (dialog, which) -> {
                    view.getSelectActionContext().forEach(ActionContext::save);
                    dialog.dismiss();
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .setView(view)
                .setTitle(R.string.task_setting_import)
                .show();
    }

}