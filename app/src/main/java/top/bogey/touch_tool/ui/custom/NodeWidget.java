package top.bogey.touch_tool.ui.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.utils.DisplayUtils;

@SuppressLint("ViewConstructor")
public class NodeWidget<T extends ViewBinding> extends BindingView<T> {
    protected LinearLayout inBox;
    protected MaterialCardView topInBox;
    protected MaterialCardView bottomInBox;
    protected MaterialTextView titleText;
    protected MaterialButton button;

    public NodeWidget(@NonNull Context context, @Nullable AttributeSet attrs, Class<T> tClass) {
        super(context, attrs, tClass);
    }

    protected void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.NodeWidget);
        int mode = array.getInt(R.styleable.NodeWidget_mode, 0);
        int normalColor = DisplayUtils.getAttrColor(context, com.google.android.material.R.attr.colorPrimary, 0);
        int conditionColor = DisplayUtils.getAttrColor(context, com.google.android.material.R.attr.colorPrimaryInverse, 0);
        int outlineColor = DisplayUtils.getAttrColor(context, com.google.android.material.R.attr.colorOutline, 0);

        switch (mode) {
            case 0:
                topInBox.setCardBackgroundColor(normalColor);
                bottomInBox.setCardBackgroundColor(normalColor);
                break;
            case 1:
                topInBox.setCardBackgroundColor(conditionColor);
                bottomInBox.setCardBackgroundColor(conditionColor);
                break;
            case 2:
                topInBox.setCardBackgroundColor(normalColor);
                bottomInBox.setCardBackgroundColor(conditionColor);
                break;
            case 3:
                topInBox.setCardBackgroundColor(outlineColor);
                bottomInBox.setCardBackgroundColor(outlineColor);
                break;
        }

        String title = array.getString(R.styleable.NodeWidget_title);
        if (title != null && !title.isEmpty()) {
            titleText.setVisibility(VISIBLE);
            titleText.setText(title);
        }

        array.recycle();
    }
}
