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
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.start.ManualStartAction;
import top.bogey.touch_tool.databinding.FloatPlayItemBinding;
import top.bogey.touch_tool.utils.TaskRunningCallback;

@SuppressLint("ViewConstructor")
public class PlayFloatViewItem extends FrameLayout implements TaskRunningCallback {
    private final FloatPlayItemBinding binding;

    private final ManualStartAction startAction;
    private TaskRunnable runnable;

    private boolean playing = false;
    private boolean needRemove = false;

    public PlayFloatViewItem(@NonNull Context context, Task task, ManualStartAction startAction) {
        super(context);
        this.startAction = startAction;

        binding = FloatPlayItemBinding.inflate(LayoutInflater.from(context), this, true);

        CharSequence title = startAction.getDes();
        if (title == null) {
            title = task.getTitle();
        }
        binding.percent.setText(getPivotalTitle(title.toString()));

        binding.playButton.setOnClickListener(v -> {
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
        });

        refreshProgress();
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

    private void refreshProgress() {
        post(() -> binding.playButton.setIndeterminate(playing));
    }

    @Override
    public void onStart(TaskRunnable runnable) {
        playing = true;
        refreshProgress();
    }

    @Override
    public void onEnd(TaskRunnable runnable) {
        playing = false;
        refreshProgress();
        if (needRemove) {
            post(() -> {
                ViewParent parent = getParent();
                if (parent != null) ((ViewGroup) parent).removeView(this);
            });
        }
    }

    @Override
    public void onProgress(TaskRunnable runnable, int percent) {
    }
}
