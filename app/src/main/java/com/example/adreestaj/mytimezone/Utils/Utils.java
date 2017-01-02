package com.example.adreestaj.mytimezone.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.TimeZone;

/**
 * Created by Adrees Taj on 1/1/2017.
 */

public class Utils {

    public static String getCurrentTimeZoneId(){
        TimeZone timeZone = TimeZone.getDefault();
        return timeZone.getID();

    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
