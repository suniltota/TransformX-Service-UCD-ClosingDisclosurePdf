package com.actualize.mortgage.domainmodels;

import java.io.IOException;

import com.actualize.mortgage.datalayer.InputData;
import com.actualize.mortgage.datalayer.PartyData;
import com.actualize.mortgage.datalayer.PartyRoleTypes;
import com.actualize.mortgage.pdfbuilder.Color;
import com.actualize.mortgage.pdfbuilder.FormattedText;
import com.actualize.mortgage.pdfbuilder.Grid;
import com.actualize.mortgage.pdfbuilder.Page;
import com.actualize.mortgage.pdfbuilder.Section;
import com.actualize.mortgage.pdfbuilder.Text;
import com.actualize.mortgage.pdfbuilder.Typeface;
import com.actualize.mortgage.pdfbuilder.Grid.Dimension;
import com.actualize.mortgage.pdfbuilder.Grid.Position;
import com.actualize.mortgage.pdfbuilder.TextBox.Direction;
import com.actualize.mortgage.pdfbuilder.TextBox.VerticalAlignment;

public class ContactInformationSeller implements Section {
	public static final Text TAB               = new Text(Color.WHITE, 11, Typeface.CALIBRI_BOLD);

	private Grid titleGrid, dataGrid;
	private Page page;
	float location = 1f;
	
	private static final float leftIndent  = 2f/72f;
	private static final float leftMargin  = 3f/72f;
	private static final float rightMargin = 3f/72f;
	private static final float mostLeft = 1f/72f;
	ContactInformationSeller(float location) {
		this.location = location;
	}
	private static final float ONE_LINE_ROW_HEIGHT = 13f/72f;
	private static final float TWO_LINE_ROW_HEIGHT = 23f/72f;
	
	// Row numbers
	private final int rowName = 1; 
	private final int rowAddress = 2; 
	private final int rowStateLicense = 3; 
	private final int rowContactName = 4; 
	private final int rowContactStateLicense = 5; 
	private final int rowEmail = 6; 
	private final int rowPhone = 7; 
	
	int    headerRow[]   = {0, 8, 16};
	String headerLabel[] = {"Real Estate Broker (B)","Real Estate Broker (S)", "Settlement Agent"};
	
	private void initializeTitleGrid() {
		float heights[] = { 12f/72f };
		float widths[] = { 3.5f };
		titleGrid = new Grid(1, heights, 1, widths);
		titleGrid.getCell(0, 0)
			.setBackground(new Tab())
			.setMargin(Direction.LEFT, leftIndent)
			.setForeground(new FormattedText("Contact Information", TAB));
	}
	
	private void initializeDataGrid(InputData data) throws IOException {
		float heights[] = { ONE_LINE_ROW_HEIGHT, TWO_LINE_ROW_HEIGHT, TWO_LINE_ROW_HEIGHT, ONE_LINE_ROW_HEIGHT, ONE_LINE_ROW_HEIGHT, ONE_LINE_ROW_HEIGHT, TWO_LINE_ROW_HEIGHT, ONE_LINE_ROW_HEIGHT,  
							ONE_LINE_ROW_HEIGHT, TWO_LINE_ROW_HEIGHT, TWO_LINE_ROW_HEIGHT, ONE_LINE_ROW_HEIGHT, ONE_LINE_ROW_HEIGHT, ONE_LINE_ROW_HEIGHT, TWO_LINE_ROW_HEIGHT,ONE_LINE_ROW_HEIGHT,  
							ONE_LINE_ROW_HEIGHT, TWO_LINE_ROW_HEIGHT, TWO_LINE_ROW_HEIGHT, ONE_LINE_ROW_HEIGHT, ONE_LINE_ROW_HEIGHT, ONE_LINE_ROW_HEIGHT, TWO_LINE_ROW_HEIGHT,ONE_LINE_ROW_HEIGHT, ONE_LINE_ROW_HEIGHT};
		
		
	/*	float heights [] = new float[25];
		for(int i =0; i<heights.length; i++){
			
				heights[i]=rowHeight;
		}
		
	//	setDynamicHeight(data, heights);
		
		for(int i=0; i<heights.length;i++)
			System.out.print(heights[i]+" ");*/	
		
		
		float widths[] = { 1.75f , 1.75f};
		
		
		dataGrid = new Grid(heights.length, heights, 2, widths);
		dataGrid.setLineVerticalAlignment(0, Dimension.COLUMN, VerticalAlignment.TOP);
		dataGrid.setLineVerticalAlignment(1, Dimension.COLUMN, VerticalAlignment.TOP);
		for (int i = 0; i < headerRow.length;i++){
			dataGrid.setLineShade(headerRow[i], Dimension.ROW, Color.MEDIUM_GRAY);
			SetRowLabels(headerRow[i], headerLabel[i]);
		}
		//lay in data
		for (PartyData partyData : data.getPartyList()) {
			switch (partyData.getRoleType())
			{
			case PartyRoleTypes.SettlementAgent:
				SetPartyText(headerRow[2], partyData);
				break;
		
			case PartyRoleTypes.RealEstateAgent:
				if (partyData.getRealEstateAgentType().equalsIgnoreCase("Selling")) {
					SetPartyText(headerRow[0], partyData);
				} else if (partyData.getRealEstateAgentType().equalsIgnoreCase("Listing")) {
					SetPartyText(headerRow[1], partyData);
				}
				break;
			}
		}
	}
	
	
//	private void setDynamicHeights(InputData data, float[] heights) {
//		int maxLength = 0;
//	      String longestNameString = null;
//	      String longestAddressString = null;
//		for (PartyData partyData : data.getPartyList()) {
//		          if (partyData.getPartyName().length() > maxLength) {
//		              maxLength = partyData.getPartyName().length();
//		              longestNameString = partyData.getPartyName();
//		              
//		          
//		      }
//		          if (partyData.getFullAddress().length() > maxLength) {
//		              maxLength = partyData.getFullAddress().length();
//		              longestAddressString = partyData.getFullAddress();
//		          
//		      }
//		}
//		
//		System.out.println("Name: "+longestNameString);
//		System.out.println("Address: "+longestAddressString);
//		
//		System.out.println("Name Hieght: "+longestNameString.length());
//		System.out.println("Address hieght: "+longestAddressString.length());
//		
//		/*float heights[] = { rowHeight,	2*rowHeight, 2*rowHeight, rowHeight, rowHeight, rowHeight, rowHeight, rowHeight,  
//		rowHeight, 2*rowHeight, 2*rowHeight, rowHeight, rowHeight, rowHeight, rowHeight, rowHeight,  
//		rowHeight, 2*rowHeight, 2*rowHeight, rowHeight, rowHeight, rowHeight, rowHeight, rowHeight, rowHeight};*/
//			if (longestNameString.length() > 34 && longestNameString.length() < 67
//					|| longestAddressString.length() > 34 && longestAddressString.length() < 67) {
//				//heightColDefault =17f/72f;
//				for (int i = 0; i < heights.length; i++) {
//					if (longestNameString.length() >34){
//						if(i==1)
//							heights[i] = 2 * rowHeight;
//						if(i==9)
//							heights[i] = 2 * rowHeight;
//						if(i==17)
//							heights[i] = 2 * rowHeight;
//					}
//					if(longestAddressString.length() > 34){
//						if(i==2)
//							heights[i] = 2 *rowHeight;
//						if(i==10)
//							heights[i] = 2 * rowHeight;
//						if(i==18)
//							heights[i] = 2 * rowHeight;
//					}
//					else{
//						heights[i] =  rowHeight;
//					}				
//					
//				}
//
//			}
//			
//			if (longestNameString.length() >= 68 && longestNameString.length() < 102
//					|| longestAddressString.length() >= 68  && longestAddressString.length() < 102) {
//				//heightColDefault =17f/72f;
//				for (int i = 0; i < heights.length; i++) {
//					if (longestNameString.length() >=68){
//						if(i==1)
//							heights[i] = 3 *rowHeight;
//						if(i==9)
//							heights[i] = 3 * rowHeight;
//						if(i==17)
//							heights[i] = 3 * rowHeight;
//					}
//					if(longestAddressString.length() >= 68){
//						if(i==2)
//							heights[i] = 3 *rowHeight;
//						if(i==10)
//							heights[i] = 3 * rowHeight;
//						if(i==18)
//							heights[i] = 3 * rowHeight;
//					}
//					else{
//						heights[i] =  rowHeight;
//					}				
//					
//				}
//
//			}
//		
//		
//}
	
	
	public void SetRowLabels(int start,String title) {
		dataGrid.setLineBorder(start, Position.BOTTOM,  Color.DARK_GRAY);
		dataGrid.setCellText(start++, 0, 				new FormattedText(title,Text.TABLE_TEXT_BOLD));
		dataGrid.setLineBorder(start, Position.BOTTOM,  Color.DARK_GRAY);
		dataGrid.setCellBorder(start, 0, Position.RIGHT,Color.BLACK);
		dataGrid.setCellText(start++, 0, 				new FormattedText("Name", Text.TABLE_HEADER));
		dataGrid.setLineBorder(start, Position.BOTTOM,  Color.DARK_GRAY);
		dataGrid.setCellBorder(start, 0, Position.RIGHT,Color.BLACK);
		dataGrid.setCellText(start++, 0, 				new FormattedText("Address", Text.TABLE_HEADER));
		dataGrid.setLineBorder(start, Position.BOTTOM,  Color.DARK_GRAY);
		dataGrid.setCellBorder(start, 0, Position.RIGHT,Color.BLACK);
		dataGrid.setCellText(start++, 0, 				new FormattedText("License ID", Text.TABLE_HEADER));
		dataGrid.setLineBorder(start, Position.BOTTOM,  Color.DARK_GRAY);
		dataGrid.setCellBorder(start, 0, Position.RIGHT,Color.BLACK);
		dataGrid.setCellText(start++, 0, 				new FormattedText("Contact", Text.TABLE_HEADER));
		dataGrid.setLineBorder(start, Position.BOTTOM,  Color.DARK_GRAY);
		dataGrid.setCellBorder(start, 0, Position.RIGHT,Color.BLACK);
		dataGrid.setCellText(start++, 0, 				new FormattedText("Contact _License ID", Text.TABLE_HEADER));
		dataGrid.setLineBorder(start, Position.BOTTOM,  Color.DARK_GRAY);
		dataGrid.setCellBorder(start, 0, Position.RIGHT,Color.BLACK);
		dataGrid.setCellText(start++, 0, 				new FormattedText("Email", Text.TABLE_HEADER));
		dataGrid.setLineBorder(start, Position.BOTTOM,  Color.DARK_GRAY);
		dataGrid.setCellBorder(start, 0, Position.RIGHT,Color.BLACK);
		dataGrid.setCellText(start++, 0, 				new FormattedText("Phone", Text.TABLE_HEADER));
	}
	
	public void SetPartyText(int start, PartyData partyData) throws IOException {
		
		String statusCode =  partyData.getStateCode().equals("") ? "":partyData.getStateCode()+" ";
		
		
		if (partyData.getIsLegalEntity() == true) {
			
			dataGrid.getCell(start+rowAddress, 1).setWrap(true).setMargin(Direction.LEFT, leftMargin).setMargin(Direction.RIGHT, rightMargin)
				.setForeground(new FormattedText(partyData.getFullAddress(), Text.TABLE_TEXT));
			dataGrid.getCell(start+rowName, 1).setWrap(true).setMargin(Direction.LEFT, leftMargin).setMargin(Direction.RIGHT, rightMargin)
				.setForeground(new FormattedText(partyData.getPartyName(), Text.TABLE_TEXT));
			dataGrid.getCell(start+rowStateLicense, 1).setMargin(Direction.LEFT, leftMargin).setMargin(Direction.RIGHT, rightMargin)
				.setForeground(new FormattedText(partyData.getLicenseIdentifier(), Text.TABLE_TEXT));
			
			dataGrid.getCell(start+rowStateLicense, 0).setMargin(Direction.LEFT,  mostLeft).setMargin(Direction.RIGHT, rightMargin)
				.setForeground(new FormattedText(statusCode + "License ID", Text.TABLE_TEXT_BOLD));
		} else {
			dataGrid.getCell(start+rowContactName, 1).setWrap(true).setMargin(Direction.LEFT, leftMargin).setMargin(Direction.RIGHT, rightMargin)
				.setForeground(new FormattedText(partyData.getPartyName(), Text.TABLE_TEXT));
			dataGrid.getCell(start+rowPhone, 1).setWrap(true).setMargin(Direction.LEFT, leftMargin).setMargin(Direction.RIGHT, rightMargin)
				.setForeground(new FormattedText(StringFormatter.PHONENUMBER.formatString(partyData.getPhoneNumber()), Text.TABLE_TEXT));	
			dataGrid.getCell(start+rowContactStateLicense, 1).setWrap(true).setMargin(Direction.LEFT, leftMargin).setMargin(Direction.RIGHT, rightMargin)
				.setForeground(new FormattedText(partyData.getLicenseIdentifier(), Text.TABLE_TEXT));
			dataGrid.getCell(start+rowContactStateLicense, 0).setWrap(true).setMargin(Direction.LEFT,  mostLeft).setMargin(Direction.RIGHT, rightMargin)
				.setForeground(new FormattedText(statusCode + "License ID", Text.TABLE_TEXT_BOLD));
			String emailString = FormatWrapEmail(1, partyData.getEmailAddress());
			dataGrid.getCell(start+rowEmail, 1).setWrap(true).setMargin(Direction.LEFT, leftMargin).setMargin(Direction.RIGHT, rightMargin)
				.setForeground(new FormattedText(emailString, Text.TABLE_TEXT));	
		}
	}

	public String FormatWrapEmail(int columnNumber, String emailString) throws IOException {
		float columnWidth = dataGrid.getSize(page, Dimension.COLUMN, columnNumber);	
		
		// insert spaces between common email address delimiters for wrapping guidance
		String[] emailParts = emailString.split("((?<=@)|(?=@)|(?<=\\.)|(?=\\.)|(?<=_)|(?=_))");
		String finalString = "", currentSection = "";
		for (String emailPart : emailParts) {
			FormattedText emailTextSection = new FormattedText(currentSection + emailPart, Text.TABLE_TEXT);
			float emailWidth = emailTextSection.width(page);
			if (emailWidth < columnWidth) {
				currentSection = currentSection + emailPart;
			} else {
				finalString = finalString + currentSection + " ";
				currentSection = emailPart;
			}
		}
		finalString = finalString + currentSection;
		
		return finalString;							
	}
	
	public void draw(Page page, Object d) throws IOException {
		this.page = page;
		InputData data = (InputData)d;
		initializeDataGrid(data);
		initializeTitleGrid();
		titleGrid.draw(page, 4.5f, this.location + dataGrid.height(page));
		dataGrid.draw(page,  4.5f, this.location);
	}
}