package com.example.razli.newsreader;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    ListView mArticleListView;
    ArrayList<String> mArticleNames;
    ArrayList<String> mArticleUrls;
    ArrayAdapter<String> mArrayAdapter;

    SQLiteDatabase mDatabase;
    int mNoOfArticles = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mArticleListView = findViewById(R.id.listView);
        mArticleUrls = new ArrayList<>();
        mArticleNames = new ArrayList<>();

        mArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mArticleNames);
        mArticleListView.setAdapter(mArrayAdapter);

        mArticleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Start new activity. Opens a WebView
            }
        });

        try {
            String result = new DownloadJsonTask().execute("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty").get();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class DownloadJsonTask extends AsyncTask<String, Void, String> {

        // Extract the JSON objects
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... urls) {

            String result = "";

            try {

                URL url;
                HttpURLConnection urlConnection;
                InputStreamReader reader;
                int data;

                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                reader = new InputStreamReader(urlConnection.getInputStream());
                data = reader.read();

                while(data != -1) {
                    char currentChar = (char) data;
                    result += currentChar;
                    data = reader.read();
                }

                JSONArray jsonArray = new JSONArray(result);

                // Gets 20 Article Ids and adds to ArrayList
                for(int i = 0; i < mNoOfArticles; i++) {
                    url = new URL("https://hacker-news.firebaseio.com/v0/item/" + Integer.toString(jsonArray.getInt(i)) + ".json?print=pretty");

                    String articleInfo = "";

                    // Same code as doInBackground
                    // Gets all the concatenated JSON data
                    urlConnection = (HttpURLConnection) url.openConnection();
                    reader = new InputStreamReader(urlConnection.getInputStream());
                    data = reader.read();

                    while(data != -1) {
                        char currentChar = (char) data;
                        articleInfo += currentChar;
                        data = reader.read();
                    }

                    // Create jsonObject to hold the data and to extract values from
                    JSONObject jsonObject = new JSONObject(articleInfo);

                    // Add to "Title" and "Url" to ArrayLists
                    // Note: one of the JSON objects is missing a "url" field, hence the IF ELSE statement
                    mArticleNames.add(jsonObject.getString("title"));

                    if(jsonObject.has("url")) {
                        mArticleUrls.add(jsonObject.getString("url"));
                    } else {
                        mArticleUrls.add("");
                    }
                }

                // Store info in SQLite database
                createDatabaseAndStoreInfo();

                return result;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    private void createDatabaseAndStoreInfo() {

        mDatabase = this.openOrCreateDatabase("HackerNews", MODE_PRIVATE, null);

        // Create table
        mDatabase.execSQL("CREATE TABLE IF NOT EXISTS topStories (id INTEGER PRIMARY KEY, title VARCHAR, url VARCHAR)");

//        // Insert 20 articles into database
//        for(int i = 0; i < mNoOfArticles; i++) {
//            mDatabase.execSQL("INSERT INTO topStories(title, url) VALUES ('" + mArticleNames.get(i) + "','" + mArticleUrls.get(i) + "')");;
//        }

        // Test to see if data is correct
        Cursor resultSet = mDatabase.rawQuery("SELECT * FROM topStories", null);

        int idIndex = resultSet.getColumnIndex("id");
        int titleIndex = resultSet.getColumnIndex("title");
        int urlIndex = resultSet.getColumnIndex("url");

        resultSet.moveToFirst();

        while (resultSet != null) {
            Log.i(TAG, "createDatabaseAndStoreInfo: ID: " + resultSet.getString(idIndex));
            Log.i(TAG, "createDatabaseAndStoreInfo: Title: " + resultSet.getString(titleIndex));
            Log.i(TAG, "createDatabaseAndStoreInfo: Url: " + resultSet.getString(urlIndex));

            resultSet.moveToNext();
        }

        resultSet.close();
    }
}
