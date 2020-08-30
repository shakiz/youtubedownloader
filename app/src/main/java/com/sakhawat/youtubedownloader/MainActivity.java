package com.sakhawat.youtubedownloader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.sakhawat.youtubedownloader.tools.Tools;
import com.sakhawat.youtubedownloader.tools.UX;
import com.yausername.youtubedl_android.DownloadProgressCallback;
import com.yausername.youtubedl_android.YoutubeDL;
import com.yausername.youtubedl_android.YoutubeDLException;
import com.yausername.youtubedl_android.YoutubeDLRequest;
import com.yausername.youtubedl_android.mapper.VideoInfo;

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
    private TextView tv_status, tv_command_output;
    private ProgressBar progress_bar, pb_status;
    private UX ux;

    private DownloadProgressCallback callback = new DownloadProgressCallback() {
        @Override
        public void onProgressUpdate(float progress, long etaInSeconds) {
            runOnUiThread(() -> {
                        progress_bar.setProgress((int) progress);
                        tv_status.setText(String.valueOf(progress) + "% (ETA " + String.valueOf(etaInSeconds) + " seconds)");
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
    }

    //region init UI with components
    private void initUI() {
        tools = new Tools(this);
        ux = new UX(this);
        urlEditText = findViewById(R.id.urlEditText);
        tv_status = findViewById(R.id.tv_status);
        tv_command_output = findViewById(R.id.tv_command_output);
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
                tools.exitApp();
            }
        });
        //endregion

        //region start download button
        findViewById(R.id.startDownload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            Toast.makeText(MainActivity.this, "Grant storage permission and retry", Toast.LENGTH_LONG).show();
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
                    progress_bar.setProgress(100);
                    tv_status.setText(getString(R.string.download_complete));
                    tv_command_output.setText(youtubeDLResponse.getOut());
                    Toast.makeText(MainActivity.this, "Download successful", Toast.LENGTH_LONG).show();
                    downloading = false;
                }, e -> {
                    if(BuildConfig.DEBUG) Log.e("Download Error",  "Failed to download", e);
                    pb_status.setVisibility(View.GONE);
                    tv_status.setText(getString(R.string.download_failed));
                    tv_command_output.setText(e.getMessage());
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
        File youtubeDLDir = new File(downloadsDir, "youtubedl-android");
        if (!youtubeDLDir.exists()) youtubeDLDir.mkdir();
        return youtubeDLDir;
    }

    private void showStart() {
        tv_status.setText(getString(R.string.download_start));
        progress_bar.setProgress(0);
        pb_status.setVisibility(View.VISIBLE);
    }

    private class DownloadVideo extends AsyncTask<Void, Void, Void>{

        @Override
        protected void onPreExecute() {
            ux.getLoadingView();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                YoutubeDL.getInstance().init(getApplication());
                YoutubeDLRequest request = new YoutubeDLRequest("https://www.youtube.com/watch?v=668nUCeBHyY");
                request.addOption("-f", "best");
                VideoInfo streamInfo = YoutubeDL.getInstance().getInfo(request);
                System.out.println(streamInfo.getUrl());
                Log.v("Error", "Video Downloading");
            } catch (YoutubeDLException | InterruptedException e) {
                Log.e("Error", "failed to initialize youtubedl-android", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ux.removeLoadingView();
        }
    }

    //region activity components
    @Override
    public void onBackPressed() {
        tools.exitApp();
    }
    //endregion
}