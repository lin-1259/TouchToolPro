package top.bogey.touch_tool.ui.card.pin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.viewbinding.ViewBinding;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.action.TimeArea;
import top.bogey.touch_tool.data.action.pin.Pin;
import top.bogey.touch_tool.data.action.pin.PinSpinnerHelper;
import top.bogey.touch_tool.data.action.pin.PinSelectAppHelper;
import top.bogey.touch_tool.ui.card.pin_widget.PinWidgetCheckBox;
import top.bogey.touch_tool.ui.card.pin_widget.PinWidgetInputInteger;
import top.bogey.touch_tool.ui.card.pin_widget.PinWidgetInputText;
import top.bogey.touch_tool.ui.card.pin_widget.PinWidgetSelectApp;
import top.bogey.touch_tool.ui.card.pin_widget.PinWidgetSpinner;
import top.bogey.touch_tool.ui.card.pin_widget.PinWidgetTime;
import top.bogey.touch_tool.ui.card.pin_widget.PinWidgetTimeArea;
import top.bogey.touch_tool.ui.custom.BindingView;

@SuppressLint("ViewConstructor")
public class BasePin<T extends ViewBinding> extends BindingView<T> {
    protected final FrameLayout pinSlotBox;
    protected final MaterialCardView pinSlot;
    protected final FrameLayout pinBox;
    protected final MaterialTextView titleText;
    protected final MaterialButton removeButton;

    protected final BaseAction action;
    protected final Pin<?> pin;

    public BasePin(@NonNull Context context, Class<T> tClass, BaseAction action, Pin<?> pin) {
        super(context, null, tClass);
        this.action = action;
        this.pin = pin;

        try {
            pinSlotBox = (FrameLayout) tClass.getField("pinSlotBox").get(binding);
            pinSlot = (MaterialCardView) tClass.getField("pinSlot").get(binding);
            pinBox = (FrameLayout) tClass.getField("pinBox").get(binding);
            titleText = (MaterialTextView) tClass.getField("title").get(binding);
            removeButton = (MaterialButton) tClass.getField("removeButton").get(binding);
            if (pinSlot == null || pinBox == null || titleText == null || removeButton == null)
                throw new RuntimeException("无效的绑定");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        if (pin == null) return;
        pinSlot.setCardBackgroundColor(pin.getType().getPinColor(context));
        if (pin.getTitle() != 0) titleText.setText(pin.getTitle());
        removeButton.setVisibility(pin.isRemoveAble() ? VISIBLE : GONE);
        switch (pin.getType()) {
            case TIME_AREA:
                pinBox.addView(new PinWidgetTimeArea(context, (TimeArea) pin.getValue()));
                break;
            case APP:
                pinBox.addView(new PinWidgetSelectApp(context, (PinSelectAppHelper) pin.getValue()));
                break;
            case ARRAY:
                pinBox.addView(new PinWidgetSpinner(context, (PinSpinnerHelper) pin.getValue()));
                break;
            case BOOLEAN:
                pinBox.addView(new PinWidgetCheckBox(context, (AtomicBoolean) pin.getValue()));
                break;
            case INTEGER:
                pinBox.addView(new PinWidgetInputInteger(context, (AtomicInteger) pin.getValue()));
                break;
            case STRING:
                pinBox.addView(new PinWidgetInputText(context, (AtomicReference<CharSequence>) pin.getValue()));
                break;
            case DATE:
            case TIME:
            case PERIODIC:
                pinBox.addView(new PinWidgetTime(context, (AtomicLong) pin.getValue(), pin.getType()));
                break;
        }
    }

    public Pin<?> getPin() {
        return pin;
    }

    public int[] getSlotLocationOnScreen() {
        throw new RuntimeException("未覆盖此方法");
    }

    public View getPinBox() {
        return pinSlotBox;
    }
}
