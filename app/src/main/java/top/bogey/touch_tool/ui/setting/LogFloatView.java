package top.bogey.touch_tool.ui.setting;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.LogInfo;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.databinding.FloatLogBinding;
import top.bogey.touch_tool.service.MainAccessibilityService;
import top.bogey.touch_tool.utils.DisplayUtils;
import top.bogey.touch_tool.utils.TaskRunningCallback;
import top.bogey.touch_tool.utils.easy_float.EasyFloat;
import top.bogey.touch_tool.utils.easy_float.FloatGravity;
import top.bogey.touch_tool.utils.easy_float.FloatViewHelper;
import top.bogey.touch_tool.utils.easy_float.FloatViewInterface;

public class LogFloatView extends FrameLayout implements FloatViewInterface, TaskRunningCallback {
    private final HashMap<String, ArrayList<LogInfo>> logs = new LinkedHashMap<>();
    private final HashMap<String, String> tasks = new LinkedHashMap<>();
    private final FloatLogBinding binding;
    private final LogRecyclerViewAdapter adapter;

    private final ArrayAdapter<String> arrayAdapter;
    private String selectedTaskId;

    private float lastY = 0f;
    private boolean isToBottom = false;
    private boolean isToTop = true;

    private boolean isExpand = true;
    private boolean isZoom = false;

    @SuppressLint("ClickableViewAccessibility")
    public LogFloatView(@NonNull Context context) {
        super(context);
        binding = FloatLogBinding.inflate(LayoutInflater.from(context), this, true);

        binding.closeButton.setOnClickListener(v -> {
            dismiss();
            SettingView.resetSwitchState();
        });
        binding.expandButton.setOnClickListener(v -> {
            isExpand = false;
            refreshUI();
        });
        binding.showButton.setOnClickListener(v -> {
            isExpand = true;
            refreshUI();
        });

        binding.zoomButton.setOnClickListener(v -> {
            isZoom = !isZoom;
            refreshUI();
        });

        adapter = new LogRecyclerViewAdapter();
        binding.recyclerView.setAdapter(adapter);

        binding.recyclerView.setOnTouchListener((v, event) -> {
            ViewParent parent = getParent();
            if (parent != null) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastY = event.getY();
                        parent.requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        checkPosition(event.getY());
                        if (isToBottom || isToTop) {
                            parent.requestDisallowInterceptTouchEvent(false);
                            return false;
                        } else {
                            parent.requestDisallowInterceptTouchEvent(true);
                        }
                        lastY = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        parent.requestDisallowInterceptTouchEvent(false);
                        break;
                }
            }
            return false;
        });

        arrayAdapter = new ArrayAdapter<>(context, R.layout.pin_widget_spinner_item);
        binding.spinner.setAdapter(arrayAdapter);
        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Set<String> keySet = tasks.keySet();
                ArrayList<String> list = new ArrayList<>(keySet);
                if (list.size() > position && position >= 0) {
                    selectedTaskId = list.get(position);
                    adapter.addLogs(selectedTaskId, logs.computeIfAbsent(selectedTaskId, k -> new ArrayList<>()));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        MainAccessibilityService service = MainApplication.getInstance().getService();
        if (service != null) {
            service.addRunningCallback(this);
        }
    }

    private void refreshUI() {
        FloatViewHelper helper = EasyFloat.getHelper(LogFloatView.class.getCanonicalName());

        Point size = DisplayUtils.getScreenSize(getContext());
        int height = DisplayUtils.getStatusBarHeight(getContext());

        int bgWidth = (int) DisplayUtils.dp2px(getContext(), isExpand ? (isZoom ? 320 : 240) : 32);
        int bgHeight = (int) DisplayUtils.dp2px(getContext(), isExpand ? (isZoom ? 640 : 240) : 32);
        bgHeight = Math.min(bgHeight, size.y - height);

        binding.contentBox.setVisibility(isExpand ? VISIBLE : GONE);
        binding.showButton.setVisibility(isExpand ? GONE : VISIBLE);
        binding.zoomButton.setIconResource(isZoom ? R.drawable.icon_zoom_in : R.drawable.icon_zoom_out);

        MaterialCardView root = binding.getRoot();
        ViewGroup.LayoutParams rootLayoutParams = root.getLayoutParams();
        rootLayoutParams.width = bgWidth;
        rootLayoutParams.height = bgHeight;
        root.setLayoutParams(rootLayoutParams);
        helper.params.width = bgWidth;
        helper.params.height = bgHeight;
        helper.manager.updateViewLayout(helper.floatViewParent, helper.params);


        postDelayed(helper::initGravity, 50);
    }

    private void checkPosition(float nowY) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) binding.recyclerView.getLayoutManager();
        if (layoutManager != null) {
            if (layoutManager.getItemCount() > 3) {
                isToTop = false;
                isToBottom = false;
                int first = layoutManager.findFirstCompletelyVisibleItemPosition();
                int last = layoutManager.findLastCompletelyVisibleItemPosition();

                if (layoutManager.getChildCount() > 0) {
                    if (last == layoutManager.getItemCount() - 1) {
                        if (canScrollVertically(-1) && nowY < lastY) {
                            isToBottom = true;
                        }
                    } else if (first == 0) {
                        if (canScrollVertically(1) && nowY > lastY) {
                            isToTop = true;
                        }
                    }
                }
            } else {
                isToTop = true;
                isToBottom = true;
            }
        }
    }

    @Override
    public void show() {
        EasyFloat.with(MainApplication.getInstance().getService())
                .setLayout(this)
                .setGravity(FloatGravity.CENTER, 0, 0)
                .setTag(LogFloatView.class.getName())
                .setAlwaysShow(true)
                .setAnimator(null)
                .show();
    }

    @Override
    public void dismiss() {
        EasyFloat.dismiss(LogFloatView.class.getName());
    }

    @Override
    public void onStart(TaskRunnable runnable) {
    }

    @Override
    public void onEnd(TaskRunnable runnable) {
    }

    @Override
    public void onProgress(TaskRunnable runnable, int progress) {
    }

    @Override
    public void onAction(TaskRunnable runnable, ActionContext context, BaseAction action, int progress) {
        post(() -> {
            Task task = runnable.getStartTask();
            String log = action.getTitle(getContext());
            LogInfo logInfo = new LogInfo(task.getId(), log);
            logInfo.setIndex(progress);

            ArrayList<LogInfo> logInfoList = logs.computeIfAbsent(task.getId(), k -> new ArrayList<>());
            logInfoList.add(logInfo);
            tasks.put(task.getId(), task.getTitle());

            arrayAdapter.clear();
            arrayAdapter.addAll(tasks.values());
            if (selectedTaskId == null || selectedTaskId.isEmpty()) {
                binding.spinner.setSelection(0);
            }

            adapter.addLog(logInfo);
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        MainAccessibilityService service = MainApplication.getInstance().getService();
        if (service != null) {
            service.removeRunningCallback(this);
        }
        super.onDetachedFromWindow();
    }
}
