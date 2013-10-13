package com.asdamp.utility;

import com.asdamp.x_day.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
 
public class LongClickDialog extends DialogFragment{
	private String[] descrizioni;
	private String titolo; 
	private LongClickDialogListener chiamante;
	public static final String PARAMETRI_STRING="parametriString";
	public static final String TITOLO="titolo";
	/*
	 * le descrizioni: descrivono ogni elemento della lista
	 * titolo= il titolo visualizzato dal dialog */
	public interface LongClickDialogListener {
        public void onLongClickDialogClick(int positionClick);
        public void onLongClickDialogNegativeClick(DialogFragment dialog);
    }
	
	public LongClickDialog(){
	
		
	}
	 
	public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            chiamante = (LongClickDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
	public Dialog onCreateDialog(Bundle a) {
		/*estraggoil bundle*/
		Bundle s=this.getArguments();
		descrizioni=(String[]) s.getCharSequenceArray(PARAMETRI_STRING);
		titolo=s.getString(TITOLO);
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LongClickDialogAdapter ad=new LongClickDialogAdapter(getActivity(), descrizioni);
		builder.setAdapter(ad, new DialogInterface.OnClickListener(){

			public void onClick(DialogInterface dialog, int which) {
				chiamante.onLongClickDialogClick(which);
				
			}})
		.setNegativeButton(getString(R.string.Annulla), new DialogInterface.OnClickListener(){

			public void onClick(DialogInterface arg0, int arg1) {
				chiamante.onLongClickDialogNegativeClick(LongClickDialog.this);
				
			}});
		builder.setTitle(titolo);


		
		
		return builder.create();
	}

}
