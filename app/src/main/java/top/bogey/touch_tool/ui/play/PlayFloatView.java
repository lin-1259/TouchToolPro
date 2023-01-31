package top.bogey.touch_tool.ui.play;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import java.util.HashSet;
import java.util.LinkedHashMap;

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

    public PlayFloatView(@NonNull Context context) {
        super(context);
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

        refreshExpandState(SettingSave.getInstance().isPlayViewExpand());
    }

    private void refreshExpandState(boolean expand) {
        ViewGroup.LayoutParams params = binding.closeButton.getLayoutParams();
        if (!expand) {
            binding.buttonBox.setVisibility(GONE);
            binding.closeButton.setIconResource(R.drawable.icon_down);
            params.height = DisplayUtils.dp2px(getContext(), 32);
        } else {
            binding.buttonBox.setVisibility(VISIBLE);
            binding.closeButton.setIconResource(R.drawable.icon_up);
            params.height = DisplayUtils.dp2px(getContext(), 24);
        }
        binding.closeButton.setLayoutParams(params);
        SettingSave.getInstance().setPlayViewExpand(expand);
    }

    @Override
    public void show() {
        EasyFloat.with(MainApplication.getService())
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
        LinkedHashMap<ManualStartAction, Task> manualStartActions = WorldState.getInstance().getManualStartActions();
        HashSet<ManualStartAction> alreadyManualStartActions = new HashSet<>();

        for (int i = binding.buttonBox.getChildCount() - 1; i >= 0; i--) {
            PlayFloatViewItem view = (PlayFloatViewItem) binding.buttonBox.getChildAt(i);
            if (manualStartActions.containsKey(view.getStartAction())) {
                // 已经在显示了，不移除
                view.setNeedRemove(false);
                alreadyManualStartActions.add(view.getStartAction());
            } else {
                // 没在新列表里的，需要移除
                if (view.isFree()) binding.buttonBox.removeView(view);
                else view.setNeedRemove(true);
            }
        }

        manualStartActions.forEach((startAction, task) -> {
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
