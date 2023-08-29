package top.bogey.touch_tool_pro.utils.easy_float;

import android.accessibilityservice.AccessibilityService;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.EditText;

import top.bogey.touch_tool_pro.utils.DisplayUtils;

public class FloatViewHelper {
    Context context;
    FloatConfig config;

    FloatTouchUtils touchUtils = null;
    public WindowManager manager = null;
    public LayoutParams params = null;
    public FloatViewParent floatViewParent = null;

    Boolean isPortrait = null;

    public FloatViewHelper(Context context, FloatConfig config) {
        this.context = context;
        this.config = config;
    }

    void createView() {
        touchUtils = new FloatTouchUtils(context, config);
        manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        params = new LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (context instanceof AccessibilityService) params.type = LayoutParams.TYPE_ACCESSIBILITY_OVERLAY;
            else params.type = LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            params.type = LayoutParams.TYPE_PHONE;
        }
        params.format = PixelFormat.RGBA_8888;
        params.gravity = Gravity.START | Gravity.TOP;
        params.flags = EasyFloat.NOT_FOCUSABLE | config.flag;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            params.layoutInDisplayCutoutMode = LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        params.width = config.matchWidth ? LayoutParams.MATCH_PARENT : LayoutParams.WRAP_CONTENT;
        params.height = config.matchHeight ? LayoutParams.MATCH_PARENT : LayoutParams.WRAP_CONTENT;

        floatViewParent = new FloatViewParent(context, config);
        View floatInnerView = config.layoutView;
        if (floatInnerView == null) {
            floatInnerView = LayoutInflater.from(context).inflate(config.layoutId, floatViewParent, true);
            config.layoutView = floatInnerView;
        } else {
            floatViewParent.addView(floatInnerView);
        }

        floatViewParent.setVisibility(View.INVISIBLE);
        manager.addView(floatViewParent, params);
        floatViewParent.touchCallback = event -> touchUtils.updateFloatPosition(floatViewParent, event, manager, params);
        floatViewParent.layoutCallback = () -> {
            initGravity();

            isPortrait = DisplayUtils.isPortrait(context);

            if (config.callback != null) {
                config.callback.onCreate(true);
            }
            if (config.animator != null) {
                Animator animator = config.animator.enterAnim(floatViewParent, manager, params, config.side);
                if (animator != null) {
                    animator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            config.isAnim = false;
                            initEditText(floatViewParent);
                        }

                        @Override
                        public void onAnimationStart(Animator animation) {
                            config.isAnim = true;
                            floatViewParent.setVisibility(View.VISIBLE);
                            if (config.callback != null) {
                                config.callback.onShow(config.tag);
                            }
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            config.isAnim = false;
                            initEditText(floatViewParent);
                        }
                    });
                    animator.start();
                }
            } else {
                floatViewParent.setVisibility(View.VISIBLE);
                initEditText(floatViewParent);
                if (config.callback != null) {
                    config.callback.onShow(config.tag);
                }
            }
        };

        setViewChangedListener();
    }

    private void setViewChangedListener() {
        floatViewParent.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            if (isPortrait == null || isPortrait.equals(DisplayUtils.isPortrait(context))) {
                return;
            }
            isPortrait = DisplayUtils.isPortrait(context);

            initGravity();
        });
    }

    public Point getConfigPosition() {
        Point position = getGravityPosition();
        position.x = params.x - position.x;
        position.y = params.y - position.y;
        return position;
    }

    private Rect getShowRect() {
        Rect showSize = DisplayUtils.getScreenArea(context);
        int statusBarHeight = DisplayUtils.getStatusBarHeight(floatViewParent, params);
        showSize.top += config.topBorder;
        showSize.left += config.leftBorder;
        showSize.bottom -= (statusBarHeight + config.bottomBorder);
        showSize.right -= config.rightBorder;

        showSize.right -= floatViewParent.getWidth();
        showSize.bottom -= floatViewParent.getHeight();

        return showSize;
    }

    private Point getGravityPosition() {
        Rect showSize = getShowRect();

        Point point = new Point();
        switch (config.gravity) {
            case TOP_LEFT -> {
                point.x = showSize.left;
                point.y = showSize.top;
            }
            case TOP_CENTER -> {
                point.x = (showSize.left + showSize.right) / 2;
                point.y = showSize.top;
            }
            case TOP_RIGHT -> {
                point.x = showSize.right;
                point.y = showSize.top;
            }
            case LEFT_CENTER -> {
                point.x = showSize.left;
                point.y = (showSize.top + showSize.bottom) / 2;
            }
            case CENTER -> {
                point.x = (showSize.left + showSize.right) / 2;
                point.y = (showSize.top + showSize.bottom) / 2;
            }
            case RIGHT_CENTER -> {
                point.x = showSize.right;
                point.y = (showSize.top + showSize.bottom) / 2;
            }
            case BOTTOM_LEFT -> {
                point.x = showSize.left;
                point.y = showSize.bottom;
            }
            case BOTTOM_CENTER -> {
                point.x = (showSize.left + showSize.right) / 2;
                point.y = showSize.bottom;
            }
            case BOTTOM_RIGHT -> {
                point.x = showSize.right;
                point.y = showSize.bottom;
            }
        }
        return point;
    }

    public void offset(int x, int y) {
        config.offset.x += x;
        config.offset.y += y;
    }

    public void initGravity() {
        Rect showSize = getShowRect();
        Point position = getGravityPosition();
        params.x = Math.max(Math.min(config.offset.x + position.x, showSize.right), showSize.left);
        params.y = Math.max(Math.min(config.offset.y + position.y, showSize.bottom), showSize.top);
        manager.updateViewLayout(floatViewParent, params);
    }

    private void initEditText(View view) {
        if (config.hasEditText) {
            if (view instanceof ViewGroup viewGroup) {
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    initEditText(viewGroup.getChildAt(i));
                }
            } else {
                if (view instanceof EditText) {
                    EasyFloat.initInput((EditText) view, config.tag);
                }
            }
        }
    }

    View getView() {
        return config.layoutView;
    }

    void exitAnim() {
        if (config.animator != null) {
            Animator animator = config.animator.exitAnim(floatViewParent, manager, params, config.side);
            if (animator != null) {
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        config.isAnim = true;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        config.isAnim = false;
                        remove();
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        config.isAnim = false;
                        remove();
                    }
                });
                animator.start();
                return;
            }
        }
        remove();
    }

    private void remove() {
        try {
            manager.removeView(floatViewParent);
        } catch (Exception ignored) {
        }
    }
}
