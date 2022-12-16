package top.bogey.touch_tool.ui.card.pin_widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.action.pin.PinArrayHelper;
import top.bogey.touch_tool.databinding.PinWidgetCheckboxBinding;
import top.bogey.touch_tool.databinding.PinWidgetSpinnerBinding;
import top.bogey.touch_tool.ui.custom.BindingView;

public class PinWidgetCheckBox extends BindingView<PinWidgetCheckboxBinding> {
    private final AtomicBoolean atomicBoolean;

    public PinWidgetCheckBox(@NonNull Context context, AtomicBoolean atomicBoolean) {
        this(context, null, atomicBoolean);
    }

    public PinWidgetCheckBox(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, new AtomicBoolean(false));
    }

    public PinWidgetCheckBox(@NonNull Context context, @Nullable AttributeSet attrs, AtomicBoolean atomicBoolean) {
        super(context, attrs, PinWidgetCheckboxBinding.class);
        if (atomicBoolean == null) throw new RuntimeException("不是有效的引用");
        this.atomicBoolean = atomicBoolean;

        binding.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> atomicBoolean.set(isChecked));
        binding.checkBox.setChecked(atomicBoolean.get());
    }
}
