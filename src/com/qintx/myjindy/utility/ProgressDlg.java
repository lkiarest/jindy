package com.qintx.myjindy.utility;

import com.qintx.myjindy.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.TextView;

public class ProgressDlg {

    private static ProgressDialog pdlg;

    public static void showProgress(Context context, String title, String msg) {
        if (pdlg != null) {
            return;
        }
        pdlg = new ProgressDialog(context);
        pdlg.setIndeterminate(true);
        pdlg.setCancelable(true);
        pdlg.setMessage(msg);
        pdlg.show();
        pdlg.setContentView(R.layout.customprogress);
        ((TextView)pdlg.findViewById(R.id.oaprogresstitle)).setText(msg);
    }

    public static void showProgress(Context context, String title, int msgId) {
        String msg = context.getResources().getString(msgId);
        showProgress(context, title, msg);
    }

    public static void closeProgress() {
        if (pdlg != null) {
            pdlg.dismiss();
            pdlg = null;
        }
    }
}
