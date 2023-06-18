package top.bogey.touch_tool.ui.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.service.MainAccessibilityService;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.databinding.FloatKeepAliveBinding;
import top.bogey.touch_tool.utils.DisplayUtils;
import top.bogey.touch_tool.utils.TaskRunningCallback;
import top.bogey.touch_tool.utils.easy_float.EasyFloat;
import top.bogey.touch_tool.utils.easy_float.FloatGravity;
import top.bogey.touch_tool.utils.easy_float.FloatViewInterface;

@SuppressLint("ViewConstructor")
public class KeepAliveFloatView extends FrameLayout implements FloatViewInterface, TaskRunningCallback {
    private final FloatKeepAliveBinding binding;
    private final Handler handler;

    public KeepAliveFloatView(@NonNull Context context) {
        super(context);
        binding = FloatKeepAliveBinding.inflate(LayoutInflater.from(context), this, true);
        handler = new Handler();
        MainAccessibilityService service = MainApplication.getInstance().getService();
        if (service != null) {
            service.addRunningCallback(this);
        }
    }

    public void showMe() {
        post(() -> {
            binding.getRoot().animate().alpha(0.5f);
            handler.removeCallbacksAndMessages(null);
            handler.postDelayed(this::hideMe, 1500);
        });
    }

    private void hideMe() {
        binding.getRoot().animate().alpha(0f);
    }

    @Override
    public void show() {
        MainAccessibilityService service = MainApplication.getInstance().getService();
        if (service == null || !service.isServiceConnected()) {
            return;
        }

        EasyFloat.with(service)
                .setLayout(this)
                .setGravity(FloatGravity.TOP_CENTER, 0, Math.round(DisplayUtils.dp2px(getContext(), 2)))
                .setTag(KeepAliveFloatView.class.getCanonicalName())
                .setAlwaysShow(true)
                .setDragEnable(false)
                .setAnimator(null)
                .show();

        showMe();
    }

    @Override
    public void dismiss() {
        EasyFloat.dismiss(KeepAliveFloatView.class.getCanonicalName());
    }

    @Override
    public void onStart(TaskRunnable runnable) {
        showMe();
    }

    @Override
    public void onEnd(TaskRunnable runnable) {

    }

    @Override
    public void onProgress(TaskRunnable runnable, int progress) {

    }

    @Override
    public void onAction(TaskRunnable runnable, ActionContext context, BaseAction action) {

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
