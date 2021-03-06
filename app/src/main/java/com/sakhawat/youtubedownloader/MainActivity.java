package com.sakhawat.youtubedownloader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.sakhawat.youtubedownloader.tools.Tools;
import com.sakhawat.youtubedownloader.tools.UX;
import com.yausername.youtubedl_android.DownloadProgressCallback;
import com.yausername.youtubedl_android.YoutubeDL;
import com.yausername.youtubedl_android.YoutubeDLException;
import com.yausername.youtubedl_android.YoutubeDLRequest;
import java.io.File;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private Tools tools;
    private boolean downloading = false;
    private EditText urlEditText;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private TextView tv_status;
    private ProgressBar progress_bar, pb_status;
    private UX ux;
    private AdView mAdView;

    private DownloadProgressCallback callback = new DownloadProgressCallback() {
        @Override
        public void onProgressUpdate(float progress, long etaInSeconds) {
            runOnUiThread(() -> {
                        progress_bar.setProgress((int) progress);
                        tv_status.setText(progress + "% (Required " + etaInSeconds + " seconds more)");
                    }
            );
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //region init and perform UI interactions
        initUI();
        bindUIWithComponents();
        //endregion

        //region adMob
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                Log.v("onInitComplete","InitializationComplete");
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.v("onAdListener","AdlLoaded");
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
                Log.v("onAdListener","AdFailedToLoad");
                Log.v("onAdListener","AdFailedToLoad Error "+adError.getMessage());
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                Log.v("onAdListener","AdOpened");
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                Log.v("onAdListener","AdClicked");
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
                Log.v("onAdListener","AdLeftApplication");
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
                Log.v("onAdListener","AdClosed");
            }
        });
        //endregion
    }

    //region init UI with components
    private void initUI() {
        tools = new Tools(this);
        ux = new UX(this);
        urlEditText = findViewById(R.id.urlEditText);
        tv_status = findViewById(R.id.tv_status);
        progress_bar = findViewById(R.id.progress_bar);
        pb_status = findViewById(R.id.pb_status);
    }
    //endregion

    //region perform all UI interactions
    private void bindUIWithComponents() {
        //region set on back press
        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ux.getCustomDialog(R.layout.dialog_layout_exit_app, getString(R.string.close_application));
            }
        });
        //endregion

        //region start download button
        findViewById(R.id.startDownload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tools.hideSoftKeyboard(MainActivity.this);
                startDownload();
            }
        });
        //endregion
    }
    //endregion

    //region start download and perform download related job
    private void startDownload(){
        //region if a download is already pending
        if (downloading) {
            Toast.makeText(MainActivity.this, "Cannot start download.A download is already in progress", Toast.LENGTH_LONG).show();
            return;
        }
        //endregion

        //region get permission
        if (!tools.isStoragePermissionGranted()) {
            Toast.makeText(MainActivity.this, "Grant storage permission and try again", Toast.LENGTH_LONG).show();
            return;
        }
        //endregion

        //region get url from editText
        String url = urlEditText.getText().toString().trim();
        if (TextUtils.isEmpty(url)) {
            urlEditText.setError(getString(R.string.url_not_found));
            return;
        }
        //endregion

        YoutubeDLRequest request = new YoutubeDLRequest(url);
        File youtubeDLDir = getDownloadLocation();
        request.addOption("-o", youtubeDLDir.getAbsolutePath() + "/%(title)s.%(ext)s");

        showStart();

        downloading = true;
        try {
            YoutubeDL.getInstance().init(getApplication());
        } catch (YoutubeDLException e) {
            Log.e("Error", "Failed to initialize youtubedl-android", e);
        }
        Disposable disposable = Observable.fromCallable(() -> YoutubeDL.getInstance().execute(request, callback))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(youtubeDLResponse -> {
                    pb_status.setVisibility(View.GONE);
                    tv_status.setVisibility(View.VISIBLE);
                    progress_bar.setProgress(100);
                    tv_status.setText(getString(R.string.download_complete));
                    ux.getCustomDialog(R.layout.dialog_layout_download_finish,getString(R.string.ok));
                    Toast.makeText(MainActivity.this, "Download successful", Toast.LENGTH_LONG).show();
                    downloading = false;
                }, e -> {
                    if(BuildConfig.DEBUG) Log.e("Download Error",  "Failed to download", e);
                    pb_status.setVisibility(View.GONE);
                    tv_status.setVisibility(View.VISIBLE);
                    tv_status.setText(getString(R.string.download_failed));
                    ux.getCustomDialog(R.layout.dialog_layout_download_failed,getString(R.string.error));
                    Toast.makeText(MainActivity.this, "Download failed", Toast.LENGTH_LONG).show();
                    downloading = false;
                });
        compositeDisposable.add(disposable);
    }
    //endregion

    @Override
    protected void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }

    @NonNull
    private File getDownloadLocation() {
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File youtubeDLDir = new File(downloadsDir, "fast-youtube-downloader");
        if (!youtubeDLDir.exists()) youtubeDLDir.mkdir();
        return youtubeDLDir;
    }

    private void showStart() {
        tv_status.setText(getString(R.string.download_start));
        progress_bar.setProgress(0);
        pb_status.setVisibility(View.VISIBLE);
        tv_status.setVisibility(View.VISIBLE);
    }

    //region activity components
    @Override
    public void onBackPressed() {
        ux.getCustomDialog(R.layout.dialog_layout_exit_app, getString(R.string.close_application));
    }
    //endregion
}