package com.example.android.popularmoviesapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.android.popularmoviesapp.data.Result;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class MovieAdapter  extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> implements Filterable {

    public final static String LOG_TAG= MovieAdapter.class.getName().toString();
    private Context context;
    private List<Result> mMovieData;
    private List<Result> mMovieDataFull;
    private final MovieAdapterOnClickHandler mClickHandler;
    private int mWidth;
    private int mHeight;

    public MovieAdapter(MovieAdapterOnClickHandler clickHandler, List<Result> movieFetchedData,int width,int height) {
        mClickHandler =  clickHandler;
        setMovieData(movieFetchedData);
        mWidth=width;
        mHeight=height;
    }

    public void restartSearch(){

         mMovieData.clear();
         mMovieData.addAll(mMovieDataFull);
         notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter= new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            List<Result> filteredMovieData= new ArrayList<Result>();

            if(charSequence==null || charSequence.length()==0 ||charSequence.equals("")){

                filteredMovieData.addAll(mMovieDataFull);
            }
            else {
                String filterPattern=charSequence.toString().toLowerCase().trim();
                for (Result item:mMovieDataFull){
                    if(item.getOriginalTitle().toLowerCase().contains(filterPattern)){
                        filteredMovieData.add(item);
                    }
                }
            }
            FilterResults filterResults= new FilterResults();
            filterResults.values=filteredMovieData;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            mMovieData.clear();
            mMovieData.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public interface MovieAdapterOnClickHandler {
        void onClick(Result movieData,View view);
    }


    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.favorite_movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder movieViewHolder, int position ) {
        String movieImage = mMovieData.get(position).getPosterPath();
        String mTitle =mMovieData.get(position).getTitle();

        Log.v(LOG_TAG,"movie image"+movieImage);
        TypedValue outValue = new TypedValue();

        context.getResources().getValue(R.dimen.picasso_image_divisor,outValue,true);

        float divisor= outValue.getFloat();

        if (!(movieImage==null)){
            Picasso.with(context).load ("http://image.tmdb.org/t/p/w185/"+movieImage)
                    .resize((int) Math.round(mWidth/divisor),(int) Math.round(mHeight/divisor) )
                    .centerInside()
                    .placeholder(R.drawable.user_placeholder)
                    .error(R.drawable.user_placeholder)
                    .into(movieViewHolder.mMoviePosterView);
        }else{
            Picasso.with(context)
                    .load(R.drawable.not_found)
                    .resize(600,700)
                    .placeholder(R.drawable.user_placeholder)
                    .error(R.drawable.user_placeholder)
                    .into(movieViewHolder.mMoviePosterView);
        }
        movieViewHolder.mTitleMovie.setText(mTitle);


    }

    @Override
    public int getItemCount() {
        if (null == mMovieData) return 0;
        return mMovieData.size();
    }

    public void setMovieData(List<Result> movieData) {

        mMovieData = new ArrayList<Result>(movieData);
        mMovieDataFull=new ArrayList<Result>(movieData);
        notifyDataSetChanged();
    }

    public void restartData(){
        mMovieData.clear();
        mMovieDataFull.clear();
        notifyDataSetChanged();
    }



    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public final ImageView mMoviePosterView;
        public final TextView mTitleMovie;


        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);

            mMoviePosterView= (ImageView) itemView.findViewById(R.id.favorite_movie_poster);
            mTitleMovie=(TextView) itemView.findViewById(R.id.favorite_movie_title) ;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition=getAdapterPosition();
            Result movieData = mMovieDataFull.get(adapterPosition);
            mClickHandler.onClick(movieData,view);
        }


    }





}
