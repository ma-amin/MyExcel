package ir.chamran.myexcel.db.excelFile;

import android.os.Handler;
import android.os.Looper;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ReadExcel {

    private static Handler handler = new Handler(Looper.getMainLooper());

    public ReadExcel() {
    }

    public void importFromFile(String filePath, ImportListener listener) {
        importFromFile(new File(filePath), listener);
    }

    private void importFromFile(final File file, final ImportListener listener) {
        if (listener != null) {
            listener.onStart();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONArray work = working(new FileInputStream(file));

                    if (listener != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onCompleted(work);
                            }
                        });
                    }

                } catch (final Exception e) {
                    if (listener != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onError(e);
                            }
                        });
                    }
                }
            }
        }).start();
    }

    private JSONArray working(InputStream stream) throws Exception {
        JSONArray jsonArray = new JSONArray();

        HSSFWorkbook workbook = new HSSFWorkbook(stream);
        try {

            Iterator<Row> rit = workbook.getSheetAt(0).rowIterator();
            Row rowHeader = rit.next();
            List<String> columns = new ArrayList<>();
            for (int i = 0; i < rowHeader.getPhysicalNumberOfCells(); i++) {
                columns.add(rowHeader.getCell(i).getStringCellValue());
            }

            while (rit.hasNext()) {
                Row row = rit.next();
                JSONObject jsonObject = new JSONObject();

                for (int n = 0; n < row.getPhysicalNumberOfCells(); n++) {

                    if (columns.get(n).equals("vaziyat")) {
                        String temp = row.getCell(n).getStringCellValue();
                        if (temp.equals("1"))
                            jsonObject.put(columns.get(n), true);
                        else
                            jsonObject.put(columns.get(n), false);

                    } else {
                        setJSONObject(jsonObject, columns.get(n), row.getCell(n));
                    }
                }

                jsonArray.put(jsonObject);
            }

            return jsonArray;

        } catch (Exception e) {
            e.printStackTrace();
            return jsonArray;
        }
    }

    private void setJSONObject(JSONObject jsonObject, String columns, Cell cell) throws JSONException {
        if (cell.getCellTypeEnum() == CellType.NUMERIC) {
            jsonObject.put(columns, cell.getNumericCellValue());

        } else {
            jsonObject.put(columns, cell.getStringCellValue());
        }
    }

    public interface ImportListener {
        void onStart();

        void onCompleted(JSONArray jsonArray);

        void onError(Exception e);
    }
}
