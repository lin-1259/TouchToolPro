package top.bogey.touch_tool.ui.custom;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import top.bogey.touch_tool.databinding.WidgetInBinding;

public class InWdiget extends NodeWidget<WidgetInBinding>{

    public InWdiget(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, WidgetInBinding.class);

        inBox = binding.inBox;
        topInBox = binding.topInBox;
        bottomInBox = binding.bottomInBox;
        titleText = binding.title;
        button = binding.button;

        init(context, attrs);
    }
}
