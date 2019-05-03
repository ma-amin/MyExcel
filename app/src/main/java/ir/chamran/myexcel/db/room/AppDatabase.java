package ir.chamran.myexcel.db.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import ir.chamran.myexcel.model.ShopDetails;

@Database(entities = {ShopDetails.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "SHOPCDB";
    private volatile static AppDatabase INSTANCE;

    public abstract PostDao postDao();

    private static final Object sLock = new Object();

    public static AppDatabase getInstance(Context context) {
        synchronized (sLock) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, DATABASE_NAME)
                        .build();
            }
            return INSTANCE;
        }
    }
}
