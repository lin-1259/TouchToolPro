package top.bogey.touch_tool.ui.card.pin_widget;

import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.TimeUnit;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.action.TimeArea;
import top.bogey.touch_tool.databinding.PinWidgetTimeAreaBinding;
import top.bogey.touch_tool.ui.custom.BindingView;
import top.bogey.touch_tool.utils.TextChangedListener;

public class PinWidgetTimeArea extends BindingView<PinWidgetTimeAreaBinding> {
    private final TimeArea timeArea;

    public PinWidgetTimeArea(@NonNull Context context, TimeArea timeArea) {
        this(context, null, timeArea);
    }

    public PinWidgetTimeArea(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, new TimeArea(300, TimeUnit.MILLISECONDS));
    }

    public PinWidgetTimeArea(@NonNull Context context, @Nullable AttributeSet attrs, TimeArea timeArea) {
        super(context, attrs, PinWidgetTimeAreaBinding.class);
        if (timeArea == null) throw new RuntimeException("不是有效的引用");
        this.timeArea = timeArea;

        binding.timeUnit.setSelection(unitToIndex(timeArea.getUnit()));

        binding.lockButton.addOnCheckedChangeListener((button, isChecked) -> {
            button.setIconResource(isChecked ? R.drawable.icon_lock : R.drawable.icon_unlock);
            if (isChecked) binding.maxEdit.setText(binding.maxEdit.getText());
            binding.maxLayout.setEnabled(!isChecked);
            binding.maxEdit.setText(binding.minEdit.getText());
        });
        binding.lockButton.setChecked(timeArea.getMin() == timeArea.getMax());

        binding.minEdit.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                if (binding.lockButton.isChecked()) {
                    binding.maxEdit.setText(s);
                } else {
                    setTimeAreaValue();
                }
            }
        });

        binding.maxEdit.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                setTimeAreaValue();
            }
        });
        binding.minEdit.setText(String.valueOf(timeArea.getMin()));
        binding.maxEdit.setText(String.valueOf(timeArea.getMax()));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.pin_widget_spinner_item);
        adapter.addAll(getResources().getStringArray(R.array.time_unit));
        binding.timeUnit.setAdapter(adapter);
        binding.timeUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                timeArea.setUnit(indexToUnit(position));
                setTimeAreaValue();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.timeUnit.setSelection(unitToIndex(timeArea.getUnit()));
    }

    private void setTimeAreaValue() {
        Editable minEdit = binding.minEdit.getText();
        Editable maxEdit = binding.maxEdit.getText();
        int min = 0, max = 0;
        if (minEdit != null && minEdit.length() > 0)
            min = Integer.parseInt(String.valueOf(minEdit));
        timeArea.setMin(min);
        if (maxEdit != null && maxEdit.length() > 0)
            max = Integer.parseInt(String.valueOf(maxEdit));
        timeArea.setMax(max);
        TimeUnit unit = indexToUnit(binding.timeUnit.getSelectedItemPosition());
        timeArea.setUnit(unit);
    }

    private int unitToIndex(TimeUnit unit) {
        return Math.max(0, unit.ordinal() - 2);
    }

    private TimeUnit indexToUnit(int index) {
        if (index > TimeUnit.values().length) return TimeUnit.MILLISECONDS;
        return TimeUnit.values()[index + 2];
    }
}
