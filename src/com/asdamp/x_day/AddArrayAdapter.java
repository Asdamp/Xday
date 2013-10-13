package com.asdamp.x_day;




import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.asdamp.x_day.R;

public class AddArrayAdapter extends ArrayAdapter<Object> {
	private final Context context;
	private Bundle bundle[];
	public static final String TITLE="titolo";
	public static final String SUBTITLE="subtitle";
	public static final String IMAGE="immagine";
	public static final String CHECK_BOX = "checkbox";
	public static final String CHECKED = "checked";
	public static final String INACTIVE = "inactive";
	public static final int NOTIFICATION_POSITION = 5;

	@Override
	public boolean isEnabled(int position) {
		return !bundle[position].getBoolean(INACTIVE, false);
	}



	public AddArrayAdapter(Context context, Bundle b[]) {
		super(context, R.layout.rawlayout, b);
		this.context = context;
		bundle=b;
	}

	public void setInactive(int position, boolean inactive){
		bundle[position].putBoolean(INACTIVE, inactive);
		this.notifyDataSetChanged();
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.rawlayout, parent, false);
		TextView titolo = (TextView) rowView.findViewById(R.id.titolo);
		TextView descrizione = (TextView) rowView.findViewById(R.id.descrizione);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
		
		Resources res=context.getResources();
		if(bundle[position].getBoolean(CHECK_BOX, false)){
			CheckBox checkBox = (CheckBox) rowView.findViewById(R.id.AddCheckBox);
			checkBox.setVisibility(View.VISIBLE);
			boolean checked=bundle[position].getBoolean(CHECKED, false);
			Log.d("checked",""+checked);
			checkBox.setChecked(checked);//if necessary, set the checkbox checked
		}
		if(bundle[position].getBoolean(INACTIVE, false)){
			ImageView inaIm = (ImageView) rowView.findViewById(R.id.inactiveImage);
			inaIm.setVisibility(View.VISIBLE);
		}
		titolo.setText(bundle[position].getString(TITLE));
		descrizione.setText(bundle[position].getString(SUBTITLE));
		imageView.setImageDrawable(res.getDrawable(bundle[position].getInt(IMAGE)));

		return rowView;
	}
} 