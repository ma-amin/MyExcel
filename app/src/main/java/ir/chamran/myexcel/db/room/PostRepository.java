package ir.chamran.myexcel.db.room;

import android.content.Context;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;

import ir.chamran.myexcel.db.room.callback.PostCallback;
import ir.chamran.myexcel.model.ShopDetails;

public class PostRepository {
    private AppExecutors mAppExecutors;
    private Context mContext;

    public PostRepository(@NonNull Context context) {
        mContext = context;
        mAppExecutors = AppExecutors.getInstance();
    }

    public void insertPost(final ShopDetails post, PostCallback callback) {
        final WeakReference<PostCallback> callbackWeakReference = new WeakReference<>(callback);

        mAppExecutors.diskIO().execute(() -> {
            AppDatabase.getInstance(mContext).postDao().insertPost(post);

            mAppExecutors.mainThread().execute(() -> {
                final PostCallback postCallback = callbackWeakReference.get();
                if (postCallback != null) {
                    postCallback.onInsertPost();
                }
            });
        });
    }
}
