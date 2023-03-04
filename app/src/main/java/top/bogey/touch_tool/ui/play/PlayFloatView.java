package top.bogey.touch_tool.ui.play;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.start.ManualStartAction;
import top.bogey.touch_tool.databinding.FloatPlayBinding;
import top.bogey.touch_tool.utils.DisplayUtils;
import top.bogey.touch_tool.utils.SettingSave;
import top.bogey.touch_tool.utils.easy_float.EasyFloat;
import top.bogey.touch_tool.utils.easy_float.FloatGravity;
import top.bogey.touch_tool.utils.easy_float.FloatViewInterface;
import top.bogey.touch_tool.utils.easy_float.SidePattern;

@SuppressLint("ViewConstructor")
public class PlayFloatView extends FrameLayout implements FloatViewInterface {
    private final FloatPlayBinding binding;
    private boolean clickFirst = false;

    private LinkedHashMap<ManualStartAction, Task> manualStartActions;
    private final String ALL;
    private final ArrayList<String> tags = new ArrayList<>();
    private String currTag;

    public PlayFloatView(@NonNull Context context) {
        super(context);
        ALL = context.getString(R.string.tag_all);

        binding = FloatPlayBinding.inflate(LayoutInflater.from(context), this, true);
        binding.closeButton.setOnClickListener(v -> {
            if (clickFirst) dismiss();
            else {
                clickFirst = true;
                postDelayed(() -> clickFirst = false, 500);
                refreshExpandState(binding.buttonBox.getVisibility() == GONE);
            }
        });

        binding.buttonBox.setOnHierarchyChangeListener(new OnHierarchyChangeListener() {
            @Override
            public void onChildViewAdded(View parent, View child) {

            }

            @Override
            public void onChildViewRemoved(View parent, View child) {
                checkShow();
            }
        });

        binding.nextButton.setOnClickListener(v -> showActions(getActionsByNextTag()));

        refreshExpandState(SettingSave.getInstance().isPlayViewExpand());
    }

    private void refreshExpandState(boolean expand) {
        ViewGroup.LayoutParams params = binding.closeButton.getLayoutParams();
        if (!expand) {
            binding.buttonBox.setVisibility(GONE);
            binding.nextButton.setVisibility(GONE);
            binding.closeButton.setIconResource(R.drawable.icon_down);
            params.height = DisplayUtils.dp2px(getContext(), 32);
        } else {
            binding.buttonBox.setVisibility(VISIBLE);
            binding.nextButton.setVisibility(tags.size() > 1 ? VISIBLE : GONE);
            binding.closeButton.setIconResource(R.drawable.icon_up);
            params.height = DisplayUtils.dp2px(getContext(), 24);
        }
        binding.closeButton.setLayoutParams(params);
        binding.getRoot().setAlpha(expand ? 1 : 0.4f);
        SettingSave.getInstance().setPlayViewExpand(expand);
    }

    private void calculateTags(Collection<Task> tasks) {
        tags.clear();
        if (tasks != null) {
            for (Task task : tasks) {
                String tag = ALL;
                if (task.getTag() != null) tag = task.getTag();
                if (!tags.contains(tag)) tags.add(tag);
            }
        }
        binding.nextButton.setVisibility(tags.size() > 1 ? VISIBLE : GONE);
    }

    private LinkedHashMap<ManualStartAction, Task> getActionsByNextTag() {
        int index = tags.indexOf(currTag);
        currTag = tags.get((index + 1) % tags.size());

        LinkedHashMap<ManualStartAction, Task> actions = new LinkedHashMap<>();
        for (Map.Entry<ManualStartAction, Task> entry : manualStartActions.entrySet()) {
            String tag = ALL;
            if (entry.getValue().getTag() != null) tag = entry.getValue().getTag();
            if (tag.equals(currTag)) actions.put(entry.getKey(), entry.getValue());
        }
        return actions;
    }

    @Override
    public void show() {
        EasyFloat.with(MainApplication.getInstance().getService())
                .setLayout(this)
                .setSidePattern(SidePattern.HORIZONTAL)
                .setGravity(FloatGravity.RIGHT_CENTER, 0, 0)
                .setBorder(20, 20, 0, 0)
                .setTag(PlayFloatView.class.getCanonicalName())
                .setAlwaysShow(true)
                .show();
    }

    @Override
    public void dismiss() {
        EasyFloat.dismiss(PlayFloatView.class.getCanonicalName());
    }

    public void setNeedRemove(boolean needRemove) {
        post(() -> {
            for (int i = binding.buttonBox.getChildCount() - 1; i >= 0; i--) {
                PlayFloatViewItem view = (PlayFloatViewItem) binding.buttonBox.getChildAt(i);
                if (view.isFree() && needRemove) {
                    binding.buttonBox.removeView(view);
                } else {
                    view.setNeedRemove(needRemove);
                }
            }
        });
    }

    public void onNewActions() {
        manualStartActions = WorldState.getInstance().getManualStartActions();
        if (manualStartActions.size() > 4) {
            calculateTags(manualStartActions.values());
            showActions(getActionsByNextTag());
        } else {
            calculateTags(null);
            showActions(manualStartActions);
        }
    }

    public void showActions(LinkedHashMap<ManualStartAction, Task> actions) {
        HashSet<ManualStartAction> alreadyManualStartActions = new HashSet<>();

        for (int i = binding.buttonBox.getChildCount() - 1; i >= 0; i--) {
            PlayFloatViewItem view = (PlayFloatViewItem) binding.buttonBox.getChildAt(i);
            if (actions.containsKey(view.getStartAction())) {
                // 已经在显示了，不移除
                view.setNeedRemove(false);
                alreadyManualStartActions.add(view.getStartAction());
            } else {
                // 没在新列表里的，需要移除
                if (view.isFree()) binding.buttonBox.removeView(view);
                else view.setNeedRemove(true);
            }
        }

        actions.forEach((startAction, task) -> {
            if (!alreadyManualStartActions.contains(startAction)) {
                binding.buttonBox.addView(new PlayFloatViewItem(getContext(), task, startAction));
            }
        });
    }

    public void checkShow() {
        postDelayed(() -> {
            if (binding.buttonBox.getChildCount() == 0) dismiss();
        }, 200);
    }
}
