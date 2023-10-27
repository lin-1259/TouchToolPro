package top.bogey.touch_tool_pro.ui.picker;

import android.app.ActivityManager;
import android.content.Context;
import android.view.MotionEvent;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.ui.MainActivity;
import top.bogey.touch_tool_pro.utils.easy_float.EasyFloat;
import top.bogey.touch_tool_pro.utils.easy_float.FloatCallback;

public class FloatBaseCallback implements FloatCallback {
    @Override
    public void onCreate(boolean succeed) {

    }

    @Override
    public void onShow(String tag) {
        EasyFloat.hideAll(tag);
        MainActivity activity = MainApplication.getInstance().getMainActivity();
        if (activity != null) {
            activity.moveTaskToBack(true);
        }
    }

    @Override
    public void onHide() {

    }

    @Override
    public void onDismiss() {
        if (!EasyFloat.showLast()) {
            MainActivity activity = MainApplication.getInstance().getMainActivity();
            if (activity != null) {
                ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
                manager.moveTaskToFront(activity.getTaskId(), ActivityManager.MOVE_TASK_NO_USER_ACTION);
            }
        }
    }

    @Override
    public void onTouch(MotionEvent event) {

    }

    @Override
    public void onDrag(MotionEvent event) {

    }

    @Override
    public void onDragEnd() {

    }
}
