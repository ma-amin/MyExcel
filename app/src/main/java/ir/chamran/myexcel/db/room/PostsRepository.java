package ir.chamran.myexcel.db.room;

import android.content.Context;

import androidx.annotation.NonNull;

import java.util.List;

import ir.chamran.myexcel.model.ShopDetails;

public class PostsRepository {
    private AppExecutors mAppExecutors;
    private Context mContext;

    public PostsRepository(@NonNull Context context) {
        mContext = context;
        mAppExecutors = AppExecutors.getInstance();
    }

    public void insertPosts(final List<ShopDetails> posts) {
        mAppExecutors.diskIO().execute(() -> {
            AppDatabase.getInstance(mContext).postDao().insertPostAll(posts);
        });
    }
}
