package com.actualize.closingdisclosure.domainmodels;
import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;

import com.actualize.closingdisclosure.datalayer.ClosingMap;
import com.actualize.closingdisclosure.datalayer.Fees;
import com.actualize.closingdisclosure.datalayer.InputData;
import com.actualize.closingdisclosure.datalayer.SubjectProperty;
import com.actualize.closingdisclosure.pdfbuilder.BoxedCharacter;
import com.actualize.closingdisclosure.pdfbuilder.Color;
import com.actualize.closingdisclosure.pdfbuilder.FormattedText;
import com.actualize.closingdisclosure.pdfbuilder.Grid;
import com.actualize.closingdisclosure.pdfbuilder.LineFeed;
import com.actualize.closingdisclosure.pdfbuilder.Page;
import com.actualize.closingdisclosure.pdfbuilder.Paragraph;
import com.actualize.closingdisclosure.pdfbuilder.Region;
import com.actualize.closingdisclosure.pdfbuilder.Section;
import com.actualize.closingdisclosure.pdfbuilder.Text;
import com.actualize.closingdisclosure.pdfbuilder.Typeface;
import com.actualize.closingdisclosure.pdfbuilder.Grid.Dimension;
import com.actualize.closingdisclosure.pdfbuilder.Grid.Position;
import com.actualize.closingdisclosure.pdfbuilder.TextBox.Direction;
import com.actualize.closingdisclosure.pdfbuilder.TextBox.HorizontalAlignment;
import com.actualize.closingdisclosure.pdfbuilder.TextBox.VerticalAlignment;

public class LoanDisclosuresSection implements Section {
	public static final Text TITLE              = new Text(Color.BLACK, 14, Typeface.CALIBRI_BOLD);
	public static final Text TAB                = new Text(Color.WHITE, 11, Typeface.CALIBRI_BOLD);
	public static final Text TEXT_LARGE         = new Text(Color.BLACK, 10, Typeface.CALIBRI);
	public static final Text TEXT_BOLD          = new Text(Color.BLACK, 10, Typeface.CALIBRI_BOLD);
	public static final Text TEXT_BOLD_ITALICS  = new Text(Color.BLACK, 10, Typeface.CALIBRI_BOLD_OBLIQUE);
	public static final Text TEXT_ITALICS       = new Text(Color.BLACK, 9, Typeface.CALIBRI_OBLIQUE);
	public static final Text TEXT               = new Text(Color.BLACK, 9, Typeface.CALIBRI);
	public static final Text TABLE_TEXT         = new Text(Color.BLACK, 8, Typeface.CALIBRI);
	public static final Text TABLE_TEXT_ITALICS = new Text(Color.BLACK, 8, Typeface.CALIBRI_OBLIQUE);
	
	final float tdh[] = { Grid.DYNAMIC, 36f/72f };
	final float tdw[] = { 7.5f };
	private final Grid titleGrid = new Grid(tdh.length, tdh, tdw.length, tdw);
	private final Region col1 = new Region();
	private final Region col2 = new Region();

	private static final float leftIndent  = 2f/72f;

	public void initializeGrid(InputData inputData) {
		// Data used
		final ClosingMap closingMap = inputData.getClosingMap();
		final SubjectProperty subjectProperty = inputData.getSubjectProperty();
		List<Fees> feeList = inputData.getFeeList();
		
		// Key spacing and dimensions
		final float colWidth = 3.55f;
		final float headerPre  = 8f/72f;
		final float headerPost = 1f/72f;

		// Check-box grid parameters
		final float numWidth = 10f / 72f;
		final float height[] = { Grid.DYNAMIC };
		final float width[] = { numWidth, colWidth-numWidth };
		
		// Reused variables
		Grid grid;
		Paragraph para;

		// Set title
		titleGrid.setCellText(0, 0, new FormattedText("Additional Information About This Loan", TITLE));
		titleGrid.setLineBorder(1, Position.TOP, Color.BLACK, 2f/72f);
		titleGrid.getCell(1, 0)
			.setBackground(new Tab())
			.setMargin(Direction.LEFT, leftIndent)
			.setForeground(new FormattedText("Loan Disclosures", TAB));
		titleGrid.setLineBorder(1, Position.BOTTOM, Color.BLACK);
		
		// Assumption (column 1) 
		//17.1
		grid = new Grid(2, height, 2, width);
		grid.setLineMargin(1, Dimension.COLUMN, Direction.BOTTOM, 0);
		if (closingMap.getClosingMapValue("LOAN_DETAIL.AssumabilityIndicator").equals("true")) {
			grid.getCell(0, 0).setVerticalAlignment(VerticalAlignment.TOP).setMargin(Direction.TOP, 4f/72f).setMargin(Direction.BOTTOM, -4f/72f)
				.setForeground(BoxedCharacter.CHECK_BOX_NO); 
			grid.getCell(1, 0).setVerticalAlignment(VerticalAlignment.TOP).setMargin(Direction.TOP, 4f/72f).setMargin(Direction.BOTTOM, -4f/72f)
				.setForeground(BoxedCharacter.CHECK_BOX_EMPTY); 
		} else {
			grid.getCell(0, 0).setVerticalAlignment(VerticalAlignment.TOP).setMargin(Direction.TOP, 4f/72f).setMargin(Direction.BOTTOM, -4f/72f)
				.setForeground(BoxedCharacter.CHECK_BOX_EMPTY); 
			grid.getCell(1, 0).setVerticalAlignment(VerticalAlignment.TOP).setMargin(Direction.TOP, 4f/72f).setMargin(Direction.BOTTOM, -4f/72f)
				.setForeground(BoxedCharacter.CHECK_BOX_NO); 
		}
		grid.getCell(0, 1).setWrap(true).setForeground(new FormattedText("will allow, under certain conditions, this person to assume this " +
				"loan on the original terms.", TEXT));
		grid.getCell(1, 1).setWrap(true).setForeground(new FormattedText("will not allow assumption of this loan on the original terms.",
				TEXT));
		col1.append(new LineFeed(headerPre))
			.append(new FormattedText("Assumption", TEXT_BOLD))
			.append(new LineFeed(headerPost))
			.append(new FormattedText("If you sell or transfer this property to another person, your lender", TEXT))
			.append(grid);
		
		//17.2
		// Demand Feature (column 1)
		grid = new Grid(2, height, 2, width);
		grid.setLineMargin(1, Dimension.COLUMN, Direction.BOTTOM, 0);
		if (closingMap.getClosingMapValue("LOAN_DETAIL.DemandFeatureIndicator").equals("true")) {
			grid.getCell(0, 0).setVerticalAlignment(VerticalAlignment.TOP).setMargin(Direction.TOP, 4f/72f).setMargin(Direction.BOTTOM, -4f/72f)
				.setForeground(BoxedCharacter.CHECK_BOX_NO); 
			grid.getCell(1, 0).setVerticalAlignment(VerticalAlignment.TOP).setMargin(Direction.TOP, 4f/72f).setMargin(Direction.BOTTOM, -4f/72f)
				.setForeground(BoxedCharacter.CHECK_BOX_EMPTY); 
		} else {
			grid.getCell(0, 0).setVerticalAlignment(VerticalAlignment.TOP).setMargin(Direction.TOP, 4f/72f).setMargin(Direction.BOTTOM, -4f/72f)
				.setForeground(BoxedCharacter.CHECK_BOX_EMPTY); 
			grid.getCell(1, 0).setVerticalAlignment(VerticalAlignment.TOP).setMargin(Direction.TOP, 4f/72f).setMargin(Direction.BOTTOM, -4f/72f)
				.setForeground(BoxedCharacter.CHECK_BOX_NO);
		}
		grid.getCell(0, 1).setWrap(true).setForeground(new FormattedText("has a demand feature, which permits your lender to require early " +
				"repayment of the loan. You should review your note for details.", TEXT));
		grid.getCell(1, 1).setWrap(true).setForeground(new FormattedText("does not have a demand feature.", TEXT));
		col1.append(new LineFeed(headerPre))
			.append(new FormattedText("Demand Feature", TEXT_BOLD))
			.append(new LineFeed(headerPost))
			.append(new FormattedText("Your loan", TEXT))
			.append(grid);
		
		//17.3
		para = new Paragraph();
		para.append(new FormattedText("If your payment is more than ", TEXT))
			.append(new FormattedText(closingMap.getClosingMapValue("LATE_CHARGE_RULE.LateChargeGracePeriodDaysCount"), TEXT_ITALICS));
		String lateChargeType = closingMap.getClosingMapValue("LATE_CHARGE_RULE.LateChargeType");
		if (lateChargeType.equalsIgnoreCase("NoLateCharges"))
			para.append(new FormattedText(" days late, your lender will not charge a late fee.", TEXT));
		else {
			String maximum = closingMap.getClosingMapValue("LATE_CHARGE_RULE.LateChargeMaximumAmount");
			String minimum = closingMap.getClosingMapValue("LATE_CHARGE_RULE.LateChargeMinimumAmount");
			para.append(new FormattedText(" days late, your lender will charge a late fee of ", TEXT));
			if (!lateChargeType.equalsIgnoreCase("FlatDollarAmount") && !closingMap.getClosingMapValue("LATE_CHARGE_RULE.LateChargeAmount").equals(""))
				para.append(new FormattedText(String.format("$%.2f or ", Double.valueOf(closingMap.getClosingMapValue("LATE_CHARGE_RULE.LateChargeAmount"))), TEXT_ITALICS));
			switch (lateChargeType) {
			case "FlatDollarAmount":
				para.append(new FormattedText(String.format("$%.2f", Double.valueOf(closingMap.getClosingMapValue("LATE_CHARGE_RULE.LateChargeAmount"))) + " per month", TEXT_ITALICS));
				break;
			case "PercentageOfDelinquentInterest":
				para.append(new FormattedText(StringFormatter.PERCENT.formatString(closingMap.getClosingMapValue("LATE_CHARGE_RULE.LateChargeRatePercent")) + " of the delinquent interest", TEXT_ITALICS));
				break;
			case "PercentOfPrincipalAndInterest":	
				para.append(new FormattedText(StringFormatter.PERCENT.formatString(closingMap.getClosingMapValue("LATE_CHARGE_RULE.LateChargeRatePercent")) + " of the monthly principal and interest payment", TEXT_ITALICS));
				break;
			case "PercentageOfPrincipalBalance":	
				para.append(new FormattedText(StringFormatter.PERCENT.formatString(closingMap.getClosingMapValue("LATE_CHARGE_RULE.LateChargeRatePercent")) + " of the principal balance", TEXT_ITALICS));
				break;
			case "PercentageOfNetPayment":
				para.append(new FormattedText(StringFormatter.PERCENT.formatString(closingMap.getClosingMapValue("LATE_CHARGE_RULE.LateChargeRatePercent")) + " of the net payment", TEXT_ITALICS));
				break;
			case "PercentageOfTotalPayment":
				para.append(new FormattedText(StringFormatter.PERCENT.formatString(closingMap.getClosingMapValue("LATE_CHARGE_RULE.LateChargeRatePercent")) + " of the total payment", TEXT_ITALICS));
			}
			if ("".equals(maximum)) {
				if (!"".equals(minimum))
					para.append(new FormattedText(" but no less than ", TEXT_ITALICS)).append(new FormattedText(StringFormatter.DOLLARS.formatString(minimum), TEXT_ITALICS));
				para.append(new FormattedText(".", TEXT_ITALICS));
			} else {
				if ("".equals(minimum))
					para.append(new FormattedText(" not to exceed ", TEXT_ITALICS));
				else
					para.append(new FormattedText(". Late fee to be no less than ", TEXT_ITALICS)).append(new FormattedText(StringFormatter.DOLLARS.formatString(minimum), TEXT_ITALICS)).append(new FormattedText(" and will not exceed ", TEXT_ITALICS));
				para.append(new FormattedText(StringFormatter.DOLLARS.formatString(maximum), TEXT_ITALICS)).append(new FormattedText(".", TEXT_ITALICS));
			}
		}
		col1.append(new LineFeed(headerPre))
			.append(new FormattedText("Late Payment", TEXT_BOLD))
			.append(new LineFeed(headerPost))
			.append(para);

		//17.4
		// Negative Amortization (column 1)
		para = new Paragraph();
		para.append(new FormattedText("Negative Amortization", TEXT_BOLD))
			.append(new FormattedText(" (Increase in Loan Amount)", TEXT));
		grid = new Grid(3, height, 2, width);
		grid.setLineMargin(1, Dimension.COLUMN, Direction.BOTTOM, 0);
		if (closingMap.getClosingMapValue("NEGATIVE_AMORTIZATION_RULE.NegativeAmortizationType").equals("ScheduledNegativeAmortization")) {
			grid.getCell(0, 0).setVerticalAlignment(VerticalAlignment.TOP).setMargin(Direction.TOP, 4f/72f).setMargin(Direction.BOTTOM, -4f/72f)
				.setForeground(BoxedCharacter.CHECK_BOX_NO);
		} else {
			grid.getCell(0, 0).setVerticalAlignment(VerticalAlignment.TOP).setMargin(Direction.TOP, 4f/72f).setMargin(Direction.BOTTOM, -4f/72f)
				.setForeground(BoxedCharacter.CHECK_BOX_EMPTY);
		}
		grid.getCell(0, 1).setWrap(true).setForeground(new FormattedText("are scheduled to make monthly payments that do not pay all of " +
				"the interest due that month.  As a result, your loan amount will   increase (negatively amortize), and your loan amount " +
				"will likely  become larger than your original loan amount. Increases in your  loan amount lower the equity you have in " +
				"this property. ", TEXT));
		if (closingMap.getClosingMapValue("NEGATIVE_AMORTIZATION_RULE.NegativeAmortizationType").equals("PotentialNegativeAmortization")) {
			grid.getCell(1, 0).setVerticalAlignment(VerticalAlignment.TOP).setMargin(Direction.TOP, 4f/72f).setMargin(Direction.BOTTOM, -4f/72f)
				.setForeground(BoxedCharacter.CHECK_BOX_NO);
		} else {
			grid.getCell(1, 0).setVerticalAlignment(VerticalAlignment.TOP).setMargin(Direction.TOP, 4f/72f).setMargin(Direction.BOTTOM, -4f/72f)
				.setForeground(BoxedCharacter.CHECK_BOX_EMPTY);
		}
		grid.getCell(1, 1).setWrap(true).setForeground(new FormattedText("may have monthly payments that do not pay all of the interest   " +
				"due that month. If you do, your loan amount will increase (negatively amortize), and, as a result, your loan amount may " +
				"become larger than your original loan amount. Increases in your  loan amount lower the equity you have in this property. ",
				TEXT));
		if (closingMap.getClosingMapValue("LOAN_DETAIL.NegativeAmortizationIndicator").equals("false")) {
			grid.getCell(2, 0).setVerticalAlignment(VerticalAlignment.TOP).setMargin(Direction.TOP, 4f/72f).setMargin(Direction.BOTTOM, -4f/72f)
				.setForeground(BoxedCharacter.CHECK_BOX_NO);
		} else {
			grid.getCell(2, 0).setVerticalAlignment(VerticalAlignment.TOP).setMargin(Direction.TOP, 4f/72f).setMargin(Direction.BOTTOM, -4f/72f)
				.setForeground(BoxedCharacter.CHECK_BOX_EMPTY); 
		}
		grid.getCell(2, 1).setWrap(true).setForeground(new FormattedText("do not have a negative amortization feature.", TEXT));
		col1.append(new LineFeed(headerPre))
			.append(para)
			.append(new LineFeed(headerPost))
			.append(new FormattedText("Under your loan terms, you", TEXT))
			.append(grid);

		//17.5
		// Partial Payments (column 1)
		grid = new Grid(3, height, 2, width);
		grid.setLineMargin(1, Dimension.COLUMN, Direction.BOTTOM, 0);
		if (closingMap.getClosingMapValue("PARTIAL_PAYMENT.PartialPaymentApplicationMethodType").equals("ApplyPartialPayment")) {
			grid.getCell(0, 0).setVerticalAlignment(VerticalAlignment.TOP).setMargin(Direction.TOP, 4f/72f).setMargin(Direction.BOTTOM, -4f/72f)
				.setForeground(BoxedCharacter.CHECK_BOX_NO);
		} else {
			grid.getCell(0, 0).setVerticalAlignment(VerticalAlignment.TOP).setMargin(Direction.TOP, 4f/72f).setMargin(Direction.BOTTOM, -4f/72f)
				.setForeground(BoxedCharacter.CHECK_BOX_EMPTY);
		}
		grid.getCell(0, 1).setWrap(true).setForeground(new FormattedText("may accept payments that are less than the full amount due " +
				"(partial payments) and apply them to your loan. ", TEXT));
		if (closingMap.getClosingMapValue("PARTIAL_PAYMENT.PartialPaymentApplicationMethodType").equals("HoldUntilCompleteAmount")) {
			grid.getCell(1, 0).setVerticalAlignment(VerticalAlignment.TOP).setMargin(Direction.TOP, 4f/72f).setMargin(Direction.BOTTOM, -4f/72f)
				.setForeground(BoxedCharacter.CHECK_BOX_NO);
		} else {
			grid.getCell(1, 0).setVerticalAlignment(VerticalAlignment.TOP).setMargin(Direction.TOP, 4f/72f).setMargin(Direction.BOTTOM, -4f/72f)
				.setForeground(BoxedCharacter.CHECK_BOX_EMPTY);
		}
		grid.getCell(1, 1).setWrap(true).setForeground(new FormattedText("may hold them in a separate account until you pay the rest of " +
				"the payment, and then apply the full payment to your loan. ", TEXT));
		if (closingMap.getClosingMapValue("PAYMENT_RULE.PartialPaymentAllowedIndicator").equals("true")) {
			grid.getCell(2, 0).setVerticalAlignment(VerticalAlignment.TOP).setMargin(Direction.TOP, 4f/72f).setMargin(Direction.BOTTOM, -4f/72f)
				.setForeground(BoxedCharacter.CHECK_BOX_EMPTY); 
		} else {
			grid.getCell(2, 0).setVerticalAlignment(VerticalAlignment.TOP).setMargin(Direction.TOP, 4f/72f).setMargin(Direction.BOTTOM, -4f/72f)
				.setForeground(BoxedCharacter.CHECK_BOX_NO);
		}
		grid.getCell(2, 1).setWrap(true).setForeground(new FormattedText("does not accept any partial payments.", TEXT));
		col1.append(new LineFeed(headerPre))
			.append(new FormattedText("Partial Payments", TEXT_BOLD))
			.append(new LineFeed(headerPost))
			.append(new FormattedText("Your lender", TEXT))
			.append(grid)
			.append(new LineFeed(3f/72f))
			.append(new FormattedText("If this loan is sold, your new lender may have a different policy.", TEXT));
		
		//17.6
		// Security Interest (column 1)
		String str = "";
		if (!subjectProperty.getAddressLine().equals(""))
			str = str + StringFormatter.STRINGCLEAN.formatString(subjectProperty.getAddressLine()) + ", ";
		if (!subjectProperty.getCityName().equals(""))
			str = str + StringFormatter.STRINGCLEAN.formatString(subjectProperty.getCityName()) + ", ";
		if (!subjectProperty.getStateCode().equals(""))
			str = str + subjectProperty.getStateCode() + " ";
		str += subjectProperty.getPostalCode();
		col1.append(new LineFeed(headerPre))
			.append(new FormattedText("Security Interest", TEXT_BOLD))
			.append(new LineFeed(headerPost))
			.append(new FormattedText("You are granting a security interest in", TEXT))
		    .append(new FormattedText(str, TEXT_ITALICS));
		col1.append(new LineFeed(20f/72f))
			.append(new FormattedText("You may lose this property if you do not make your payments or satisfy other obligations for this loan.", TEXT));
		
		// Escrow Account (column 2)
		//18.1
		boolean hasEscrow = closingMap.getClosingMapValue("LOAN_DETAIL.EscrowIndicator").equals("true");
		para = new Paragraph();
		para.append(new FormattedText("For now, ", TEXT_BOLD_ITALICS))
			.append(new FormattedText("your loan", TEXT));
		grid = new Grid(1, height, 2, width);
		grid.setLineMargin(1, Dimension.COLUMN, Direction.BOTTOM, 0);
		if (hasEscrow)
			grid.getCell(0, 0).setVerticalAlignment(VerticalAlignment.TOP).setMargin(Direction.TOP, 4f/72f).setMargin(Direction.BOTTOM, -4f/72f)
				.setForeground(BoxedCharacter.CHECK_BOX_NO); 
		else
			grid.getCell(0, 0).setVerticalAlignment(VerticalAlignment.TOP).setMargin(Direction.TOP, 4f/72f).setMargin(Direction.BOTTOM, -4f/72f)
				.setForeground(BoxedCharacter.CHECK_BOX_EMPTY); 
		grid.getCell(0, 1).setWrap(true).setMargin(Direction.LEFT, 0).setMargin(Direction.RIGHT, 0)
			.setForeground(new FormattedText("will have an escrow account (also called an \"impound\" or " +
				"\"trust\" account) to pay the property costs listed below. Without an escrow account, you would pay them directly " +
				"possibly in one or two large payments a year. Your lender may be liable for penalties and interest for failing to " +
				"make a payment.", TEXT));
		col2.append(new LineFeed(headerPre))
			.append(new FormattedText("Escrow Account", TEXT_BOLD))
			.append(new LineFeed(headerPost))
			.append(para)
			.append(grid);
		
		// Set escrow grid parameters
		float eHeight[] = { Grid.DYNAMIC };
		float eWidth[] = { .91f, .64f, 1.95f };

		// Escrow Table setup
		grid = new Grid(5, eHeight, eWidth.length, eWidth);
		for (int row = 0; row < grid.rows();  row++)
			grid.setLineBorder(row, Position.TOP, Color.BLACK, 0.5f/72f);
		grid.setLineBorder(4, Position.BOTTOM, Color.BLACK, 0.5f/72f);
		grid.setLineBorder(1, Position.LEFT, Color.BLACK, 0.5f/72f);
		grid.setLineBorder(2, Position.LEFT, Color.BLACK, 0.5f/72f);
		grid.setCellBorder(0, 1, Position.LEFT, null);
		grid.setCellBorder(0, 2, Position.LEFT, null);
		grid.setLineShade(0, Dimension.ROW, Color.MEDIUM_GRAY);
		grid.setLineMargin(1, Dimension.COLUMN, Direction.RIGHT, 5.0f/72.0f);
		grid.setLineMargin(2, Dimension.COLUMN, Direction.LEFT, 5.0f/72.0f);
		
		// Escrow Table fill
		grid.setLineMargin(0, Dimension.ROW, Direction.TOP, 0f/72.0f);
		grid.setLineMargin(0, Dimension.ROW, Direction.BOTTOM, 4.0f/72.0f);
		grid.getCell(0, 0).setForeground(new FormattedText("Escrow", TEXT_LARGE));
		grid.setLineMargin(1, Dimension.ROW, Direction.TOP, 0f/72.0f);
		grid.setLineMargin(1, Dimension.ROW, Direction.BOTTOM, 4.0f/72.0f);
		grid.getCell(1, 0).setWrap(true).setVerticalAlignment(VerticalAlignment.TOP)
			.setForeground(new FormattedText("Escrowed Property Costs over Year 1", TABLE_TEXT));
		if(hasEscrow){
			grid.getCell(1, 1).setVerticalAlignment(VerticalAlignment.TOP).setHorizontalAlignment(HorizontalAlignment.RIGHT)
				.setForeground(new FormattedText(StringFormatter.DOLLARS.formatString(
					closingMap.getClosingMapValue("INTEGRATED_DISCLOSURE_DETAIL.FirstYearTotalEscrowPaymentAmount")), TABLE_TEXT));
		}
		String description = StringFormatter.STRINGCLEAN.formatString(closingMap.getClosingMapValue("INTEGRATED_DISCLOSURE_DETAIL.FirstYearTotalEscrowPaymentDescription"));
		//StringTokenizer st = new StringTokenizer(description,",");
		Region escrowRegion = new Region()
			.append(new FormattedText("Estimated total amount over year 1 for your escrowed property costs:", TABLE_TEXT));
	     /*while (st.hasMoreTokens()) {
	         //System.out.println(st.nextToken());
	    	 escrowRegion.append(new FormattedText(st.nextToken().trim(), TABLE_TEXT_ITALICS))
	    	.append(new LineFeed(1f/72f));
	     }*/
		
		/*As per UCD-142 we are using comma separated here for Escrowed.*/
		
		String st1 = StringFormatter.CAMEL.formatString(description);
		st1 = st1.replace("Homeowners", "Homeowner's");
		escrowRegion.append(new FormattedText(st1, TABLE_TEXT_ITALICS))
    	.append(new LineFeed(1f/72f));
	    grid.getCell(1, 2).setWrap(true).setVerticalAlignment(VerticalAlignment.TOP).setForeground(escrowRegion);
		grid.setLineMargin(2, Dimension.ROW, Direction.TOP, 0f/72.0f);
		grid.setLineMargin(2, Dimension.ROW, Direction.BOTTOM, 4.0f/72.0f);
		grid.getCell(2, 0).setWrap(true).setVerticalAlignment(VerticalAlignment.TOP)
			.setForeground(new FormattedText("Non-Escrowed  Property Costs  over Year 1", TABLE_TEXT));
		Region nonEscrowRegion = new Region().append(new FormattedText("Estimated total amount over year 1 for your non-escrowed property costs:", TABLE_TEXT));
		if (hasEscrow) {
			description = StringFormatter.STRINGCLEAN.formatString(closingMap.getClosingMapValue("INTEGRATED_DISCLOSURE_DETAIL.FirstYearTotalNonEscrowPaymentDescription"));
			grid.getCell(2, 1).setVerticalAlignment(VerticalAlignment.TOP).setHorizontalAlignment(HorizontalAlignment.RIGHT)
				.setForeground(new FormattedText( StringFormatter.DOLLARS.formatString(
						closingMap.getClosingMapValue("INTEGRATED_DISCLOSURE_DETAIL.FirstYearTotalNonEscrowPaymentAmount")), TABLE_TEXT));
			
			/*StringTokenizer st1 = new StringTokenizer(description,",");
			if (st1.countTokens()<1)
				nonEscrowRegion.append(new LineFeed(2f/72f));
			while (st1.hasMoreTokens()) {
				nonEscrowRegion.append(new FormattedText(st1.nextToken().trim(), TABLE_TEXT_ITALICS))
		    	.append(new LineFeed(1f/72f));
		     }*/
			
			/*As per UCD-142 we are using comma separated here for Non Escrowed.*/
			
			String st2 = StringFormatter.CAMEL.formatString(description);
			st2 = st2.replace("Homeowners", "Homeowner's");
			nonEscrowRegion.append(new FormattedText(st2, TABLE_TEXT_ITALICS))
	    	.append(new LineFeed(1f/72f));
		}
		nonEscrowRegion.append(new FormattedText("You may have other property costs.", TABLE_TEXT));
		grid.getCell(2, 2).setWrap(true).setVerticalAlignment(VerticalAlignment.TOP).setForeground(nonEscrowRegion);
		grid.setLineMargin(3, Dimension.ROW, Direction.TOP, 0f/72.0f);
		grid.setLineMargin(3, Dimension.ROW, Direction.BOTTOM, 0f/72.0f);
		grid.getCell(3, 0).setWrap(true).setVerticalAlignment(VerticalAlignment.TOP)
			.setForeground(new FormattedText("Initial Escrow Payment", TABLE_TEXT));
		if(hasEscrow){
			String amt = closingMap.getClosingMapValue("INTEGRATED_DISCLOSURE_SECTION_SUMMARY_DETAIL.InitialEscrowPaymentAtClosing");
			if (inputData.isDocsDirect() && !"".equals(closingMap.getClosingMapValue("INTEGRATED_DISCLOSURE_DETAIL.dd:InitialEscrowPaymentAtClosing")))
				amt = closingMap.getClosingMapValue("INTEGRATED_DISCLOSURE_DETAIL.dd:InitialEscrowPaymentAtClosing");
			grid.getCell(3, 1).setVerticalAlignment(VerticalAlignment.TOP).setHorizontalAlignment(HorizontalAlignment.RIGHT)
				.setForeground(new FormattedText(StringFormatter.DOLLARS.formatString(amt), TABLE_TEXT));
		}
		grid.getCell(3, 2).setWrap(true).setVerticalAlignment(VerticalAlignment.TOP)
			.setForeground(new Region()
				.append(new FormattedText("A cushion for the escrow account you", TABLE_TEXT))
				.append(new FormattedText("pay at closing. See Section G on page 2.", TABLE_TEXT))
				.append(new LineFeed(8f/72f)));
		grid.setLineMargin(4, Dimension.ROW, Direction.TOP, 0f/72.0f);
		grid.setLineMargin(4, Dimension.ROW, Direction.BOTTOM, 4.0f/72.0f);
		grid.getCell(4, 0).setWrap(true).setVerticalAlignment(VerticalAlignment.TOP)
			.setForeground(new FormattedText("Monthly Escrow Payment", TABLE_TEXT));
		if(hasEscrow){
		grid.getCell(4, 1).setVerticalAlignment(VerticalAlignment.TOP).setHorizontalAlignment(HorizontalAlignment.RIGHT)
			.setForeground(new FormattedText( StringFormatter.DOLLARS.formatString(
				closingMap.getClosingMapValue("PROJECTED_PAYMENT.ProjectedPaymentEstimatedEscrowPaymentAmount")), TABLE_TEXT));
		}
		grid.getCell(4, 2).setWrap(true).setVerticalAlignment(VerticalAlignment.TOP)
			.setForeground(new FormattedText("The amount included in your total  monthly payment.", TABLE_TEXT));
		col2.append(new LineFeed(headerPre))
			.append(grid);
		
		// 18.1, Continued
		grid = new Grid(1, height, 2, width);
		grid.setLineVerticalAlignment(0, Dimension.ROW, VerticalAlignment.TOP);
		grid.getCell(0, 0).setMargin(Direction.TOP, -2f/72f);
		if (hasEscrow)
			grid.getCell(0, 0).setForeground(BoxedCharacter.CHECK_BOX_EMPTY); 
		else
			grid.getCell(0, 0).setForeground(BoxedCharacter.CHECK_BOX_NO); 
		para = new Paragraph()
			.append(new FormattedText("will not have an escrow account because ", TEXT));
		if (closingMap.getClosingMapValue("LOAN_DETAIL.EscrowAbsenceReasonType").equals("BorrowerDeclined"))
			para.append(BoxedCharacter.CHECK_BOX_NO);
		else
			para.append(BoxedCharacter.CHECK_BOX_EMPTY);
		para.append(new FormattedText(" you declined it ", TEXT));
		if (closingMap.getClosingMapValue("LOAN_DETAIL.EscrowAbsenceReasonType").equals("LenderDoesNotOffer"))
			para.append(BoxedCharacter.CHECK_BOX_NO); 
		else
			para.append(BoxedCharacter.CHECK_BOX_EMPTY); 
		para.append(new FormattedText(" your lender does not offer one. You must directly pay your  property costs, such as taxes " +
					"and homeowner's insurance. Contact your lender to ask if your loan can have an escrow account.", TEXT));		
		grid.getCell(0, 1).setMargin(Direction.TOP, -4f/72f);
		grid.getCell(0, 1).setWrap(true).setForeground(para);
		col2.append(new LineFeed(headerPre))
			.append(grid);

		// No Escrow Table setup
		grid = new Grid(3, eHeight, eWidth.length, eWidth);
		for (int row = 0; row < grid.rows();  row++)
			grid.setLineBorder(row, Position.TOP, Color.BLACK, 0.5f/72f);
		grid.setLineBorder(2, Position.BOTTOM, Color.BLACK, 0.5f/72f);
		grid.setLineBorder(1, Position.LEFT, Color.BLACK, 0.5f/72f);
		grid.setLineBorder(2, Position.LEFT, Color.BLACK, 0.5f/72f);
		grid.setCellBorder(0, 1, Position.LEFT, null);
		grid.setCellBorder(0, 2, Position.LEFT, null);
		grid.setLineShade(0, Dimension.ROW, Color.MEDIUM_GRAY);
		grid.setLineMargin(1, Dimension.COLUMN, Direction.RIGHT, 5.0f/72.0f);
		grid.setLineMargin(2, Dimension.COLUMN, Direction.LEFT, 5.0f/72.0f);
		grid.setLineMargin(2, Dimension.COLUMN, Direction.RIGHT, -1.0f/72.0f);
		
		// No Escrow Table fill
		grid.setLineMargin(0, Dimension.ROW, Direction.TOP, 0f/72.0f);
		grid.setLineMargin(0, Dimension.ROW, Direction.BOTTOM, 4.0f/72.0f);
		grid.getCell(0, 0).setForeground(new FormattedText("No Escrow", TEXT_LARGE));
		grid.setLineMargin(1, Dimension.ROW, Direction.TOP, 0f/72.0f);
		grid.setLineMargin(1, Dimension.ROW, Direction.BOTTOM, 4.0f/72.0f);
		grid.getCell(1, 0).setWrap(true)
			.setForeground(new FormattedText("Estimated       Property Costs       over Year 1", TABLE_TEXT));
		if (!hasEscrow)
			grid.getCell(1, 1).setVerticalAlignment(VerticalAlignment.TOP).setHorizontalAlignment(HorizontalAlignment.RIGHT).setMargin(Direction.LEFT, 2f/72f)
				.setForeground(new FormattedText(StringFormatter.DOLLARS.formatString(
						closingMap.getClosingMapValue("INTEGRATED_DISCLOSURE_DETAIL.FirstYearTotalNonEscrowPaymentAmount")), TABLE_TEXT));
		grid.getCell(1, 2).setWrap(true)
			.setForeground(new FormattedText("Estimated total amount over year 1. You must pay these costs directly, " +
				"possibly     in one or two large payments a year.", TABLE_TEXT));
		grid.setLineMargin(2, Dimension.ROW, Direction.TOP, 0f/72.0f);
		grid.setLineMargin(2, Dimension.ROW, Direction.BOTTOM, 4.0f/72.0f);
		grid.getCell(2, 0).setWrap(false)
			.setForeground(new FormattedText("Escrow Waiver Fee", TABLE_TEXT)).setMargin(Direction.RIGHT, 9.0f/72.0f);
		str = "";
		if (!hasEscrow){
			str = "0";
			for (Fees feeLocal:feeList){
				if(feeLocal.getType().equals("EscrowWaiverFee") || feeLocal.getLabel().equals("Escrow Account Waiver")){
					str = feeLocal.getPaymentAmount();
					break;
				}
			}
			grid.getCell(2, 1).setVerticalAlignment(VerticalAlignment.TOP).setHorizontalAlignment(HorizontalAlignment.RIGHT)
			.setForeground(new FormattedText(StringFormatter.DOLLARS.formatString(str), TABLE_TEXT));  
		}
		col2.append(new LineFeed(4f/72f)).append(grid);

		// In the future
		col2.append(new LineFeed(headerPre))
			.append(new FormattedText("In the future,", TEXT_BOLD_ITALICS))
			.append(new LineFeed(headerPost))
			.append(new FormattedText("Your property costs may change and, as a result, your escrow pay-   ment may change. You may be able " +
					"to cancel your escrow account, but if you do, you must pay your property costs directly. If you fail to pay your " +
					"property taxes, your state or local government may (1) impose fines and penalties or (2) place a tax lien on this " +
					"property. If you fail to pay any of your property costs, your lender may (1) add the amounts to your loan balance, " +
					"(2) add an escrow account to your  loan, or (3) require you to pay for property insurance that the lender buys on " +
					"your behalf, which likely would cost more and provide fewer benefits than what you could buy on your own.",
					TEXT));
	}

	public void draw(Page page, Object d) throws IOException {
		InputData data = (InputData)d;
		initializeGrid(data);
		col1.wrapAt(page, 3.5f);
		col2.wrapAt(page, 3.5f);

		float y = page.height - page.topMargin - titleGrid.height(page);
		titleGrid.draw(page, page.leftMargin, y);
		y = y - .25f/72f;
		col1.draw(page, page.leftMargin, y - col1.height(page));
		col2.draw(page, page.width - page.rightMargin - col2.width(page), y - col2.height(page));
	}
}
