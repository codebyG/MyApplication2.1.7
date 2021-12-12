package kr.or.seoulshimin.myapplication;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;

/**
 * Created by LG on 2018-11-30.
 */

public class RetriveTweetTask extends AsyncTask<String, Void, Boolean> {

    private Context mContext;

    public RetriveTweetTask (Context context){
        mContext = context;
    }

    @Override
    protected Boolean doInBackground(String... packageName) {
        final boolean[] result = {false};
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(mContext);

        // Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    // This example applies an immediate update. To apply a flexible update
                    // instead, pass in AppUpdateType.FLEXIBLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                // Request the update.
                result[0] = true;
            }
        });

        /*String store_version = CommonUtils.getMarketVersion(packageName[0]);
        String device_version = "";

        try {
            device_version = mContext.getPackageManager().getPackageInfo(packageName[0], 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        JLog.d("store_version :::"+store_version);
        JLog.d("device_version :::"+device_version);
        boolean result = false;
        if (device_version != null && store_version != null && store_version.compareTo(device_version) > 0) {
            JLog.d("업데이트 필요");
            result = true;
        }*/

         /*
        String url = "market://details?id=" + "kr.or.seoulshimin.myapplication";

        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));*/

        return result[0];
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
    }
}
