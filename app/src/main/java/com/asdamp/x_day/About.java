package com.asdamp.x_day;

import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.asdamp.utility.StartupUtility;
import com.vansuita.materialabout.builder.AboutBuilder;
import com.vansuita.materialabout.views.AboutView;

public class About extends AppCompatActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		FrameLayout layout=findViewById(R.id.about_frame);
		AboutBuilder builder = AboutBuilder.with(this)
				.setAppIcon(R.mipmap.ic_launcher)
				.setAppName(R.string.app_name)
				.setPhoto(R.mipmap.profile_picture)
				.setCover(R.mipmap.profile_cover)
				.setLinksAnimated(true)
				.setDividerDashGap(13)
				.setName("Antonio Altieri")
				.setSubTitle("Mobile Developer")
				.setLinksColumnsCount(4)
				.addGooglePlayStoreLink("8002078663318221363")
				.addGitHubLink("asdamp")
				.addLinkedInLink("antonio-altieri-4b2544b0")
				.addEmailLink("altieriantonio.dev@gmail.com")
				.addFiveStarsAction()
				.setVersionNameAsAppSubTitle()
				.addShareAction(R.string.app_name)
				.addUpdateAction()
				.setActionsColumnsCount(2)
				.addFeedbackAction("altieriantonio.dev@gmail.com")
				.addRemoveAdsAction((Intent) null)
				.addDonateAction((Intent) null)
				.setWrapScrollView(true)
				.setShowAsCard(true);
		layout.addView(builder.build());
	}
}
