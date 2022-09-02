package com.handheld.uhfrdemo.SAED.Helpers;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.graphics.Color;
import android.support.design.widget.Snackbar;

import android.view.View;

import android.widget.TextView;


import com.handheld.uhfr.R;


public class NoteMessage {




    public static void showSnackBar(Activity activity, String message) {
        // create an instance of the snackbar
        View v = activity.findViewById(android.R.id.content);
        final Snackbar snackbar = Snackbar.make(v, "", Snackbar.LENGTH_LONG);

        // inflate the custom_snackbar_view created previously
        @SuppressLint("InflateParams")
        View customSnackView = activity.getLayoutInflater().inflate(R.layout.custom_snackbar_view, null);

        // set the background of the default snackbar as transparent
        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);

        // now change the layout of the snackbar
        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();

        // set padding of the all corners as 0
        snackbarLayout.setPadding(0, 0, 0, 0);

        TextView tv = customSnackView.findViewById(R.id.textView1);
        tv.setText(message);

        customSnackView.findViewById(R.id.snack_icon).setOnClickListener(v1 -> snackbar.dismiss());

        // add the custom snack bar layout to snackbar layout
        snackbarLayout.addView(customSnackView, 0);

        snackbar.show();

    }

    public interface OnSnackBarClicked {
        void onSnackBarClicked(Snackbar snackbar);
    }
}
