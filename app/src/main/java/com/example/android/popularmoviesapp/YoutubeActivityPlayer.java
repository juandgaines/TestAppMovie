package com.example.android.popularmoviesapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import butterknife.BindView;
import butterknife.ButterKnife;



public class YoutubeActivityPlayer extends YouTubeBaseActivity {

    @BindView(R.id.youtube_player)
    YouTubePlayerView youTubePlayerView;

    private boolean first_time=true;
    String url;
    YouTubePlayer.OnInitializedListener onInitializedListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_player);

        ButterKnife.bind(this);
        Intent intent= getIntent();

        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {

            url = intent.getStringExtra(Intent.EXTRA_TEXT);

        }

        onInitializedListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.loadVideo(url);
                youTubePlayer.play();
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };

        youTubePlayerView.initialize(BuildConfig.YOUTUBE_API, onInitializedListener);




    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onInitializedListener=null;
    }


}
