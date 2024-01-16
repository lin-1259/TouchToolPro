package top.bogey.touch_tool_pro.ui.blueprint.pin_widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.array.ArrayAction;
import top.bogey.touch_tool_pro.bean.action.function.FunctionInnerAction;
import top.bogey.touch_tool_pro.bean.action.function.FunctionReferenceAction;
import top.bogey.touch_tool_pro.bean.action.var.GetVariableValue;
import top.bogey.touch_tool_pro.bean.action.var.SetVariableValue;
import top.bogey.touch_tool_pro.bean.pin.PinType;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValueArray;
import top.bogey.touch_tool_pro.databinding.PinWidgetArrayBinding;
import top.bogey.touch_tool_pro.ui.blueprint.CardLayoutView;
import top.bogey.touch_tool_pro.ui.blueprint.card.ActionCard;
import top.bogey.touch_tool_pro.ui.blueprint.pin.PinView;
import top.bogey.touch_tool_pro.utils.SpinnerSelectedListener;

@SuppressLint("ViewConstructor")
public class PinWidgetArray extends PinWidget<PinValueArray> {
    private final PinWidgetArrayBinding binding;
    private final ArrayList<PinType> pinTypes = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    public PinWidgetArray(@NonNull Context context, ActionCard<?> card, PinView pinView, PinValueArray pinObject, boolean custom) {
        super(context, card, pinView, pinObject, custom);
        binding = PinWidgetArrayBinding.inflate(LayoutInflater.from(context), this, true);
        init();
    }

    @Override
    public void initBase() {
        adapter = new ArrayAdapter<>(context, R.layout.pin_widget_spinner_item);
        for (PinType pinType : PinType.values()) {
            if (pinType.getConfig().isCanCustom() && pinType != PinType.VALUE_ARRAY && pinType != PinType.VALUE) {
                pinTypes.add(pinType);
                adapter.add(pinType.getConfig().getTitle());
            }
        }
        binding.spinner.setAdapter(adapter);
        binding.spinner.setOnItemSelectedListener(new SpinnerSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                PinType pinType = pinTypes.get(position);
                if (pinType == pinObject.getPinType()) return;
                Action action = card.getAction();
                if (action instanceof ArrayAction arrayAction) {
                    arrayAction.setValueType(card.getFunctionContext(), pinType);
                    card.refreshPinView();
                } else if (action instanceof GetVariableValue || action instanceof SetVariableValue) {
                    pinObject.setPinType(pinType);
                    ((CardLayoutView) card.getParent()).refreshVariableAction(pinView.getPin().getTitle(), pinObject);
                } else if (action instanceof FunctionInnerAction) {
                    pinObject.setPinType(pinType);
                    pinView.getPin().cleanLinks(card.getFunctionContext());
                    pinView.refreshPinView();
                }
            }
        });
        binding.spinner.setSelection(pinTypes.indexOf(pinObject.getPinType()));
    }

    @Override
    public void initCustom() {

    }
}
