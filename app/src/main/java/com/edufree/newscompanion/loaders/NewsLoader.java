package com.edufree.newscompanion.loaders;
import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;
import com.edufree.newscompanion.models.Article;
import com.edufree.newscompanion.utils.networkUtils;
import java.util.ArrayList;

public class NewsLoader extends AsyncTaskLoader<ArrayList<Article>> {
    private static final String TAG="NewsLoader";

    private String queryString;
    private String classifierString;

    public NewsLoader(@NonNull Context context, String queryString, String classiffierString) {
        super(context);
        this.queryString=queryString;
        this.classifierString=classiffierString;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Nullable
    @Override
    public ArrayList<Article> loadInBackground() {
        ArrayList<Article> data= networkUtils.getFormattedNewsJsonData(queryString,classifierString);
        if(data!=null){
            return data;
        }else{
            return null;
        }
    }


}
