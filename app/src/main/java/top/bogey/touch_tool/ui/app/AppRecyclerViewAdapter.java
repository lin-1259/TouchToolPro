package top.bogey.touch_tool.ui.app;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.databinding.ViewAppItemBinding;
import top.bogey.touch_tool.utils.ResultCallback;

public class AppRecyclerViewAdapter extends RecyclerView.Adapter<AppRecyclerViewAdapter.ViewHolder> {
    private final Map<CharSequence, List<CharSequence>> selectedActivitys;
    private final ResultCallback callback;

    private final List<PackageInfo> apps = new ArrayList<>();

    private final Map<CharSequence, Drawable> icons = new HashMap<>();

    public AppRecyclerViewAdapter(Map<CharSequence, List<CharSequence>> selectedActivitys, ResultCallback callback) {
        this.selectedActivitys = selectedActivitys;
        this.callback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ViewAppItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PackageInfo appInfo = apps.get(position);
        holder.refreshView(appInfo);
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }

    public void refreshApps(List<PackageInfo> newApps) {
        if (newApps == null || newApps.size() == 0) {
            int size = apps.size();
            apps.clear();
            notifyItemRangeRemoved(0, size);
            return;
        }

        for (int i = apps.size() - 1; i >= 0; i--) {
            PackageInfo info = apps.get(i);
            boolean flag = true;
            for (PackageInfo newApp : newApps) {
                if (info.packageName.equals(newApp.packageName)) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                apps.remove(i);
                notifyItemRemoved(i);
            }
        }

        for (int i = 0; i < newApps.size(); i++) {
            PackageInfo newApp = newApps.get(i);
            boolean flag = true;
            for (PackageInfo info : apps) {
                if (info.packageName.equals(newApp.packageName)) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                apps.add(i, newApp);
                notifyItemInserted(i);
            }
        }
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        private final ViewAppItemBinding binding;
        private final Context context;

        private final String commonPkgName;
        private PackageInfo info;

        public ViewHolder(ViewAppItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            context = binding.getRoot().getContext();
            commonPkgName = context.getString(R.string.common_package_name);

            binding.getRoot().setOnClickListener(v -> {
                if (selectedActivitys.remove(info.packageName) == null)
                    selectedActivitys.put(info.packageName, new ArrayList<>());

                if (info.packageName.equals(commonPkgName)) {
                    for (CharSequence packageName : selectedActivitys.keySet()) {
                        for (int i = 0; i < apps.size(); i++) {
                            PackageInfo info = apps.get(i);
                            if (packageName.equals(info.packageName)) {
                                notifyItemChanged(i);
                                break;
                            }
                        }
                    }
                }
                notifyItemChanged(getAdapterPosition());
                callback.onResult(true);
            });

            binding.selectAppButton.setOnClickListener(v -> {
                CharSequence[] choices = new CharSequence[info.activities.length];
                ActivityInfo[] activities = info.activities;
                for (int i = 0; i < activities.length; i++) {
                    ActivityInfo activityInfo = activities[i];
                    choices[i] = activityInfo.name;
                }
                List<CharSequence> charSequences = selectedActivitys.get(info.packageName);
                if (charSequences == null) charSequences = new ArrayList<>();
                boolean[] choicesInitial = new boolean[choices.length];
                for (int i = 0; i < choices.length; i++) {
                    CharSequence choice = choices[i];
                    choicesInitial[i] = charSequences.contains(choice);
                }

                new MaterialAlertDialogBuilder(context)
                        .setPositiveButton(R.string.enter, (dialog, which) -> {
                            SparseBooleanArray checkedItemPositions = ((AlertDialog) dialog).getListView().getCheckedItemPositions();
                            List<CharSequence> result = new ArrayList<>();
                            for (int i = 0; i < choices.length; i++) {
                                if (checkedItemPositions.get(i)) {
                                    result.add(choices[i]);
                                }
                            }
                            selectedActivitys.put(info.packageName, result);
                            notifyItemChanged(getAdapterPosition());
                            callback.onResult(true);
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .setMultiChoiceItems(choices, choicesInitial, null)
                        .show();
            });
        }

        public void refreshView(PackageInfo packageInfo) {
            info = packageInfo;
            PackageManager manager = context.getPackageManager();
            binding.pkgName.setText(packageInfo.packageName);
            if (packageInfo.packageName.equals(context.getString(R.string.common_package_name))) {
                binding.appName.setText(context.getString(R.string.common_name));
            } else {
                binding.appName.setText(packageInfo.applicationInfo.loadLabel(manager));
            }

            Drawable drawable = icons.get(packageInfo.packageName);
            if (drawable == null) {
                if (packageInfo.packageName.equals(context.getString(R.string.common_package_name))) {
                    drawable = context.getApplicationInfo().loadIcon(manager);
                } else {
                    drawable = packageInfo.applicationInfo.loadIcon(manager);
                }
                icons.put(packageInfo.packageName, drawable);
            }
            binding.icon.setImageDrawable(drawable);

            boolean containsKey = selectedActivitys.containsKey(commonPkgName);
            boolean isCommon = packageInfo.packageName.equals(commonPkgName);

            binding.getRoot().setCheckedIconResource(isCommon || !containsKey ? R.drawable.icon_radio_selected : R.drawable.icon_radio_unselected);
            binding.getRoot().setChecked(selectedActivitys.containsKey(packageInfo.packageName));

            List<CharSequence> list = selectedActivitys.get(packageInfo.packageName);
            if (list == null) {
                binding.selectAppButton.setText(null);
                binding.selectAppButton.setIconResource(R.drawable.icon_more);
            } else {
                binding.selectAppButton.setText(String.valueOf(list.size()));
                binding.selectAppButton.setIcon(null);
            }
            binding.selectAppButton.setVisibility((isCommon || info.activities == null || info.activities.length == 0) ? View.GONE : View.VISIBLE);
        }
    }
}
