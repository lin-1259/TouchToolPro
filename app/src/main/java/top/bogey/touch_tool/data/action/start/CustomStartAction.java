package top.bogey.touch_tool.data.action.start;

import android.os.Parcel;

import top.bogey.touch_tool.R;

public class CustomStartAction extends StartAction {
    public CustomStartAction() {
        super();
        titleId = R.string.task_type_custom;
    }

    public CustomStartAction(Parcel in) {
        super(in);
        titleId = R.string.task_type_custom;
    }
}
