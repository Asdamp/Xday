package com.asdamp.x_day;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.asdamp.database.DBHelper;
import com.asdamp.utility.LongClickDialog;
import com.asdamp.widget.XdayWidgetProvider;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;

public class MainActivity extends SherlockFragmentActivity implements
		com.asdamp.utility.LongClickDialog.LongClickDialogListener {

	public MainActivity() {
	}

	public void onCreate(Bundle bundle) {
		// popup window. request a review on play store after the 4th opening of
		// the application
		SharedPreferences sharedpreferences = getSharedPreferences(
				"PrivateOption", 0);
		final android.content.SharedPreferences.Editor spe = sharedpreferences
				.edit();
		int i = sharedpreferences.getInt("Utilizzi", 0);
		spe.putInt("Utilizzi", i + 1);
		Resources r = this.getResources();

		if (i == 4 && r.getBoolean(R.bool.ad)) {
			android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(
					this);
			builder.setMessage(getText(R.string.VotamiCorpo))
					.setTitle(getText(R.string.VotamiTitolo))
					.setIcon(R.drawable.ic_launcher);
			builder.setPositiveButton(getText(R.string.RecensisciSubito),
					new DialogInterface.OnClickListener() {
/*this onclick method open the play store for the app review.
 * if the play store doesn't exist, open the amazon appshop
 * if neither play store nor appshop are installed, the method open the xday play store web page*/
						public void onClick(DialogInterface dialog, int which) {
							Uri ur;
							try {
								ur = Uri.parse("market://details?id=com.asdamp.x_day");
								startActivity(new Intent(
										"android.intent.action.VIEW", ur));
							} catch (ActivityNotFoundException activitynotfoundexception)

							{
								try {
									ur = Uri.parse("amzn://apps/android?p=com.asdamp.x_day");
									startActivity(new Intent(
											"android.intent.action.VIEW", ur));
								} catch (ActivityNotFoundException acnf) {
									startActivity(new Intent(
											Intent.ACTION_VIEW,
											Uri.parse("http://play.google.com/store/apps/details?id="
													+ "com.asdamp.x_day")));
								}
							}

						}

					});
			builder.setNegativeButton(getText(R.string.RicordaMai),
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {

						}

					});
			builder.setNeutralButton(getText(R.string.RicordaTardi),
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							spe.putInt("Utilizzi", 0);

						}
					});
			builder.create().show();
		}
		spe.commit();
		super.onCreate(bundle);
		setContentView(R.layout.main_activity);
		lv = (DragSortListView) findViewById(R.id.listaMainActivity);
		date = new ArrayList<Data>();
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> adapterview, View view,
					int i, long l) {
				onListItemClick(i);
			}
		});
		// set-up listview
		lv.setLongClickable(true);
		DragSortController dragsortcontroller = new DragSortController(lv);
		dragsortcontroller.setDragHandleId(R.id.drag_image);
		dragsortcontroller.setRemoveEnabled(true);
		dragsortcontroller.setRemoveMode(DragSortController.FLING_REMOVE);
		lv.setFloatViewManager(dragsortcontroller);
		lv.setOnTouchListener(dragsortcontroller);
		lv.setDropListener(new DragSortListView.DropListener() {

			public void drop(int i, int j) {
				Data data = (Data) MainActivity.date.get(i);
				Costanti.getDB().spostamento(i + 1, j + 1,
						data.getMillisecondiIniziali());
				vista.remove(data);
				vista.insert(data, j);
			}
		});

		lv.setRemoveListener(new DragSortListView.RemoveListener() {

			public void remove(int which) {
				rimuoviData(which);

			}
		});
		lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> adapterview,
					View view, int i, long l) {
				showLongClickDialog(view, i);
				return false;
			}
		});
		vista = new ArrayAdapterPrincipale(this, date);
		lv.setAdapter(vista);

	}

	private void aggiorna() {
		vista.notifyDataSetChanged();
	}

	public static Data getData(int i) {
		return (Data) date.get(i);
	}

	// Get all the date in db
	private int leggiDati() {
		date.clear();
		Cursor cursor = Costanti.getDB().fetchAllData();

		do {
			if (!cursor.moveToNext()) {
				/*
				 * TextView tv=(TextView) this.findViewById(R.id.nessunaData);
				 * int c=cursor.getCount(); if(c>0)tv.setVisibility(View.GONE);
				 * else tv.setVisibility(View.VISIBLE);
				 */
				return cursor.getCount();
			}
			try {
				date.add(Data.leggi(cursor, this));
			} catch (IllegalArgumentException illegalargumentexception) {
				Log.e("lettura", illegalargumentexception.getMessage(),
						illegalargumentexception);
			}
		} while (true);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	// delete date
	private void rimuoviData(int i) {
		Costanti.getDB().deleteData((Data) date.get(i));
		Costanti.getDB();
		vista.remove((Data) date.get(i));
		dataChiamata = -1;
		if (Costanti.getOsVersion() >= 11) {
			AppWidgetManager appwidgetmanager = AppWidgetManager
					.getInstance(this);
			appwidgetmanager.notifyAppWidgetViewDataChanged(appwidgetmanager
					.getAppWidgetIds(new ComponentName(this,
							XdayWidgetProvider.class)), R.id.list_view_widget);
		}
	}

	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		switch (requestCode) {
		case Costanti.FILE_SELECT_CODE:
			if (resultCode == RESULT_OK) {
				// Get the Uri of the selected file
				Uri uri = intent.getData();

				String path = uri.getPath();
				Log.d("percorso file selezionato", "File Path: " + path);

				File dbfi = this.getDatabasePath(DBHelper.DATABASE_NAME);
				File desti = new File(path);
				Pattern pattern = Pattern.compile("([^\\s]+(\\.(?i)(xdy))$)");
				Matcher m = pattern.matcher(desti.getAbsolutePath());
				if (m.find()) {
					FileChannel src = null;
					FileChannel dst = null;
					try {
						dst = new FileOutputStream(dbfi).getChannel();
						src = new FileInputStream(desti).getChannel();
						dst.transferFrom(src, 0, src.size());
						src.close();
						dst.close();
						Toast.makeText(this,
								this.getString(R.string.ImportSucceded),
								Toast.LENGTH_SHORT).show();

					} catch (FileNotFoundException e) {
						Toast.makeText(
								this,
								this.getString(R.string.ImportError,
										desti.getAbsolutePath()),
								Toast.LENGTH_LONG).show();
						e.printStackTrace();
					} catch (IOException e) {
						Toast.makeText(
								this,
								this.getString(R.string.ImportError,
										desti.getAbsolutePath()),
								Toast.LENGTH_LONG).show();
						e.printStackTrace();
					}
					leggiDati();
					aggiorna();
				} else
					Toast.makeText(this, this.getString(R.string.WrongFile),
							Toast.LENGTH_LONG).show();
			}
		}

	}

	public boolean onCreateOptionsMenu(Menu menu1) {
		getSupportMenuInflater().inflate(R.menu.activity_main, menu1);
		menu = menu1;
		return true;
	}

	protected void onListItemClick(int i) {
		dataChiamata = i;
		Intent intent = new Intent("com.asdamp.x_day.ADD");
		intent.putExtra("requestCode", 1);
		intent.putExtra("posizioneData", i);
		startActivityForResult(intent, 1);
	}

	public void onLongClickDialogClick(int i) {
		if (i == 0)
			rimuoviData(dataChiamata);
		else if (i == 1)
			onListItemClick(dataChiamata);
		else
			Toast.makeText(this, "I'm an Error", Toast.LENGTH_SHORT).show();
	}

	public void onLongClickDialogNegativeClick(DialogFragment dialogfragment) {
	}

	public boolean onOptionsItemSelected(MenuItem menuitem) {
		switch (menuitem.getItemId()) {

		case R.id.menu_settings:
			dataChiamata = -1;
			Intent intent = new Intent("com.asdamp.x_day.ADD");
			intent.putExtra("requestCode", 4);
			startActivityForResult(intent, 4);
			break;
		case R.id.Aggiorna:
			aggiorna();
			break;
		case R.id.aboutMain:
			startActivity(new Intent(this, About.class));
			break;
		case R.id.Riordina:
			menuitem.setVisible(false);
			lv.setDragEnabled(true);
			menu.findItem(R.id.Fine_riordinamento).setVisible(true);
			vista.ModRiordina(true);
			vista.notifyDataSetChanged();
			break;
		case R.id.Fine_riordinamento:
			menuitem.setVisible(false);
			lv.setDragEnabled(false);
			menu.findItem(R.id.Riordina).setVisible(true);
			vista.ModRiordina(false);
			vista.notifyDataSetChanged();
			break;
		case R.id.Esporta:
			File dbf = this.getDatabasePath(DBHelper.DATABASE_NAME);
			File dest = new File(Environment.getExternalStorageDirectory(),
					"Xdaydb.xdy");

			if (dbf.exists()) {
				FileChannel src = null;
				FileChannel dst = null;
				try {
					src = new FileInputStream(dbf).getChannel();
					dst = new FileOutputStream(dest).getChannel();
					dst.transferFrom(src, 0, src.size());
					src.close();
					dst.close();
					Toast.makeText(
							this,
							this.getString(R.string.ExportSucceded,
									dest.getAbsolutePath()), Toast.LENGTH_LONG)
							.show();

				} catch (FileNotFoundException e) {
					Toast.makeText(this, this.getText(R.string.ExportError),
							Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				} catch (IOException e) {
					Toast.makeText(this, this.getText(R.string.ExportError),
							Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}

			}

			break;
		case R.id.Importa: {
			this.showFileChooser();

			break;
		}
		default:
			return false;
		}
		return true;
	}

	protected void onResume() {
		super.onResume();
		// with this broadcast receiver the app refresh itself every minute
		broadcastReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().compareTo(
						"android.intent.action.TIME_TICK") == 0)
					aggiorna();

			}
		};
		registerReceiver(broadcastReceiver, new IntentFilter(
				"android.intent.action.TIME_TICK"));
		leggiDati();

		aggiorna();
	}

	protected void onStop() {
		try {
			unregisterReceiver(broadcastReceiver);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		super.onStop();
	}

	protected void showLongClickDialog(View view, int i) {
		Bundle bundle = new Bundle();
		dataChiamata = i;
		String as[] = new String[2];
		as[0] = getText(R.string.Elimina).toString();
		as[1] = getText(R.string.Modifica).toString();
		LongClickDialog longclickdialog = new LongClickDialog();
		bundle.putCharSequenceArray("parametriString", as);
		bundle.putString("titolo", getText(R.string.Opzioni).toString());
		longclickdialog.setArguments(bundle);
		longclickdialog.show(getSupportFragmentManager(),
				getText(R.string.Parametri).toString());
	}

	private void showFileChooser() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("file/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);

		try {
			startActivityForResult(
					Intent.createChooser(intent,
							this.getString(R.string.importFile)),
					Costanti.FILE_SELECT_CODE);
		} catch (android.content.ActivityNotFoundException ex) {
			// Potentially direct the user to the Market with a Dialog
			Toast.makeText(this, "Please install a File Manager.",
					Toast.LENGTH_SHORT).show();
		}
	}

	private static ArrayList<Data> date;
	private BroadcastReceiver broadcastReceiver;
	private int dataChiamata;
	private DragSortListView lv;
	private Menu menu;
	private ArrayAdapterPrincipale vista;

}
