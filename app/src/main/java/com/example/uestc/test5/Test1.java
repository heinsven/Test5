package com.example.uestc.test5;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher.ViewFactory;

import java.util.ArrayList;
import java.util.List;

public class Test1 extends AppCompatActivity {
    private Button btnAdd, btnSub;
    private ImageSwitcher imageSwitcher;
    private int index = 0;
    private List<Drawable> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test1);
        putData();
        imageSwitcher = (ImageSwitcher) findViewById(R.id.imageSwitcher1);
        btnAdd = (Button) findViewById(R.id.btnadd);
        btnSub = (Button) findViewById(R.id.btnSub);
        btnAdd.setOnClickListener(myClick);
        btnSub.setOnClickListener(myClick);

        //通过代码设定从左缓进，从右换出的效果。
        imageSwitcher.setInAnimation(AnimationUtils.loadAnimation(Test1.this, android.R.anim.slide_in_left));
        imageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(Test1.this, android.R.anim.slide_out_right));
        imageSwitcher.setFactory(new ViewFactory() {
            @Override
            public View makeView() {
                return new ImageView(Test1.this);
            }
        });
        imageSwitcher.setImageDrawable(list.get(0));
    }

    private View.OnClickListener myClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnadd:
                    index--;
                    if (index < 0) {
                        index = list.size() - 1;
                    }
                    imageSwitcher.setImageDrawable(list.get(index));
                    break;
                case R.id.btnSub:
                    index++;
                    if (index >= list.size()) {
                        index = 0;
                    }
                    imageSwitcher.setImageDrawable(list.get(index));
                    break;
            }
        }
    };

    private void putData() {
        //填充图片的Drawable资源数组
        list = new ArrayList<>();
        list.add(getResources().getDrawable(R.drawable.bmp1));
        list.add(getResources().getDrawable(R.drawable.bmp2));
        list.add(getResources().getDrawable(R.drawable.bmp3));
        list.add(getResources().getDrawable(R.drawable.bmp4));
        list.add(getResources().getDrawable(R.drawable.bmp5));
    }
}
