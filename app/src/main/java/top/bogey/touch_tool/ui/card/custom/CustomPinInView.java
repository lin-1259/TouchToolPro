package top.bogey.touch_tool.ui.card.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.text.Editable;
import android.util.ArrayMap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.action.function.BaseFunction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinMap;
import top.bogey.touch_tool.data.pin.object.PinObject;
import top.bogey.touch_tool.databinding.PinCustomInBinding;
import top.bogey.touch_tool.ui.card.pin.PinBaseView;
import top.bogey.touch_tool.utils.TextChangedListener;

@SuppressLint("ViewConstructor")
public class CustomPinInView extends PinBaseView<PinCustomInBinding> {
    private PinObject pinObject;
    private final ArrayMap<Class<? extends PinObject>, Integer> map = PinMap.getInstance().getMap();

    private final int[][] states = new int[2][];

    public CustomPinInView(@NonNull Context context, CustomCard card, Pin pin) {
        super(context, PinCustomInBinding.class, card, pin);
        initRemoveButton(binding.removeButton);
        setValueView();

        states[0] = new int[] {android.R.attr.state_focused};
        states[1] = new int[] {};

        pinObject = pin.getValue();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.pin_widget_spinner_item);
        for (Integer id : map.values()) {
            adapter.add(context.getString(id));
        }
        binding.spinner.setAdapter(adapter);
        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Class<? extends PinObject> aClass = map.keyAt(position);
                // 类型一样，直接返回
                if (pinObject != null && aClass.equals(pinObject.getClass())) return;

                try {
                    pinObject = aClass.newInstance();
                    ((BaseFunction) card.getActionContext()).setPinValue(card.getAction(), pin, pinObject);
                    setValueView();
                    refreshPinUI();
                } catch (IllegalAccessException | InstantiationException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.editText.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s != null) {
                    if (s.toString().equals(pin.getTitle(context))) return;
                    ((BaseFunction) card.getActionContext()).setPinTitle(card.getAction(), pin, s.toString());
                }
            }
        });

        refreshPinUI();
    }

    @Override
    public void refreshPinUI() {
        int[] colors = new int[] {getPinColor(), getPinColor()};
        binding.editTextBox.setBoxStrokeColorStateList(new ColorStateList(states, colors));

        if (map != null) {
            binding.spinner.setSelection(map.indexOfKey(pin.getPinClass()));
            if (!binding.editText.hasFocus() && pin.getTitle(getContext()) != null) {
                binding.editText.setText(pin.getTitle(getContext()));
            }
        }
    }

    @Override
    public int[] getSlotLocationOnScreen(float scale) {
        return null;
    }

    @Override
    public View getSlotBox() {
        return null;
    }

    @Override
    public ViewGroup getPinBox() {
        return binding.pinBox;
    }
}
