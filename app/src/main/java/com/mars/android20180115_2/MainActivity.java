package com.mars.android20180115_2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity {
    ImageView img;
    TextView tv;
    ProgressBar pb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img = (ImageView)findViewById(R.id.imageView);
        tv = (TextView)findViewById(R.id.textView);
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

}
