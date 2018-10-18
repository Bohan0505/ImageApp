package com.example.imageapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.imageapp.R;
import com.example.imageapp.view.CameraPreview;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraActivity extends AppCompatActivity implements View.OnClickListener {

    private CameraPreview mPreview;
    private Button btn_back;
    private Button btn_take;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPreview.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stopCamera();
    }

    private void initView() {
        mPreview = (CameraPreview) findViewById(R.id.cameraView);
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_take = (Button) findViewById(R.id.btn_take);

        btn_back.setOnClickListener(this);
        btn_take.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:

                setResult(RESULT_OK);
                finish();

                break;
            case R.id.btn_take:

                mPreview.takePicture(new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        mPreview.showCameraPreview();
                        new SavePicTask(data).execute();
                    }
                });

                break;
        }
    }

    private class SavePicTask extends AsyncTask<Void, Void, String> {
        private byte[] data;

        SavePicTask(byte[] data) {
            this.data = data;
        }

        @Override
        protected String doInBackground(Void... params) {

            try {

                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss"); // format time
                String filename = format.format(new Date()) + ".jpg";
                File folder = new File(Environment.getExternalStorageDirectory()
                        + "/DCIM/");
                if (!folder.exists()) { // 如果目录不存在，则创建一个名为"finger"的目录
                    folder.mkdirs();
                }
                File jpgFile = new File(folder, filename);

                Bitmap bMap = BitmapFactory.decodeByteArray(data, 0, data.length);

                Matrix matrix = new Matrix();
                matrix.reset();
                matrix.postRotate(90);
                Bitmap bMapRotate = Bitmap.createBitmap(bMap, 0, 0, bMap.getWidth(),
                        bMap.getHeight(), matrix, true);

                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(jpgFile));
                bMapRotate.compress(Bitmap.CompressFormat.JPEG, 100, bos);//copmress images to stream
                bos.flush();//output
                bos.close();//input
                bMap.recycle();
                bMapRotate.recycle();

                return jpgFile.getPath();


            } catch (Exception e) {

                return null;

            }

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (!TextUtils.isEmpty(result)) {

                Uri data = Uri.parse("file://" + result);
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, data));
                Toast.makeText(CameraActivity.this, result, Toast.LENGTH_SHORT).show();

            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(RESULT_OK);
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
