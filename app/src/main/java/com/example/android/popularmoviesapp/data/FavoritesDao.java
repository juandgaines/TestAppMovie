package com.example.android.popularmoviesapp.data;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface FavoritesDao {

    @Query("SELECT * FROM favorites ORDER BY id")
    LiveData<List<MovieData>> loadAllFavoritesMovies();

    @Query("SELECT * FROM cache_data WHERE category=:category")
    LiveData<List<CacheMovieData>> loadAllCacheMovies(String category);

    @Query("SELECT * FROM favorites WHERE movie_id=:id")
    LiveData<MovieData> loadFavoriteItemByName(int id);

    @Query("SELECT * FROM cache_data WHERE movie_id=:id")
    CacheMovieData loadCacheItemByName(int id);

    @Insert
    public void insertFavoriteMovie(MovieData movieData);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertMovieOnCache(CacheMovieData movieCache);


    @Update(onConflict = OnConflictStrategy.REPLACE)
    public void updateFavoriteMovie(MovieData movieData);

    @Delete
    public void deleteFavoriteMovie(MovieData movieData);

    @Query("DELETE FROM favorites")
    public void deleteAll();

}
