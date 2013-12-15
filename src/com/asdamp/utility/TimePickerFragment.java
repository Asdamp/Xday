package com.asdamp.utility;


import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

public class TimePickerFragment extends DialogFragment implements
		android.app.TimePickerDialog.OnTimeSetListener {

	public interface TimePickerListener {
		public void setTime(int ora,int minuti);

	}

	private int ora;

	private int minuti;

	private TimePickerListener listener;

	public TimePickerFragment() {
	}
	
	/*@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			chiamante = (TimePickerListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement TimePickerListener");
		}
	}*/
	public static TimePickerFragment newInstance(int ora, int minuti, TimePickerListener s){
		TimePickerFragment tpf=new TimePickerFragment();
		tpf.initialize(ora,minuti,s);
		return tpf;
	}
	private void initialize(int ora2, int minuti2, TimePickerListener s) {
		ora=ora2;
		minuti=minuti2;
		listener=s;
	}

	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Create a new instance of TimePickerDialog and return it
		return new TimePickerDialog(getActivity(), this, ora, minuti,
				DateFormat.is24HourFormat(getActivity()));
	}

	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		listener.setTime(hourOfDay, minute);
		
	}
}