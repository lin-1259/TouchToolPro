package top.bogey.touch_tool.ui.card.action;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.action.TimeArea;
import top.bogey.touch_tool.data.action.action.DelayAction;
import top.bogey.touch_tool.databinding.CardDelayBinding;
import top.bogey.touch_tool.ui.card.BaseCard;

public class DelayActionCard extends BaseCard<CardDelayBinding, DelayAction> {

    public DelayActionCard(@NonNull Context context, Task task, DelayAction action) {
        this(context, null, task, action);
    }

    public DelayActionCard(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, null, null);
    }

    public DelayActionCard(@NonNull Context context, @Nullable AttributeSet attrs, Task task, DelayAction action) {
        super(context, attrs, CardDelayBinding.class, task, action);

        binding.timeInputWidget.setWatcher((min, max, unit) -> action.getDelay().setTime(min, max, unit));

        if (action != null) {
            TimeArea delay = action.getDelay();
            binding.timeInputWidget.setTime(delay.getMin(), delay.getMax(), delay.getUnit());
        }
    }
}
