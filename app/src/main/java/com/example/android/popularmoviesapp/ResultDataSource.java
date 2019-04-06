package com.example.android.popularmoviesapp;

import android.arch.paging.PageKeyedDataSource;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.example.android.popularmoviesapp.data.Result;
import com.example.android.popularmoviesapp.data.Results;
import com.example.android.popularmoviesapp.network.RetroClassMainListView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResultDataSource extends PageKeyedDataSource<Integer,Result> {

    private static final int FIRST_PAGE=1;

    private static final String apiKey = BuildConfig.OPEN_THE_MOVIE_DB_API_KEY;



    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull final LoadInitialCallback<Integer, Result> callback) {

        //SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences();
        //String syncConnPref = sharedPref.getString( getResources().getString(R.string.pref_order_key),"");

        RetroClassMainListView.getMovieService().getMoviesPaged("popular", FIRST_PAGE, apiKey).enqueue(new Callback<Results>() {
            @Override
            public void onResponse(Call<Results> call, Response<Results> response) {

                if(response.body()!=null){
                    callback.onResult(response.body().getResults(),null,FIRST_PAGE+1);
                }
            }

            @Override
            public void onFailure(Call<Results> call, Throwable t) {

            }
        });
    }

    @Override
    public void loadBefore(@NonNull final LoadParams<Integer> params, @NonNull final LoadCallback<Integer, Result> callback) {

        RetroClassMainListView.getMovieService().getMoviesPaged("popular",params.key,apiKey).enqueue(new Callback<Results>() {
            @Override
            public void onResponse(Call<Results> call, Response<Results> response) {

                if(response.body()!=null){

                    Integer key= (params.key>1)? params.key-1:null;

                    callback.onResult(response.body().getResults(),key);
                }
            }

            @Override
            public void onFailure(Call<Results> call, Throwable t) {

            }
        });

    }

    @Override
    public void loadAfter(@NonNull final LoadParams<Integer> params, @NonNull final LoadCallback<Integer, Result> callback) {

        RetroClassMainListView.getMovieService().getMoviesPaged("popular",params.key,apiKey).enqueue(new Callback<Results>() {
            @Override
            public void onResponse(Call<Results> call, Response<Results> response) {

                if(response.body()!=null){


                    Integer key= (response.body().getTotalPages()>params.key)? params.key+1:null;

                    callback.onResult(response.body().getResults(),key);
                }
            }

            @Override
            public void onFailure(Call<Results> call, Throwable t) {

            }
        });
    }
}
