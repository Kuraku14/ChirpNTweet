package com.dyadav.chirpntweet.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtility {

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public static boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        Process ipProcess = null;
        try {
            ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            if(ipProcess!=null) { ipProcess.destroy(); }
            return (exitValue == 0);
        }  catch (InterruptedException e) {
            if(ipProcess!=null) { ipProcess.destroy(); }
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            if(ipProcess!=null) { ipProcess.destroy(); }
        }
        return false;
    }
}
