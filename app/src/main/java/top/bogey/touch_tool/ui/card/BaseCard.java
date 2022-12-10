package top.bogey.touch_tool.ui.card;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.ui.custom.BindingView;

@SuppressLint("ViewConstructor")
public class BaseCard<T extends ViewBinding, A extends BaseAction> extends BindingView<T> {
    protected final Task task;
    protected A action;

    public BaseCard(@NonNull Context context, Class<T> tClass, Task task, A action) {
        this(context, null, tClass, task, action);
    }

    public BaseCard(@NonNull Context context, @Nullable AttributeSet attrs, Class<T> tClass, Task task, A action) {
        super(context, attrs, tClass);
        this.task = task;
        this.action = action;

        if (!(binding.getRoot() instanceof HelperCard)) throw new RuntimeException("未使用CardHelper");
        HelperCard helperCard = (HelperCard) binding.getRoot();
        helperCard.setCopyCallback(result -> {

        });

        helperCard.setRemoveCallback(result -> {

        });

        helperCard.setEnableCallback(result -> {
            if (action.isEnable() == result) return;
            action.setEnable(result);
        });
        if (action != null) {
            helperCard.setEnable(action.isEnable());
        }
    }
}
