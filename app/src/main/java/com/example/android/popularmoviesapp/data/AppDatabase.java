package com.example.android.popularmoviesapp.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.util.Log;

@Database(entities = {MovieData.class},version = 1,exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static final String LOG_TAG=AppDatabase.class.getSimpleName();
    private static final Object LOCK= new Object();
    private static final String DATABASE_NAME= "movies_list";

    private static AppDatabase sInstance;

    public static AppDatabase getsInstance(Context context){

        if(sInstance==null){

            synchronized (LOCK){
                Log.d(LOG_TAG,"Creating new database instance");
                sInstance= Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class,AppDatabase.DATABASE_NAME)
                        //Remove this line when you made sure the DB is working.
                        //.allowMainThreadQueries()
                        .build();
            }
        }
        Log.v(LOG_TAG,"Getting database instance");
        return sInstance;
    }

    public abstract FavoritesDao favoritesDao();




}
