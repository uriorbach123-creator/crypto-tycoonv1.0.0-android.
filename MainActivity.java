package com.cryptotycoon.game;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.libraries.ads.mobile.sdk.MobileAds;
import com.google.android.libraries.ads.mobile.sdk.common.AdRequest;
import com.google.android.libraries.ads.mobile.sdk.common.FullScreenContentError;
import com.google.android.libraries.ads.mobile.sdk.common.PreloadConfiguration;
import com.google.android.libraries.ads.mobile.sdk.initialization.InitializationConfig;
import com.google.android.libraries.ads.mobile.sdk.rewarded.RewardedAd;
import com.google.android.libraries.ads.mobile.sdk.rewarded.RewardedAdEventCallback;
import com.google.android.libraries.ads.mobile.sdk.rewarded.RewardedAdPreloader;

import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends Activity {
    // Google's official test IDs. Replace with your own AdMob IDs only after testing.
    private static final String ADMOB_APP_ID = "ca-app-pub-3940256099942544~3347511713";
    private static final String REWARDED_UNIT_ID = "ca-app-pub-3940256099942544/5224354917";

    private WebView webView;
    private boolean adsInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        webView = new WebView(this);
        webView.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowContentAccess(false);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.addJavascriptInterface(new AdsBridge(), "AndroidAds");
        setContentView(webView);
        webView.loadUrl("file:///android_asset/index.html");

        // GMA Next-Gen requires initialization on a background thread.
        new Thread(() -> MobileAds.initialize(
                getApplicationContext(),
                new InitializationConfig.Builder(ADMOB_APP_ID).build(),
                status -> runOnUiThread(() -> {
                    adsInitialized = true;
                    preloadRewardedAd();
                })
        )).start();
    }

    private void preloadRewardedAd() {
        if (!adsInitialized) return;
        AdRequest request = new AdRequest.Builder(REWARDED_UNIT_ID).build();
        PreloadConfiguration config = new PreloadConfiguration(request);
        RewardedAdPreloader.start(REWARDED_UNIT_ID, config);
    }

    private void sendAdResult(boolean earned) {
        runOnUiThread(() -> {
            if (webView != null) {
                String js = "if(window.onNativeAdResult){window.onNativeAdResult(" + earned + ");}";
                webView.evaluateJavascript(js, null);
            }
        });
    }

    private class AdsBridge {
        @JavascriptInterface
        public void showRewardedAd() {
            runOnUiThread(() -> {
                if (!adsInitialized) {
                    sendAdResult(false);
                    return;
                }

                RewardedAd ad = RewardedAdPreloader.pollAd(REWARDED_UNIT_ID);
                if (ad == null) {
                    sendAdResult(false);
                    preloadRewardedAd();
                    return;
                }

                AtomicBoolean resultSent = new AtomicBoolean(false);
                ad.setAdEventCallback(new RewardedAdEventCallback() {
                    @Override
                    public void onAdFailedToShowFullScreenContent(FullScreenContentError error) {
                        if (resultSent.compareAndSet(false, true)) sendAdResult(false);
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        if (resultSent.compareAndSet(false, true)) sendAdResult(false);
                    }
                });

                ad.show(MainActivity.this, rewardItem -> {
                    if (resultSent.compareAndSet(false, true)) sendAdResult(true);
                });
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (webView != null) {
            webView.evaluateJavascript(
                "(function(){var s=document.getElementById('sheet');if(s&&!s.hidden){document.dispatchEvent(new KeyboardEvent('keydown',{key:'Escape'}));return true;}return false;})()",
                value -> {
                    if (!"true".equals(value)) {
                        superOnBackPressed();
                    }
                }
            );
        } else {
            super.onBackPressed();
        }
    }

    private void superOnBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        if (webView != null) webView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webView != null) webView.onResume();
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.removeJavascriptInterface("AndroidAds");
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }
}
