package com.example.android.popularmoviesapp.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Reviews implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "author")
    private String author;
    @ColumnInfo(name = "content")
    private String content;
    @ColumnInfo(name = "id_content")
    private String id_content;
    @ColumnInfo(name = "url")
    private String url;

    @Ignore
    public  Reviews(String author ,String content,String id_content, String url){

        this.author=author;
        this.content=content;
        this.id_content=id_content;
        this.url=url;
    }

    @Ignore
    public Reviews(){

    }

    public Reviews(int id, String id_movie,String iso6391,String iso31661, String key,String name, String site, Integer size,String type){

        this.id=id;
        this.author=author;
        this.content=content;
        this.id_content=id_content;
        this.url=url;

    }

    public void setId(int id){
        this.id=id;

    }

    public int getId(){
        return id;
    }
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIdContent() {
        return id_content;
    }

    public void setIdContent(String id_content) {
        this.id_content = id_content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(author);
        dest.writeString(content);
        dest.writeString(id_content);
        dest.writeString(url);


    }
    //constructor used for parcel
    @Ignore
    public Reviews(Parcel parcel){
        //read and set saved values from parcel
        author=parcel.readString();
        content= parcel.readString();
        id_content=parcel.readString();
        url=parcel.readString();

    }

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
                .append("author:"+author+"\n")
                .append("content: "+content+"\n")
                .append("id content: "+id_content+"\n")
                .append("url: "+url+"\n");


        return movieString.toString();
    }
}
