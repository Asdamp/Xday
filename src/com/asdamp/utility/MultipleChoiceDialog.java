package com.asdamp.utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

public class MultipleChoiceDialog extends DialogFragment{
	private boolean[] parametri;
	private String[] descrizioni;
	private String titolo;
	private int obbligatori;
	private MultipleChoiceDialogListener chiamante;
	public static final String PARAMETRI_BOOLEAN="parametriBoolean";
	public static final String PARAMETRI_STRING="parametriString";
	public static final String TITOLO="titolo";
	public static final String OBBLIGATORI="obbligatori";
	/*per costruire un multiple choice dialog bisogna passare 4 cose in un bundle.
	 * i parametri booleani: un array di booleani che definiscono i parametri di defaolt
	 * 						e che verrà poi restituito
	 * le descrizioni: descrivono ogni singolo parametro. sono array paralleli
	 * titolo= il titolo visualizzato dal dialog
	 * obbligatori: intero che definisce il minimo numero degli elementi dell'array parametri booleani
	 * 				deve essere vero. se il numero è strettamente inferiore visualizza un messaggio di errore
	 * */
	public interface MultipleChoiceDialogListener {
        public void onMultipleDialogPositiveClick(boolean[] parametri);
        public void onMultipleDialogNegativeClick(DialogFragment dialog);
    }
	
	public MultipleChoiceDialog(){
	
		
	}
	
	public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            chiamante = (MultipleChoiceDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
	public Dialog onCreateDialog(Bundle a) {
		/*estraggoil bundle*/
		Bundle s=this.getArguments();
		parametri=s.getBooleanArray(PARAMETRI_BOOLEAN);
		descrizioni=(String[]) s.getCharSequenceArray(PARAMETRI_STRING);
		titolo=s.getString(TITOLO);
		obbligatori=s.getInt(OBBLIGATORI, 0);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	
		builder.setMultiChoiceItems(descrizioni, parametri, new DialogInterface.OnMultiChoiceClickListener(){

			public void onClick(DialogInterface dialog, int quale, boolean checkato) {
				parametri[quale]=checkato;
				
			}})
		.setPositiveButton("Conferma", new DialogInterface.OnClickListener(){

			public void onClick(DialogInterface arg0, int arg1) {
				int veri=0;
				for(int i=0;i<parametri.length;i++)	if (parametri[i]) veri++;
				if(veri>=obbligatori) chiamante.onMultipleDialogPositiveClick(parametri);
				else Toast.makeText((Context) chiamante, "devi scegliere almeno "+obbligatori+" parametro", Toast.LENGTH_LONG).show();
				
			}})
		.setNegativeButton("Annulla", new DialogInterface.OnClickListener(){

			public void onClick(DialogInterface arg0, int arg1) {
				chiamante.onMultipleDialogNegativeClick(MultipleChoiceDialog.this);
				
			}});
		builder.setTitle(titolo);


		
		
		return builder.create();
	}

}
