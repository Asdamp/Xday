package com.asdamp.utility;



import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

public class DatePickerFragment extends DialogFragment
implements android.app.DatePickerDialog.OnDateSetListener {
//interfaccia necessaria
	public interface DatePickerListener{
		public void setYear(int y);
		public void setMonth(int m);
		public void setDay(int d);
	}
	
	//variabili di stato
	private DatePickerListener chiamante;
	public static final String YEAR="year";
	public static final String MONTH="month";
	public static final String DAY="day";
	
	public DatePickerFragment(){
	}
@Override
	public void onAttach(Activity activity) {
	 super.onAttach(activity);   
     try {
         chiamante = (DatePickerListener) activity;
     } catch (ClassCastException e) {
         throw new ClassCastException(activity.toString()
                 + " must implement DataPickerListener");
     }
	}
public Dialog onCreateDialog(Bundle savedInstanceState) {
	Bundle b=this.getArguments();
	int year =b.getInt(YEAR);
	int month = b.getInt(MONTH);
	int day = b.getInt(DAY);
	return new DatePickerDialog(getActivity(), this, year, month, day);
}

public void onDateSet(DatePicker view, int year, int month, int day) {
	chiamante.setYear(year);
	chiamante.setMonth(month);
	chiamante.setDay(day);
}

}