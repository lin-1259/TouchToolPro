package top.bogey.touch_tool.data.action;

import android.os.Parcel;

import top.bogey.touch_tool.data.action.BaseAction;

public class NormalAction extends BaseAction {

    public NormalAction() {
        super();
        addPin(inPin);
        addPin(outPin);
    }

    public NormalAction(Parcel in) {
        super(in);
        inPin = addPin(pinsTmp.remove(0));
        outPin = addPin(pinsTmp.remove(0));
    }
}
