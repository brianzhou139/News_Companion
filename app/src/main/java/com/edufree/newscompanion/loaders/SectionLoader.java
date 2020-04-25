package com.edufree.newscompanion.loaders;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.edufree.newscompanion.models.Section;
import com.edufree.newscompanion.utils.networkUtils;

import java.util.ArrayList;

public class SectionLoader extends AsyncTaskLoader<ArrayList<Section>> {
    private static final String TAG="NewsLoader";
    private String queryString;
    private String classifierString;

    public SectionLoader(@NonNull Context context, String queryString, String classifierString) {
        super(context);
        this.queryString = queryString;
        this.classifierString = classifierString;
    }

    @Nullable
    @Override
    public ArrayList<Section> loadInBackground() {
        ArrayList<Section> infor= networkUtils.getFormattedJsonSectionData(queryString,classifierString);
        if(infor!=null){
            return infor;
        }else{
            return null;
        }
    }

    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

}
