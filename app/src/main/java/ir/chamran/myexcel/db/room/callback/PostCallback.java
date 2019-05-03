package ir.chamran.myexcel.db.room.callback;

import androidx.annotation.MainThread;

import ir.chamran.myexcel.model.ShopDetails;

public interface PostCallback {

    @MainThread
    void onInsertPost();
}
