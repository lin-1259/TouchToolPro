package top.bogey.touch_tool.ui.card;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.action.pin.Pin;
import top.bogey.touch_tool.data.action.pin.PinDirection;
import top.bogey.touch_tool.databinding.CardBaseBinding;
import top.bogey.touch_tool.ui.card.pin.BasePin;
import top.bogey.touch_tool.ui.card.pin.InPin;
import top.bogey.touch_tool.ui.card.pin.OutPin;
import top.bogey.touch_tool.ui.task_blueprint.CardLayoutView;
import top.bogey.touch_tool.utils.DisplayUtils;

@SuppressLint("ViewConstructor")
public class BaseCard<A extends BaseAction> extends MaterialCardView {
    private final CardBaseBinding binding;
    private final Task task;
    private final A action;

    private final List<BasePin<?>> basePins = new ArrayList<>();

    private boolean needDelete = false;

    public BaseCard(@NonNull Context context, Task task, A action) {
        super(context, null);
        if (action == null) throw new RuntimeException("无效的动作");
        this.task = task;
        this.action = action;

        setCardElevation(DisplayUtils.dp2px(context, 5));
        setStrokeWidth(0);
        setCardBackgroundColor(DisplayUtils.getAttrColor(context, com.google.android.material.R.attr.colorSurfaceVariant, 0));
        ViewGroup.LayoutParams params = getLayoutParams();
        if (params == null)
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(params);

        binding = CardBaseBinding.inflate(LayoutInflater.from(context), this, true);
        binding.copyButton.setOnClickListener(v -> {
            String cls = action.getCls();
            try {
                Class<?> aClass = Class.forName(cls);
                A o = (A) aClass.newInstance();
                o.x = action.x + 1;
                o.y = action.y + 1;
                CardLayoutView parent = (CardLayoutView) getParent();
                parent.addAction(o);
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                throw new RuntimeException(e);
            }
        });

        binding.removeButton.setOnClickListener(v -> {
            if (needDelete) {
                binding.removeButton.setChecked(false);
                needDelete = false;
                CardLayoutView parent = (CardLayoutView) getParent();
                parent.removeAction(action);
            } else {
                binding.removeButton.setChecked(true);
                needDelete = true;
                postDelayed(() -> {
                    binding.removeButton.setChecked(false);
                    needDelete = false;
                }, 1500);
            }
        });

        binding.title.setText(action.getTitle(context));

        for (Pin<?> pin : action.getPins()) {
            if (pin.getDirection() == PinDirection.IN) {
                InPin inPin = new InPin(context, action, pin);
                binding.inBox.addView(inPin);
                basePins.add(inPin);
            } else if (pin.getDirection() == PinDirection.OUT) {
                OutPin outPin = new OutPin(context, action, pin);
                binding.outBox.addView(outPin);
                basePins.add(outPin);
            }
        }
    }

    public A getAction() {
        return action;
    }

    public BasePin<?> getPinById(String id) {
        if (id == null || id.isEmpty()) return null;
        for (BasePin<?> pin : basePins) {
            if (id.equals(pin.getPin().getId())) {
                return pin;
            }
        }
        return null;
    }

    public BasePin<?> getPinByPosition(float rawX, float rawY) {
        for (BasePin<?> pin : basePins) {
            int[] location = new int[2];
            View pinBox = pin.getPinBox();
            pinBox.getLocationOnScreen(location);
            if (new Rect(location[0], location[1], location[0] + pinBox.getWidth(), location[1] + pinBox.getHeight()).contains((int) rawX, (int) rawY)) {
                return pin;
            }
        }
        return null;
    }
}
