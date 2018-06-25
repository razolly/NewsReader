package com.example.razli.newsreader;

import android.content.Intent;
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
    ArrayAdapter<String> mArrayAdapter;
    JSONArray jsonArray;
    ArrayList<Integer> mArticleIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mArticleIds = new ArrayList<>();
        mArticleListView = findViewById(R.id.listView);
        mArticleNames = new ArrayList<>();
        mArticleNames.add("Article 1");
        mArticleNames.add("Article 2");
        mArticleNames.add("Article 3");
        mArticleNames.add("Article 4");

        mArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mArticleNames);
        mArticleListView.setAdapter(mArrayAdapter);

        mArticleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Start new activity. Opens a WebView
                try {
                    String result = new DownloadJsonTask().execute("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty").get();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
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
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStreamReader reader = new InputStreamReader(urlConnection.getInputStream());
                int data = reader.read();

                while(data != -1) {
                    char currentChar = (char) data;
                    result += currentChar;
                    data = reader.read();
                }

                JSONArray jsonArray = new JSONArray(result);

                // Gets 20 Article Ids and adds to ArrayList
                for(int i = 0; i < 20; i++) {
                    mArticleIds.add(jsonArray.getInt(i));
                    Log.i(TAG, "onPostExecute: mArticleId added: " + jsonArray.getInt(i));
                }

                return result;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
