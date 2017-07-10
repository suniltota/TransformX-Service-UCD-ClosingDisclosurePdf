package com.actualize.mortgage.domainmodels;

import java.io.IOException;
import java.util.List;

import com.actualize.mortgage.datalayer.ClosingMap;
import com.actualize.mortgage.datalayer.InputData;
import com.actualize.mortgage.datalayer.InterestRule;
import com.actualize.mortgage.datalayer.Utils;
import com.actualize.mortgage.pdfbuilder.Color;
import com.actualize.mortgage.pdfbuilder.FormattedText;
import com.actualize.mortgage.pdfbuilder.Grid;
import com.actualize.mortgage.pdfbuilder.Page;
import com.actualize.mortgage.pdfbuilder.Region;
import com.actualize.mortgage.pdfbuilder.Section;
import com.actualize.mortgage.pdfbuilder.Text;
import com.actualize.mortgage.pdfbuilder.Typeface;
import com.actualize.mortgage.pdfbuilder.Grid.Dimension;
import com.actualize.mortgage.pdfbuilder.Grid.Position;
import com.actualize.mortgage.pdfbuilder.TextBox.Direction;
import com.actualize.mortgage.pdfbuilder.TextBox.HorizontalAlignment;

public class AdjustableRateSection implements Section {
	public static final Text TAB               = new Text(Color.WHITE, 11, Typeface.CALIBRI_BOLD);

	private final float borderWidth = 1f/72f;
	private static final float leftIndent  = 2f/72f;
	
	private Grid airTitleGrid, airDataGrid;
	Region info;

	private void initializeAirTitleGrid() {
		float col1Width = 2.6f;
		float heights[] = { 11f/72f };
		float widths[] = { col1Width };
		airTitleGrid = new Grid(2, heights, widths.length, widths);
		airTitleGrid.getCell(0, 0)
			.setBackground(new Tab(col1Width))
			.setMargin(Direction.LEFT, leftIndent)
			.setForeground(new FormattedText("Adjustable Interest Rate (AIR) Table", TAB));
		
	}
	
	private void initializeAirDataGrid(InputData inputData) {
		float heights[] = { 11f/72f };
		float widths[] = { 80f/72f, 160f/72f };

		ClosingMap closingMap = inputData.getClosingMap();
		List<InterestRule> interestRuleList = inputData.getInterestRuleList();

		airDataGrid = new Grid(9, heights, widths.length, widths);
		for (int row = 0 ; row < 9; row++){
			if (inputData.isDocsDirect())
				airDataGrid.setLineBorder(row, Position.BOTTOM, Color.BLACK, borderWidth);
			else
				airDataGrid.setLineBorder(row, Position.BOTTOM, Color.MEDIUM_GRAY, borderWidth);
		}
		airDataGrid.setLineHorizontalAlignment(1, Dimension.COLUMN, HorizontalAlignment.RIGHT);
		airDataGrid.setLineBorder(0, Position.TOP, Color.BLACK, borderWidth);

		// Row Descriptors
		airDataGrid.setCellText(0, 0, new FormattedText( "Index + Margin", Text.TABLE_TEXT));
		airDataGrid.setCellText(1, 0, new FormattedText( "Initial Interest Rate", Text.TABLE_TEXT));
		airDataGrid.setCellText(2, 0, new FormattedText( "Minimum/Maximum Interest Rate", Text.TABLE_TEXT));
		airDataGrid.setCellText(3, 0, new FormattedText( "Change Frequency", Text.TABLE_TEXT_BOLD));
		airDataGrid.setCellText(4, 0, new FormattedText( "    First Change", Text.TABLE_TEXT));
		airDataGrid.setCellText(5, 0, new FormattedText( "    Subsequent Change", Text.TABLE_TEXT));
		airDataGrid.setCellText(6, 0, new FormattedText( "Limits on Interest Rate Changes", Text.TABLE_TEXT_BOLD));
		airDataGrid.setCellText(7, 0, new FormattedText( "    First Change", Text.TABLE_TEXT));
		airDataGrid.setCellText(8, 0, new FormattedText( "    Subsequent Change", Text.TABLE_TEXT));
		

		//Data 
		//20.1
		String index = closingMap.getClosingMapValue("INDEX_RULE.IndexType");
		if ("Other".equals(index) && !"".equals(closingMap.getClosingMapValue("INDEX_RULE.IndexTypeOtherDescription")))
			index = closingMap.getClosingMapValue("INDEX_RULE.IndexTypeOtherDescription");
		info = new Region();
		info.append(new FormattedText(StringFormatter.CAMEL.formatString(index)
				+" + "+StringFormatter.PERCENT.formatString(closingMap.getClosingMapValue("INTEREST_RATE_LIFETIME_ADJUSTMENT_RULE.MarginRatePercent"))
				,Text.TABLE_TEXT));
		airDataGrid.setCellText(0, 1, info);
		
		//20.2
		String str;
		if (closingMap.getClosingMapValue("TERMS_OF_LOAN.BuydownReflectedInNoteIndicator").equalsIgnoreCase("true")) {
			str = closingMap.getClosingMapValue("BUYDOWN.BuydownInitialEffectiveInterestRatePercent");
		} else if(!closingMap.getClosingMapValue("TERMS_OF_LOAN.DisclosedFullyIndexedRatePercent").equals("")) {
			str = closingMap.getClosingMapValue("TERMS_OF_LOAN.DisclosedFullyIndexedRatePercent");
		} else if (!closingMap.getClosingMapValue("TERMS_OF_LOAN.WeightedAverageInterestRatePercent").equals("")) {
			str = closingMap.getClosingMapValue("TERMS_OF_LOAN.WeightedAverageInterestRatePercent");
		} else {
			str = closingMap.getClosingMapValue("TERMS_OF_LOAN.NoteRatePercent");
		}
		airDataGrid.setCellText(1, 1, new FormattedText( StringFormatter.PERCENT.formatString(str), Text.TABLE_TEXT));

		//20.3
		info = new Region();
		info.append( new FormattedText( StringFormatter.PERCENT.formatString(closingMap.getClosingMapValue("INTEREST_RATE_LIFETIME_ADJUSTMENT_RULE.FloorRatePercent"))
				+"/"+ StringFormatter.PERCENT.formatString(closingMap.getClosingMapValue("INTEREST_RATE_LIFETIME_ADJUSTMENT_RULE.CeilingRatePercent")),Text.TABLE_TEXT));
		airDataGrid.setCellText(2,1, info);
		
		//20.4
		String firstChange = "";
		if (!closingMap.getClosingMapValue("INTEREST_RATE_LIFETIME_ADJUSTMENT_RULE.FirstRateChangeMonthsCount").equals("")) {
			try {
				int month = Integer.parseInt(closingMap.getClosingMapValue("INTEREST_RATE_LIFETIME_ADJUSTMENT_RULE.FirstRateChangeMonthsCount"));
				if (month > 0 )
					firstChange = StringFormatter.INTEGERSUFFIX.formatString(String.valueOf(month+1));
			} catch (Exception e) {
				// do nothing
			}
		}
		airDataGrid.setCellText(4,1,  new FormattedText("Beginning of " + firstChange + " Month", Text.TABLE_TEXT));

		//20.5
		String firstLimit = "";
		String subsequentLimit = "";
		String subsequentChange = "";
		for (InterestRule interestLocal:interestRuleList) {
			if(interestLocal.getAdjustmentRuleType().equals("First")){
				firstLimit = interestLocal.getPerChangeMaximumIncreaseRatePercent();
			} else if(interestLocal.getAdjustmentRuleType().equals("Subsequent")) {
				subsequentLimit = interestLocal.getPerChangeMaximumIncreaseRatePercent();
				subsequentChange = interestLocal.getPerChangeRateAdjustmentFrequencyMonthsCount();
				if (!subsequentChange.equals(""))
					subsequentChange = StringFormatter.INTEGERSUFFIX.formatString(subsequentChange);
				break;
			}
		}
		airDataGrid.setCellText(5,1,  new FormattedText("Every " + subsequentChange + " month after first change", Text.TABLE_TEXT));
		airDataGrid.setCellText(7, 1, new FormattedText(StringFormatter.PERCENT.formatString(firstLimit),Text.TABLE_TEXT));
		airDataGrid.setCellText(8, 1, new FormattedText(StringFormatter.PERCENT.formatString(subsequentLimit),Text.TABLE_TEXT));
	}
	
	public void draw(Page page, Object d) throws IOException {
		InputData data = (InputData)d;
		if (Utils.hasAdjustableInterestRate(data)) {
			initializeAirTitleGrid();
			initializeAirDataGrid(data);
			airTitleGrid.draw( page, 4.5f, page.bottomMargin + airDataGrid.height(page) + 4f/72f );
			airDataGrid.draw(  page, 4.5f, page.bottomMargin + 15f/72f);
		}
	}


}
