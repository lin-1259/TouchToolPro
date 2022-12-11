package top.bogey.touch_tool.ui.card.start;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.action.start.ManualStartAction;
import top.bogey.touch_tool.databinding.CardManualStartBinding;
import top.bogey.touch_tool.ui.app.AppView;
import top.bogey.touch_tool.ui.card.BaseCard;

@SuppressLint("ViewConstructor")
public class ManualStartCard extends BaseCard<CardManualStartBinding, ManualStartAction> {

    public ManualStartCard(@NonNull Context context, Task task, ManualStartAction action) {
        this(context, null, task, action);
    }

    public ManualStartCard(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, null, null);
    }

    public ManualStartCard(@NonNull Context context, @Nullable AttributeSet attrs, Task task, ManualStartAction action) {
        super(context, attrs, CardManualStartBinding.class, task, action);
        if (action != null) {
            binding.selectAppWidget.setPackages(action.getPackages(), AppView.MULTI_MODE);
        }
    }
}
