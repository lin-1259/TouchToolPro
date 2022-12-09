package top.bogey.touch_tool.ui.custom;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskHelper;
import top.bogey.touch_tool.databinding.WidgetAppSelectBinding;
import top.bogey.touch_tool.databinding.WidgetAppSelectItemBinding;
import top.bogey.touch_tool.ui.app.AppView;

public class SelectAppWidget extends BindingView<WidgetAppSelectBinding> {
    private Map<CharSequence, List<CharSequence>> packages;
    private int mode;

    public SelectAppWidget(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, WidgetAppSelectBinding.class);

        binding.selectAppButton.setOnClickListener(v -> new AppView(packages, mode, result -> refreshPackages()).show(MainApplication.getActivity().getSupportFragmentManager(), null));
    }

    public void setPackages(Map<CharSequence, List<CharSequence>> packages, int mode) {
        this.packages = packages;
        this.mode = mode;
        refreshPackages();
    }

    private void refreshPackages() {
        binding.includeAppsIconBox.removeAllViews();
        binding.excludeAppsIconBox.removeAllViews();
        binding.excludeAppsBox.setVisibility(GONE);

        Set<CharSequence> packageNames = packages.keySet();
        if (packageNames.isEmpty()) return;

        PackageManager manager = getContext().getPackageManager();
        String commonPackage = getContext().getString(R.string.common_package_name);
        boolean includeCommon = packageNames.contains(commonPackage);

        LinearLayout appIconBox = binding.includeAppsIconBox;
        if (includeCommon) {
            Drawable drawable = getContext().getApplicationInfo().loadIcon(manager);
            WidgetAppSelectItemBinding itemBinding = WidgetAppSelectItemBinding.inflate(LayoutInflater.from(getContext()), appIconBox, true);
            itemBinding.icon.setImageDrawable(drawable);

            if (packageNames.size() == 1) return;
            packageNames.remove(commonPackage);
            appIconBox = binding.excludeAppsIconBox;
            binding.excludeAppsBox.setVisibility(VISIBLE);
        }


        int index = 0;
        for (CharSequence packageName : packageNames) {
            WidgetAppSelectItemBinding itemBinding = WidgetAppSelectItemBinding.inflate(LayoutInflater.from(getContext()), appIconBox, true);
            if (index == 4) {
                itemBinding.icon.setImageResource(R.drawable.icon_more);
                ((View) itemBinding.numberText.getParent()).setVisibility(GONE);
                break;

            } else {
                List<CharSequence> list = packages.get(packageName);
                if (list == null) continue;
                PackageInfo packageInfo = TaskHelper.getInstance().getPackage(packageName);
                itemBinding.icon.setImageDrawable(packageInfo.applicationInfo.loadIcon(manager));
                itemBinding.numberText.setText(String.valueOf(list.size()));
            }
            index++;
        }
    }
}
