package com.actualize.closingdisclosure.domainmodels;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.actualize.closingdisclosure.datalayer.ClosingMap;
import com.actualize.closingdisclosure.datalayer.Escrows;
import com.actualize.closingdisclosure.datalayer.Fees;
import com.actualize.closingdisclosure.datalayer.ID_Subsection;
import com.actualize.closingdisclosure.datalayer.InputData;
import com.actualize.closingdisclosure.datalayer.Prepaids;
import com.actualize.closingdisclosure.domainmodels.CostsTableRow.Columns;
import com.actualize.closingdisclosure.pdfbuilder.Color;
import com.actualize.closingdisclosure.pdfbuilder.Drawable;
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

public class OtherCosts implements Section {
	private static final Text TAB          = new Text(Color.WHITE, 11, Typeface.CALIBRI_BOLD);
	private static final Text SMALL_HEADER = new Text(Color.BLACK, 7.5f, Typeface.CALIBRI);
	
	private static final float headerBuffer = 1f/72f;
	private static final float headerHeight = 1.5f*CostsTableRow.lineHeight;
	private static final float leftIndent  = 2f/72f;
	private boolean withHeader = false;
	private float position = 0;
	ArrayList<CostsTable> tables = new ArrayList<CostsTable>();

	OtherCosts(Page page, InputData data) {
		extractData(page, data);
	}
	
	void setWithHeader(boolean withHeader) {
		this.withHeader = withHeader;
	}
	
	class PrepaidCostsTableRow extends CostsTableRow {
		PrepaidCostsTableRow(Prepaids prepaid, String to, String label) {
			super(false);
			
			// Insert cost label
			String str = label == null ? prepaid.getLabel() : label;
			if (to!=null)
				str = str + " " + to;
			add(Columns.CostLabel, str);
			// Populate amounts
			if (!prepaid.getBuyerAtClosingAmount().equals("") /*&& StringFormatter.doubleValue(prepaid.getBuyerAtClosingAmount()) != 0*/)
				add(Columns.BuyerAtClosing, prepaid.getBuyerAtClosingAmount());
			if (!prepaid.getBuyerOutsideClosingAmount().equals("") && StringFormatter.doubleValue(prepaid.getBuyerOutsideClosingAmount()) != 0)
				add(Columns.BuyerOutsideClosing, prepaid.getBuyerOutsideClosingAmount());
			if (!prepaid.getSellerAtClosingAmount().equals("") && StringFormatter.doubleValue(prepaid.getSellerAtClosingAmount()) != 0)
				add(Columns.SellerAtClosing, prepaid.getSellerAtClosingAmount());
			if (!prepaid.getSellerOutsideClosingAmount().equals("") && StringFormatter.doubleValue(prepaid.getSellerOutsideClosingAmount()) != 0)
				add(Columns.SellerOutsideClosing, prepaid.getSellerOutsideClosingAmount());
			String prefix = prepaid.getOtherEntity().equalsIgnoreCase("Lender") ? "(L)" : "";
			if (!prepaid.getOtherAmount().equals("") && StringFormatter.doubleValue(prepaid.getOtherAmount()) != 0)
				addNoFormat(Columns.Other, prefix + Columns.Other.formatter().formatString(prepaid.getOtherAmount()));
		}
	}
	
	class EscrowCostsTableRow extends CostsTableRow {
		EscrowCostsTableRow(Page page, Escrows escrow, String to, String label) {
			super(false);

			// Insert cost label
			String str = label;
			if (label== null || label.equals(""))
				str = escrow.getLabel();
			add(Columns.CostLabel, str.replaceAll(" Escrow(s*)$", ""));
			
			// Insert to entity
			add(page, Columns.ToEntity, to, true);
			
			// Populate amounts
			if (!escrow.getBuyerAtClosingAmount().equals("") && StringFormatter.doubleValue(escrow.getBuyerAtClosingAmount()) != 0)
				add(Columns.BuyerAtClosing, escrow.getBuyerAtClosingAmount());
			if (!escrow.getBuyerOutsideClosingAmount().equals("") && StringFormatter.doubleValue(escrow.getBuyerOutsideClosingAmount()) != 0)
				add(Columns.BuyerOutsideClosing, escrow.getBuyerOutsideClosingAmount());
			if (!escrow.getSellerAtClosingAmount().equals("") && StringFormatter.doubleValue(escrow.getSellerAtClosingAmount()) != 0)
				add(Columns.SellerAtClosing, escrow.getSellerAtClosingAmount());
			if (!escrow.getSellerOutsideClosingAmount().equals("") && StringFormatter.doubleValue(escrow.getSellerOutsideClosingAmount()) != 0)
				add(Columns.SellerOutsideClosing, escrow.getSellerOutsideClosingAmount());
			String prefix = escrow.getOtherEntity().equalsIgnoreCase("Lender") ? "(L)" : "";
			if (!escrow.getOtherAmount().equals("") && StringFormatter.doubleValue(escrow.getOtherAmount()) != 0)
				addNoFormat(Columns.Other, prefix + Columns.Other.formatter().formatString(escrow.getOtherAmount()));
		}
	}

	class TaxesAndGovernmentFeesTable extends CostsTable {		
		TaxesAndGovernmentFeesTable(int pLines,InputData data) {
			super(pLines, Color.LIGHT_GRAY);
			// preferred 2
			setHeader("E. Taxes and Other Government Fees", StringFormatter.DOLLARS.formatString(
					data.getClosingMap().getClosingMapValue("INTEGRATED_DISCLOSURE_SECTION_SUMMARY_DETAIL.TaxesAndOtherGovernmentFees")));

			//8.2.1 - recording fee
			Fees recordingFee = null;
			String deedAmt = "Deed: ";
			String mrtgAmt = "                   Mortgage: ";
			for(Fees fee : data.getFeeList())
				if (fee.getIntegratedDisclosureSectionType().equalsIgnoreCase("TaxesAndOtherGovernmentFees"))
					switch (fee.getType()) {
					case "RecordingFeeForDeed":
						deedAmt += StringFormatter.DOLLARS.formatString(fee.getTotalAmount());
						break;
						//8.2.1
					case "RecordingFeeForMortgage":
						mrtgAmt += StringFormatter.DOLLARS.formatString(fee.getTotalAmount());
						break;
					case "RecordingFeeTotal":
						recordingFee = fee;
						break;
					}
			String str = "Recording Fees                                          " + deedAmt + mrtgAmt;
			if (recordingFee == null)
				addRow(new CostsTableRow(false).add(Columns.CostLabel, str));
			else
				addRow(new FeeCostsTableRow(recordingFee, false, str, null, false));

			//8.3 - other taxes and government fees
			boolean foundTransferTax = false;
			for (Fees fee : data.getFeeList())
				if (fee.getIntegratedDisclosureSectionType().equalsIgnoreCase("TaxesAndOtherGovernmentFees") &&
						!fee.getType().equalsIgnoreCase("RecordingFeeForDeed") &&
						!fee.getType().equalsIgnoreCase("RecordingFeeForMortgage") &&
						!fee.getType().equalsIgnoreCase("RecordingFeeTotal")) {
					addRow(new FeeCostsTableRow(fee, true, fee.getLabel(), null));
					foundTransferTax = true;
				}
			if (!foundTransferTax) // Leave an empty line
				addRow(new CostsTableRow().add(Columns.CostLabel, ""));
		}
	}
	
	class PrepaidsTable extends CostsTable {
		PrepaidsTable(int pLines, InputData data) {
			super(pLines, Color.LIGHT_GRAY);
			//preferred 5
			setHeader("F. Prepaids", StringFormatter.DOLLARS.formatString(
					data.getClosingMap().getClosingMapValue("INTEGRATED_DISCLOSURE_SECTION_SUMMARY_DETAIL.Prepaids")));

			List<Prepaids> prepaids = data.getPrepaidList();

			//8.5
			addPrepaidByType(prepaids, "HomeownersInsurancePremium");
		
			//8.6
			addPrepaidByType(prepaids, "MortgageInsurancePremium");
			
			//8.7 Prepaid Interest
			addPrepaidByType(prepaids, "PrepaidInterest");

			//8.7 Find "Property Taxes" and display
			Prepaids propertyTaxes = null;
			for (Prepaids prepaid : prepaids)
				if (prepaid.getLabel().equals("Property Taxes")) {
					propertyTaxes = prepaid;
					break;
				} else if (propertyTaxes == null && isPropertyTax(prepaid.getType())) {
					propertyTaxes = prepaid;
				}
			if (propertyTaxes == null)
				addRow(new CostsTableRow().add(Columns.CostLabel, "Property Taxes "));
			else
				addRow(new PrepaidCostsTableRow(propertyTaxes, getPrepaidItemMonthsPaidCount(propertyTaxes), "Property Taxes"));
			
			// Other prepaids including taxes but... not any of the above
			for (Prepaids prepaid : prepaids)
				if (!prepaid.getType().equalsIgnoreCase("HomeownersInsurancePremium") && !prepaid.getType().equalsIgnoreCase("MortgageInsurancePremium")
						&& !prepaid.getType().equalsIgnoreCase("PrepaidInterest") && prepaid != propertyTaxes)
					addRow(new PrepaidCostsTableRow(prepaid, getPrepaidItemMonthsPaidCount(prepaid), null));
		}
		
		void addPrepaidByType(List<Prepaids> prepaids, String prepaidType) {
			String label = StringFormatter.CAMEL.formatString(prepaidType);
			if(label.equalsIgnoreCase("Homeowners Insurance Premium")){
			      label = "Homeowner's Insurance Premium";
			}
			Prepaids found = null;
			for (Prepaids prepaid : prepaids)
				if (prepaid.getType().equals(prepaidType)) {
					found = prepaid;
					break;
				}
			String to = prepaidType.equalsIgnoreCase("PrepaidInterest") ? getPrepaidPerDiemPaidCount(found) : getPrepaidItemMonthsPaidCount(found);
			if (found == null)
				//addRow(new CostsTableRow().add(Columns.CostLabel, label).add(Columns.ToEntity, to));	
				addRow(new CostsTableRow(false).add(Columns.CostLabel, label + " " + to));		
			else
				addRow(new PrepaidCostsTableRow(found, to, label));
		}

		String getPrepaidItemMonthsPaidCount(Prepaids prepaid) {
			String str = "";
			if (prepaid == null){
				str = "";
		    }
			else if(null != prepaid.getPrepaidItemMonthsPaidCount() && !"".equalsIgnoreCase(prepaid.getPrepaidItemMonthsPaidCount()) ){
				str = "( " + prepaid.getPrepaidItemMonthsPaidCount() + " mo.)  " ;
				if (null != prepaid.getPaymentToEntity() && !"".equalsIgnoreCase(prepaid.getPaymentToEntity()) ){
					str = str + " to " + prepaid.getPaymentToEntity();
				}
			}
			else {
				if(prepaid.getLabel() == ""){
			    str = "";
			    }
			}
			return str;
		}
		
		String getPrepaidPerDiemPaidCount(Prepaids prepaid) {
			if (prepaid == null)
				return "( $   per day from   to   )";
			return "( " + StringFormatter.DOLLARS.formatString(prepaid.getPrepaidItemPerDiemAmount()) + "  per day from " +
					StringFormatter.DATE.formatString(prepaid.getPrepaidItemPaidFromDate()) + " to " +
					StringFormatter.DATE.formatString(prepaid.getPrepaidItemPaidThroughDate()) + ")";
		}
	}

	class EscowAtClosingTable extends CostsTable {		
		EscowAtClosingTable(int pLines, Page page, InputData data) {
			super(pLines, Color.LIGHT_GRAY);
			//preferred 8
			setHeader("G. Initial Escrow Payment at Closing", StringFormatter.DOLLARS.formatString(
					data.getClosingMap().getClosingMapValue("INTEGRATED_DISCLOSURE_SECTION_SUMMARY_DETAIL.InitialEscrowPaymentAtClosing")));

			List<Escrows> escrows = data.getEscrowList();
			ClosingMap closingMap = data.getClosingMap();

			addEscrowByType(page, escrows, "HomeownersInsurance",data);
			addEscrowByType(page, escrows, "MortgageInsurance",data);

			// Find "Property Taxes" and display
			Escrows propertyTaxes = null;
			for (Escrows escrow : escrows)
				if (escrow.getLabel().equals("Property Tax")) {
					propertyTaxes = escrow;
					break;
				} 
				else if (propertyTaxes == null && isPropertyTax(escrow.getType())) {
					propertyTaxes = escrow;
				}
			if (propertyTaxes == null)
				addRow(new CostsTableRow().setInputData(data).add(Columns.CostLabel, "Property Taxes").add(page, Columns.ToEntity, getEscrowMonthsPaidCount(null), true));
			else
				addRow(new EscrowCostsTableRow(page, propertyTaxes, getEscrowMonthsPaidCount(propertyTaxes), "Property Taxes").setInputData(data));

			// Other escrow including taxes but... not any of the above
			for (Escrows escrow : escrows)
				if (!escrow.getType().equals("HomeownersInsurance") && !escrow.getType().equals("MortgageInsurance") && escrow != propertyTaxes && !escrow.getType().equals(""))
					addRow(new EscrowCostsTableRow(page, escrow, getEscrowMonthsPaidCount(escrow), null));

			// Aggregate adjustment
			CostsTableRow aggregateAdjustment = new CostsTableRow().add(Columns.CostLabel, "Aggregate Adjustment");		
			if (!closingMap.getClosingMapValue("ESCROW_DETAIL.EscrowAggregateAccountingAdjustmentAmount").equals(""))
				aggregateAdjustment.add(Columns.BuyerAtClosing, closingMap.getClosingMapValue("ESCROW_DETAIL.EscrowAggregateAccountingAdjustmentAmount"));
			addRow(aggregateAdjustment);
		}

		void addEmptyLines(int count) {
			int index = numLines() - 1;
			for (int i = 0; i < count; i++)
				addRow(index, new CostsTableRow());
		}
		
		void addEscrowByType(Page page, List<Escrows> escrows, String prepaidType, InputData data) {
			String label = StringFormatter.CAMEL.formatString(prepaidType);
			if(label.equalsIgnoreCase("Homeowners Insurance")){
			      label = "Homeowner's Insurance";
			}
			Escrows found = null;
			for (Escrows escrow : escrows)
				if (escrow.getType().equals(prepaidType)) {
					found = escrow;
					break;
				}
			if (found == null)
				addRow(new CostsTableRow().setInputData(data).add(Columns.CostLabel, label).add(page, Columns.ToEntity, getEscrowMonthsPaidCount(found), true));		
			else
				addRow(new EscrowCostsTableRow(page, found, getEscrowMonthsPaidCount(found), label));
		}

		String getEscrowMonthsPaidCount(Escrows escrow) {
			if (escrow == null)
				return "  per month for    mo.";
			return StringFormatter.DOLLARS.formatString(escrow.getMonthlyPaymentAmount()) + " per month for " + escrow.getCollectedNumberOfMonthsCount() + " mo.";
		}
	}

	class OtherCostsTable extends CostsTable {		
		OtherCostsTable(int pLines,InputData data) {
			super(pLines, Color.LIGHT_GRAY);
			// preferred 8
			setHeader(
					"H. Other", StringFormatter.DOLLARS.formatString(
					data.getClosingMap().getClosingMapValue("INTEGRATED_DISCLOSURE_SECTION_SUMMARY_DETAIL.OtherCosts")));
			for(Fees fee : data.getFeeList())
				if (fee.getIntegratedDisclosureSectionType().equalsIgnoreCase("OtherCosts"))
					addRow(new FeeCostsTableRow(fee, true, null, null));
			addEmptyLines(1);
		}
	}

	class TotalOtherCostsTable extends CostsTable {	
		TotalOtherCostsTable(InputData data) {
			String suffix = " (Borrower-Paid)";
			if (CostsTableRow.noBuyer(data))
				suffix = "";
			setHeader("I. TOTAL OTHER COSTS" + suffix, StringFormatter.DOLLARS.formatString(
					data.getClosingMap().getClosingMapValue("INTEGRATED_DISCLOSURE_SECTION_SUMMARY_DETAIL.TotalOtherCosts")));

			for(ID_Subsection ids : data.getIdsList()){
				if ("0.00".equals(ids.getPaymentAmount()))
					ids.setPaymentAmount("");
				if(ids.getIntegratedDisclosureSubsectionType().equals("OtherCostsSubtotal"))
					if (numLines() == 1)
						((IDCostsTableRow)getRow(0)).appendPayment(ids);
					else
						addRow(new IDCostsTableRow(ids, "Other Costs Subtotals (E + F + G + H)"));
			}
		}
		void drawOverlayGrid(Page page, float[] widths, Columns[] columns, float x, float y) {
			if (headerLabel == null || columns.length == 0 || columns[0] != Columns.BuyerAtClosing)
				return;
			//float[] owidths = { widths[0] + widths[1] + widths[2], widths[3] + widths[4], widths[5] };
			float[] bwidths = { widths[0] + widths[1] + widths[2], widths[3] + widths[4], widths[5] };
			float[] owidths = null;
			if (widths.length >= 8){
				owidths = Arrays.copyOf(bwidths, 5);
				owidths[3] = widths[6];
				owidths[4] = widths[7];
			} else {
				owidths = Arrays.copyOf(bwidths, bwidths.length);
			}
			float[] heights = new float[2];
			heights[0] = getHeaderHeight(page, widths);
			if (heights[0] == 0)
				return;
			heights[1] = heights[0];
			Grid overlayGrid = new Grid(heights.length, heights, owidths.length, owidths);
			try {
				overlayGrid.setLineBorder(0, Position.TOP, Color.BLACK, 1f/72f);
				overlayGrid.setLineBorder(1, Position.TOP, Color.BLACK, 1f/72f);
				overlayGrid.setLineBorder(1, Position.BOTTOM, Color.BLACK, 1f/72f);
				overlayGrid.setLineBorder(1, Position.LEFT, Color.DARK_GRAY, 2f/72f);
				overlayGrid.setLineBorder(1, Position.RIGHT, Color.DARK_GRAY, 2f/72f);
				if (widths.length >= 8)
					overlayGrid.setCellBorder(0, 4, Position.LEFT, Color.WHITE, 2f/72f);
				overlayGrid.setLineHorizontalAlignment(1, Dimension.COLUMN, HorizontalAlignment.CENTER);
				overlayGrid.setCellShade(0, 1, Color.LIGHT_GRAY);
				overlayGrid.setCellText(0, 1, headerAmt);
				overlayGrid.draw(page, x, y - 2*heights[0]);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	class TotalClosingCostsTable extends CostsTable {
		private Region sellerAmount = null;
		private Region sellerAmountOutside = null;
		
		protected Drawable getSellerAmount() {
			return sellerAmount;
		}
			protected Drawable getSellerAmountOutside() {
				return sellerAmountOutside;
		}
		
		TotalClosingCostsTable(InputData data) {
			
		
			super(0, Color.BLACK);
	
			String suffix = " (Borrower-Paid)";
			if (CostsTableRow.noBuyer(data))
				suffix = "";
			setHeader("J. TOTAL CLOSING COSTS" + suffix, StringFormatter.DOLLARS.formatString(
					data.getClosingMap().getClosingMapValue("INTEGRATED_DISCLOSURE_SECTION_SUMMARY_DETAIL.TotalClosingCosts")));

			// Set seller - only
			if (data.isSellerOnly()) {
				for (ID_Subsection ids : data.getIdsList())
					if (ids.getIntegratedDisclosureSubsectionType().equals("TotalClosingCostsSellerOnly") &&
							ids.getPaymentPaidByType().equals("Seller") &&  !"0.00".equals(ids.getPaymentAmount()))
						if (ids.isPaidOutsideOfClosingIndicator()){
							sellerAmountOutside = CostsTableRow.Columns.CostLabel.formatString(StringFormatter.DOLLARS.formatString(ids.getPaymentAmount()));
						} else {
							sellerAmount = CostsTableRow.Columns.Number.formatString(StringFormatter.DOLLARS.formatString(ids.getPaymentAmount()));
						}
				return;
			}
			
			// Set first line, Closing Costs Subtotals
			for (ID_Subsection ids : data.getIdsList()){
				if ("0.00".equals(ids.getPaymentAmount()))
					ids.setPaymentAmount("");
				if(ids.getIntegratedDisclosureSubsectionType().equals("ClosingCostsSubtotal"))
					if (numLines() == 1)
						((IDCostsTableRow)getRow(0)).appendPayment(ids);
					else
						addRow(new IDCostsTableRow(ids, "Closing Costs Subtotals (D + I)"));
			}
			// Set second line, Lender Credits
			ID_Subsection lenderCredit = null;
			for (ID_Subsection ids : data.getIdsList())
				if (ids.getIntegratedDisclosureSubsectionType().equals("LenderCredits")) {
					lenderCredit = ids;
					break;
				}
			if (lenderCredit == null)
				addRow(new CostsTableRow().add(Columns.Number, "Lender Credits"));
			else {
				String str = "Lender Credits";
				if (!lenderCredit.getLenderTolerance().equals("") && !"0.00".equals(lenderCredit.getLenderTolerance()))
					str += "(Includes " + StringFormatter.DOLLARS.formatString(lenderCredit.getLenderTolerance()) + " credit for increase in Closing Costs above legal Limit)";
				addRow(new IDCostsTableRow(lenderCredit, str));
			}
		}
		void drawOverlayGrid(Page page, float[] widths, Columns[] columns, float x, float y) {
			if (headerLabel == null || columns.length == 0 || columns[0] != Columns.BuyerAtClosing)
				return;
			//float[] owidths = { widths[0] + widths[1] + widths[2], widths[3] + widths[4], widths[5] };
			float[] bwidths = { widths[0] + widths[1] + widths[2], widths[3] + widths[4], widths[5] };
			float[] owidths = null;
			if (widths.length >= 8){
				owidths = Arrays.copyOf(bwidths, 5);
				owidths[3] = widths[6];
				owidths[4] = widths[7];
			} else {
				owidths = Arrays.copyOf(bwidths, bwidths.length);
			}
			float[] heights = new float[1];
			heights[0] = getHeaderHeight(page, widths);
			if (heights[0] == 0)
				return;
			Grid overlayGrid = new Grid(heights.length, heights, owidths.length, owidths);
			try {
				overlayGrid.setLineBorder(0, Position.TOP, Color.BLACK, 1f/72f);
				overlayGrid.setLineBorder(1, Position.TOP, Color.BLACK, 1f/72f);
				overlayGrid.setCellBorder(0, 2, Position.TOP, Color.WHITE, 2f/72f);
				if (owidths.length >= 4)
					overlayGrid.setCellBorder(0, 3, Position.TOP, Color.WHITE, 2f/72f);
				if (owidths.length >= 5)
					overlayGrid.setCellBorder(0, 4, Position.TOP, Color.WHITE, 2f/72f);
				overlayGrid.setLineBorder(1, Position.LEFT, Color.DARK_GRAY, 2f/72f);
				overlayGrid.setLineBorder(1, Position.RIGHT, Color.DARK_GRAY, 2f/72f);
				if (owidths.length >= 5)
					overlayGrid.setCellBorder(0, 4, Position.LEFT, Color.WHITE, 2f/72f);
				overlayGrid.setLineHorizontalAlignment(1, Dimension.COLUMN, HorizontalAlignment.CENTER);
				overlayGrid.setCellShade(0, 1, Color.LIGHT_GRAY);
				overlayGrid.setCellText(0, 1, headerAmt);
				overlayGrid.draw(page, x, y - heights[0]);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private boolean isPropertyTax(String type) {
		return	   type.equalsIgnoreCase("BoroughPropertyTax")
				|| type.equalsIgnoreCase("CityPropertyTax")
				|| type.equalsIgnoreCase("CountyPropertyTax")
				|| type.equalsIgnoreCase("DistrictPropertyTax")
				|| type.equalsIgnoreCase("PropertyTaxes")
				|| type.equalsIgnoreCase("StatePropertyTax")
				|| type.equalsIgnoreCase("TownPropertyTax");
	}

	private void drawHeaderGrid(Page page, InputData data, float[] widths, Columns[] columnNames, float x, float y) {
		Grid title = null;
		Grid header1 = null;
		
		if (withHeader) {
			// Draw top-of-line title
			float theights[] = { 2 * CostsTableRow.lineHeight };
			float twidths[]  = { 7.5f };
			title = new Grid(1, theights, 1, twidths);
			title.setCellText(0, 0, new FormattedText("Closing Cost Details", Text.HEADER_MEDIUM));
			title.setLineBorder(0, Position.BOTTOM, Color.BLACK, 1f/72f);
	
			// Draw table column titles
			float h1heights[] = { CostsTableRow.lineHeight };
			float h1widths[] = CostsTableRow.columnWidthsH1(data);
			header1 = new Grid(1, h1heights, h1widths.length, h1widths);
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
		}

		float h2heights[] = { CostsTableRow.lineHeight };
		float h2widths[] = CostsTableRow.columnWidthsH2(data);
		int h2Columns = columnNames.length + 1;
		Grid header2 = new Grid(1, h2heights, h2widths.length, h2widths);
		header2.setLineHorizontalAlignment(0, Dimension.ROW, HorizontalAlignment.CENTER);
		header2.getCell(0, 0).setHorizontalAlignment(HorizontalAlignment.LEFT).setBackground(new Tab())
			.setMargin(Direction.LEFT, leftIndent).setForeground(new FormattedText("Other Costs", TAB));
		if (withHeader)
			for (int i = 1; i < h2Columns; i++) {
				if (i % 2 == 1)
					header2.setCellBorder(0, i, Position.LEFT, Color.DARK_GRAY, 2f/72f);
				header2.setCellShade(0, i, Color.LIGHT_GRAY);
				if (columnNames[i-1] == Columns.Other)
					header2.setCellText(0, i, new FormattedText("Others", Text.TABLE_HEADER_LARGE));
				else
					header2.setCellText(0, i, new FormattedText(i % 2 == 1 ? "At Closing" : "Before Closing", SMALL_HEADER));
			}
		
		try {
			header2.draw(page, x, y);
			if (header1 != null)
				header1.draw(page, x, y + CostsTableRow.lineHeight);
			if (title != null)
				title.draw(page, x, y + 2*CostsTableRow.lineHeight);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void extractData(Page page, InputData data) {
		int pLines = 2;
		if (data.isPages2A2B())
			pLines = 12;
		tables.add(new TaxesAndGovernmentFeesTable(pLines,data));
		pLines = 5;
		if (data.isPages2A2B())
			pLines = 15;
		tables.add(new PrepaidsTable(pLines,data));
		pLines = 8;
		if (data.isPages2A2B())
			pLines = 17;
		tables.add(new EscowAtClosingTable(pLines,page, data));
		pLines = 6;
		if (data.isPages2A2B())
			pLines = 17;
		tables.add(new OtherCostsTable(pLines,data));
		if (!data.isSellerOnly())
			tables.add(new TotalOtherCostsTable(data));
		tables.add(new TotalClosingCostsTable(data));
	}

	public float getHeight(Page page, InputData data) {
		float height = 0;
		for (int i = 0; i < tables.size(); i++)
			height += tables.get(i).getHeight(page, CostsTableRow.columnWidths((InputData)data));
		return height + headerHeight + headerBuffer + CostsTableRow.lineHeight;
	}

	public void setPosition(float position) {
		this.position = position;
	}
	
	public void stretch(Page page, InputData data, float height) {
		int[] stretchTables = { 1, 2, 3 };
		CostsTable.stretch(page, tables, stretchTables, CostsTableRow.columnWidths(data), height - 5*CostsTableRow.lineHeight - headerHeight - headerBuffer);
	}
	
	public void draw(Page page, Object inputData) throws IOException {
		Columns[] columnNames = CostsTableRow.columnNames((InputData)inputData);
		float[] columnWidths = CostsTableRow.columnWidths((InputData)inputData);
		float y = (position == 0 ? page.height - page.topMargin : position) - CostsTableRow.lineHeight;
		drawHeaderGrid(page, (InputData)inputData, columnWidths, columnNames, page.leftMargin, y);
		y -= .5f/72f;
		for (CostsTable table : tables) {
			y -= table.getHeight(page, columnWidths);
			if (tables.indexOf(table) == tables.size() - 1)
				y -= CostsTableRow.lineHeight;
			table.draw(page, columnWidths, columnNames, page.leftMargin, y);
		}
	}
}
