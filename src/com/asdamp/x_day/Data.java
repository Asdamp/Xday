package com.asdamp.x_day;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.text.format.DateFormat;
import org.joda.time.*;
import com.asdamp.utility.UtilityDate;

/* this class isn't an activity. this class represent a Date*/
public class Data implements Comparator<Data> {

	public Data(int year, int month, int day, int hour, int minute,
			boolean years, boolean months, boolean weeks, boolean days,
			boolean hours, boolean minutes, boolean seconds, String s,
			long msI, int color, boolean notification,Context context) {
		anno = year;
		mese = month;
		giorno = day;
		ora = hour;
		minuto = minute;
		this.color=color;
		this.notifica=notification;
		tipo = creaPeriodType(years, months, weeks, days, hours, minutes,
				seconds);
		descrizione = s;
		millisecondiIniziali = msI;
		c = context;
		GregorianCalendar gregoriancalendar1 = new GregorianCalendar(anno,
				mese, giorno, ora, minuto);
		msFinali = gregoriancalendar1.getTimeInMillis();

	}

	public Data(int i, int j, int k, int l, int i1, boolean flag,
			boolean flag1, boolean flag2, boolean flag3, boolean flag4,
			boolean flag5, boolean seconds, String s, Context context) {
		millisecondiIniziali = (new GregorianCalendar()).getTimeInMillis();
		anno = i;
		mese = j;
		giorno = k;
		ora = l;
		minuto = i1;
		tipo = creaPeriodType(flag, flag1, flag2, flag3, flag4, flag5, seconds);
		descrizione = s;
		c = context;
		GregorianCalendar gregoriancalendar1 = new GregorianCalendar(anno,
				mese, giorno, ora, minuto);
		msFinali = gregoriancalendar1.getTimeInMillis();

	}

	private PeriodType creaPeriodType(boolean flag, boolean flag1,
			boolean flag2, boolean flag3, boolean flag4, boolean flag5,
			boolean seconds) {
		ArrayList<DurationFieldType> arraylist = new ArrayList<DurationFieldType>();
		if (flag)
			arraylist.add(DurationFieldType.years());
		if (flag1)
			arraylist.add(DurationFieldType.months());
		if (flag2)
			arraylist.add(DurationFieldType.weeks());
		if (flag3)
			arraylist.add(DurationFieldType.days());
		if (flag4)
			arraylist.add(DurationFieldType.hours());
		if (flag5)
			arraylist.add(DurationFieldType.minutes());
		if (seconds)
			arraylist.add(DurationFieldType.seconds());
		DurationFieldType[] adurationfieldtype = arraylist.toArray(new DurationFieldType[arraylist.size()]);/*.iterator();
		while(iterator.hasNext()){
			adurationfieldtype[i] = (DurationFieldType) iterator.next();
			i++;
		}*/
		if(adurationfieldtype==null || adurationfieldtype.length==0){
			adurationfieldtype=new DurationFieldType[1];
			adurationfieldtype[0]=DurationFieldType.days();
		}
		return PeriodType.forFields(adurationfieldtype);
			
	}

	public static Data leggi(Cursor cursor, Context context) {
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
		if (cursor.getInt(cursor.getColumnIndex("ore")) > 0)
			ore = true;
		else
			ore = false;
		if (cursor.getInt(cursor.getColumnIndex("secondi")) > 0)
			secondi = true;
		else
			secondi = false;
		if (cursor.getInt(cursor.getColumnIndex("minuti")) > 0)
			minuti = true;
		else
			minuti = false;
		if (cursor.getInt(cursor.getColumnIndex("anni")) > 0)
			anni = true;
		else
			anni = false;
		if (cursor.getInt(cursor.getColumnIndex("mesi")) > 0)
			mesi = true;
		else
			mesi = false;
		if (cursor.getInt(cursor.getColumnIndex("giorni")) > 0)
			giorni = true;
		else
			giorni = false;
		if (cursor.getInt(cursor.getColumnIndex("settimane")) > 0)
			settimane = true;
		else
			settimane = false;
		int colore=cursor.getInt(cursor.getColumnIndex("colore"));
		boolean notifica;
		 if(cursor.getInt(cursor.getColumnIndex("notifica")) > 0)
	            notifica = true;
		 else notifica=false;
		return new Data(anno, mese, giorno, ora, minuto, anni, mesi, settimane,
				giorni, ore, minuti, secondi, s, l1, colore, notifica, context);
	}

	public String aggiorna() {
		Resources resources = c.getResources();
		GregorianCalendar gregoriancalendar = new GregorianCalendar();
		GregorianCalendar gregoriancalendar1 = new GregorianCalendar(anno,
				mese, giorno, ora, minuto);
		Instant instant = new Instant(gregoriancalendar.getTimeInMillis());
		Instant InstFinale = new Instant(gregoriancalendar1.getTimeInMillis());
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
		String stringaTotale="";
		double d;
		PeriodType periodtype = tipo;

		try{
			if (instant.isAfter(InstFinale)) {
			period = new Period(InstFinale, instant, periodtype);
		} else {
			period = new Period(instant, InstFinale, periodtype);
		}
		}
		catch(ArithmeticException e){
			return  c.getString(R.string.overflow);
		}
		anno = period.getYears();
		if (anno == 0) {
			stringaTotale = stringaTotale+"";
		} else {
			stringaTotale = stringaTotale+ " "+resources.getQuantityString(R.plurals.Anni, anno, anno);
		}
		j = period.getMonths();
		if (j == 0) {
			stringaTotale = stringaTotale+"";
		} else {
			String tmp;
			if(stringaTotale.equalsIgnoreCase("")) tmp="";
			else tmp=", ";
			s1 = tmp+resources.getQuantityString(R.plurals.Mesi, j, j);
		}
		k = period.getWeeks();
		if (k == 0) {
			stringaTotale = stringaTotale+"";
		} else {
			String tmp;
			if(stringaTotale.equalsIgnoreCase("")) tmp="";
			else tmp=", ";
			stringaTotale = stringaTotale+tmp+resources.getQuantityString(R.plurals.Settimane, k, k);
		}
		l = period.getDays();
		if (l == 0) {
			stringaTotale = stringaTotale+"";
		} else {
			String tmp;
			if(stringaTotale.equalsIgnoreCase("")) tmp="";
			else tmp=", ";
			stringaTotale = stringaTotale+tmp+resources.getQuantityString(R.plurals.Giorni, l, l);
		}

		i1 = period.getHours();
		if (i1 == 0) {
			stringaTotale = stringaTotale+"";
		} else {
			String tmp;
			if(stringaTotale.equalsIgnoreCase("")) tmp="";
			else tmp=", ";
			stringaTotale = stringaTotale+tmp+resources.getQuantityString(R.plurals.Ore, i1, i1);
		}
		j1 = period.getMinutes();
		if (j1 == 0) {
			stringaTotale = stringaTotale+"";
		} else {
			String tmp;
			if(stringaTotale.equalsIgnoreCase("")) tmp="";
			else tmp=", ";
			stringaTotale = stringaTotale+ tmp+resources.getQuantityString(R.plurals.Minuti, j1, j1);
		}
		sec = period.getSeconds();
		if (sec == 0) {
			stringaTotale = stringaTotale+"";
		} else {
			String tmp;
			if(stringaTotale.equalsIgnoreCase("")) tmp="";
			else tmp=", ";
			stringaTotale = stringaTotale+ tmp+resources.getQuantityString(R.plurals.Secondi, sec, sec);
		}
	
		if (stringaTotale.equalsIgnoreCase(""))
			if (tipo.isSupported(DurationFieldType.seconds())) {
				stringaTotale = resources.getQuantityString(R.plurals.Secondi, 0, 0);
			} else if (tipo.isSupported(DurationFieldType.minutes())) {
				stringaTotale = resources.getQuantityString(R.plurals.Minuti, 0, 0);
			} else if (tipo.isSupported(DurationFieldType.hours())) {
				stringaTotale = resources.getQuantityString(R.plurals.Ore, 0, 0);
			} else if (tipo.isSupported(DurationFieldType.days())) {
				stringaTotale = resources.getQuantityString(R.plurals.Giorni, 0, 0);
			} else if (tipo.isSupported(DurationFieldType.weeks())) {
				stringaTotale = resources.getQuantityString(R.plurals.Settimane, 0, 0);
			} else if (tipo.isSupported(DurationFieldType.months())) {
				stringaTotale = resources.getQuantityString(R.plurals.Mesi, 0, 0);
			} else {
				stringaTotale = resources.getQuantityString(R.plurals.Giorni, 0, 0);
			}
		
		d = (double) (gregoriancalendar.getTimeInMillis() - millisecondiIniziali)
				/ (double) (gregoriancalendar1.getTimeInMillis() - millisecondiIniziali);
		if (d >= 1.0D || d < 0.0D)
			percentuale = 1000;
		else
			percentuale = (int) Math.floor(1000D * d);
		return stringaTotale;
	}

	public int getAnno() {
		return anno;
	}

	public boolean getBoolAnni() {
		return tipo.isSupported(DurationFieldType.years());
	}

	public boolean getBoolGiorni() {
		return tipo.isSupported(DurationFieldType.days());
	}

	public boolean getBoolMesi() {
		return tipo.isSupported(DurationFieldType.months());
	}

	public boolean getBoolMinuti() {
		return tipo.isSupported(DurationFieldType.minutes());
	}
	public boolean getBoolSettimane() {
		return tipo.isSupported(DurationFieldType.weeks());	}
	public boolean getBoolSecondi() {
		return tipo.isSupported(DurationFieldType.seconds());	}
	public boolean getBoolOre() {
		return tipo.isSupported(DurationFieldType.hours());
	}

	public boolean getBoolSettimana() {
		return tipo.isSupported(DurationFieldType.weeks());
	}

	public String getDescrizione() {
		if(!descrizione.equals("")) return descrizione;
		else return this.toString();
	}
	public String getDescrizioneIfExists() {
		return descrizione;
	}

	public String toString()
    {
		Date d = UtilityDate.creaData(anno, mese, giorno, minuto, ora);
		String dataStr = UtilityDate.convertiDataInStringaBasandosiSuConfigurazione(d,
				Costanti.dt);
		dataStr=dataStr+" - "+UtilityDate.convertiDataInStringaBasandosiSuConfigurazione(d,
				DateFormat.getTimeFormat(c));
		return dataStr;
		}

	public int getGiorno() {
		return giorno;
	}

	public int getMese() {
		return mese;
	}

	public long getMillisecondiIniziali() {
		return millisecondiIniziali;
	}

	public int getMinuto() {
		return minuto;
	}
	public int getColor() {
		return color;
	}

	public boolean isNotificationEnabled() {
		return notifica;
	}

	public int getOra() {
		return ora;
	}

	public int getPercentuale() {
		return percentuale;
	}

	public PeriodType getTipo() {
		return tipo;
	}

	public void modifica(int i, int j, int k, int l, int i1, boolean flag,
			boolean flag1, boolean flag2, boolean flag3, boolean flag4,
			boolean flag5, boolean seconds, String s) {
		ora = l;
		minuto = i1;
		anno = i;
		mese = j;
		giorno = k;
		descrizione = s;
		tipo = creaPeriodType(flag, flag1, flag2, flag3, flag4, flag5, seconds);
	}

	public void setAnno(int i) {
		anno = i;
	}

	public void setGiorno(int i) {
		giorno = i;
	}

	public void setMese(int i) {
		mese = i;
	}

	public void setMillisecondiIniziali(long l) {
		millisecondiIniziali = l;
	}

	public void setMinuto(int i) {
		minuto = i;
	}

	public void setOra(int i) {
		ora = i;
	}

	public void setText(String s) {
		descrizione = s;
	}

	public void setTipo(PeriodType periodtype) {
		tipo = periodtype;
	}

	public int compare(Data lhs, Data rhs) {
		if (lhs.msFinali < rhs.msFinali)
			return 1;
		else if (lhs.msFinali > rhs.msFinali)
			return -1;
		return 0;
	}
	/**
	 * @param d
	 * @return
	 */
	public String getShareText() {
		String shareText;
		if(getPercentuale()==1000)
			shareText=MessageFormat.format(c.getString(R.string.passato_all_evento), aggiorna(),
					getDescrizione());
		else shareText=MessageFormat.format(c.getString(R.string.mancano_all_evento), aggiorna(),
				getDescrizione());
		shareText=shareText+"\n[Xday: https://play.google.com/store/apps/details?id=com.asdamp.x_day]";
		return shareText;
	}
	
	public boolean isAfterToday(){
		Calendar cal=new GregorianCalendar(anno, mese, giorno,ora,minuto);
		Calendar today=new GregorianCalendar();

		long msFin =cal.getTimeInMillis();
		if(msFin>today.getTimeInMillis()) return true;
		return false;
	}
	private int anno;
	private Context c;
	private String descrizione;
	private int giorno;
	private int mese;
	private long millisecondiIniziali;
	public long msFinali;
	private int minuto;
	private int ora;
	private boolean notifica;
	private int color;
	private int percentuale;
	PeriodType tipo;
	
	
	
}
