package top.bogey.touch_tool.data.pin.object;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;

import top.bogey.touch_tool.data.TaskRepository;
import top.bogey.touch_tool.utils.DisplayUtils;

public class PinImage extends PinValue {
    private transient Bitmap bitmap;
    private transient Bitmap scaleBitmap;
    private transient float scale = 1;

    private int screen;

    private String image;
    private Rect area;

    public PinImage() {
        super();
        screen = 1080;
        area = new Rect();
    }

    public PinImage(JsonObject jsonObject) {
        super(jsonObject);
        Gson gson = TaskRepository.getInstance().getGson();
        screen = jsonObject.get("screen").getAsInt();
        JsonElement element = jsonObject.get("image");
        if (element != null) image = element.getAsString();
        area = gson.fromJson(jsonObject.get("area"), Rect.class);
    }

    public Bitmap getBitmap() {
        if (bitmap == null && (image != null && !image.isEmpty())) {
            try {
                byte[] bitmapArray = Base64.decode(image, Base64.DEFAULT);
                bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
            } catch (IllegalArgumentException ignored) {
            }
        }
        return bitmap;
    }

    public void setBitmap(Context context, Bitmap bitmap, Rect area) {
        this.bitmap = bitmap;
        this.area = area;
        screen = DisplayUtils.getScreen(context);

        if (bitmap == null) image = "";
        else {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] bytes = stream.toByteArray();
            image = Base64.encodeToString(bytes, Base64.DEFAULT);
        }

        if (scaleBitmap != null) {
            scaleBitmap.recycle();
            scaleBitmap = null;
        }
    }

    public Bitmap getScaleBitmap(Context context) {
        float scale = DisplayUtils.getScreen(context) * 1f / screen;
        if (scaleBitmap == null || scale != this.scale) {
            Bitmap bitmap = getBitmap();
            if (bitmap != null) {
                Matrix matrix = new Matrix();
                matrix.postScale(scale, scale);
                scaleBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            }
        }
        this.scale = scale;
        return scaleBitmap;
    }

    public String getImage() {
        return image;
    }

    public Rect getArea(Context context) {
        if (area.left == 0 && area.right == 0 && area.top == 0 && area.bottom == 0) {
            area = DisplayUtils.getScreenArea(context);
            return area;
        }

        float scale = DisplayUtils.getScreen(context) * 1f / screen;
        return new Rect((int) (area.left * scale), (int) (area.top * scale), (int) (area.right * scale), (int) (area.bottom * scale));
    }
}
