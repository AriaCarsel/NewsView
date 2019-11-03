package com.carsel.one;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

public class Detail  extends AppCompatActivity  {
    TextView title;
    TextView article;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.detail_news);
        title = findViewById(R.id.title);
        article = findViewById(R.id.article);

        Intent intent = getIntent();
        Bundle bdl = intent.getExtras();

        String news_title = (String) bdl.get("news_title");
        String news_detail = (String) bdl.get("news_detail");
        title.setText(news_title);
        if(news_detail !=null){
            article.setText("  "+news_detail);
        }
       else
            article.setText("暂无新闻");
   }
}
