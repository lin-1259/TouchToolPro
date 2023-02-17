package top.bogey.touch_tool.ui.card.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.util.ArrayMap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.PinMap;
import top.bogey.touch_tool.data.pin.PinSlotType;
import top.bogey.touch_tool.data.pin.object.PinExecute;
import top.bogey.touch_tool.data.pin.object.PinObject;
import top.bogey.touch_tool.databinding.ViewCardCustomOutBinding;
import top.bogey.touch_tool.ui.custom.BindingView;

@SuppressLint("ViewConstructor")
public class CustomCardOutItem extends BindingView<ViewCardCustomOutBinding> {
    private PinObject pinObject;

    public CustomCardOutItem(@NonNull Context context, ViewGroup parent, Pin pin) {
        super(context, ViewCardCustomOutBinding.class);

        binding.removeButton.setOnClickListener(v -> parent.removeView(this));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.pin_widget_spinner_item);
        ArrayMap<Class<? extends PinObject>, Integer> map = PinMap.getInstance().getMap();
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
                    binding.pinSlot.setStrokeColor(pinObject.getPinColor(context));
                } catch (IllegalAccessException | InstantiationException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if (pin != null) {
            pinObject = pin.getValue();
            binding.spinner.setSelection(map.indexOfKey(pin.getPinClass()));
            binding.pinSlot.setStrokeColor(pinObject.getPinColor(context));
            binding.editText.setText(pin.getTitle());
        } else {
            binding.spinner.setSelection(0);
        }
    }

    public Pin getValue() {
        String title = "";
        Editable text = binding.editText.getText();
        if (text != null) title = text.toString();
        return new Pin(pinObject, title, PinDirection.OUT, pinObject.getClass().equals(PinExecute.class) ? PinSlotType.SINGLE : PinSlotType.MULTI);
    }
}
