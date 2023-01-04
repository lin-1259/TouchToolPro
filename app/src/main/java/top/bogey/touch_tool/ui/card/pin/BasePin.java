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

import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.action.pin.object.PinBoolean;
import top.bogey.touch_tool.data.action.pin.object.PinInteger;
import top.bogey.touch_tool.data.action.pin.object.PinLong;
import top.bogey.touch_tool.data.action.pin.object.PinObject;
import top.bogey.touch_tool.data.action.pin.object.PinString;
import top.bogey.touch_tool.data.action.pin.object.PinTimeArea;
import top.bogey.touch_tool.data.action.pin.Pin;
import top.bogey.touch_tool.data.action.pin.object.PinSpinner;
import top.bogey.touch_tool.data.action.pin.object.PinSelectApp;
import top.bogey.touch_tool.data.action.pin.object.PinValueArea;
import top.bogey.touch_tool.ui.card.pin_widget.PinWidgetBoolean;
import top.bogey.touch_tool.ui.card.pin_widget.PinWidgetInteger;
import top.bogey.touch_tool.ui.card.pin_widget.PinWidgetString;
import top.bogey.touch_tool.ui.card.pin_widget.PinWidgetSelectApp;
import top.bogey.touch_tool.ui.card.pin_widget.PinWidgetSpinner;
import top.bogey.touch_tool.ui.card.pin_widget.PinWidgetLong;
import top.bogey.touch_tool.ui.card.pin_widget.PinWidgetTimeArea;
import top.bogey.touch_tool.ui.card.pin_widget.PinWidgetValueArea;
import top.bogey.touch_tool.ui.custom.BindingView;

@SuppressLint("ViewConstructor")
public class BasePin<T extends ViewBinding> extends BindingView<T> {
    protected final FrameLayout pinSlotBox;
    protected final MaterialCardView pinSlot;
    protected final FrameLayout pinBox;
    protected final MaterialTextView titleText;
    protected final MaterialButton removeButton;

    protected final BaseAction action;
    protected final Pin<? extends PinObject> pin;

    public BasePin(@NonNull Context context, Class<T> tClass, BaseAction action, Pin<? extends PinObject> pin) {
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
        pinSlot.setCardBackgroundColor(pin.getValue().getPinColor(context));

        if (pin.getTitle() != 0) titleText.setText(pin.getTitle());
        removeButton.setVisibility(pin.isRemoveAble() ? VISIBLE : GONE);

        Class<? extends PinObject> aClass = pin.getValue().getClass();
        if (PinTimeArea.class.equals(aClass)) {
            pinBox.addView(new PinWidgetTimeArea(context, (PinTimeArea) pin.getValue()));
        } else if (PinSelectApp.class.equals(aClass)) {
            pinBox.addView(new PinWidgetSelectApp(context, (PinSelectApp) pin.getValue()));
        } else if (PinSpinner.class.equals(aClass)) {
            pinBox.addView(new PinWidgetSpinner(context, (PinSpinner) pin.getValue()));
        } else if (PinBoolean.class.equals(aClass)) {
            pinBox.addView(new PinWidgetBoolean(context, (PinBoolean) pin.getValue()));
        } else if (PinInteger.class.equals(aClass)) {
            pinBox.addView(new PinWidgetInteger(context, (PinInteger) pin.getValue()));
        } else if (PinString.class.equals(aClass)) {
            pinBox.addView(new PinWidgetString(context, (PinString) pin.getValue()));
        } else if (PinLong.class.equals(aClass)) {
            pinBox.addView(new PinWidgetLong(context, (PinLong) pin.getValue(), pin.getSubType()));
        } else if (PinValueArea.class.equals(aClass)) {
            pinBox.addView(new PinWidgetValueArea(context, (PinValueArea) pin.getValue()));
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
