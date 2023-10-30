package top.bogey.touch_tool_pro.bean.action.logic;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.ActionMorePinInterface;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.action.normal.NormalAction;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinAdd;
import top.bogey.touch_tool_pro.bean.pin.pins.PinExecute;
import top.bogey.touch_tool_pro.bean.pin.pins.PinInteger;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.ui.custom.KeepAliveFloatView;
import top.bogey.touch_tool_pro.ui.custom.ManualChoiceFloatView;

public class ManualChoiceAction extends NormalAction implements ActionMorePinInterface {
    private transient Pin outTimePin = new Pin(new PinInteger(60000), R.string.action_wait_condition_logic_subtitle_timeout);
    private transient Pin secondPin = new Pin(new PinExecute(), R.string.pin_execute, true);
    private final transient Pin morePin = new Pin(new PinExecute(), R.string.pin_execute, true);
    private transient Pin addPin = new Pin(new PinAdd(morePin), R.string.action_subtitle_add_execute);

    public ManualChoiceAction() {
        super(ActionType.LOGIC_MANUAL_CHOICE);
        outTimePin = addPin(outTimePin);
        secondPin = addPin(secondPin);
        addPin = addPin(addPin);
    }

    public ManualChoiceAction(JsonObject jsonObject) {
        super(jsonObject);
        outTimePin = reAddPin(outTimePin);
        secondPin = reAddPin(secondPin);
        reAddPin(morePin, 1);
        addPin = reAddPin(addPin);
    }

    @Override
    public void execute(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinInteger timeout = (PinInteger) getPinValue(runnable, context, outTimePin);

        ArrayList<Pin> pins = calculateMorePins();
        ArrayList<String> items = new ArrayList<>();
        for (Pin executePin : pins) {
            Action nextAction = getNextAction(context, executePin);
            if (nextAction != null) {
                items.add(nextAction.getValidDescription());
            }
        }

        AtomicInteger nextIndex = new AtomicInteger(0);
        AtomicReference<ManualChoiceFloatView> floatView = new AtomicReference<>();

        KeepAliveFloatView keepView = MainApplication.getInstance().getKeepView();
        if (keepView != null) {
            new Handler(Looper.getMainLooper()).post(() -> {
                ManualChoiceFloatView view = new ManualChoiceFloatView(keepView.getContext(), items, index -> {
                    if (index < 0) {
                        runnable.stop();
                    } else {
                        nextIndex.set(index);
                        runnable.resume();
                    }
                });
                floatView.set(view);
                view.show();
            });

            runnable.pause(timeout.getValue());
            floatView.get().dismiss();
        }

        executeNext(runnable, context, pins.get(nextIndex.get()));
    }

    private static Action getNextAction(FunctionContext context, Pin pin) {
        Pin linkedPin = pin.getLinkedPin(context);
        if (linkedPin == null) return null;
        return context.getActionById(linkedPin.getActionId());
    }

    @Override
    public ArrayList<Pin> calculateMorePins() {
        ArrayList<Pin> pins = new ArrayList<>();
        pins.add(outPin);
        boolean start = false;
        for (Pin pin : getPins()) {
            if (pin == secondPin) start = true;
            if (pin == addPin) start = false;
            if (start) pins.add(pin);
        }
        return pins;
    }
}
