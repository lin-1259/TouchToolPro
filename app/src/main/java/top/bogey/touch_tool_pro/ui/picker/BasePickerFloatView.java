package top.bogey.touch_tool_pro.ui.picker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.utils.easy_float.EasyFloat;
import top.bogey.touch_tool_pro.utils.easy_float.FloatViewInterface;

@SuppressLint("ViewConstructor")
public class BasePickerFloatView extends FrameLayout implements FloatViewInterface {
    protected final IPickerCallback pickerCallback;
    protected String tag;
    protected FloatBaseCallback floatCallback = new FloatBaseCallback();

    public BasePickerFloatView(@NonNull Context context, IPickerCallback pickerCallback) {
        super(context);
        this.pickerCallback = pickerCallback;
        tag = this.getClass().getName();
    }

    public void setPickerCallback(FloatBaseCallback floatCallback) {
        this.floatCallback = floatCallback;
    }

    @Override
    public void show() {
        EasyFloat.with(MainApplication.getInstance().getService())
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

    @Override
    public String getTag() {
        return tag;
    }
}
