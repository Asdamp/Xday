package com.asdamp.x_day;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.asdamp.database.DBAdapter;
import com.asdamp.database.DBHelper;
import com.asdamp.utility.ShareUtility;
import com.asdamp.utility.StartupUtility;
import com.google.ads.AdView;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class MainActivity extends SherlockFragmentActivity implements
		ActionMode.Callback {

	public MainActivity() {
	}

	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		shprs = StartupUtility.getInstance(this).shprs;
		setContentView(R.layout.main_activity);
		StartupUtility st = StartupUtility.getInstance(this);
		AdView ad = (AdView) this.findViewById(R.id.adView);
		st.showAdMobAds(ad);
		st.toPlayStore(getText(R.string.VotamiTitolo),
				getText(R.string.VotamiCorpo), R.drawable.ic_launcher,
				getText(R.string.RecensisciSubito),
				getText(R.string.RicordaMai), getText(R.string.RicordaTardi),
				4, "com.asdamp.x_day");
		// st.toPlayStore(getString(R.string.prova_smartpizza),
		// getText(R.string.SmartPizzaCorpo),
		// R.drawable.smart_pizza,getText(R.string.VediSubito),getText(R.string.RicordaMai),getText(R.string.RicordaTardi),7,
		// "com.asdamp.smartpizza");
		st.showChangelogIfVersionChanged(getText(R.string.cl),
				getText(R.string.Changelog));
		lv = (DragSortListView) findViewById(R.id.listaMainActivity);
		date = new ArrayList<Data>();
		setListView();

	}

	/**
	 * Initialize the list view of the main activity
	 * 
	 */
	private void setListView() {
		this.registerForContextMenu(lv);
		//TODO list.SetEmptyView()

		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> adapterview, View view,
					int i, long l) {
				onListItemClick(view, i);
			}
		});
		// set-up listview
		// lv.setLongClickable(true);
		DragSortController dragsortcontroller = new DragSortController(lv);
		dragsortcontroller.setDragHandleId(R.id.drag_image);
		dragsortcontroller.setRemoveEnabled(true);
		dragsortcontroller.setRemoveMode(DragSortController.FLING_REMOVE);
		lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		lv.setFloatViewManager(dragsortcontroller);
		lv.setOnTouchListener(dragsortcontroller);
		lv.setLongClickable(true);
		lv.setDropListener(new DragSortListView.DropListener() {

			public void drop(int i, int j) {
				Data data = (Data) getDate(i);
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
				if (mode == null) {
					mode = startActionMode(MainActivity.this);
					pauseAutoUpdate();
				}
				boolean checkState = !(lv.isItemChecked(i));
				lv.setItemChecked(i, checkState);
				return toggleListItem(view, i);
			}
		});
		vista = new ArrayAdapterPrincipale(this, date);
		lv.setAdapter(vista);
	}

	private synchronized void aggiorna() {
		vista.notifyDataSetChanged();
	}

	// Get all the date in db
	private int leggiDati() {
		date.clear();
		Cursor cursor = Costanti.getDB().fetchAllData();

		do {
			if (!cursor.moveToNext()) {
				return cursor.getCount();
			}
			try {
				date.add(Data.leggi(cursor));
			} catch (IllegalArgumentException illegalargumentexception) {
				Log.e("lettura", illegalargumentexception.getMessage(),
						illegalargumentexception);
			}
		} while (true);
	}

	private void rimuoviData(int i) {
		Costanti.getDB().deleteData((Data) date.get(i));
		vista.remove((Data) date.get(i));
		((MainApplication) this.getApplication()).aggiornaWidget();
	}

	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		Data data;
		switch (requestCode) {
		
		case Costanti.FILE_SELECT_CODE:
			if (resultCode == RESULT_OK) {
				DBAdapter db=Costanti.getDB();
				Uri uri=intent.getData();
				try {
					db.importDB(intent.getData()/* Get the Uri of the selected file*/);
				} catch (IOException e) {	
					Crouton.makeText(this,this.getString(R.string.ImportError, uri.toString()),Style.ALERT).show();
				}
				if (db.numRecordsDate() > 0)
					Crouton.makeText(this,this.getString(R.string.ImportSucceded),Style.CONFIRM).show();
				else
					Crouton.makeText(this,R.string.inport_error_file_corrupted, Style.ALERT).show();
				leggiDati();
				aggiorna();

			}
			break;
		case Costanti.MODIFICA_DATA:
			data=intent.getParcelableExtra("data");
            int index=date.indexOf(data);
			switch (resultCode){
			case Costanti.TUTTO_BENE:
                Costanti.getDB().updateData(data);
                date.remove(index);
                date.add(index, data);
                break;
			case Costanti.CANCELLA_DATA:
				this.rimuoviData(index);break;
			}
			break;
		case Costanti.CREA_DATA:
			data=intent.getParcelableExtra("data");
			switch (resultCode){
			case Costanti.TUTTO_BENE:
	            Costanti.getDB().createData(data);
	            date.add(data);
	            break;
			}
			
		}

	}



	public boolean onCreateOptionsMenu(Menu menu1) {
		getSupportMenuInflater().inflate(R.menu.activity_main, menu1);
		// activate autoreorder?
		boolean reorder = shprs.getBoolean(autoreorder, false);
		menu1.findItem(R.id.Temporale).setChecked(reorder);
		mainMenu = menu1;
		return true;
	}

	protected void onListItemClick(View v, int i) {
		if (mode == null) {
			lv.setItemChecked(i, false);
			Intent intent = new Intent(this, Add.class);
			intent.putExtra("requestCode", 1);
			intent.putExtra("data", (Parcelable) getDate(i));
			startActivityForResult(intent, 1);
		} else {
			this.toggleListItem(v, i);
		}
	}

	private synchronized Data getDate(int i) {
		return date.get(i);
	}

	public boolean onOptionsItemSelected(MenuItem menuitem) {
		switch (menuitem.getItemId()) {
		case R.id.Temporale:
			boolean ch = !menuitem.isChecked();
			shprs.edit().putBoolean(autoreorder, ch).commit();
			menuitem.setChecked(ch);
			if (ch && !date.isEmpty()) {
				Collections.sort(date, getDate(0));
				for (int i = 1; i <= date.size(); i++) {
					long msTemp = getDate(i - 1).getMillisecondiIniziali();
					Costanti.getDB().cambiaPosizione(msTemp, i);
				}
				((MainApplication) this.getApplication()).aggiornaWidget();
			}
			mainMenu.findItem(R.id.Manuale).setVisible(!ch);
			break;
		case R.id.menu_settings:
			Intent intent = new Intent(this,Add.class);
			intent.putExtra("requestCode", 4);
			this.pauseAutoUpdate();
			startActivityForResult(intent, 4);
			break;
		case R.id.Aggiorna:
			aggiorna();
			break;
		case R.id.aboutMain:
			startActivity(new Intent(this, About.class));
			break;
		case R.id.Manuale:
			menuitem.setVisible(false);
			this.pauseAutoUpdate();
			lv.setDragEnabled(true);
			mainMenu.findItem(R.id.Fine_riordinamento).setVisible(true);
			vista.ModRiordina(true);
			vista.notifyDataSetChanged();
			break;
		case R.id.Fine_riordinamento:
			menuitem.setVisible(false);
			lv.setDragEnabled(false);
			mainMenu.findItem(R.id.Manuale).setVisible(true);
			vista.ModRiordina(false);
			vista.notifyDataSetChanged();
			((MainApplication) this.getApplication()).aggiornaWidget();
			this.resumeAutoUpdate();
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
					Crouton.makeText(
							this,
							this.getString(R.string.ExportSucceded,
									dest.getAbsolutePath()), Style.INFO).show();

				} catch (FileNotFoundException e) {
					Crouton.makeText(this, this.getText(R.string.ExportError),
							Style.ALERT).show();
					e.printStackTrace();
				} catch (IOException e) {
					Crouton.makeText(this, this.getText(R.string.ExportError),
							Style.ALERT).show();
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
		this.resumeAutoUpdate();
		leggiDati();
		if (lv == null) {
			lv = (DragSortListView) findViewById(R.id.listaMainActivity);

		}
		aggiorna();
		boolean reorder = shprs.getBoolean(autoreorder, false);
		if (reorder && !date.isEmpty())/*
										 * la riga sottostante riordina in
										 * ordine temporale.
										 */
		Collections.sort(date, getDate(0));

	}

	private synchronized void pauseAutoUpdate() {
		if (timer != null) {
			timer.cancel();
			timer.purge();
			timer = null;
		}
	}

	private void resumeAutoUpdate() {
		if (timer == null) {
			timer = new Timer();
			timer.scheduleAtFixedRate(new TimerTask() {
				public void run() {
					mHandler.obtainMessage(1).sendToTarget();
				}
			}, 0, 1000);
		}
	}

	private void showFileChooser() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);

		try {
			startActivityForResult(
					Intent.createChooser(intent,
							this.getString(R.string.importFile)),
					Costanti.FILE_SELECT_CODE);
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(this, "Please install a File Manager.",
					Toast.LENGTH_SHORT).show();
		}
	}

	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		// Inflate a menu resource providing context menu items
		MenuInflater inflater = mode.getMenuInflater();
		this.contextMenu = menu;
		inflater.inflate(R.menu.activity_main_context, menu);
		return true;
	}

	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		return false;
	}

	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

		switch (item.getItemId()) {
		case (R.id.elimina):
			SparseBooleanArray posDaEliminare = lv.getCheckedItemPositions();
			for (int i = lv.getCount() - 1; i >= 0; i--) {

				Log.d("eliminazione", i + " " + posDaEliminare.get(i));
				if (posDaEliminare.get(i)) {
					this.rimuoviData(i);
					lv.setItemChecked(i, false);
					View temp = lv.getChildAt(i);
					if (temp != null)
						lv.getChildAt(i).setBackgroundColor(0);
				}
			}
			vista.notifyDataSetChanged();
			mode.finish();
			this.mode = null;
			break;
		case (R.id.share): {
			int i = firstChecked();
			lv.setItemChecked(i, false);
			Data d = getDate(i);
			String shareText;
			shareText = d.getShareText(this);
			ShareUtility.shareText(this, shareText, d.getDescrizione());
			mode.finish();
			break;
		}
		}
		return true;
	}

	/**
	 * @return
	 */
	private int firstChecked() {
		Log.d("share", "initializing share");
		SparseBooleanArray checkedItem = lv.getCheckedItemPositions();
		int i = 0;
		boolean found = false;
		while (i <= checkedItem.size() - 1 && !found) {
			if (checkedItem.get(i)) {
				found = true;
			} else
				i++;
		}
		return i;
	}

	public void onDestroyActionMode(ActionMode mode) {
		Log.d("Action Mode", "Beginning destroy action mode. listview have "
				+ lv.getCount() + " elements");
		for (int i = 0; i < lv.getCount(); i++) {
			Log.d("Action Mode", "Setting normal color for " + i + ".");
			lv.setItemChecked(i, false);
			View v = lv.getChildAt(i);
			if (v != null)
				lv.getChildAt(i).setBackgroundResource(0);
		}
		this.resumeAutoUpdate();
		this.mode = null;
	}

	/**
	 * @param view
	 * @param i
	 * @return
	 */
	private boolean toggleListItem(View view, int i) {
		SparseBooleanArray sp = lv.getCheckedItemPositions();
		boolean checkState = sp.get(i);
		// lv.isItemChecked(i);
		// toggle checked item
		Log.d("checked", i + "-->" + lv.getCheckedItemPositions().get(i));
		int checkedNum = checkedItemCount();
		if (checkedNum <= 0) {
			if (mode != null)
				mode.finish();
		} else if (checkedNum >= 2)
			contextMenu.findItem(R.id.share).setVisible(false);
		else
			contextMenu.findItem(R.id.share).setVisible(true);
		return checkState;
	}


	private int checkedItemCount() {
		SparseBooleanArray checked = lv.getCheckedItemPositions();
		int num = 0;
		for (int i = 0; i < date.size(); i++) {
			if (checked.get(i))
				num++;
		}
		return num;
	}

	public static final String autoreorder = "AutoReorder";
	private static ArrayList<Data> date;
	private SharedPreferences shprs;
	private DragSortListView lv;
	private Menu mainMenu;
	private Menu contextMenu;
	private ArrayAdapterPrincipale vista;
	private Timer timer;
	private ActionMode mode = null;
	public Handler mHandler = new Handler() {

		synchronized public void handleMessage(Message msg) {
			aggiorna();
		}
	};
}
