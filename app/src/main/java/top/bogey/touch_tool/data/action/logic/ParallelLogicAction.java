package top.bogey.touch_tool.data.action.logic;

import android.content.Context;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import top.bogey.touch_tool.MainAccessibilityService;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.NormalAction;
import top.bogey.touch_tool.data.action.start.InnerStartAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.PinSlotType;
import top.bogey.touch_tool.data.pin.object.PinAdd;
import top.bogey.touch_tool.data.pin.object.PinExecute;
import top.bogey.touch_tool.data.pin.object.PinInteger;
import top.bogey.touch_tool.utils.TaskRunningCallback;

public class ParallelLogicAction extends NormalAction {
    private transient final Pin conditionPin;
    private transient final Pin timeOutPin;
    private transient final Pin completePin;
    private transient final Pin falsePin;

    public ParallelLogicAction(Context context) {
        super(context, R.string.action_parallel_logic_title);
        conditionPin = addPin(new Pin(new PinInteger(1), context.getString(R.string.action_parallel_logic_subtitle_condition)));
        timeOutPin = addPin(new Pin(new PinInteger(5000), context.getString(R.string.action_parallel_logic_subtitle_timeout)));

        addPin(new Pin(new PinExecute(), context.getString(R.string.action_subtitle_execute), PinDirection.OUT));
        Pin executePin = new Pin(new PinExecute(), context.getString(R.string.action_subtitle_execute), PinDirection.OUT);
        addPin(new Pin(new PinAdd(executePin, 3), context.getString(R.string.action_subtitle_add_pin), PinDirection.OUT, PinSlotType.EMPTY));

        completePin = addPin(new Pin(new PinExecute(), context.getString(R.string.action_parallel_logic_subtitle_complete), PinDirection.OUT));
        falsePin = addPin(new Pin(new PinExecute(), context.getString(R.string.action_logic_subtitle_false), PinDirection.OUT));
    }

    public ParallelLogicAction(JsonObject jsonObject) {
        super(jsonObject);
        conditionPin = addPin(tmpPins.remove(0));
        timeOutPin = addPin(tmpPins.remove(0));

        int size = tmpPins.size() - 2;
        for (int i = 0; i < size; i++) {
            addPin(tmpPins.remove(0));
        }

        completePin = addPin(tmpPins.remove(0));
        falsePin = addPin(tmpPins.remove(0));
    }

    @Override
    public void doAction(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        ArrayList<Pin> pins = getPins();
        ArrayList<Pin> subPins = new ArrayList<>();
        subPins.add(outPin);
        for (int i = pins.indexOf(timeOutPin) + 1; i < pins.size() - 3; i++) {
            subPins.add(pins.get(i));
        }

        PinInteger condition = (PinInteger) getPinValue(actionContext, conditionPin);
        PinInteger timeout = (PinInteger) getPinValue(actionContext, timeOutPin);

        MainAccessibilityService service = MainApplication.getService();
        CountDownLatch latch = new CountDownLatch(condition.getValue() > 0 ? condition.getValue() : 1);
        ArrayList<TaskRunnable> runnableList = new ArrayList<>();
        for (Pin subPin : subPins) {
            TaskRunnable taskRunnable = service.runTask(runnable.getTask(), new InnerStartAction(service, subPin), new TaskRunningCallback() {
                @Override
                public void onStart(TaskRunnable runnable) {}

                @Override
                public void onEnd(TaskRunnable runnable) {
                    latch.countDown();
                }

                @Override
                public void onProgress(TaskRunnable runnable, int progress) {}
            });

            runnableList.add(taskRunnable);
        }
        try {
            boolean result = latch.await(timeout.getValue(), TimeUnit.MILLISECONDS);
            if (result) {
                doNextAction(runnable, actionContext, completePin);
            } else {
                doNextAction(runnable, actionContext, falsePin);
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            runnableList.forEach(TaskRunnable::stop);
        }
    }
}
