package com.sakhawat.youtubedownloader.tools;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import com.sakhawat.youtubedownloader.R;

public class UX {
    private Context context;
    public Dialog loadingDialog;
    private Tools tools;

    public UX(Context context) {
        this.context = context;
        tools = new Tools(context);
        loadingDialog = new Dialog(context);
    }

    /**
     * This method will remove Loading view
     */
    public void removeLoadingView(){
        if (loadingDialog.isShowing()) loadingDialog.cancel();
    }

    /**
     * This method will perform a custom dialog by passing the layout resId
     */
    public void getCustomDialog(int layoutResId, String tag){
        Dialog customDialog = new Dialog(context);
        customDialog.setContentView(layoutResId);
        customDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        if (customDialog != null) {
            customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
        customDialog.getWindow().setDimAmount(0.6f);
        customDialog.setCanceledOnTouchOutside(false);
        customDialog.show();

        if (tag.equals(context.getString(R.string.close_application))) {
            customDialog.findViewById(R.id.CloseApp).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tools.exitApp();
                }
            });

            customDialog.findViewById(R.id.NotCloseApp).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (customDialog.isShowing()){
                        customDialog.dismiss();
                    }
                }
            });
        }
        else if (tag.equals(context.getString(R.string.ok))){
            customDialog.findViewById(R.id.OkButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (customDialog.isShowing()){
                        customDialog.dismiss();
                    }
                }
            });
        }
        else if (tag.equals(context.getString(R.string.error))){
            customDialog.findViewById(R.id.CloseButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (customDialog.isShowing()){
                        customDialog.dismiss();
                    }
                }
            });
        }
    }

    //region get layout params
    public LinearLayout.LayoutParams getLayoutParams(int width, int height, int gravity){
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width,height);
        layoutParams.gravity = gravity;
        return layoutParams;
    }
    //endregion
}
