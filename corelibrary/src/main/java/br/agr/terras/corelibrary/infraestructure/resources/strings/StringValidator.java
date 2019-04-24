package br.agr.terras.corelibrary.infraestructure.resources.strings;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by leo on 07/09/15.
 */
public class StringValidator {
    public static boolean isNotNumeric(String str)
    {
        try
        {
            Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
            return true;
        }
        return false;
    }

    public static boolean isNotInteger(String str)
    {
        try
        {
            Integer.parseInt(str);
        }
        catch(NumberFormatException nfe)
        {
            return true;
        }
        return false;
    }

    public static boolean isNotDate(String string){
        Date date;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            date = sdf.parse(string);
            if (!string.equals(sdf.format(date))) {
                date = null;
            }
        } catch (ParseException ex) {
            return true;
        }
        if (date == null) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isNotRetroactiveDate(String string){
        Date date;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date today = calendar.getTime();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            date = sdf.parse(string);
            if (!string.equals(sdf.format(date))) {
                date = null;
            }
        } catch (ParseException ex) {
            return true;
        }
        if (date == null) {
            return true;
        } else {
            if (date.before(today)){
                return true;
            }else{
                return false;
            }
        }
    }
}
