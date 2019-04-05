package com.example.android.popularmoviesapp;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.paging.PageKeyedDataSource;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;

import com.example.android.popularmoviesapp.data.Result;

public class ResultViewModel extends AndroidViewModel {

    private LiveData<PagedList<Result>> resultPagedList;
    private LiveData<PageKeyedDataSource<Integer,Result>> liveDataSource;

    public ResultViewModel(@NonNull Application application) {
        super(application);
    }
}
