package com.example.android.popularmoviesapp.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

@Entity(tableName = "cache_data")
public class CacheMovieData  implements Parcelable {

    public static final String BASE_LINK = "http://image.tmdb.org/t/p/w185";

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "movie_id")
    private int movie_id;

    @ColumnInfo(name = "rate")
    private Double rate;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "path")
    private String path;

    @ColumnInfo(name = "overview")
    private String overview;

    @ColumnInfo(name = "release")
    private String release;

    @ColumnInfo(name="category")
    private String category;

    public static  final String PARCELABLE="parcelable";



    public CacheMovieData(int id, int movie_id, Double rate, String title, String path,String overview,String release,String category){

        this.id=id;
        this.movie_id=movie_id;
        this.rate=rate;
        this.title=title;
        this.path=path;
        this.overview=overview;
        this.release=release;
        this.category=category;


    }
    @Ignore
    public CacheMovieData(){}

    @Ignore
    public  CacheMovieData( int movie_id, Double rate, String title, String path,String overview,String release,String category){

        this.title=title;
        this.overview=overview;
        this.rate=rate;
        this.release=release;
        this.path=path;
        this.movie_id=movie_id;
        this.category=category;


    }

    @Ignore
    public CacheMovieData(Parcel parcel){
        //read and set saved values from parcel
        movie_id=Integer.parseInt(parcel.readString());
        title= parcel.readString();
        overview=parcel.readString();
        path=parcel.readString();
        rate=Double.parseDouble(parcel.readString()) ;
        release=parcel.readString();
        category=parcel.readString();

    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getMovie_id(){return movie_id;}

    public void setMovie_id(int id){
        this.movie_id=id;
    }

    public int getId(){return id;}

    public void setId(int id){
        this.id=id;
    }


    public Double getRate() {
        return rate;
    }

    public void setRate(Double voteAverage) {
        this.rate = voteAverage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getPath() {
        return path;
    }

    public void setPath(String posterPath) {
        this.path = posterPath;
    }


    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getRelease() {
        return release;
    }

    public void setRelease(String releaseDate) {
        this.release = releaseDate;
    }


    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Integer.toString(movie_id));
        dest.writeString(title);
        dest.writeString(overview);
        dest.writeString(path);
        dest.writeString(Double.toString(rate));
        dest.writeString(release);
        dest.writeString(category);
    }
    //constructor used for parcel



    //creator - used when un-parceling our parcle (creating the object)
    public static final Parcelable.Creator<MovieData> CREATOR = new Parcelable.Creator<MovieData>(){

        @Override
        public MovieData createFromParcel(Parcel parcel) {
            return new MovieData(parcel);
        }

        @Override
        public MovieData[] newArray(int size) {
            return new MovieData[0];
        }
    };

    @Override
    public String toString() {
        StringBuilder movieString=new StringBuilder("");
        movieString
                .append("Id:"+movie_id+"\n")
                .append("Title: "+title+"\n")
                .append("Overview: "+overview+"\n")
                .append("Path: "+path+"\n")
                .append("Rate: "+rate+"\n")
                .append("Release: "+release+"\n")
                .append("Category:"+release+"\n");

        return movieString.toString();
    }
}
