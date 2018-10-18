package com.example.imageapp.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.imageapp.R;
import com.example.imageapp.adapter.ImageAdapter;
import com.example.imageapp.model.ImageBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageAdapter mAdapter;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            mAdapter.addAllImages((List<ImageBean>) msg.obj);
        }
    };
    private GridView gv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        loadPicture();

    }

    //在menu bar加
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_take) {
            startActivityForResult(new Intent(this, CameraActivity.class), 1024);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void loadPicture() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //scan images
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = getContentResolver();

                Cursor mCursor = mContentResolver.query(mImageUri, new String[]{
                                MediaStore.Images.Media.DATA,
                                MediaStore.Images.Media.DISPLAY_NAME,
                                MediaStore.Images.Media.DATE_ADDED,
                                MediaStore.Images.Media._ID},
                        null,
                        null,
                        MediaStore.Images.Media.DATE_ADDED);

                ArrayList<ImageBean> imageList = new ArrayList<>();

                //read the scaned images
                if (mCursor != null) {
                    while (mCursor.moveToNext()) {
                        // get image path
                        String path = mCursor.getString(
                                mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        //get image name
                        String name = mCursor.getString(
                                mCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                        //get the image capture time
                        long time = mCursor.getLong(
                                mCursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
                        imageList.add(new ImageBean(path, time, name));
                        System.out.println(path);
                    }
                    mCursor.close();
                }
                Collections.reverse(imageList);
                Message msg = mHandler.obtainMessage();
                msg.obj = imageList;
                mHandler.sendMessage(msg);
            }
        }).start();
    }


    private void initView() {
        gv = (GridView) findViewById(R.id.gv);
        mAdapter = new ImageAdapter(this);
        gv.setAdapter(mAdapter);

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageBean image = (ImageBean) mAdapter.getItem(position);
                Intent intent = new Intent(MainActivity.this, ShowActivity.class);
                intent.putExtra("image", image);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loadPicture();
    }
}
