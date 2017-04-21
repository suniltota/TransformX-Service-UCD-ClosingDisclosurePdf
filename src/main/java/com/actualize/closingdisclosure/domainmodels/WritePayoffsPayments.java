package com.actualize.closingdisclosure.domainmodels;

import java.io.IOException;
import java.util.List;

import com.actualize.closingdisclosure.datalayer.Adjustments;
import com.actualize.closingdisclosure.datalayer.ClosingMap;
import com.actualize.closingdisclosure.datalayer.InputData;
import com.actualize.closingdisclosure.datalayer.Liabilities;
import com.actualize.closingdisclosure.pdfbuilder.Color;
import com.actualize.closingdisclosure.pdfbuilder.FormattedText;
import com.actualize.closingdisclosure.pdfbuilder.Grid;
import com.actualize.closingdisclosure.pdfbuilder.Page;
import com.actualize.closingdisclosure.pdfbuilder.Section;
import com.actualize.closingdisclosure.pdfbuilder.Text;
import com.actualize.closingdisclosure.pdfbuilder.Typeface;
import com.actualize.closingdisclosure.pdfbuilder.Grid.Dimension;
import com.actualize.closingdisclosure.pdfbuilder.Grid.Position;
import com.actualize.closingdisclosure.pdfbuilder.TextBox.HorizontalAlignment;
import com.actualize.closingdisclosure.pdfbuilder.TextBox.VerticalAlignment;

public class WritePayoffsPayments implements Section {
	private Grid titleGrid, dataGrid;
	private static final float NUM_WIDTH = 12f/72f;
	private static final float COL_1_WIDTH = 3.0f - NUM_WIDTH/2;
	private static final float COL_2_WIDTH = 3.0f - NUM_WIDTH/2;
	private static final float COL_3_WIDTH = 1.5f;
	private static final float NARROW = 1f/72f;
	private static final float HEADER_ROW_HEIGHT = 15f/72f ;
	private static final float DATA_ROW_HEIGHT  = 27f/72f ;
	float topMargin = 0.0f;
	public static final Text TABLE_TEXT_NEW         = new Text(Color.BLACK, 9, Typeface.CALIBRI);
	
	public WritePayoffsPayments() {
	}
	
	public WritePayoffsPayments(float topMargin) {
		this.topMargin = topMargin;
	}
	
	static boolean IsAddendumRequired(InputData data) {
	    ClosingMap closingMap = data.getClosingMap();
	    boolean isRefinanceTypeLoan = false;
	    if (closingMap.getClosingMapValue("TERMS_OF_LOAN.LoanPurposeType").equalsIgnoreCase("Refinance"))
            isRefinanceTypeLoan = true;
		if (LayoutPageThree.alternateView(data))
			return false;
		for (Liabilities liability : data.getLiabilitiesList())
			if ("PayoffsAndPayments".equalsIgnoreCase(liability.getIDSection()) && isRefinanceTypeLoan)
				return true;
		return false;
	}
	
	private void initializeTitleGrid() {
		float heights[] = { 22f/72f };
		float widths[] = { 1.75f, 5.75f};
		titleGrid = new Grid(1, heights, 2, widths);
		titleGrid.getCell(0, 0)
			.setHorizontalAlignment(HorizontalAlignment.LEFT)
			.setBackground(new Tab(widths[0]))
			.setForeground(new FormattedText("   Payoffs and Payments", Text.SECTION_HEADER));
		titleGrid.setCellText(0, 1, new FormattedText("    Use this table to see a summary of your payoffs and payments to others from your loan amount",
				Text.HEADER_SMALL));
		titleGrid.setLineBorder(0, Position.BOTTOM, Color.BLACK, NARROW);
	}
	
	private void initializeDataGrid(Object inputData) {
		float heights[] = {HEADER_ROW_HEIGHT,DATA_ROW_HEIGHT,DATA_ROW_HEIGHT,DATA_ROW_HEIGHT,DATA_ROW_HEIGHT,DATA_ROW_HEIGHT,DATA_ROW_HEIGHT,DATA_ROW_HEIGHT,DATA_ROW_HEIGHT,DATA_ROW_HEIGHT,DATA_ROW_HEIGHT,DATA_ROW_HEIGHT,DATA_ROW_HEIGHT,DATA_ROW_HEIGHT,DATA_ROW_HEIGHT,DATA_ROW_HEIGHT,HEADER_ROW_HEIGHT};
		float widths[] = {NUM_WIDTH, COL_1_WIDTH, COL_2_WIDTH, COL_3_WIDTH};

		List<Liabilities> liabilityList = ((InputData) inputData).getLiabilitiesList();
		List <Adjustments> adjustmentList = ((InputData) inputData).getAdjustmentList();
		ClosingMap closingMap = ((InputData) inputData).getClosingMap();
		
		dataGrid = new Grid(17, heights, 4, widths);
		dataGrid.setLineShade(0, Dimension.ROW, Color.LIGHT_GRAY);
		dataGrid.setLineHorizontalAlignment(3, Dimension.COLUMN, HorizontalAlignment.RIGHT);
		dataGrid.setCellText(0, 0, new FormattedText(" TO",Text.HEADER_SMALL));
		dataGrid.getCell(0, 3)
			.setHorizontalAlignment(HorizontalAlignment.LEFT)
			.setForeground(new FormattedText(" AMOUNT",Text.HEADER_SMALL));
		dataGrid.setLineBorder(0, Position.TOP, Color.BLACK, NARROW);
		dataGrid.setLineBorder(0, Position.BOTTOM, Color.DARK_GRAY, NARROW);
		dataGrid.setLineBorder(3, Position.LEFT, Color.DARK_GRAY, NARROW);
		
		int lineNumber = 1;
		int row = 1;
		String totalPayoffs = closingMap.getClosingMapValue("INTEGRATED_DISCLOSURE_SECTION_SUMMARY_DETAIL.PayoffsAndPayments");
		for(Liabilities liabilityLocal:liabilityList){
			if (!LayoutPageThree.standardView((InputData)inputData) || liabilityLocal.getIDSection().equalsIgnoreCase("PayoffsAndPayments")) {
				dataGrid.setLineBorder(row, Position.BOTTOM, Color.LIGHT_GRAY, NARROW);
				dataGrid.getCell(row,0).setVerticalAlignment(VerticalAlignment.MIDDLE);
				dataGrid.setCellText(row, 0, new FormattedText(String.format("%02d", lineNumber++), Text.TABLE_NUMBER));
				dataGrid.getCell(row,1).setVerticalAlignment(VerticalAlignment.MIDDLE);
				if(((InputData)inputData).isDocsDirect()){
					dataGrid.setCellText(row, 1, new FormattedText(liabilityLocal.getLabel(),TABLE_TEXT_NEW));
				}else if (liabilityLocal.isPayoffPartialIndicator() == true){
					dataGrid.setCellText(row, 1, new FormattedText(liabilityLocal.getFullName()+" to paydown "+liabilityLocal.getLabel(),TABLE_TEXT_NEW));
				}else{
					dataGrid.setCellText(row, 1, new FormattedText(liabilityLocal.getFullName()+" for payoff "+liabilityLocal.getLabel(),TABLE_TEXT_NEW));
				}
				//dataGrid.setCellText(row, 2, new FormattedText("For "+liabilityLocal.getLabel(),TABLE_TEXT_NEW));
				dataGrid.getCell(row,3).setVerticalAlignment(VerticalAlignment.MIDDLE);
				dataGrid.setCellText(row++, 3, new FormattedText(StringFormatter.DOLLARS.formatString(liabilityLocal.getPayoffAmount()), TABLE_TEXT_NEW));
			} else if (totalPayoffs.equals("") && liabilityLocal.getLabel().equalsIgnoreCase("Total Payoffs") && liabilityLocal.getIDSection().equalsIgnoreCase("DueFromBorrowerAtClosing"))
				totalPayoffs = liabilityLocal.getPayoffAmount();
		}
		
		// Write adjustments if not a standard view
		for (Adjustments adjustmentLocal:adjustmentList) {
			if (adjustmentLocal.getIntegratedDisclosureSectionType().equalsIgnoreCase("PayoffsAndPayments") || 
			        (!LayoutPageThree.standardView((InputData)inputData) &&
			        		adjustmentLocal.getIntegratedDisclosureSectionType().equalsIgnoreCase("PaidAlreadyByOrOnBehalfOfBorrowerAtClosing"))){
				dataGrid.setLineBorder(row, Position.BOTTOM, Color.LIGHT_GRAY, NARROW);
				dataGrid.getCell(row,0).setVerticalAlignment(VerticalAlignment.MIDDLE);
				dataGrid.setCellText(row, 0, new FormattedText(String.format("%02d", lineNumber++), Text.TABLE_NUMBER));
				dataGrid.getCell(row,1).setVerticalAlignment(VerticalAlignment.MIDDLE);
				if("Gift".equalsIgnoreCase(adjustmentLocal.getLabel()) || "Grant".equalsIgnoreCase(adjustmentLocal.getLabel())) {
					dataGrid.setCellText(row, 1, new FormattedText(adjustmentLocal.getPaymentToEntity()+" for "+adjustmentLocal.getLabel()+" from "+adjustmentLocal.getPaymentPaidByType(),TABLE_TEXT_NEW));
				} else if ("Rebate Credit".equalsIgnoreCase(adjustmentLocal.getLabel())) {
				    dataGrid.setCellText(row, 1, new FormattedText(adjustmentLocal.getPaymentToEntity()+" for "+adjustmentLocal.getLabel() + " by "+adjustmentLocal.getPaymentPaidByType(),TABLE_TEXT_NEW));
				} else if("Principal Reduction".equalsIgnoreCase(adjustmentLocal.getLabel())) {
				    dataGrid.setCellText(row, 1, new FormattedText(adjustmentLocal.getPaymentToEntity()+" for "+adjustmentLocal.getLabel(),TABLE_TEXT_NEW));
				} else {
				    String textToDisplay = "";
				    if(!"".equalsIgnoreCase(adjustmentLocal.getPaymentToEntity()) && null!=adjustmentLocal.getPaymentToEntity())
				        textToDisplay = textToDisplay + adjustmentLocal.getPaymentToEntity()+" for ";
				    textToDisplay = textToDisplay + adjustmentLocal.getLabel();
				    if(!"".equalsIgnoreCase(adjustmentLocal.getPaymentPaidByType()) && null!=adjustmentLocal.getPaymentPaidByType())
				        textToDisplay = textToDisplay + " from " +adjustmentLocal.getPaymentPaidByType();
				    dataGrid.setCellText(row, 1, new FormattedText(textToDisplay,TABLE_TEXT_NEW));
				}
				//dataGrid.setCellText(row, 2, new FormattedText("For "+adjustmentLocal.getLabel(),TABLE_TEXT_NEW));
				dataGrid.getCell(row,3).setVerticalAlignment(VerticalAlignment.MIDDLE);
				dataGrid.setCellText(row++, 3, new FormattedText(StringFormatter.DOLLARS.formatString(adjustmentLocal.getPaymentAmount()), TABLE_TEXT_NEW));
				//System.out.println("ajustment:"+adjustmentLocal.getPaymentToEntity());
			}
		}
		
		while (row < 16){
			dataGrid.setLineBorder(row, Position.BOTTOM, Color.LIGHT_GRAY, NARROW);
			dataGrid.getCell(row,0).setVerticalAlignment(VerticalAlignment.MIDDLE);
			dataGrid.setCellText(row++, 0, new FormattedText(String.format("%02d", lineNumber++), Text.TABLE_NUMBER));
		}
		//dataGrid.setLineShade(row, Dimension.ROW, Color.MEDIUM_GRAY);
		dataGrid.setLineBorder(row, Position.TOP, Color.BLACK, NARROW);
		dataGrid.setLineBorder(row, Position.BOTTOM, Color.BLACK, NARROW);
		dataGrid.setCellBorder(row, 2, Position.RIGHT, Color.DARK_GRAY);
		dataGrid.setCellText(row, 0, new FormattedText("K. TOTAL PAYOFFS AND PAYMENTS",Text.HEADER_SMALL));
		dataGrid.setCellText(row, 3, new FormattedText(StringFormatter.DOLLARS.formatString(totalPayoffs), Text.HEADER_SMALL));
		
	}
	
	public void draw(Page page, Object data) throws IOException {
		initializeTitleGrid();
		initializeDataGrid(data);
		float rowLocation = page.height - page.topMargin - titleGrid.height(page) - topMargin + 1.25f;
		if (WritePayoffsPayments.IsAddendumRequired((InputData) data)){
			rowLocation = page.height - page.topMargin - titleGrid.height(page) - topMargin;
		} 
		titleGrid.draw(page, page.leftMargin, rowLocation);
		rowLocation -= dataGrid.height(page);
		dataGrid.draw(page, page.leftMargin, rowLocation);
		((InputData) data).setAlternativeC2Crow(rowLocation);
	}
}
