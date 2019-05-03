package ir.chamran.myexcel.utilities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import java.util.Objects;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class Utils {
    private static final String ROOT = "fonts/", fontAwesome = ROOT + "awesome_solid.ttf",
            fontIRANSans = ROOT + "IRANSansMobile.ttf", fontIRANSansBold = ROOT + "IRANSansMobile_Bold.ttf";

    public static void setFontIcon(Context context, TextView textView) {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), fontAwesome);
        textView.setTypeface(typeface);
    }

    public static Typeface getTypeface(Context context) {
        return Typeface.createFromAsset(context.getAssets(), fontIRANSans);
    }

    public static Typeface getTypefaceBold(Context context) {
        return Typeface.createFromAsset(context.getAssets(), fontIRANSansBold);
    }

    public static void hideKeyboard(Activity activity){
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        Objects.requireNonNull(imm).hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showNetErrorToast(@NonNull Context mContext, @StringRes int mess){
        Toast.makeText(mContext, mess, Toast.LENGTH_SHORT).show();
    }
}
