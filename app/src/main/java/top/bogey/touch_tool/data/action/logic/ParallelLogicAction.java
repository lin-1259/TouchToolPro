package top.bogey.touch_tool.data.action.logic;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import top.bogey.touch_tool.service.MainAccessibilityService;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.NormalAction;
import top.bogey.touch_tool.data.action.start.InnerStartAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.object.PinAdd;
import top.bogey.touch_tool.data.pin.object.PinExecute;
import top.bogey.touch_tool.data.pin.object.PinInteger;
import top.bogey.touch_tool.utils.TaskRunningCallback;

public class ParallelLogicAction extends NormalAction {
    private transient Pin conditionPin = new Pin(new PinInteger(1), R.string.action_parallel_logic_subtitle_condition);
    private transient Pin timeOutPin = new Pin(new PinInteger(5000), R.string.action_parallel_logic_subtitle_timeout);

    private transient Pin secondExcutePin = new Pin(new PinExecute(), R.string.action_subtitle_execute, PinDirection.OUT);
    private final transient Pin executePin = new Pin(new PinExecute(), R.string.action_subtitle_execute, PinDirection.OUT);
    private transient Pin addPin = new Pin(new PinAdd(executePin, 3), R.string.action_subtitle_add_pin, PinDirection.OUT);

    private transient Pin completePin = new Pin(new PinExecute(), R.string.action_parallel_logic_subtitle_complete, PinDirection.OUT);
    private transient Pin falsePin = new Pin(new PinExecute(), R.string.action_logic_subtitle_false, PinDirection.OUT);

    public ParallelLogicAction() {
        super(R.string.action_parallel_logic_title);
        conditionPin = addPin(conditionPin);
        timeOutPin = addPin(timeOutPin);
        secondExcutePin = addPin(secondExcutePin);
        addPin = addPin(addPin);
        completePin = addPin(completePin);
        falsePin = addPin(falsePin);
    }

    public ParallelLogicAction(JsonObject jsonObject) {
        super(R.string.action_parallel_logic_title, jsonObject);
        conditionPin = reAddPin(conditionPin);
        timeOutPin = reAddPin(timeOutPin);
        secondExcutePin = reAddPin(secondExcutePin);
        reAddPin(executePin, 3);
        addPin = reAddPin(addPin);
        completePin = reAddPin(completePin);
        falsePin = reAddPin(falsePin);
    }

    @Override
    public void doAction(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        ArrayList<Pin> pins = getPins();
        ArrayList<Pin> subPins = new ArrayList<>();
        subPins.add(outPin);
        for (int i = pins.indexOf(secondExcutePin); i < pins.size() - 3; i++) {
            subPins.add(pins.get(i));
        }

        PinInteger condition = (PinInteger) getPinValue(runnable, actionContext, conditionPin);
        PinInteger timeout = (PinInteger) getPinValue(runnable, actionContext, timeOutPin);

        MainAccessibilityService service = MainApplication.getInstance().getService();
        CountDownLatch latch = new CountDownLatch(condition.getValue() > 0 ? condition.getValue() : 1);
        ArrayList<TaskRunnable> runnableList = new ArrayList<>();
        for (Pin subPin : subPins) {
            TaskRunnable taskRunnable = service.runTask(runnable.getStartTask(), new InnerStartAction(subPin), actionContext, new TaskRunningCallback() {
                @Override
                public void onStart(TaskRunnable runnable) {}

                @Override
                public void onEnd(TaskRunnable runnable) {
                    latch.countDown();
                }

                @Override
                public void onProgress(TaskRunnable run, int progress) {
                    if (runnable.isInterrupt()) throw new RuntimeException("并行中断");
                }
            });

            runnableList.add(taskRunnable);
        }
        try {
            boolean result = latch.await(timeout.getValue(), TimeUnit.MILLISECONDS);
            runnableList.forEach(TaskRunnable::stop);
            if (result) {
                doNextAction(runnable, actionContext, completePin);
            } else {
                doNextAction(runnable, actionContext, falsePin);
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
