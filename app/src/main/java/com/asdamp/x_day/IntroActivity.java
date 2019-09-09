package com.asdamp.x_day;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntro2;

public class IntroActivity extends AppIntro {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Instead of fragments, you can also use our default slide.
        // Just create a `SliderPage` and provide title, description, background and image.
        // AppIntro will do the rest.

        addSlide(AppIntroBaseLottieFragment.newInstance(R.raw.clock_animation,"BENVENUTO IN XDAY","Con Xday potrai sapere quanto tempo manca o è passato da un certo evento",this.getResources().getColor(R.color.md_blue_500),Color.WHITE,Color.WHITE));
        addSlide(AppIntroBaseLottieFragment.newInstance(R.raw.completed_animation,"INIZIAMO?","Aggiungi una nuova data nella prossima schermata successiva, e tieni traccia dei tuoi eventi più importanti",this.getResources().getColor(R.color.md_green_500),Color.WHITE,Color.WHITE));

        // OPTIONAL METHODS
        // Override bar/separator color.
      //  setBarColor(Color.parseColor("#3F51B5"));
        setSeparatorColor(Color.WHITE);
        // Hide Skip/Done button.
        showSkipButton(true);
        setProgressButtonEnabled(true);

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permission in Manifest.
        setVibrate(true);
        setVibrateIntensity(30);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        finish();

    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        finish();

    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}