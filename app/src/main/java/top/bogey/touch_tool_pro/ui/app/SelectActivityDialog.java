package top.bogey.touch_tool_pro.ui.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.regex.Pattern;

import top.bogey.touch_tool_pro.databinding.DialogSelectActivityBinding;
import top.bogey.touch_tool_pro.utils.TextChangedListener;

@SuppressLint("ViewConstructor")
public class SelectActivityDialog extends FrameLayout {
    private final SelectActivityRecyclerViewAdapter adapter;

    public SelectActivityDialog(@NonNull Context context, ArrayList<String> activityNameList, boolean single, ArrayList<String> selectedNameList) {
        super(context);
        DialogSelectActivityBinding binding = DialogSelectActivityBinding.inflate(LayoutInflater.from(context), this, true);

        adapter = new SelectActivityRecyclerViewAdapter(single, selectedNameList);
        binding.activityBox.setAdapter(adapter);
        adapter.refreshActivityNames(activityNameList);

        binding.searchEdit.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s != null) {
                    String searchText = s.toString();
                    if (searchText.isEmpty()) {
                        adapter.refreshActivityNames(activityNameList);
                    } else {
                        Pattern pattern = Pattern.compile(searchText.toLowerCase());
                        ArrayList<String> newActivityNameList = new ArrayList<>();
                        for (String activityName : activityNameList) {
                            if (pattern.matcher(activityName.toLowerCase()).find()) {
                                newActivityNameList.add(activityName);
                            }
                        }
                        adapter.refreshActivityNames(newActivityNameList);
                    }
                }
            }
        });
    }
}
