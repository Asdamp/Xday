package com.asdamp.x_day

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asdamp.adapters.DateListAdapter
import com.asdamp.adapters.DateListAdapter.OnListItemClickListener
import com.asdamp.utility.ImageUtils
import com.asdamp.utility.UserInfoUtility
import com.asdamp.x_day.databinding.ActivityDateListBinding
import com.google.android.gms.ads.AdView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.analytics.FirebaseAnalytics
import com.omega_r.libs.omegarecyclerview.OmegaRecyclerView
import com.pixplicity.easyprefs.library.Prefs
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.OnMenuItemClickListener
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import java.util.*
import java.util.concurrent.TimeUnit

class DateListActivity : AppCompatActivity() {
    private var dates: MutableList<Data> = mutableListOf()
    private var timer: Timer? = null
    var mListAdapter: DateListAdapter = DateListAdapter(dates)
    private var mFirebaseAnalytics =  FirebaseAnalytics.getInstance(this)
    private lateinit var binding: ActivityDateListBinding
    private lateinit var emptyListView: ConstraintLayout
    private lateinit var listView: OmegaRecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityDateListBinding.inflate(layoutInflater)
        emptyListView=binding.list.include.rvDateListEmptyView.root
        listView=binding.list.include.rvDateList

        setContentView(binding.root)
        val isFirstRun = Prefs.getBoolean("isFirstRun", true)
        if (isFirstRun) {
            //show sign up activity
            startActivity(Intent(this, IntroActivity::class.java))
        }
        Prefs.putBoolean("isFirstRun", false)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, null)
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        val mAdView = findViewById<AdView>(R.id.adView)
        UserInfoUtility.loadAd(mAdView)
        fab.setOnClickListener { view: View? -> addNewDate() }
        emptyListView.setOnClickListener { view: View? -> addNewDate() }
        dates = ArrayList()
        listView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        listView.layoutManager = layoutManager
        mListAdapter = DateListAdapter(dates)
        listView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
                        .addItem(PowerMenuItem(getString(R.string.delete),false, R.drawable.ic_delete_black_24dp))
                        .addItem(PowerMenuItem(getString(R.string.share),false, R.drawable.ic_share_black_24dp))
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
        listView.adapter = mListAdapter
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
            listView.adapter!!.notifyDataSetChanged()
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
            listView.visibility = View.GONE
            emptyListView.visibility = View.VISIBLE
        }
        return cursor.count
    }

    private fun rimuoviData(i: Int) {
        Costanti.getDB().deleteData(dates[i])
        dates.removeAt(i)
        listView.adapter!!.notifyItemRemoved(i)
        (this.application as MainApplication).aggiornaWidget()
        if (dates.isEmpty()) {
            listView.visibility = View.GONE
            emptyListView.visibility = View.VISIBLE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int,
                                  intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        val data: Data
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
                            listView.visibility = View.VISIBLE
                            emptyListView.visibility = View.GONE
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