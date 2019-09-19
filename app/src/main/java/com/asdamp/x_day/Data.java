package com.asdamp.x_day;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DurationFieldType;
import org.joda.time.Instant;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
/* this class isn't an activity. this class represent a Date*/
public class Data extends GregorianCalendar implements  Parcelable{
	private static final long serialVersionUID = 8260403943016471526L;
	public Data(){
		super();
		tipo=creaPeriodType(true, true, true, true, true, true, true);
		descrizione="";
		notifica=false;
		color=Color.parseColor("#0099CC");
		this.millisecondiIniziali=this.getTimeInMillis();
	}
	public Data(boolean years, boolean months, boolean weeks, boolean days,
			boolean hours, boolean minutes, boolean seconds){
		super();
		tipo=creaPeriodType(years, months, weeks, days, hours, minutes, seconds);
		descrizione="";
		notifica=false;
		color=Color.parseColor("#0099CC");
		this.millisecondiIniziali=this.getTimeInMillis();
	}
	public Data(long ms){
		super();
		this.millisecondiIniziali=this.getTimeInMillis();
		this.setTimeInMillis(ms);
		tipo=creaPeriodType(false,false,false,true,false,false,false);
		descrizione="";
		notifica=false;
		color=Color.parseColor("#0099CC");
	}
	public Data(long ms, String descrizione, int colore, boolean notifica,
			boolean years, boolean months, boolean weeks, boolean days,
			boolean hours, boolean minutes, boolean seconds){
		super();
		this.setTimeInMillis(ms);
		tipo=creaPeriodType(years,months,weeks,days,hours,minutes,seconds);
		this.descrizione=descrizione;
		this.color=colore;
		this.notifica=notifica;
	}
	public Data(int year, int month, int day, int hour, int minute,
			boolean years, boolean months, boolean weeks, boolean days,
			boolean hours, boolean minutes, boolean seconds, String s,
			long msI, int color, boolean notification, Uri image) {
		
		super(year,month,day,hour,minute);
		this.color=color;
		this.notifica=notification;
		tipo = creaPeriodType(years, months, weeks, days, hours, minutes,
				seconds);
		descrizione = s;
		millisecondiIniziali = msI;
		this.image=image;

	}

	private static PeriodType creaPeriodType(boolean flag, boolean flag1,
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
		DurationFieldType[] adurationfieldtype = arraylist.toArray(new DurationFieldType[arraylist.size()]);
		if(adurationfieldtype==null || adurationfieldtype.length==0){
			adurationfieldtype=new DurationFieldType[1];
			adurationfieldtype[0]=DurationFieldType.days();
		}
		return PeriodType.forFields(adurationfieldtype);
			
	}

	public static Data leggi(Cursor cursor) {
		int anno = cursor.getInt(cursor.getColumnIndex("anno"));
		int mese = cursor.getInt(cursor.getColumnIndex("mese"));
		int giorno = cursor.getInt(cursor.getColumnIndex("giorno"));
		int ora = cursor.getInt(cursor.getColumnIndex("ora"));
		int minuto = cursor.getInt(cursor.getColumnIndex("minuto"));
		long l1 = cursor.getLong(cursor.getColumnIndex("millisecondiIniziali"));
		String s = cursor.getString(cursor.getColumnIndex("descrizione"));
		int colore=cursor.getInt(cursor.getColumnIndex("colore"));
		String image=cursor.getString(cursor.getColumnIndex("immagine"));
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
		boolean notifica;
		 if(cursor.getInt(cursor.getColumnIndex("notifica")) > 0)
	            notifica = true;
		 else notifica=false;
		 Uri imageUri;
		 if(image!=null)
		 	imageUri=Uri.parse(image);
		 else
		 	imageUri=null;
		return new Data(anno, mese, giorno, ora, minuto, anni, mesi, settimane,
				giorni, ore, minuti, secondi, s, l1, colore, notifica,imageUri);
	}
	public static int[] timeDistance(Date to,PeriodType pt){
		GregorianCalendar gc=new GregorianCalendar();
		gc.setTime(to);
		return timeDistance(gc,pt);
	}
	public static int[] timeDistance(GregorianCalendar to,PeriodType pt){
		return timeDistance(new GregorianCalendar(), to,pt);
	}
	public static int[] timeDistance(Date from, Date to,PeriodType pt){
		GregorianCalendar gc=new GregorianCalendar();
		gc.setTime(from);
		GregorianCalendar gc2=new GregorianCalendar();
		gc2.setTime(to);
		return timeDistance(gc,gc2,pt);
	}
	public static int[] timeDistance(GregorianCalendar from,GregorianCalendar to,PeriodType pt){
		GregorianCalendar gregoriancalendar =from;
		GregorianCalendar gregoriancalendar1 = to;			
		Instant instant = new Instant(gregoriancalendar.getTimeInMillis());
		Instant InstFinale = new Instant(gregoriancalendar1.getTimeInMillis());
		int[] passed=new int[7];
		Period period;
		
		PeriodType periodtype; 
		if(pt==null){
			periodtype=creaPeriodType(true, true, true, true, true, true, true);
		}
		else periodtype = pt;
        //TODO
		/*try to eliminate the if block and substitute only with 		
		 * 	period = new Period(InstFinale, instant, periodtype);
		 */

		
		if (instant.isAfter(InstFinale)) {
			period = new Period(InstFinale, instant, periodtype);
		} else {
			period = new Period(instant, InstFinale, periodtype);
		}
		
		
		passed[0] = period.getYears();
		passed[1] = period.getMonths();
		passed[2] = period.getWeeks();
		passed[3] = period.getDays();
		passed[4] = period.getHours();
		passed[5] = period.getMinutes();
		passed[6] = period.getSeconds();
		return passed;
	}
	public String aggiorna(Context c) {
		Resources resources = c.getResources();
		String stringaTotale="";
		int years,months,weeks,days,hours,minutes,seconds;
		int[] passed=Data.timeDistance(this,tipo);
		years=passed[0];
		months=passed[1];
		weeks=passed[2];
		days=passed[3];
		hours=passed[4];
		minutes=passed[5];
		seconds=passed[6];
		boolean foundNotZero=false;

		if (years == 0) {
			stringaTotale = stringaTotale+"";
		} else {
			stringaTotale = stringaTotale+ " "+resources.getQuantityString(R.plurals.Anni, years, years);
		}
		if (months == 0) {
			stringaTotale = stringaTotale+"";
		} else {
			String tmp;
			if(stringaTotale.equalsIgnoreCase("")) tmp="";
			else tmp=" ";
			stringaTotale =  stringaTotale+tmp+resources.getQuantityString(R.plurals.Mesi, months, months);
		}
		if (weeks == 0) {
			stringaTotale = stringaTotale+"";
		} else {
			String tmp;
			if(stringaTotale.equalsIgnoreCase("")) tmp="";
			else tmp=" ";
			stringaTotale = stringaTotale+tmp+resources.getQuantityString(R.plurals.Settimane, weeks, weeks);
		}
		if (days == 0) {
			stringaTotale = stringaTotale+"";
		} else {
			String tmp;
			if(stringaTotale.equalsIgnoreCase("")) tmp="";
			else tmp=" ";
			stringaTotale = stringaTotale+tmp+resources.getQuantityString(R.plurals.Giorni, days, days);
		}

		if (hours == 0) {
			stringaTotale = stringaTotale+"";
		} else {
			String tmp;
			if(stringaTotale.equalsIgnoreCase("")) tmp="";
			else tmp=" ";
			stringaTotale = stringaTotale+tmp+resources.getQuantityString(R.plurals.Ore, hours, hours);
		}
		if (minutes == 0) {
			stringaTotale = stringaTotale+"";
		} else {
			String tmp;
			if(stringaTotale.equalsIgnoreCase("")) tmp="";
			else tmp=" ";
			stringaTotale = stringaTotale+ tmp+resources.getQuantityString(R.plurals.Minuti, minutes, minutes);
		}
		if (seconds == 0) {
			stringaTotale = stringaTotale+"";
		} else {
			String tmp;
			if(stringaTotale.equalsIgnoreCase("")) tmp="";
			else tmp=" ";
			stringaTotale = stringaTotale+ tmp+resources.getQuantityString(R.plurals.Secondi, seconds, seconds);
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
			} else if (tipo.isSupported(DurationFieldType.years())) {
				stringaTotale = resources.getQuantityString(R.plurals.Anni, 0, 0);
			}else {
				stringaTotale = resources.getQuantityString(R.plurals.Giorni, 0, 0);
			}
		
		
		return stringaTotale;
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

	public String getDescrizione() {
		if(!descrizione.equals("")) return descrizione;
		else return this.toString();
	}
	public String getDescrizioneIfExists() {
		return descrizione;
	}
	public String toString()
    {
	    String date =Costanti.dt.format(this.getTime());
	    String time =Costanti.tf.format(this.getTime());
		return date+" - "+time;
		}

	public long getMillisecondiIniziali() {
		return millisecondiIniziali;
	}
	public int getColor() {
		return color;
	}

	public boolean isNotificationEnabled() {
		return notifica;
	}

	public int getPercentuale() {
		double d = (double) (new GregorianCalendar().getTimeInMillis()  - millisecondiIniziali)
				/ (double) (this.getTimeInMillis() - millisecondiIniziali);
		if (d >= 1.0D || d < 0.0D)
			return 1000;
		else
			return (int) Math.floor(1000D * d);
	}

	public PeriodType getTipo() {
		return tipo;
	}

	public void setMillisecondiIniziali(long l) {
		millisecondiIniziali = l;
	}
	public void setText(String s) {
		descrizione = s;
	}
	public void setPeriodType(boolean years, boolean months, boolean weeks, boolean days,
			boolean hours, boolean minutes, boolean seconds) {
		tipo=creaPeriodType(years, months, weeks, days, hours, minutes, seconds);
	}
	public void setPeriodType(PeriodType periodtype) {
		tipo = periodtype;
	}

	public int compare(Data rhs) {
		if (this.getTimeInMillis() < rhs.getTimeInMillis())
			return 1;
		else if (this.getTimeInMillis() > rhs.getTimeInMillis())
			return -1;
		return 0;
	}
	
	
	@Override
	public boolean equals(Object ob) {
		//TODO mettere il controllo il invece del controllo millisecondi appena gli id verranno implementati
		if(!(ob instanceof Data)) return false;
		Data d=(Data) ob;
		return this.getMillisecondiIniziali()==d.getMillisecondiIniziali();
				
	}


	
	public boolean isAfterToday(){
		Calendar today=new GregorianCalendar();
        return this.getTimeInMillis() > today.getTimeInMillis();
    }
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(descrizione);
		dest.writeLong(millisecondiIniziali);
		dest.writeByte((byte) (notifica ? 1 : 0));
		dest.writeInt(color);
		dest.writeLong(this.getTimeInMillis());
		dest.writeParcelable(image,0);
		dest.writeSerializable(tipo);
	}
	public static final Parcelable.Creator<Data> CREATOR = new Parcelable.Creator<Data>() {
	        public Data createFromParcel(Parcel in) {
	            return new Data(in);
	        }

	        public Data[] newArray(int size) {
	            return new Data[size];
	}};
	private Data(Parcel in) {
	    super();
	    descrizione=in.readString();
	    millisecondiIniziali=in.readLong();
	    notifica=in.readByte() != 0;
	    color=in.readInt();
	    this.setTimeInMillis(in.readLong());
		image= in.readParcelable(Uri.class.getClassLoader());

		tipo=(PeriodType) in.readSerializable();
	}
	private String descrizione;
	private long millisecondiIniziali;
	private boolean notifica;
	private int color;
	private Uri image;
	private PeriodType tipo;
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	public int getYear() {
		return this.get(YEAR);
	}
	public int getMonth() {
		return get(MONTH);
	}
	public int getDay() {
		return get(DAY_OF_MONTH);
	}
	public int getHour() {
		return get(HOUR_OF_DAY);
	}
	public int getMinute(){
		return get(MINUTE);
	}
	public boolean isNotifica() {
		return notifica;
	}
	public void setNotification(boolean noti) {
		notifica = noti;
	}
	public void setHour(int hourOfDay) {
		this.set(HOUR_OF_DAY, hourOfDay);
		
	}
	public void setMinute(int minute) {
		this.set(MINUTE, minute);
		
	}
	public void setYear(int year) {
		this.set(YEAR, year);		
	}
	public void setMonth(int monthOfYear) {
		this.set(MONTH, monthOfYear);
	}
	public void setDay(int dayOfMonth) {
		this.set(DAY_OF_MONTH, dayOfMonth);
		
	}
	public void setColor(int c) {
		color=c;		
	}
	public void setDescription(String t) {
		descrizione=t;
		
	}


	public Uri getImage() {
		return image;
	}

	public void setImage(Uri image) {
		this.image = image;
	}
}
