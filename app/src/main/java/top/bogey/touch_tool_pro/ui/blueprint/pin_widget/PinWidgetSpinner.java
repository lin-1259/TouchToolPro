package top.bogey.touch_tool_pro.ui.blueprint.pin_widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.pin.pins.PinSpinner;
import top.bogey.touch_tool_pro.databinding.PinWidgetSpinnerBinding;
import top.bogey.touch_tool_pro.ui.blueprint.card.ActionCard;
import top.bogey.touch_tool_pro.ui.blueprint.pin.PinCustomView;
import top.bogey.touch_tool_pro.ui.blueprint.pin.PinView;
import top.bogey.touch_tool_pro.utils.SpinnerSelectedListener;

@SuppressLint("ViewConstructor")
public class PinWidgetSpinner extends PinWidget<PinSpinner> {
    private final PinWidgetSpinnerBinding binding;
    private ArrayAdapter<String> adapter;

    public PinWidgetSpinner(@NonNull Context context, ActionCard<?> card, PinView pinView, PinSpinner pinObject, boolean custom) {
        super(context, card, pinView, pinObject, custom);
        binding = PinWidgetSpinnerBinding.inflate(LayoutInflater.from(context), this, true);
        init();
    }

    @Override
    public void initBase() {
        adapter = new ArrayAdapter<>(context, R.layout.pin_widget_spinner_item);
        adapter.addAll(pinObject.getArray(context));
        binding.spinner.setAdapter(adapter);
        binding.spinner.setSelection(pinObject.getIndex());
        binding.spinner.setOnItemSelectedListener(new SpinnerSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pinObject.setIndex(position);
            }
        });
    }

    @Override
    public void initCustom() {
        binding.editTextBox.setVisibility(VISIBLE);
        binding.editText.setText(pinObject.getArrayString(context));
        binding.editTextBox.setEndIconOnClickListener(v -> {
            CharSequence s = binding.editText.getText();
            if (s != null) {
                String value = s.toString();
                String[] split = value.split("[,，]");
                pinObject.setArrays(new ArrayList<>(Arrays.asList(split)));
                ((PinCustomView) pinView).getFunctionPin().setValue(pinObject);
            }
            adapter.clear();
            adapter.addAll(pinObject.getArray(context));
            binding.spinner.setSelection(pinObject.getIndex());
        });
    }
}
