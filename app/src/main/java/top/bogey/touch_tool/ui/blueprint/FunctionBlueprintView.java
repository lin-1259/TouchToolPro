package top.bogey.touch_tool.ui.blueprint;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRepository;
import top.bogey.touch_tool.data.action.function.BaseFunction;
import top.bogey.touch_tool.databinding.ViewTaskBlueprintBinding;

public class FunctionBlueprintView extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getArguments() == null) throw new IllegalArgumentException();

        ViewTaskBlueprintBinding binding = ViewTaskBlueprintBinding.inflate(inflater, container, false);

        String functionId = getArguments().getString("functionId");
        BaseFunction function = TaskRepository.getInstance().getFunctionById(functionId);
        binding.cardLayout.setActionContext(function);

        binding.addButton.setOnClickListener(v -> {
            ActionSideSheetDialog dialog = new ActionSideSheetDialog(requireContext(), binding.cardLayout);
            dialog.show();
        });

        requireActivity().addMenuProvider(new MenuProvider() {
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
        }, getViewLifecycleOwner());

        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.function_title);
            actionBar.setSubtitle(function.getTitle());
        }

        return binding.getRoot();
    }
}
