package com.actualize.closingdisclosure.domainmodels;

import java.io.IOException;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.actualize.closingdisclosure.datalayer.ClosingMap;
import com.actualize.closingdisclosure.datalayer.InputData;
import com.actualize.closingdisclosure.pdfbuilder.FormattedText;
import com.actualize.closingdisclosure.pdfbuilder.Page;
import com.actualize.closingdisclosure.pdfbuilder.Paragraph;
import com.actualize.closingdisclosure.pdfbuilder.Region;
import com.actualize.closingdisclosure.pdfbuilder.Spacer;
import com.actualize.closingdisclosure.pdfbuilder.Text;

public class CostsTableRow {
	final static Columns[] columnAll      = { Columns.BuyerAtClosing, Columns.BuyerOutsideClosing, Columns.SellerAtClosing, Columns.SellerOutsideClosing, Columns.Other };
	final static Columns[] columnNoSeller = { Columns.BuyerAtClosing, Columns.BuyerOutsideClosing, Columns.Other };
	// final static Columns[] columnNoBuyer  = { Columns.SellerAtClosing, Columns.SellerOutsideClosing, Columns.Other };
	final static Columns[] columnNoBuyer  = { Columns.SellerAtClosing, Columns.SellerOutsideClosing };
	static float[]   widthsAll      = { 12f/72f, 117f/72f, 171f/72f, 48f/72f, 48f/72f, 48f/72f, 48f/72f, 48f/72f };
	final static float[]   widthsPartial  = { 12f/72f, 140f/72f, 208f/72f, 60f/72f, 60f/72f, 60f/72f };
	final static float[]   widthsNoBuyer  = { 12f/72f, 200f/72f, 208f/72f, 60f/72f, 60f/72f };
	final static float[]   widthsH1All    = { 300f/72f, 96f/72f, 96f/72f, 48f/72f };
	final static float[]   widthsH1Partial= { 360f/72f, 120f/72f, 60f/72f };
	final static float[]   widthsH1NoBuyer= { 420f/72f, 120f/72f };
	final static float[]   widthsH2All    = { 300f/72f, 48f/72f, 48f/72f, 48f/72f, 48f/72f, 48f/72f };
	final static float[]   widthsH2Partial= { 360f/72f, 60f/72f, 60f/72f, 60f/72f };
	final static float[]   widthsH2NoBuyer= { 420f/72f, 60f/72f, 60f/72f };
	final static float     lineHeight     = 9.45f/72f;	

	final Object cost;
	final boolean wrap;
	private static InputData inputData;
	enum Columns {
		Number, CostLabel, ToEntity, BuyerAtClosing, BuyerOutsideClosing, SellerAtClosing, SellerOutsideClosing, Other;
		
		public StringFormatter formatter() {
			switch (this) {
			case Number:
			case CostLabel:
			case ToEntity:
				return null;
			default:
				return StringFormatter.DOLLARS;
			}
		}

		public Region formatString(String str) {
			return formatString(null, str, false);
		}

		public Region formatString(Page page, String str, boolean alignPer) {
			return formatText(page, formatter() != null && str != null && !"".equals(str) && !"0".equals(str) ? formatter().formatString(str) : str, alignPer);
		}

		public Region formatText(String str) {
			return formatText(null, str, false);
		}

		public Region formatText(Page page, String str, boolean alignPer) {
			if (str == null)
				return null;
			Paragraph p = new Paragraph();
			Spacer mSpacer = null;
			String getMonth = "";
			if (alignPer && str.indexOf("per") != -1) {
				float monthlyAmountWidth = 0;
				try {
					monthlyAmountWidth = new FormattedText(str.substring(0, str.indexOf("per")), Text.TABLE_TEXT).width(page);
					str = str.replaceFirst(" per", "    per");
					getMonth = str.substring(str.indexOf("for"), str.length() - 1);
						getMonth = getMonth.substring(getMonth.indexOf(" ")+1, getMonth.length() - 3);
					mSpacer = new Spacer((new FormattedText("12",Text.TABLE_TEXT).width(page) - new FormattedText(getMonth,Text.TABLE_TEXT).width(page))/2,0);
					if(inputData!=null && inputData.getClosingMap()!=null){
						ClosingMap closingMap = inputData.getClosingMap();
						if (closingMap.getClosingMapValue("TERMS_OF_LOAN.LoanPurposeType").equalsIgnoreCase("Refinance"))
							p.append(new Spacer(-.30f - monthlyAmountWidth, 0));
						else if(inputData.isSellerOnly())
							p.append(new Spacer(-1.13f - monthlyAmountWidth, 0));
						else
							p.append(new Spacer( - monthlyAmountWidth + 0.02f, 0));
					}else{
							p.append(new Spacer(- monthlyAmountWidth + 0.02f , 0));
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				p.append(new FormattedText(str.substring(0, str.indexOf("per")), Text.TABLE_TEXT)).append(new FormattedText("per month for ", Text.TABLE_TEXT)).append(mSpacer).append(new FormattedText(getMonth, Text.TABLE_TEXT)).append(mSpacer).append(new FormattedText(" mo.", Text.TABLE_TEXT));
			}
			else
				p.append(new FormattedText(str, Text.TABLE_TEXT));
			return new Region().append(p);
		}
	}
		
	private float margin = 2f/72f;
	private TreeMap<Columns, Region> data = new TreeMap<Columns, Region>();
	
	CostsTableRow() {
		this(true);
	}
	
	CostsTableRow(boolean wrap) {
		this.cost = null;
		this.wrap = wrap;
	}
	
	CostsTableRow(Object cost) {
		this(cost, true);
	}
	
	CostsTableRow(Object cost, boolean wrap) {
		this.cost = cost;
		this.wrap = wrap;
	}
	
	Region item(Columns key) {
		return data.get(key);
	}
	
	CostsTableRow add(Columns key, Region value) {
		data.put(key, value);
		return this;
	}
	
	CostsTableRow add(Page page, Columns key, String str, boolean alignPer) {
		return add(key, key.formatString(page, str, alignPer));
	}
	
	CostsTableRow add(Columns key, String str) {
		return add(null, key, str, false);
	}
	
	CostsTableRow addNoFormat(Columns key, String str) {
		return add(key, key.formatText(null, str, false));
	}

	float getHeight(Page page, float widths[]) {
		if (!wrap)
			return lineHeight;
		Region region;
		float height = lineHeight;
		region = item(Columns.Number);
		if (region != null) {
			try {
				region.wrapAt(page, widths[0] + widths[1] + widths[2] - margin);
				float h = region.height(page);
				if (h > height) height = h;
			} catch (Exception e) {
			}
		}
		region = item(Columns.CostLabel);
		if (region != null) {
			try {
				region.wrapAt(page, widths[1] - margin);
				float h = region.height(page);
				if (h > height) height = h;
			} catch (Exception e) {
			}
		}
		region = item(Columns.ToEntity);
		if (region != null) {
			try {
				region.wrapAt(page, widths[2] - margin);
				float h = region.height(page);
				if (h > height) height = h;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return height;
	}
	CostsTableRow setInputData(InputData data){
		inputData =  data;
		return this;
	}
	public static boolean noBuyer(InputData data) {
		inputData =  data;
		return data.isSellerOnly();
	}
	
	public static boolean noSeller(ClosingMap closingMap) {
		return closingMap.getClosingMapValue("TERMS_OF_LOAN.LoanPurposeType").equalsIgnoreCase("Refinance")
				&& !closingMap.getClosingMapValue("INTEGRATED_DISCLOSURE_DETAIL.IntegratedDisclosureHomeEquityLoanIndicator").equalsIgnoreCase("true");
	}

	public static Columns[] columnNames(InputData data) {
		if (noBuyer(data))
			return columnNoBuyer;
		if (noSeller(data.getClosingMap()))
			return columnNoSeller;
		return columnAll;
	}

	public static float[] columnWidths(InputData data) {
		if (noBuyer(data))
			return widthsNoBuyer;
		if (noSeller(data.getClosingMap()))
			return widthsPartial;
		// for Docs Direct give a little more room to the label column
		if (data.isDocsDirect()){
			widthsAll[1] = 137f/72f;
			widthsAll[2] = 151f/72f;
		}
		return widthsAll;
	}

	public static float[] columnWidthsH1(InputData data) {
		if (noBuyer(data))
			return widthsH1NoBuyer;
		if (noSeller(data.getClosingMap()))
			return widthsH1Partial;
		return widthsH1All;
	}

	public static float[] columnWidthsH2(InputData data) {
		if (noBuyer(data))
			return widthsH2NoBuyer;
		if (noSeller(data.getClosingMap()))
			return widthsH2Partial;
		return widthsH2All;
	}
}
