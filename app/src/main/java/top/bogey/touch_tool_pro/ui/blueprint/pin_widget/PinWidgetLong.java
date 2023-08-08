package top.bogey.touch_tool_pro.ui.blueprint.pin_widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.PinSubType;
import top.bogey.touch_tool_pro.bean.pin.pins.PinLong;
import top.bogey.touch_tool_pro.databinding.PinWidgetInputBinding;
import top.bogey.touch_tool_pro.ui.blueprint.card.ActionCard;
import top.bogey.touch_tool_pro.ui.blueprint.pin.PinCustomView;
import top.bogey.touch_tool_pro.ui.blueprint.pin.PinView;
import top.bogey.touch_tool_pro.utils.AppUtils;
import top.bogey.touch_tool_pro.utils.SpinnerSelectedListener;

@SuppressLint("ViewConstructor")
public class PinWidgetLong extends PinWidget<PinLong> {
    private final PinWidgetInputBinding binding;

    public PinWidgetLong(@NonNull Context context, ActionCard<?> card, PinView pinView, PinLong pinObject, boolean custom) {
        super(context, card, pinView, pinObject, custom);
        binding = PinWidgetInputBinding.inflate(LayoutInflater.from(context), this, true);
        init();
    }

    @Override
    public void initBase() {
        binding.editText.setSaveEnabled(false);
        binding.editText.setSaveFromParentEnabled(false);
        binding.editText.setEnabled(false);
        binding.pickButton.setVisibility(VISIBLE);

        switch (pinObject.getSubType()) {
            case DATE -> {
                binding.editText.setText(AppUtils.formatDateLocalDate(context, pinObject.getValue()));
                binding.pickButton.setIconResource(R.drawable.icon_date);
                binding.pickButton.setOnClickListener(v -> {
                    CalendarConstraints calendarConstraints = new CalendarConstraints.Builder()
                            .setValidator(DateValidatorPointForward.from(System.currentTimeMillis() - 48 * 60 * 60 * 1000))
                            .build();

                    MaterialDatePicker<Long> picker = MaterialDatePicker.Builder
                            .datePicker()
                            .setSelection(pinObject.getValue())
                            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                            .setCalendarConstraints(calendarConstraints)
                            .build();

                    picker.show(((AppCompatActivity) context).getSupportFragmentManager(), null);

                    picker.addOnPositiveButtonClickListener(selection -> {
                        pinObject.setValue(selection);
                        binding.editText.setText(AppUtils.formatDateLocalDate(context, pinObject.getValue()));
                    });
                });
            }
            case TIME -> {
                binding.editText.setText(AppUtils.formatDateLocalTime(context, pinObject.getValue()));
                binding.pickButton.setIconResource(R.drawable.icon_time);
                binding.pickButton.setOnClickListener(v -> {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(pinObject.getValue());

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
                        pinObject.setValue(calendar.getTimeInMillis());
                        binding.editText.setText(AppUtils.formatDateLocalTime(context, pinObject.getValue()));
                    });
                });
            }
            case PERIODIC -> {
                binding.editText.setText(AppUtils.formatDateLocalDuration(context, pinObject.getValue()));
                binding.pickButton.setIconResource(R.drawable.icon_delay);
                binding.pickButton.setOnClickListener(v -> {
                    MaterialTimePicker picker = new MaterialTimePicker.Builder()
                            .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                            .setTimeFormat(TimeFormat.CLOCK_24H)
                            .setHour((int) TimeUnit.MILLISECONDS.toHours(pinObject.getValue()))
                            .setMinute((int) TimeUnit.MILLISECONDS.toMinutes(pinObject.getValue()))
                            .setTitleText(R.string.action_time_start_tips)
                            .build();

                    picker.show(((AppCompatActivity) context).getSupportFragmentManager(), null);

                    picker.addOnPositiveButtonClickListener(view -> {
                        pinObject.setValue(TimeUnit.HOURS.toMillis(picker.getHour()) + TimeUnit.MINUTES.toMillis(picker.getMinute()));
                        if (picker.getHour() == 0 && picker.getMinute() == 0)
                            pinObject.setValue(TimeUnit.HOURS.toMillis(24));
                        long millis = TimeUnit.MINUTES.toMillis(15);
                        if (pinObject.getValue() < millis) pinObject.setValue(0L);
                        binding.editText.setText(AppUtils.formatDateLocalDuration(context, pinObject.getValue()));
                    });
                });
            }
        }
    }

    @Override
    public void initCustom() {
        binding.spinner.setVisibility(VISIBLE);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.pin_widget_spinner_item);
        adapter.addAll(context.getResources().getStringArray(R.array.long_value_type));
        binding.spinner.setAdapter(adapter);
        binding.spinner.setSelection(subTypeToIndex(pinObject.getSubType()));
        binding.spinner.setOnItemSelectedListener(new SpinnerSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == subTypeToIndex(pinObject.getSubType())) return;
                Pin functionPin = ((PinCustomView) pinView).getFunctionPin();
                functionPin.cleanLinks(card.getFunctionContext());
                functionPin.setValue(new PinLong(indexToSubType(position), System.currentTimeMillis()));
                pinView.refreshPinView();
            }
        });
    }

    private int subTypeToIndex(PinSubType subType) {
        return switch (subType) {
            case TIME -> 1;
            case PERIODIC -> 2;
            default -> 0;
        };
    }

    private PinSubType indexToSubType(int index) {
        return switch (index) {
            case 1 -> PinSubType.TIME;
            case 2 -> PinSubType.PERIODIC;
            default -> PinSubType.DATE;
        };
    }
}
