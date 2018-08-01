package com.asdamp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
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

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DateListAdapter extends RecyclerView.Adapter<DateListAdapter.ViewHolder>
{
    private ArrayList<Data> date;
    public OnListItemClickListener callback;
    public interface OnListItemClickListener{
        void onListItemClick(int i);
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        @BindView(R.id.data)
        public TextView mDate;
        @BindView(R.id.mancante)
        public TextView mLeft;
        @BindView(R.id.descrizionePersonale)
        public TextView mDescription;
      /*  @BindView(R.id.alladata)
        public TextView mFromOrTo;
        @BindView(R.id.mancanoopassato)
        public TextView mLeftOrPassed;
        @BindView(R.id.progressi)
        public TextRoundCornerProgressBar mProgress;*/

        //public View root;

        public ViewHolder(View v) {
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
    @Override
    public DateListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.principale, parent, false);
        return new ViewHolder(v);
    }
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int i) {
        Context context=holder.itemView.getContext();
        Data data = date.get(i);
        CardView cardView= (CardView) holder.itemView;
        try {
            cardView.setCardBackgroundColor(Costanti.getDB().cercaColore(data.getMillisecondiIniziali()));
        } catch (DateNotFoundException e1) {
            holder.mDescription.setTextColor(context.getResources().getColor(R.color.md_red_500));
        }
        Palette p = null;
        if(i==0){
            GlideApp.with(context).load(R.drawable.test1).centerCrop().into((ImageView) cardView.findViewById(R.id.iv_date_image));
            Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.test1);
            p = Palette.from(icon).generate();

        }
        else if(i==1){
            GlideApp.with(context).load(R.drawable.test2).centerCrop().into((ImageView) cardView.findViewById(R.id.iv_date_image));
            Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.test2);
            p = Palette.from(icon).generate();
        }
        holder.mDate.setText(" "+date.get(i).toString());
        try{
            holder.mLeft.setText(" "+data.aggiorna(context));
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
        if(p!=null && p.getDominantSwatch()!=null)
            holder.mLeft.setTextColor(ColorUtils.setAlphaComponent(p.getDominantSwatch().getBodyTextColor(), 255));
      /*  if(data.getPercentuale() == 1000)
        {
            holder.mLeftOrPassed.setText(context.getText(R.string.Passato));
            holder.mFromOrTo.setText(context.getText(R.string.DallaData));
            holder.mProgress.setVisibility(View.GONE);
        } else
        {
            holder.mProgress.setProgress(1000-data.getPercentuale());
            holder.mProgress.setVisibility(View.VISIBLE);
        }*/
        if(callback!=null)
            holder.itemView.setOnClickListener(v->callback.onListItemClick(i));
        // imageview = (ImageView)view1.findViewById(R.id.drag_image);
            /*if(riordina)
                imageview.setVisibility(View.VISIBLE);
            else
                imageview.setVisibility(View.GONE);*/
    }

    @Override
    public int getItemCount() {
        return date.size();
    }


}
