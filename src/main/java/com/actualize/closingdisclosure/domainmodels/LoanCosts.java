package com.actualize.closingdisclosure.domainmodels;

import java.io.IOException;
import java.util.ArrayList;

import com.actualize.closingdisclosure.datalayer.ClosingMap;
import com.actualize.closingdisclosure.datalayer.Fees;
import com.actualize.closingdisclosure.datalayer.ID_Subsection;
import com.actualize.closingdisclosure.datalayer.InputData;
import com.actualize.closingdisclosure.datalayer.LoanCostHeight;
import com.actualize.closingdisclosure.domainmodels.CostsTableRow.Columns;
import com.actualize.closingdisclosure.pdfbuilder.Color;
import com.actualize.closingdisclosure.pdfbuilder.FormattedText;
import com.actualize.closingdisclosure.pdfbuilder.Grid;
import com.actualize.closingdisclosure.pdfbuilder.Page;
import com.actualize.closingdisclosure.pdfbuilder.Region;
import com.actualize.closingdisclosure.pdfbuilder.Section;
import com.actualize.closingdisclosure.pdfbuilder.Text;
import com.actualize.closingdisclosure.pdfbuilder.Typeface;
import com.actualize.closingdisclosure.pdfbuilder.Grid.Dimension;
import com.actualize.closingdisclosure.pdfbuilder.Grid.Position;
import com.actualize.closingdisclosure.pdfbuilder.TextBox.Direction;
import com.actualize.closingdisclosure.pdfbuilder.TextBox.HorizontalAlignment;

public class LoanCosts implements Section {
	private static final Text SMALL_HEADER = new Text(Color.BLACK, 7.5f, Typeface.CALIBRI);
	private static final Text TAB          = new Text(Color.WHITE, 11, Typeface.CALIBRI_BOLD);
	

	private static final float headerBuffer = 0f/72f;
	private static final float headerHeight = 4f*CostsTableRow.lineHeight;
	private static final float leftIndent  = 2f/72f;
	private ArrayList<CostsTable> tables = new ArrayList<CostsTable>();

	LoanCosts(InputData data) {
		extractData(data);
	}
	
	class LoanCostsTable extends CostsTable {
		LoanCostsTable(int preferedLines, InputData data, String sectionType) {
			super(preferedLines, Color.LIGHT_GRAY);
			for (Fees fee : data.getFeeList())
				if (fee.getIntegratedDisclosureSectionType().equalsIgnoreCase(sectionType) && !fee.getLabel().equals(""))
					addFee(fee);		
		}

		void addFee(Fees fee) {
			if (fee.getType().equalsIgnoreCase("LoanDiscountPoints")){
				if(null !=fee.getTotalPercent() && !"0.0000".equalsIgnoreCase(fee.getTotalPercent())){
				addRow(0, new FeeCostsTableRow(fee, true, StringFormatter.PERCENTWITHOUTPRECEEDING.formatString(fee.getTotalPercent()) + " of Loan Amount (Points)", null));
				}
				else{
					addRow(0, new FeeCostsTableRow(fee, true, "      % of Loan Amount (Points)", null));
				}
			}
			else
				addRow(new FeeCostsTableRow(fee, true, null, null));
		}
	}

	private void drawHeaderGrid(Page page, InputData data, float[] widths, Columns[] columnNames, float x, float y) {
		float theights[] = { 2 * CostsTableRow.lineHeight };
		float twidths[]  = { 7.5f };
		Grid title = new Grid(1, theights, 1, twidths);
		title.setCellText(0, 0, new FormattedText("Closing Cost Details", Text.HEADER_MEDIUM));
		title.setLineBorder(0, Position.BOTTOM, Color.BLACK, 1f/72f);

		float h1heights[] = { CostsTableRow.lineHeight };
		float h1widths[] = CostsTableRow.columnWidthsH1(data);
		Grid header1 = new Grid(1, h1heights, h1widths.length, h1widths);
		header1.setLineHorizontalAlignment(0, Dimension.ROW, HorizontalAlignment.CENTER);
		for (int i = 1; i < h1widths.length; i++) {
			header1.setCellBorder(0, i, Position.LEFT, Color.DARK_GRAY, 2f/72f);
			header1.setCellShade(0, i, Color.LIGHT_GRAY);
		}
		if (columnNames[0] == Columns.BuyerAtClosing)
			header1.setCellText(0, 1, new FormattedText("Borrower-Paid", Text.TABLE_HEADER));
		else
			header1.setCellText(0, 1, new FormattedText("Seller-Paid", Text.TABLE_HEADER));
		if (columnNames.length > 2)
			if (columnNames[2] == Columns.SellerAtClosing) {
				header1.setCellText(0, 2, new FormattedText("Seller-Paid", Text.TABLE_HEADER));
				header1.setCellText(0, 3, new FormattedText("Paid By", Text.TABLE_HEADER));
			} else
				header1.setCellText(0, 2, new FormattedText("Paid By", Text.TABLE_HEADER));

		float h2heights[] = { CostsTableRow.lineHeight };
		float h2widths[] = CostsTableRow.columnWidthsH2(data);
		int h2Columns = columnNames.length + 1;
		Grid header2 = new Grid(1, h2heights, h2widths.length, h2widths);
		header2.setLineHorizontalAlignment(0, Dimension.ROW, HorizontalAlignment.CENTER);
		header2.getCell(0, 0).setHorizontalAlignment(HorizontalAlignment.LEFT).setBackground(new Tab())
			.setMargin(Direction.LEFT, leftIndent).setForeground(new FormattedText("Loan Costs", TAB));
		for (int i = 1; i < h2Columns; i++) {
			if (i % 2 == 1)
				header2.setCellBorder(0, i, Position.LEFT, Color.DARK_GRAY, 2f/72f);
			header2.setCellShade(0, i, Color.LIGHT_GRAY);
			if (columnNames[i-1] == Columns.Other)
				header2.setCellText(0, i, new FormattedText("Others", Text.TABLE_HEADER));
			else
				header2.setCellText(0, i, new FormattedText(i % 2 == 1 ? "At Closing" : "Before Closing", SMALL_HEADER));
		}
		
		try {
			header2.draw(page, x, y);
			header1.draw(page, x, y + CostsTableRow.lineHeight);
			title.draw(page, x, y + 2*CostsTableRow.lineHeight);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void extractData(InputData data) {
		ClosingMap closingMap = data.getClosingMap();

		// Create origination charges table
		int pLines = 8;
		if(data.isPages2A2B())
			pLines = 21;
		tables.add(new LoanCostsTable(pLines, data, "OriginationCharges").setHeader("A. Origination Charges",
				StringFormatter.DOLLARS.formatString(closingMap.getClosingMapValue("INTEGRATED_DISCLOSURE_SECTION_SUMMARY_DETAIL.OriginationCharges"))));
		
		// Create did not shop for table
		pLines = 10;
		if(data.isPages2A2B())
				pLines = 20;
		tables.add(new LoanCostsTable(pLines, data, "ServicesBorrowerDidNotShopFor").setHeader("B. Services Borrower Did Not Shop For",StringFormatter.DOLLARS.formatString(closingMap.getClosingMapValue("INTEGRATED_DISCLOSURE_SECTION_SUMMARY_DETAIL.ServicesBorrowerDidNotShopFor"))));
			
		// Create did shop for table
		//System.out.println("ServicesBorrowerDidShopFor data "+data);
		pLines = 10;
		if(data.isPages2A2B())
			pLines = 20;
		tables.add(new LoanCostsTable(pLines, data, "ServicesBorrowerDidShopFor").setHeader("C. Services Borrower Did Shop For",
				StringFormatter.DOLLARS.formatString(closingMap.getClosingMapValue("INTEGRATED_DISCLOSURE_SECTION_SUMMARY_DETAIL.ServicesBorrowerDidShopFor"))));
		
		// Ensure loan amount points line is inserted (if not done already
		if (tables.get(0).numLines() > 0) {
			Fees fee = (Fees)tables.get(0).getRow(0).cost;
			if (fee != null && !fee.getType().equalsIgnoreCase("LoanDiscountPoints")) {
				CostsTableRow line = new CostsTableRow();
				line.add(Columns.CostLabel, new Region().append(new FormattedText("% of Loan Amount (Points)", Text.TABLE_TEXT)));
				tables.get(0).addRow(0, line);
			}
		} else {
			// fix for no rows in table error caused by no origination fees in input
			CostsTableRow line = new CostsTableRow();
			line.add(Columns.CostLabel, new Region().append(new FormattedText("% of Loan Amount (Points)", Text.TABLE_TEXT)));
			tables.get(0).addRow(0, line);
		}

		if (!data.isSellerOnly()) {
			
			// Create total costs table
			String suffix = " (Borrower-Paid)";
			if (CostsTableRow.noBuyer(data))
				suffix = "";
			tables.add(new CostsTable().setHeader("D. TOTAL LOAN COSTS" + suffix,
				StringFormatter.DOLLARS.formatString(closingMap.getClosingMapValue("INTEGRATED_DISCLOSURE_SECTION_SUMMARY_DETAIL.TotalLoanCosts"))));

			// Add section D. line
			CostsTableRow line = new CostsTableRow();
			line.add(Columns.Number, new Region().append(new FormattedText("Loan Costs Subtotals (A + B + C)", Text.TABLE_TEXT)));
			for (ID_Subsection idsLocal : data.getIdsList()) {
				if (idsLocal.getIntegratedDisclosureSubsectionType().equals("LoanCostsSubtotal")) {
					Columns column = Columns.Other;
					if (idsLocal.getPaymentPaidByType().equalsIgnoreCase("Buyer"))
						column = idsLocal.isPaidOutsideOfClosingIndicator() ? Columns.BuyerOutsideClosing : Columns.BuyerAtClosing;
					else if (idsLocal.getPaymentPaidByType().equalsIgnoreCase("Seller"))
						column = idsLocal.isPaidOutsideOfClosingIndicator() ? Columns.SellerOutsideClosing : Columns.SellerAtClosing;
					String amt = idsLocal.getPaymentAmount();
					if (!"".equals(amt) && !"0.00".equals(amt))
						line.add(column, new Region().append(new FormattedText(StringFormatter.DOLLARS.formatString(amt), Text.TABLE_TEXT)));
				}
			}
			tables.get(3).addRow(line);
		}
	}

	public void stretch(Page page, InputData data, float height) {
		int[] stretchTables = { 0, 1, 2 };
		CostsTable.stretch(page, tables, stretchTables, CostsTableRow.columnWidths(data), height - 4*CostsTableRow.lineHeight);
	}
	
	public float getHeight(Page page, InputData data) {
		float height = 0;
		for (int i = 0; i < tables.size(); i++)
			height += tables.get(i).getHeight(page, CostsTableRow.columnWidths((InputData)data));
		
		LoanCostHeight.getLoanCostHeight().setLoadGridHeight(height + headerHeight + headerBuffer);
		
		return height + headerHeight + headerBuffer;
	}
	
	public void draw(Page page, Object data) throws IOException {
		Columns[] columnNames = CostsTableRow.columnNames((InputData)data);
		float[] columnWidths = CostsTableRow.columnWidths((InputData)data);
		float y = page.height - page.topMargin - headerHeight;
		drawHeaderGrid(page, (InputData)data, columnWidths, columnNames, page.leftMargin, y);
		y -= headerBuffer;
		for (int i = 0; i < tables.size(); i++) {
			y -= tables.get(i).getHeight(page, columnWidths);
			tables.get(i).draw(page, columnWidths, columnNames, page.leftMargin, y);
		}
	}
}
