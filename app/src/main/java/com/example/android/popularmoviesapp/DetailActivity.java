package com.example.android.popularmoviesapp;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.graphics.Matrix;
import android.graphics.Movie;
import android.net.Uri;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmoviesapp.data.AppDatabase;
import com.example.android.popularmoviesapp.data.AppExecutors;
import com.example.android.popularmoviesapp.data.ResultReviews;
import com.example.android.popularmoviesapp.data.ResultTrailers;
import com.example.android.popularmoviesapp.data.Review;
import com.example.android.popularmoviesapp.data.Trailer;
import com.example.android.popularmoviesapp.databinding.ActivityDetailBinding;
import com.example.android.popularmoviesapp.network.MovieService;
import com.example.android.popularmoviesapp.network.RetroClassMainListView;
import com.squareup.picasso.Picasso;

import com.example.android.popularmoviesapp.data.MovieData;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity implements TrailersAdapter.TrailerAdapterOnClickHandler{
    private static final String LOG_TAG= DetailActivity.class.getName().toString();
    //ActivityDetailBinding mBinding;
    private ReviewsAdapter reviewsAdapter;
    private TrailersAdapter trailersAdapter;
    public static final String KEY_RECYCLERVIEW_1="position1";
    public static final String KEY_RECYCLERVIEW_2="position2";
    private Parcelable mRecyclerViewState1;
    private Parcelable mRecyclerViewState2;

    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;

    @BindView(R.id.favoriteButton)ImageView favoriteButton;

    @BindView(R.id.trailer_listview)RecyclerView trailerListview;

    @BindView(R.id.empty_view_trailers)TextView emptyViewTrailers;
    @BindView(R.id.reviews_listview)RecyclerView reviewsListview;
    @BindView(R.id.empty_view_reviews)TextView emptyViewReviews;

    @BindView(R.id.scrollview_detail)ScrollView scrollviewDetail;

    @BindView(R.id.name_movie)TextView nameMovie;
    @BindView(R.id.date_view)TextView dateView;

    @BindView(R.id.rate_view)TextView rateView;

    @BindView(R.id.overview_parts) View overviewParts;
    @BindView(R.id.movie_picture) ImageView moviePicture;

    Matrix matrix;
    AppDatabase mDb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        matrix = new Matrix();
        matrix.postScale(1.8f, 1.8f);
        matrix.postTranslate(0,-100);

        // my_child_toolbar is defined in the layout file
        Toolbar myChildToolbar =
                (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(myChildToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        Intent intent=getIntent();

        //mBinding=DataBindingUtil.setContentView(this,R.layout.activity_detail);

        //mBinding.

        mDb= AppDatabase.getsInstance(getApplicationContext());

        if(intent!=null && intent.hasExtra(MovieData.PARCELABLE)){

            final MovieData movieData = intent.getParcelableExtra(MovieData.PARCELABLE);
            final int id_movie=movieData.getMovieId();
            final String mTitleStr =movieData.getTitle();
            final String mPathStr=movieData.getPosterPath();
            final String mOverviewStr=movieData.getOverview();
            final String mRateStr= Double.toString(movieData.getVoteAverage()) ;
            final String mDateStr=movieData.getReleaseDate();

            Log.v(LOG_TAG,"MovieData"+ movieData.toString());

            DetailViewModelFactory factory =new DetailViewModelFactory(mDb,id_movie);

            final DetailActivityViewModel viewModel=
                    ViewModelProviders.of(this,factory).get(DetailActivityViewModel.class);


            viewModel.getmCurrentMovie().observe(this, new Observer<MovieData>() {
                @Override
                public void onChanged(@Nullable MovieData movieData) {
                    if(viewModel.getmCurrentMovie().getValue()!=null){

                        favoriteButton.setImageDrawable(getResources().getDrawable(android.R.drawable.btn_star_big_on));
                    }
                    else{
                        favoriteButton.setImageDrawable(getResources().getDrawable(android.R.drawable.btn_star_big_off));
                    }

                    populateUI(id_movie,mTitleStr,mPathStr,mOverviewStr,mRateStr,mDateStr);
                }
            });



            viewModel.getTrailersLiveData(id_movie).observe(this, new Observer<List<Trailer>>() {
                @Override
                public void onChanged(@Nullable List<Trailer> trailers) {

                    trailersAdapter= new TrailersAdapter(DetailActivity.this,trailers);
                    //trailersAdapter.setEmptyView(R.id.empty_view_reviews);
                    trailerListview.setAdapter(trailersAdapter);

                    if(trailersAdapter.getItemCount()!=0){
                        trailerListview.setVisibility(View.VISIBLE);
                        emptyViewTrailers.setVisibility(View.GONE);
                    }
                    else{
                        trailerListview.setVisibility(View.GONE);
                        emptyViewTrailers.setVisibility(View.VISIBLE);

                    }

                }
            });

            viewModel.getReviewLiveData(id_movie).observe(this, new Observer<List<Review>>() {
                @Override
                public void onChanged(@Nullable List<Review> reviews) {

                    reviewsAdapter= new ReviewsAdapter(reviews);
                    reviewsListview.setAdapter(reviewsAdapter);

                    if(reviewsAdapter.getItemCount()!=0){
                        reviewsListview.setVisibility(View.VISIBLE);
                        emptyViewReviews.setVisibility(View.GONE);
                    }
                    else{
                        reviewsListview.setVisibility(View.GONE);
                        emptyViewReviews.setVisibility(View.VISIBLE);

                    }


                }
            });


            int orientation = getResources().getConfiguration().orientation;
            LinearLayoutManager layoutManager;



            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                // In landscape
                layoutManager= new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
            }
            else {
                // In portrait
                layoutManager= new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
            }

            if (mRecyclerViewState1 != null) {
                layoutManager.onRestoreInstanceState(mRecyclerViewState1);
            }

            LinearLayoutManager layoutManager2= new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);

            if (mRecyclerViewState2 != null) {
                layoutManager2.onRestoreInstanceState(mRecyclerViewState2);
            }


            reviewsListview.setLayoutManager(layoutManager2);
            trailerListview.setLayoutManager(layoutManager);



            trailerListview.setHasFixedSize(true);
           reviewsListview.setHasFixedSize(true);

            favoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {

                    final MovieData movieCache=new MovieData(mTitleStr,mOverviewStr,Double.parseDouble(mRateStr) ,mDateStr, mPathStr,id_movie);

                    AppExecutors.getsInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            if(viewModel.getmCurrentMovie().getValue()!=null){
                                mDb.favoritesDao().deleteFavoriteMovie(viewModel.getmCurrentMovie().getValue());
                                Log.v(LOG_TAG,"MovieData removed");
                            }
                            else {
                                mDb.favoritesDao().insertFavoriteMovie(movieCache);
                                Log.v(LOG_TAG,"MovieData inserted");
                            }
                        }
                    });

                }
            });


        }



    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelable(KEY_RECYCLERVIEW_1,trailerListview.getLayoutManager().onSaveInstanceState());
        savedInstanceState.putParcelable(KEY_RECYCLERVIEW_2, reviewsListview.getLayoutManager().onSaveInstanceState());// get current recycle view position here.

    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if(savedInstanceState!=null){
            mRecyclerViewState1 = savedInstanceState.getParcelable(KEY_RECYCLERVIEW_1);
            mRecyclerViewState2 = savedInstanceState.getParcelable(KEY_RECYCLERVIEW_2);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        scrollviewDetail.scrollTo(0,0);
    }

    @Override
    public void onClick(Trailer trailerData) {
        Uri url =Uri.parse("https://www.youtube.com/watch?v="+ trailerData.getKey());

        Intent intent =new Intent(Intent.ACTION_VIEW,url);

        intent.putExtra(Intent.EXTRA_TEXT, url.toString());


        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }


    }

    private void  populateUI(int id_movie, final String mTitleStr, String mPathStr, String mOverviewStr, String mRateStr, String mDateStr){
        nameMovie.setText(mTitleStr);


        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    ((CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout)).setTitle(mTitleStr);
                    isShow = true;
                } else if(isShow) {
                    ((CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout)).setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                    isShow = false;
                }
            }
        });

        Log.v(LOG_TAG,"value of date "+mDateStr);
        if(mDateStr==null|| mDateStr.equals("")) {
            //mDateStr= getResources().getString(R.string.error_date_message);
            dateView.setText(getResources().getString(R.string.error_date_message));
        }
        else{
            dateView.setText(mDateStr);
        }

        Log.v(LOG_TAG,"value of rate "+mRateStr);
        if(mRateStr!=null || mRateStr.equals("")){
            rateView.setText(mRateStr+"/10");
        }
        else{
            rateView.setText(getResources().getString(R.string.error_rate_message));
        }
        Log.v(LOG_TAG,"value of overview "+mOverviewStr);
        if(mOverviewStr==null|| mOverviewStr.equals("")){

            ((TextView)overviewParts.findViewById(R.id.Overview_view)).setText(getResources().getString(R.string.error_overview_message));
        }
        else{
            ((TextView)overviewParts.findViewById(R.id.Overview_view)).setText(mOverviewStr);
        }


        Log.v(LOG_TAG,"value of post: "+mPathStr);


        moviePicture.setImageMatrix(matrix);

        if(!mPathStr.equals("null")){
            Picasso.with(this)
                    .load(MovieData.BASE_LINK +mPathStr)
                    .resize(600,1000)
                    .centerInside()
                    .into(moviePicture);
        }
        else{
            Picasso.with(this)
                    .load(R.drawable.not_found)
                    .resize(600,800)
                    .into(moviePicture);
        }
    }
}
