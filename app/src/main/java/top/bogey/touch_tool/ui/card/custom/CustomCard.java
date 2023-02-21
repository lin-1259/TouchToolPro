package top.bogey.touch_tool.ui.card.custom;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;

import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.action.function.FunctionAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.ui.card.BaseCard;
import top.bogey.touch_tool.ui.task_blueprint.CardLayoutView;

@SuppressLint("ViewConstructor")
public class CustomCard extends BaseCard<FunctionAction> {

    public CustomCard(@NonNull Context context, Task task, FunctionAction action) {
        super(context, task, action);
        binding.copyButton.setOnClickListener(v -> {
            if (!action.getTag().isStart()) {
                ((CardLayoutView) getParent()).addAction(action.copy());
            }
        });
    }

    @Override
    public void addMorePinView(Pin pin, int offset) {

    }

    @Override
    public void addPinView(Pin pin, int offset) {

    }


}
