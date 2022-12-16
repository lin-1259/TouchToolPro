package top.bogey.touch_tool.ui.card.pin_widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.action.pin.PinType;
import top.bogey.touch_tool.databinding.PinWidgetTimeBinding;
import top.bogey.touch_tool.ui.custom.BindingView;
import top.bogey.touch_tool.utils.AppUtils;

public class PinWidgetTime extends BindingView<PinWidgetTimeBinding> {
    private final AtomicLong atomicLong;

    public PinWidgetTime(@NonNull Context context, AtomicLong atomicLong, PinType pinType) {
        this(context, null, atomicLong, pinType);
    }

    public PinWidgetTime(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, new AtomicLong(System.currentTimeMillis()), PinType.DATE);
    }

    public PinWidgetTime(@NonNull Context context, @Nullable AttributeSet attrs, AtomicLong atomicLong, PinType pinType) {
        super(context, attrs, PinWidgetTimeBinding.class);
        if (atomicLong == null) throw new RuntimeException("不是有效的引用");
        this.atomicLong = atomicLong;

        if (pinType == PinType.DATE) {
            binding.title.setText(AppUtils.formatDateLocalDate(context, atomicLong.get()));
            binding.pickButton.setIconResource(R.drawable.icon_date);
            binding.pickButton.setOnClickListener(v -> {
                CalendarConstraints calendarConstraints = new CalendarConstraints.Builder()
                        .setValidator(DateValidatorPointForward.now())
                        .build();

                MaterialDatePicker<Long> picker = MaterialDatePicker.Builder
                        .datePicker()
                        .setSelection(atomicLong.get())
                        .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                        .setCalendarConstraints(calendarConstraints)
                        .build();

                picker.show(MainApplication.getActivity().getSupportFragmentManager(), null);

                picker.addOnPositiveButtonClickListener(selection -> {
                    atomicLong.set(selection);
                    binding.title.setText(AppUtils.formatDateLocalDate(context, atomicLong.get()));
                });
            });
        } else if (pinType == PinType.TIME) {
            binding.title.setText(AppUtils.formatDateLocalTime(context, atomicLong.get()));
            binding.pickButton.setIconResource(R.drawable.icon_time);
            binding.pickButton.setOnClickListener(v -> {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(atomicLong.get());

                MaterialTimePicker picker = new MaterialTimePicker.Builder()
                        .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                        .setTimeFormat(TimeFormat.CLOCK_24H)
                        .setHour(calendar.get(Calendar.HOUR_OF_DAY))
                        .setMinute(calendar.get(Calendar.MINUTE))
                        .build();

                picker.show(MainApplication.getActivity().getSupportFragmentManager(), null);

                picker.addOnPositiveButtonClickListener(view -> {
                    calendar.set(Calendar.HOUR_OF_DAY, picker.getHour());
                    calendar.set(Calendar.MINUTE, picker.getMinute());
                    calendar.set(Calendar.SECOND, 0);
                    atomicLong.set(calendar.getTimeInMillis());
                    binding.title.setText(AppUtils.formatDateLocalTime(context, atomicLong.get()));
                });
            });
        } else {
            binding.title.setText(AppUtils.formatDateLocalDuration(context, atomicLong.get()));
            binding.pickButton.setIconResource(R.drawable.icon_action_delay);
            binding.pickButton.setOnClickListener(v -> {
                MaterialTimePicker picker = new MaterialTimePicker.Builder()
                        .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                        .setTimeFormat(TimeFormat.CLOCK_24H)
                        .setHour((int) TimeUnit.MILLISECONDS.toHours(atomicLong.get()))
                        .setMinute((int) TimeUnit.MILLISECONDS.toMinutes(atomicLong.get()))
                        .setTitleText(R.string.time_condition_periodic_tips)
                        .build();

                picker.show(MainApplication.getActivity().getSupportFragmentManager(), null);

                picker.addOnPositiveButtonClickListener(view -> {
                    atomicLong.set(TimeUnit.HOURS.toMillis(picker.getHour()) + TimeUnit.MINUTES.toMillis(picker.getMinute()));
                    if (picker.getHour() == 0 && picker.getMinute() == 0)
                        atomicLong.set(TimeUnit.HOURS.toMillis(24));
                    long millis = TimeUnit.MINUTES.toMillis(15);
                    if (atomicLong.get() < millis) atomicLong.set(0);
                    binding.title.setText(AppUtils.formatDateLocalDuration(context, atomicLong.get()));
                });
            });
        }
    }
}
