package com.edufree.newscompanion.utils;

import android.net.Uri;
import android.util.Log;

import com.edufree.newscompanion.models.Article;
import com.edufree.newscompanion.models.Section;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class networkUtils {
    //https://content.guardianapis.com/sections?api-key=78e05202-dfd4-4ba0-b29c-bcc410b498a9
    //https://content.guardianapis.com/search?q=football&api-key=78e05202-dfd4-4ba0-b29c-bcc410b498a9

    private static final String TAG="networkUtils" ;
    //api_Key
    private static final String API_KEY= "78e05202-dfd4-4ba0-b29c-bcc410b498a9";
    // Base URL for searching
    private static final String NEWS_BASE_URL_SEARCH =  "https://content.guardianapis.com/search?";

    private static final String NEWS_BASE_URL_SECTIONS=  "https://content.guardianapis.com/sections?";

    private static final String QUERY="q";
    // Parameter to filter by print type.
    private static final String API_KEY_REFERENCE = "api-key";

    static public ArrayList<Article> getFormattedNewsJsonData(String userWord, String classifier){

        ArrayList<Article> newsList=new ArrayList<>();
        String id=null,type=null,sectionId=null,sectionName=null,webPublicationDate=null,webTitle=null,webUrl=null;
        String NewsInfoString=getNewsInfo(userWord,classifier);

        if(NewsInfoString!=null){
            try {
                //get the whole Object yeah
                JSONObject mObject=new JSONObject(NewsInfoString);

                JSONObject response=mObject.getJSONObject("response");
                JSONArray resultsArray=response.getJSONArray("results");

                //implement a for loop to get get all the data yeah..
                for(int a=0;a<resultsArray.length();a++){
                    JSONObject each_article = resultsArray.getJSONObject(a);
                    try {
                        id=each_article.getString("id");
                        type=each_article.getString("type");
                        sectionId=each_article.getString("sectionId");
                        sectionName=each_article.getString("sectionName");
                        webPublicationDate=each_article.getString("webPublicationDate");
                        webTitle=each_article.getString("webTitle");
                        webUrl=each_article.getString("webUrl");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Article article=new Article(id,type,sectionId,sectionName,webPublicationDate,webTitle,webUrl);
                    newsList.add(article);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Log.d(TAG,e.getMessage());
                newsList=null;
                return newsList;
            }
        }else {
            return null;
        }
        return newsList;
    }

    static public ArrayList<Section> getFormattedJsonSectionData(String userWord, String classifier){
        ArrayList<Section> sectionList=new ArrayList<>();
        String id=null,webTitle=null,webUrl=null;
        String NewsSectionInfor=getNewsInfo(userWord,classifier);
        if(NewsSectionInfor!=null){
            try {
                //get the whole Object yeah
                JSONObject mObject=new JSONObject(NewsSectionInfor);
                JSONObject response=mObject.getJSONObject("response");
                JSONArray resultsArray=response.getJSONArray("results");
                //implement a for loop to get get all the data yeah..
                for(int a=0;a<resultsArray.length();a++){
                    JSONObject each_section = resultsArray.getJSONObject(a);

                    try {
                        id=each_section.getString("id");
                        webTitle=each_section.getString("webTitle");
                        webUrl=each_section.getString("webUrl");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    Section section=new Section(id,webTitle,webUrl);
                    sectionList.add(section);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Log.d(TAG,e.getMessage());
                sectionList=null;
                return sectionList;
            }

        }else {
            sectionList=null;
            return sectionList;
        }

        return sectionList;
    }

    public static String getNewsInfo(String queryString,String classifier){

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String articleJSONString = null;
        URL requestURL=null;
        int type=Integer.parseInt(classifier);

        if(type==1){
            //buld this type of url to get mainActivity news
            //https://content.guardianapis.com/search?api-key=78e05202-dfd4-4ba0-b29c-bcc410b498a9
            try {
                Uri builtURI = Uri.parse(NEWS_BASE_URL_SEARCH).buildUpon()
                        .appendQueryParameter(API_KEY_REFERENCE,API_KEY)
                        .build();
                requestURL = new URL(builtURI.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }else if(type==2){
            //buidling this url
            //https://content.guardianapis.com/sections?api-key=78e05202-dfd4-4ba0-b29c-bcc410b498a9
            try {
                Uri builtURI = Uri.parse(NEWS_BASE_URL_SECTIONS).buildUpon()
                        .appendQueryParameter(API_KEY_REFERENCE,API_KEY)
                        .build();
                requestURL = new URL(builtURI.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }else if(type==3){
            //building this example UI
            //https://content.guardianapis.com/search?q=football&api-key=78e05202-dfd4-4ba0-b29c-bcc410b498a9
            try {
                Uri builtURI = Uri.parse(NEWS_BASE_URL_SEARCH).buildUpon()
                        .appendQueryParameter(QUERY,queryString)
                        .appendQueryParameter(API_KEY_REFERENCE,API_KEY)
                        .build();
                requestURL = new URL(builtURI.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }else {
            //building this type of address to handle user searches
            //https://content.guardianapis.com/search?q=betting&api-key=78e05202-dfd4-4ba0-b29c-bcc410b498a9
            try {
                Uri builtURI = Uri.parse(NEWS_BASE_URL_SEARCH).buildUpon()
                        .appendQueryParameter(QUERY,queryString)
                        .appendQueryParameter(API_KEY_REFERENCE,API_KEY)
                        .build();
                requestURL = new URL(builtURI.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        try {
            urlConnection = (HttpURLConnection) requestURL.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            // Create a buffered reader from that input stream.
            reader = new BufferedReader(new InputStreamReader(inputStream));
            // Use a StringBuilder to hold the incoming response.
            StringBuilder builder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append("\n");
            }

            if (builder.length() == 0) {
                // Stream was empty. No point in parsing.
                return null;
            }

            articleJSONString = builder.toString();

        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG,e.getMessage());
            articleJSONString=null;
            return articleJSONString;
        }finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return articleJSONString;
    }


}
