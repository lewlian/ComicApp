package com.example.norman_lee.comicapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    EditText editTextComicNo;
    Button buttonGetComic;
    TextView textViewTitle;
    ImageView imageViewComic;

    String comicNo;
    public static final String TAG = "Logcat";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextComicNo = findViewById(R.id.editTextComicNo);
        buttonGetComic = findViewById(R.id.buttonGetComic);
        textViewTitle = findViewById(R.id.textViewTitle);
        imageViewComic = findViewById(R.id.imageViewComic);

        buttonGetComic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                String userinput = editTextComicNo.getText().toString();
                URL url = buildURL(userinput);

                if(Utils.isNetworkAvailable(MainActivity.super.getBaseContext())) {
                    GetComic getComic = new GetComic();
                    getComic.execute(url);
                }
            }
        });
    }
        private URL buildURL (String comicNo){

            String scheme = "https";
            final String authority = "xkcd.com";
            final String back = "info.0.json";
            Uri.Builder builder;
            URL url = null;


            builder = new Uri.Builder();
            builder.scheme(scheme)
                    .authority(authority)
                    .appendPath(comicNo)
                    .appendPath(back);
            Uri uri = builder.build();

            try {
                url = new URL(uri.toString());
                Log.i(TAG, "URL OK: " + url.toString());
            } catch (MalformedURLException e) {
                Log.i(TAG, "malformed URL: " + url.toString());
            }
            return url;
        }
        class GetComic extends AsyncTask<URL,String,Bitmap>{
            @Override
            protected Bitmap doInBackground(URL... urls) {
                String json = Utils.getJson(urls[0]);
                if (json == null){
                    publishProgress();
                    Log.i(TAG, "json is null");
                    return null;
                }else{
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        String safe_title = jsonObject.getString("safe_title");
                        textViewTitle.setText(safe_title);

                        String imgurl = jsonObject.getString("img");
                        URL url1 = new URL(imgurl);
                        Bitmap bitmap = Utils.getBitmap(url1);
                        return bitmap;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
                Bitmap bitmap = null;
                return bitmap;
            }
            @Override
            protected void onProgressUpdate(String... values){
                textViewTitle.setText("Loading...");
                Log.i(TAG,"still loading");
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                imageViewComic.setImageBitmap(bitmap);

            }




        }


    }


