package top.bogey.touch_tool_pro.ui.setting;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.base.LogInfo;
import top.bogey.touch_tool_pro.bean.task.WorldState;
import top.bogey.touch_tool_pro.databinding.FloatPackageInfoViewItemBinding;

public class PackageInfoRecyclerViewAdapter extends RecyclerView.Adapter<PackageInfoRecyclerViewAdapter.ViewHolder> {
    private final ArrayList<LogInfo> packageInfo = new ArrayList<>();
    private RecyclerView recyclerView;

    public PackageInfoRecyclerViewAdapter() {

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(FloatPackageInfoViewItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.refreshItem(packageInfo.get(position));
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
        recyclerView.scrollToPosition(getItemCount() - 1);
    }

    @Override
    public int getItemCount() {
        return packageInfo.size();
    }

    public void addPackageInfo(String packageName, String activityName) {
        PackageInfo info = WorldState.getInstance().getPackage(packageName);
        CharSequence appName = info.applicationInfo.loadLabel(recyclerView.getContext().getPackageManager());
        LogInfo logInfo = new LogInfo(packageInfo.size() + 1, appName + ":" + activityName);
        packageInfo.add(logInfo);
        notifyItemInserted(packageInfo.size());
        if (recyclerView != null) recyclerView.scrollToPosition(packageInfo.size() - 1);
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        public final FloatPackageInfoViewItemBinding binding;
        private final Context context;

        public ViewHolder(@NonNull FloatPackageInfoViewItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            context = binding.getRoot().getContext();

            binding.copyButton.setOnClickListener(v -> {
                int index = getBindingAdapterPosition();
                LogInfo info = packageInfo.get(index);
                copy(info.getLog());
            });
        }

        @SuppressLint("DefaultLocale")
        public void refreshItem(LogInfo packageInfo) {
            binding.titleText.setText(packageInfo.getLogString());
        }

        private void copy(String text) {
            String[] split = text.split(":");
            if (split.length == 2) {
                ClipboardManager manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText(context.getString(R.string.app_name), split[1]);
                manager.setPrimaryClip(clipData);
                Toast.makeText(context, R.string.report_running_error_copied, Toast.LENGTH_SHORT).show();
            }
        }
    }
}