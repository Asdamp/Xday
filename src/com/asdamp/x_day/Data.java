
package com.asdamp.x_day;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import org.joda.time.*;
/* this class isn't an activity. this class represent a Date*/
public class Data
{

    public Data(int year, int month, int day, int hour, int minute, boolean years, boolean months, 
    		boolean weeks, boolean days, boolean hours,  boolean minutes, boolean seconds, String s, long msI, 
            Context context)
    {
        anno = year;
        mese = month;
        giorno = day;
        ora = hour;
        minuto = minute;
        tipo = creaPeriodType(years, months, weeks,days, hours,  minutes, seconds);
        descrizione = s;
        millisecondiIniziali = msI;
        c = context;
    }

    public Data(int i, int j, int k, int l, int i1, boolean flag, boolean flag1, 
            boolean flag2, boolean flag3, boolean flag4, boolean flag5,boolean seconds, String s, Context context)
    {
        millisecondiIniziali =(new GregorianCalendar()).getTimeInMillis();
        anno = i;
        mese = j;
        giorno = k;
        ora = l;
        minuto = i1;
        tipo = creaPeriodType(flag, flag1, flag2, flag3, flag4, flag5, seconds);
        descrizione = s;
        c = context;
    }

    private PeriodType creaPeriodType(boolean flag, boolean flag1, boolean flag2, boolean flag3, boolean flag4, boolean flag5, boolean seconds)
    {
        ArrayList<DurationFieldType> arraylist = new ArrayList<DurationFieldType>();
        if(flag)
            arraylist.add(DurationFieldType.years());
        if(flag1)
            arraylist.add(DurationFieldType.months());
        if(flag2)
            arraylist.add(DurationFieldType.weeks());
        if(flag3)
            arraylist.add(DurationFieldType.days());
        if(flag4)
            arraylist.add(DurationFieldType.hours());
        if(flag5)
            arraylist.add(DurationFieldType.minutes());
        if(seconds)
            arraylist.add(DurationFieldType.seconds());
        DurationFieldType adurationfieldtype[] = new DurationFieldType[arraylist.size()];
        int i = 0;
        Iterator<DurationFieldType> iterator = arraylist.iterator();
        do
        {
            if(!iterator.hasNext())
                return PeriodType.forFields(adurationfieldtype);
            adurationfieldtype[i] = (DurationFieldType)iterator.next();
            i++;
        } while(true);
    }

    public static Data leggi(Cursor cursor, Context context)
    {
        int anno = cursor.getInt(cursor.getColumnIndex("anno"));
        int mese = cursor.getInt(cursor.getColumnIndex("mese"));
        int giorno = cursor.getInt(cursor.getColumnIndex("giorno"));
        int ora = cursor.getInt(cursor.getColumnIndex("ora"));
        int minuto = cursor.getInt(cursor.getColumnIndex("minuto"));

        long l1 = cursor.getLong(cursor.getColumnIndex("millisecondiIniziali"));
        String s = cursor.getString(cursor.getColumnIndex("descrizione"));
        boolean ore;
        boolean minuti;
        boolean anni;
        boolean mesi;
        boolean giorni;
        boolean settimane;
        boolean secondi;
        if(cursor.getInt(cursor.getColumnIndex("ore")) > 0)
            ore = true;
        else
            ore = false;
        if(cursor.getInt(cursor.getColumnIndex("secondi")) > 0)
            secondi = true;
        else
            secondi = false;
        if(cursor.getInt(cursor.getColumnIndex("minuti")) > 0)
            minuti = true;
        else
            minuti = false;
        if(cursor.getInt(cursor.getColumnIndex("anni")) > 0)
            anni = true;
        else
            anni = false;
        if(cursor.getInt(cursor.getColumnIndex("mesi")) > 0)
            mesi = true;
        else
            mesi = false;
        if(cursor.getInt(cursor.getColumnIndex("giorni")) > 0)
            giorni = true;
        else
            giorni = false;
        if(cursor.getInt(cursor.getColumnIndex("settimane")) > 0)
            settimane = true;
        else
            settimane = false;
        return new Data(anno, mese, giorno, ora, minuto, anni, mesi, settimane, giorni, ore, minuti,secondi, s, l1, context);
    }

    public String aggiorna()
    {
        boolean flag = false;
        Resources resources = c.getResources();
        GregorianCalendar gregoriancalendar = new GregorianCalendar();
        GregorianCalendar gregoriancalendar1 = new GregorianCalendar(anno, mese, giorno, ora, minuto);
        Instant instant = new Instant(gregoriancalendar.getTimeInMillis());
        Instant instant1 = new Instant(gregoriancalendar1.getTimeInMillis());
        Period period;
        int anno;
        String s;
        int j;
        String s1;
        int k;
        String s2;
        int l;
        String s3;
        int i1;
        int sec;
        String secondiT;
        String s4;
        int j1;
        String s5;
        String s6;
        double d;
        if(instant.isAfter(instant1))
        {
            PeriodType periodtype1 = tipo;
            period = new Period(instant1, instant, periodtype1);
            flag = true;
        } else
        {
            PeriodType periodtype = tipo;
            period = new Period(instant, instant1, periodtype);
        }
        anno = period.getYears();
        if(anno == 0)
        {
            s = "";
        } else
        {
            s = resources.getQuantityString(R.plurals.Anni, anno, anno);
        }
        j = period.getMonths();
        if(j == 0)
        {
            s1 = "";
        } else
        {
           
            s1 = resources.getQuantityString(R.plurals.Mesi, j, j);
        }
        k = period.getWeeks();
        if(k == 0)
        {
            s2 = "";
        } else
        {
            s2 = resources.getQuantityString(R.plurals.Settimane, k, k);
        }
        l = period.getDays();
        if(l == 0)
        {
            s3 = "";
        } else
        {
            
            s3 = resources.getQuantityString(R.plurals.Giorni, l, l);
        }
        
        i1 = period.getHours();
        if(i1 == 0)
        {
            s4 = "";
        } else
        {
            
            s4 = resources.getQuantityString(R.plurals.Ore, i1, i1);
        }
        j1 = period.getMinutes();
        if(j1 == 0)
        {
            s5 = "";
        } else
        {
           
            s5 = resources.getQuantityString(R.plurals.Minuti, j1, j1);
        }
        sec = period.getSeconds();
        if(sec == 0)
        {
            secondiT = "";
        } else
        {
            
            secondiT = resources.getQuantityString(R.plurals.Secondi, sec, sec);
        }
        s6 = (new StringBuilder(String.valueOf(s))).append(s1).append(s2).append(s3).append(s4).append(s5).append(secondiT).toString();
        if(s6.equalsIgnoreCase(""))
        	if(tipo.isSupported(DurationFieldType.seconds()))
            {
                s6 = resources.getQuantityString(R.plurals.Secondi, 0, 0);
            } else
            if(tipo.isSupported(DurationFieldType.minutes()))
            {
                s6 = resources.getQuantityString(R.plurals.Minuti, 0, 0);
            } else
            if(tipo.isSupported(DurationFieldType.hours()))
            {
                s6 = resources.getQuantityString(R.plurals.Ore, 0, 0);
            } else
            if(tipo.isSupported(DurationFieldType.days()))
            {
                s6 = resources.getQuantityString(R.plurals.Giorni, 0, 0);
            } else
            if(tipo.isSupported(DurationFieldType.weeks()))
            {
                s6 = resources.getQuantityString(R.plurals.Settimane, 0, 0);
            } else
            if(tipo.isSupported(DurationFieldType.months()))
            {
                s6 = resources.getQuantityString(R.plurals.Mesi, 0, 0);
            } else
            {
                s6 = resources.getQuantityString(R.plurals.Giorni, 0, 0);
            }
        if(!flag)
            s6 = (new StringBuilder(" -")).append(s6).toString();
        d = (double)(gregoriancalendar.getTimeInMillis() - millisecondiIniziali) / (double)(gregoriancalendar1.getTimeInMillis() - millisecondiIniziali);
        if(d >= 1.0D || d < 0.0D)
            percentuale = 1000;
        else
            percentuale = (int)Math.floor(1000D * d);
        return s6;
    }

    public int getAnno()
    {
        return anno;
    }

    public boolean getBoolAnni()
    {
        return tipo.isSupported(DurationFieldType.years());
    }

    public boolean getBoolGiorni()
    {
        return tipo.isSupported(DurationFieldType.days());
    }

    public boolean getBoolMesi()
    {
        return tipo.isSupported(DurationFieldType.months());
    }

    public boolean getBoolMinuti()
    {
        return tipo.isSupported(DurationFieldType.minutes());
    }

    public boolean getBoolOre()
    {
        return tipo.isSupported(DurationFieldType.hours());
    }

    public boolean getBoolSettimana()
    {
        return tipo.isSupported(DurationFieldType.weeks());
    }

    public String getDescrizione()
    {
        return descrizione;
    }

    public int getGiorno()
    {
        return giorno;
    }

    public int getMese()
    {
        return mese;
    }

    public long getMillisecondiIniziali()
    {
        return millisecondiIniziali;
    }

    public int getMinuto()
    {
        return minuto;
    }

    public int getOra()
    {
        return ora;
    }

    public int getPercentuale()
    {
        return percentuale;
    }

    public PeriodType getTipo()
    {
        return tipo;
    }

    public void modifica(int i, int j, int k, int l, int i1, boolean flag, boolean flag1, 
            boolean flag2, boolean flag3, boolean flag4, boolean flag5, boolean seconds, String s)
    {
        ora = l;
        minuto = i1;
        anno = i;
        mese = j;
        giorno = k;
        descrizione = s;
        tipo = creaPeriodType(flag, flag1, flag2, flag3, flag4, flag5, seconds);
    }

    public void setAnno(int i)
    {
        anno = i;
    }

    public void setGiorno(int i)
    {
        giorno = i;
    }

    public void setMese(int i)
    {
        mese = i;
    }

    public void setMillisecondiIniziali(long l)
    {
        millisecondiIniziali = l;
    }

    public void setMinuto(int i)
    {
        minuto = i;
    }

    public void setOra(int i)
    {
        ora = i;
    }

    public void setText(String s)
    {
        descrizione = s;
    }

    public void setTipo(PeriodType periodtype)
    {
        tipo = periodtype;
    }

    private int anno;
    private Context c;
    private String descrizione;
    private int giorno;
    private int mese;
    private long millisecondiIniziali;
    private int minuto;
    private int ora;
    private int percentuale;
    PeriodType tipo;
}
