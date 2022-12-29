package top.bogey.touch_tool.data.action.start;

import android.os.Parcel;

import top.bogey.touch_tool.R;

public class CustomStartAction extends StartAction {
    public CustomStartAction() {
        super();
        titleId = R.string.action_custom_start_title;
    }

    public CustomStartAction(Parcel in) {
        super(in);
        titleId = R.string.action_custom_start_title;
    }
}
