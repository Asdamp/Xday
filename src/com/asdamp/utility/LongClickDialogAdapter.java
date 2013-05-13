package com.asdamp.utility;



import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import android.widget.TextView;
import com.asdamp.x_day.R;

public class LongClickDialogAdapter extends ArrayAdapter<String> {
	private final Context context;
	private final String[] values; 
	public LongClickDialogAdapter(Context context, String[] values) {
		super(context, R.layout.rawlayout, values);
		this.context = context;
		this.values =values;
	}

	
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.solo_testo, parent, false);
		TextView descrizione = (TextView) rowView.findViewById(R.id.descrizioneSoloTesto);
		descrizione.setText(values[position]);
		return rowView;
	}
} 