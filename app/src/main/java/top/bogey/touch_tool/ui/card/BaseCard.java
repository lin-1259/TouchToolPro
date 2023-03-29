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

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.object.PinAdd;
import top.bogey.touch_tool.data.pin.object.PinExecute;
import top.bogey.touch_tool.databinding.CardBaseBinding;
import top.bogey.touch_tool.ui.card.pin.PinBaseView;
import top.bogey.touch_tool.ui.card.pin.PinBottomView;
import top.bogey.touch_tool.ui.card.pin.PinInView;
import top.bogey.touch_tool.ui.card.pin.PinOutView;
import top.bogey.touch_tool.ui.card.pin.PinTopView;
import top.bogey.touch_tool.ui.blueprint.CardLayoutView;
import top.bogey.touch_tool.utils.AppUtils;
import top.bogey.touch_tool.utils.DisplayUtils;

@SuppressLint("ViewConstructor")
public class BaseCard<A extends BaseAction> extends MaterialCardView {
    protected final CardBaseBinding binding;
    protected final ActionContext actionContext;
    protected final A action;

    protected final List<PinBaseView<?>> pinDetailViews = new ArrayList<>();
    protected final List<PinBaseView<?>> pinBaseViews = new ArrayList<>();

    protected boolean needDelete = false;

    @SuppressLint("ClickableViewAccessibility")
    public BaseCard(@NonNull Context context, ActionContext actionContext, A action) {
        super(context, null);
        if (action == null) throw new RuntimeException("无效的动作");
        this.actionContext = actionContext;
        this.action = action;

        setCardElevation(DisplayUtils.dp2px(context, 5));
        setStrokeWidth(0);
        setCardBackgroundColor(DisplayUtils.getAttrColor(context, com.google.android.material.R.attr.colorSurfaceVariant, 0));
        setPivotX(0);
        setPivotY(0);
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

        binding.title.setText(action.getTitle(context));
        binding.des.setText(action.getDes());
        binding.desBox.setVisibility((action.getDes() == null || action.getDes().length() == 0) ? GONE : VISIBLE);

        binding.editButton.setOnClickListener(v -> AppUtils.showEditDialog(context, R.string.action_subtitle_add_des, action.getDes(), result -> {
            action.setDes((String) result);
            binding.des.setText(result);
            binding.desBox.setVisibility((result == null || result.length() == 0) ? GONE : VISIBLE);
        }));

        binding.expandButton.setOnClickListener(v -> {
            action.showDetail = !action.showDetail;
            binding.pinBox.setVisibility(action.showDetail ? VISIBLE : GONE);
            binding.expandButton.setIconResource(action.showDetail ? R.drawable.icon_up : R.drawable.icon_down);
            actionContext.save();
        });
        binding.pinBox.setVisibility(action.showDetail ? VISIBLE : GONE);
        binding.expandButton.setIconResource(action.showDetail ? R.drawable.icon_up : R.drawable.icon_down);

        for (Pin pin : action.getShowPins()) {
            addPinView(pin, 0);
        }
    }

    public void addPinView(Pin pin, int offset) {
        PinBaseView<?> pinDetailView = null;
        PinBaseView<?> pinBaseView;
        if (pin.getDirection() == PinDirection.IN) {
            if (!(pin.getValue() instanceof PinExecute)) {
                pinDetailView = new PinInView(getContext(), this, pin);
                binding.inBox.addView(pinDetailView, binding.inBox.getChildCount() - offset);
            }
            pinBaseView = new PinTopView(getContext(), this, pin);
            binding.topBox.addView(pinBaseView, binding.topBox.getChildCount() - offset);

        } else {
            if (!(pin.getValue() instanceof PinExecute)) {
                pinDetailView = new PinOutView(getContext(), this, pin);
                binding.outBox.addView(pinDetailView, binding.outBox.getChildCount() - offset);
            }
            pinBaseView = new PinBottomView(getContext(), this, pin);
            binding.bottomBox.addView(pinBaseView, binding.bottomBox.getChildCount() - offset);
        }
        if (pinDetailView != null) pinDetailViews.add(pinDetailView);

        if (pin.getValue() instanceof PinAdd) {
            pinBaseView.setVisibility(GONE);
        }
        pinBaseViews.add(pinBaseView);
    }

    public void addMorePinView(Pin pin, int offset) {
        action.addPin(action.getPins().size() - offset, pin);
        addPinView(pin, pin.getPinClass().isAssignableFrom(PinExecute.class) ? offset - 1 : offset);
    }

    public void removeMorePinView(Pin pin) {
        Pin removePin = action.removePin(pin);
        if (removePin == null) return;
        removePin.removeLinks(actionContext);

        PinBaseView<?> pinBaseView = getPinById(pin.getId());
        ((ViewGroup) pinBaseView.getParent()).removeView(pinBaseView);

        PinBaseView<?> pinDetailView = getPinDetailById(pin.getId());
        if (pinDetailView != null) ((ViewGroup) pinDetailView.getParent()).removeView(pinDetailView);
    }

    public ActionContext getActionContext() {
        return actionContext;
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

    public PinBaseView<?> getPinDetailById(String id) {
        if (id == null || id.isEmpty()) return null;
        for (PinBaseView<?> pinBaseView : pinDetailViews) {
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
            if (new Rect(location[0], location[1], location[0] + (int) (pinBox.getWidth() * getScaleX()), location[1] + (int) (pinBox.getHeight() * getScaleY())).contains((int) rawX, (int) rawY)) {
                return pinBaseView;
            }
        }
        return null;
    }
}
