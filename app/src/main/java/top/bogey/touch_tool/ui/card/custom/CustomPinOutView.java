package top.bogey.touch_tool.ui.card.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.util.ArrayMap;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinMap;
import top.bogey.touch_tool.data.pin.object.PinObject;
import top.bogey.touch_tool.databinding.PinCustomOutBinding;
import top.bogey.touch_tool.ui.card.pin.PinBaseView;
import top.bogey.touch_tool.utils.TextChangedListener;

@SuppressLint("ViewConstructor")
public class CustomPinOutView extends PinBaseView<PinCustomOutBinding> {
    private PinObject pinObject;

    public CustomPinOutView(@NonNull Context context, CustomCard card, Pin pin) {
        super(context, PinCustomOutBinding.class, card, pin);
        pinObject = pin.getValue();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.pin_widget_spinner_item);
        ArrayMap<Class<? extends PinObject>, Integer> map = PinMap.getInstance().getMap();
        for (Integer id : map.values()) {
            adapter.add(context.getString(id));
        }
        binding.spinner.setAdapter(adapter);
        binding.spinner.setSelection(map.indexOfKey(pin.getPinClass()));
        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Class<? extends PinObject> aClass = map.keyAt(position);
                // 类型一样，直接返回
                if (pinObject != null && aClass.equals(pinObject.getClass())) return;

                try {
                    pinObject = aClass.newInstance();
                    card.getAction().setPinValue(pin, pinObject);
                    refreshPinUI();
                } catch (IllegalAccessException | InstantiationException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.editText.setText(pin.getTitle());
        binding.editText.addTextChangedListener(new TextChangedListener(){
            @Override
            public void afterTextChanged(Editable s) {
                if (s != null) card.getAction().setPinTitle(pin, s.toString());
            }
        });
    }

    @Override
    protected void setValueView() {

    }

    @Override
    public void refreshPinUI() {
        super.refreshPinUI();
        pinBox.setVisibility(VISIBLE);
        pinSlot.setStrokeColor(getPinColor());
    }

    @Override
    public int[] getSlotLocationOnScreen(float scale) {
        int[] location = new int[2];
        pinSlot.getLocationOnScreen(location);
        location[0] += (pinSlot.getWidth() * scale);
        location[1] += (pinSlot.getHeight() * scale / 2);
        return location;
    }
}
