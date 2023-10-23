package top.bogey.touch_tool_pro.utils.ocr;

import android.graphics.Rect;

public class OcrResult {
    private final Rect area;
    private final String label;
    private final int similar;

    public OcrResult(Rect area, String label, int similar) {
        this.area = area;
        this.label = label;
        this.similar = similar;
    }

    public Rect getArea() {
        return area;
    }

    public String getLabel() {
        return label;
    }

    public int getSimilar() {
        return similar;
    }
}
