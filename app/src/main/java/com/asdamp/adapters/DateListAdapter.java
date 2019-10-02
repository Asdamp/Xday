package com.asdamp.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;
import androidx.palette.graphics.Palette;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.asdamp.exception.DateNotFoundException;
import com.asdamp.utility.UserInfoUtility;
import com.asdamp.x_day.Costanti;
import com.asdamp.x_day.Data;
import com.asdamp.x_day.GlideApp;
import com.asdamp.x_day.R;
import com.jaychang.st.SimpleText;
import com.omega_r.libs.omegarecyclerview.OmegaRecyclerView;
import com.omega_r.libs.omegarecyclerview.sticky_decoration.StickyAdapter;
import com.pixplicity.easyprefs.library.Prefs;

import org.threeten.bp.Month;
import org.threeten.bp.format.TextStyle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static org.apache.commons.lang3.StringUtils.capitalize;

public class DateListAdapter extends RecyclerView.Adapter<DateListAdapter.ViewHolder>  implements StickyAdapter<DateListAdapter.HeaderHolder>

{
    private ArrayList<Data> date;
    private OnListItemClickListener callback;

    @Override
    public long getStickyId(int position) {
        if(Prefs.getString("sortby","time").equalsIgnoreCase("time")){
            if(position>0)
                return Long.parseLong(date.get(position).getMonth()+""+date.get(position).getYear());
            return Long.parseLong(date.get(0).getMonth()+""+date.get(0).getYear());
        }
        else if(Prefs.getString("sortby","time").equalsIgnoreCase("alphabetical")){
            if(position>0)
                if(date.get(position).getDescrizioneIfExists()!=null && !date.get(position).getDescrizioneIfExists().equals(""))
                    return date.get(position).getDescrizioneIfExists().charAt(0);
                else return '#';
            else{
                    if(date.get(0).getDescrizioneIfExists()!=null && !date.get(0).getDescrizioneIfExists().equals(""))
                        return date.get(0).getDescrizioneIfExists().charAt(0);
                    else return '#';

            }
        }
        else if(Prefs.getString("sortby","time").equalsIgnoreCase("color")){
            if(position>0)
                return date.get(position).getColor();
            return date.get(0).getColor();
        }

        return '#';
    }

    @Override
    public HeaderHolder onCreateStickyViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sticky_header_test, parent, false);
        return new HeaderHolder(view);
    }

    @Override
    public void onBindStickyViewHolder(HeaderHolder viewHolder, int position) {
        Context context=viewHolder.itemView.getContext();

        if(Prefs.getString("sortby","time").equalsIgnoreCase("time")){
            String monthName = Month.of(date.get(position).getMonth()+1).getDisplayName(TextStyle.FULL_STANDALONE, context.getResources().getConfiguration().locale) + " " + date.get(position).getYear();
            monthName=capitalize(monthName);
            viewHolder.text.setText(monthName);
            viewHolder.text.setVisibility(View.VISIBLE);

            viewHolder.color.setVisibility(View.GONE);
        }
        else if(Prefs.getString("sortby","time").equalsIgnoreCase("alphabetical")){
            viewHolder.text.setText(""+(char) getStickyId(position));
            viewHolder.color.setVisibility(View.GONE);
            viewHolder.text.setVisibility(View.VISIBLE);


        }
        else if(Prefs.getString("sortby","time").equalsIgnoreCase("color")){
            viewHolder.color.setVisibility(View.VISIBLE);
            viewHolder.text.setVisibility(View.GONE);

            viewHolder.color.setImageDrawable(new ColorDrawable((int) getStickyId(position)));

        }



    }

    public interface OnListItemClickListener{
        void onListItemClick(View v,int i);
        boolean onListItemLongClick(View v,int i);
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        @BindView(R.id.data)
        TextView mDate;
        @BindView(R.id.mancante)
        TextView mLeft;
        @BindView(R.id.descrizionePersonale)
        TextView mDescription;
        @BindView(R.id.iv_date_image)
        ImageView mImage;


        ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
            //root=v;
        }
    }
    public DateListAdapter(ArrayList<Data> arraylist)
    {
        date = arraylist;
    }

    public void setOnListItemClickListener(OnListItemClickListener callback){
        this.callback=callback;
    }
    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public DateListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                         int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.principale, parent, false);
        return new ViewHolder(v);
    }
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        Context context=holder.itemView.getContext();
        Data data = date.get(i);
        CardView cardView= (CardView) holder.itemView;
        cardView.setCardBackgroundColor(data.getColor());

        Palette p = null;
        holder.mImage.setImageDrawable(null);
        if(data.getImage()!=null)
            GlideApp.with(context).load(data.getImage()).centerCrop().into(holder.mImage);
      /*  else
            holder.mImage.setVisibility(View.INVISIBLE
            );*/

        holder.mDate.setText(date.get(i).toString());
        try{
            String lefttext=data.aggiorna(context);

            holder.mLeft.setText(UserInfoUtility.makeSpannable(lefttext, "\\d+"));
        }
        catch (ArithmeticException e){
            holder.mLeft.setText(context.getResources().getQuantityString(R.plurals.Secondi, Integer.MAX_VALUE)+"+");
        }
        String s = data.getDescrizioneIfExists();
        if(s.equalsIgnoreCase(""))
        {
            holder.mDescription.setVisibility(View.GONE);
    } else
        {
            holder.mDescription.setVisibility(View.VISIBLE);
            holder.mDescription.setText(s);
        }
        if(callback!=null) {
            holder.itemView.setOnClickListener(v -> callback.onListItemClick(holder.itemView,i));
            holder.itemView.setOnLongClickListener(v -> callback.onListItemLongClick(holder.itemView,i));

        }

    }
    public synchronized void sortList(Comparator<Data> comparator, boolean reverse){
        Collections.sort(date, comparator);
        if(reverse)
            Collections.reverse(date);

    }

    @Override
    public int getItemCount() {
        return date.size();
    }


    static class HeaderHolder extends OmegaRecyclerView.ViewHolder {
        public TextView text;
        public CircleImageView color;

        public HeaderHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.list_view_section_header);
            color= itemView.findViewById(R.id.list_view_section_color);
        }
    }
}
