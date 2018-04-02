package com.slkk.opencvdemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class JniActivity extends AppCompatActivity implements View.OnClickListener {

    static {
        System.loadLibrary("native-lib");
    }

    private ImageView imageView;
    private Button showBtn;
    private Button processBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jni);
        imageView = findViewById(R.id.imageView);
        showBtn = findViewById(R.id.show);
        showBtn.setOnClickListener(this);
        processBtn = findViewById(R.id.process);
        processBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test);
        if (v == showBtn) {
            imageView.setImageBitmap(bitmap);
        }else{
            getEge(bitmap);
            imageView.setImageBitmap(bitmap);
        }

    }

    native void getEge(Bitmap bitmap) ;
}
