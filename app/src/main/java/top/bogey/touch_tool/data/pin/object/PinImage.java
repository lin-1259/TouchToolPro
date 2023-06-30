package top.bogey.touch_tool.data.pin.object;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Base64;

import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.utils.DisplayUtils;
import top.bogey.touch_tool.utils.GsonUtils;

public class PinImage extends PinValue {
    private transient Bitmap bitmap;
    private transient Bitmap scaleBitmap;
    private transient float scale = 1;

    private int screen;

    private String image;

    public PinImage() {
        super();
        screen = 1080;
    }

    public PinImage(JsonObject jsonObject) {
        super(jsonObject);
        screen = GsonUtils.getAsInt(jsonObject, "screen", 1080);
        image = GsonUtils.getAsString(jsonObject, "image", null);
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

    public void setBitmap(Context context, Bitmap bitmap) {
        this.bitmap = bitmap;
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

    @Override
    public int getPinColor(Context context) {
        return context.getColor(R.color.ImagePinColor);
    }

    @Override
    public boolean isEmpty() {
        return image == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        PinImage pinImage = (PinImage) o;

        if (screen != pinImage.screen) return false;
        return Objects.equals(image, pinImage.image);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + screen;
        result = 31 * result + (image != null ? image.hashCode() : 0);
        return result;
    }
}
