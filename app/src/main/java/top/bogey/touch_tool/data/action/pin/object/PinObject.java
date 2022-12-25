package top.bogey.touch_tool.data.action.pin.object;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import top.bogey.touch_tool.utils.DisplayUtils;

public abstract class PinObject implements Parcelable {
    private final String cls;

    public PinObject() {
        cls = getClass().getSimpleName();
    }

    public PinObject(Parcel in) {
        cls = in.readString();
    }

    public static final Creator<PinObject> CREATOR = new Creator<PinObject>() {
        @Override
        public PinObject createFromParcel(Parcel in) {
            String cls = in.readString();
            Package aPackage = getClass().getPackage();
            try {
                Class<?> aClass = Class.forName(aPackage.getName() + "." + cls);
                Constructor<?> constructor = aClass.getConstructor(Parcel.class);
                Object o = constructor.newInstance(in);
                return (PinObject) o;
            } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
                     IllegalAccessException | InstantiationException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public PinObject[] newArray(int size) {
            return new PinObject[size];
        }
    };

    public int getPinColor(Context context) {
        return DisplayUtils.getAttrColor(context, com.google.android.material.R.attr.colorSurfaceVariant, 0);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(cls);
    }
}
