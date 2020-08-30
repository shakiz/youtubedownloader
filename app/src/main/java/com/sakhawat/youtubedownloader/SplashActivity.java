package com.sakhawat.youtubedownloader;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import com.sakhawat.youtubedownloader.tools.PrefManager;
import static com.sakhawat.youtubedownloader.tools.Constants.mAlreadyVisited;

public class SplashActivity extends AppCompatActivity {
    private Animation topAnim, middleAnim, bottomAnim;
    private View first,second,third,fourth,fifth;
    private TextView AppName, DevelopedBy, Developer, TagLine;
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //region init and perform UI interactions
        initUI();
        bindUIWithComponents();
        //endregion
    }

    //region init XML components with backend
    private void initUI() {
        first = findViewById(R.id.FirstLine);
        second = findViewById(R.id.SecondLine);
        third = findViewById(R.id.ThirdLine);
        fourth = findViewById(R.id.FourthLine);
        fifth = findViewById(R.id.FifthLine);
        AppName = findViewById(R.id.AppName);
        DevelopedBy = findViewById(R.id.DevelopedBy);
        Developer = findViewById(R.id.DeveloperName);
        TagLine = findViewById(R.id.TagLine);
        prefManager = new PrefManager(this);
    }
    //endregion

    //region perform UI interactions
    private void bindUIWithComponents() {
        setAnimation();
        loadAnimationForUI();

        //region splash screen code to call new activity after some time
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if (prefManager.getBoolean(mAlreadyVisited)) {
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                } else {
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }, 2000);
    }
    //endregion

    //region set animation
    private void setAnimation() {
        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_anim_splash);
        middleAnim = AnimationUtils.loadAnimation(this,R.anim.middle_anim_splash);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_anim_splash);
    }
    //endregion

    //region load animation into UI components
    private void loadAnimationForUI() {
        first.setAnimation(topAnim);
        second.setAnimation(topAnim);
        third.setAnimation(topAnim);
        fourth.setAnimation(topAnim);
        fifth.setAnimation(topAnim);

        AppName.setAnimation(middleAnim);
        DevelopedBy.setAnimation(bottomAnim);
        Developer.setAnimation(bottomAnim);
        TagLine.setAnimation(bottomAnim);
    }
    //endregion
}