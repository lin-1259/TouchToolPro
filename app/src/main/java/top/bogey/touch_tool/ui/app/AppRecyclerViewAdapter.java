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
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.databinding.ViewAppItemBinding;
import top.bogey.touch_tool.utils.ResultCallback;

public class AppRecyclerViewAdapter extends RecyclerView.Adapter<AppRecyclerViewAdapter.ViewHolder> {
    private final Map<String, ArrayList<String>> selectedActivities;
    private final ResultCallback callback;

    private final ArrayList<PackageInfo> apps = new ArrayList<>();

    private final Map<String, Drawable> icons = new HashMap<>();
    private final boolean single;

    private boolean showMore = true;
    private CharSequence searchText;

    public AppRecyclerViewAdapter(Map<String, ArrayList<String>> selectedActivities, ResultCallback callback, boolean single) {
        this.selectedActivities = selectedActivities;
        this.callback = callback;
        this.single = single;
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

    public void refreshApps(ArrayList<PackageInfo> newApps, CharSequence searchText) {
        this.searchText = searchText;
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

    public void setShowMore(boolean showMore) {
        this.showMore = showMore;
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        private final ViewAppItemBinding binding;
        private final Context context;

        private final String commonPkgName;
        private final ArrayList<String> activities = new ArrayList<>();
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
                        String[] keys = new String[selectedActivities.size()];
                        selectedActivities.keySet().toArray(keys);
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

            binding.selectAppButton.setOnClickListener(v -> {
                filterActivities(null);
                showSelectDialog();
            });

            binding.searchActivityButton.setOnClickListener(v -> {
                filterActivities(searchText);
                showSelectDialog();
            });
        }

        private void showSelectDialog() {
            ArrayList<String> strings = selectedActivities.get(info.packageName);
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context).setTitle(R.string.picker_app_title_select_activity).setNegativeButton(R.string.cancel, null);
            if (single) {
                int index = 0;
                if (strings != null) {
                    for (int i = 0; i < activities.size(); i++) {
                        String choice = activities.get(i);
                        if (strings.contains(choice)) {
                            index = i;
                            break;
                        }
                    }
                }
                String[] activitiesArray = new String[activities.size()];
                builder.setSingleChoiceItems(activities.toArray(activitiesArray), index, null).setPositiveButton(R.string.enter, (dialog, which) -> {
                    if (activities.size() == 0) return;
                    int checkedItemPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                    if (checkedItemPosition != AdapterView.INVALID_POSITION) {
                        selectedActivities.clear();
                        selectedActivities.put(info.packageName, new ArrayList<>(Collections.singletonList(activities.get(checkedItemPosition))));
                        notifyItemChanged(getBindingAdapterPosition());
                        callback.onResult(true);
                    }
                });
            } else {
                if (strings == null) strings = new ArrayList<>();
                boolean[] choicesInitial = new boolean[activities.size()];
                for (int i = 0; i < activities.size(); i++) {
                    String choice = activities.get(i);
                    choicesInitial[i] = strings.contains(choice);
                }

                String[] activitiesArray = new String[activities.size()];
                builder.setMultiChoiceItems(activities.toArray(activitiesArray), choicesInitial, null).setPositiveButton(R.string.enter, (dialog, which) -> {
                    SparseBooleanArray checkedItemPositions = ((AlertDialog) dialog).getListView().getCheckedItemPositions();
                    ArrayList<String> result = selectedActivities.get(info.packageName);
                    if (result == null) result = new ArrayList<>();
                    for (int i = 0; i < activities.size(); i++) {
                        if (checkedItemPositions.get(i) && !result.contains(activities.get(i))) {
                            result.add(activities.get(i));
                        }
                    }
                    selectedActivities.put(info.packageName, result);
                    notifyItemChanged(getBindingAdapterPosition());
                    callback.onResult(true);
                });
            }

            builder.show();
        }

        private void filterActivities(CharSequence searchText) {
            activities.clear();
            if (info.activities != null) {
                Pattern pattern = null;
                if (!(searchText == null || searchText.length() == 0)) {
                    pattern = Pattern.compile(searchText.toString().toLowerCase());
                }
                for (ActivityInfo activityInfo : info.activities) {
                    if (!single || activityInfo.exported) {
                        if (pattern != null) {
                            if (pattern.matcher(activityInfo.name.toLowerCase()).find()) {
                                activities.add(activityInfo.name);
                            }
                        } else {
                            activities.add(activityInfo.name);
                        }
                    }
                }
            }
        }

        public void refreshView(PackageInfo packageInfo) {
            info = packageInfo;
            filterActivities(null);

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
            binding.selectAppButton.setVisibility(((!showMore) || isCommon || activities.size() == 0) ? View.GONE : View.VISIBLE);
            binding.searchActivityButton.setVisibility(((!showMore) || isCommon || activities.size() == 0) ? View.GONE : View.VISIBLE);
        }
    }
}
