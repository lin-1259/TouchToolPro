package top.bogey.touch_tool_pro.ui.picker;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.ui.MainActivity;
import top.bogey.touch_tool_pro.ui.custom.KeepAliveFloatView;
import top.bogey.touch_tool_pro.utils.easy_float.EasyFloat;
import top.bogey.touch_tool_pro.utils.easy_float.FloatCallback;

public class FloatBaseCallback implements FloatCallback {
    private int taskId;

    @Override
    public void onCreate(boolean succeed) {
        Log.d("TAG", "onCreate: ");
    }

    @Override
    public void onShow(String tag) {
        Log.d("TAG", "onShow: ");
        EasyFloat.hideAll(tag);
        MainActivity activity = MainApplication.getInstance().getMainActivity();
        if (activity != null) {
            taskId = activity.getTaskId();
            activity.moveTaskToBack(true);
        }
    }

    @Override
    public void onHide() {
        Log.d("TAG", "onHide: ");
    }

    @Override
    public void onDismiss() {
        Log.d("TAG", "onDismiss: ");
        if (!EasyFloat.showLast()) {
            KeepAliveFloatView keepView = MainApplication.getInstance().getKeepView();
            if (keepView != null) {
                ActivityManager manager = (ActivityManager) keepView.getContext().getSystemService(Context.ACTIVITY_SERVICE);
                try {
                    manager.moveTaskToFront(taskId, ActivityManager.MOVE_TASK_NO_USER_ACTION);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onTouch(MotionEvent event) {
        Log.d("TAG", "onTouch: ");
    }

    @Override
    public void onDrag(MotionEvent event) {
        Log.d("TAG", "onDrag: ");

    }

    @Override
    public void onDragEnd() {
        Log.d("TAG", "onDragEnd: ");
    }
}
