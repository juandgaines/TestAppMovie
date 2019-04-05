package com.example.android.popularmoviesapp;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;
import android.arch.paging.PageKeyedDataSource;

import com.example.android.popularmoviesapp.data.Result;

public class ResultDataSourceFactory extends DataSource.Factory {

    private MutableLiveData<PageKeyedDataSource<Integer,Result>> resultLiveDataSource=new MutableLiveData<>();
    @Override
    public DataSource create() {
        ResultDataSource resultDataSource=new ResultDataSource();

        resultLiveDataSource.postValue(resultDataSource);
        return  resultDataSource;
    }

    public MutableLiveData<PageKeyedDataSource<Integer, Result>> getResultLiveDataSource() {
        return resultLiveDataSource;
    }
}
