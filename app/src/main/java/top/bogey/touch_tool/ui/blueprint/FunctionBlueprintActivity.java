package top.bogey.touch_tool.ui.blueprint;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRepository;
import top.bogey.touch_tool.data.action.function.BaseFunction;
import top.bogey.touch_tool.databinding.ActivityBlueprintBinding;

public class FunctionBlueprintActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent == null) throw new IllegalArgumentException();

        String functionId = intent.getStringExtra("functionId");
        BaseFunction function = TaskRepository.getInstance().getFunctionById(functionId);

        ActivityBlueprintBinding binding = ActivityBlueprintBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.cardLayout.setActionContext(function);

        binding.addButton.setOnClickListener(v -> {
            ActionSideSheetDialog dialog = new ActionSideSheetDialog(this, binding.cardLayout);
            dialog.show();
        });

        addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.menu_function, menu);
            }

            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.saveTask) {
                    TaskRepository.getInstance().saveFunction(function);
                }
                return true;
            }
        });

        binding.toolBar.setTitle(R.string.function_title);
        binding.toolBar.setSubtitle(function.getTitle());
    }
}
