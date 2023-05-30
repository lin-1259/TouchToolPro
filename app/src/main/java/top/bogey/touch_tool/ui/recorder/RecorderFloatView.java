package top.bogey.touch_tool.ui.recorder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Iterator;

import top.bogey.touch_tool.MainAccessibilityService;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.action.NormalAction;
import top.bogey.touch_tool.data.action.action.DelayAction;
import top.bogey.touch_tool.data.action.action.LogAction;
import top.bogey.touch_tool.data.action.action.TouchNodeAction;
import top.bogey.touch_tool.data.action.action.TouchPathAction;
import top.bogey.touch_tool.data.action.action.TouchPosAction;
import top.bogey.touch_tool.data.action.function.BaseFunction;
import top.bogey.touch_tool.data.action.function.FunctionAction;
import top.bogey.touch_tool.data.action.logic.WaitConditionLogicAction;
import top.bogey.touch_tool.data.action.start.InnerStartAction;
import top.bogey.touch_tool.data.action.state.ColorStateAction;
import top.bogey.touch_tool.data.action.state.ImageStateAction;
import top.bogey.touch_tool.data.action.state.StateAction;
import top.bogey.touch_tool.data.action.state.TextStateAction;
import top.bogey.touch_tool.data.action.state.XPathWidgetStateAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinColor;
import top.bogey.touch_tool.data.pin.object.PinImage;
import top.bogey.touch_tool.data.pin.object.PinPath;
import top.bogey.touch_tool.data.pin.object.PinString;
import top.bogey.touch_tool.data.pin.object.PinValueArea;
import top.bogey.touch_tool.data.pin.object.PinXPath;
import top.bogey.touch_tool.databinding.FloatRecorderBinding;
import top.bogey.touch_tool.ui.picker.BasePickerFloatView;
import top.bogey.touch_tool.ui.picker.ImagePickerFloatPreview;
import top.bogey.touch_tool.ui.picker.PickerCallback;
import top.bogey.touch_tool.ui.picker.TextPickerFloatPreview;
import top.bogey.touch_tool.ui.picker.WidgetPickerFloatPreview;
import top.bogey.touch_tool.utils.FloatBaseCallback;
import top.bogey.touch_tool.utils.TaskRunningCallback;
import top.bogey.touch_tool.utils.easy_float.EasyFloat;

@SuppressLint("ViewConstructor")
public class RecorderFloatView extends BasePickerFloatView {
    private final FloatRecorderBinding binding;
    private final QuickRecordFloatView quickRecordFloatView;
    private final ArrayList<RecorderStep> steps = new ArrayList<>();
    private final ArrayList<RecorderStep> history = new ArrayList<>();

    private final MainAccessibilityService service;

    private long delayStartTime;

    public RecorderFloatView(@NonNull Context context, PickerCallback pickerCallback, BaseFunction function) {
        super(context, pickerCallback);
        quickRecordFloatView = new QuickRecordFloatView(context, this);
        binding = FloatRecorderBinding.inflate(LayoutInflater.from(context), this, true);
        service = MainApplication.getInstance().getService();

        binding.closeButton.setOnClickListener(v -> dismiss());

        binding.saveButton.setOnClickListener(v -> {
            addActionsToFunction(function);
            if (pickerCallback != null) pickerCallback.onComplete();
            dismiss();
        });

        binding.backButton.setOnClickListener(v -> addHistory());

        binding.nextButton.setOnClickListener(v -> removeHistory());

        binding.countText.setText(String.valueOf(steps.size()));

        binding.widgetButton.setOnClickListener(v -> {
            long delay = System.currentTimeMillis() - delayStartTime;
            PinXPath pinXPath = new PinXPath();
            new WidgetPickerFloatPreview(context, () -> {
                RecorderStep recorderStep = new RecorderStep();
                recorderStep.addTouchNodeAction((int) delay, pinXPath);
                addStep(recorderStep);
                runRecorderStep(recorderStep);
            }, pinXPath).show();
        });

        binding.imageButton.setOnClickListener(v -> {
            long delay = System.currentTimeMillis() - delayStartTime;
            PinImage pinImage = new PinImage();
            new ImagePickerFloatPreview(context, () -> {
                RecorderStep recorderStep = new RecorderStep();
                recorderStep.addTouchPosAction((int) delay, pinImage);
                addStep(recorderStep);
                runRecorderStep(recorderStep);
            }, pinImage).show();
        });

        binding.colorButton.setOnClickListener(v -> {
            long delay = System.currentTimeMillis() - delayStartTime;
            PinColor pinColor = new PinColor();
            new ImagePickerFloatPreview(context, () -> {
                RecorderStep recorderStep = new RecorderStep();
                recorderStep.addTouchPosAction((int) delay, pinColor);
                addStep(recorderStep);
                runRecorderStep(recorderStep);
            }, pinColor).show();
        });

        binding.textButton.setOnClickListener(v -> {
            long delay = System.currentTimeMillis() - delayStartTime;
            PinString pinString = new PinString();
            new TextPickerFloatPreview(context, () -> {
                RecorderStep recorderStep = new RecorderStep();
                recorderStep.addTouchNodeAction((int) delay, pinString.getValue());
                addStep(recorderStep);
                runRecorderStep(recorderStep);
            }, pinString).show();
        });

        binding.logButton.setOnClickListener(v -> {
            PinString pinString = new PinString();
            new TextPickerFloatPreview(context, () -> {
                RecorderStep recorderStep = new RecorderStep();
                recorderStep.addLogAction(pinString.getValue());
                addStep(recorderStep);
                runRecorderStep(recorderStep);
            }, pinString).show();
        });
    }

    public void addTouchStep(PinPath path, int time) {
        long delay = System.currentTimeMillis() - delayStartTime;
        RecorderStep recorderStep = new RecorderStep();
        recorderStep.addTouchPathAction((int) delay, path, time);
        addStep(recorderStep);
        runRecorderStep(recorderStep);
    }

    private void addStep(RecorderStep step) {
        steps.add(step);
        binding.backButton.setVisibility(VISIBLE);
        binding.countText.setText(String.valueOf(steps.size()));

        history.clear();
        binding.nextButton.setVisibility(GONE);
    }

    private void runRecorderStep(RecorderStep step) {
        if (service != null && service.isServiceEnabled()) {
            Task task = new Task();
            InnerStartAction innerStartAction = null;
            for (BaseAction action : step.getActions()) {
                if (action instanceof DelayAction) {
                    innerStartAction = new InnerStartAction(((DelayAction) action).getOutPin());
                }
                task.addAction(action);
            }
            if (innerStartAction == null) {
                delayStartTime = System.currentTimeMillis();
                return;
            }
            task.addAction(innerStartAction);

            EasyFloat.hide(tag);
            InnerStartAction finalInnerStartAction = innerStartAction;
            postDelayed(() -> service.runTask(task, finalInnerStartAction, new TaskRunningCallback() {
                @Override
                public void onStart(TaskRunnable runnable) {

                }

                @Override
                public void onEnd(TaskRunnable runnable) {
                    delayStartTime = System.currentTimeMillis();
                    post(() -> EasyFloat.show(tag));
                }

                @Override
                public void onProgress(TaskRunnable runnable, int progress) {

                }
            }), 100);
        }
    }

    private void addHistory() {
        RecorderStep step = steps.remove(steps.size() - 1);
        history.add(step);
        binding.nextButton.setVisibility(VISIBLE);
        binding.backButton.setVisibility(steps.isEmpty() ? GONE : VISIBLE);
        binding.countText.setText(String.valueOf(steps.size()));
    }

    private void removeHistory() {
        RecorderStep step = history.remove(history.size() - 1);
        steps.add(step);
        binding.backButton.setVisibility(VISIBLE);
        binding.countText.setText(String.valueOf(steps.size()));
        binding.nextButton.setVisibility(history.isEmpty() ? GONE : VISIBLE);
    }

    private void addActionsToFunction(BaseFunction function) {
        if (steps.isEmpty()) return;

        FunctionAction start = null, end = null;
        for (BaseAction action : function.getActionsByClass(FunctionAction.class)) {
            FunctionAction functionAction = (FunctionAction) action;
            if (functionAction.getTag().isStart()) start = functionAction;
            else end = functionAction;
        }
        if (start == null || end == null) return;

        int stepIndex = setActionPosition(start, 0);

        Iterator<RecorderStep> iterator = steps.iterator();
        RecorderStep step = iterator.next();
        addLink(step.getInPin(), start.getExecutePin());
        stepIndex = setStepPosition(function, step, stepIndex);

        while (iterator.hasNext()) {
            RecorderStep nextStep = iterator.next();
            addLink(step.getOutPin(), nextStep.getInPin());
            stepIndex = setStepPosition(function, step, stepIndex);
            step = nextStep;
        }

        addLink(step.getOutPin(), end.getExecutePin());
        setActionPosition(end, stepIndex);
    }

    private int setStepPosition(BaseFunction function, RecorderStep step, int index) {
        for (BaseAction action : step.getActions()) {
            index = setActionPosition(action, index);
            function.addAction(action);
        }
        return index;
    }

    private int setActionPosition(BaseAction action, int index) {
        final int MAIN_X = 21;
        final int STATE_X = 1;
        final int START_Y = 1;
        final int OFFSET_Y = 20;

        action.y = START_Y + index * OFFSET_Y;
        if (action instanceof StateAction) {
            action.x = STATE_X;
        } else if (action instanceof NormalAction || action instanceof FunctionAction) {
            action.x = MAIN_X;
            index++;
        }
        return index;
    }

    private static void addLink(Pin pin1, Pin pin2) {
        pin1.addLink(pin2);
        pin2.addLink(pin1);
    }

    @Override
    public void show() {
        quickRecordFloatView.show();
        EasyFloat.with(MainApplication.getInstance().getService())
                .setLayout(this)
                .setTag(tag)
                .setDragEnable(true)
                .setCallback(new RecorderFloatCallback())
                .setAnimator(null)
                .show();
    }

    private static class RecorderStep {
        private final ArrayList<BaseAction> actions = new ArrayList<>();
        private Pin inPin;
        private Pin outPin;

        private DelayAction addDelayAction(int delay) {
            DelayAction delayAction = new DelayAction();

            Pin delayPin = delayAction.getDelayPin();
            ((PinValueArea) delayPin.getValue()).setCurrMin(delay);
            ((PinValueArea) delayPin.getValue()).setCurrMax(delay);

            actions.add(delayAction);
            return delayAction;
        }

        public void addTouchPathAction(int delay, PinPath path, int time) {
            DelayAction delayAction = addDelayAction(delay);
            inPin = delayAction.getInPin();

            TouchPathAction touchPathAction = new TouchPathAction();
            outPin = touchPathAction.getOutPin();

            Pin pathPin = touchPathAction.getPathPin();
            pathPin.setValue(path);

            Pin timePin = touchPathAction.getTimePin();
            ((PinValueArea) timePin.getValue()).setCurrMin(time);
            ((PinValueArea) timePin.getValue()).setCurrMax(time);

            addLink(delayAction.getOutPin(), touchPathAction.getInPin());

            actions.add(touchPathAction);
        }

        public void addTouchNodeAction(int delay, String text) {
            DelayAction delayAction = addDelayAction(delay);
            inPin = delayAction.getInPin();

            TextStateAction textStateAction = new TextStateAction();
            ((PinString) textStateAction.getTextPin().getValue()).setValue(text);

            WaitConditionLogicAction waitConditionLogicAction = new WaitConditionLogicAction();

            TouchNodeAction touchNodeAction = new TouchNodeAction();
            outPin = touchNodeAction.getOutPin();

            addLink(waitConditionLogicAction.getInPin(), delayAction.getOutPin());
            addLink(textStateAction.getStatePin(), waitConditionLogicAction.getConditionPin());
            addLink(textStateAction.getNodePin(), touchNodeAction.getNodePin());
            addLink(waitConditionLogicAction.getOutPin(), touchNodeAction.getInPin());

            actions.add(textStateAction);
            actions.add(waitConditionLogicAction);
            actions.add(touchNodeAction);
        }

        public void addTouchNodeAction(int delay, PinXPath xPath) {
            DelayAction delayAction = addDelayAction(delay);
            inPin = delayAction.getInPin();

            XPathWidgetStateAction xPathWidgetStateAction = new XPathWidgetStateAction();
            xPathWidgetStateAction.getxPathPin().setValue(xPath);

            WaitConditionLogicAction waitConditionLogicAction = new WaitConditionLogicAction();

            TouchNodeAction touchNodeAction = new TouchNodeAction();
            outPin = touchNodeAction.getOutPin();

            addLink(waitConditionLogicAction.getInPin(), delayAction.getOutPin());
            addLink(xPathWidgetStateAction.getStatePin(), waitConditionLogicAction.getConditionPin());
            addLink(xPathWidgetStateAction.getNodePin(), touchNodeAction.getNodePin());
            addLink(waitConditionLogicAction.getOutPin(), touchNodeAction.getInPin());

            actions.add(xPathWidgetStateAction);
            actions.add(waitConditionLogicAction);
            actions.add(touchNodeAction);
        }

        public void addTouchPosAction(int delay, PinImage image) {
            DelayAction delayAction = addDelayAction(delay);
            inPin = delayAction.getInPin();

            ImageStateAction imageStateAction = new ImageStateAction();
            imageStateAction.getImagePin().setValue(image);

            WaitConditionLogicAction waitConditionLogicAction = new WaitConditionLogicAction();

            TouchPosAction touchPosAction = new TouchPosAction();
            outPin = touchPosAction.getOutPin();

            addLink(waitConditionLogicAction.getInPin(), delayAction.getOutPin());
            addLink(imageStateAction.getStatePin(), waitConditionLogicAction.getConditionPin());
            addLink(imageStateAction.getPosPin(), touchPosAction.getPosPin());
            addLink(waitConditionLogicAction.getOutPin(), touchPosAction.getInPin());

            actions.add(imageStateAction);
            actions.add(waitConditionLogicAction);
            actions.add(touchPosAction);
        }

        public void addTouchPosAction(int delay, PinColor color) {
            DelayAction delayAction = addDelayAction(delay);
            inPin = delayAction.getInPin();

            ColorStateAction colorStateAction = new ColorStateAction();
            colorStateAction.getColorPin().setValue(color);

            WaitConditionLogicAction waitConditionLogicAction = new WaitConditionLogicAction();

            TouchPosAction touchPosAction = new TouchPosAction();
            outPin = touchPosAction.getOutPin();

            addLink(waitConditionLogicAction.getInPin(), delayAction.getOutPin());
            addLink(colorStateAction.getStatePin(), waitConditionLogicAction.getConditionPin());
            addLink(colorStateAction.getPosPin(), touchPosAction.getPosPin());
            addLink(waitConditionLogicAction.getOutPin(), touchPosAction.getInPin());

            actions.add(colorStateAction);
            actions.add(waitConditionLogicAction);
            actions.add(touchPosAction);
        }

        public void addLogAction(String log) {
            LogAction logAction = new LogAction();
            inPin = logAction.getInPin();
            outPin = logAction.getOutPin();
            ((PinString) logAction.getTextPin().getValue()).setValue(log);

            actions.add(logAction);
        }

        public ArrayList<BaseAction> getActions() {
            return actions;
        }

        public Pin getInPin() {
            return inPin;
        }

        public Pin getOutPin() {
            return outPin;
        }
    }

    private class RecorderFloatCallback extends FloatBaseCallback {

        @Override
        public void onShow(String tag) {
            super.onShow(tag);
            EasyFloat.show(quickRecordFloatView.getTag());
        }

        @Override
        public void onHide() {
            super.onHide();
            EasyFloat.hide(quickRecordFloatView.getTag());
        }

        @Override
        public void onDismiss() {
            quickRecordFloatView.dismiss();
            super.onDismiss();
        }
    }
}
