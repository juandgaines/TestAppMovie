package com.example.android.popularmoviesapp;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.android.popularmoviesapp.data.AppDatabase;
import com.example.android.popularmoviesapp.data.MovieData;
import com.example.android.popularmoviesapp.data.Result;
import com.example.android.popularmoviesapp.data.Results;
import com.example.android.popularmoviesapp.network.MovieService;
import com.example.android.popularmoviesapp.network.RetroClassMainListView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FetchViewModel extends AndroidViewModel {
    private static final String API_KEY = BuildConfig.OPEN_THE_MOVIE_DB_API_KEY;
    private MutableLiveData<List<Result>> resultsLiveData;
    private MutableLiveData<BooleanJ> mNetworkProblem;
    private LiveData<List<MovieData>> resultsLiveDataDB;

    public FetchViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database=AppDatabase.getsInstance(this.getApplication());
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

    public void initNetLiveData(){
        if( mNetworkProblem==null){
            mNetworkProblem=new MutableLiveData<>();
        }
    }

    public void loadLiveData(String pref){

        MovieService movieService= RetroClassMainListView.getMovieService();
        initNetLiveData();



        if(!pref.equals("favorites")) {
            movieService.getMovies(pref, API_KEY).enqueue(new Callback<Results>() {
                @Override
                public void onResponse(Call<Results> call, Response<Results> response) {

                    Log.v("Retrofit", response.toString());
                    List<Result> res = response.body().getResults();
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

    public LiveData <List<MovieData>> getFavorites(){

        return resultsLiveDataDB;

    }





}
