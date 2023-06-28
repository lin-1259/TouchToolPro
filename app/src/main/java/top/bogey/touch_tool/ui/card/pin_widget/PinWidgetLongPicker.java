package top.bogey.touch_tool.ui.card.pin_widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.pin.PinSubType;
import top.bogey.touch_tool.data.pin.object.PinLong;
import top.bogey.touch_tool.databinding.PinWidgetStringPickerBinding;
import top.bogey.touch_tool.ui.custom.BindingView;
import top.bogey.touch_tool.utils.AppUtils;

public class PinWidgetLongPicker extends BindingView<PinWidgetStringPickerBinding> {

    public PinWidgetLongPicker(@NonNull Context context, PinLong pinLong, PinSubType pinSubType) {
        this(context, null, pinLong, pinSubType);
    }

    public PinWidgetLongPicker(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, new PinLong(System.currentTimeMillis()), PinSubType.DATE);
    }

    public PinWidgetLongPicker(@NonNull Context context, @Nullable AttributeSet attrs, PinLong pinLong, PinSubType pinSubType) {
        super(context, attrs, PinWidgetStringPickerBinding.class);
        if (pinLong == null) throw new RuntimeException("不是有效的引用");

        if (pinSubType == PinSubType.DATE) {
            binding.title.setText(AppUtils.formatDateLocalDate(context, pinLong.getValue()));
            binding.pickButton.setIconResource(R.drawable.icon_date);
            binding.pickButton.setOnClickListener(v -> {
                CalendarConstraints calendarConstraints = new CalendarConstraints.Builder()
                        .setValidator(DateValidatorPointForward.from(System.currentTimeMillis() - 48 * 60 * 60 * 1000))
                        .build();

                MaterialDatePicker<Long> picker = MaterialDatePicker.Builder
                        .datePicker()
                        .setSelection(pinLong.getValue())
                        .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                        .setCalendarConstraints(calendarConstraints)
                        .build();

                picker.show(((AppCompatActivity) context).getSupportFragmentManager(), null);

                picker.addOnPositiveButtonClickListener(selection -> {
                    pinLong.setValue(selection);
                    binding.title.setText(AppUtils.formatDateLocalDate(context, pinLong.getValue()));
                });
            });
        } else if (pinSubType == PinSubType.TIME) {
            binding.title.setText(AppUtils.formatDateLocalTime(context, pinLong.getValue()));
            binding.pickButton.setIconResource(R.drawable.icon_time);
            binding.pickButton.setOnClickListener(v -> {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(pinLong.getValue());

                MaterialTimePicker picker = new MaterialTimePicker.Builder()
                        .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                        .setTimeFormat(TimeFormat.CLOCK_24H)
                        .setHour(calendar.get(Calendar.HOUR_OF_DAY))
                        .setMinute(calendar.get(Calendar.MINUTE))
                        .build();

                picker.show(((AppCompatActivity) context).getSupportFragmentManager(), null);

                picker.addOnPositiveButtonClickListener(view -> {
                    calendar.set(Calendar.HOUR_OF_DAY, picker.getHour());
                    calendar.set(Calendar.MINUTE, picker.getMinute());
                    calendar.set(Calendar.SECOND, 0);
                    pinLong.setValue(calendar.getTimeInMillis());
                    binding.title.setText(AppUtils.formatDateLocalTime(context, pinLong.getValue()));
                });
            });
        } else if (pinSubType == PinSubType.PERIODIC) {
            binding.title.setText(AppUtils.formatDateLocalDuration(context, pinLong.getValue()));
            binding.pickButton.setIconResource(R.drawable.icon_delay);
            binding.pickButton.setOnClickListener(v -> {
                MaterialTimePicker picker = new MaterialTimePicker.Builder()
                        .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                        .setTimeFormat(TimeFormat.CLOCK_24H)
                        .setHour((int) TimeUnit.MILLISECONDS.toHours(pinLong.getValue()))
                        .setMinute((int) TimeUnit.MILLISECONDS.toMinutes(pinLong.getValue()))
                        .setTitleText(R.string.action_time_start_tips)
                        .build();

                picker.show(((AppCompatActivity) context).getSupportFragmentManager(), null);

                picker.addOnPositiveButtonClickListener(view -> {
                    pinLong.setValue(TimeUnit.HOURS.toMillis(picker.getHour()) + TimeUnit.MINUTES.toMillis(picker.getMinute()));
                    if (picker.getHour() == 0 && picker.getMinute() == 0)
                        pinLong.setValue(TimeUnit.HOURS.toMillis(24));
                    long millis = TimeUnit.MINUTES.toMillis(15);
                    if (pinLong.getValue() < millis) pinLong.setValue(0);
                    binding.title.setText(AppUtils.formatDateLocalDuration(context, pinLong.getValue()));
                });
            });
        }
    }
}
