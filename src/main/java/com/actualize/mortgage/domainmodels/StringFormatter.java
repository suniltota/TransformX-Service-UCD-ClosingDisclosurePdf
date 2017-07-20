package com.actualize.mortgage.domainmodels;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public enum StringFormatter {
	DATE,                 // Applies the date format: "mm/dd/yyyy"
	SHORTDATE,            // Applies the date format: "mm/dd/yyyy"
	ABSDOLLARS,			  // Dollar format of absolute value
	NODOLLARS,            // remove doller sign from amount.
	DOLLARS,              // Adds a preceding dollar sign and two digits after decimal.
	TRUNCDOLLARS,         // Removes decimal and cents from the dollar format.
	ZEROTRUNCDOLLARS,     // Dollar format, except zero's are '$0'
	PERCENT,			  // Adds a percent sign as a suffix
	PERCENTWITHOUTPRECEEDING,// Adds a percent sign as a suffix, removes preceeding zero(0.0250 =>.025% )
	NUMBERTWODIGITS,      // Adds a percent sign as a suffix
	NUMBERTHREEDIGITS,    // Adds a percent sign as a suffix
	INTERESTRATE,         // Whole number if no significant digits after decimal. Otherwise, minimum of two digits after decimal, maximum three digits.
	YEARS,
	ROUNDUPYEARS,
	ROUNDUPPLUSONEYEAR,
	ROUNDUPYEAR,
	MONTHSORYEARS,
	INTEGERSUFFIX,		// add the appropriate suffix e.g.st, nd, th
	MONTH,			// Translate MISMO month (--nn) to month name
	PHONENUMBER,    // Puts dashes into a number string
	CAMEL,          // Reformats a Camel-case string to have spaces
	NEGATE,         // Negates a number string, e.g. turns negatives into positives and vice versa
	STRINGCLEAN;    // Takes bad characters out of strings

	private static NumberFormat     noCurrencyFormatter = NumberFormat.getInstance();
	private static NumberFormat     currencyFormatter   = NumberFormat.getCurrencyInstance();
	private static DateFormat       inputDates          = new SimpleDateFormat("yyyy-mm-dd", Locale.ENGLISH);
	private static SimpleDateFormat outputDates         = new SimpleDateFormat("m/dd/yyyy");
	private static SimpleDateFormat shortOutputDates    = new SimpleDateFormat("m/d/yy");
	private static DecimalFormat    dfTwo               = new DecimalFormat("#.00");
	private static DecimalFormat    dfThree             = new DecimalFormat("#.000");
	
	public String formatString(String inStr) {
		String outStr = inStr; // Default to input string
		switch (this) {
		case DATE:
			try {
				outStr = outputDates.format(inputDates.parse(inStr));
			} catch (Exception e) {
			}
			break;
		case SHORTDATE:
			try {
				outStr = shortOutputDates.format(inputDates.parse(inStr));
			} catch (Exception e) {
			}
			break;
		case ABSDOLLARS:
			currencyFormatter.setMinimumFractionDigits(2);
			currencyFormatter.setMaximumFractionDigits(2);
			if (doubleValue(inStr) < 0)
				outStr = currencyFormat(NEGATE.formatString(inStr));
			else
				outStr = currencyFormat(inStr);
			break;
		case NODOLLARS:
			noCurrencyFormatter.setMinimumFractionDigits(2);
			noCurrencyFormatter.setMaximumFractionDigits(2);
			outStr = noCurrencyFormat(inStr);
			break;
		case DOLLARS:
			currencyFormatter.setMinimumFractionDigits(2);
			currencyFormatter.setMaximumFractionDigits(2);
			outStr = currencyFormat(inStr);
			break;
		case TRUNCDOLLARS:
			currencyFormatter.setMaximumFractionDigits(0);
			outStr = currencyFormat(inStr);
			break;
		case ZEROTRUNCDOLLARS:
			if (doubleValue(inStr) % 1 == 0)
				outStr = TRUNCDOLLARS.formatString(inStr);
			else
				outStr = DOLLARS.formatString(inStr);
			break;
		case PERCENT:
			// Remove trailing zeroes
			outStr = inStr.indexOf(".") < 0 ? inStr : outStr.replaceAll("0*$", "").replaceAll("\\.$", ""); 				
			outStr = outStr + "%";
			break;
		case PERCENTWITHOUTPRECEEDING:
			outStr = inStr.indexOf(".") < 0 ? inStr : cleanZeroBeforeDecimal(inStr); 				
			outStr = outStr + "%";
			break;
		case NUMBERTWODIGITS:
			outStr = dfTwo.format(doubleValue(inStr));
			break;
		case NUMBERTHREEDIGITS:
			outStr = dfThree.format(doubleValue(inStr));
			break;
		case INTERESTRATE:
			if (inStr.indexOf(".") < 0) // No decimal, format is fine
				outStr = inStr;
			else {
				DecimalFormat df = new DecimalFormat("#.000");          // Interest rate can have up to three digits
				outStr = df.format(doubleValue(inStr));
				while (outStr.charAt(outStr.length()-1) == '0')         // Remove trailing 0's
					outStr = outStr.substring(0, outStr.length()-1);
				if (outStr.charAt(outStr.length()-1) == '.')            // Remove decimal if it's the last character in string
					outStr = outStr.substring(0, outStr.length()-1);
			}
			outStr = outStr + "%";
			break;
		case YEARS:
			outStr = Integer.toString((int)doubleValue(inStr)/12);
			break;
		case ROUNDUPYEARS:
			outStr = Integer.toString((int)Math.ceil((doubleValue(inStr)+1)/12.0));
			break;
		case ROUNDUPPLUSONEYEAR:
			outStr = Integer.toString((int)Math.ceil((doubleValue(inStr)+12)/12));
			break;
		case ROUNDUPYEAR:
			outStr = Integer.toString((int)Math.ceil((doubleValue(inStr))/12));
			break;
		case MONTHSORYEARS:
			int months = (int)doubleValue(inStr) + 1;
			if (months > 23)
				outStr = "year " + Integer.toString(months/12 + (months%12==0 ? 0 : 1));
			else
				outStr = "mo. " + Integer.toString(months-1);
			break;
		case PHONENUMBER:
			if (inStr.length() > 6)
				outStr = inStr.substring(0,3) + "-" + inStr.substring(3, 6) + "-" + inStr.substring(6, inStr.length());
			break;
		case CAMEL:
			outStr = "";
			
			for (String s : inStr.replaceAll("\\s*-+\\s*", " - ").split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])"))
				outStr = outStr.equals("") ? s.trim() : outStr + " " + s.trim();	
				if(outStr.contains("( Optional)"))
				outStr = outStr.replace("( Optional)", " (Optional)");	
			//if (outStr.startsWith("Homeowners Association "))
				//outStr = "HOA" + outStr.substring("Homeowners Association".length());
			break;
		case NEGATE:
			outStr = String.valueOf(-doubleValue(inStr));
			break;
		case STRINGCLEAN:
			outStr = inStr.trim().replaceAll("\\s+"," ").replaceAll("\\u2018|\\u2019|\\u00B4","'");
			break;
		case MONTH:
			switch (inStr) {
			case "--01":
				outStr = "January";
				break;
			case "--02":
				outStr = "February";
				break;
			case "--03":
				outStr = "March";
				break;
			case "--04":
				outStr = "April";
				break;
			case "--05":
				outStr = "May";
				break;
			case "--06":
				outStr = "June";
				break;
			case "--07":
				outStr = "July";
				break;
			case "--08":
				outStr = "August";
				break;
			case "--09":
				outStr = "September";
				break;
			case "--10":
				outStr = "October";
				break;
			case "--11":
				outStr = "November";
				break;
			case "--12":
				outStr = "December";
				break;
			default:
				outStr = "";
			}
			break;
		case INTEGERSUFFIX:
			int val = integerValue(inStr);
			int dd = val%100;
			if (dd > 10 && dd < 20) // teens
				outStr = Integer.toString(val)+"th";
			else // non teens
				switch (dd % 10) {
				case 1:
					outStr = Integer.toString(val)+"st";
					break;
				case 2:
					outStr = Integer.toString(val)+"nd";
					break;
				case 3:
					outStr = Integer.toString(val)+"rd";
					break;
				default:
					outStr = Integer.toString(val)+"th";
				}
		default:
			break;
		}
		return outStr;
	}

	static double doubleValue(String str) {
		try {
			return Double.parseDouble(str);
		} catch (Exception e) {
		}
		return 0;
	}

	static int integerValue(String str) {
		try {
			if(str.indexOf(".")!=-1) 
				return Integer.valueOf(str.split("\\.")[0]);
			return Integer.valueOf(str);
		} catch (Exception e) {
		}
		return 0;
	}
	
	static private String currencyFormat(String str) {
		double amount = doubleValue(str);
		if (amount == 0)
			return "$0";
		if (amount < 0)
			return "-" + currencyFormatter.format(-amount);
		return currencyFormatter.format(amount);
	}
	
	static private String noCurrencyFormat(String str) {
		double amount = doubleValue(str);
		if (amount == 0)
			return "$0";
		if (amount < 0)
			return "-" + noCurrencyFormatter.format(-amount);
		return noCurrencyFormatter.format(amount);
	}
	static private String cleanZeroBeforeDecimal(String str){
		String percent = str;
		String deciLoc = str.substring(0, str.indexOf("."));
		if(deciLoc.length() == 1 && "0".equals(deciLoc))
			str = str.substring(1, str.length());
		 percent = str.replaceAll("0*$", "").replaceAll("\\.$", "");
		return percent;
	}
}
