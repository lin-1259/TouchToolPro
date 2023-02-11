package top.bogey.touch_tool.ui.card.pin_widget;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.pin.PinSubType;
import top.bogey.touch_tool.data.pin.object.PinString;
import top.bogey.touch_tool.databinding.PinWidgetStringPickerBinding;
import top.bogey.touch_tool.ui.custom.BindingView;

public class PinWidgetStringPicker extends BindingView<PinWidgetStringPickerBinding> {

    public PinWidgetStringPicker(@NonNull Context context, PinString pinString, PinSubType pinSubType) {
        this(context, null, pinString, pinSubType);
    }

    public PinWidgetStringPicker(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, new PinString(), PinSubType.URL);
    }

    public PinWidgetStringPicker(@NonNull Context context, @Nullable AttributeSet attrs, PinString pinString, PinSubType pinSubType) {
        super(context, attrs, PinWidgetStringPickerBinding.class);
        if (pinString == null) throw new RuntimeException("不是有效的引用");

        if (pinSubType == PinSubType.URL) {
            binding.title.setText(R.string.action_out_start_subtitle_copy);
            binding.pickButton.setIconResource(R.drawable.icon_copy);
            binding.pickButton.setOnClickListener(v -> {
                ClipboardManager manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText(context.getString(R.string.app_name), "ttp://do_action?" + pinString.getValue());
                manager.setPrimaryClip(clipData);
                Toast.makeText(context, R.string.report_running_error_copied, Toast.LENGTH_SHORT).show();
            });
        }
    }
}
