package kr.or.seoulshimin.myapplication;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.tasks.Task;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;

/**
 * Created by LG on 2018-11-30.
 */

public class RetriveTweetTask extends AsyncTask<String, Void, Boolean> {

    private Context mContext;

    public RetriveTweetTask (Context context){
        mContext = context;
    }

    @Override //업데이트 체크 불가 !!! derprecated
    protected Boolean doInBackground(String... packageName) {


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

        return null;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
    }
}
