package top.bogey.touch_tool.ui.setting;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.databinding.ViewSettingBinding;
import top.bogey.touch_tool.service.MainAccessibilityService;
import top.bogey.touch_tool.ui.BaseActivity;
import top.bogey.touch_tool.ui.MainActivity;
import top.bogey.touch_tool.ui.picker.PackagePickerFloatPreview;
import top.bogey.touch_tool.utils.AppUtils;
import top.bogey.touch_tool.utils.SettingSave;
import top.bogey.touch_tool.utils.easy_float.EasyFloat;

public class SettingView extends Fragment {
    private ViewSettingBinding binding;

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        resetSwitchState();
    }

    public static void resetSwitchState() {
        BaseActivity activity = MainApplication.getInstance().getActivity();
        if (activity instanceof MainActivity) {
            Fragment fragment = ((MainActivity) activity).getCurrFragment();
            if (fragment instanceof SettingView) {
                ((SettingView) fragment).refreshSwitchState();
            }
        }
    }

    public void refreshSwitchState() {
        binding.playViewVisibleSwitch.setChecked(SettingSave.getInstance().isPlayViewVisible());
        binding.showPackageInfoSwitch.setChecked(EasyFloat.getView(PackagePickerFloatPreview.class.getName()) != null);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ViewSettingBinding.inflate(inflater, container, false);

        binding.playViewVisibleSwitch.setOnClickListener(v -> SettingSave.getInstance().setPlayViewVisible(binding.playViewVisibleSwitch.isChecked()));
        binding.showPackageInfoSwitch.setOnClickListener(v -> {
            MainAccessibilityService service = MainApplication.getInstance().getService();
            if (service == null || !service.isServiceEnabled()) {
                binding.showPackageInfoSwitch.setChecked(false);
                Toast.makeText(getContext(), R.string.accessibility_service_off_tips, Toast.LENGTH_SHORT).show();
                return;
            }
            View view = EasyFloat.getView(PackagePickerFloatPreview.class.getName());
            if (view == null) {
                new PackagePickerFloatPreview(requireContext()).show();
            } else {
                EasyFloat.dismiss(PackagePickerFloatPreview.class.getName());
            }
        });
        refreshSwitchState();

        binding.hideBackgroundSwitch.setOnClickListener(v -> SettingSave.getInstance().setHideBackground(requireContext(), binding.hideBackgroundSwitch.isChecked()));
        binding.hideBackgroundSwitch.setChecked(SettingSave.getInstance().isHideBackground());

        binding.keepAliveSwitch.setOnClickListener(v -> SettingSave.getInstance().setKeepAlive(requireContext(), binding.keepAliveSwitch.isChecked()));
        binding.keepAliveSwitch.setChecked(SettingSave.getInstance().isKeepAlive());

        binding.taskBackupButton.setOnClickListener(v -> {
            HandleActionContextView view = new HandleActionContextView(requireContext());
            new MaterialAlertDialogBuilder(requireContext())
                    .setPositiveButton(R.string.enter, (dialog, which) -> {
                        ArrayList<ActionContext> actionContexts = view.getSelectActionContext();
                        AppUtils.backupActionContexts(requireContext(), actionContexts);
                        dialog.dismiss();
                    })
                    .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                    .setView(view)
                    .setTitle(R.string.task_setting_backup)
                    .show();
        });

        binding.taskImportButton.setOnClickListener(v -> {
            MainActivity mainActivity = (MainActivity) requireActivity();
            mainActivity.launcherContent((code, intent) -> {
                if (code == Activity.RESULT_OK) {
                    Uri uri = intent.getData();
                    if (uri != null) {
                        mainActivity.saveTasks(uri);
                    }
                }
            });
        });

        binding.nightModeGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                View view = group.findViewById(checkedId);
                SettingSave.getInstance().setNightMode(group.indexOfChild(view));
            }
        });
        binding.nightModeGroup.check(binding.nightModeGroup.getChildAt(SettingSave.getInstance().getNightMode()).getId());

        binding.dynamicColorSwitch.setOnClickListener(v -> SettingSave.getInstance().setDynamicColor(requireContext(), binding.dynamicColorSwitch.isChecked()));
        binding.dynamicColorSwitch.setChecked(SettingSave.getInstance().isDynamicColor());

        binding.showTaskSwitch.setOnClickListener(v -> SettingSave.getInstance().setFirstShowTask(binding.showTaskSwitch.isChecked()));
        binding.showTaskSwitch.setChecked(SettingSave.getInstance().isFirstShowTask());

        PackageManager manager = requireContext().getPackageManager();
        try {
            PackageInfo packageInfo = manager.getPackageInfo(requireContext().getPackageName(), 0);
            binding.versionText.setText(requireContext().getString(R.string.app_info_setting_version_format, packageInfo.versionName, packageInfo.versionCode));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        binding.sourceCodeButton.setOnClickListener(v -> AppUtils.gotoUrl(getContext(), "https://github.com/mr-bogey/TouchToolPro"));

        return binding.getRoot();
    }
}
