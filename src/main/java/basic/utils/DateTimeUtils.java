package basic.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DateTimeUtils {
	
	/**
	 * Transfer a {@link Date} to formatted {@link String}.
	 * 
	 * @see {@link SimpleDateFormat#format(Date)}
	 * 
	 * @param date
	 * 		A {@link Date} instance to be transformed.
	 * @param format
	 * 		The format transform into.
	 * @return Correspondent time {@link String}.
	 */
	public static String toString(Date date, String format) {
		return new SimpleDateFormat(format).format(date);
	}
	
	/**
	 * Formatted {@link String} of now.
	 * 
	 * @see {@link #toString(Date, String)}
	 * 
	 * @param format
	 * 		The format transform into.
	 * @return Current time {@link String}.
	 */
	public static String currentDateTimeString(String format) {
		return toString(new Date(), format);
	}
}
