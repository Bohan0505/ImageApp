package com.example.imageapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.imageapp.R;
import com.example.imageapp.model.ImageBean;
import com.example.imageapp.util.ImageUtil;
import com.example.imageapp.view.ClipImageView;

import java.util.ArrayList;
import java.util.List;

public class ShowActivity extends AppCompatActivity implements View.OnClickListener {

    private ClipImageView process_img;
    private Button btn_crop;
    private Button btn_grey;
    private Button btn_back;

    private List<Bitmap> mBitmaps = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        initView();

        ImageBean image = getIntent().getParcelableExtra("image");
        if (image != null) {
            Bitmap bitmap = ImageUtil.decodeSampledBitmapFromFile(image.getPath(), 720, 1080);
            if (bitmap != null) {
                mBitmaps.add(bitmap);
                process_img.setBitmapData(bitmap);
            }
        }

    }

    private void initView() {
        process_img = (ClipImageView) findViewById(R.id.process_img);
        btn_crop = (Button) findViewById(R.id.btn_crop);
        btn_grey = (Button) findViewById(R.id.btn_grey);
        btn_back = (Button) findViewById(R.id.btn_back);

        btn_crop.setOnClickListener(this);
        btn_grey.setOnClickListener(this);
        btn_back.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.crop, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_undo) {

            if (mBitmaps.size() > 1) {
                Bitmap lastBitmap = mBitmaps.get(mBitmaps.size() - 1);
                mBitmaps.remove(lastBitmap);
                process_img.setBitmapData(mBitmaps.get(mBitmaps.size() - 1));
                lastBitmap.recycle();

            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_crop:

                Bitmap cropBitmap = process_img.clipImage();
                mBitmaps.add(cropBitmap);
                process_img.setBitmapData(cropBitmap);

                break;
            case R.id.btn_grey:

                Bitmap greyBitmap = greyImage(mBitmaps.get(mBitmaps.size() - 1));
                mBitmaps.add(greyBitmap);
                process_img.setBitmapData(greyBitmap);

                break;
            case R.id.btn_back:
                Intent intent = new Intent(ShowActivity.this, MainActivity.class);
                startActivity(intent);
                break;
        }
    }

    /** */
    /**
     * greying
     *
     * input image
     *
     * @return grey image
     */
    private Bitmap greyImage(Bitmap bmp) {
        if (bmp != null) {
            Paint paint = new Paint();
            int height = bmp.getHeight();
            int width = bmp.getWidth();
            Bitmap bm = Bitmap.createBitmap(width, height,
                    Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(bm);
            ColorMatrix cm = new ColorMatrix();
            cm.setSaturation(0);
            ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
            paint.setColorFilter(f);
            c.drawBitmap(bmp, 0, 0, paint);
            return bm;
        } else {
            return null;
        }
    }
}
