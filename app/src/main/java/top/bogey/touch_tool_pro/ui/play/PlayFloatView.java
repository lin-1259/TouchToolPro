package top.bogey.touch_tool_pro.ui.play;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.start.ManualStartAction;
import top.bogey.touch_tool_pro.bean.base.SaveRepository;
import top.bogey.touch_tool_pro.bean.task.Task;
import top.bogey.touch_tool_pro.bean.task.WorldState;
import top.bogey.touch_tool_pro.databinding.FloatPlayBinding;
import top.bogey.touch_tool_pro.ui.picker.FloatBaseCallback;
import top.bogey.touch_tool_pro.utils.DisplayUtils;
import top.bogey.touch_tool_pro.utils.SettingSave;
import top.bogey.touch_tool_pro.utils.easy_float.EasyFloat;
import top.bogey.touch_tool_pro.utils.easy_float.FloatGravity;
import top.bogey.touch_tool_pro.utils.easy_float.FloatViewHelper;
import top.bogey.touch_tool_pro.utils.easy_float.FloatViewInterface;
import top.bogey.touch_tool_pro.utils.easy_float.SidePattern;

@SuppressLint("ViewConstructor")
public class PlayFloatView extends FrameLayout implements FloatViewInterface {
    private static final int SCALE = 2;
    private final FloatPlayBinding binding;
    private final ArrayList<String> tags = new ArrayList<>();
    private final int border;
    private boolean clickFirst = false;
    private LinkedHashMap<ManualStartAction, Task> manualStartActions;
    private String currTag;

    public PlayFloatView(@NonNull Context context) {
        super(context);
        border = Math.round(DisplayUtils.dp2px(context, 8));

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
            binding.closeButton.setIconResource(R.drawable.icon_launcher_mono);
            params.height = Math.round(DisplayUtils.dp2px(getContext(), 32));
            binding.closeButton.setIconSize(params.height);
            setX((SCALE * border) * (isInLeft() ? -1 : 1));
        } else {
            binding.buttonBox.setVisibility(VISIBLE);
            binding.nextButton.setVisibility(tags.size() > 1 ? VISIBLE : GONE);
            binding.closeButton.setIconResource(R.drawable.icon_up);
            params.height = Math.round(DisplayUtils.dp2px(getContext(), 24));
            binding.closeButton.setIconSize(params.height * 3 / 4);
            setX(0);
        }
        binding.closeButton.setLayoutParams(params);
        binding.getRoot().setAlpha(expand ? 1 : 0.4f);
        SettingSave.getInstance().setPlayViewExpand(expand);
    }

    private boolean isInLeft() {
        int[] location = new int[2];
        getLocationOnScreen(location);
        Point size = DisplayUtils.getScreenSize(getContext());
        return location[0] < size.x / 2;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        boolean expand = SettingSave.getInstance().isPlayViewExpand();
        if (!expand) {
            PlayFloatView.this.setX((SCALE * border) * (isInLeft() ? -1 : 1));
        }
    }

    private void calculateTags(Collection<Task> tasks) {
        tags.clear();
        if (tasks != null) {
            for (Task task : tasks) {
                if (task.getTags() != null) {
                    task.getTags().forEach(tag -> {
                        if (tags.contains(tag)) return;
                        tags.add(tag);
                    });
                } else {
                    if (!tags.contains(SaveRepository.NO_TAG)) tags.add(SaveRepository.NO_TAG);
                }
            }
        }
        if (tags.isEmpty()) tags.add(SaveRepository.NO_TAG);
        binding.nextButton.setVisibility(tags.size() > 1 && SettingSave.getInstance().isPlayViewExpand() ? VISIBLE : GONE);
    }

    private LinkedHashMap<ManualStartAction, Task> getActionsByNextTag() {
        LinkedHashMap<ManualStartAction, Task> actions = new LinkedHashMap<>();
        if (tags.isEmpty()) return actions;
        int index = tags.indexOf(currTag);
        currTag = tags.get((index + 1) % tags.size());

        for (Map.Entry<ManualStartAction, Task> entry : manualStartActions.entrySet()) {
            HashSet<String> currTags = entry.getValue().getTags();
            if (currTags != null && !currTags.isEmpty()) {
                if (currTags.contains(currTag)) actions.put(entry.getKey(), entry.getValue());
            } else {
                if (SaveRepository.NO_TAG.equals(currTag)) actions.put(entry.getKey(), entry.getValue());
            }
        }
        return actions;
    }

    @Override
    public void show() {
        Point position = SettingSave.getInstance().getPlayViewPosition();
        EasyFloat.with(MainApplication.getInstance().getService())
                .setLayout(this)
                .setSidePattern(SidePattern.HORIZONTAL)
                .setGravity(FloatGravity.RIGHT_CENTER, position.x, position.y)
                .setTag(PlayFloatView.class.getName())
                .setAlwaysShow(true)
                .setCallback(new FloatCallback())
                .show();
    }

    @Override
    public void dismiss() {
        EasyFloat.dismiss(PlayFloatView.class.getName());
    }

    public void setNeedRemove(boolean needRemove) {
        for (int i = binding.buttonBox.getChildCount() - 1; i >= 0; i--) {
            PlayFloatViewItem view = (PlayFloatViewItem) binding.buttonBox.getChildAt(i);
            if (view.isFree() && needRemove) {
                binding.buttonBox.removeView(view);
            } else {
                view.setNeedRemove(needRemove);
            }
        }
    }

    public synchronized void onNewActions() {
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
        HashSet<View> needDelete = new HashSet<>();

        for (int i = binding.buttonBox.getChildCount() - 1; i >= 0; i--) {
            PlayFloatViewItem view = (PlayFloatViewItem) binding.buttonBox.getChildAt(i);
            if (actions.containsKey(view.getStartAction())) {
                // 已经在显示了，不移除
                view.setNeedRemove(false);
                alreadyManualStartActions.add(view.getStartAction());
            } else {
                // 没在新列表里的，需要移除
                if (view.isFree()) needDelete.add(view);
                else view.setNeedRemove(true);
            }
        }

        actions.forEach((startAction, task) -> {
            if (!alreadyManualStartActions.contains(startAction)) {
                binding.buttonBox.addView(new PlayFloatViewItem(getContext(), task, startAction));
            }
        });

        for (View view : needDelete) {
            binding.buttonBox.removeView(view);
        }
    }

    public void checkShow() {
        if (binding.buttonBox.getChildCount() == 0) dismiss();
    }

    private class FloatCallback extends FloatBaseCallback {
        @Override
        public void onDrag(MotionEvent event) {
            super.onDrag(event);
            PlayFloatView.this.setX(0);
        }

        @Override
        public void onDragEnd() {
            FloatViewHelper helper = EasyFloat.getHelper(PlayFloatView.class.getName());
            if (helper == null) return;
            Point position = helper.getConfigPosition();
            SettingSave.getInstance().setPlayViewPosition(new Point(position.x, position.y));


            boolean expand = SettingSave.getInstance().isPlayViewExpand();
            if (!expand) {
                PlayFloatView.this.setX((SCALE * border) * (isInLeft() ? -1 : 1));
            }
        }

        @Override
        public void onShow(String tag) {

        }

        @Override
        public void onDismiss() {

        }
    }
}
