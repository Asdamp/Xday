package com.asdamp.x_day;




import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.asdamp.x_day.R;

public class AddArrayAdapter extends ArrayAdapter<Integer> {
	private final Context context;
	private final static Integer a[]={1,2,3};
	public AddArrayAdapter(Context context) {
		super(context, R.layout.rawlayout, a);
		this.context = context;
	}

	
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.rawlayout, parent, false);
		TextView titolo = (TextView) rowView.findViewById(R.id.titolo);
		TextView descrizione = (TextView) rowView.findViewById(R.id.descrizione);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
		Resources res=context.getResources();
		String str[]={"I'm an error", "I'm an error"};
		switch (position){
		case 0: {
				str=res.getStringArray(R.array.ADDselezionaData);				
				imageView.setImageDrawable(res.getDrawable(R.drawable.ic_action_calendar)); break;}
		case 1:
		{
			str=res.getStringArray(R.array.ADDselezionaOra);
		imageView.setImageDrawable(res.getDrawable(R.drawable.ic_action_clock)); break;}
		case 2: 
		{
		str=res.getStringArray(R.array.ADDselezionaParametri);
		imageView.setImageDrawable(res.getDrawable(R.drawable.ic_action_tick)); break;}
		}
		titolo.setText(str[0]);
		descrizione.setText(str[1]);
		return rowView;
	}
} 