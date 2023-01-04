package top.bogey.touch_tool;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.databinding.ActivityMainBinding;
import top.bogey.touch_tool.utils.DisplayUtils;

public class MainActivity extends AppCompatActivity {
    static {
        System.loadLibrary("touch_tool");
    }

    public static final String INTENT_KEY_BACKGROUND = "INTENT_KEY_BACKGROUND";
    public static final String INTENT_KEY_PLAY_PACKAGE = "INTENT_KEY_PLAY_PACKAGE";
    public static final String INTENT_KEY_QUICK_MENU = "INTENT_KEY_QUICK_MENU";
    public static final String INTENT_KEY_START_CAPTURE = "INTENT_KEY_START_CAPTURE";

    private ActivityMainBinding binding;

    private ActivityResultLauncher<Intent> intentLauncher;
    private ActivityResultLauncher<String> permissionLauncher;
    private ActivityResultLauncher<String> contentLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(params);
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolBar);

        MainApplication.setActivity(this);

        DisplayUtils.initParams(this);

        intentLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

        });

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {

        });

        contentLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {

        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainApplication.setActivity(null);
    }

    @Override
    protected void onStart() {
        super.onStart();
        NavController controller = Navigation.findNavController(this, R.id.conView);
        NavigationUI.setupWithNavController(binding.menuView, controller);
        AppBarConfiguration configuration = new AppBarConfiguration.Builder(R.id.home, R.id.task, R.id.setting).build();
        NavigationUI.setupActionBarWithNavController(this, controller, configuration);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController controller = Navigation.findNavController(this, R.id.conView);
        return controller.navigateUp() || super.onSupportNavigateUp();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        WorldState.getInstance().resetAppMap(this);
    }
}