package com.example.android.popularmoviesapp;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.SearchManager;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmoviesapp.data.AppDatabase;
import com.example.android.popularmoviesapp.data.CacheMovieData;
import com.example.android.popularmoviesapp.data.MovieData;
import com.example.android.popularmoviesapp.data.Result;

import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;




public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler
        ,SharedPreferences.OnSharedPreferenceChangeListener,FavoriteMovieAdapter.MovieAdapterOnClickHandler {

    public static final String LOG_TAG=MainActivity.class.getName().toString();
    public static final String KEY_RECYCLERVIEW_1="position1";
    public static final String KEY_RECYCLERVIEW_2="position2";

    private Parcelable mRecyclerViewState1;
    private Parcelable mRecyclerViewState2;

    private String apiKey;

    private boolean isConnected;
    private FetchViewModel fetchViewModel;
    AppDatabase mDb;

    private int width;
    private int height;

    @BindView(R.id.my_recycler_view)  RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private FavoriteMovieAdapter mFavoriteMovieAdapter;
    @BindView(R.id.pb_loading_indicator)ProgressBar mLoadingIndicator;
    @BindView(R.id.tv_error_message_display)TextView mErrorMessage;
    @BindView(R.id.my_favortire_recycler_view) RecyclerView mRecyclerView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Intent intent=getIntent();
        if(intent!=null ) {
            handleIntent(intent);
        }

        FetchMode();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelable(KEY_RECYCLERVIEW_1, mRecyclerView.getLayoutManager().onSaveInstanceState());
        savedInstanceState.putParcelable(KEY_RECYCLERVIEW_2, mRecyclerView2.getLayoutManager().onSaveInstanceState());// get current recycle view position here.

    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if(savedInstanceState!=null){
            mRecyclerViewState1 = savedInstanceState.getParcelable(KEY_RECYCLERVIEW_1);
            mRecyclerViewState2 = savedInstanceState.getParcelable(KEY_RECYCLERVIEW_2);
        }
    }

    void FetchMode(){
        PreferenceManager.setDefaultValues(this,R.xml.pref_general,false);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.registerOnSharedPreferenceChangeListener(this);
        String syncConnPref = sharedPref.getString(getResources().getString(R.string.pref_order_key),"");
        String apiKey = BuildConfig.OPEN_THE_MOVIE_DB_API_KEY;


        Resources res = getResources();
        String[] moviesValues = res.getStringArray(R.array.pref_order_peliculas_values);
        String value= moviesValues[3];
        Log.d(LOG_TAG,"Que es :"+ syncConnPref + "=="+ value);

        if(!syncConnPref.equals(value)) {
            mErrorMessage.setVisibility(View.INVISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
            mLoadingIndicator.setVisibility(View.VISIBLE);
            mRecyclerView2.setVisibility(View.GONE);
        }
        else{
            mErrorMessage.setVisibility(View.INVISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            mRecyclerView2.setVisibility(View.VISIBLE);
        }


        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;

        fetchViewModel= ViewModelProviders.of(this).get(FetchViewModel.class);

        GridLayoutManager layoutManager2;


        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // In landscape
            layoutManager2= new GridLayoutManager(this,5,GridLayoutManager.VERTICAL,false);
        }
        else {
            // In portrait
            layoutManager2= new GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false);
        }

        if( orientation != Configuration.ORIENTATION_LANDSCAPE && dpWidth>=600) {
            layoutManager2= new GridLayoutManager(this,3,GridLayoutManager.VERTICAL,false);
        }


        if (mRecyclerViewState2 != null) {
            layoutManager2.onRestoreInstanceState(mRecyclerViewState2);
        }
        mRecyclerView2.setLayoutManager(layoutManager2);
        mRecyclerView2.setHasFixedSize(true);


        Log.v("Width",Integer.toString(width));

        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();


        if (isConnected) {
            onlineFetch(syncConnPref,apiKey);
        }
        else {
            offlineFetch(null,syncConnPref);

        }


        fetchViewModel.getFavorites().observe(this, new Observer<List<MovieData>>() {
            @Override
            public void onChanged(@Nullable List<MovieData> movieData) {
                mFavoriteMovieAdapter = new FavoriteMovieAdapter(MainActivity.this,movieData, width,height);

                mRecyclerView2.setAdapter(mFavoriteMovieAdapter);


            }
        });

        GridLayoutManager layoutManager;

        if (orientation == Configuration.ORIENTATION_LANDSCAPE ) {
            // In landscape
            layoutManager= new GridLayoutManager(this,5,GridLayoutManager.VERTICAL,false);
        }else if( dpWidth>=600) {
            layoutManager= new GridLayoutManager(this,3,GridLayoutManager.VERTICAL,false);

        }
        else {
            // In portrait
            layoutManager= new GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false);
        }
        if (mRecyclerViewState1 != null) {
            layoutManager.onRestoreInstanceState(mRecyclerViewState1);
        }

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);



    }



    @Override
    public void onClick(Result movieData,View view) {
        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intent= new Intent(context,destinationClass);
        String title = movieData.getTitle();
        String overview = movieData.getOverview();
        String release = movieData.getReleaseDate();
        Log.v("Release:", release);
        double rate = movieData.getVoteAverage();
        String path = movieData.getPosterPath();
        int id_movie=movieData.getId();


        MovieData movie = new MovieData(title,overview,rate,release, path,id_movie);
        intent.putExtra(MovieData.PARCELABLE,movie);
        Bundle bundle= ActivityOptions.makeSceneTransitionAnimation(this,view,
                getResources().getString(R.string.transition_image_name)
                ).toBundle();
        startActivity(intent,bundle);
    }

    @Override
    public void onClick(MovieData movieData) {

        Intent intent =new Intent(this, DetailActivity.class);

        intent.putExtra(MovieData.PARCELABLE,movieData);
        startActivity(intent);

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(getString(R.string.pref_order_key))){

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            String syncConnPref = sharedPref.getString(getResources().getString(R.string.pref_order_key),"");

            Resources res = getResources();
            String[] moviesValues = res.getStringArray(R.array.pref_order_peliculas_values);
            String value= moviesValues[3];

            ConnectivityManager cm =
                    (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();

            if(!syncConnPref.equals(value)) {
                mErrorMessage.setVisibility(View.INVISIBLE);
                mRecyclerView.setVisibility(View.VISIBLE);
                mLoadingIndicator.setVisibility(View.VISIBLE);
                mRecyclerView2.setVisibility(View.GONE);

                if (isConnected){
                    onlineFetch(syncConnPref,apiKey);
                }
                else {
                    offlineFetch(null,syncConnPref);

                }

            }
            else{
                mErrorMessage.setVisibility(View.INVISIBLE);
                mRecyclerView.setVisibility(View.GONE);
                mRecyclerView2.setVisibility(View.VISIBLE);


            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater searchInflater =getMenuInflater();
        searchInflater.inflate(R.menu.main, menu);

        MenuItem searchItem= menu.findItem(R.id.action_search);
        SearchView searchView= (SearchView) searchItem.getActionView();


        SearchManager searchManager=(SearchManager) getSystemService(Context.SEARCH_SERVICE);
        ComponentName componentName= new ComponentName(this,MainActivity.class);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName));

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {

                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {


                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                String syncConnPref = sharedPref.getString(getResources().getString(R.string.pref_order_key),"");
                mMovieAdapter.restartSearch();
                if(isConnected) {

                    onlineFetch(syncConnPref,apiKey);

                }
                else {
                    offlineFetch(null,syncConnPref);
                }

                return true;
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //Toast.makeText(MainActivity.this,"Enviar",Toast.LENGTH_LONG).show();
                PreferenceManager.setDefaultValues(MainActivity.this,R.xml.pref_general,false);

                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                sharedPref.registerOnSharedPreferenceChangeListener(MainActivity.this);

                String syncConnPref = sharedPref.getString(getResources().getString(R.string.pref_order_key),"");
                if (isConnected) {
                    onlineFetchWithQuery(s,syncConnPref,apiKey);

                }

                else {
                    offlineFetch(s, syncConnPref);

                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(isConnected) {
                    mMovieAdapter.getFilter().filter(s);
                }
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void onlineFetchWithQuery(String s, String syncConnPref, String apiKey) {
        fetchViewModel.getResultsLiveDataByQuery(s, syncConnPref,apiKey).observe(MainActivity.this, new Observer<List<Result>>() {
            @Override
            public void onChanged(@Nullable List<Result> results) {

                mMovieAdapter = new MovieAdapter(MainActivity.this, results, width, height);

                mRecyclerView.setAdapter(mMovieAdapter);
                showMovieDataView();
                runAnimation(mRecyclerView);

            }
        });
        fetchViewModel.loadLiveDataByQuery(s,syncConnPref);
    }


    private void offlineFetch(String query, String syncConnPref) {
        final Snackbar sn=Snackbar.make(findViewById(R.id.coordinator_layout), "You are in offline mode",Snackbar.LENGTH_INDEFINITE);

        sn.setAction("Ok", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sn.dismiss();
            }
        });

        sn.show();
        fetchViewModel.loadLiveDataFromCache(query,syncConnPref).observe(this, new Observer<List<CacheMovieData>>() {
            @Override
            public void onChanged(@Nullable List<CacheMovieData> cacheMovieData) {

                List<Result> resultListFromCache = new ArrayList<Result>();



                for (CacheMovieData cm : cacheMovieData) {
                    Log.d(LOG_TAG, "Before-BD content:"+cm.toString() );
                    //Result( int movie_id, Double rate, String title, String path,String overview,String release)
                    int movie_id = cm.getMovie_id();
                    Double rate = cm.getRate();
                    String title = cm.getTitle();
                    String path = cm.getPath();
                    String overview = cm.getOverview();
                    String release = cm.getRelease();

                    Result obj = new Result(new Integer(movie_id), rate, title, path, overview, release);
                    Log.d(LOG_TAG, "After-BD content:"+obj.toString() );
                    resultListFromCache.add(obj);

                }

                mMovieAdapter = new MovieAdapter(MainActivity.this, resultListFromCache, width, height);
                mRecyclerView.setAdapter(mMovieAdapter);
                showMovieDataView();
                runAnimation(mRecyclerView);
            }
        });
    }
    private void onlineFetch(String syncConnPref, String apiKey) {

        fetchViewModel.getResultsLiveData(syncConnPref, apiKey).observe(MainActivity.this, new Observer<List<Result>>() {
            @Override
            public void onChanged(@Nullable List<Result> results) {

                mMovieAdapter = new MovieAdapter(MainActivity.this, results, width, height);

                mRecyclerView.setAdapter(mMovieAdapter);
                showMovieDataView();
                runAnimation(mRecyclerView);

            }
        });
        fetchViewModel.loadLiveData(syncConnPref);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent startIntentSettings = new Intent(this, SettingsActivity.class);
            startActivity(startIntentSettings);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showMovieDataView() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.VISIBLE);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.unregisterOnSharedPreferenceChangeListener(this);
    }

    private void runAnimation(RecyclerView recyclerView){

        Context context=recyclerView.getContext();
        LayoutAnimationController controller= AnimationUtils.loadLayoutAnimation(context,R.anim.layout_slide_from_bottom);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();

    }

    private void handleIntent(Intent intent){
        if(Intent.ACTION_SEARCH.equals(intent.getAction())){
            String query= intent.getStringExtra(SearchManager.QUERY);

            PreferenceManager.setDefaultValues(MainActivity.this,R.xml.pref_general,false);

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            sharedPref.registerOnSharedPreferenceChangeListener(MainActivity.this);

            String syncConnPref = sharedPref.getString(getResources().getString(R.string.pref_order_key),"");

            if(isConnected) {

                onlineFetchWithQuery(query,syncConnPref,apiKey);

            }
            else {
                offlineFetch(query,syncConnPref);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }


}
