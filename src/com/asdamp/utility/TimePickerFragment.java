package com.asdamp.utility;


import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

public class TimePickerFragment extends DialogFragment implements
		android.app.TimePickerDialog.OnTimeSetListener {

	public interface TimePickerListener {
		public void setMinute(int m);

		public void setHour(int h);

	}

	public static final String HOUR = "hour";

	public static final String MINUTE = "minute";

	private TimePickerListener chiamante;

	public TimePickerFragment() {
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			chiamante = (TimePickerListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement TimePickerListener");
		}
	}

	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current time as the default values for the picker
		Bundle b=this.getArguments();
		int hour = b.getInt(HOUR);
		int minute = b.getInt(MINUTE);

		// Create a new instance of TimePickerDialog and return it
		return new TimePickerDialog(getActivity(), this, hour, minute,
				DateFormat.is24HourFormat(getActivity()));
	}

	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		chiamante.setHour(hourOfDay);
		chiamante.setMinute(minute);
	}
}