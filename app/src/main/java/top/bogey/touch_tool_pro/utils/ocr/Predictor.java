package top.bogey.touch_tool_pro.utils.ocr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.utils.AppUtils;

public class Predictor {
    private static Predictor predictor;

    public static Predictor getInstance() {
        if (predictor == null) {
            predictor = new Predictor(MainApplication.getInstance());
        }
        return predictor;
    }

    private long nativePointer;
    private boolean warmup = false;

    private final Vector<String> labels = new Vector<>();

    private Predictor(Context context) {
        loadModel(context);
        loadLabels(context);
    }

    public ArrayList<OcrResult> runOcr(Bitmap image) {
        if (image == null) return null;
        if (!warmup) {
            forward(nativePointer, image, 960, 1, 0, 1);
            warmup = true;
        }
        float[] floats = forward(nativePointer, image, 960, 1, 0, 1);

        ArrayList<OcrResult> results = new ArrayList<>();
        int begin = 0;
        while (begin < floats.length) {
            int pointNum = Math.round(floats[begin]);
            int wordNum = Math.round(floats[begin + 1]);
            int similar = Math.round(floats[begin + 2] * 100);

            int current = begin + 3;
            Rect rect = new Rect();
            boolean init = false;
            for (int i = 0; i < pointNum; i++) {
                int x = Math.round(floats[current + i * 2]);
                int y = Math.round(floats[current + i * 2 + 1]);
                if (init) {
                    rect.left = Math.min(x, rect.left);
                    rect.right = Math.max(x, rect.right);
                    rect.top = Math.min(y, rect.top);
                    rect.bottom = Math.max(y, rect.bottom);
                } else {
                    rect.set(x, y, x, y);
                    init = true;
                }
            }

            StringBuilder builder = new StringBuilder();
            current += (pointNum * 2);
            for (int i = 0; i < wordNum; i++) {
                int index = Math.round(floats[current + i]);
                builder.append(labels.get(index));
            }

            results.add(new OcrResult(rect, builder.toString(), similar));

            begin += (3 + pointNum * 2 + wordNum + 2);
        }
        return results;
    }

    public void destroy() {
        release(nativePointer);
    }

    private void loadModel(Context context) {
        String cacheDirPath = context.getCacheDir() + "/models";
        AppUtils.copyDirFromAssets(context, "models", cacheDirPath);
        nativePointer = init(cacheDirPath + File.separator + "det.nb",
                cacheDirPath + File.separator + "rec.nb",
                cacheDirPath + File.separator + "cls.nb",
                0, 4, "LITE_POWER_HIGH");
    }

    private void loadLabels(Context context) {
        labels.clear();
        labels.add("black");

        try(InputStream inputStream = context.getAssets().open("labels/keys.txt")) {
            int available = inputStream.available();
            byte[] lines = new byte[available];
            inputStream.read(lines);
            String word = new String(lines);
            String[] words = word.split("\n");
            Collections.addAll(labels, words);
            labels.add(" ");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static native long init(String detModelPath, String recModelPath, String clsModelPath, int useOpencl, int threadNum, String cpuMode);
    public native float[] forward(long pointer, Bitmap originalImage, int max_size_len, int run_det, int run_cls, int run_rec);
    public native void release(long pointer);
}
