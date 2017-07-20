package com.actualize.mortgage.domainmodels;

import java.io.IOException;
import java.util.List;

import com.actualize.mortgage.datalayer.ClosingMap;
import com.actualize.mortgage.datalayer.InputData;
import com.actualize.mortgage.datalayer.InterestRule;
import com.actualize.mortgage.datalayer.PIadjustments;
import com.actualize.mortgage.pdfbuilder.Bullet;
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

public class LoanTermsSection implements Section {
	private static final Text TAB         = new Text(Color.WHITE, 11, Typeface.CALIBRI_BOLD);
	//private static final Text TEXT_AMOUNT = new Text(Color.BLACK, 16, Typeface.CALIBRI);
	private Text TEXT_AMOUNT = null;
	
	Grid titleGrid, dataGrid;
	private final float borderWidth = 1f/72f;
	private final float topMargin   = 2f/72f;
	private final float col1Width   = 1.85f;
	private final float col2Width   = 1.55f;
	private final float col3Width   = 1.0f;
	private final float col4Width   = 7.5f - col1Width - col2Width - col3Width;
	private Region info = new Region();

	private static final float leftIndent  = 2f/72f;

	public static final Text TABLE_TEXT_BOLD    = new Text(Color.BLACK, 10, Typeface.CALIBRI_BOLD);
	public static final Text TABLE_TEXT  = new Text(Color.BLACK, 10, Typeface.CALIBRI);
	
	private void initializeTitleGrid() {
		float heights[] = { 22f/72f };
		float widths[] = { col1Width + col2Width, 4.1f};
		titleGrid = new Grid(1, heights, 2, widths);
		titleGrid.setLineHorizontalAlignment(0, Dimension.ROW, HorizontalAlignment.CENTER);
		titleGrid.getCell(0, 0)
			.setHorizontalAlignment(HorizontalAlignment.LEFT)
			.setMargin(Direction.LEFT, leftIndent)
			.setBackground(new Tab(col1Width))
			.setForeground(new FormattedText("Loan Terms", TAB));
		titleGrid.setLineShade(0, Dimension.ROW, Color.LIGHT_GRAY);
		titleGrid.setCellShade(0, 1, Color.LIGHT_GRAY);
		titleGrid.setLineHorizontalAlignment(0, Dimension.ROW, HorizontalAlignment.LEFT);
		titleGrid.setCellForeground(0, 1, new FormattedText("Can this amount increase after closing?", Text.SECTION_INFO));
		titleGrid.setLineBorder(0, Position.BOTTOM, Color.BLACK, borderWidth);
		titleGrid.setLineMargin(1, Dimension.COLUMN, Direction.LEFT, 0.1f);
	}
	
	private void initializeDataGrid(InputData inputData, Page page) throws IOException {
		ClosingMap closingMap = inputData.getClosingMap();
		List<InterestRule> interestRuleList = inputData.getInterestRuleList();
		List<PIadjustments> piAdjustmentList = inputData.getPiAdjustmentsList();
		// different font sizes for DD and USBank
		if (inputData.isDocsDirect()){
			TEXT_AMOUNT = new Text(Color.BLACK, 14, Typeface.CALIBRI);
		} else {
			TEXT_AMOUNT = new Text(Color.BLACK, 14, Typeface.CALIBRI);
		}
		
		float interestRow = 23f/72f;
		if (closingMap.getClosingMapValue("LOAN_DETAIL.InterestRateIncreaseIndicator").equalsIgnoreCase("true")){
			interestRow = 40f/72f;
		}
		
		// Create grid with set widths and dynamic height
		float heights[] = { 25f/72f, interestRow, 52f/72f, 20f/72f, 26f/72f, 19f/72f };
		float widths[]  = { col1Width, col2Width, col3Width, col4Width };
		dataGrid = new Grid(heights.length, heights, widths.length, widths);	
		dataGrid.setLineBorder(1, Position.LEFT, Color.BLACK, borderWidth);
		dataGrid.setLineMargin(1, Dimension.COLUMN, Direction.LEFT, 0.2f);
		dataGrid.setLineMargin(2, Dimension.COLUMN, Direction.LEFT, 0.1f);
				
		//4.1
		dataGrid.setLineBorder(0, Position.TOP, Color.BLACK, borderWidth);
		dataGrid.setLineVerticalAlignment(0, Dimension.ROW, VerticalAlignment.TOP);
		dataGrid.setLineMargin(0, Dimension.ROW, Direction.TOP, topMargin);
		dataGrid.getCell(0, 0).setForeground(new FormattedText("Loan Amount", Text.SECTION_INFO)).setMargin(Direction.LEFT, leftIndent).setMargin(Direction.TOP, 0f/72f);
		dataGrid.getCell(0, 1).setMargin(Direction.TOP, 0f/72f);
		dataGrid.setCellText(0, 1, new FormattedText(StringFormatter.ZEROTRUNCDOLLARS.formatString(
				closingMap.getClosingMapValue("TERMS_OF_LOAN.NoteAmount")), TEXT_AMOUNT));
		//NEG AM
		dataGrid.getCell(0, 2).setMargin(Direction.TOP, -1.5f/72f);
		if (closingMap.getClosingMapValue("LOAN_DETAIL.NegativeAmortizationIndicator").equalsIgnoreCase("true")) {
			String text4_1_2 = "Can go as high as ";
			String text4_1_3 = "Can increase until year ";
			if (closingMap.getClosingMapValue("LOAN_DETAIL.LoanAmountIncreaseIndicator").equalsIgnoreCase("true")) {
				if (closingMap.getClosingMapValue("NEGATIVE_AMORTIZATION_RULE.NegativeAmortizationType").equalsIgnoreCase("ScheduledNegativeAmortization")) {
					text4_1_2 = "Does go as high as ";
					text4_1_3 = "Does increase until year ";
				}
			}
			Paragraph para4_2 = (new Paragraph())
					.append(Bullet.BULLET)
					.append(new FormattedText(text4_1_2,TABLE_TEXT))
					.append(new FormattedText( 
							StringFormatter.TRUNCDOLLARS.formatString(closingMap.getClosingMapValue("NEGATIVE_AMORTIZATION_RULE.NegativeAmortizationMaximumLoanBalanceAmount")),
							TABLE_TEXT_BOLD));
			Paragraph para4_3= (new Paragraph())
					.append(Bullet.BULLET)
					.append(new FormattedText(text4_1_3,TABLE_TEXT))
					.append(new FormattedText( 
							StringFormatter.ROUNDUPYEAR.formatString(closingMap.getClosingMapValue("NEGATIVE_AMORTIZATION_RULE.NegativeAmortizationLimitMonthsCount")),
							TABLE_TEXT_BOLD));
			info = (new Region())
					.append(para4_2)
					.append(para4_3);
			dataGrid.setCellText(0, 2, new FormattedText("YES",Text.TABLE_HEADER_XLRG));
			dataGrid.getCell(0, 3).setForeground(info).setMargin(Direction.LEFT, -35f/72f).setMargin(Direction.TOP, 0f/72f);
		} else {
			dataGrid.setCellText(0, 2, new FormattedText("NO",Text.TABLE_HEADER_XLRG));
			dataGrid.getCell(0, 2).setMargin(Direction.TOP, -0.7f/72f);
		}
		
		//4.2
		dataGrid.setLineBorder(1, Position.TOP, Color.BLACK, borderWidth);
		dataGrid.setLineVerticalAlignment(1, Dimension.ROW, VerticalAlignment.TOP);
		dataGrid.setLineMargin(1, Dimension.ROW, Direction.TOP, topMargin);
		dataGrid.getCell(1, 0).setForeground(new FormattedText("Interest Rate", Text.SECTION_INFO)).setMargin(Direction.LEFT, leftIndent);
		String text4_2;
		if (closingMap.getClosingMapValue("BUYDOWN.BuydownReflectedInNoteIndicator").equalsIgnoreCase("true") 
		        && !closingMap.getClosingMapValue("BUYDOWN.BuydownInitialEffectiveInterestRatePercent").equals("")) {
			text4_2 = closingMap.getClosingMapValue("BUYDOWN.BuydownInitialEffectiveInterestRatePercent");
		} else if(!closingMap.getClosingMapValue("TERMS_OF_LOAN.DisclosedFullyIndexedRatePercent").equals("")) {
			text4_2 = closingMap.getClosingMapValue("TERMS_OF_LOAN.DisclosedFullyIndexedRatePercent");
		} else if (!closingMap.getClosingMapValue("TERMS_OF_LOAN.WeightedAverageInterestRatePercent").equals("")) {
			text4_2 = closingMap.getClosingMapValue("TERMS_OF_LOAN.WeightedAverageInterestRatePercent");
		} else {
			text4_2 = closingMap.getClosingMapValue("TERMS_OF_LOAN.NoteRatePercent");
		}
		dataGrid.getCell(1, 1).setMargin(Direction.TOP, 0.0f);
		dataGrid.setCellText(1, 1, new FormattedText(StringFormatter.PERCENT.formatString(text4_2), TEXT_AMOUNT));
		
		//Adjustable Rate AIR message
		if (closingMap.getClosingMapValue("LOAN_DETAIL.InterestRateIncreaseIndicator").equalsIgnoreCase("true")) {
			dataGrid.setCellText(1, 2, new FormattedText("YES",Text.TABLE_HEADER_XLRG));
			dataGrid.getCell(1, 2).setMargin(Direction.TOP, 1f/72f);
			String text4_2_2 = "";
			for (InterestRule interestRuleLocal:interestRuleList) {
				if (inputData.isDocsDirect()) {
					text4_2_2 = interestRuleLocal.getPerChangeRateAdjustmentFrequencyMonthsCount();
					if (!"".equals(text4_2_2))
						break;
				} else {
					if ("First".equalsIgnoreCase(interestRuleLocal.getAdjustmentRuleType())) {
						text4_2_2 = interestRuleLocal.getPerChangeRateAdjustmentFrequencyMonthsCount();
						break;
					}
				}
			}
			String years = StringFormatter.YEARS.formatString(text4_2_2);
			Paragraph text4_2_1 = (new Paragraph())
					.append(Bullet.BULLET)
					.append(new FormattedText("Adjusts ", TABLE_TEXT))
					.append(new FormattedText("every " + ("1".equals(years) ? "year" : (years + " years")), TABLE_TEXT_BOLD))
					.append(new FormattedText(" starting in ", TABLE_TEXT))
					.append(new FormattedText("year " + StringFormatter.ROUNDUPPLUSONEYEAR.formatString(closingMap.getClosingMapValue("INTEREST_RATE_LIFETIME_ADJUSTMENT_RULE.FirstRateChangeMonthsCount")),
							TABLE_TEXT));
			Paragraph text4_2_3 = (new Paragraph())
					.append(Bullet.BULLET)
					.append(new FormattedText("Can go ", TABLE_TEXT))
					.append(new FormattedText("as high as " + StringFormatter.PERCENT.formatString(closingMap.getClosingMapValue("INTEREST_RATE_LIFETIME_ADJUSTMENT_RULE.CeilingRatePercent")),
							TABLE_TEXT_BOLD))
					.append(new FormattedText(" in ", TABLE_TEXT))
					.append(new FormattedText("year " + StringFormatter.ROUNDUPYEARS.formatString(closingMap.getClosingMapValue("INTEREST_RATE_LIFETIME_ADJUSTMENT_RULE.CeilingRatePercentEarliestEffectiveMonthsCount")),
							TABLE_TEXT));
			Paragraph text4_2_4 = (new Paragraph())
					.append(Bullet.BULLET)
					.append(new FormattedText("See ", TABLE_TEXT))
					.append(new FormattedText("AIR Table on page 4", TABLE_TEXT_BOLD))
					.append(new FormattedText(" for details", TABLE_TEXT));
			info = new Region()
				.append(text4_2_1)
				.append(text4_2_3)
				.append(text4_2_4);
			dataGrid.getCell(1, 3).setForeground(info).setMargin(Direction.LEFT, -35f/72f).setMargin(Direction.TOP, 2f/72f);
		} else {
			dataGrid.setCellText(1, 2, new FormattedText("NO",Text.TABLE_HEADER_XLRG));
			dataGrid.getCell(1, 2).setMargin(Direction.TOP, 1f/72f);
		}
		
		//4.3
		dataGrid.setLineBorder(2, Position.TOP, Color.BLACK, borderWidth);
		dataGrid.setLineVerticalAlignment(2, Dimension.ROW, VerticalAlignment.TOP);
		dataGrid.setLineMargin(2, Dimension.ROW, Direction.TOP, topMargin);
		Region para = new Region();
		String text4_3 = "";
		if (!closingMap.getClosingMapValue("PAYMENT_RULE.PaymentFrequencyType").equalsIgnoreCase("")){
			text4_3 = closingMap.getClosingMapValue("PAYMENT_RULE.PaymentFrequencyType")+" ";
		}
		text4_3 += "Principal & Interest ";
		para.append(new FormattedText(text4_3, Text.SECTION_INFO))
			.append(new LineFeed(2f/72f))
			.append(new FormattedText("See projected payments below for your", Text.TABLE_OBLIQUE))
			.append(new LineFeed(0))
			.append(new FormattedText("Estimated Total Monthly Payment", Text.TABLE_OBLIQUE));
		dataGrid.getCell(2, 0).setForeground(para).setMargin(Direction.LEFT, leftIndent);
		dataGrid.getCell(2, 1).setMargin(Direction.TOP, 2f/72f);
		if (!closingMap.getClosingMapValue("PAYMENT_RULE.InitialPrincipalAndInterestPaymentAmount").equalsIgnoreCase(""))
			dataGrid.setCellText(2, 1, new FormattedText(StringFormatter.DOLLARS.formatString(
				closingMap.getClosingMapValue("PAYMENT_RULE.InitialPrincipalAndInterestPaymentAmount")), TEXT_AMOUNT));
		else
			dataGrid.setCellText(2, 1, new FormattedText(StringFormatter.DOLLARS.formatString(
					closingMap.getClosingMapValue("PAYMENT_RULE.FullyIndexedInitialPrincipalAndInterestPaymentAmount")), TEXT_AMOUNT));
		
		//PAYMENT
		if (closingMap.getClosingMapValue("LOAN_DETAIL.PaymentIncreaseIndicator").equalsIgnoreCase("true")){
			dataGrid.setCellText(2, 2, new FormattedText("YES",Text.TABLE_HEADER_XLRG));
			dataGrid.getCell(2, 2).setMargin(Direction.TOP, 1f/72f);
			// TODO need Fully indexed disclosed logic here
			// TODO need additional code here to test for scheduled
			String adjustment = "";
			for (PIadjustments piAdjustment:piAdjustmentList) {
				if (inputData.isDocsDirect()) {
					if ("Subsequent".equalsIgnoreCase(piAdjustment.getAdjustmentRuleType())) {
						adjustment = piAdjustment.getPerChangeAdjustmentFrequencyMonthsCount();
						break;
					}
				} else {
					if ("First".equalsIgnoreCase(piAdjustment.getAdjustmentRuleType())) {
						adjustment = piAdjustment.getPerChangeAdjustmentFrequencyMonthsCount();
						break;
					}
				}
			}
			para = new Region();
			if (closingMap.getClosingMapValue("LOAN_DETAIL.InterestRateIncreaseIndicator").equalsIgnoreCase("true")) {
				//if (!adjustment.equals("")) {
					String years = StringFormatter.YEARS.formatString(adjustment);
					Paragraph text4_3_1 = (new Paragraph())
							.append(Bullet.BULLET).append(new FormattedText("Adjusts ", TABLE_TEXT))
							.append(new FormattedText("every " + ("1".equals(years) ? "year" : (years + " years")), TABLE_TEXT_BOLD))
							.append(new FormattedText(" starting in ", TABLE_TEXT))
							.append(new FormattedText("year " + StringFormatter.ROUNDUPYEARS.formatString(
									closingMap.getClosingMapValue("PRINCIPAL_AND_INTEREST_PAYMENT_LIFETIME_ADJUSTMENT_RULE.FirstPrincipalAndInterestPaymentChangeMonthsCount")),
									TABLE_TEXT));
					para.append(text4_3_1);
				//}
				String maxAmount = closingMap.getClosingMapValue("PRINCIPAL_AND_INTEREST_PAYMENT_LIFETIME_ADJUSTMENT_RULE.PrincipalAndInterestPaymentMaximumAmount");
				if (!maxAmount.equals("")) {
					Paragraph text4_3_2 = (new Paragraph())
						.append(Bullet.BULLET)
						.append(new FormattedText("Can go ", TABLE_TEXT))
						.append(new FormattedText("as high as " + StringFormatter.TRUNCDOLLARS.formatString(maxAmount), TABLE_TEXT_BOLD))
						.append(new FormattedText(" in ", TABLE_TEXT))
						.append(new FormattedText("year " +
								StringFormatter.ROUNDUPYEARS.formatString(closingMap.getClosingMapValue("PRINCIPAL_AND_INTEREST_PAYMENT_LIFETIME_ADJUSTMENT_RULE.PrincipalAndInterestPaymentMaximumAmountEarliestEffectiveMonthsCount")),
								TABLE_TEXT));
					para.append(text4_3_2);
				}
			}
			if (closingMap.getClosingMapValue("LOAN_DETAIL.InterestOnlyIndicator").equalsIgnoreCase("true")){
				Paragraph text4_3_3 = (new Paragraph())
					.append(Bullet.BULLET)
					.append(new FormattedText("Includes ", TABLE_TEXT))
					.append(new FormattedText("only interest", TABLE_TEXT_BOLD))
					.append(new FormattedText(" and ", TABLE_TEXT))
					.append(new FormattedText("no principal", TABLE_TEXT_BOLD))
					.append(new FormattedText(" until " + StringFormatter.MONTHSORYEARS.formatString(closingMap.getClosingMapValue("INTEREST_ONLY.InterestOnlyTermMonthsCount")),
							TABLE_TEXT));
				Paragraph text4_3_4 = (new Paragraph())
					.append(Bullet.BULLET)
					.append(new FormattedText("See ", TABLE_TEXT))
					.append(new FormattedText("AP Table on page 4", TABLE_TEXT_BOLD))
					.append(new FormattedText(" for details", TABLE_TEXT));
				para.append(text4_3_3)
					.append(text4_3_4);
			}
			dataGrid.getCell(2, 3).setForeground(para).setMargin(Direction.LEFT, -35f/72f).setMargin(Direction.TOP, 2f/72f);
		} else {
			dataGrid.setCellText(2, 2, new FormattedText("NO", Text.TABLE_HEADER_XLRG));
			dataGrid.getCell(2, 2).setMargin(Direction.TOP, 2f/72f);
		}
	
		//4.4
		dataGrid.setLineBorder(3, Position.TOP, Color.BLACK, borderWidth);
		dataGrid.setLineVerticalAlignment(3, Dimension.ROW, VerticalAlignment.TOP);
		dataGrid.setLineMargin(3, Dimension.ROW, Direction.TOP, topMargin);
		dataGrid.setCellShade(3, 2, Color.LIGHT_GRAY);
		dataGrid.setCellShade(3, 3, Color.LIGHT_GRAY);
		dataGrid.setCellText(3, 2, new FormattedText("Does the loan have these features?", Text.SECTION_INFO));
		dataGrid.setLineVerticalAlignment(4, Dimension.ROW, VerticalAlignment.TOP);
		dataGrid.setLineMargin(4, Dimension.ROW, Direction.TOP, 0);
		dataGrid.getCell(4, 0).setForeground(new FormattedText("Prepayment Penalty", Text.SECTION_INFO)).setMargin(Direction.LEFT, leftIndent);
		
		if (closingMap.getClosingMapValue("LOAN_DETAIL.PrepaymentPenaltyIndicator").equalsIgnoreCase("true")){
			dataGrid.setCellText(4, 2, new FormattedText("YES",Text.TABLE_HEADER_XLRG));
			dataGrid.getCell(4, 2).setMargin(Direction.TOP, -1f/72f);
			Paragraph para4_4_1 = (new Paragraph())
					.append(Bullet.BULLET)
					.append(new FormattedText(
							"As high as " + StringFormatter.TRUNCDOLLARS.formatString(closingMap.getClosingMapValue("PREPAYMENT_PENALTY_LIFETIME_RULE.PrepaymentPenaltyMaximumLifeOfLoanAmount")),
							TABLE_TEXT_BOLD))
					.append(new FormattedText(" if you payoff the ", TABLE_TEXT));
			Paragraph para4_4_2 =(new Paragraph())
				.append(new Spacer(Bullet.BULLET.width(page), 0))
				.append(new FormattedText("loan during the ", TABLE_TEXT))
				.append(new FormattedText(
							"first " + StringFormatter.YEARS.formatString(closingMap.getClosingMapValue("PREPAYMENT_PENALTY_LIFETIME_RULE.PrepaymentPenaltyExpirationMonthsCount")) + " years",
							TABLE_TEXT_BOLD));
			info = (new Region())
				.append(para4_4_1)
				.append(para4_4_2);
			dataGrid.getCell(4, 3).setForeground(info).setMargin(Direction.LEFT, -35f/72f);
		} else {
			dataGrid.setCellText(4, 2, new FormattedText("NO",Text.TABLE_HEADER_XLRG));
			dataGrid.getCell(4, 2).setMargin(Direction.TOP, -1f/72f);
		}
		
		//4.5
		dataGrid.setLineBorder(5, Position.TOP, Color.BLACK, borderWidth);
		dataGrid.setLineVerticalAlignment(5, Dimension.ROW, VerticalAlignment.TOP);
		dataGrid.setLineMargin(5, Dimension.ROW, Direction.TOP, topMargin);
		dataGrid.getCell(5, 0).setForeground(new FormattedText("Balloon Payment", Text.SECTION_INFO)).setMargin(Direction.LEFT, leftIndent);
		
		//BALLOON
		if (closingMap.getClosingMapValue("LOAN_DETAIL.BalloonIndicator").equalsIgnoreCase("true")) {
			dataGrid.setCellText(5, 2, new FormattedText("YES",Text.TABLE_HEADER_XLRG));
			dataGrid.getCell(5, 2).setMargin(Direction.TOP, 1f/72f);
			Paragraph para4_5_1 = (new Paragraph())
					.append(Bullet.BULLET)
					.append(new FormattedText("You will have to pay ",TABLE_TEXT))
					.append(new FormattedText(
							StringFormatter.TRUNCDOLLARS.formatString(closingMap.getClosingMapValue("LOAN_DETAIL.BalloonPaymentAmount"))
							,TABLE_TEXT_BOLD))
					.append(new FormattedText(" at the end of " + closingMap.getClosingMapValue("MATURITY_RULE.LoanMaturityPeriodType").toLowerCase()
							+ " " + closingMap.getClosingMapValue("MATURITY_RULE.LoanMaturityPeriodCount"), TABLE_TEXT));
			dataGrid.getCell(5, 3).setForeground(para4_5_1).setMargin(Direction.LEFT, -35f/72f).setMargin(Direction.TOP, 2f/72f);
		} else {
			dataGrid.setCellText(5, 2, new FormattedText("NO",Text.TABLE_HEADER_XLRG));
			dataGrid.getCell(5, 2).setMargin(Direction.TOP, 1f/72f);
		}
		
		dataGrid.setLineBorder(5, Position.BOTTOM, Color.BLACK, borderWidth);
	}
	
	public void draw(Page page, Object d) throws IOException {
		InputData data = (InputData)d;
		initializeTitleGrid();
		initializeDataGrid(data, page);
		final float location = page.bottomMargin + 5.2f;
		titleGrid.draw(page, page.leftMargin, location + dataGrid.height(page));
		dataGrid.draw(page, page.leftMargin, location);
	}

	public float height(Page page) throws IOException {
		return titleGrid.height(page) + dataGrid.height(page);
	}

}
