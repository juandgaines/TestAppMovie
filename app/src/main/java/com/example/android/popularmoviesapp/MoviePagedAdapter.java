package com.example.android.popularmoviesapp;

import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmoviesapp.data.Result;
import com.example.android.popularmoviesapp.data.Results;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MoviePagedAdapter extends PagedListAdapter<Result,MoviePagedAdapter.MovieViewHolder> {

    public final static String LOG_TAG= MoviePagedAdapter.class.getSimpleName();
    private Context context;
    private final MoviePagedAdapter.MoviePagedAdapterOnClickHandler mClickHandler;
    private int mWidth;
    private int mHeight;

    protected MoviePagedAdapter(Context mContext, MoviePagedAdapterOnClickHandler clickHandler,int width,int height) {
        super(DIFF_CALLBACK);
        context=mContext;
        mClickHandler=clickHandler;
        mWidth=width;
        mHeight=height;

    }

    public interface MoviePagedAdapterOnClickHandler {
        void onClick(Result movieData);
    }


    private static  DiffUtil.ItemCallback<Result> DIFF_CALLBACK=
            new DiffUtil.ItemCallback<Result>() {
                @Override
                public boolean areItemsTheSame(Result oldItem, Result newItem) {

                    return oldItem.getId()==newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(Result oldItem, Result newItem) {
                    return oldItem.equals(newItem);
                }
            };

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.favorite_movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new MoviePagedAdapter.MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder movieViewHolder, int position) {


        Result mResult=getItem(position);

        if (mResult==null){
            return;
        }

        String movieImage = mResult.getPosterPath();;
        String mTitle =mResult.getTitle();

        Log.v(LOG_TAG,"movie image"+movieImage);
        TypedValue outValue = new TypedValue();

        context.getResources().getValue(R.dimen.picasso_image_divisor,outValue,true);

        float divisor= outValue.getFloat();

        if (!movieImage.equals("null")){
            Picasso.with(context).load ("http://image.tmdb.org/t/p/w185/"+movieImage)
                    .resize((int) Math.round(mWidth/divisor),(int) Math.round(mHeight/divisor) )
                    .centerInside()
                    .into(movieViewHolder.mMoviePosterView);
        }else{
            Picasso.with(context)
                    .load(R.drawable.not_found)
                    .resize(600,1000)
                    .into(movieViewHolder.mMoviePosterView);
        }
        movieViewHolder.mTitleMovie.setText(mTitle);
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
            Result movieData =getItem(adapterPosition);
            mClickHandler.onClick(movieData);
        }


    }

}
