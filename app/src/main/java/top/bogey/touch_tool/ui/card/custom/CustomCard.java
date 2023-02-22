package top.bogey.touch_tool.ui.card.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.action.function.BaseFunction;
import top.bogey.touch_tool.data.action.function.FunctionAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.PinSlotType;
import top.bogey.touch_tool.data.pin.PinSubType;
import top.bogey.touch_tool.data.pin.object.PinExecute;
import top.bogey.touch_tool.data.pin.object.PinString;
import top.bogey.touch_tool.databinding.CardCustomBinding;
import top.bogey.touch_tool.ui.card.BaseCard;
import top.bogey.touch_tool.ui.card.pin.PinBaseView;
import top.bogey.touch_tool.ui.card.pin.PinBottomView;
import top.bogey.touch_tool.ui.card.pin.PinTopView;
import top.bogey.touch_tool.ui.blueprint.CardLayoutView;
import top.bogey.touch_tool.utils.AppUtils;

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

        CardCustomBinding cardBinding = CardCustomBinding.inflate(LayoutInflater.from(context), action.getTag().isStart() ? binding.topBox : binding.bottomBox, true);
        cardBinding.addButton.setOnClickListener(v -> {
            Pin pin;
            // 结束动作需要添加进入针脚
            if (!action.getTag().isStart()) pin = new Pin(new PinString(), PinSlotType.SINGLE, true);
            // 开始动作需要添加输出针脚
            else pin = new Pin(new PinString(), null, PinDirection.OUT, PinSlotType.MULTI, PinSubType.NORMAL, true);
            addPinView(action.addPin(pin), 0);
        });

        cardBinding.enableSwitch.setChecked(baseFunction.isJustCall());
        cardBinding.enableSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> baseFunction.setJustCall(isChecked));
        cardBinding.stateBox.setVisibility(action.getTag().isStart() ? VISIBLE : GONE);
    }

    @Override
    public void addMorePinView(Pin pin, int offset) {
        addPinView(pin, offset);
    }

    @Override
    public void addPinView(Pin pin, int offset) {
        PinBaseView<?> pinBaseView = null;
        if (pin.getDirection() == PinDirection.IN) {
            if (pin.getPinClass().isAssignableFrom(PinExecute.class)) {
                pinBaseView = new PinTopView(getContext(), this, pin);
                binding.topBox.addView(pinBaseView, binding.topBox.getChildCount() - offset);
            } else {
                pinBaseView = new CustomPinInView(getContext(), this, pin);
                binding.inBox.addView(pinBaseView, binding.inBox.getChildCount() - offset);
            }
        } else if (pin.getDirection() == PinDirection.OUT) {
            if (pin.getPinClass().isAssignableFrom(PinExecute.class)) {
                pinBaseView = new PinBottomView(getContext(), this, pin);
                binding.bottomBox.addView(pinBaseView, binding.bottomBox.getChildCount() - offset);
            } else {
                pinBaseView = new CustomPinOutView(getContext(), this, pin);
                binding.outBox.addView(pinBaseView, binding.outBox.getChildCount() - offset);
            }
        }
        pinBaseViews.add(pinBaseView);
    }
}
