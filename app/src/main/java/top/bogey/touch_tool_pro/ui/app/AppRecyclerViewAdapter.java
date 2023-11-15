package top.bogey.touch_tool_pro.ui.app;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.databinding.ViewAppItemBinding;
import top.bogey.touch_tool_pro.utils.BooleanResultCallback;

public class AppRecyclerViewAdapter extends RecyclerView.Adapter<AppRecyclerViewAdapter.ViewHolder> {
    private final HashMap<String, ArrayList<String>> selectedActivities;
    private final BooleanResultCallback callback;

    private final ArrayList<PackageInfo> apps = new ArrayList<>();

    private final boolean single;
    private final boolean all;
    private final boolean share;

    private final boolean showActivity;

    public AppRecyclerViewAdapter(HashMap<String, ArrayList<String>> selectedActivities, BooleanResultCallback callback, boolean single, boolean all, boolean share, boolean showActivity) {
        this.selectedActivities = selectedActivities;
        this.callback = callback;
        this.single = single;
        this.all = all;
        this.share = share;
        this.showActivity = showActivity;
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

    public void refreshApps(ArrayList<PackageInfo> newApps) {
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
                if (i > apps.size()) {
                    apps.add(newApp);
                } else {
                    apps.add(i, newApp);
                }
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
                ArrayList<String> remove = selectedActivities.remove(info.packageName);
                if (remove == null || remove.size() > 0) {
                    if (single) {
                        ArrayList<String> keys = new ArrayList<>(selectedActivities.keySet());
                        selectedActivities.clear();
                        for (String packageName : keys) {
                            for (int i = 0; i < apps.size(); i++) {
                                PackageInfo info = apps.get(i);
                                if (packageName.equals(info.packageName)) {
                                    notifyItemChanged(i);
                                    break;
                                }
                            }
                        }
                    }
                    selectedActivities.put(info.packageName, new ArrayList<>());
                }

                if (!single && info.packageName.equals(commonPkgName)) {
                    for (String packageName : selectedActivities.keySet()) {
                        for (int i = 0; i < apps.size(); i++) {
                            PackageInfo info = apps.get(i);
                            if (packageName.equals(info.packageName)) {
                                notifyItemChanged(i);
                                break;
                            }
                        }
                    }
                }

                notifyItemChanged(getBindingAdapterPosition());
                callback.onResult(true);
            });

            binding.selectAppButton.setOnClickListener(v -> showSelectDialog());
        }

        private void showSelectDialog() {
            ArrayList<String> activityNameList = new ArrayList<>();
            ArrayList<String> list = selectedActivities.get(info.packageName);
            if (list != null) activityNameList.addAll(list);
            SelectActivityDialog view = new SelectActivityDialog(context, getAllActivities(), single, activityNameList);
            new MaterialAlertDialogBuilder(context)
                    .setTitle(R.string.picker_app_title_select_activity)
                    .setNegativeButton(R.string.cancel, null)
                    .setView(view)
                    .setPositiveButton(R.string.enter, (dialog, which) -> {
                        if (single) selectedActivities.clear();
                        selectedActivities.put(info.packageName, activityNameList);
                        if (single) notifyDataSetChanged();
                        else notifyItemChanged(getBindingAdapterPosition());
                    })
                    .show();
        }

        private HashMap<String, String> getAllActivities() {
            HashMap<String, String> allActivities = new HashMap<>();
            if (share) {
                PackageManager manager = context.getPackageManager();
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("*/*");
                intent.setPackage(info.packageName);
                List<ResolveInfo> infoList = manager.queryIntentActivities(intent, PackageManager.MATCH_ALL);
                for (ResolveInfo resolveInfo : infoList) {
                    CharSequence label = resolveInfo.loadLabel(manager);
                    allActivities.put(resolveInfo.activityInfo.name, label.toString());
                }
            } else {
                if (info.activities != null) {
                    for (ActivityInfo activityInfo : info.activities) {
                        if (all || activityInfo.exported) {
                            allActivities.put(activityInfo.name, activityInfo.name);
                        }
                    }
                }
            }
            return allActivities;
        }

        public void refreshView(PackageInfo packageInfo) {
            Log.d("TAG", "refreshView: " + getAbsoluteAdapterPosition());
            info = packageInfo;

            PackageManager manager = context.getPackageManager();
            binding.pkgName.setText(packageInfo.packageName);
            if (packageInfo.packageName.equals(context.getString(R.string.common_package_name))) {
                binding.appName.setText(context.getString(R.string.common_name));
            } else {
                binding.appName.setText(packageInfo.applicationInfo.loadLabel(manager));
            }

            if (packageInfo.packageName.equals(context.getString(R.string.common_package_name))) {
                binding.icon.setImageDrawable(context.getApplicationInfo().loadIcon(manager));
            } else {
                binding.icon.setImageDrawable(packageInfo.applicationInfo.loadIcon(manager));
            }

            boolean containsKey = selectedActivities.containsKey(commonPkgName);
            boolean isCommon = packageInfo.packageName.equals(commonPkgName);

            binding.getRoot().setCheckedIconResource(isCommon || !containsKey ? R.drawable.icon_radio_selected : R.drawable.icon_radio_unselected);
            binding.getRoot().setChecked(selectedActivities.containsKey(packageInfo.packageName));

            ArrayList<String> list = selectedActivities.get(packageInfo.packageName);
            if (list == null || list.size() == 0) {
                binding.selectAppButton.setText(null);
                binding.selectAppButton.setIconResource(R.drawable.icon_more);
            } else {
                binding.selectAppButton.setText(String.valueOf(list.size()));
                binding.selectAppButton.setIcon(null);
            }
            binding.selectAppButton.setVisibility(((!showActivity) || isCommon) ? View.GONE : View.VISIBLE);
        }
    }
}
