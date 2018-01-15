package com.mars.android20180115_2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ImageView img;
    TextView tv, tv2, tv3;
    ProgressBar pb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img = (ImageView)findViewById(R.id.imageView);
        tv = (TextView)findViewById(R.id.textView);
        tv2 = (TextView)findViewById(R.id.textView2);
        tv3 = (TextView)findViewById(R.id.textView3);
        pb = (ProgressBar)findViewById(R.id.progressBar);

    }
    public void click1(View v)
    {
        //副執行緒
        new Thread()
        {
            @Override
            public void run() {
                super.run();
                //網址
                String str_url = "https://5.imimg.com/data5/UH/ND/MY-4431270/red-rose-flower-500x500.jpg";
                URL url;
                try {
                    url = new URL(str_url);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();//创建一个Http客户端
                    conn.setRequestMethod("GET");
                    conn.connect();
                    InputStream inputStream = conn.getInputStream();
                    //ByteArrayOutputStream
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    byte[] buf = new byte[1024];//每次讀的字元
                    final int totalLength = conn.getContentLength();///文件长度
                    int sum = 0;
                    int length;

                    // 以字元的方式來讀圖片數據
                    while ((length = inputStream.read(buf)) != -1)
                    {
                        sum += length;
                        final int tmp = sum;

                        bos.write(buf, 0, length);//写入输出流
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                tv.setText(String.valueOf(tmp) + "/" + totalLength);
                                //寫入進度到進度條
                                pb.setProgress(100 * tmp / totalLength);
                            }
                        });
                    }
                    //将字节数组输出流转换为字节数组
                    byte[] results = bos.toByteArray();
                    //生成二进制图片
                    final Bitmap bmp = BitmapFactory.decodeByteArray(results, 0, results.length);
                    //更新主程序的UI畫面
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            img.setImageBitmap(bmp);
                        }
                    });
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    public void click2(View v)
    {
        MyTask task = new MyTask();
        task.execute(10);
    }
    //建立AsyncTask class 來用
    class MyTask extends AsyncTask<Integer, Integer, String> {
        @Override
        //Integer... 參數不確定
        //這個是在背後執行 副執行緒
        protected String doInBackground(Integer... integers) {
            int i;

            for (i = 0; i <= integers[0]; i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d("task", "doinblackground, I:" + i);
                publishProgress(i);//公佈進度
            }
            return "OKay";
        }

        @Override
        //主執行緒執行
        protected void onPreExecute() {
            super.onPreExecute();
        }

        //主執行緒執行
        //String s是接收doInBackground return的字串
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            tv3.setText(s);
        }

        //主執行緒執行
        @Override
        //接受publishProgress(i); i會傳到參數裡面
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            tv2.setText(String.valueOf(values[0]));
        }
        }

    public void click3(View v)
    {
        MyImageTask task = new MyImageTask();
        task.execute("https://5.imimg.com/data5/UH/ND/MY-4431270/red-rose-flower-500x500.jpg");
    }
         /*
        AsyncTask<Params, Progress, Result>，這是基本的架構，使用泛型來定義參數，
        泛型意思是，你可以定義任意的資料型態給他。
        Params ： 參數，你要餵什麼樣的參數給它。
        Progress ：進度數值
        Result ： 結果，你希望這個背景任務最後會有什麼樣的結果回傳給你。
         */
        //String 因為要傳網址給他
        //Integer 進度數值就是用整數就可以
        //我們要下載的是圖片 所以用Bitmap
         class MyImageTask extends AsyncTask<String, Integer, Bitmap>
         {
             @Override
             protected void onPostExecute(Bitmap bitmap) {
                 super.onPostExecute(bitmap);
                 img.setImageBitmap(bitmap);
             }

             @Override
             protected void onProgressUpdate(Integer... values) {
                 super.onProgressUpdate(values);
             }

             @Override
             protected Bitmap doInBackground(String... strings) {
                 String str_url = strings[0];
                 URL url;
                 try {
                     url = new URL(str_url);
                     HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                     conn.setRequestMethod("GET");
                     conn.connect();
                     InputStream inputStream = conn.getInputStream();
                     ByteArrayOutputStream bos = new ByteArrayOutputStream();
                     byte[] buf = new byte[1024];
                     final int totalLength = conn.getContentLength();
                     int sum = 0;
                     int length;
                     while ((length = inputStream.read(buf)) != -1)
                     {
                         sum += length;
                         final int tmp = sum;
                         bos.write(buf, 0, length);
                     }
                     byte[] results = bos.toByteArray();
                     final Bitmap bmp = BitmapFactory.decodeByteArray(results, 0, results.length);
                     return bmp;
                 } catch (MalformedURLException e) {
                     e.printStackTrace();
                 } catch (ProtocolException e) {
                     e.printStackTrace();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
                 return null;
             }
         }


    }




