package com.example.android.popularmoviesapp;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.android.popularmoviesapp.data.AppDatabase;
import com.example.android.popularmoviesapp.data.AppExecutors;
import com.example.android.popularmoviesapp.data.CacheMovieData;
import com.example.android.popularmoviesapp.data.MovieData;
import com.example.android.popularmoviesapp.data.Result;
import com.example.android.popularmoviesapp.data.Results;
import com.example.android.popularmoviesapp.network.MovieService;
import com.example.android.popularmoviesapp.network.RetroClassMainListView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FetchViewModel extends AndroidViewModel {
    private static  final  String LOG_TAG=FetchViewModel.class.getSimpleName();
    private static final String API_KEY = BuildConfig.OPEN_THE_MOVIE_DB_API_KEY;
    private MutableLiveData<List<Result>> resultsLiveData;
    private MutableLiveData<BooleanJ> mNetworkProblem;
    private LiveData<List<MovieData>> resultsLiveDataDB;
    private LiveData<List<CacheMovieData>> mCacheListMovies;
    private MutableLiveData<List<Result>> resultsLiveDataByQuery;

    private  AppDatabase database;


    public FetchViewModel(@NonNull Application application) {
        super(application);
        database=AppDatabase.getsInstance(this.getApplication());
        resultsLiveDataDB=database.favoritesDao().loadAllFavoritesMovies();

    }


    public LiveData<BooleanJ> getmNetworkProblem() {

        if( mNetworkProblem==null){
            mNetworkProblem=new MutableLiveData<>();
        }
        return mNetworkProblem;
    }

    public LiveData<List<Result>> getResultsLiveData(String pref, String apiKey){
        if (resultsLiveData==null ){

            resultsLiveData=new MutableLiveData<>();
            loadLiveData(pref);
        }


        return resultsLiveData;
    }

    public LiveData<List<Result>> getResultsLiveDataByQuery(String pref, String apiKey){

        if (resultsLiveDataByQuery==null ){

            resultsLiveDataByQuery=new MutableLiveData<>();
            loadLiveDataByQuery(pref);
        }


        return resultsLiveDataByQuery;
    }

    public void initNetLiveData(){
        if( mNetworkProblem==null){
            mNetworkProblem=new MutableLiveData<>();
        }
    }

    public void loadLiveData(final String pref){

        MovieService movieService= RetroClassMainListView.getMovieService();
        initNetLiveData();



        if(!pref.equals("favorites")) {
            movieService.getMovies(pref, API_KEY).enqueue(new Callback<Results>() {
                @Override
                public void onResponse(Call<Results> call, Response<Results> response) {

                    Log.v("Retrofit", response.toString());
                    List<Result> res = response.body().getResults();



                    for (final Result cacheMovies:res){

                        //int movie_id, Double rate, String title, String path,String overview,String release,String category)
                        final int movie_id=cacheMovies.getId().intValue();


                        AppExecutors.getsInstance().diskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                CacheMovieData co=database.favoritesDao().loadCacheItemByName(movie_id);
                                if (co==null) {

                                    Double rate = cacheMovies.getVoteAverage();
                                    String title = cacheMovies.getTitle();
                                    String path = cacheMovies.getPosterPath();
                                    String overview = cacheMovies.getOverview();
                                    String release = cacheMovies.getReleaseDate();
                                    String category = pref;
                                    final CacheMovieData bufferObject=  new CacheMovieData(movie_id,rate,title,path,overview,release,category);
                                    Log.d(LOG_TAG,"Inserted on Database:"+bufferObject.toString());
                                    database.favoritesDao().insertMovieOnCache(bufferObject);

                                }


                            }
                        });






                    }
                    resultsLiveData.setValue(res);


                    BooleanJ booleanJ = new BooleanJ();
                    booleanJ.setStatus(false);
                    mNetworkProblem.setValue(booleanJ);

                }

                @Override
                public void onFailure(Call<Results> call, Throwable t) {
                    Log.v("Retrofit", "No internet connection");
                    BooleanJ booleanJ = new BooleanJ();
                    booleanJ.setStatus(true);
                    mNetworkProblem.setValue(booleanJ);

                }
            });
        }
        else{

        }


    }
    public void loadLiveDataByQuery(final String pref){

        MovieService movieService= RetroClassMainListView.getMovieService();
        initNetLiveData();



        if(!pref.equals("favorites")) {
            movieService.getMoviesByQuery(pref, API_KEY).enqueue(new Callback<Results>() {
                @Override
                public void onResponse(Call<Results> call, Response<Results> response) {

                    Log.v("Retrofit", response.toString());
                    List<Result> res = response.body().getResults();

                    for (final Result cacheMovies:res){
                        Log.v("Retrofit-pelis", cacheMovies.toString());
                        //int movie_id, Double rate, String title, String path,String overview,String release,String category)
                        final int movie_id=cacheMovies.getId().intValue();


                        AppExecutors.getsInstance().diskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                CacheMovieData co=database.favoritesDao().loadCacheItemByName(movie_id);
                                if (co==null) {

                                    Double rate = cacheMovies.getVoteAverage();
                                    String title = cacheMovies.getTitle();
                                    String path = cacheMovies.getPosterPath();
                                    String overview = cacheMovies.getOverview();
                                    String release = cacheMovies.getReleaseDate();
                                    String category = pref;
                                    final CacheMovieData bufferObject=  new CacheMovieData(movie_id,rate,title,path,overview,release,category);
                                    database.favoritesDao().insertMovieOnCache(bufferObject);

                                }


                            }
                        });






                    }
                    resultsLiveDataByQuery.setValue(res);


                    BooleanJ booleanJ = new BooleanJ();
                    booleanJ.setStatus(false);
                    mNetworkProblem.setValue(booleanJ);

                }

                @Override
                public void onFailure(Call<Results> call, Throwable t) {
                    Log.v("Retrofit", "No internet connection");
                    BooleanJ booleanJ = new BooleanJ();
                    booleanJ.setStatus(true);
                    mNetworkProblem.setValue(booleanJ);

                }
            });
        }
        else{

        }


    }



    public LiveData<List<CacheMovieData>> loadLiveDataFromCache(final String pref){


        mCacheListMovies= database.favoritesDao().loadAllCacheMovies(pref);

        return mCacheListMovies;


    }

    public LiveData <List<MovieData>> getFavorites(){

        return resultsLiveDataDB;

    }





}
