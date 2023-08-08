package top.bogey.touch_tool_pro.ui.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.databinding.FloatToastBinding;
import top.bogey.touch_tool_pro.service.MainAccessibilityService;
import top.bogey.touch_tool_pro.utils.DisplayUtils;
import top.bogey.touch_tool_pro.utils.easy_float.EasyFloat;
import top.bogey.touch_tool_pro.utils.easy_float.FloatGravity;
import top.bogey.touch_tool_pro.utils.easy_float.FloatViewInterface;

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
        EasyFloat.getHelper(ToastFloatView.class.getName()).initGravity();
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
                .setTag(ToastFloatView.class.getName())
                .setAlwaysShow(true)
                .setAnimator(null)
                .setDragEnable(false)
                .show();

        handler.postDelayed(this::dismiss, 1500);
    }

    @Override
    public void dismiss() {
        EasyFloat.dismiss(ToastFloatView.class.getName());
    }
}
