package com.asdamp.utility;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UtilityDate {


	public static String convertiDataInStringaBasandosiSuConfigurazione(Date d, DateFormat dt){
		Date date = d;
		DateFormat dateFormat = dt;
		return dateFormat.format(date);
		
	}
	public static String convertiDataInStringaBasandosiSuConfigurazione(int anno, int mese, int giorno, int minuto, int ora, DateFormat dt){
		Date date = UtilityDate.creaData(anno, mese, giorno, minuto, ora);
		DateFormat dateFormat = dt;
		return dateFormat.format(date);
		
	}
	public static Date creaData(int anno, int mese, int giorno, int minuto, int ora){
		DateFormat df=new SimpleDateFormat("dd-MM-yyyy-kk-mm");
		try {
			int m=mese;
			m++;
			return df.parse(giorno+"-"+m+"-"+anno+"-"+ora+"-"+minuto);
		} catch (ParseException e) {
			System.err.println("parsing problem");
			System.exit(-1);
		}
		return null;
	}
}
