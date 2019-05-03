package ir.chamran.myexcel.db.room;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import ir.chamran.myexcel.model.ShopDetails;

public class PostViewModel extends AndroidViewModel {

    private final LiveData<ShopDetails> shopDetails;
    //public ObservableField<ShopDetails> product = new ObservableField<>();

    public PostViewModel(@NonNull Application application, final int productId) {
        super(application);

        PostDao postDao = AppDatabase.getInstance(application).postDao();
        shopDetails = postDao.getPost(productId);
    }

    public LiveData<ShopDetails> getPost() {
        return shopDetails;
    }

    /*public void setProduct(ShopDetails product) {
        this.product.set(product);
    }*/

    /*public void insert(ShopDetails shopDetails) {
        new insertAsyncTask(postDao).execute(shopDetails);
    }

    private static class insertAsyncTask extends AsyncTask<ShopDetails, Void, Void> {
        private PostDao dao;

        insertAsyncTask(PostDao postDao) {
            dao = postDao;
        }

        @Override
        protected Void doInBackground(final ShopDetails... params) {
            dao.insertPost(params[0]);
            return null;
        }
    }*/

    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        @NonNull
        private final Application mApplication;
        private final int mProductId;

        public Factory(@NonNull Application application, int productId) {
            mApplication = application;
            mProductId = productId;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(PostViewModel.class)) {
                return (T) new PostViewModel(mApplication, mProductId);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
