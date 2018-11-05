
package com.asdamp.utility;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;

public class SingleChoiceDialog extends DialogFragment
{
    public static interface SingleChoiceDialogListener
    {

        public abstract void onSingleDialogNegativeClick(int i);

        public abstract void onSingleDialogPositiveClick(int i);
    }


    public SingleChoiceDialog()
    {
        selezionato = -1;
    }

    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try
        {
            chiamante = (SingleChoiceDialogListener)activity;
            return;
        }
        catch(ClassCastException classcastexception)
        {
            throw new ClassCastException((new StringBuilder(String.valueOf(activity.toString()))).append(" must implement NoticeDialogListener").toString());
        }
    }

    public Dialog onCreateDialog(Bundle bundle)
    {
        Bundle bundle1 = getArguments();
        descrizioni = (String[])bundle1.getCharSequenceArray("parametriString");
        titolo = bundle1.getString("titolo");
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        builder.setSingleChoiceItems(descrizioni, selezionato, new android.content.DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialoginterface, int i)
            {
                selezionato = i;
            }
        }
).setPositiveButton(getString(android.R.string.ok), new android.content.DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialoginterface, int i)
            {
                chiamante.onSingleDialogPositiveClick(selezionato);
            }
        }
).setNegativeButton(getString(android.R.string.cancel), new android.content.DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialoginterface, int i)
            {
                chiamante.onSingleDialogNegativeClick(-1);
            }
        }
);
        builder.setTitle(titolo);
        return builder.create();
    }

    public static final int ABORT_SELECTION = -1;
    public static final String PARAMETRI_STRING = "parametriString";
    public static final String TITOLO = "titolo";
    private SingleChoiceDialogListener chiamante;
    private String descrizioni[];
    private int selezionato;
    private String titolo;



}
