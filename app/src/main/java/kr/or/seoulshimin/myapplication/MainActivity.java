package kr.or.seoulshimin.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    WebView web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        Context context =  MainActivity.this;
        RetriveTweetTask rtt = new RetriveTweetTask(context);
        Boolean hasUpdateVerTf = false;
        try {
            hasUpdateVerTf = rtt.execute(getPackageName()).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if(hasUpdateVerTf){
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
                                    JLog.d("Move for update");

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
        }

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
    }

    //뒤로가기 시 이전페이지
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && web.canGoBack()) {
            web.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}




