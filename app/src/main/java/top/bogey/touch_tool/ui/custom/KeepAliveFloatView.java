package top.bogey.touch_tool.ui.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import top.bogey.touch_tool.MainAccessibilityService;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.databinding.ViewKeepAliveBinding;
import top.bogey.touch_tool.utils.DisplayUtils;
import top.bogey.touch_tool.utils.easy_float.EasyFloat;
import top.bogey.touch_tool.utils.easy_float.FloatGravity;
import top.bogey.touch_tool.utils.easy_float.FloatViewInterface;

@SuppressLint("ViewConstructor")
public class KeepAliveFloatView extends FrameLayout implements FloatViewInterface {
    private final ViewKeepAliveBinding binding;
    private final Handler handler;

    public KeepAliveFloatView(@NonNull Context context) {
        super(context);
        binding = ViewKeepAliveBinding.inflate(LayoutInflater.from(context), this, true);
        handler = new Handler();
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
                .setGravity(FloatGravity.TOP_CENTER, 0, DisplayUtils.dp2px(getContext(), 2))
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
}
