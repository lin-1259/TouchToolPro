package top.bogey.touch_tool_pro.ui.app;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.radiobutton.MaterialRadioButton;

import java.util.ArrayList;

public class SelectActivityRecyclerViewAdapter extends RecyclerView.Adapter<SelectActivityRecyclerViewAdapter.ViewHolder> {
    private final ArrayList<String> activityNames = new ArrayList<>();
    private final ArrayList<String> selectedActivityNames;
    private final boolean single;

    public SelectActivityRecyclerViewAdapter(boolean single, ArrayList<String> selectedActivityNames) {
        this.single = single;
        this.selectedActivityNames = selectedActivityNames;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (single) {
            return new ViewHolder(new MaterialRadioButton(parent.getContext()));
        } else {
            return new ViewHolder(new MaterialCheckBox(parent.getContext()));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String activityName = activityNames.get(position);
        holder.refreshView(activityName);
    }

    @Override
    public int getItemCount() {
        return activityNames.size();
    }

    public void refreshActivityNames(ArrayList<String> newActivityNames) {
        if (newActivityNames != null) {
            for (int i = selectedActivityNames.size() - 1; i >= 0; i--) {
                String activityName = selectedActivityNames.get(i);
                if (!newActivityNames.contains(activityName)) {
                    newActivityNames.add(0, activityName);
                }
            }
        }

        if (newActivityNames == null || newActivityNames.size() == 0) {
            int size = activityNames.size();
            activityNames.clear();
            notifyItemRangeRemoved(0, size);
            return;
        }

        for (int i = activityNames.size() - 1; i >= 0; i--) {
            String info = activityNames.get(i);
            if (newActivityNames.contains(info)) continue;
            activityNames.remove(i);
            notifyItemRemoved(i);
        }

        for (int i = 0; i < newActivityNames.size(); i++) {
            String newActivity = newActivityNames.get(i);
            if (activityNames.contains(newActivity)) continue;
            activityNames.add(newActivity);
            notifyItemInserted(activityNames.size() - 1);
        }
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        private final Context context;
        private MaterialRadioButton radioButton;
        private MaterialCheckBox checkBox;

        public ViewHolder(MaterialRadioButton radioButton) {
            super(radioButton);
            this.radioButton = radioButton;
            context = radioButton.getContext();

            radioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    int index = getBindingAdapterPosition();
                    String activityName = activityNames.get(index);
                    String lastActivityName = null;
                    if (selectedActivityNames.size() > 0) lastActivityName = selectedActivityNames.get(0);
                    if (activityName.equals(lastActivityName)) return;

                    if (lastActivityName != null) {
                        int i = activityNames.indexOf(lastActivityName);
                        if (i >= 0) notifyItemChanged(i);
                    }
                    selectedActivityNames.clear();
                    selectedActivityNames.add(activityName);
                    notifyItemChanged(index);
                }
            });
        }

        public ViewHolder(MaterialCheckBox checkBox) {
            super(checkBox);
            this.checkBox = checkBox;
            context = checkBox.getContext();

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int index = getBindingAdapterPosition();
                String activityName = activityNames.get(index);
                if (isChecked) {
                    if (selectedActivityNames.contains(activityName)) return;
                    selectedActivityNames.add(activityName);
                    notifyItemChanged(index);
                } else {
                    if (selectedActivityNames.remove(activityName)) notifyItemChanged(index);
                }
            });
        }

        public void refreshView(String activityName) {
            if (radioButton != null) {
                radioButton.setText(activityName);
                radioButton.setChecked(selectedActivityNames.contains(activityName));
            }
            if (checkBox != null) {
                checkBox.setText(activityName);
                checkBox.setChecked(selectedActivityNames.contains(activityName));
            }
        }
    }
}
