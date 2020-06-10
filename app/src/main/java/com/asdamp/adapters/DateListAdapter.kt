package com.asdamp.adapters

import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.asdamp.adapters.DateListAdapter.HeaderHolder
import com.asdamp.utility.UserInfoUtility
import com.asdamp.x_day.Data
import com.asdamp.x_day.GlideApp
import com.asdamp.x_day.R
import com.omega_r.libs.omegarecyclerview.OmegaRecyclerView
import com.omega_r.libs.omegarecyclerview.sticky_decoration.StickyAdapter
import com.pixplicity.easyprefs.library.Prefs
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.principale.view.*
import kotlinx.android.synthetic.main.sticky_header_test.view.*
import org.apache.commons.lang3.StringUtils
import org.threeten.bp.Month
import org.threeten.bp.format.TextStyle
import java.util.*

class DateListAdapter(private val date: MutableList<Data>) : RecyclerView.Adapter<DateListAdapter.ViewHolder>(), StickyAdapter<HeaderHolder> {
    private var callback: OnListItemClickListener? = null
    override fun getStickyId(position: Int): Long {
        val sortMode = Prefs.getString("sortby", "time")
        when {
            sortMode.equals("time", ignoreCase = true) -> {
                return if (position > 0) (date[position].month.toString() + "" + date[position].year).toLong() else (date[0].month.toString() + "" + date[0].year).toLong()
            }
            sortMode.equals("alphabetical", ignoreCase = true) -> {
                return if (position > 0)
                    if (date[position].descrizioneIfExists != null && date[position].descrizioneIfExists != "")
                        date[position].descrizioneIfExists[0].toLong()
                    else '#'.toLong()
                else {
                    if (date[0].descrizioneIfExists != null && date[0].descrizioneIfExists != "")
                        date[0].descrizioneIfExists[0].toLong()
                    else '#'.toLong()
                }
            }
            sortMode.equals("color", ignoreCase = true) -> {
                return if (position > 0) date[position].color.toLong() else date[0].color.toLong()
            }
            else -> return '#'.toLong()
        }
    }

    override fun onCreateStickyViewHolder(parent: ViewGroup): HeaderHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sticky_header_test, parent, false)
        return HeaderHolder(view)
    }

    override fun onBindStickyViewHolder(viewHolder: HeaderHolder, position: Int) {
        val context = viewHolder.itemView.context
        when {
            Prefs.getString("sortby", "time").equals("time", ignoreCase = true) -> {
                val currLocale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    context.resources.configuration.locales[0]
                } else {
                    context.resources.configuration.locale
                }
                var monthName: String? = Month.of(date[position].month + 1).getDisplayName(TextStyle.FULL_STANDALONE, currLocale) + " " + date[position].year
                monthName = StringUtils.capitalize(monthName)
                viewHolder.text.text = monthName
                viewHolder.text.visibility = View.VISIBLE
                viewHolder.color.visibility = View.GONE
            }
            Prefs.getString("sortby", "time").equals("alphabetical", ignoreCase = true) -> {
                viewHolder.text.text = getStickyId(position).toChar().toString()
                viewHolder.color.visibility = View.GONE
                viewHolder.text.visibility = View.VISIBLE
            }
            Prefs.getString("sortby", "time").equals("color", ignoreCase = true) -> {
                viewHolder.color.visibility = View.VISIBLE
                viewHolder.text.visibility = View.GONE
                viewHolder.color.setImageDrawable(ColorDrawable(getStickyId(position).toInt()))
            }
        }
    }

    interface OnListItemClickListener {
        fun onListItemClick(v: View, i: Int)
        fun onListItemLongClick(v: View, i: Int): Boolean
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        // each data item is just a string in this case
        var mDate: TextView = v.data
        var mLeft: TextView = v.mancante
        var mDescription: TextView = v.descrizionePersonale
        var mImage: ImageView = v.iv_date_image

    }

    fun setOnListItemClickListener(callback: OnListItemClickListener?) {
        this.callback = callback
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ViewHolder {
        // create a new view
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.principale, parent, false)
        return ViewHolder(v)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, i: Int) {
        val context = holder.itemView.context
        val data = date[i]
        val cardView = holder.itemView as CardView
        cardView.setCardBackgroundColor(data.color)
        val p: Palette? = null
        holder.mImage.setImageDrawable(null)
        if (data.image != null) GlideApp.with(context).load(data.image).centerCrop().into(holder.mImage)
        /*  else
            holder.mImage.setVisibility(View.INVISIBLE
            );*/holder.mDate.text = date[i].toString()
        try {
            val lefttext = data.aggiorna(context)
            holder.mLeft.text = UserInfoUtility.makeSpannable(lefttext, "\\d+")
        } catch (e: ArithmeticException) {
            holder.mLeft.text = context.resources.getQuantityString(R.plurals.Secondi, Int.MAX_VALUE) + "+"
        }
        val s = data.descrizioneIfExists
        if (s.equals("", ignoreCase = true)) {
            holder.mDescription.visibility = View.GONE
        } else {
            holder.mDescription.visibility = View.VISIBLE
            holder.mDescription.text = s
        }
        if (callback != null) {
            holder.itemView.setOnClickListener { v: View? -> callback?.onListItemClick(holder.itemView, i) }
            holder.itemView.setOnLongClickListener { v: View? -> callback?.onListItemLongClick(holder.itemView, i) ?: false }
        }
    }

    @Synchronized
    fun sortList(comparator: (Data, Data) -> Int, reverse: Boolean) {
        Collections.sort(date, comparator)
        if (reverse) date.reverse()
    }

    override fun getItemCount(): Int {
        return date.size
    }

    class HeaderHolder(itemView: View) : OmegaRecyclerView.ViewHolder(itemView) {
        var text: TextView = itemView.list_view_section_header
        var color: CircleImageView = itemView.list_view_section_color

    }

}