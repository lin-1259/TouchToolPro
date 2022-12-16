package top.bogey.touch_tool.data.action.start;

import android.content.Context;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.ActionTag;
import top.bogey.touch_tool.data.action.pin.Pin;
import top.bogey.touch_tool.data.action.pin.PinSelectAppHelper;
import top.bogey.touch_tool.data.action.pin.PinType;

public class ManualStartAction extends StartAction {
    private final Pin<PinSelectAppHelper> appPin;

    public ManualStartAction() {
        super(ActionTag.START_MANUAL);
        appPin = addPin(new Pin<>(PinType.APP, new PinSelectAppHelper(PinSelectAppHelper.MULTI_MODE)));
        addPin(restartPin);
        titleId = R.string.task_type_manual;
    }

    @Override
    public boolean checkReady(WorldState worldState, Task task) {
        CharSequence packageName = worldState.getPackageName();
        if (packageName == null) return false;

        PinSelectAppHelper helper = appPin.getValue();
        Map<CharSequence, List<CharSequence>> packages = helper.getPackages();
        List<CharSequence> activityClasses = packages.get(packageName);
        if (activityClasses == null) return false;

        return activityClasses.isEmpty() || activityClasses.contains(worldState.getActivityName());
    }
}
