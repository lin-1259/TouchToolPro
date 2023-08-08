package top.bogey.touch_tool_pro.ui.picker;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.annotation.NonNull;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.task.WorldState;
import top.bogey.touch_tool_pro.databinding.FloatPickerPackagePreviewBinding;
import top.bogey.touch_tool_pro.ui.setting.SettingView;
import top.bogey.touch_tool_pro.utils.easy_float.EasyFloat;

@SuppressLint("ViewConstructor")
public class PackagePickerFloatPreview extends BasePickerFloatView {
    private final FloatPickerPackagePreviewBinding binding;

    public PackagePickerFloatPreview(@NonNull Context context) {
        super(context, null);

        binding = FloatPickerPackagePreviewBinding.inflate(LayoutInflater.from(context), this, true);

        binding.playButton.setOnClickListener(v -> resetInfo());

        binding.copyNameButton.setOnClickListener(v -> copy(binding.nameTitle.getText()));
        binding.copyPackageButton.setOnClickListener(v -> copy(binding.packageTitle.getText()));
        binding.copyActivityButton.setOnClickListener(v -> copy(binding.activityTitle.getText()));

        binding.closeButton.setOnClickListener(v -> {
            dismiss();
            SettingView.resetSwitchState();
        });

        resetInfo();
    }

    private void resetInfo() {
        WorldState worldState = WorldState.getInstance();
        String packageName = worldState.getPackageName();

        for (PackageInfo info : worldState.findPackageList(getContext(), true, packageName, true)) {
            if (info.packageName.equals(packageName)) {
                PackageManager manager = getContext().getPackageManager();

                binding.nameTitle.setText(info.applicationInfo.loadLabel(manager));
                break;
            }
        }

        binding.packageTitle.setText(packageName);
        binding.activityTitle.setText(worldState.getActivityName());
    }

    private void copy(CharSequence text) {
        ClipboardManager manager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(getContext().getString(R.string.app_name), text);
        manager.setPrimaryClip(clipData);
        Toast.makeText(getContext(), R.string.report_running_error_copied, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void show() {
        EasyFloat.with(MainApplication.getInstance().getService())
                .setLayout(this)
                .setTag(tag)
                .setDragEnable(true)
                .setAnimator(null)
                .show();
    }
}
