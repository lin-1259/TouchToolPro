package top.bogey.touch_tool.ui.card;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.object.PinObject;
import top.bogey.touch_tool.databinding.CardBaseBinding;
import top.bogey.touch_tool.ui.card.pin.PinBaseView;
import top.bogey.touch_tool.ui.card.pin.PinInView;
import top.bogey.touch_tool.ui.card.pin.PinOutView;
import top.bogey.touch_tool.ui.task_blueprint.CardLayoutView;
import top.bogey.touch_tool.utils.AppUtils;
import top.bogey.touch_tool.utils.DisplayUtils;

@SuppressLint("ViewConstructor")
public class BaseCard<A extends BaseAction> extends MaterialCardView {
    private final CardBaseBinding binding;
    private final Task task;
    private final A action;

    private final List<PinBaseView<?>> pinBaseViews = new ArrayList<>();

    private boolean needDelete = false;

    @SuppressLint("ClickableViewAccessibility")
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
        binding.copyButton.setOnClickListener(v -> ((CardLayoutView) getParent()).addAction(action.copy()));

        binding.removeButton.setOnClickListener(v -> {
            if (needDelete) {
                binding.removeButton.setChecked(false);
                needDelete = false;
                ((CardLayoutView) getParent()).removeAction(action);
            } else {
                binding.removeButton.setChecked(true);
                needDelete = true;
                postDelayed(() -> {
                    binding.removeButton.setChecked(false);
                    needDelete = false;
                }, 1500);
            }
        });

        binding.title.setText(action.getTitle());
        binding.des.setText(action.getDes());
        binding.des.setVisibility((action.getDes() == null || action.getDes().length() == 0) ? GONE : VISIBLE);

        binding.editButton.setOnClickListener(v -> AppUtils.showEditDialog(context, R.string.action_subtitle_add_des, action.getDes(), result -> {
            action.setDes(result);
            binding.des.setText(result);
            binding.des.setVisibility((result == null || result.length() == 0) ? GONE : VISIBLE);
        }));

        for (Pin<? extends PinObject> pin : action.getPins()) {
            if (pin.getDirection() == PinDirection.IN) {
                PinInView pinInView = new PinInView(getContext(), this, pin);
                binding.inBox.addView(pinInView);
                pinBaseViews.add(pinInView);
            } else if (pin.getDirection() == PinDirection.OUT) {
                PinOutView pinOutView = new PinOutView(getContext(), this, pin);
                binding.outBox.addView(pinOutView);
                pinBaseViews.add(pinOutView);
            }
        }
    }

    public void addMorePinView(Pin<? extends PinObject> pin) {
        action.addPin(action.getPins().size() - 1, pin);
        if (pin.getDirection() == PinDirection.IN) {
            PinInView pinInView = new PinInView(getContext(), this, pin);
            binding.inBox.addView(pinInView, binding.inBox.getChildCount() - 1);
            pinBaseViews.add(pinInView);
        } else if (pin.getDirection() == PinDirection.OUT) {
            PinOutView pinOutView = new PinOutView(getContext(), this, pin);
            binding.outBox.addView(pinOutView, binding.outBox.getChildCount() - 1);
            pinBaseViews.add(pinOutView);
        }
    }

    public void removeMorePinView(PinBaseView<?> pinBaseView) {
        Pin<?> pin = pinBaseView.getPin();
        Pin<? extends PinObject> removePin = action.removePin(pin);
        if (removePin == null) return;
        ((CardLayoutView) getParent()).linksRemovePin(removePin.getLinks(), pinBaseView);

        LinearLayout linearLayout = removePin.getDirection() == PinDirection.IN ? binding.inBox : binding.outBox;
        linearLayout.removeView(pinBaseView);
    }

    public Task getTask() {
        return task;
    }

    public A getAction() {
        return action;
    }

    public PinBaseView<?> getPinById(String id) {
        if (id == null || id.isEmpty()) return null;
        for (PinBaseView<?> pinBaseView : pinBaseViews) {
            if (id.equals(pinBaseView.getPin().getId())) {
                return pinBaseView;
            }
        }
        return null;
    }

    public PinBaseView<?> getPinByPosition(float rawX, float rawY) {
        for (PinBaseView<?> pinBaseView : pinBaseViews) {
            int[] location = new int[2];
            View pinBox = pinBaseView.getPinBox();
            pinBox.getLocationOnScreen(location);
            if (new Rect(location[0], location[1], location[0] + pinBox.getWidth(), location[1] + pinBox.getHeight()).contains((int) rawX, (int) rawY)) {
                return pinBaseView;
            }
        }
        return null;
    }
}
