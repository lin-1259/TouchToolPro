package top.bogey.touch_tool.ui.custom;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import top.bogey.touch_tool.databinding.WidgetOutBinding;

public class OutWdiget extends NodeWidget<WidgetOutBinding>{

    public OutWdiget(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, WidgetOutBinding.class);

        inBox = binding.inBox;
        topInBox = binding.topInBox;
        bottomInBox = binding.bottomInBox;
        titleText = binding.title;
        button = binding.button;

        init(context, attrs);
    }
}
