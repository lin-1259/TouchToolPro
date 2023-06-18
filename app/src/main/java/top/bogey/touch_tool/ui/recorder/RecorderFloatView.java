package top.bogey.touch_tool.ui.recorder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.service.MainAccessibilityService;
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
    private static final int MAIN_X = 21;
    private static final int STATE_X = 1;
    private static final int START_Y = 1;
    private static final int OFFSET_Y = 20;

    private final FloatRecorderBinding binding;
    private final QuickRecordFloatView quickRecordFloatView;
    private final ArrayList<RecorderStep> steps = new ArrayList<>();
    private final ArrayList<RecorderStep> history = new ArrayList<>();

    private final MainAccessibilityService service;

    private long delayStartTime;
    private long pauseStartTime;
    private boolean recording = false;

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

        binding.recodeButton.setOnClickListener(v -> {
            if (recording) {
                pauseStartTime = System.currentTimeMillis();
            } else {
                if (pauseStartTime > 0) {
                    // 恢复延迟偏移
                    delayStartTime = System.currentTimeMillis() - pauseStartTime + delayStartTime;
                }
            }
            recording = !recording;
            binding.recodeButton.setChecked(recording);
            showQuickTouch(recording);
        });

        binding.backButton.setOnClickListener(v -> addHistory());

        binding.nextButton.setOnClickListener(v -> removeHistory());

        binding.countText.setText(String.valueOf(steps.size()));

        binding.widgetButton.setOnClickListener(v -> {
            if (isStopRecord()) return;
            PinXPath pinXPath = new PinXPath();
            new WidgetPickerFloatPreview(context, () -> {
                RecorderStep recorderStep = new RecorderStep();
                recorderStep.addTouchNodeAction(getDelay(), pinXPath);
                addStep(recorderStep);
            }, pinXPath).show();
        });

        binding.imageButton.setOnClickListener(v -> {
            if (isStopRecord()) return;
            PinImage pinImage = new PinImage();
            new ImagePickerFloatPreview(context, () -> {
                RecorderStep recorderStep = new RecorderStep();
                recorderStep.addTouchPosAction(getDelay(), pinImage);
                addStep(recorderStep);
            }, pinImage).show();
        });

        binding.colorButton.setOnClickListener(v -> {
            if (isStopRecord()) return;
            PinColor pinColor = new PinColor();
            new ImagePickerFloatPreview(context, () -> {
                RecorderStep recorderStep = new RecorderStep();
                recorderStep.addTouchPosAction(getDelay(), pinColor);
                addStep(recorderStep);
            }, pinColor).show();
        });

        binding.textButton.setOnClickListener(v -> {
            if (isStopRecord()) return;
            PinString pinString = new PinString();
            new TextPickerFloatPreview(context, () -> {
                RecorderStep recorderStep = new RecorderStep();
                recorderStep.addTouchNodeAction(getDelay(), pinString.getValue());
                addStep(recorderStep);
            }, pinString).show();
        });

        binding.logButton.setOnClickListener(v -> {
            if (isStopRecord()) return;
            PinString pinString = new PinString();
            new TextPickerFloatPreview(context, () -> {
                RecorderStep recorderStep = new RecorderStep();
                recorderStep.addLogAction(pinString.getValue());
                addStep(recorderStep);
            }, pinString).show();
        });
    }

    private int getDelay() {
        if (delayStartTime == 0) return 0;
        return (int) (System.currentTimeMillis() - delayStartTime);
    }

    public void showQuickTouch(boolean show) {
        if (show && recording) {
            EasyFloat.show(quickRecordFloatView.getTag());
        } else {
            EasyFloat.hide(quickRecordFloatView.getTag());
        }
    }

    private boolean isStopRecord() {
        if (recording) return false;
        Toast.makeText(getContext(), R.string.function_record_tips, Toast.LENGTH_SHORT).show();
        return true;
    }

    public void addTouchStep(PinPath path, int time) {
        if (isStopRecord()) return;

        RecorderStep recorderStep = new RecorderStep();
        recorderStep.addTouchPathAction(getDelay(), path, time);
        addStep(recorderStep);
    }

    private void addStep(RecorderStep step) {
        steps.add(step);
        binding.backButton.setVisibility(VISIBLE);
        binding.countText.setText(String.valueOf(steps.size()));

        history.clear();
        binding.nextButton.setVisibility(GONE);
        runRecorderStep(step);
    }

    private void runRecorderStep(RecorderStep step) {
        if (service != null && service.isServiceEnabled()) {
            Task task = new Task();
            InnerStartAction innerStartAction = null;
            for (BaseAction action : step.getActions()) {
                if (action instanceof NormalAction) {
                    HashMap<String, String> links = ((NormalAction) action).getInPin().getLinks();
                    if (links.size() == 0) {
                        innerStartAction = new InnerStartAction(((NormalAction) action).getOutPin());
                    }
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

                @Override
                public void onAction(TaskRunnable runnable, ActionContext context, BaseAction action) {

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
        delayStartTime = System.currentTimeMillis();
    }

    private void removeHistory() {
        RecorderStep step = history.remove(history.size() - 1);
        steps.add(step);
        binding.backButton.setVisibility(VISIBLE);
        binding.countText.setText(String.valueOf(steps.size()));
        binding.nextButton.setVisibility(history.isEmpty() ? GONE : VISIBLE);
        delayStartTime = System.currentTimeMillis();
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
        // 首个录制的动作移除延迟
        step.detachDelayAction();
        addLink(step.getInPin(), start.getExecutePin());
        stepIndex = setStepPosition(function, step, stepIndex);

        while (iterator.hasNext()) {
            RecorderStep nextStep = iterator.next();
            addLink(step.getOutPin(), nextStep.getInPin());
            stepIndex = setStepPosition(function, nextStep, stepIndex);
            step = nextStep;
        }

        addLink(step.getOutPin(), end.getExecutePin());
        setActionPosition(end, stepIndex);
    }

    private int setStepPosition(BaseFunction function, RecorderStep step, int index) {
        for (BaseAction action : step.getActions()) {
            index = setActionPosition(action, index);
            action.showDetail = false;
            function.addAction(action);
        }
        return index;
    }

    private int setActionPosition(BaseAction action, int index) {
        action.y = START_Y + index * OFFSET_Y;
        if (action instanceof StateAction) {
            action.x = STATE_X;
            action.y += 3;
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
        if (EasyFloat.getView(tag) != null) {
            EasyFloat.show(tag);
            return;
        }

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

        private void attachDelayAction(int delay) {
            if (delay == 0) return;
            DelayAction delayAction = new DelayAction();

            Pin delayPin = delayAction.getDelayPin();
            ((PinValueArea) delayPin.getValue()).setCurrMin(delay);
            ((PinValueArea) delayPin.getValue()).setCurrMax(delay);

            addLink(delayAction.getOutPin(), inPin);
            inPin = delayAction.getInPin();

            actions.add(0, delayAction);
        }

        public void detachDelayAction() {
            for (BaseAction action : actions) {
                if (action instanceof DelayAction) {
                    Pin delayOutPin = ((DelayAction) action).getOutPin();
                    inPin = delayOutPin.getLinkedPin(actions);
                    if (inPin != null) {
                        actions.remove(action);
                        return;
                    }
                }
            }
        }

        public void addTouchPathAction(int delay, PinPath path, int time) {
            TouchPathAction touchPathAction = new TouchPathAction();
            inPin = touchPathAction.getInPin();
            outPin = touchPathAction.getOutPin();

            Pin pathPin = touchPathAction.getPathPin();
            pathPin.setValue(path);

            Pin timePin = touchPathAction.getTimePin();
            ((PinValueArea) timePin.getValue()).setCurrMin(time);
            ((PinValueArea) timePin.getValue()).setCurrMax(time);

            actions.add(touchPathAction);
            attachDelayAction(delay);
        }

        public void addTouchNodeAction(int delay, String text) {
            TextStateAction textStateAction = new TextStateAction();
            ((PinString) textStateAction.getTextPin().getValue()).setValue(text);

            WaitConditionLogicAction waitConditionLogicAction = new WaitConditionLogicAction();
            inPin = waitConditionLogicAction.getInPin();

            TouchNodeAction touchNodeAction = new TouchNodeAction();
            outPin = touchNodeAction.getOutPin();

            addLink(textStateAction.getStatePin(), waitConditionLogicAction.getConditionPin());
            addLink(textStateAction.getNodePin(), touchNodeAction.getNodePin());
            addLink(waitConditionLogicAction.getOutPin(), touchNodeAction.getInPin());

            actions.add(textStateAction);
            actions.add(waitConditionLogicAction);
            actions.add(touchNodeAction);

            attachDelayAction(delay);
        }

        public void addTouchNodeAction(int delay, PinXPath xPath) {
            XPathWidgetStateAction xPathWidgetStateAction = new XPathWidgetStateAction();
            xPathWidgetStateAction.getxPathPin().setValue(xPath);

            WaitConditionLogicAction waitConditionLogicAction = new WaitConditionLogicAction();
            inPin = waitConditionLogicAction.getInPin();

            TouchNodeAction touchNodeAction = new TouchNodeAction();
            outPin = touchNodeAction.getOutPin();

            addLink(xPathWidgetStateAction.getStatePin(), waitConditionLogicAction.getConditionPin());
            addLink(xPathWidgetStateAction.getNodePin(), touchNodeAction.getNodePin());
            addLink(waitConditionLogicAction.getOutPin(), touchNodeAction.getInPin());

            actions.add(xPathWidgetStateAction);
            actions.add(waitConditionLogicAction);
            actions.add(touchNodeAction);

            attachDelayAction(delay);
        }

        public void addTouchPosAction(int delay, PinImage image) {
            ImageStateAction imageStateAction = new ImageStateAction();
            imageStateAction.getImagePin().setValue(image);

            WaitConditionLogicAction waitConditionLogicAction = new WaitConditionLogicAction();
            inPin = waitConditionLogicAction.getInPin();

            TouchPosAction touchPosAction = new TouchPosAction();
            outPin = touchPosAction.getOutPin();

            addLink(imageStateAction.getStatePin(), waitConditionLogicAction.getConditionPin());
            addLink(imageStateAction.getPosPin(), touchPosAction.getPosPin());
            addLink(waitConditionLogicAction.getOutPin(), touchPosAction.getInPin());

            actions.add(imageStateAction);
            actions.add(waitConditionLogicAction);
            actions.add(touchPosAction);

            attachDelayAction(delay);
        }

        public void addTouchPosAction(int delay, PinColor color) {
            ColorStateAction colorStateAction = new ColorStateAction();
            colorStateAction.getColorPin().setValue(color);

            WaitConditionLogicAction waitConditionLogicAction = new WaitConditionLogicAction();
            inPin = waitConditionLogicAction.getInPin();

            TouchPosAction touchPosAction = new TouchPosAction();
            outPin = touchPosAction.getOutPin();

            addLink(colorStateAction.getStatePin(), waitConditionLogicAction.getConditionPin());
            addLink(colorStateAction.getPosPin(), touchPosAction.getPosPin());
            addLink(waitConditionLogicAction.getOutPin(), touchPosAction.getInPin());

            actions.add(colorStateAction);
            actions.add(waitConditionLogicAction);
            actions.add(touchPosAction);

            attachDelayAction(delay);
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
            showQuickTouch(true);
        }

        @Override
        public void onHide() {
            super.onHide();
            showQuickTouch(false);
        }

        @Override
        public void onDismiss() {
            quickRecordFloatView.dismiss();
            super.onDismiss();
        }
    }
}
