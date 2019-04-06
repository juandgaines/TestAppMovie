package com.example.android.popularmoviesapp.data;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

public class Trailers implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "id_movie")
    private String id_movie;
    @ColumnInfo(name = "iso_6391")
    private String iso6391;
    @ColumnInfo(name = "iso_31661")
    private String iso31661;

    @ColumnInfo(name = "key")
    private String key;
    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "site")
    private String site;
    @ColumnInfo(name = "size")
    private Integer size;

    @ColumnInfo(name = "type")
    private String type;

    @Ignore
    public  Trailers(String id_movie,String iso6391,String iso31661, String key,String name, String site, Integer size,String type){

        this.id_movie=id_movie;
        this.iso6391=iso6391;
        this.iso31661=iso31661;
        this.key=key;
        this.name=name;
        this.site=site;
        this.size=size;
        this.type= type;


    }
    @Ignore
    public Trailers(){

    }
    public Trailers(int id, String id_movie,String iso6391,String iso31661, String key,String name, String site, Integer size,String type){

        this.id=id;
        this.id_movie=id_movie;
        this.iso6391=iso6391;
        this.iso31661=iso31661;
        this.key=key;
        this.name=name;
        this.site=site;
        this.size=size;
        this.type= type;


    }

    public String getMovieId() {
        return id_movie;
    }

    public void setId(String id_movie) {
        this.id_movie = id_movie;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIso6391() {
        return iso6391;
    }

    public void setIso6391(String iso6391) {
        this.iso6391 = iso6391;
    }

    public String getIso31661() {
        return iso31661;
    }

    public void setIso31661(String iso31661) {
        this.iso31661 = iso31661;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }





    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id_movie);
        dest.writeString(iso6391);
        dest.writeString(iso31661);
        dest.writeString(key);
        dest.writeString(name);
        dest.writeString(site);
        dest.writeString(Integer.toString(size));
        dest.writeString(type);
    }
    //constructor used for parcel
    @Ignore
    public Trailers(Parcel parcel){
        //read and set saved values from parcel
        id_movie=parcel.readString();
        iso6391= parcel.readString();
        iso31661=parcel.readString();
        key=parcel.readString();
        name=parcel.readString() ;
        site=parcel.readString();
        size=Integer.parseInt(parcel.readString());
        type=parcel.readString();

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
                .append("Id:"+id_movie+"\n")
                .append("iso 6391: "+iso6391+"\n")
                .append("iso 31661: "+iso31661+"\n")
                .append("key: "+key+"\n")
                .append("name: "+name+"\n")
                .append("site: "+site+"\n")
                .append("size: "+size+"\n")
                .append("type: "+type+"\n");


        return movieString.toString();
    }
}
