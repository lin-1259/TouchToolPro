package top.bogey.touch_tool.ui.play;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import top.bogey.touch_tool.MainAccessibilityService;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.start.ManualStartAction;
import top.bogey.touch_tool.databinding.FloatPlayItemBinding;
import top.bogey.touch_tool.utils.TaskRunningCallback;

@SuppressLint("ViewConstructor")
public class PlayFloatViewItem extends FrameLayout implements TaskRunningCallback {
    private final FloatPlayItemBinding binding;

    private final Task task;
    private final ManualStartAction startAction;
    private TaskRunnable runnable;

    private boolean playing = false;
    private boolean needRemove = false;

    private final String title;

    public PlayFloatViewItem(@NonNull Context context, Task task, ManualStartAction startAction) {
        super(context);
        this.task = task;
        this.startAction = startAction;

        binding = FloatPlayItemBinding.inflate(LayoutInflater.from(context), this, true);

        CharSequence title = startAction.getDes();
        if (title == null) {
            title = task.getTitle();
        }
        this.title = getPivotalTitle(title.toString());
        binding.percent.setText(this.title);

        binding.playButton.setOnClickListener(v -> {
            MainAccessibilityService service = MainApplication.getService();
            // 录屏服务没开启，需要检查涉及图片或颜色的动作
            if (service != null && !service.isCaptureEnabled()) {
                if (task.needCaptureService()) {
                    service.showToast(context.getString(R.string.capture_service_on_tips));
                    service.startCaptureService(true, null);
                } else {
                    startPlay();
                }
            } else {
                startPlay();
            }
        });

        refreshProgress(0);
    }

    private void startPlay() {
        MainAccessibilityService service = MainApplication.getService();
        if (service != null && service.isServiceConnected()) {
            if (playing) {
                if (runnable != null) {
                    service.stopTask(runnable);
                }
            } else {
                runnable = service.runTask(task, startAction);
                runnable.addCallback(this);
            }
        }
    }

    public ManualStartAction getStartAction() {
        return startAction;
    }

    public boolean isFree() {
        return !playing;
    }

    public void setNeedRemove(boolean needRemove) {
        this.needRemove = needRemove;
    }

    private String getPivotalTitle(String title) {
        if (title == null || title.isEmpty()) return "?";
        Pattern pattern = Pattern.compile("[\"|“](.*)[\"|”]");
        Matcher matcher = pattern.matcher(title);
        if (matcher.find()) {
            String group = matcher.group(1);
            if (group != null) return group.substring(0, 1);
        }
        return title.substring(0, 1);
    }

    private void refreshProgress(int progress) {
        post(() -> {
            binding.playButton.setIndeterminate(playing);
            binding.percent.setText(progress == 0 ? title : String.valueOf(progress));
        });
    }

    @Override
    public void onStart(TaskRunnable runnable) {
        playing = true;
        refreshProgress(0);
    }

    @Override
    public void onEnd(TaskRunnable runnable) {
        playing = false;
        refreshProgress(0);
        if (needRemove) {
            post(() -> {
                ViewParent parent = getParent();
                if (parent != null) ((ViewGroup) parent).removeView(this);
            });
        }
    }

    @Override
    public void onProgress(TaskRunnable runnable, int progress) {
        playing = true;
        refreshProgress(progress);
    }
}
