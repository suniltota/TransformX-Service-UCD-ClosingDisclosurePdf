package com.actualize.mortgage.domainmodels;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.actualize.mortgage.datalayer.ClosingMap;
import com.actualize.mortgage.datalayer.InputData;
import com.actualize.mortgage.datalayer.PIadjustments;
import com.actualize.mortgage.datalayer.Utils;
import com.actualize.mortgage.pdfbuilder.Color;
import com.actualize.mortgage.pdfbuilder.FormattedText;
import com.actualize.mortgage.pdfbuilder.Grid;
import com.actualize.mortgage.pdfbuilder.Page;
import com.actualize.mortgage.pdfbuilder.Paragraph;
import com.actualize.mortgage.pdfbuilder.Region;
import com.actualize.mortgage.pdfbuilder.Section;
import com.actualize.mortgage.pdfbuilder.Text;
import com.actualize.mortgage.pdfbuilder.Typeface;
import com.actualize.mortgage.pdfbuilder.Grid.Dimension;
import com.actualize.mortgage.pdfbuilder.Grid.Position;
import com.actualize.mortgage.pdfbuilder.TextBox.HorizontalAlignment;

public class AdjustablePaymentSection implements Section {
	public static final Text TAB = new Text(Color.WHITE, 11, Typeface.CALIBRI_BOLD);
	
	private Grid apTitleGrid, apDataGrid;
	Region info;
	float rowHeight = 11f/72f;
	float borderWidth = 1f/72f;
	
	private void initializeAPTitleGrid() {		
		float col1Width = 2.6f;
		float heights[] = { rowHeight };
		float widths[] = { col1Width };
		apTitleGrid = new Grid(2, heights, widths.length, widths);
		apTitleGrid.getCell(0, 0)
		.setHorizontalAlignment(HorizontalAlignment.LEFT)
		.setBackground(new Tab(col1Width))
		.setForeground(new FormattedText(" Adjustable Payment (AP) Table", TAB));
	}
	
	private void initializeAPDataGrid(InputData inputData) {
		List<PIadjustments> piAdjustmentsList = inputData.getPiAdjustmentsList();
		float heights[] = { rowHeight };
		float widths[]  = { 110f/72f, 20f/72f, 115f/72f };
		ClosingMap closingMap = inputData.getClosingMap();

		apDataGrid = new Grid( 10, heights, 3, widths);
		apDataGrid.setLineHorizontalAlignment(2, Dimension.COLUMN, HorizontalAlignment.RIGHT);
		int row = 0;
		apDataGrid.setLineBorder(row, Position.TOP, Color.BLACK, borderWidth);
		apDataGrid.setCellBorder(row, 0, Position.RIGHT, Color.BLACK, borderWidth);

		//INTEREST ONLY--------------------------------------------------------------------------------------
		if (closingMap.getClosingMapValue("LOAN_DETAIL.InterestOnlyIndicator").equalsIgnoreCase("true")) {
			Paragraph para = new Paragraph()
			.append(new FormattedText( "  For your first ",Text.TABLE_TEXT))
			.append(new FormattedText( 
					closingMap.getClosingMapValue("INTEREST_ONLY.InterestOnlyTermMonthsCount")
					,Text.TABLE_TEXT))
					.append(new FormattedText( " payments",Text.TABLE_TEXT));
			apDataGrid.setCellText(row, 1, new FormattedText(" YES",Text.TABLE_TEXT_BOLD));
			apDataGrid.setCellText(row, 2, para);
		} else {
			apDataGrid.setCellText(row, 1, new FormattedText(" NO",Text.TABLE_TEXT_BOLD));
		}
		apDataGrid.setCellText(row++, 0, new FormattedText("Interest Only Payments?",Text.TABLE_TEXT_BOLD));

		//OPTIONAL PAYMENTS -----------------------------------------------------------------------------------------------------
		apDataGrid.setLineBorder(row, Position.TOP, Color.BLACK, borderWidth);
		apDataGrid.setCellBorder(row, 0, Position.RIGHT, Color.BLACK, borderWidth);

		if(closingMap.getClosingMapValue("PAYMENT_RULE.PaymentOptionIndicator").equalsIgnoreCase("true")){
			int diff = monthsBetween(closingMap.getClosingMapValue("PRINCIPAL_AND_INTEREST_ADJUSTMENT_LIMITED_PAYMENT_OPTION.LimitedPrincipalAndInterestPaymentEffectiveDate"), 
					closingMap.getClosingMapValue("PRINCIPAL_AND_INTEREST_ADJUSTMENT_LIMITED_PAYMENT_OPTION.LimitedPrincipalAndInterestPaymentPeriodEndDate"));
			Paragraph para = (new Paragraph())
					.append(new FormattedText(" For your first ",Text.TABLE_TEXT))
					.append(new FormattedText(Integer.toString(diff),Text.TABLE_TEXT))
									.append(new FormattedText(" payments",Text.TABLE_TEXT));
			apDataGrid.setCellText(row, 1, new FormattedText(" YES",Text.TABLE_TEXT_BOLD));
			apDataGrid.setCellText(row, 2, para);
		} else {
			apDataGrid.setCellText(row,1, new FormattedText(" NO",Text.TABLE_TEXT_BOLD));
		}
		apDataGrid.setCellText(row++, 0, new FormattedText("Optional Payments?",Text.TABLE_TEXT_BOLD));
		
		//STEP PAYMNENTS -----------------------------------------------------------------------------------------------------------------
		apDataGrid.setLineBorder(row, Position.TOP, Color.BLACK, borderWidth);
		apDataGrid.setCellBorder(row, 0, Position.RIGHT, Color.BLACK, borderWidth);
		if(closingMap.getClosingMapValue("AMORTIZATION_RULE.AmortizationType").equalsIgnoreCase("Step")){
			int diff = monthsBetween(closingMap.getClosingMapValue("PAYMENT_RULE.ScheduledFirstPaymentDate"), 
					closingMap.getClosingMapValue("PRINCIPAL_AND_INTEREST_PAYMENT_LIFETIME_ADJUSTMENT_RULE.FinalPrincipalAndInterestPaymentChangeDate"));
			Paragraph para = (new Paragraph())
					.append(new FormattedText(" For your first ",Text.TABLE_TEXT))
					.append(new FormattedText( Integer.toString(diff), Text.TABLE_TEXT))
					.append(new FormattedText(" payments",Text.TABLE_TEXT));
			apDataGrid.setCellText(row, 1, new FormattedText(" YES",Text.TABLE_TEXT_BOLD));
			apDataGrid.setCellText(row, 2, para);
		} else {
			apDataGrid.setCellText(row,1, new FormattedText(" NO",Text.TABLE_TEXT_BOLD));
		}
		apDataGrid.setCellText(row++, 0, new FormattedText("Step Payments?",Text.TABLE_TEXT_BOLD));

		//SEASONAL PAYMENTS ------------------------------------------------------------------------------------------------------------------
		apDataGrid.setLineBorder(row, Position.TOP, Color.BLACK, borderWidth);
		apDataGrid.setCellBorder(row, 0, Position.RIGHT, Color.BLACK, borderWidth);
		if(closingMap.getClosingMapValue("LOAN_DETAIL.SeasonalPaymentFeatureIndicator").equalsIgnoreCase("true")){
			Paragraph para = (new Paragraph())
					.append(new FormattedText(
							StringFormatter.MONTH.formatString(
							closingMap.getClosingMapValue("PAYMENT_RULE.SeasonalPaymentPeriodStartMonth")),
							Text.TABLE_TEXT))
							.append(new FormattedText(" to ",Text.TABLE_TEXT))
							.append(new FormattedText(
									StringFormatter.MONTH.formatString(
											closingMap.getClosingMapValue("PAYMENT_RULE.SeasonalPaymentPeriodEndMonth")),
											Text.TABLE_TEXT))
							.append(new FormattedText(" each year",Text.TABLE_TEXT));
			apDataGrid.setCellText(row, 1, new FormattedText(" YES",Text.TABLE_TEXT_BOLD));
			apDataGrid.setCellText(row, 2, para);
		} else {
			apDataGrid.setCellText(row,1, new FormattedText(" NO",Text.TABLE_TEXT_BOLD));
		}
		apDataGrid.setCellText(row++, 0, new FormattedText("Seasonal Payments?",Text.TABLE_TEXT_BOLD));

		// MONTHLY Principal and Interest Payments -----------------------------------------------------------------------------------------------------------
		// Capture monthly amounts/changes
		String max = "";
		String min = "";
		String first = "";
		String freq = "";
		for (PIadjustments piAdjustment:piAdjustmentsList) {
			if (piAdjustment.getAdjustmentRuleType().equalsIgnoreCase("First")) {
				max = piAdjustment.getPerChangeMaximumPaymentAmount();
				min = piAdjustment.getPerChangeMinimumPaymentAmount();
				first = piAdjustment.getFirstChangeMonthsCount();
			} else if (piAdjustment.getAdjustmentRuleType().equalsIgnoreCase("Subsequent")) {
				freq = piAdjustment.getPerChangeAdjustmentFrequencyMonthsCount();
			}
		}

		// Write intro
		apDataGrid.setLineBorder(row, Position.TOP, Color.BLACK, borderWidth);
		apDataGrid.setCellText(row++, 0, new FormattedText("Monthly Principal and Interest Payments",Text.TABLE_TEXT_BOLD));
		apDataGrid.setLineBorder(row, Position.TOP, Color.BLACK, borderWidth);
		
		// determine if the last three lines of the AP table are written
		boolean isPrintAPBottom = false;
		if ("step".equalsIgnoreCase(closingMap.getClosingMapValue("AMORTIZATION_RULE.AmortizationType")))
			isPrintAPBottom = true;
		if ("true".equalsIgnoreCase(closingMap.getClosingMapValue("LOAN_DETAIL.InterestOnlyIndicator")))
			isPrintAPBottom = true;
		if ("true".equalsIgnoreCase(closingMap.getClosingMapValue("LOAN_DETAIL.OptionalPaymentIndicator")))
			isPrintAPBottom = true;
		if ("true".equalsIgnoreCase(closingMap.getClosingMapValue("LOAN_DETAIL.SeasonalFeaturePaymentIndicator")))
			isPrintAPBottom = true;
		
		// special case for a construction or construction to perm  loan that is not a balloon
		if  (closingMap.getClosingMapValue("LOAN_DETAIL.BalloonIndicator").equalsIgnoreCase("true") 
				&& (closingMap.getClosingMapValue("LOAN_DETAIL.ConstructionLoanIndicator").equalsIgnoreCase("true")
				|| closingMap.getClosingMapValue("CONSTRUCTION.ConstructionLoanType").equalsIgnoreCase("ConstructionToPermanent")))
			isPrintAPBottom = false;
		
		// also, don't print if none of the values have been set
		if ("".equals(max) && "".equals(min) && "".equals(first) && "".equals(freq))
			isPrintAPBottom = false;
			
		// Write "First Change/Amount"
		if (isPrintAPBottom) {
			Paragraph para = new Paragraph();
			if (!min.equals("0") && !min.equals(""))
				para.append(new FormattedText(StringFormatter.TRUNCDOLLARS.formatString(min) + " - ", Text.TABLE_TEXT));
			para.append(new FormattedText(StringFormatter.TRUNCDOLLARS.formatString(max) + " at ", Text.TABLE_TEXT))
				.append(new FormattedText(StringFormatter.INTEGERSUFFIX.formatString(first), Text.TABLE_TEXT))
				.append(new FormattedText(" payment",Text.TABLE_TEXT));
			apDataGrid.setCellText(row, 2, para);
		}
		apDataGrid.setCellText(row++, 0, new FormattedText("    First Change/Amount", Text.TABLE_TEXT));
		apDataGrid.setLineBorder(row, Position.TOP, Color.BLACK, borderWidth);

		// Write "Subsequent changes"
		if (isPrintAPBottom) {
			Paragraph para = new Paragraph();
			if (freq.equals(""))
				para.append(new FormattedText("No subsequent changes.", Text.TABLE_TEXT));
			else {
				int months = StringFormatter.integerValue(freq);
				String str = "Every " + freq + " months";
				if (months == 12)
					str = "Every year";
				else if (months%12 == 0)
					str = "Every " + months/12 + " years";				
				para.append(new FormattedText(str, Text.TABLE_TEXT));
			}
			apDataGrid.setCellText(row, 2, para);
		}
		apDataGrid.setCellText(row++, 0, new FormattedText("    Subsequent Changes", Text.TABLE_TEXT));
		apDataGrid.setLineBorder(row, Position.TOP, Color.BLACK, borderWidth);

		// Write "Maximum payment"
		if (isPrintAPBottom) {
			Paragraph para = (new Paragraph())
				.append(new FormattedText(StringFormatter.TRUNCDOLLARS.formatString( 
						closingMap.getClosingMapValue("PRINCIPAL_AND_INTEREST_PAYMENT_LIFETIME_ADJUSTMENT_RULE.PrincipalAndInterestPaymentMaximumAmount")), Text.TABLE_TEXT))
				.append(new FormattedText(" starting at ",Text.TABLE_TEXT))
				.append(new FormattedText(StringFormatter.INTEGERSUFFIX.formatString(
						closingMap.getClosingMapValue("PRINCIPAL_AND_INTEREST_PAYMENT_LIFETIME_ADJUSTMENT_RULE.PrincipalAndInterestPaymentMaximumAmountEarliestEffectiveMonthsCount")), Text.TABLE_TEXT))
				.append(new FormattedText(" payment",Text.TABLE_TEXT));
			apDataGrid.setLineBorder(row, Position.TOP, Color.BLACK, borderWidth);
			apDataGrid.setCellText(row, 2,para);
		}
		apDataGrid.setCellText(row, 0, new FormattedText("    Maximum Payment", Text.TABLE_TEXT));
		apDataGrid.setLineBorder(row, Position.BOTTOM, Color.BLACK, borderWidth);
	}

	private int monthsBetween(String startString, String endString) {
		int diffMonth = 0;
		if (startString != "" && endString != ""){
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date startDate = new Date(), endDate = new Date();
			try {
				startDate = format.parse(startString);
				endDate   = format.parse(endString);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Calendar startCalendar = new GregorianCalendar();
			startCalendar.setTime(startDate);
			Calendar endCalendar = new GregorianCalendar();
			endCalendar.setTime(endDate);
			int diffYear = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
			diffMonth = diffYear * 12 + endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);
			//System.out.println("startString:"+startString+" date:"+startDate);
			//System.out.println("months:"+diffMonth+" startMonth:"+startCalendar.get(Calendar.MONTH)+" endMonth:"+endCalendar.get(Calendar.MONTH));
		}
		return diffMonth;
	}
	
	public void draw(Page page, Object d) throws IOException {
		InputData data = (InputData)d;
		if (Utils.hasAdjustablePayment(data)) {
			initializeAPTitleGrid();
			initializeAPDataGrid(data);
			apTitleGrid.draw( page, page.leftMargin, page.bottomMargin + apDataGrid.height(page) - 7f/72f );
			apDataGrid.draw(  page, page.leftMargin, page.bottomMargin + rowHeight - 7f/72f);
		}
	}


}
