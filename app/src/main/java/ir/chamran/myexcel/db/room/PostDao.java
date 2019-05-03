package ir.chamran.myexcel.db.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import ir.chamran.myexcel.model.ShopDetails;

@Dao
public interface PostDao {

    @Query("SELECT * FROM ShopDetails ORDER BY vaziyat ASC, radif DESC")
    LiveData<List<ShopDetails>> getPostAll();

    @Query("SELECT * FROM ShopDetails WHERE radif = :postID ")
    LiveData<ShopDetails> getPost(int postID);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPostAll(List<ShopDetails> posts);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPost(ShopDetails post);

   /* @Update
    void updatePost(ShopDetails post);*/

    @Query("DELETE FROM ShopDetails WHERE radif = :postID")
    void deleteByPostId(int postID);
/*
    @Delete
    void deletePost(ShopDetails post);

    @Query("DELETE FROM ShopDetails")
    void deleteAll();

    @Delete
    void deletePostAll(List<ShopDetails> posts);*/
}
