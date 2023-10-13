package top.bogey.touch_tool_pro.ui.setting;

import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.base.LogInfo;

public class RuntimeLogInfo extends LogInfo {
    private final int x;
    private final int y;
    private final Class<? extends Action> actionClass;

    public RuntimeLogInfo(int index, String log, Action action) {
        super(index, log);
        x = action.getX();
        y = action.getY();
        actionClass = action.getClass();
    }

    @Override
    public String getLogString() {
        return super.getLogString() + "(" + x + "," + y + ")";
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Class<? extends Action> getActionClass() {
        return actionClass;
    }
}
