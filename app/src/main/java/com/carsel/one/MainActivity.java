package com.carsel.one;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
public class MainActivity extends AppCompatActivity implements Runnable {
    EditText username, password;
    Handler handler;//管理不同线程之间的消息，协助线程消息传递
    Message msg;//线程之间消息实例
    String TAG = "TAG";
    ArrayList<HashMap<String, String>> listItems = new ArrayList<>();
    private Context mContext;
    private SQLiteDatabase db;
    private MyDBOpenHelper myDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        mContext = MainActivity.this;
        myDBHelper = new MyDBOpenHelper(mContext, "my.db", null, 1);
        db = myDBHelper.getWritableDatabase();
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        insert();
        handler = new Handler() {//生命周期为窗口的打开到关闭
            public void handleMessage(Message mesg) {//时刻监测

                if (mesg != null && mesg.what == 1) {//检查发送过来的快递单号
                    Elements listDiv = (Elements) mesg.obj;
                    Log.i(TAG, "获取到了元素！");
                    for (Element element : listDiv) {
                        Elements texts = element.getElementsByTag("a");

                        for (Element text : texts) {
                            HashMap<String, String> map = new HashMap<String, String>();
                            Elements href = text.getElementsByAttributeValueMatching("href", "article");
                            for (Element temp : href) {
                                String a = temp.attr("href").trim();//每一条新闻的连接
                                if (!a.contains("http")) {
                                    a = "http://translate.ltaaa.com" + a;
                                }
                                map.put("news_href", a);//存入链接
                            }
                            try {
                                Document  document = Jsoup.parse(new URL(map.get("news_href")).openStream(), "UTF-8", map.get("news_href"));
                            Elements elem = document.getElementsByAttributeValue("class", "content");
                            String textArea = elem.text();
                            String words = textArea.replaceAll("[^(a-zA-Z)—] ", "");
                            if (words != null && words.length() > 0) {
                                map.put("news_detail", words);//存入新闻标题
                            }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            String title = text.attr("title").trim();//新闻的标题
                            if (title != null && title.length() > 0) {
                                map.put("news_title", title);//存入新闻标题
                                listItems.add(map);
                            }
                        }
                    }
                    Log.i(TAG, "开始输出抓取到的新闻！___________________________________________");
                    for (int i = 0; i < listItems.size(); i++) {
                        Log.i(TAG, listItems.get(i).get("news_title"));
                        Log.i(TAG, listItems.get(i).get("news_href"));
                        if(!(listItems.get(i).get("news_detail") ==null)){
                            Log.i(TAG, listItems.get(i).get("news_detail"));
                        }

                    }
                }
            }

        };//Handel结束

        Thread thread = new Thread(this);//线程初始化，注意线程的初始化应该在Handel后面
        thread.start();


    }//onCreate结束

    @Override
    public void run() {//爬虫爬取数据
        String url = "http://www.ltaaa.com/";
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
            Elements listDiv = doc.getElementsByAttributeValue("class", "show");
            msg = handler.obtainMessage(1);//填写快递单号
            msg.obj = listDiv;//快递内容
            handler.sendMessage(msg);//将元素发过去
            Log.i(TAG, "元素已发送");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }//run方法结束

    public boolean select(String id,String pwd){
        SQLiteDatabase db = myDBHelper.getReadableDatabase();
        Cursor cursor =  db.rawQuery("SELECT * FROM USERS WHERE Users_name = ?",
                new String[]{id});
        //存在数据才返回true
        if(cursor.moveToNext()) {
            String db_password = cursor.getString(cursor.getColumnIndex("Users_password"));
            if(db_password.equals(pwd)){
                cursor.close();
                return true;
            }
            else {
                cursor.close();
                return false;
            }
        }
        else{
            cursor.close();
            return false;
        }
    }


    public void insert(){
        SQLiteDatabase db = myDBHelper.getWritableDatabase();

        for(int i=0;i<5;i++){
            if(!ifhave("username"+String.valueOf(i))){
                db.execSQL("INSERT INTO USERS(Users_name,Users_password) values(?,?)", new String[]{"username"+String.valueOf(i) , String.valueOf(i) +String.valueOf(i)+ String.valueOf(i) });
                Log.i(TAG,"insert="+i+"   "+"username"+i);
            }
            else{
                Log.i(TAG,"已经有数据了！");
            }
        }
    }

    public boolean ifhave(String id){
        SQLiteDatabase db = myDBHelper.getReadableDatabase();
        Cursor cursor =  db.rawQuery("SELECT * FROM USERS WHERE Users_name = ?", new String[]{id});
        //存在数据才返回true
        if(cursor.moveToNext()) {
            cursor.close();
            return true;
        }
        else{
            cursor.close();
            return false;
        }
    }

    public void button(View view) {//登录界面
        String user = String.valueOf(username.getText());
        String pwd = String.valueOf(password.getText());
        boolean flag = select(user,pwd);

        if (flag) {
            Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
            Intent intent;
            intent = new Intent(MainActivity.this,News.class);//打开列表页面
            //装入Intent
            Bundle bdl = new Bundle();
            for(int i=0;i<listItems.size();i++){//目前表里只有新闻标题和
                bdl.putSerializable(String.valueOf(i), listItems.get(i));
            }
            bdl.putInt("list_size",listItems.size());
            Log.i(TAG,"put list into Intent:"+listItems);
            intent.putExtras(bdl);  //intent传输数据

//            Intent开始跳转
            startActivity(intent);//该方法不需要接收反馈数据
            finish();
        }
        else {
            Toast.makeText(this, "登录失败", Toast.LENGTH_SHORT).show();
        }
    }
}
