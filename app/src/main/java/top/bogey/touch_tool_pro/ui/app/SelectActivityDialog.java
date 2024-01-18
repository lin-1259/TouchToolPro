package top.bogey.touch_tool_pro.ui.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import top.bogey.touch_tool_pro.databinding.DialogSelectActivityBinding;
import top.bogey.touch_tool_pro.utils.TextChangedListener;

@SuppressLint("ViewConstructor")
public class SelectActivityDialog extends FrameLayout {
    private final SelectActivityRecyclerViewAdapter adapter;

    public SelectActivityDialog(@NonNull Context context, HashMap<String, String> activityNameMap, boolean single, ArrayList<String> selectedNameList) {
        super(context);
        DialogSelectActivityBinding binding = DialogSelectActivityBinding.inflate(LayoutInflater.from(context), this, true);

        adapter = new SelectActivityRecyclerViewAdapter(single, activityNameMap, selectedNameList);
        binding.activityBox.setAdapter(adapter);
        adapter.refreshActivityNames(new ArrayList<>(activityNameMap.keySet()));

        binding.searchEdit.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s != null) {
                    String searchText = s.toString();
                    if (searchText.isEmpty()) {
                        adapter.refreshActivityNames(new ArrayList<>(activityNameMap.keySet()));
                    } else {
                        Pattern pattern;
                        try {
                            pattern = Pattern.compile(searchText.toLowerCase());
                        } catch (PatternSyntaxException ignored){
                            return;
                        }

                        ArrayList<String> list = new ArrayList<>();

                        activityNameMap.forEach((k, v) -> {
                            if (pattern.matcher(k.toLowerCase()).find()) {
                                list.add(k);
                            } else if (pattern.matcher(v.toLowerCase()).find()) {
                                list.add(k);
                            }
                        });

                        adapter.refreshActivityNames(list);
                    }
                }
            }
        });
    }
}
