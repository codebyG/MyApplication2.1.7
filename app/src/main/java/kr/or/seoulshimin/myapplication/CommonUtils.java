package kr.or.seoulshimin.myapplication;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CommonUtils {

    public static String getMarketVersion(String packageName) {
        try {
            Document doc =
                    Jsoup.connect("https://play.google.com/store/apps/details?id=" + packageName).get();
            Elements Version = doc.select(".htlgb").eq(6);
            for (Element mElement : Version) {
                return mElement.text().trim();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String getMarketVersionFast(String packageName) {
        String mData = "", mVer = null;
        try {
            URL mUrl = new URL("https://play.google.com/store/apps/details?id=" + packageName);
            HttpURLConnection mConnection = (HttpURLConnection) mUrl.openConnection();
            if (mConnection == null) return null;
            mConnection.setConnectTimeout(5000);
            mConnection.setUseCaches(false);
            mConnection.setDoOutput(true);
            if (mConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader mReader =
                        new BufferedReader(new InputStreamReader(mConnection.getInputStream()));
                while (true) {
                    String line = mReader.readLine();
                    if (line == null) break;
                    mData += line;
                }
                mReader.close();
            }
            mConnection.disconnect();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        String startToken = "<div class=\"BgcNfc\">현재 버전</div><span class=\"htlgb\"><div><span class=\"htlgb\">";
        String endToken = "</span></div>";
        int index = mData.indexOf(startToken);
        if (index == -1) {
            mVer = null;
        } else {
            mVer = mData.substring(index + startToken.length(), index + startToken.length() + 100);
            mVer = mVer.substring(0, mVer.indexOf(endToken)).trim();
        }
        return mVer;
    }



}


