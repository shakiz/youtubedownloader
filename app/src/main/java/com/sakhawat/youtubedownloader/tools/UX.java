package com.sakhawat.youtubedownloader.tools;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.sakhawat.youtubedownloader.R;
import java.io.Serializable;

public class UX {
    private Context context;
    public Dialog loadingDialog;

    public UX(Context context) {
        this.context = context;
        loadingDialog = new Dialog(context);
    }

    /**
     * This method will set the Toolbar
     *
     * @param toolbar,from,to
     */
    public void setToolbar(Toolbar toolbar, final Activity from, final Class to, final String key, final String value){
        ((AppCompatActivity) context).setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(key) && TextUtils.isEmpty(value)) context.startActivity(new Intent(from, to));
                else context.startActivity(new Intent(from, to).putExtra(key,value));
            }
        });
    }

    /**
     * This method will set the Toolbar
     *
     * @param toolbar,from,to
     */
    public void setToolbar(Toolbar toolbar, final Activity from, final Class to, final String key, final Serializable value){
        ((AppCompatActivity) context).setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(key) && value == null) context.startActivity(new Intent(from, to));
                else context.startActivity(new Intent(from, to).putExtra(key,value));
            }
        });
    }

    /**
     * This method will perform Loading view creator and cancel
     */
    public void getLoadingView(){
        loadingDialog.setContentView(R.layout.loading_layout);
        loadingDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        loadingDialog.getWindow().setDimAmount(0.6f);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.show();
    }

    /**
     * This method will remove Loading view
     */
    public void removeLoadingView(){
        if (loadingDialog.isShowing()) loadingDialog.cancel();
    }

    /**
     * This method will set spinner adapter
     */
    public void setSpinnerAdapter(String[] dataSet, Spinner spinner) {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(context,R.layout.spinner_drop,dataSet);
        spinner.setAdapter(spinnerAdapter);
        spinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spinnerAdapter.notifyDataSetChanged();
    }

    /**
     * This method will perform Spinner on change
     */
    public void onSpinnerChange(Spinner spinner, onSpinnerChangeListener listener) {
        final onSpinnerChangeListener customListener = listener;
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                customListener.onChange(parent, view, position, id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public interface onSpinnerChangeListener {
        void onChange(AdapterView<?> parent, View view, int position, long id);
    }
    //End spinner

    //region get layout params
    public LinearLayout.LayoutParams getLayoutParams(int width, int height, int gravity){
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width,height);
        layoutParams.gravity = gravity;
        return layoutParams;
    }
    //endregion
}
