package com.actualize.mortgage.domainmodels;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.actualize.mortgage.datalayer.Escrows;
import com.actualize.mortgage.datalayer.FeeTypes;
import com.actualize.mortgage.datalayer.Fees;
import com.actualize.mortgage.datalayer.InputData;
import com.actualize.mortgage.datalayer.PageTwo;
import com.actualize.mortgage.datalayer.Prepaids;
import com.actualize.mortgage.pdfbuilder.Color;
import com.actualize.mortgage.pdfbuilder.FormattedText;
import com.actualize.mortgage.pdfbuilder.Grid;
import com.actualize.mortgage.pdfbuilder.Page;
import com.actualize.mortgage.pdfbuilder.Region;
import com.actualize.mortgage.pdfbuilder.Text;
import com.actualize.mortgage.pdfbuilder.Grid.Position;

public class LayoutPageTwo {
	
	public static boolean sections(Page page, InputData inputData) throws IOException{
		List<Fees> feeList = inputData.getFeeList();
		PageTwo pageTwo = new PageTwo();
		
		List<Prepaids> prepaidList = inputData.getPrepaidList();
		List<Escrows>  escrowList  = inputData.getEscrowList();

		float width1 = 0;
		float width2 = 0;
		boolean preAlloted = false;
		
		//LOAN COSTS
		//Fee Loop
		boolean LoanDiscountPoints = true;
		for (Fees feeLocal : feeList) {
			preAlloted = false;
			if (feeLocal.getIntegratedDisclosureSectionType().equals("OriginationCharges")){
				if (!feeLocal.getType().equals("LoanDiscountPoints")) {
					width1 = ( new FormattedText(feeLocal.getLabel(),Text.TABLE_TEXT)).width(page);
					width2 = ( new FormattedText(feeLocal.getPaymentToEntity(),Text.TABLE_TEXT)).width(page);
					if(LoanDiscountPoints){
						LoanDiscountPoints = false;
						preAlloted = true;
					}
					if(allocateRows(PageTwo.ORIGINATION_CHARGES, width1, width2, pageTwo, preAlloted)){
						return true;
					}
				}
			} else if (feeLocal.getIntegratedDisclosureSectionType().equals("ServicesBorrowerDidNotShopFor")){
				width1 = ( new FormattedText(feeLocal.getLabel(),Text.TABLE_TEXT)).width(page);
				width2 = ( new FormattedText(feeLocal.getPaymentToEntity(),Text.TABLE_TEXT)).width(page);
				if(allocateRows(PageTwo.DID_NOT_SHOP_FOR, width1, width2, pageTwo, preAlloted)){
					return true;
				}

			} else if (feeLocal.getIntegratedDisclosureSectionType().equals("ServicesBorrowerDidShopFor")){
				width1 = ( new FormattedText(feeLocal.getLabel(),Text.TABLE_TEXT)).width(page);
				width2 = ( new FormattedText(feeLocal.getPaymentToEntity(),Text.TABLE_TEXT)).width(page);
				if(allocateRows(PageTwo.DID_SHOP_FOR, width1, width2, pageTwo, preAlloted)){
					return true;
				}
				//OTHER FEES
			} else if(feeLocal.getIntegratedDisclosureSectionType().equals("OtherCosts")
					&& Arrays.asList(FeeTypes.otherTypes).contains(feeLocal.getType())){
				String str = feeLocal.getLabel();
				if(feeLocal.isOptionalCostIndicator()){
					str+= " (Optional) ";
				}
				width1 = ( new FormattedText(str,Text.TABLE_TEXT)).width(page);
				width2 = ( new FormattedText("to "+feeLocal.getPaymentToEntity(),Text.TABLE_TEXT)).width(page);
				if (allocateRows(PageTwo.OTHER_FEES, width1, width2, pageTwo, preAlloted)){
					return true;
				}
			} else if (feeLocal.getIntegratedDisclosureSectionType().equals("TaxesAndOtherGovernmentFees")) {
				if(Arrays.asList(FeeTypes.transferTypes).contains(feeLocal.getType())){
					width1 = ( new FormattedText(feeLocal.getLabel(),Text.TABLE_TEXT)).width(page);
					width2 = ( new FormattedText(feeLocal.getPaymentToEntity(),Text.TABLE_TEXT)).width(page);
					if(allocateRows(PageTwo.TAXES_GOVERNMENT, width1, width2, pageTwo, preAlloted)){
						return true;
					}
				}
			}
		}//end fee for loop
		
		//PRE PAIDS
		//pre alloted lines
		boolean HomeownersInsurancePremium = true;
		boolean MortgageInsurancePremium = true;
		boolean PrepaidInterest = true;
		//		if(allocateRows(PageTwo.PREPAIDS, width1, width2, pageTwo, preAlloted)){
		//			return true;
		//		}
		//8.5
		String str1 = "Homeowners Insurance Premium";
		String str2 = "( mo.) to ";
		for (Prepaids prepaid:prepaidList){
			preAlloted = false;
			if (prepaid.getIntegratedDisclosureSectionType().equals("Prepaids") 
							&& prepaid.getType().equals("HomeownersInsurancePremium")) {
				str2 = "("+prepaid.getPrepaidItemMonthsPaidCount() + " mo.) to " + prepaid.getPaymentToEntity();
				if (HomeownersInsurancePremium){
					preAlloted = true;
					HomeownersInsurancePremium = false;
				}
				width1 = ( new FormattedText(str1,Text.TABLE_TEXT)).width(page);
				width2 = ( new FormattedText(str2,Text.TABLE_TEXT)).width(page);
				if(allocateRows(PageTwo.PREPAIDS, width1, width2, pageTwo, preAlloted)){
					return true;
				}
			}

			//8.6
			str1 = "Mortgage Insurance Premium";
			str2 = "( mo.) to     ";
			if (prepaid.getIntegratedDisclosureSectionType().equals("Prepaids") && prepaid.getType().equals("MortgageInsurancePremium")) {
				str2 = "(" + prepaid.getPrepaidItemMonthsPaidCount() + " mo.) to " + prepaid.getPaymentToEntity();
				if (MortgageInsurancePremium){
					preAlloted = true;
					MortgageInsurancePremium = false;
				}
				width1 = ( new FormattedText(str1,Text.TABLE_TEXT)).width(page);
				width2 = ( new FormattedText(str2,Text.TABLE_TEXT)).width(page);
				if(allocateRows(PageTwo.PREPAIDS, width1, width2, pageTwo, preAlloted)){
					return true;
				}
			}

			//8.7 Prepaid Interest
			str1 = "Prepaid Interest ";
			str2 = "($   per day from   to   ) ";
			if (prepaid.getType().equals("PrepaidInterest")) {
				str2 = "(" + StringFormatter.DOLLARS.formatString(prepaid.getPrepaidItemPerDiemAmount()) + " per day from " +
								StringFormatter.DATE.formatString(prepaid.getPrepaidItemPaidFromDate()) + " to " +
								StringFormatter.DATE.formatString(prepaid.getPrepaidItemPaidThroughDate()) + ")";
				if (PrepaidInterest){
					preAlloted = true;
					PrepaidInterest = false;
				}
				width1 = ( new FormattedText(str1,Text.TABLE_TEXT)).width(page);
				width2 = ( new FormattedText(str2,Text.TABLE_TEXT)).width(page);
				if(allocateRows(PageTwo.PREPAIDS, width1, width2, pageTwo, preAlloted)){
					return true;
				}
			}

			//8.7 Property Taxes
			str1 = "Property Taxes ";
			str2 = "( mo.) to     ";
			if (prepaid.getType().equals("CityPropertyTax") || prepaid.getType().equals("CountyPropertyTax")) {
				str2 = "(" + prepaid.getPrepaidItemMonthsPaidCount() + " mo.) to " + prepaid.getPaymentToEntity();
				if (PrepaidInterest){
					preAlloted = true;
					PrepaidInterest = false;
				}
				width1 = ( new FormattedText(str1,Text.TABLE_TEXT)).width(page);
				width2 = ( new FormattedText(str2,Text.TABLE_TEXT)).width(page);
				if(allocateRows(PageTwo.PREPAIDS, width1, width2, pageTwo, preAlloted)){
					return true;
				}
			}
		}
		
		//pageTwo.ESCROWS
		//pre alotted
//		boolean HomeownersInsurance = true;
//		boolean MortgageInsurance = true;
//		boolean PropertyTax = true;
		for(Escrows escrowLocal:escrowList){
			if(escrowLocal.getIntegratedDisclosureSectionType().equals("InitialEscrowPaymentAtClosing")){
				if(escrowLocal.getType().equals("PropertyTax")){
					width1 = ( new FormattedText(escrowLocal.getLabel(),Text.TABLE_TEXT)).width(page);
					width2 = ( new FormattedText(escrowLocal.getPaymentToEntity(),Text.TABLE_TEXT)).width(page);
					if(allocateRows(PageTwo.ESCROWS, width1, width2, pageTwo, preAlloted)){
						return true;
					}
				}
			}
		}//end pageTwo.ESCROWS loop
		setupSections(pageTwo);
		inputData.setPageTwo(pageTwo);
		
		return false;
	}
	private static boolean allocateRows(Integer here, float width1, float width2, 
			PageTwo pageTwo, boolean preAlloted) {
		int lines = 1;
		float colwidth = 120f/72f;
		//System.out.println("start:"+here+" allocate lines:"+lines);
		if (width1>=colwidth || width2>=colwidth){
			//System.out.println("section:"+here+" Label:"+width1+" ToEntity:"+width2);
			lines = 2;
		}
		if(preAlloted){
			lines--;
		}
		if(lines < 1){
			return false;
		}
		if (pageTwo.getSlack(here) >= lines){
			pageTwo.useSlack(here, lines);
		} else {
			if(!steal(here, lines, pageTwo)){
				//System.err.println("Slack Overflow switching to 2A and 2B");
				return true;
			}
		}
		return false;
	}
	public static boolean steal( Integer to, Integer lines, PageTwo pageTwo){
		// find largest slack area on page
		int max = 0;
		int from = 0;
		for (int i = 0; i < 10; i++){
			if(pageTwo.getSlack(i) > max){
				max = pageTwo.getSlack(i);
				from = i;
			}
		}
		//System.out.println("from:"+from+" slackFrom:"+pageTwo.getSlack(from)+" to:"+to+" requiredTo:"+pageTwo.getRequired(to)+" lines:"+lines);
		if (max >= lines && to < 9 && from < 9){
			pageTwo.setSlack(from, pageTwo.getSlack(from) - lines);
			pageTwo.setRequired(to, pageTwo.getRequired(to) + lines);
			//System.out.println("from:"+from+" slack:"+pageTwo.getSlack(from)+" to:"+to+" required:"+pageTwo.getRequired(to));
			setupSections(pageTwo);
			return true;
		}
		return false;
	}
	
	public static void setupSections(PageTwo pageTwo) {
		//loan costs section
		pageTwo.setStart(PageTwo.ORIGINATION_CHARGES, 2);
		pageTwo.setStart(PageTwo.DID_NOT_SHOP_FOR, pageTwo.getStart(PageTwo.ORIGINATION_CHARGES) 
				+ pageTwo.getSlack(PageTwo.ORIGINATION_CHARGES) + pageTwo.getRequired(PageTwo.ORIGINATION_CHARGES));
		pageTwo.setStart(PageTwo.DID_SHOP_FOR, pageTwo.getStart(PageTwo.DID_NOT_SHOP_FOR)  
				+ pageTwo.getSlack(PageTwo.DID_NOT_SHOP_FOR) + pageTwo.getRequired(PageTwo.DID_NOT_SHOP_FOR));
		pageTwo.setStart(PageTwo.TOTAL_LOAN_COSTS, pageTwo.getStart(PageTwo.DID_SHOP_FOR) 
				+ pageTwo.getSlack(PageTwo.DID_SHOP_FOR) + pageTwo.getRequired(PageTwo.DID_SHOP_FOR));
		Integer value = pageTwo.getStart(PageTwo.TOTAL_LOAN_COSTS)  + 2;
		pageTwo.setLoanCostsGridHeight(value);
		//other costs section
		pageTwo.setStart(PageTwo.TAXES_GOVERNMENT, 1);
		pageTwo.setStart(PageTwo.PREPAIDS, pageTwo.getStart(PageTwo.TAXES_GOVERNMENT)
				+ pageTwo.getSlack(PageTwo.TAXES_GOVERNMENT) + pageTwo.getRequired(PageTwo.TAXES_GOVERNMENT));
		pageTwo.setStart(PageTwo.ESCROWS, pageTwo.getStart(PageTwo.PREPAIDS) 
				+ pageTwo.getSlack(PageTwo.PREPAIDS) + pageTwo.getRequired(PageTwo.PREPAIDS));
		pageTwo.setStart(PageTwo.OTHER_FEES, pageTwo.getStart(PageTwo.ESCROWS)  
				+ pageTwo.getSlack(PageTwo.ESCROWS) + pageTwo.getRequired(PageTwo.ESCROWS));
		pageTwo.setStart(PageTwo.TOTAL_OTHER_FEES, pageTwo.getStart(PageTwo.OTHER_FEES) 
				+ pageTwo.getSlack(PageTwo.OTHER_FEES) + pageTwo.getRequired(PageTwo.OTHER_FEES));
		pageTwo.setStart(PageTwo.TOTAL_CLOSING,  pageTwo.getStart(PageTwo.TOTAL_OTHER_FEES) 
				+ pageTwo.getSlack(PageTwo.TOTAL_OTHER_FEES) + pageTwo.getRequired(PageTwo.TOTAL_OTHER_FEES));
		value = pageTwo.getStart(PageTwo.TOTAL_CLOSING) + 2;
		pageTwo.setOtherCostsGridHeight(value);
	}
	
	public static void expandFees(InputData inputData){
		PageTwo pageTwo = inputData.getPageTwo();
		if (pageTwo == null){
			pageTwo = new PageTwo();
		}
		pageTwo.setSlack(PageTwo.ORIGINATION_CHARGES, 19);
		pageTwo.setSlack(PageTwo.DID_NOT_SHOP_FOR, 20);
		pageTwo.setSlack(PageTwo.DID_SHOP_FOR, 21);
		pageTwo.setSlack(PageTwo.TOTAL_LOAN_COSTS, 0);	
		pageTwo.setSlack(PageTwo.TAXES_GOVERNMENT, 12);
		pageTwo.setSlack(PageTwo.PREPAIDS, 10);
		pageTwo.setSlack(PageTwo.ESCROWS, 9);
		pageTwo.setSlack(PageTwo.OTHER_FEES, 14);
		pageTwo.setSlack(PageTwo.TOTAL_OTHER_FEES, 0);
		pageTwo.setSlack(PageTwo.TOTAL_CLOSING, 0);
		
		pageTwo.setRequired(PageTwo.ORIGINATION_CHARGES, 2);
		pageTwo.setRequired(PageTwo.DID_NOT_SHOP_FOR, 1);
		pageTwo.setRequired(PageTwo.DID_SHOP_FOR, 1);
		pageTwo.setRequired(PageTwo.TOTAL_LOAN_COSTS, 3);	
		pageTwo.setRequired(PageTwo.TAXES_GOVERNMENT, 2);
		pageTwo.setRequired(PageTwo.PREPAIDS, 4);
		pageTwo.setRequired(PageTwo.ESCROWS, 4);
		pageTwo.setRequired(PageTwo.OTHER_FEES, 1);
		pageTwo.setRequired(PageTwo.TOTAL_OTHER_FEES, 3);
		pageTwo.setRequired(PageTwo.TOTAL_CLOSING, 2);
		pageTwo.setExpandedFees(true);
		setupSections(pageTwo);
		inputData.setPageTwo(pageTwo);
		
	}
	
	public static int writeLabel(Page page, Grid labelGrid, String str1, String str2, int lineNumber, int localRow, PageTwo pageTwo) throws IOException {
		float narrowRule = 1f/72f;
		
		labelGrid.setCellText(localRow, 0, new FormattedText(String.format("%02d", lineNumber), Text.TABLE_NUMBER));
		FormattedText fT1 =  new FormattedText( str1,Text.TABLE_TEXT);
		if (fT1.width(page)<=pageTwo.getWidthLabel1()){
			labelGrid.setCellText(localRow, 1, fT1);
		} else {
			wrapLabel(page, labelGrid, str1, localRow++, 1, pageTwo);
		}
		FormattedText fT2 =  new FormattedText( str2,Text.TABLE_TEXT);
		if (fT2.width(page)<=pageTwo.getWidthLabel2()){
			labelGrid.setCellText(localRow, 2, fT2);
		} else {
			wrapLabel(page, labelGrid, str2, localRow++, 2, pageTwo);
		}
		labelGrid.setLineBorder( localRow, Position.BOTTOM, Color.LIGHT_GRAY, narrowRule);
		localRow++;
		return localRow;
	}

	private static void wrapLabel(Page page, Grid labelGrid, String str1, int row, int col, PageTwo pageTwo)
			throws IOException {
		String[] temp;
		int iTrial = 0;
		temp = str1.split(" ");
		String str3 = temp[0];
		
		pageTwo.setDoubleLines(row);
		float colWidth = pageTwo.getWidthLabel1();
		if(col == 2){
			colWidth = pageTwo.getWidthLabel2();
		}
		FormattedText ft = new FormattedText(temp[0],Text.TABLE_TEXT);
		iTrial = 0;
		while (ft.width(page) <= colWidth && iTrial < temp.length - 1){
			iTrial++;
			str3 = str3 + " " + temp[iTrial];
			ft = new FormattedText(str3, Text.TABLE_TEXT);
		}
		str3 = temp[0];
		for (int i = 1; i<iTrial; i++){
			str3 = str3+" "+temp[i];
		}
		String str4 = temp[iTrial];
		for(int i = iTrial+1;i < temp.length;i++){
			str4 = str4 +" "+temp[i];
		}
		Region label = (new Region())
			.append(new FormattedText(str3, Text.TABLE_TEXT))
			.append(new FormattedText(str4, Text.TABLE_TEXT));
		labelGrid.setCellText(row+1, col, label);
		return;
	}
}
