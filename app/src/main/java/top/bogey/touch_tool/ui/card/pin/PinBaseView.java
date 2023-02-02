package top.bogey.touch_tool.ui.card.pin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.viewbinding.ViewBinding;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.HashMap;

import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.object.PinAdd;
import top.bogey.touch_tool.data.pin.object.PinBoolean;
import top.bogey.touch_tool.data.pin.object.PinColor;
import top.bogey.touch_tool.data.pin.object.PinImage;
import top.bogey.touch_tool.data.pin.object.PinInteger;
import top.bogey.touch_tool.data.pin.object.PinLong;
import top.bogey.touch_tool.data.pin.object.PinObject;
import top.bogey.touch_tool.data.pin.object.PinPath;
import top.bogey.touch_tool.data.pin.object.PinSelectApp;
import top.bogey.touch_tool.data.pin.object.PinSpinner;
import top.bogey.touch_tool.data.pin.object.PinString;
import top.bogey.touch_tool.data.pin.object.PinTimeArea;
import top.bogey.touch_tool.data.pin.object.PinValueArea;
import top.bogey.touch_tool.data.pin.object.PinWidget;
import top.bogey.touch_tool.ui.card.BaseCard;
import top.bogey.touch_tool.ui.card.pin_widget.PinWidgetAdd;
import top.bogey.touch_tool.ui.card.pin_widget.PinWidgetAppPicker;
import top.bogey.touch_tool.ui.card.pin_widget.PinWidgetBoolean;
import top.bogey.touch_tool.ui.card.pin_widget.PinWidgetColorPicker;
import top.bogey.touch_tool.ui.card.pin_widget.PinWidgetImagePicker;
import top.bogey.touch_tool.ui.card.pin_widget.PinWidgetInteger;
import top.bogey.touch_tool.ui.card.pin_widget.PinWidgetLongPicker;
import top.bogey.touch_tool.ui.card.pin_widget.PinWidgetPathPicker;
import top.bogey.touch_tool.ui.card.pin_widget.PinWidgetSpinner;
import top.bogey.touch_tool.ui.card.pin_widget.PinWidgetString;
import top.bogey.touch_tool.ui.card.pin_widget.PinWidgetTimeArea;
import top.bogey.touch_tool.ui.card.pin_widget.PinWidgetValueArea;
import top.bogey.touch_tool.ui.card.pin_widget.PinWidgetWidgetPicker;
import top.bogey.touch_tool.ui.custom.BindingView;
import top.bogey.touch_tool.utils.DisplayUtils;

@SuppressLint("ViewConstructor")
public class PinBaseView<T extends ViewBinding> extends BindingView<T> {
    protected final LinearLayout pinSlotBox;
    protected final MaterialCardView pinSlot;
    protected final FrameLayout pinBox;
    protected final MaterialTextView titleText;
    protected final MaterialButton removeButton;

    protected final BaseAction action;
    protected final Pin<? extends PinObject> pin;

    public PinBaseView(@NonNull Context context, Class<T> tClass, BaseCard<? extends BaseAction> card, Pin<? extends PinObject> pin) {
        super(context, null, tClass);
        action = card.getAction();
        this.pin = pin;

        try {
            pinSlotBox = (LinearLayout) tClass.getField("pinSlotBox").get(binding);
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
        pinSlot.setCardBackgroundColor(pin.getPinColor(context));
        pinSlot.setStrokeWidth(DisplayUtils.dp2px(context, 1.1f));
        pinSlot.setStrokeColor(pin.getPinColor(context));

        refreshPinUI();
        removeButton.setVisibility(pin.isRemoveAble() ? VISIBLE : GONE);
        removeButton.setOnClickListener(v -> card.removeMorePinView(this));

        Class<? extends PinObject> aClass = pin.getPinClass();
        if (PinTimeArea.class.equals(aClass)) {
            pinBox.addView(new PinWidgetTimeArea(context, (PinTimeArea) pin.getValue()));
        } else if (PinSelectApp.class.equals(aClass)) {
            pinBox.addView(new PinWidgetAppPicker(context, (PinSelectApp) pin.getValue()));
        } else if (PinSpinner.class.equals(aClass)) {
            pinBox.addView(new PinWidgetSpinner(context, (PinSpinner) pin.getValue()));
        } else if (PinBoolean.class.equals(aClass)) {
            pinBox.addView(new PinWidgetBoolean(context, (PinBoolean) pin.getValue()));
        } else if (PinInteger.class.equals(aClass)) {
            pinBox.addView(new PinWidgetInteger(context, (PinInteger) pin.getValue()));
        } else if (PinString.class.equals(aClass)) {
            pinBox.addView(new PinWidgetString(context, (PinString) pin.getValue()));
        } else if (PinValueArea.class.equals(aClass)) {
            pinBox.addView(new PinWidgetValueArea(context, (PinValueArea) pin.getValue()));
        }

        // picker
        if (PinPath.class.equals(aClass)) {
            pinBox.addView(new PinWidgetPathPicker(context, (PinPath) pin.getValue()));
        } else if (PinImage.class.equals(aClass)) {
            pinBox.addView(new PinWidgetImagePicker(context, (PinImage) pin.getValue()));
        } else if (PinColor.class.equals(aClass)) {
            pinBox.addView(new PinWidgetColorPicker(context, (PinColor) pin.getValue()));
        } else if (PinLong.class.equals(aClass)) {
            pinBox.addView(new PinWidgetLongPicker(context, (PinLong) pin.getValue(), pin.getSubType()));
        } else if (PinWidget.class.equals(aClass)) {
            pinBox.addView(new PinWidgetWidgetPicker(context, (PinWidget) pin.getValue()));
        }

        if (PinAdd.class.equals(aClass)) {
            pinBox.addView(new PinWidgetAdd(context, (PinAdd) pin.getValue(), card));
        }
    }

    public Pin<?> getPin() {
        return pin;
    }

    public HashMap<String, String> addLink(Pin<? extends PinObject> pin) {
        HashMap<String, String> removedLinkMap = this.pin.addLink(pin);
        refreshPinUI();
        return removedLinkMap;
    }

    public void removeLink(Pin<? extends PinObject> pin) {
        this.pin.removeLink(pin);
        refreshPinUI();
    }

    public void refreshPinUI() {
        boolean linked = pin.getLinks().size() > 0;
        boolean hidePinBox = linked || pin.getDirection() == PinDirection.OUT;
        pinBox.setVisibility(hidePinBox ? GONE : VISIBLE);
        if (pin.getTitle() != null)
            titleText.setText(String.format(hidePinBox ? " %s " : " %s: ", pin.getTitle()));

        pinSlot.setCardBackgroundColor(linked ? pin.getPinColor(getContext()) : DisplayUtils.getAttrColor(getContext(), com.google.android.material.R.attr.colorSurfaceVariant, 0));
        pinSlot.setShapeAppearanceModel(pin.getValue().getPinStyle(getContext()));
    }

    public int[] getSlotLocationOnScreen() {
        throw new RuntimeException("未覆盖此方法");
    }

    public View getPinBox() {
        return pinSlotBox;
    }

    public BaseAction getAction() {
        return action;
    }
}
