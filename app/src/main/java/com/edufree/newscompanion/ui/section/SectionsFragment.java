package com.edufree.newscompanion.ui.section;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.edufree.newscompanion.DetailsActivity;
import com.edufree.newscompanion.R;
import com.edufree.newscompanion.loaders.SectionLoader;
import com.edufree.newscompanion.models.Section;
import java.util.ArrayList;

import static android.view.View.GONE;

public class SectionsFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<Section>> {
    private ArrayList<Section> secList;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recycler;
    private ProgressBar progressbar;
    private sectionInnerAdapter innerAdapter;
    private ImageView networkError,networkRefresh;
    private TextView textError;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_sections, container, false);
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

        if(getActivity().getSupportLoaderManager().getLoader(5)!=null){
            getActivity().getSupportLoaderManager().initLoader(5,null,this);
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

    private void makeQuery(Boolean isRefresh){
        if(isRefresh){
            Bundle queryBundle1 = new Bundle();
            String queryString="";
            String classyString="2";

            queryBundle1.putBoolean("refresh",isRefresh);
            queryBundle1.putString("qString", queryString);
            queryBundle1.putString("cString",classyString);
            getActivity().getSupportLoaderManager().restartLoader(5, queryBundle1, this);
        }else{
            Bundle queryBundle2 = new Bundle();
            String queryString="";
            String classyString="2";

            queryBundle2.putString("qString", queryString);
            queryBundle2.putString("cString",classyString);
            getActivity().getSupportLoaderManager().restartLoader(5, queryBundle2, this);
        }

    }

    @NonNull
    @Override
    public Loader<ArrayList<Section>> onCreateLoader(int id, @Nullable Bundle args) {
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
            progressbar.setVisibility(View.GONE);
        }

        return new SectionLoader(getActivity(),queryString,classiffierString);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<Section>> loader, ArrayList<Section> data) {
        secList=data;
        //check to see if theres data or no ,if true populate views
        if(secList==null || secList.size()==0){
            progressbar.setVisibility(GONE);
            textError.setText(R.string.no_data);
            textError.setVisibility(View.VISIBLE);
            networkRefresh.setVisibility(View.VISIBLE);
        }else{
            textError.setVisibility(GONE);
            networkError.setVisibility(GONE);
            networkRefresh.setVisibility(GONE);
            secList.remove(0);
            innerAdapter=new sectionInnerAdapter(secList);
            recycler.setAdapter(innerAdapter);
            innerAdapter.notifyDataSetChanged();
            progressbar.setVisibility(View.GONE);
            swipeRefresh.setRefreshing(false);
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<Section>> loader) {

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

    class sectionInnerAdapter extends RecyclerView.Adapter<sectionInnerAdapter.mViewHolder>{
        private ArrayList<Section> secList;
        public sectionInnerAdapter(ArrayList<Section> secList) {
            this.secList = secList;
        }

        @NonNull
        @Override
        public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view=LayoutInflater.from(getActivity()).inflate(R.layout.section_list,parent,false);
            return new mViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull mViewHolder holder, final int position) {
            holder.name.setText(secList.get(position).getWebTitle());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(getContext(), DetailsActivity.class);
                    intent.putExtra("section",secList.get(position).getId());
                    intent.putExtra("section_name",secList.get(position).getWebTitle());
                    startActivity(intent);
                }
            });

        }

        @Override
        public int getItemCount() {
            return secList.size();
        }

        public class mViewHolder extends RecyclerView.ViewHolder {
            TextView name;
            public mViewHolder(@NonNull View itemView) {
                super(itemView);
                name=(TextView)itemView.findViewById(R.id.section_name);
            }
        }
    }

}