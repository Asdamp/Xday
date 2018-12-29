package com.asdamp.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;
import androidx.palette.graphics.Palette;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
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
import com.asdamp.x_day.Costanti;
import com.asdamp.x_day.Data;
import com.asdamp.x_day.GlideApp;
import com.asdamp.x_day.R;
import com.jaychang.st.SimpleText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DateListAdapter extends RecyclerView.Adapter<DateListAdapter.ViewHolder>
{
    private ArrayList<Data> date;
    private OnListItemClickListener callback;
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

            holder.mLeft.setText(makeSpannable(lefttext, "\\d+"));
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
    private SpannableStringBuilder makeSpannable(String text, String regex) {

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

            spannable.setSpan( new RelativeSizeSpan(2.0f), start, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        sb.setLength(0);
        matcher.appendTail(sb);
        spannable.append(sb.toString());
        return spannable;
    }
    @Override
    public int getItemCount() {
        return date.size();
    }


}
