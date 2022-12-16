package top.bogey.touch_tool.data.action.action;

import top.bogey.touch_tool.data.action.BaseAction;

public class NormalAction extends BaseAction {

    public NormalAction(String tag) {
        super(tag);
        addPin(inPin);
        addPin(outPin);
    }
}
