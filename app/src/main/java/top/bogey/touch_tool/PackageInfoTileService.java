package top.bogey.touch_tool;

import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import top.bogey.touch_tool.data.WorldState;

public class PackageInfoTileService extends TileService {

    @Override
    public void onClick() {
        super.onClick();
        MainAccessibilityService service = MainApplication.getInstance().getService();
        if (service != null && service.isServiceEnabled()) {
            String packageName = WorldState.getInstance().getPackageName();
            String activityName = WorldState.getInstance().getActivityName();
            service.showToast(getString(R.string.package_tile_service_tips, packageName, activityName));
        }
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        Tile tile = getQsTile();
        MainAccessibilityService service = MainApplication.getInstance().getService();
        if (service != null && service.isServiceEnabled()) tile.setState(Tile.STATE_INACTIVE);
        else tile.setState(Tile.STATE_UNAVAILABLE);
        tile.updateTile();
    }
}
