package ir.chamran.myexcel.db.room;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.List;

import ir.chamran.myexcel.model.ShopDetails;

public class PostsViewModel extends AndroidViewModel {

    private PostDao postDao;
    private final MediatorLiveData<List<ShopDetails>> shopDetailsList;

    public PostsViewModel(Application application) {
        super(application);

        shopDetailsList = new MediatorLiveData<>();
        shopDetailsList.setValue(null);

        postDao = AppDatabase.getInstance(application).postDao();
        LiveData<List<ShopDetails>> postAll = postDao.getPostAll();

        shopDetailsList.addSource(postAll, shopDetailsList::setValue);
    }

    public LiveData<List<ShopDetails>> getPosts() {
        return shopDetailsList;
    }

    public void delete(int postID) {
        new deleteAsyncTask(postDao).execute(postID);
    }

    private static class deleteAsyncTask extends AsyncTask<Integer, Void, Void> {
        private PostDao dao;

        deleteAsyncTask(PostDao postDao) {
            dao = postDao;
        }

        @Override
        protected Void doInBackground(final Integer... params) {
            dao.deleteByPostId(params[0]);
            return null;
        }
    }
}
