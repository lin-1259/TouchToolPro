package top.bogey.touch_tool.ui.card.pin_widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.action.pin.PinSpinnerHelper;
import top.bogey.touch_tool.databinding.PinWidgetSpinnerBinding;
import top.bogey.touch_tool.ui.custom.BindingView;

public class PinWidgetSpinner extends BindingView<PinWidgetSpinnerBinding> {
    private final PinSpinnerHelper helper;

    public PinWidgetSpinner(@NonNull Context context, PinSpinnerHelper helper) {
        this(context, null, helper);
    }

    public PinWidgetSpinner(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, new PinSpinnerHelper(new String[0]));
    }

    public PinWidgetSpinner(@NonNull Context context, @Nullable AttributeSet attrs, PinSpinnerHelper helper) {
        super(context, attrs, PinWidgetSpinnerBinding.class);
        if (helper == null) throw new RuntimeException("不是有效的引用");
        this.helper = helper;

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.pin_widget_spinner_item);
        adapter.addAll(helper.getArrays(context));
        binding.spinner.setAdapter(adapter);
        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                helper.setIndex(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.spinner.setSelection(helper.getIndex());
    }
}
