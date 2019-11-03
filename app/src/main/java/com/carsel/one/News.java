package com.carsel.one;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
public class News extends AppCompatActivity {
    private boolean isExit;
    ArrayList<HashMap<String, String>> listItems = new ArrayList<>();
    String TAG="TAG";
    ListView listView;
    int list_size;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_news);
        listView = findViewById(R.id.listview);//获取标题列表

        Intent intent = getIntent();//获取intent
        Bundle bdl = intent.getExtras();//里面有 news_title，news_href
        if(bdl!=null){//存入本地的listItems中去
            list_size = bdl.getInt("list_size");//获取循环次数
            for(int k=0;k<list_size;k++){//
                HashMap<String,String> map ;
                map = (HashMap<String, String>) bdl.get(String.valueOf(k));
//                map.put("news_id", String.valueOf(k));//在这里添加好新闻的 news_id
                listItems.add(map);//添加到listItems里去，这次里面有三个内容了
            }
        }//listItems初始化结束
        Log.i(TAG ,"%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%输出一开始的listItems");
        show();



        MyAdapter myAdapter = new MyAdapter(this, R.layout.list_item, listItems);
        listView.setAdapter(myAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {//点击打开详情页面
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Object itemAtPosition = listView.getItemAtPosition(position);
                HashMap<String,String> map = (HashMap<String, String>) itemAtPosition;
                String news_detail = listItems.get(position).get("news_detail");//获取id，用于下个页面查询新闻内容
                String news_title = listItems.get(position).get("news_title");//获取id，用于下个页面查询新闻内容

                Log.i(TAG,"onItemClick: news_detail="+news_detail);
                Log.i(TAG,"onItemClick: news_title="+news_title);


                Intent config = new Intent(News.this, Detail.class);//本页面News传递给下一个页面Detail
                Bundle bdl = new Bundle();
//                bdl.putString("news_id",news_id);//存入id，用于下一个页面查询
                bdl.putString("news_detail",news_detail);//存入id，用于下一个页面查询
                bdl.putString("news_title",news_title);//存入id，用于下一个页面查询
                config.putExtras(bdl);//传递
                startActivityForResult(config,2);
            }
        });//详情页面结束

    }//onCreate结束


    public void show(){//输出传递过来的东西，查看是否合格。
        for(int i=0;i<listItems.size();i++){//循环输出
            Log.i(TAG, String.valueOf(listItems.get(i)));
        }
    }//显示listItems
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){//点击两次退出程序
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(isExit){
                this.finish();
            }
            else{
                Toast.makeText(this,"再按一次退出",Toast.LENGTH_SHORT).show();
                isExit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isExit = false;
                    }
                },2000);
            }
        }
        return true;
    }//点击两次退出结束效果

}
