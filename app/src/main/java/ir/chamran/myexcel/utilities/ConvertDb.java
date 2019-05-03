package ir.chamran.myexcel.utilities;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import org.json.JSONArray;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import ir.chamran.myexcel.R;
import ir.chamran.myexcel.db.excelFile.ReadExcel;
import ir.chamran.myexcel.db.excelFile.SQLiteToExcel;
import ir.chamran.myexcel.model.ShopDetails;

import static ir.chamran.myexcel.MyApp.directory_path;
import static ir.chamran.myexcel.db.room.AppDatabase.DATABASE_NAME;

public class ConvertDb {

    private Context context;
    private final String excelName = "appBackup.xls";

    public ConvertDb(Context context) {
        this.context = context;
    }

    public void onExportExcel(@NonNull SQLiteToExcel.ExportListener listener) {

        if (onFileExists(directory_path)) {
            // Export SQLite DB as EXCEL FILE
            SQLiteToExcel sqliteToExcel = new SQLiteToExcel(context.getApplicationContext(), DATABASE_NAME, directory_path);
            sqliteToExcel.exportSingleTable(ShopDetails.class.getSimpleName(), excelName, listener);
        } else {
            listener.onError(new Exception("The directory was not created"));
        }
    }

    public void onImportExcel(@NonNull ImportListener listener) {

        if (onFileExists(directory_path))
            onImport(directory_path + File.separator + excelName, listener);
        else
            listener.onError(new Exception("The directory was not created"));
    }

    public void onImportExcel(@NonNull String excelPath, @NonNull ImportListener listener) {
        onImport(excelPath, listener);
    }

    private void onImport(String excelPath, @NonNull ImportListener listener) {
        // Is used to import data from excel without dropping table
        // ExcelToSQLite excelToSQLite = new ExcelToSQLite(getApplicationContext(), DBHelper.DB_NAME);
        // if you want to add column in excel and import into DB, you must drop the table

        //ExcelToSQLite excelToSQLite = new ExcelToSQLite(context.getApplicationContext(), DATABASE_NAME, true);

        // Import EXCEL FILE to SQLite
        //excelToSQLite.importFromFile(excelPath, listener);

        ReadExcel excelToSQLite = new ReadExcel();
        excelToSQLite.importFromFile(excelPath, new ReadExcel.ImportListener() {
            @Override
            public void onStart() {
                listener.onStart();
            }

            @Override
            public void onCompleted(JSONArray jsonArray) {
                //Log.d("Mammad", "JSONArray.toString()>> " + jsonArray.toString());
                listener.onCompleted(getParsingJSONArray(jsonArray, ShopDetails[].class, context));
            }

            @Override
            public void onError(Exception e) {
                listener.onError(e);
            }
        });
    }

    private boolean onFileExists(String path) {
        File file = new File(path);
        if (!file.exists())
            return file.mkdirs();
        else
            return true;
    }

    public <T> List<T> getParsingJSONArray(JSONArray json, Class<T[]> classOfT, Context context) {
        try {
            return Arrays.asList(new Gson().fromJson(json.toString(), classOfT));

        } catch (Exception e) {
            Utils.showNetErrorToast(context, R.string.toast_excelError_import);
            return null;
        }
    }

    public interface ImportListener {
        void onStart();
        void onCompleted(List<ShopDetails> shopDetails);
        void onError(Exception e);
    }
}
