package com.sakhawat.youtubedownloader;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.sakhawat.youtubedownloader.tools.Tools;
import com.sakhawat.youtubedownloader.tools.UX;

public class MainActivity2 extends AppCompatActivity {
    private Tools tools;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //region init and perform UI interactions
        initUI();
        bindUIWithComponents();
        //endregion
    }

    //region init UI with components
    private void initUI() {
        tools = new Tools(this);
    }
    //endregion

    //region perform all UI interactions
    private void bindUIWithComponents() {
        //region set on back press
        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tools.exitApp();
            }
        });
        //endregion
    }
    //endregion
}