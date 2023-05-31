package top.bogey.touch_tool.ui.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import top.bogey.touch_tool.service.MainAccessibilityService;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.databinding.FloatToastBinding;
import top.bogey.touch_tool.utils.DisplayUtils;
import top.bogey.touch_tool.utils.easy_float.EasyFloat;
import top.bogey.touch_tool.utils.easy_float.FloatGravity;
import top.bogey.touch_tool.utils.easy_float.FloatViewInterface;

@SuppressLint("ViewConstructor")
public class ToastFloatView extends FrameLayout implements FloatViewInterface {
    private final FloatToastBinding binding;
    private final Handler handler;

    public ToastFloatView(@NonNull Context context) {
        super(context);
        binding = FloatToastBinding.inflate(LayoutInflater.from(context), this, true);
        handler = new Handler();
    }

    public void showToast(String msg) {
        MainAccessibilityService service = MainApplication.getInstance().getService();
        if (service == null || !service.isServiceConnected()) {
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
            return;
        }

        handler.removeCallbacksAndMessages(null);
        binding.title.setText(msg);
        handler.postDelayed(this::dismiss, 1500);
    }

    @Override
    public void show() {
        MainAccessibilityService service = MainApplication.getInstance().getService();
        if (service == null || !service.isServiceConnected()) {
            return;
        }

        Point screenSize = DisplayUtils.getScreenSize(getContext());
        EasyFloat.with(service)
                .setLayout(this)
                .setGravity(FloatGravity.BOTTOM_CENTER, 0, -screenSize.y / 5)
                .setTag(ToastFloatView.class.getCanonicalName())
                .setAlwaysShow(true)
                .setAnimator(null)
                .setDragEnable(false)
                .show();

        handler.postDelayed(this::dismiss, 1500);
    }

    @Override
    public void dismiss() {
        EasyFloat.dismiss(ToastFloatView.class.getCanonicalName());
    }
}
