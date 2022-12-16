package top.bogey.touch_tool.data.action.start;

import android.content.Context;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.action.ActionTag;

public class CustomStartAction extends StartAction {
    public CustomStartAction() {
        super(ActionTag.START_CUSTOM);
        titleId = R.string.task_type_custom;
    }
}
