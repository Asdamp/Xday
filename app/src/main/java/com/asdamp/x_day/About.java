package com.asdamp.x_day;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
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
				.setPhoto(R.drawable.aa_profile)
				.setCover(R.mipmap.profile_cover)
				.setLinksAnimated(true)
				.setDividerDashGap(13)
				.setName("Antonio Altieri")
				.setSubTitle("Mobile Developer")
				.setLinksColumnsCount(4)
				.addGooglePlayStoreLink(getResources().getString(R.string.play_store_link))
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
				.addDonateAction(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						String url = "https://ko-fi.com/asdamp";
						Intent i = new Intent(Intent.ACTION_VIEW);
						i.setData(Uri.parse(url));
						startActivity(i);
					}
				})

				.addLicenseAction(new OnClickListener() {
					@Override
					public void onClick(View v) {
						final FragmentManager fm = About.this.getSupportFragmentManager();
						final FragmentTransaction ft = fm.beginTransaction();
						final Fragment prev = fm.findFragmentByTag("dialog_licenses");
						if (prev != null) {
							ft.remove(prev);
						}
						ft.addToBackStack(null);

						new OpenSourceLicensesDialog().show(ft, "dialog_licenses");
					}
				})
				.setWrapScrollView(true)
				.setShowAsCard(true);
		layout.addView(builder.build());
	}

	public static class OpenSourceLicensesDialog extends DialogFragment {

		public OpenSourceLicensesDialog() {
		}

		@Override
		public Dialog onCreateDialog(@NonNull Bundle savedInstanceState) {
			final WebView webView = new WebView(getActivity());
			webView.loadUrl("file:///android_asset/open_source_licenses.html");

			return new AlertDialog.Builder(getActivity())
					.setTitle("Open Source Licenses")
					.setView(webView)
					.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							}
					)
					.create();
		}
	}
}
