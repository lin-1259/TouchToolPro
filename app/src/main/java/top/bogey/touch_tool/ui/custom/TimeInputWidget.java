package top.bogey.touch_tool.ui.custom;

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
import top.bogey.touch_tool.databinding.WidgetTimeInputBinding;
import top.bogey.touch_tool.utils.TextChangedListener;

public class TimeInputWidget extends BindingView<WidgetTimeInputBinding> {
    private TimeInputWatcher watcher = null;
    private TimeUnit unit = TimeUnit.MILLISECONDS;

    public TimeInputWidget(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, WidgetTimeInputBinding.class);
        binding.timeUnit.setSelection(unitToIndex(unit));

        binding.lockButton.addOnCheckedChangeListener((button, isChecked) -> {
            button.setIconResource(isChecked ? R.drawable.icon_lock : R.drawable.icon_unlock);
            if (isChecked) binding.maxEdit.setText(binding.maxEdit.getText());
            binding.maxLayout.setEnabled(!isChecked);
            binding.maxEdit.setText(binding.minEdit.getText());
        });
        binding.lockButton.setChecked(true);

        binding.minEdit.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                if (binding.lockButton.isChecked()) {
                    binding.maxEdit.setText(s);
                } else {
                    notifyWatcher();
                }
            }
        });

        binding.maxEdit.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                notifyWatcher();
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.widget_spinner_item);

        for (String s : getResources().getStringArray(R.array.time_unit)) {
            adapter.add(s);
        }
        binding.timeUnit.setAdapter(adapter);
        binding.timeUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                unit = indexToUnit(position);
                notifyWatcher();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public TimeInputWidget(@NonNull Context context, TimeInputWatcher watcher) {
        this(context, (AttributeSet) null);
        this.watcher = watcher;
    }

    public void setTime(int min, int max, TimeUnit unit) {
        binding.minEdit.setText(String.valueOf(min));
        binding.maxEdit.setText(String.valueOf(max));
        binding.timeUnit.setSelection(unitToIndex(unit));
        binding.lockButton.setChecked(min == max);
    }

    public void setWatcher(TimeInputWatcher watcher) {
        this.watcher = watcher;
    }

    private void notifyWatcher() {
        if (watcher == null) return;
        Editable minEdit = binding.minEdit.getText();
        Editable maxEdit = binding.maxEdit.getText();
        int min = 0, max = 0;
        if (minEdit != null && minEdit.length() > 0)
            min = Integer.parseInt(String.valueOf(minEdit));
        if (maxEdit != null && maxEdit.length() > 0)
            max = Integer.parseInt(String.valueOf(maxEdit));
        watcher.newTime(min, max, unit);
    }

    private int unitToIndex(TimeUnit unit) {
        return Math.max(0, unit.ordinal() - 2);
    }

    private TimeUnit indexToUnit(int index) {
        if (index > TimeUnit.values().length) return TimeUnit.MILLISECONDS;
        return TimeUnit.values()[index + 2];
    }

    public interface TimeInputWatcher {
        void newTime(int min, int max, TimeUnit unit);
    }
}
