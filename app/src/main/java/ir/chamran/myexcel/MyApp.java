package ir.chamran.myexcel;

import android.os.Environment;

import androidx.multidex.MultiDexApplication;

import com.armdroid.rxfilechooser.RxFileChooser;

import java.io.File;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;

public class MyApp extends MultiDexApplication {

    public static String directory_path;

    @Override
    public void onCreate() {
        super.onCreate();

        directory_path = Environment.getExternalStorageDirectory().getPath() + File.separator + getResources().getString(R.string.app_name);

        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/IRANSansMobile.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());

        RxFileChooser.register(this);
    }
}
