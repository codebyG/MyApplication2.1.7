package kr.or.seoulshimin.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResultLauncher;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.appupdate.AppUpdateOptions;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;

public class MainActivity extends AppCompatActivity {

    WebView web;

    private AppUpdateManager appUpdateManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("onCreate","start");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        web = (WebView) this.findViewById(R.id.webview1);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //새창 허용
        WebSettings set = web.getSettings();
        set.setSupportMultipleWindows(true);

        //메인슬라이드 영상 멈춤 현상 제거
        set.setMediaPlaybackRequiresUserGesture(false);
        web.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onCreateWindow(WebView view, boolean dialog, boolean userGesture, Message resultMsg)
            {
                WebView.HitTestResult result = view.getHitTestResult();
                String data = result.getExtra();
                Context context = view.getContext();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data));
                context.startActivity(browserIntent);
                return false;
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                //alert  허용
                return super.onJsAlert(view, url, message, result);
            }
        });

        //캐시 삭제
        web.clearCache(true);
        web.clearHistory();

        //자바스크립트 허용
        web.getSettings().setJavaScriptEnabled(true);
        //헤더 SSCWS 기입 native 확인시 사용
        web.getSettings().setUserAgentString(
                this.web.getSettings().getUserAgentString()
                        + " "
                        + getString(R.string.user_agent_suffix)
        );


        //쿠키 동기화
        web.setWebViewClient(new WebViewClient(){
                @Override
                public void onPageFinished(WebView view, String url) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        CookieSyncManager.getInstance().sync();
                    } else {
                        CookieManager.getInstance().flush();
                    }
                }
            }
        );

        //android.os.NetworkOnMainThreadException 발생으로 AsyncTask 사용

        /*************************/
        /* 버전 체크 및 업데이트 */
        /*************************/
        //1.8 버전부터 사용가능 20211212 -구글 버전확인 불가로 변경
        //1.5 버전부터 사용가능 X
        //rt 버전업데이트 필요, ver 현재 기기 설치 버전

        // sdk30 부터 동작 안됨 20241108 - 아래코드로 대체
        /*  RetriveTweetTask rtt = new RetriveTweetTask(context);
        Boolean hasUpdateVerTf = false;
        try {
            hasUpdateVerTf = rtt.execute(getPackageName()).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }*/

         /* if(false){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    context);

            // 제목셋팅
            alertDialogBuilder.setTitle("업데이트 알림");

            // AlertDialog 셋팅
            alertDialogBuilder
                    .setMessage("새로운 업데이트가 존재합니다. 새로운 버전으로 업데이트 하시겠습니까?")
                    .setCancelable(false)
                    .setPositiveButton("플레이스토어 이동",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog, int id) {
                                    // 업데이트로 이동한다
                                    //JLog.d("Move for update");

                                    final String appPackageName = getPackageName();
                                    try {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                    } catch (android.content.ActivityNotFoundException anfe) {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                    }
                                }
                            })
                    .setNegativeButton("취소",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog, int id) {
                                    // 다이얼로그를 취소한다
                                    dialog.cancel();
                                }
                            });

            // 다이얼로그 생성
            AlertDialog alertDialog = alertDialogBuilder.create();

            // 다이얼로그 보여주기
            alertDialog.show();
        }*/

        // 업데이트 상태 확인 //(업데이트 이 코드로 변경 - 20241108)
        Context context =  MainActivity.this;
        appUpdateManager = AppUpdateManagerFactory.create(context);
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                    MainActivity.this.startUpdate(appUpdateInfo, AppUpdateType.IMMEDIATE);
                }
            }
        });

        web.getSettings().setBuiltInZoomControls(true);
        web.getSettings().setSupportZoom(true);
        //YTPlayer 실행 시 반드시 필요
        web.getSettings().setDomStorageEnabled(true);
        web.loadUrl("http://seoulshimin.or.kr/");
    }

    protected void onPause() {
        super.onPause();
        try {
            Class.forName("android.webkit.WebView")
                    .getMethod("onPause", (Class[]) null)
                    .invoke(web, (Object[]) null);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    protected void onResume() {
        super.onResume();
        try {
            Class.forName("android.webkit.WebView")
                    .getMethod("onResume", (Class[]) null)
                    .invoke(web, (Object[]) null);
        } catch(Exception e) {
            e.printStackTrace();
        }

        //인앱업데이트  - 백그라운드로 내려가거나 업데이트중 멈추었다 다시 켯을때 계속 진행
        if(appUpdateManager!=null){
            appUpdateManager
                    .getAppUpdateInfo()
                    .addOnSuccessListener(
                            appUpdateInfo -> {
                                if (appUpdateInfo.updateAvailability()
                                        == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                                    // If an in-app update is already running, resume the update.
                                    appUpdateManager.startUpdateFlowForResult(
                                            appUpdateInfo,
                                            activityResultLauncher,
                                            AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build());
                                }
                            });
        }
    }

    //뒤로가기 시 이전페이지
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && web.canGoBack()) {
            web.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //인앱업데이트 실행
    void startUpdate(AppUpdateInfo info,int type){
        appUpdateManager.startUpdateFlowForResult(
                // Pass the intent that is returned by 'getAppUpdateInfo()'.
                info,
                // an activity result launcher registered via registerForActivityResult
                activityResultLauncher,
                // Or pass 'AppUpdateType.FLEXIBLE' to newBuilder() for
                // flexible updates.
                AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build());
    }

    //인앱업데이트 진행 여부에 따른 처리
    ActivityResultLauncher<IntentSenderRequest> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(),result -> {
        if (result.getResultCode() != RESULT_OK) {
            // If the update is cancelled or fails,
            // you can request to start the update again.
            // 앱 업데이트를 안했을때 처리를 해주면됨.
            Log.v("UPDATE_SUCCESS","CANCEL");
        }else{
            // 앱 업데이트를 진행되고 다시 돌아왔을경우 계속 진행되도록 작업.
            Log.v("UPDATE_SUCCESS","COMPLETE");
        }

    });

}




