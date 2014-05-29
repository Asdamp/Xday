package com.asdamp.x_day;

import android.content.Context;

import android.view.*;
import android.widget.*;

import com.asdamp.exception.DateNotFoundException;
import java.util.ArrayList;

public class ArrayAdapterPrincipale extends ArrayAdapter<Data>
{

    public ArrayAdapterPrincipale(Context context1, ArrayList<Data> arraylist)
    {
        super(context1, R.layout.rawlayout, arraylist);
        riordina = false;
        context = context1;
        date = arraylist;
    }

    public void ModRiordina(boolean flag)
    {
        riordina = flag;
    }

    public View getView(int i, View view, ViewGroup viewgroup)
    {
    	
        View view1 = ((LayoutInflater)context.getSystemService("layout_inflater")).inflate(R.layout.principale, viewgroup, false);
        TextView textview = (TextView)view1.findViewById(R.id.data);
        TextView textview1 = (TextView)view1.findViewById(R.id.mancante);
        TextView textview2 = (TextView)view1.findViewById(R.id.descrizionePersonale);
        ProgressBar progressbar = (ProgressBar)view1.findViewById(R.id.progressi);
        if(date.size() != 0)
        {
            Data data = (Data)date.get(i);
            try {
				textview2.setTextColor(Costanti.getDB().cercaColore(data.getMillisecondiIniziali()));
			} catch (DateNotFoundException e1) {
				textview2.setTextColor(-16746590);
				e1.printStackTrace();
			}

            textview.setText(" "+date.get(i).toString());
            try{
            	textview1.setText(" "+data.aggiorna(context));
            }
            catch (ArithmeticException e){
            	textview1.setText(context.getResources().getQuantityString(R.plurals.Secondi, Integer.MAX_VALUE)+"+");
            }
            String s = data.getDescrizioneIfExists();
            ImageView imageview;
            if(s.equalsIgnoreCase(""))
            {
                textview2.setVisibility(View.GONE);
            } else
            {
                textview2.setVisibility(View.VISIBLE);
                textview2.setText(s);
            }
            if(data.getPercentuale() == 1000)
            {
                ((TextView)view1.findViewById(R.id.mancanoopassato)).setText(context.getText(R.string.Passato));
                ((TextView)view1.findViewById(R.id.alladata)).setText(context.getText(R.string.DallaData));
                progressbar.setVisibility(View.GONE);
            } else
            {
                progressbar.setProgress(data.getPercentuale());
                progressbar.setVisibility(View.VISIBLE);
            }
            imageview = (ImageView)view1.findViewById(R.id.drag_image);
            if(riordina)
                imageview.setVisibility(View.VISIBLE);
            else
                imageview.setVisibility(View.GONE);
        }
        return view1;
    }

    private final Context context;
    private ArrayList<Data> date;
    private boolean riordina;
	
}
