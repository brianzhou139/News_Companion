package com.edufree.newscompanion.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.edufree.newscompanion.R;
import com.edufree.newscompanion.models.Article;
import com.edufree.newscompanion.utils.networkUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class newsAdapter extends RecyclerView.Adapter<newsAdapter.newsViewHolder> {

    private Context context;
    private ArrayList<Article> newsList;

    public newsAdapter(Context context, ArrayList<Article> newsList) {
        this.context = context;
        this.newsList = newsList;
    }

    @NonNull
    @Override
    public newsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(context).inflate(R.layout.news_item,parent,false);
        return new newsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull newsViewHolder holder, int position) {
        final Article currentArticle=newsList.get(position);
        holder.news_title.setText(currentArticle.getWebTitle());
        holder.news_section.setText(currentArticle.getSectionId());
        holder.news_date.setText(currentArticle.getWebPublicationDate());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(currentArticle.getWebUrl()));
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public class newsViewHolder extends RecyclerView.ViewHolder {
        private TextView news_title,news_section,news_date;
        public newsViewHolder(@NonNull View itemView) {
            super(itemView);
            news_title=(TextView)itemView.findViewById(R.id.article_title);
            news_section=(TextView)itemView.findViewById(R.id.article_section);
            news_date=(TextView)itemView.findViewById(R.id.article_time);

        }
    }


}
