package com.asdamp.utility;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.View;


import com.asdamp.x_day.BuildConfig;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import timber.log.Timber;

public class UserInfoUtility {
    public static void smallVibration(Context c){
        Vibrator v = (Vibrator) c.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (v != null) {
                v.vibrate(VibrationEffect.createOneShot(100,VibrationEffect.DEFAULT_AMPLITUDE));
            }
        }else{
            //deprecated in API 26
            if (v != null) {
                v.vibrate(100);
            }
        }
    }

    public static void loadAd(AdView mAdView){
        if(BuildConfig.FLAVOR.equals("ads")) {
            List<String> testDevices = new ArrayList<>();
            testDevices.add(AdRequest.DEVICE_ID_EMULATOR);
            testDevices.add("D443C852D1059D95BAACAE55EF01A624");

            RequestConfiguration requestConfiguration
                    = new RequestConfiguration.Builder()
                    .setTestDeviceIds(testDevices)
                    .build();
            MobileAds.setRequestConfiguration(requestConfiguration);
            mAdView.loadAd(new AdRequest.Builder().build());
            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    mAdView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAdFailedToLoad(LoadAdError adError) {
                    Timber.d(adError.getMessage());
                    mAdView.setVisibility(View.GONE);
                }

                @Override
                public void onAdOpened() {
                    // Code to be executed when an ad opens an overlay that
                    // covers the screen.
                }

                @Override
                public void onAdClosed() {
                    // Code to be executed when when the user is about to return
                    // to the app after tapping on an ad.
                }
            });
        }
        else
            mAdView.setVisibility(View.GONE);
    }

    public static SpannableStringBuilder makeSpannable(String text, String regex) {

        StringBuffer sb = new StringBuffer();
        SpannableStringBuilder spannable = new SpannableStringBuilder();

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            sb.setLength(0); // clear
            String group = matcher.group();
            // caution, this code assumes your regex has single char delimiters
            matcher.appendReplacement(sb, group);

            spannable.append(sb.toString());
            int start = spannable.length() - group.length();

            spannable.setSpan( new RelativeSizeSpan(1.5f), start, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        sb.setLength(0);
        matcher.appendTail(sb);
        spannable.append(sb.toString());
        return spannable;
    }
}
