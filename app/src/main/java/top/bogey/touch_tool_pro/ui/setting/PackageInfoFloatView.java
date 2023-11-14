package top.bogey.touch_tool_pro.ui.setting;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.databinding.FloatPackageInfoViewBinding;
import top.bogey.touch_tool_pro.service.EnterActivityListener;
import top.bogey.touch_tool_pro.service.MainAccessibilityService;
import top.bogey.touch_tool_pro.utils.easy_float.EasyFloat;
import top.bogey.touch_tool_pro.utils.easy_float.FloatViewInterface;

@SuppressLint("ViewConstructor")
public class PackageInfoFloatView extends FrameLayout implements FloatViewInterface, EnterActivityListener {
    private final FloatPackageInfoViewBinding binding;
    private final PackageInfoRecyclerViewAdapter adapter;

    private float lastY = 0f;
    private boolean isToBottom = false;
    private boolean isToTop = true;

    @SuppressLint("ClickableViewAccessibility")
    public PackageInfoFloatView(@NonNull Context context) {
        super(context);
        binding = FloatPackageInfoViewBinding.inflate(LayoutInflater.from(context), this, true);

        binding.closeButton.setOnClickListener(v -> {
            dismiss();
            SettingView.resetSwitchState();
        });

        adapter = new PackageInfoRecyclerViewAdapter();
        binding.recyclerView.setAdapter(adapter);

        binding.recyclerView.setOnTouchListener((v, event) -> {
            ViewParent parent = getParent();
            if (parent != null) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN -> {
                        lastY = event.getY();
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                    case MotionEvent.ACTION_MOVE -> {
                        checkPosition(event.getY());
                        if (isToBottom || isToTop) {
                            parent.requestDisallowInterceptTouchEvent(false);
                            return false;
                        } else {
                            parent.requestDisallowInterceptTouchEvent(true);
                        }
                        lastY = event.getY();
                    }
                    case MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> parent.requestDisallowInterceptTouchEvent(false);
                }
            }
            return false;
        });

        MainAccessibilityService service = MainApplication.getInstance().getService();
        if (service != null) {
            service.addEnterListener(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        MainAccessibilityService service = MainApplication.getInstance().getService();
        if (service != null) {
            service.removeEnterListener(this);
        }
        super.onDetachedFromWindow();
    }

    private void checkPosition(float nowY) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) binding.recyclerView.getLayoutManager();
        if (layoutManager != null) {
            if (layoutManager.getItemCount() > 3) {
                isToTop = false;
                isToBottom = false;
                int first = layoutManager.findFirstCompletelyVisibleItemPosition();
                int last = layoutManager.findLastCompletelyVisibleItemPosition();

                if (layoutManager.getChildCount() > 0) {
                    if (last == layoutManager.getItemCount() - 1) {
                        if (canScrollVertically(-1) && nowY < lastY) {
                            isToBottom = true;
                        }
                    } else if (first == 0) {
                        if (canScrollVertically(1) && nowY > lastY) {
                            isToTop = true;
                        }
                    }
                }
            } else {
                isToTop = true;
                isToBottom = true;
            }
        }
    }

    @Override
    public void show() {
        EasyFloat.with(MainApplication.getInstance().getService())
                .setLayout(this)
                .setTag(PackageInfoFloatView.class.getName())
                .setDragEnable(true)
                .setAnimator(null)
                .show();
    }

    @Override
    public void dismiss() {
        EasyFloat.dismiss(PackageInfoFloatView.class.getName());
    }

    @Override
    public void onEnterActivity(String packageName, String activityName) {
        adapter.addPackageInfo(packageName, activityName);
    }
}
