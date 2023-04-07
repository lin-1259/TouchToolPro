package top.bogey.touch_tool.ui.card.pin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewbinding.ViewBinding;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.shape.ShapeAppearanceModel;

import java.util.HashMap;

import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinSubType;
import top.bogey.touch_tool.data.pin.object.PinAdd;
import top.bogey.touch_tool.data.pin.object.PinBoolean;
import top.bogey.touch_tool.data.pin.object.PinColor;
import top.bogey.touch_tool.data.pin.object.PinExecute;
import top.bogey.touch_tool.data.pin.object.PinImage;
import top.bogey.touch_tool.data.pin.object.PinInteger;
import top.bogey.touch_tool.data.pin.object.PinLong;
import top.bogey.touch_tool.data.pin.object.PinPath;
import top.bogey.touch_tool.data.pin.object.PinPoint;
import top.bogey.touch_tool.data.pin.object.PinSelectApp;
import top.bogey.touch_tool.data.pin.object.PinSpinner;
import top.bogey.touch_tool.data.pin.object.PinString;
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
import top.bogey.touch_tool.ui.card.pin_widget.PinWidgetPoint;
import top.bogey.touch_tool.ui.card.pin_widget.PinWidgetSpinner;
import top.bogey.touch_tool.ui.card.pin_widget.PinWidgetString;
import top.bogey.touch_tool.ui.card.pin_widget.PinWidgetStringPicker;
import top.bogey.touch_tool.ui.card.pin_widget.PinWidgetValueArea;
import top.bogey.touch_tool.ui.card.pin_widget.PinWidgetWidgetPicker;
import top.bogey.touch_tool.ui.custom.BindingView;
import top.bogey.touch_tool.utils.DisplayUtils;

@SuppressLint("ViewConstructor")
public abstract class PinBaseView<V extends ViewBinding> extends BindingView<V> {
    protected final BaseCard<?> card;
    protected final BaseAction action;
    protected final Pin pin;

    private boolean expand;

    public PinBaseView(@NonNull Context context, Class<V> tClass, BaseCard<? extends BaseAction> card, Pin pin) {
        super(context, null, tClass);
        if (pin == null) throw new RuntimeException("无效的针脚");
        this.card = card;
        action = card.getAction();
        this.pin = pin;

        pin.addListener(new Pin.LinkListener() {
            @Override
            public void onAdded(Pin pin) {
                post(() -> refreshPinUI());
            }

            @Override
            public void onRemoved(Pin pin) {
                post(() -> refreshPinUI());
            }

            @Override
            public void onChanged() {
                post(() -> {
                    setValueView();
                    refreshPinUI();
                });
            }
        });
    }

    protected void initRemoveButton(MaterialButton button) {
        button.setVisibility(pin.isRemoveAble() ? VISIBLE : GONE);
        button.setOnClickListener(v -> card.removeMorePinView(pin));
    }

    public void setExpand(boolean expand) {
        this.expand = expand;

        if (pin.isVertical()) return;
        HashMap<String, String> links = pin.getLinks();
        setVisibility((expand || links.size() > 0) ? VISIBLE : GONE);
    }

    public abstract void refreshPinUI();

    public abstract int[] getSlotLocationOnScreen(float scale);

    public abstract View getSlotBox();

    public abstract ViewGroup getPinBox();

    public void setValueView() {
        ViewGroup viewGroup = getPinBox();
        if (viewGroup == null) return;

        viewGroup.removeAllViews();
        Context context = getContext();
        Class<?> aClass = pin.getPinClass();
        if (PinSelectApp.class.equals(aClass)) {
            viewGroup.addView(new PinWidgetAppPicker(context, (PinSelectApp) pin.getValue()));
        } else if (PinSpinner.class.equals(aClass)) {
            viewGroup.addView(new PinWidgetSpinner(context, (PinSpinner) pin.getValue()));
        } else if (PinBoolean.class.equals(aClass)) {
            viewGroup.addView(new PinWidgetBoolean(context, (PinBoolean) pin.getValue()));
        } else if (PinInteger.class.equals(aClass)) {
            viewGroup.addView(new PinWidgetInteger(context, (PinInteger) pin.getValue()));
        } else if (PinString.class.equals(aClass)) {
            if (pin.getSubType() == PinSubType.NORMAL) {
                viewGroup.addView(new PinWidgetString(context, (PinString) pin.getValue()));
            } else {
                viewGroup.addView(new PinWidgetStringPicker(context, (PinString) pin.getValue(), pin.getSubType()));
            }
        } else if (PinValueArea.class.equals(aClass)) {
            viewGroup.addView(new PinWidgetValueArea(context, (PinValueArea) pin.getValue()));
        } else if (PinPoint.class.equals(aClass)) {
            viewGroup.addView(new PinWidgetPoint(context, (PinPoint) pin.getValue()));
        }

        // picker
        if (PinPath.class.equals(aClass)) {
            viewGroup.addView(new PinWidgetPathPicker(context, (PinPath) pin.getValue()));
        } else if (PinImage.class.equals(aClass)) {
            viewGroup.addView(new PinWidgetImagePicker(context, (PinImage) pin.getValue()));
        } else if (PinColor.class.equals(aClass)) {
            viewGroup.addView(new PinWidgetColorPicker(context, (PinColor) pin.getValue()));
        } else if (PinLong.class.equals(aClass)) {
            viewGroup.addView(new PinWidgetLongPicker(context, (PinLong) pin.getValue(), pin.getSubType()));
        } else if (PinWidget.class.equals(aClass)) {
            viewGroup.addView(new PinWidgetWidgetPicker(context, (PinWidget) pin.getValue()));
        }

        if (PinAdd.class.equals(aClass)) {
            viewGroup.addView(new PinWidgetAdd(context, (PinAdd) pin.getValue(), card));
        }
    }

    public int getPinColor() {
        return pin.getValue().getPinColor(getContext());
    }

    public ShapeAppearanceModel getPinStyle() {
        return pin.getValue().getPinStyle(getContext());
    }

    public BaseAction getAction() {
        return action;
    }

    public Pin getPin() {
        return pin;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        pin.addListener(null);
    }
}
