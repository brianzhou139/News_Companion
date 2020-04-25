package com.edufree.newscompanion.ui.home;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.edufree.newscompanion.R;
import com.edufree.newscompanion.adapters.newsAdapter;
import com.edufree.newscompanion.loaders.NewsLoader;
import com.edufree.newscompanion.models.Article;

import java.util.ArrayList;

import static android.view.View.GONE;

public class HomeFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<Article>>{

    private ArrayList<Article> newsList;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recycler;
    private ProgressBar progressbar;
    private newsAdapter adapter;
    private ImageView networkError,networkRefresh;
    private TextView textError;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        setHasOptionsMenu(true);

        networkError=(ImageView)root.findViewById(R.id.network_error);
        textError=(TextView)root.findViewById(R.id.text_no_data);
        networkRefresh=(ImageView)root.findViewById(R.id.network_retry);
        networkError.setVisibility(GONE);
        textError.setVisibility(GONE);
        networkRefresh.setVisibility(GONE);

        swipeRefresh=(SwipeRefreshLayout)root.findViewById(R.id.refreshSwipe);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //check to see if there is an internet coonection and make query
                if(isInternetConnected()){
                    networkError.setVisibility(GONE);
                    networkRefresh.setVisibility(GONE);
                    makeQuery(true);
                }else{
                    //there is no internet connection
                    networkError.setVisibility(View.VISIBLE);
                    networkRefresh.setVisibility(View.VISIBLE);
                }
            }
        });

        recycler=(RecyclerView)root.findViewById(R.id.list_rv);
        progressbar=(ProgressBar)root.findViewById(R.id.progressBar_home);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        if(getActivity().getSupportLoaderManager().getLoader(0)!=null){
            getActivity().getSupportLoaderManager().initLoader(0,null,this);
        }


        networkRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDataFromGuardianApi();
            }
        });

        getDataFromGuardianApi();

        return root;
    }

    private void getDataFromGuardianApi(){
        //check to see if there is an internet coonection and make query
        if(isInternetConnected()){
            networkError.setVisibility(GONE);
            networkRefresh.setVisibility(GONE);
            makeQuery(false);
        }else{
            //there is no internet connection
            networkError.setVisibility(View.VISIBLE);
            networkRefresh.setVisibility(View.VISIBLE);
            Toast.makeText(getActivity(),"no internet connection",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_menu,menu);

        SearchManager manager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        final SearchView search = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();

        search.setSearchableInfo(manager.getSearchableInfo(getActivity().getComponentName()));
        search.setQueryHint("enter keyword ..... ");
        //handle User Search
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                makeQueryForuserInput(query);
                hideKeyboard();
                search.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    

    private void makeQuery(Boolean isRefresh){

        if(isRefresh){
            Bundle queryBundle1 = new Bundle();
            String queryString="";
            String classyString="1";

            queryBundle1.putBoolean("refresh",isRefresh);
            queryBundle1.putString("qString", queryString);
            queryBundle1.putString("cString",classyString);
            getActivity().getSupportLoaderManager().restartLoader(0, queryBundle1, this);
        }else{
            Bundle queryBundle2 = new Bundle();
            String queryString="";
            String classyString="1";

            queryBundle2.putString("qString", queryString);
            queryBundle2.putString("cString",classyString);
            getActivity().getSupportLoaderManager().restartLoader(0, queryBundle2, this);
        }

    }

    private void makeQueryForuserInput(String input){
        Bundle queryBundle2 = new Bundle();
        String queryString=input;
        String classyString="4";

        queryBundle2.putString("qString", queryString);
        queryBundle2.putString("cString",classyString);
        getActivity().getSupportLoaderManager().restartLoader(0, queryBundle2, this);
    }

    @NonNull
    @Override
    public Loader<ArrayList<Article>> onCreateLoader(int id, @Nullable Bundle args) {
        String queryString = "";
        String classiffierString="";
        Boolean isRefresh=null;

        if (args != null) {
            queryString = args.getString("qString");
            classiffierString = args.getString("cString");
            isRefresh=args.getBoolean("refresh");
        }

        if(!isRefresh){
            progressbar.setVisibility(View.VISIBLE);
        }else{
            progressbar.setVisibility(GONE);
        }

        return new NewsLoader(getActivity(),queryString,classiffierString);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<Article>> loader, ArrayList<Article> data) {
        newsList=new ArrayList<>();
        newsList=data;

        if(newsList==null || newsList.size()==0){
            //theres is no data , dsipay infor to user
            progressbar.setVisibility(GONE);
            textError.setText(R.string.no_data);
            textError.setVisibility(View.VISIBLE);
            networkRefresh.setVisibility(View.VISIBLE);

        }else{
            textError.setVisibility(GONE);
            networkError.setVisibility(GONE);
            networkRefresh.setVisibility(GONE);
            adapter=new newsAdapter(getActivity(),newsList);
            recycler.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            progressbar.setVisibility(GONE);
            swipeRefresh.setRefreshing(false);
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<Article>> loader) {

    }

    private void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    private boolean isInternetConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;

        if (connMgr != null) {
            networkInfo = connMgr.getActiveNetworkInfo();
        }

        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

}