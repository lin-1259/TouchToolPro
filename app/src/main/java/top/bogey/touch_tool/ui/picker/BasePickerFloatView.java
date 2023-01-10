package top.bogey.touch_tool.ui.picker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.utils.FloatBaseCallback;
import top.bogey.touch_tool.utils.easy_float.EasyFloat;
import top.bogey.touch_tool.utils.easy_float.FloatViewInterface;

@SuppressLint("ViewConstructor")
public class BasePickerFloatView extends FrameLayout implements FloatViewInterface {
    protected String tag;
    protected FloatBaseCallback floatCallback = new FloatBaseCallback();

    public BasePickerFloatView(@NonNull Context context) {
        super(context);
        tag = this.getClass().getName();
    }

    @Override
    public void show() {
        EasyFloat.with(MainApplication.getActivity())
                .setLayout(this)
                .setTag(tag)
                .setDragEnable(false)
                .setMatch(true, true)
                .setCallback(floatCallback)
                .setAnimator(null)
                .show();
    }

    @Override
    public void dismiss() {
        EasyFloat.dismiss(tag);
    }
}
