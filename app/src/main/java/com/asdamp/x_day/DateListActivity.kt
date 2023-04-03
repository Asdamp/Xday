package com.asdamp.x_day

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.Parcelable
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.ButterKnife
import com.asdamp.adapters.DateListAdapter
import com.asdamp.adapters.DateListAdapter.OnListItemClickListener
import com.asdamp.utility.ImageUtils
import com.asdamp.utility.UserInfoUtility
import com.google.android.gms.ads.AdView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.analytics.FirebaseAnalytics
import com.pixplicity.easyprefs.library.Prefs
import com.pixplicity.generate.OnFeedbackListener
import com.pixplicity.generate.Rate
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.OnMenuItemClickListener
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import kotlinx.android.synthetic.main.app_bar_date_list.*
import kotlinx.android.synthetic.main.content_date_list.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class DateListActivity : AppCompatActivity() {
    var rate: Rate? = null

    private var dates: MutableList<Data> = mutableListOf()
    private var timer: Timer? = null
    var mListAdapter: DateListAdapter = DateListAdapter(dates)
    private var mFirebaseAnalytics =  FirebaseAnalytics.getInstance(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_date_list)
        ButterKnife.bind(this)
        val isFirstRun = Prefs.getBoolean("isFirstRun", true)
        if (isFirstRun) {
            //show sign up activity
            startActivity(Intent(this, IntroActivity::class.java))
        }
        rateUs()
        rate!!.count()
        Prefs.putBoolean("isFirstRun", false)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, null)
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        val mAdView = findViewById<AdView>(R.id.adView)
        UserInfoUtility.loadAd(mAdView)
        fab.setOnClickListener { view: View? -> addNewDate() }
        rv_date_list_empty_view.setOnClickListener { view: View? -> addNewDate() }
        dates = ArrayList()
        rv_date_list.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        rv_date_list.layoutManager = layoutManager
        mListAdapter = DateListAdapter(dates)
        rv_date_list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) fab.hide() else if (dy < 0) fab.show()
            }
        })
        mListAdapter.setOnListItemClickListener(object : OnListItemClickListener {
            override fun onListItemClick(v: View, i: Int) {
                this@DateListActivity.onListItemClick(v, i)
            }

            override fun onListItemLongClick(v: View, i: Int): Boolean {
                val powerMenu = PowerMenu.Builder(this@DateListActivity)
                        .addItem(PowerMenuItem(getString(R.string.delete), R.drawable.ic_delete_black_24dp))
                        .addItem(PowerMenuItem(getString(R.string.share), R.drawable.ic_share_black_24dp))
                        .setAnimation(MenuAnimation.SHOWUP_TOP_RIGHT) // Animation start point (TOP | LEFT)
                        .setMenuRadius(10f)
                        .setMenuShadow(10f)
                        .setSelectedEffect(true)
                        .setSelectedTextColor(Color.BLUE)
                        .setMenuColor(Color.WHITE)
                        .build()
                powerMenu.onMenuItemClickListener = OnMenuItemClickListener { position: Int, item: PowerMenuItem? ->
                    powerMenu.dismiss()
                    when (position) {
                        0 -> rimuoviData(i)
                        1 -> ImageUtils.shareView(this@DateListActivity, v)
                    }
                }
                powerMenu.showAsAnchorLeftTop(v)
                return true
            }
        })
        rv_date_list.adapter = mListAdapter
    }

    private fun rateUs() {
        rate = Rate.Builder(this) // Trigger dialog after this many events (optional, defaults to 6)
                .setTriggerCount(5) // After dismissal, trigger again after this many events (optional, defaults to 30)
                .setRepeatCount(5)
                .setMinimumInstallTime(TimeUnit.DAYS.toMillis(3).toInt()) // Optional, defaults to 7 days
                .setFeedbackAction(object : OnFeedbackListener {
                    // Optional
                    override fun onFeedbackTapped() {
                        val i = Intent(Intent.ACTION_SEND)
                        i.type = "message/rfc822"
                        i.putExtra(Intent.EXTRA_EMAIL, arrayOf("altieriantonio.dev@gmail.com"))
                        i.putExtra(Intent.EXTRA_SUBJECT, "Xday feedback")
                        try {
                            startActivity(Intent.createChooser(i, "Send mail..."))
                        } catch (ex: ActivityNotFoundException) {
                            Toast.makeText(this@DateListActivity, "There are no email clients installed.", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onRateTapped() {
                        //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.play_store_link))));
                    }

                    override fun onRequestDismissed(dontAskAgain: Boolean) {
                        // Prefs.putBoolean("dismissed_forever",dontAskAgain);
                    }
                })
                .setSnackBarParent(coordinator)
                .setLightTheme(true) // Default is dark
                .setSwipeToDismissVisible(true) // Add this when using the Snackbar
                // without a CoordinatorLayout as a parent.
                .build()
        // rate.showRequest();
    }

    private fun addNewDate() {
        val intent = Intent(this, Add::class.java)
        intent.putExtra("requestCode", Costanti.CREA_DATA)
        startActivityForResult(intent, Costanti.CREA_DATA)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId
        when (item.itemId) {
            R.id.time -> {
                if (Prefs.getString("sortby", "time").equals("time", ignoreCase = true)) {
                    val currReverse = Prefs.getBoolean("reverse", false)
                    Prefs.putBoolean("reverse", !currReverse)
                } else {
                    Prefs.putString("sortby", "time")
                    Prefs.putBoolean("reverse", false)
                }
                sortList()
            }
            R.id.alphabetical -> {
                if (Prefs.getString("sortby", "time").equals("alphabetical", ignoreCase = true)) {
                    val currReverse = Prefs.getBoolean("reverse", false)
                    Prefs.putBoolean("reverse", !currReverse)
                } else {
                    Prefs.putString("sortby", "alphabetical")
                    Prefs.putBoolean("reverse", false)
                }
                sortList()
            }
            R.id.color -> {
                if (Prefs.getString("sortby", "time").equals("color", ignoreCase = true)) {
                    val currReverse = Prefs.getBoolean("reverse", false)
                    Prefs.putBoolean("reverse", !currReverse)
                } else {
                    Prefs.putString("sortby", "color")
                    Prefs.putBoolean("reverse", false)
                }
                sortList()
            }
            R.id.aboutMain -> startActivity(Intent(this, About::class.java))
            else -> return false
        }
        return true
    }

    private fun sortList() {
        if (Prefs.getString("sortby", "time").equals("time", ignoreCase = true)) {
            mListAdapter.sortList({ obj: Data, rhs: Data -> obj.compare(rhs) }, Prefs.getBoolean("reverse", false))
        } else if (Prefs.getString("sortby", "time").equals("alphabetical", ignoreCase = true)) {
            mListAdapter.sortList({ o1: Data, o2: Data -> o1.descrizione.compareTo(o2.descrizione) }, Prefs.getBoolean("reverse", false))
        } else if (Prefs.getString("sortby", "time").equals("color", ignoreCase = true)) {
            mListAdapter.sortList({ o1: Data, o2: Data -> Integer.compare(o1.color, o2.color) }, Prefs.getBoolean("reverse", false))
        }
    }

    @Synchronized
    private fun aggiorna() {
        runOnUiThread {
            rv_date_list.adapter!!.notifyDataSetChanged()
        }
    }

    // Get all the date in db
    private fun leggiDati(): Int {
        dates.clear()
        val cursor = Costanti.getDB().fetchAllData()
        while (cursor.moveToNext()) {
            try {
                dates.add(Data.leggi(cursor))
            } catch (illegalargumentexception: IllegalArgumentException) {
                Log.e("lettura", illegalargumentexception.message,
                        illegalargumentexception)
            }
        }
        if (dates.isEmpty()) {
            rv_date_list.visibility = View.GONE
            rv_date_list_empty_view.visibility = View.VISIBLE
        }
        return cursor.count
    }

    private fun rimuoviData(i: Int) {
        Costanti.getDB().deleteData(dates[i])
        dates.removeAt(i)
        rv_date_list.adapter!!.notifyItemRemoved(i)
        (this.application as MainApplication).aggiornaWidget()
        if (dates.isEmpty()) {
            rv_date_list.visibility = View.GONE
            rv_date_list_empty_view.visibility = View.VISIBLE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int,
                                  intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        val data: Data
        val dismissedForever = Prefs.getBoolean("dismissed_forever", false)
        //    rate.test();
        if (!dismissedForever) {
            rate!!.showRequest()
        }
        when (requestCode) {
            Costanti.MODIFICA_DATA -> {
                data = intent!!.getParcelableExtra("data")!!
                val index = dates.indexOf(data)
                when (resultCode) {
                    Costanti.TUTTO_BENE -> {
                        dates.removeAt(index)
                        dates.add(index, data)
                        sortList()
                    }
                    Costanti.CANCELLA_DATA -> rimuoviData(index)
                }
            }
            Costanti.CREA_DATA -> {
                data = intent!!.getParcelableExtra("data")!!
                when (resultCode) {
                    Costanti.TUTTO_BENE -> {
                        dates.add(data)
                        sortList()
                        mFirebaseAnalytics.logEvent("NEW_DATE", null)
                        if (!dates.isEmpty()) {
                            rv_date_list.visibility = View.VISIBLE
                            rv_date_list_empty_view.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu1: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_main, menu1)
        // activate autoreorder?
        return true
    }

    private fun onListItemClick(v: View, i: Int) {
        pauseAutoUpdate()
        val intent = Intent(this, Add::class.java)
        intent.putExtra("requestCode", 1)
        intent.putExtra("data", getDate(i) as Parcelable)
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, v.findViewById(R.id.iv_date_image), "date_image")
        startActivityForResult(intent, 1, options.toBundle())
    }

    @Synchronized
    private fun getDate(i: Int): Data {
        return dates[i]
    }

    override fun onResume() {
        super.onResume()
        resumeAutoUpdate()
        leggiDati()
        sortList()
        aggiorna()

    }

      private fun pauseAutoUpdate() {
            timer?.cancel()
            timer?.purge()
            timer = null

    }
    private fun resumeAutoUpdate() {
            timer = Timer()
            timer?.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                   aggiorna()
                }
            }, 0, 1000)

    }
}