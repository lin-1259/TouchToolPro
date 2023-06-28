package top.bogey.touch_tool.ui.card.pin_widget;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.pin.PinSubType;
import top.bogey.touch_tool.data.pin.object.PinString;
import top.bogey.touch_tool.databinding.PinWidgetStringPickerBinding;
import top.bogey.touch_tool.ui.InstantActivity;
import top.bogey.touch_tool.ui.card.BaseCard;
import top.bogey.touch_tool.ui.custom.BindingView;

public class PinWidgetStringPicker extends BindingView<PinWidgetStringPickerBinding> {

    public PinWidgetStringPicker(@NonNull Context context, PinString pinString, PinSubType pinSubType, BaseCard<?> card) {
        this(context, null, pinString, pinSubType, card);
    }

    public PinWidgetStringPicker(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, new PinString(), PinSubType.URL, null);
    }

    public PinWidgetStringPicker(@NonNull Context context, @Nullable AttributeSet attrs, PinString pinString, PinSubType pinSubType, BaseCard<?> card) {
        super(context, attrs, PinWidgetStringPickerBinding.class);
        if (pinString == null) throw new RuntimeException("不是有效的引用");
        String value = pinString.getValue();

        if (pinSubType == PinSubType.URL) {
            binding.title.setText(R.string.action_out_start_subtitle_copy);
            binding.pickButton.setIconResource(R.drawable.icon_copy);
            binding.pickButton.setOnClickListener(v -> {
                ClipboardManager manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText(context.getString(R.string.app_name), "ttp://do_action?" + value);
                manager.setPrimaryClip(clipData);
                Toast.makeText(context, R.string.report_running_error_copied, Toast.LENGTH_SHORT).show();
            });
        } else if (pinSubType == PinSubType.SHORTCUT) {
            Task task = (Task) card.getActionContext();

            binding.title.setText(R.string.action_out_start_subtitle_shortcut_create);
            binding.pickButton.setIconResource(R.drawable.icon_export);
            binding.pickButton.setOnClickListener(v -> {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);
                    if (shortcutManager.isRequestPinShortcutSupported()) {
                        Intent intent = new Intent(context, InstantActivity.class);
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.putExtra(InstantActivity.INTENT_KEY_DO_ACTION, value);
                        ShortcutInfo shortcut = new ShortcutInfo.Builder(context, value)
                                .setShortLabel(task.getTitle())
                                .setIcon(Icon.createWithResource(context, R.drawable.icon_shortcut))
                                .setIntent(intent)
                                .build();
                        shortcutManager.requestPinShortcut(shortcut, null);
                        return;
                    }
                }
                Toast.makeText(context, R.string.device_not_support_shortcut, Toast.LENGTH_SHORT).show();
            });
        }
    }
}
