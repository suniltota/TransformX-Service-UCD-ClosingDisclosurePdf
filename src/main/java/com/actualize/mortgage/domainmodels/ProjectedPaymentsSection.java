 package com.actualize.mortgage.domainmodels;

import java.io.IOException;
import java.util.List;

import com.actualize.mortgage.datalayer.ClosingMap;
import com.actualize.mortgage.datalayer.InputData;
import com.actualize.mortgage.datalayer.ProjectedPayments;
import com.actualize.mortgage.datalayer.PropertyCostComponents;
import com.actualize.mortgage.pdfbuilder.BoxedCharacter;
import com.actualize.mortgage.pdfbuilder.Color;
import com.actualize.mortgage.pdfbuilder.FormattedText;
import com.actualize.mortgage.pdfbuilder.Grid;
import com.actualize.mortgage.pdfbuilder.LineFeed;
import com.actualize.mortgage.pdfbuilder.Page;
import com.actualize.mortgage.pdfbuilder.Paragraph;
import com.actualize.mortgage.pdfbuilder.Region;
import com.actualize.mortgage.pdfbuilder.Section;
import com.actualize.mortgage.pdfbuilder.Spacer;
import com.actualize.mortgage.pdfbuilder.Text;
import com.actualize.mortgage.pdfbuilder.Typeface;
import com.actualize.mortgage.pdfbuilder.Grid.Dimension;
import com.actualize.mortgage.pdfbuilder.Grid.Position;
import com.actualize.mortgage.pdfbuilder.TextBox.Direction;
import com.actualize.mortgage.pdfbuilder.TextBox.HorizontalAlignment;
import com.actualize.mortgage.pdfbuilder.TextBox.VerticalAlignment;

public class ProjectedPaymentsSection implements Section {
	private static final Text TAB                  = new Text(Color.WHITE, 11,    Typeface.CALIBRI_BOLD);
	private static final Text TEXT_AMOUNT_LARGE    = new Text(Color.BLACK, 14,    Typeface.CALIBRI);

	private static Text TEXT_AMOUNT 		 = null;
	private static Text ESC_TYPE_TEXT     = null;
	private static Text ESCROW_TEXT_OBLIQUE  = null;
	private static Text ESC_TEXT_BOLD     = null;
	private static Text ONLY_INTEREST     = null;
	
	
	private static final float col1Width  = 1.85f;
	private static final float remWidth   = 5.65f;
	private static final float paymentWidth[] = { remWidth, remWidth/2, remWidth/3, remWidth/4, remWidth/5 };
	private static final float margin[]       = { remWidth/3, remWidth/6, remWidth/12, remWidth/32, remWidth/24 };
	private static final float borderWidth = 1f/72f;

	private static final float leftIndent  = 3f/72f;
	
	private static final LineFeed diffLine = new LineFeed(3f/72f);
	private static final LineFeed halfLine = new LineFeed(2f/72f);
	private static final LineFeed ioLine = new LineFeed(8f/72f);
	private static final LineFeed esLine = new LineFeed(7f/72f);
	private static final LineFeed miLine = new LineFeed(4f/72f);
	private static final LineFeed fullLine = new LineFeed(11f/72f);
	
	private Grid titleGrid, paymentsGrid, overlayGrid, estimatesGrid;
	
	private void initializeTitleGrid() {
		float heights[] = { 22f/72f };
		float widths[] = { col1Width, 2.0f };
		titleGrid = new Grid(1, heights, 2, widths);
		titleGrid.setLineHorizontalAlignment(0, Dimension.ROW, HorizontalAlignment.CENTER);
		titleGrid.getCell(0, 0)
			.setHorizontalAlignment(HorizontalAlignment.LEFT)
			.setMargin(Direction.LEFT, leftIndent)
			.setBackground(new Tab(col1Width))
			.setForeground(new FormattedText("Projected Payments", TAB));
		titleGrid.setLineBorder(0, Position.BOTTOM, Color.BLACK, borderWidth);
	}
	
	private void initializePaymentsGrid(Page page, InputData inputData) {
		List<ProjectedPayments> paymentsList = inputData.getPaymentsList();
		ClosingMap closingMap = inputData.getClosingMap();
		if (inputData.isDocsDirect()){
			TEXT_AMOUNT 		= Text.SECTION_INFO;
			ESC_TYPE_TEXT 	    = Text.SECTION_TEXT;
			ESCROW_TEXT_OBLIQUE = Text.TABLE_OBLIQUE;
			ESC_TEXT_BOLD       = Text.TABLE_HEADER_LARGE;
			ONLY_INTEREST		= Text.TABLE_OBLIQUE_BOLD;
		} else {
			TEXT_AMOUNT 		= new Text(Color.BLACK, 14, Typeface.CALIBRI);
			ESC_TYPE_TEXT 	    = new Text(Color.BLACK, 10f, Typeface.CALIBRI);
			ESCROW_TEXT_OBLIQUE = new Text(Color.BLACK,  9,    Typeface.CALIBRI_OBLIQUE);
			ESC_TEXT_BOLD       = new Text(Color.BLACK, 10f, Typeface.CALIBRI_BOLD);
			ONLY_INTEREST		= Text.TABLE_TEXT_BOLD;
		}
		boolean everMI = false;
		boolean everEscrow = false;
		boolean hasMinMax = findMinMax(paymentsList) != null;
		boolean hasMinMaxIO = findMinMaxIO(paymentsList) != null;
		boolean hasIO = closingMap.getClosingMapValue("LOAN_DETAIL.InterestOnlyIndicator").equalsIgnoreCase("true") || findIO(paymentsList) != null;
		Region col0;
		
		// Determine grid cell widths and heights
		int payments = paymentsList.size();
		if (payments == 0)
			payments = 1;
		else if (payments > 5)	
			payments = 5;
		float heights[] = { 16f/72f, 77f/72f, 40f/72f };
		float paymentsHeights[] = { 16f/72f, 87f/72f, 30f/72f };
		float widths[] = new float[2*payments + 1];
		widths[0] = col1Width;
		if (payments == 1) {
			widths[1] = col1Width;
			widths[2] = 7.5f - 2*col1Width;
		} else {
			for (int i = 1; i < widths.length; i = i + 2) {
				widths[i]   = margin[payments-1];
				widths[i+1] = paymentWidth[payments-1] - widths[i] ;
				System.out.println("0 "+widths[i-0]+" i :"+i+" wi: "+widths[i]+" wi+1: "+widths[i+1]);
			}
		}
				
		// Create grid
		paymentsGrid = new Grid(paymentsHeights.length, paymentsHeights, widths.length, widths);
		paymentsGrid.setLineVerticalAlignment(0, Dimension.ROW, VerticalAlignment.TOP);
		paymentsGrid.setLineVerticalAlignment(1, Dimension.ROW, VerticalAlignment.TOP);
		paymentsGrid.setLineVerticalAlignment(2, Dimension.ROW, VerticalAlignment.BOTTOM);
		paymentsGrid.setLineHorizontalAlignment(0, Dimension.COLUMN, HorizontalAlignment.LEFT);
		paymentsGrid.setLineBorder(0, Position.TOP, Color.BLACK, borderWidth);
		paymentsGrid.setLineMargin(0, Dimension.ROW, Direction.TOP, 0f/72f);
		paymentsGrid.setLineShade(0, Dimension.ROW, Color.LIGHT_GRAY);
		paymentsGrid.setLineMargin(1, Dimension.ROW, Direction.TOP, hasMinMaxIO ? 0f/72f : 6f/72f);
		paymentsGrid.setLineBorder(1, Position.TOP, Color.BLACK, borderWidth);
		paymentsGrid.setLineBorder(2, Position.TOP, Color.BLACK, borderWidth);
		paymentsGrid.setLineBorder(2, Position.TOP, Color.BLACK, borderWidth);
		paymentsGrid.setLineMargin(2, Dimension.ROW, Direction.BOTTOM, 6f/72f); 
		
		if (payments == 1)
			paymentsGrid.setLineBorder(2*payments, Position.RIGHT, Color.BLACK);

		// Create overlay
		float overlayWidth[] = { paymentWidth[payments-1] };
		overlayGrid = new Grid(1, heights, payments, overlayWidth);
		overlayGrid.setLineVerticalAlignment(0, Dimension.ROW, VerticalAlignment.TOP);
		overlayGrid.setLineMargin(0, Dimension.ROW, Direction.TOP, 0f/72f);
		overlayGrid.setLineHorizontalAlignment(0, Dimension.ROW, HorizontalAlignment.CENTER);

		// Column headers
		paymentsGrid.getCell(0, 0).setForeground(new FormattedText("Payment Calculation", Text.SECTION_INFO)).setMargin(Direction.LEFT, leftIndent);
		col0 = new Region().append(new FormattedText("   Principal & Interest", Text.SECTION_TEXT)).append(halfLine);
		if (hasMinMaxIO)
			col0.append(ioLine).append(fullLine);
		else if (hasMinMax)
			col0.append(fullLine);
		else if (hasIO)
			col0.append(ioLine);
		col0.append(miLine)
			.append(new FormattedText("   Mortgage Insurance", Text.SECTION_TEXT))
			.append(diffLine)
			.append(esLine)
			.append(new FormattedText("   Estimated Escrow", Text.SECTION_TEXT))
			.append(new FormattedText("    Amount can increase over time", Text.TABLE_OBLIQUE));
		paymentsGrid.getCell(1, 0).setForeground(col0).setMargin(Direction.LEFT, leftIndent);

		String frequencyType = "";
		if (paymentsList.size() != 0)
			frequencyType = paymentsList.get(0).getFrequencyType();
		col0 = new Region()
			.append(new FormattedText("   Estimated Total", Text.SECTION_INFO))
			.append(new LineFeed(-2f/72f))
			.append(new FormattedText("   " + frequencyType + " Payment", Text.SECTION_INFO));
		paymentsGrid.setLineVerticalAlignment(2, Dimension.ROW, VerticalAlignment.MIDDLE);
		paymentsGrid.getCell(2, 0).setForeground(col0).setMargin(Direction.LEFT, leftIndent);

		// Print amounts
		int column = 1;
		
		for (ProjectedPayments paymentLocal:paymentsList) {
			Region col1, col2;
			paymentsGrid.setLineBorder(column, Position.LEFT, Color.BLACK);
			paymentsGrid.setLineHorizontalAlignment(column, Dimension.COLUMN, HorizontalAlignment.RIGHT);
			paymentsGrid.setLineHorizontalAlignment(column+1, Dimension.COLUMN, HorizontalAlignment.CENTER);
			paymentsGrid.setLineMargin(column+1, Dimension.COLUMN, Direction.RIGHT, margin[payments-1]);
			
			// Get currency formatter
			StringFormatter format = StringFormatter.TRUNCDOLLARS;
			if (column == 1 || paymentLocal.getPrincipalAndInterestMinimumPaymentAmount().equals(""))
				format = StringFormatter.DOLLARS;
			
			// Print date range
			if (paymentLocal.getCalculationPeriodTermType().equalsIgnoreCase("FinalPayment")){
				overlayGrid.getCell(0, (column-1)/2).setForeground(new FormattedText("Final Payment", Text.SECTION_INFO));
			} else {
				String startYear = paymentLocal.getCalculationPeriodStartNumber();
				String endYear = paymentLocal.getCalculationPeriodEndNumber();
				if (startYear.equals(endYear))
					overlayGrid.getCell(0, (column-1)/2).setForeground(new FormattedText("Year " + startYear, Text.SECTION_INFO));
				else
					overlayGrid.getCell(0, (column-1)/2).setForeground(new FormattedText("Years " + startYear + "-" + endYear, Text.SECTION_INFO));
			}
			
			//5.2 Principal and Interest
			boolean interestOnly = false;
			String monthscount  = closingMap.getClosingMapValue("INTEREST_ONLY.InterestOnlyTermMonthsCount");
			int interestOnlyTermMonthsCount = 0;
			if(null != monthscount && !"".equalsIgnoreCase(monthscount)){
				interestOnlyTermMonthsCount = Integer.parseInt(monthscount);
			}
			
			// Calculate interest only flag
			interestOnly = paymentLocal.getInterestOnlyIndicator();
			if (!interestOnly) {
				try {
					int startYear = Integer.parseInt(paymentLocal.getCalculationPeriodStartNumber());
		            if((startYear-1)*12 < interestOnlyTermMonthsCount && closingMap.getClosingMapValue("LOAN_DETAIL.InterestOnlyIndicator").equalsIgnoreCase("true")) 
		                interestOnly = true;
				} catch (Exception e) {
					// do nothing
				}
			}
            
			col1 = new Region();
			col2 = new Region();
			String min = format.formatString(paymentLocal.getPrincipalAndInterestMinimumPaymentAmount());
			String max =format.formatString(paymentLocal.getPrincipalAndInterestMaximumPaymentAmount());
			float decXLoc = 0;
			try {
				if (max.equals("$0")) {
					max = min;
					min = "$0";
				}
				decXLoc = decimalLocation(page, "  " + max, Text.SECTION_TEXT);
				if (!min.equals("$0")) {
					col1.append(fullLine).append(fullLine);
					col2.append(new Paragraph()
					    	.append(new Spacer(decXLoc - decimalLocation(page, min, Text.SECTION_TEXT), 0))
					    	.append(new FormattedText(min + " min", Text.SECTION_TEXT)))
					    .append(new Paragraph()
					    	.append(new Spacer(decXLoc - decimalLocation(page, max, Text.SECTION_TEXT), 0))
					    	.append(new FormattedText(max + " max", Text.SECTION_TEXT)));
					if (interestOnly) {
						col1.append(ioLine);
						col2.append(new FormattedText("only interest", ONLY_INTEREST));
					} else if (hasMinMaxIO) {
						col1.append(ioLine);
						col2.append(ioLine);
					}
				} else {
					col1.append(fullLine);
					col2.append(new Paragraph()
                            .append(new Spacer(decXLoc - decimalLocation(page, max, Text.SECTION_TEXT), 0))
                            .append(new FormattedText(max, Text.SECTION_TEXT)));
					//col2.append(new FormattedText(max, Text.SECTION_TEXT));
					if (interestOnly) {
						col1.append(ioLine);
						col2.append(new FormattedText("only interest", ONLY_INTEREST));
						if (hasMinMaxIO) {
							col1.append(fullLine);
							col2.append(fullLine);
						} else if (hasMinMax) {
							col1.append(diffLine);
							col2.append(diffLine);
						}
					} else if (hasMinMaxIO) {
						col1.append(ioLine).append(fullLine);
						col2.append(ioLine).append(fullLine);
					} else if (hasMinMax) {
						col1.append(fullLine);
						col2.append(fullLine);
					} else if (hasIO) {
						col1.append(ioLine);
						col2.append(ioLine);
					}
				}
			} catch (Exception e) {	
			}
			
			//5.3 (MI)
			col1.append(halfLine).append(miLine).append(new FormattedText("+", Text.SECTION_TEXT));
			col2.append(halfLine).append(miLine);
			float value = paymentLocal.getProjectedPaymentMIPaymentAmount().equals("") ? 0.0f : Float.parseFloat(paymentLocal.getProjectedPaymentMIPaymentAmount());
			String valueStr = "";
			if (value != 0)
				everMI = true;
			if (everMI && value == 0)
				valueStr = "-----";
			else {
				if (value == 0)
					valueStr = "0";
				else
					valueStr = StringFormatter.NODOLLARS.formatString(paymentLocal.getProjectedPaymentMIPaymentAmount());
			}
			try {
				col2.append(new Paragraph()
			    	.append(new Spacer(decXLoc - decimalLocation(page, valueStr, Text.SECTION_TEXT), 0))
			    	.append(new FormattedText(valueStr, Text.SECTION_TEXT)));
			}
			catch (Exception e) {				
			}
			
			//5.4 (Escrow)
			col1.append(diffLine).append(esLine).append(new FormattedText("+",Text.SECTION_TEXT));
			col2.append(diffLine).append(esLine);
			value = paymentLocal.getEstimatedEscrowPaymentAmount().equals("") ? 0.0f : Float.parseFloat(paymentLocal.getEstimatedEscrowPaymentAmount());
			valueStr = "";
			if (value != 0)
				everEscrow = true;
			if (everEscrow && value == 0)
				valueStr = "-----";
			else {
				if (value == 0)
					valueStr = "0";
				else
					valueStr = StringFormatter.NODOLLARS.formatString(paymentLocal.getEstimatedEscrowPaymentAmount());
			}
			try {
				col2.append(new Paragraph()
			    	.append(new Spacer(decXLoc - decimalLocation(page, valueStr, Text.SECTION_TEXT), 0))
			    	.append(new FormattedText(valueStr, Text.SECTION_TEXT)));
			}
			catch (Exception e) {				
			}
			
			//Paste in grid
			paymentsGrid.setCellText(1, column, col1);
			paymentsGrid.setCellText(1, column+1, col2);

			//5.5+9
			min = format.formatString(paymentLocal.getEstimatedTotalMinimumPaymentAmount());
			max = format.formatString(paymentLocal.getEstimatedTotalMaximumPaymentAmount());
			if (max.equals("$0")) {
				max = min;
				min = "$0";
			}
			col2 = new Region();
			if (!min.equals("$0")) {
				String str = format.formatString(paymentLocal.getEstimatedTotalMinimumPaymentAmount())
					+ " - " + format.formatString(paymentLocal.getEstimatedTotalMaximumPaymentAmount());
				col2.append(new FormattedText(str, TEXT_AMOUNT));
			} else {
				col2.append(new FormattedText(format.formatString(paymentLocal.getEstimatedTotalMaximumPaymentAmount()), TEXT_AMOUNT));
			}
			
			//Paste in grid
			paymentsGrid.getCell(2, column+1).setMargin(Direction.BOTTOM, 10f/72f).setForeground(col2);
			column = column + 2;
		}
	}

	private void initializeEstimatesGrid(Page page, InputData inputData) {
		ClosingMap closingMap = inputData.getClosingMap();
		final float amtWidth = 1.5f - (inputData.isDocsDirect() ? .15f : 0);
		final float incWidth = 7.5f - ProjectedPaymentsSection.col1Width - amtWidth;
		float txtWidth = 2.1f;
		
		// Count escrows
		int countPIYes = 0 ;
		int countPINo = 0 ;
		int countPISome = 0;
		int countHIYes = 0 ;
		int countHINo = 0 ;
		int countHISome = 0;
		int countOtherYes = 0 ;
		int countOtherNo = 0 ;
		int countOtherSome = 0;
		String otherStr = "";
		List<PropertyCostComponents> costList = inputData.getPropertyCostList();
		for (PropertyCostComponents costLocal:costList) {
			switch (costLocal.getEstimatedTaxesInsuranceAssessmentComponentType()) {
			case "PropertyTaxes":
				if ("Escrowed".equals(costLocal.getEscrowedType()))
					countPIYes++;
				else if ("NotEscrowed".equals(costLocal.getEscrowedType()))
					countPINo++;
				else if ("SomeEscrowed".equals(costLocal.getEscrowedType()))
					countPISome++;
				break;
			case "HomeownersInsurance":
				if ("Escrowed".equals(costLocal.getEscrowedType()))
					countHIYes++;
				else if ("NotEscrowed".equals(costLocal.getEscrowedType()))
					countHINo++;
				else if ("SomeEscrowed".equals(costLocal.getEscrowedType()))
					countHISome++;
				break;
			default: // All other escrows go here
			    if("".equalsIgnoreCase(otherStr))
			    	otherStr = StringFormatter.STRINGCLEAN.formatString(costLocal.getLabel());
				if (!inputData.isDocsDirect() && otherStr.length() > 35)
					otherStr = otherStr.substring(0, 35);
				if ("Escrowed".equals(costLocal.getEscrowedType()))
					countOtherYes++;
				else if ("NotEscrowed".equals(costLocal.getEscrowedType()))
					countOtherNo++;
				else if ("SomeEscrowed".equals(costLocal.getEscrowedType()))
					countOtherSome++;
				break;
			}
		}
		
		// Check property costs that have been included
		Paragraph propertyTaxes = new Paragraph()
			.append(hasCost(countPIYes, countPINo, countPISome) ? BoxedCharacter.CHECK_BOX_NO : BoxedCharacter.CHECK_BOX_EMPTY)
			.append(new FormattedText(" Property Taxes", ESC_TYPE_TEXT));
		Paragraph insurance = new Paragraph()
			.append(hasCost(countHIYes, countHINo, countHISome) ? BoxedCharacter.CHECK_BOX_NO : BoxedCharacter.CHECK_BOX_EMPTY)
			.append(new FormattedText(" Homeowners Insurance", ESC_TYPE_TEXT));
		Paragraph other = new Paragraph()
				.append(hasCost(countOtherYes, countOtherNo, countOtherSome) ? BoxedCharacter.CHECK_BOX_NO : BoxedCharacter.CHECK_BOX_EMPTY)
				.append(new FormattedText(" Other: " + otherStr, ESC_TYPE_TEXT));
			
		// Determine if extra space is needed for "Other text"
		try {
			if (other.width(page) > txtWidth)
				txtWidth = other.width(page);
			if (inputData.isDocsDirect() && txtWidth > 3.5f)
				txtWidth = 3.5f;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Build included grid
		txtWidth += .2f;
		float incHeights[] = { Grid.DYNAMIC };
		float incWidths[] = { txtWidth, incWidth - txtWidth - 0.225f };
		Grid included = new Grid(incHeights.length, incHeights, incWidths.length, incWidths);
		Region col1 = new Region().append(new FormattedText("This estimate includes", ESC_TEXT_BOLD))
			.append(propertyTaxes)
			.append(insurance)
			.append(other)
			.append(new LineFeed(2f/72f))
			.append(new FormattedText("See Escrow Account on page 4 for details. You must pay for other property", ESCROW_TEXT_OBLIQUE))
			.append(new LineFeed(-2f/72f))
			.append(new FormattedText("costs separately.", ESCROW_TEXT_OBLIQUE));
		Region col2 = new Region().append(new FormattedText("In escrow?", ESC_TEXT_BOLD))
			.append(new FormattedText(inEscrowText(countPIYes, countPINo, countPISome), ESC_TEXT_BOLD))
			.append(new FormattedText(inEscrowText(countHIYes, countHINo, countHISome), ESC_TEXT_BOLD))
			.append(new FormattedText(inEscrowText(countOtherYes, countOtherNo, countOtherSome), ESC_TEXT_BOLD));
		included.setLineVerticalAlignment(0, Dimension.ROW, VerticalAlignment.TOP);
		included.getCell(0, 0).setForeground(col1).setMargin(Direction.LEFT, 2.5f/72f);
		included.getCell(0, 1).setForeground(col2);
		
		// Insert estimates grid column 1 text
		col1 = new Region()
			.append(new FormattedText("Estimated Taxes, Insurance", Text.SECTION_INFO))
			.append(new LineFeed(-2f/72f))
			.append(new FormattedText("& Assessments", Text.SECTION_INFO))
			.append(new FormattedText("Amount can increase over time", Text.TABLE_OBLIQUE))
			.append(new FormattedText("See page 4 for details", Text.TABLE_OBLIQUE));
		
		// Insert estimates grid column 2 text
		col2 = new Region()
			.append(new FormattedText(StringFormatter.DOLLARS.formatString(
					closingMap.getClosingMapValue("ESTIMATED_PROPERTY_COST_DETAIL.ProjectedPaymentEstimatedTaxesInsuranceAssessmentTotalAmount")),
					TEXT_AMOUNT_LARGE));

		// Translate enumerations to english
		String str ="";
		switch (closingMap.getClosingMapValue("PROJECTED_PAYMENT.PaymentFrequencyType")){
		case "Monthly":
			str = "a month";
			break;
		case "AtMaturity":
			str = "at maturity";
			break;
		case "Biweekly":
			str = "a bi week";
			break;
		case "Quarterly":
			str = "a quarter";
			break;
		case "Semiannual":
			str = "a semiannual";
			break;
		case "Semimonthly":
			str = "a semimonth";
			break;
		case "Weekly":
			str = "a week";
		}
		col2.append(new FormattedText( str, Text.SECTION_TEXT));
		
		// Build estimates grid
		float heights[] = { 1.2f };
		float widths[] = { col1Width, amtWidth, incWidth };
		estimatesGrid = new Grid(heights.length, heights, widths.length, widths);
		estimatesGrid.setLineBorder(0, Position.TOP, Color.BLACK, borderWidth);
		estimatesGrid.setLineBorder(1, Position.LEFT, Color.BLACK);
		estimatesGrid.setLineVerticalAlignment(0, Dimension.ROW, VerticalAlignment.MIDDLE);
		estimatesGrid.setLineMargin(1, Dimension.COLUMN, Direction.LEFT, 0.2f);
		estimatesGrid.getCell(0, 0).setForeground(col1).setMargin(Direction.LEFT, leftIndent);
		estimatesGrid.setCellText(0, 1, col2);
		estimatesGrid.setCellText(0, 2, included);
		estimatesGrid.setLineBorder(0, Position.BOTTOM, Color.BLACK, borderWidth);
	}

	private boolean hasCost(int countYes, int countNo, int countSome) {
		return countYes > 0 || countNo > 0 || countSome > 0;
	}
	
	private String inEscrowText(int countYes, int countNo, int countSome) {
		if (countSome > 0 || countYes > 0 && countNo > 0)
			return "SOME";
		if (countYes > 0)
			return "YES";
		if (countNo > 0)
			return "NO";
		return "";
	}
	
	public void draw(Page page, Object d) throws IOException {
		InputData data = (InputData)d;
		initializeTitleGrid();
		initializePaymentsGrid(page, data);
		initializeEstimatesGrid(page, data);
		float location = page.bottomMargin + 4.8f;
		titleGrid.draw(page, page.leftMargin, location);
		paymentsGrid.draw(page, page.leftMargin, location - paymentsGrid.height(page));
		overlayGrid.draw(page, page.leftMargin + col1Width, location - overlayGrid.height(page));
		estimatesGrid.draw(page, page.leftMargin, location - paymentsGrid.height(page) - estimatesGrid.height(page));
	}

	public float height(Page page) throws IOException {
		return titleGrid.height(page) + paymentsGrid.height(page) + estimatesGrid.height(page);
	}
	
	
	private ProjectedPayments findIO(List<ProjectedPayments> payments) {
		for (ProjectedPayments payment : payments)
			if (payment.getInterestOnlyIndicator())
				return payment;
		return null;
	}

	private ProjectedPayments findMinMax(List<ProjectedPayments> payments) {
		for (ProjectedPayments payment : payments)
			if (!payment.getPrincipalAndInterestMinimumPaymentAmount().equals(""))
				return payment;
		return null;
	}
	
	private ProjectedPayments findMinMaxIO(List<ProjectedPayments> payments) {
		for (ProjectedPayments payment : payments)
			if (!payment.getPrincipalAndInterestMinimumPaymentAmount().equals("") && payment.getInterestOnlyIndicator())
				return payment;
		return null;
	}
	
	private float decimalLocation(Page page, String amountStr, Text format) throws IOException {
		int idx = amountStr.indexOf('.');
		if (idx != -1)
		    amountStr = amountStr.substring(0, idx);
		FormattedText fText = new FormattedText(amountStr, format);
		return fText.width(page);
	}
}