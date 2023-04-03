package com.asdamp.utility;



import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;

import android.widget.DatePicker;

public class DatePickerFragment extends DialogFragment
implements android.app.DatePickerDialog.OnDateSetListener {
//interfaccia necessaria
	public interface DatePickerListener{
		void onDateSet(int y, int m, int d);
	}
	
	//variabili di stato
	private DatePickerListener chiamante;
	private int anno;
	private int mese;
	private int giorno;
	
	public DatePickerFragment(){
	}

public static DatePickerFragment newInstance(int y,int m,int d,DatePickerListener l) {
	DatePickerFragment p=new DatePickerFragment();
	p.init(y,m,d,l);
	return p;
}
public Dialog onCreateDialog(Bundle savedInstanceState) {
	// Create a new instance of TimePickerDialog and return it
	return new DatePickerDialog(getActivity(), this, anno,mese, giorno);
}
private void init(int y, int m, int d, DatePickerListener l) {
	anno=y;
	mese=m;
	giorno=d;
	chiamante=l;
	
}
public void onDateSet(DatePicker view, int year, int month, int day) {
	chiamante.onDateSet(year,month,day);

}

}