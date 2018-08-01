package com.asdamp.utility;




import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.EditText;

public class TextEditDialog extends DialogFragment {
	
	

	
	private TextEditDialogInterface c;
	private String titolo;
	private String messaggio;
	private String testoPrecedente;
	public final static String TITOLO="titolo";
	public final static String SOTTOTITOLO="sottotitolo";
	public final static String STRINGA_BASE="strBase";
	
	public interface TextEditDialogInterface {
		public void OnTextEditDialogPositiveClick(String t);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            c = (TextEditDialogInterface) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement TextEditDialogInterface");
        }
	}
	public TextEditDialog(){
	
	}
	
	public Dialog onCreateDialog(Bundle s){
	Bundle b=this.getArguments();
	titolo=b.getString(TITOLO);
	messaggio=b.getString(SOTTOTITOLO);
	testoPrecedente=b.getString(STRINGA_BASE);
	AlertDialog.Builder alert = new AlertDialog.Builder((Context) c);
	
	alert.setTitle(titolo);
	
	//se esiste un messaggio di descrizione, allora lo inserisce
	if(!(messaggio.equals(""))) alert.setMessage(messaggio);
	final EditText input = new EditText((Context) c);
	
	input.setText(testoPrecedente);
	input.setSelection(input.getText().length());
	
	alert.setView(input);
	alert.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
	public void onClick(DialogInterface dialog, int whichButton) {
	  String testoFinale = input.getText().toString();
	  c.OnTextEditDialogPositiveClick(testoFinale);
	  
	  }
	
	});

	alert.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
	  public void onClick(DialogInterface dialog, int whichButton) {
	    
	    
	  }
	});

	return alert.create();
	

}}
