package top.bogey.touch_tool.ui.card.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.action.function.BaseFunction;
import top.bogey.touch_tool.data.action.function.FunctionAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.object.PinExecute;
import top.bogey.touch_tool.data.pin.object.PinString;
import top.bogey.touch_tool.databinding.CardCustomBinding;
import top.bogey.touch_tool.ui.blueprint.CardLayoutView;
import top.bogey.touch_tool.ui.card.BaseCard;
import top.bogey.touch_tool.ui.card.pin.PinBaseView;
import top.bogey.touch_tool.ui.card.pin.PinBottomView;
import top.bogey.touch_tool.ui.card.pin.PinTopView;

@SuppressLint("ViewConstructor")
public class CustomCard extends BaseCard<FunctionAction> {

    public CustomCard(@NonNull Context context, BaseFunction baseFunction, FunctionAction action) {
        super(context, baseFunction, action);
        binding.copyButton.setOnClickListener(v -> {
            if (!action.getTag().isStart()) {
                ((CardLayoutView) getParent()).addAction(action.copy());
            }
        });

        binding.removeButton.setOnClickListener(v -> {
            if (!action.getTag().isStart()) {
                ArrayList<BaseAction> actions = baseFunction.getActionsByClass(FunctionAction.class);
                // 最少一个开始一个结束动作，多余的结束动作都能删除
                if (actions.size() > 2) {
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
                }
            }
        });

        binding.editButton.setVisibility(action.getTag().isStart() ? VISIBLE : GONE);

        action.setCallback(new FunctionAction.FunctionChangedCallback() {

            @Override
            public void onPinAdded(Pin pin) {
                CustomCard.this.onPinAdded(pin);
            }

            @Override
            public void onPinRemoved(Pin pin) {
                CustomCard.this.onPinRemoved(pin);
            }

            @Override
            public void onPinTitleChanged(Pin pin) {
                PinBaseView<?> pinBaseView = getPinById(pin.getId());
                if (pinBaseView != null) pinBaseView.refreshPinUI();
            }
        });

        CardCustomBinding cardBinding = CardCustomBinding.inflate(LayoutInflater.from(context), action.getTag().isStart() ? binding.topBox : binding.bottomBox, true);
        cardBinding.addPinButton.setOnClickListener(v -> {
            Pin pin;
            // 这个pin是添加到BaseFunction的，所以方向与动作方向一致，与动作内针脚方向相反
            if (action.getTag().isStart()) pin = new Pin(new PinString());
            else pin = new Pin(new PinString(), PinDirection.OUT);
            baseFunction.addPin(pin);
        });

        cardBinding.addExecuteButton.setOnClickListener(v -> {
            Pin pin;
            // 这个pin是添加到BaseFunction的，所以方向与动作方向一致，与动作内针脚方向相反
            if (action.getTag().isStart()) pin = new Pin(new PinExecute());
            else pin = new Pin(new PinExecute(), PinDirection.OUT);
            baseFunction.addPin(pin);
        });

        cardBinding.justCallSwitch.setChecked(baseFunction.isJustCall());
        cardBinding.justCallSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> baseFunction.setJustCall(isChecked));

        cardBinding.fastEndSwitch.setChecked(baseFunction.isFastEnd());
        cardBinding.fastEndSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> baseFunction.setFastEnd(isChecked));

        cardBinding.stateBox.setVisibility(action.getTag().isStart() ? VISIBLE : GONE);
        cardBinding.fastEndBox.setVisibility(action.getTag().isStart() ? GONE : VISIBLE);
    }

    @Override
    public void removeMorePinView(Pin pin) {
        String pinUid = action.getPinUidMap().get(pin.getUid());
        BaseFunction function = (BaseFunction) actionContext;
        function.removePin(null, function.getPinByUid(pinUid));
    }

    private void onPinAdded(Pin pin) {
        addPinView(pin, 0);
    }

    private void onPinRemoved(Pin pin) {
        pin.removeLinks(actionContext);

        PinBaseView<?> pinBaseView = getPinById(pin.getId());
        if (pinBaseView != null) ((ViewGroup) pinBaseView.getParent()).removeView(pinBaseView);
    }

    @Override
    public void addPinView(Pin pin, int offset) {
        PinBaseView<?> pinBaseView;
        if (pin.getDirection() == PinDirection.IN) {
            if (pin.isVertical()) {
                pinBaseView = new PinTopView(getContext(), this, pin);
                binding.topBox.addView(pinBaseView, binding.topBox.getChildCount() - offset);
            } else {
                pinBaseView = new CustomPinInView(getContext(), this, pin);
                binding.inBox.addView(pinBaseView, binding.inBox.getChildCount() - offset);
            }
        } else {
            if (pin.isVertical()) {
                pinBaseView = new PinBottomView(getContext(), this, pin);
                binding.bottomBox.addView(pinBaseView, binding.bottomBox.getChildCount() - offset);
            } else {
                pinBaseView = new CustomPinOutView(getContext(), this, pin);
                binding.outBox.addView(pinBaseView, binding.outBox.getChildCount() - offset);
            }
        }

        pinBaseView.setExpand(action.showDetail);
        pinBaseViews.add(pinBaseView);
    }
}
