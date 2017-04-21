package com.actualize.closingdisclosure.domainmodels;

import java.io.IOException;

import com.actualize.closingdisclosure.datalayer.ClosingMap;
import com.actualize.closingdisclosure.datalayer.InputData;
import com.actualize.closingdisclosure.datalayer.PartyData;
import com.actualize.closingdisclosure.datalayer.PartyRoleTypes;
import com.actualize.closingdisclosure.datalayer.SubjectProperty;
import com.actualize.closingdisclosure.pdfbuilder.Color;
import com.actualize.closingdisclosure.pdfbuilder.FormattedText;
import com.actualize.closingdisclosure.pdfbuilder.Grid;
import com.actualize.closingdisclosure.pdfbuilder.Page;
import com.actualize.closingdisclosure.pdfbuilder.Section;
import com.actualize.closingdisclosure.pdfbuilder.Text;
import com.actualize.closingdisclosure.pdfbuilder.Typeface;
import com.actualize.closingdisclosure.pdfbuilder.Grid.Dimension;
import com.actualize.closingdisclosure.pdfbuilder.Grid.Position;
import com.actualize.closingdisclosure.pdfbuilder.TextBox.Direction;
import com.actualize.closingdisclosure.pdfbuilder.TextBox.HorizontalAlignment;
import com.actualize.closingdisclosure.pdfbuilder.TextBox.VerticalAlignment;

public class ContactInformationSection implements Section {
	public static final Text TAB               = new Text(Color.WHITE, 11, Typeface.CALIBRI_BOLD);

	private Grid  titleGrid, dataGrid;
	private float location;
	private Page  page;
	
	private String str;
	private String  patternAnd = "[a|A][n|N][d|D][a|A][m|M][p|P];";
	
	//private float heightColDefault = 13f/72f;

	// Column numbers
	private final int colLabel = 0; 
	private final int colLender = 1; 
	private final int colMortgageBroker = 2; 
	
	// Row numbers
	private final int rowHeader = 0; 
	private final int rowName = 1; 
	private final int rowAddress = 2; 
	private final int rowNMLS_ID = 3; 
	private final int rowStateLicense = 4; 
	private final int rowContactName = 5; 
	private final int rowContactNMLS_ID = 6; 
	private final int rowContactStateLicense = 7; 
	private final int rowEmail = 8; 
	private final int rowPhone = 9; 

	private final float tableWidth = 7.5f;
	private final float leftColumnWidth = 1.1f;
	private final float leftIndent  = 2f/72f;
	private final float leftMargin  = 3f/72f;
	private final float rightMargin = 3f/72f;

	ContactInformationSection(float location) {
		this.location = location;
	}
	
	private void initializeTitleGrid() {
		float heights[] = { 12f/72f };
		float widths[] = { 7.5f };
		titleGrid = new Grid(1, heights, 1, widths);
		titleGrid.getCell(0, 0)
			.setBackground(new Tab())
			.setMargin(Direction.LEFT, leftIndent)
			.setForeground(new FormattedText("Contact Information", TAB));
	}
	
	private void initializeDataGrid(InputData data) {
		int columncount = 4;
		
		if (PartyRelationshipHelper.IsRealEstateAgentTypeInvolved("Selling", data.getPartyList()))
			columncount++;
		if (PartyRelationshipHelper.IsRealEstateAgentTypeInvolved("Listing", data.getPartyList()))
			columncount++;
        
		/*float columnwidth = 5.75f/(columncount-1);*/
		
		float columnwidth = (tableWidth - leftColumnWidth)/(columncount-1);
		
		//float heights[] = { heightColDefault, 3*heightColDefault, 3*heightColDefault, heightColDefault, heightColDefault, 
		//		heightColDefault, heightColDefault, 2*heightColDefault, 3*heightColDefault, heightColDefault };
		float heights[] = { Grid.DYNAMIC,  Grid.DYNAMIC, Grid.DYNAMIC,  Grid.DYNAMIC, Grid.DYNAMIC, Grid.DYNAMIC, Grid.DYNAMIC, 
				Grid.DYNAMIC, Grid.DYNAMIC, Grid.DYNAMIC};			
		float widths[] = { leftColumnWidth , columnwidth};
		
		dataGrid = new Grid(heights.length, heights, columncount, widths);
		
		for (int column = 0; column < dataGrid.columns(); column++) {
			dataGrid.setLineVerticalAlignment(colLabel, Dimension.COLUMN, VerticalAlignment.TOP);
			//dataGrid.setLineHorizontalAlignment(colLabel, Dimension.COLUMN , HorizontalAlignment.LEFT);
		}
	}

	private void formatDataGrid() throws IOException {
		dataGrid.setLineShade(0, Dimension.ROW, Color.MEDIUM_GRAY);
		dataGrid.setLineHorizontalAlignment(rowHeader, Dimension.ROW, HorizontalAlignment.LEFT);
		dataGrid.setLineMargin(0, Dimension.COLUMN, Direction.LEFT, 4f/72f);
		for (int column = 0; column < dataGrid.columns(); column++) {
			dataGrid.setLineVerticalAlignment(column, Dimension.COLUMN, VerticalAlignment.TOP);
			
			if (column != 0) {
				dataGrid.setLineBorder(column, Position.LEFT, Color.BLACK);
			}
			
			for (int row = 1; row < dataGrid.rows() - 1; row++) {
				dataGrid.setCellBorder(row, column, Position.TOP, Color.MEDIUM_GRAY);
				dataGrid.setCellBorder(row, column, Position.BOTTOM, Color.MEDIUM_GRAY);
			}
			
			dataGrid.setLineBorder(rowPhone, Position.BOTTOM, Color.BLACK);
			dataGrid.setLineBorder(rowHeader, Position.TOP, Color.BLACK);
		}
	}
	
	private void insertText(InputData data) throws IOException {
		SetRowLabels(dataGrid, data);
		Boolean stateLicense = false;
		dataGrid.setCellText(rowHeader, colMortgageBroker, new FormattedText(" Mortgage Broker", Text.TABLE_HEADER));
		
		for (PartyData partyData : data.getPartyList()) {
			switch (partyData.getRoleType())
			{
			case PartyRoleTypes.SettlementAgent:
				stateLicense = true;
				dataGrid.setCellText(rowHeader, getClosingAgentColumn(data), new FormattedText(" Settlement Agent", Text.TABLE_HEADER));
				SetColumnText(getClosingAgentColumn(data), partyData, data);
				break;
		
			case PartyRoleTypes.Lender:
				dataGrid.setCellText(rowHeader, colLender, new FormattedText(" Lender", Text.TABLE_HEADER));
				SetColumnText(colLender, partyData, data);
				break;
		
			case PartyRoleTypes.RealEstateAgent:
				stateLicense = true;
				if (partyData.getRealEstateAgentType().equalsIgnoreCase("Selling")) {
					dataGrid.setCellText(rowHeader, getBuyingRealEstateAgentColumn(data), new FormattedText(" Real Estate Broker (B)", Text.TABLE_HEADER));
					SetColumnText(getBuyingRealEstateAgentColumn(data), partyData, data);
				} else if (partyData.getRealEstateAgentType().equalsIgnoreCase("Listing")) {
					dataGrid.setCellText(rowHeader, getSellingRealEstateAgentColumn(data), new FormattedText(" Real Estate Broker (S)", Text.TABLE_HEADER));
					SetColumnText(getSellingRealEstateAgentColumn(data), partyData, data);
				}
				break;
				
			case PartyRoleTypes.MortgageBroker:
				SetColumnText(colMortgageBroker, partyData, data);
				break;
			}
		}
		// use the subject property state in the row label if there is any RealEstateAgent or SettlementAgent
		if (stateLicense){
			SubjectProperty subjectProperty = data.getSubjectProperty();
			if (!subjectProperty.getStateCode().isEmpty() && !subjectProperty.getStateCode().equals("")){
				dataGrid.setCellText(rowStateLicense,        colLabel, new FormattedText(subjectProperty.getStateCode()+"\u00A0"+"License ID", Text.TABLE_HEADER));
				dataGrid.setCellText(rowContactStateLicense, colLabel, new FormattedText("Contact                     "+
				subjectProperty.getStateCode()+"\u00A0"+"License ID", Text.TABLE_HEADER));
			}
		}
	}
	
	public void SetRowLabels(Grid dataGrid, InputData data) {
		
		dataGrid.getCell(rowContactStateLicense, colLabel).setWrap(true);
		dataGrid.setCellText(rowStateLicense,        colLabel, new FormattedText("   License ID", Text.TABLE_HEADER));
		dataGrid.setCellText(rowContactStateLicense, colLabel, new FormattedText("Contact                   License ID", Text.TABLE_HEADER));
		
		dataGrid.setCellText(rowStateLicense,        colLabel, new FormattedText("   License ID", Text.TABLE_HEADER));
		dataGrid.setCellText(rowName,                colLabel, new FormattedText("Name", Text.TABLE_HEADER));
		dataGrid.setCellText(rowAddress,             colLabel, new FormattedText("Address", Text.TABLE_HEADER));
		dataGrid.setCellText(rowNMLS_ID,             colLabel, new FormattedText("NMLS ID", Text.TABLE_HEADER));
		dataGrid.setCellText(rowContactName,         colLabel, new FormattedText("Contact", Text.TABLE_HEADER));
		dataGrid.setCellText(rowContactNMLS_ID,      colLabel, new FormattedText("Contact NMLS ID", Text.TABLE_HEADER));
		dataGrid.setCellText(rowEmail,               colLabel, new FormattedText("Email", Text.TABLE_HEADER));
		dataGrid.setCellText(rowPhone,               colLabel, new FormattedText("Phone", Text.TABLE_HEADER));
	}
	
	public int getClosingAgentColumn(InputData data) {
		int defaultColumn = 3;
		if (PartyRelationshipHelper.IsRealEstateAgentTypeInvolved("Selling", data.getPartyList()))
			defaultColumn++;
		if (PartyRelationshipHelper.IsRealEstateAgentTypeInvolved("Listing", data.getPartyList()))
			defaultColumn++;
		
		return defaultColumn;
	}

	public int getSellingRealEstateAgentColumn(InputData data) {
		int defaultColumn = 0;
		if (PartyRelationshipHelper.IsRealEstateAgentTypeInvolved("Listing", data.getPartyList()))
			defaultColumn = 3;
		if (PartyRelationshipHelper.IsRealEstateAgentTypeInvolved("Selling", data.getPartyList()))
			defaultColumn++;
		
		return defaultColumn;
	}
	
	public int getBuyingRealEstateAgentColumn(InputData data) {
		int defaultColumn = 0;
		if (PartyRelationshipHelper.IsRealEstateAgentTypeInvolved("Selling", data.getPartyList()))
			defaultColumn = 3;
		
		return defaultColumn;
	}
	
	public void SetColumnText(int columnNumber, PartyData partyData, InputData data) throws IOException {
		SetPartyText(columnNumber, partyData);
		//PartyData relatedPartyData = PartyRelationshipHelper
		//		.getRelatedParty(partyData, data.getPartyList(), data.getRelationshipList());
		//if (relatedPartyData != null)
		//	SetPartyText(columnNumber, relatedPartyData);		
	}
	
	public void SetPartyText(int columnNumber, PartyData partyData) throws IOException {
		if (partyData.getIsLegalEntity() == true) {
			dataGrid.getCell(rowAddress, columnNumber).setWrap(true).setMargin(Direction.LEFT, leftMargin).setMargin(Direction.RIGHT, rightMargin)
				.setForeground(new FormattedText(partyData.getFullAddress(), Text.TABLE_TEXT));
			str = partyData.getPartyName().replaceAll(patternAnd, "&");
			str = str.replaceAll("''''", "'");
			dataGrid.getCell(rowName, columnNumber).setWrap(true).setMargin(Direction.LEFT, leftMargin).setMargin(Direction.RIGHT, rightMargin)
				.setForeground(new FormattedText(str, Text.TABLE_TEXT));
			SetLicenseIdentifier(columnNumber, partyData, rowNMLS_ID, rowStateLicense);
		} else {
			str = partyData.getPartyName().replaceAll(patternAnd, "&");
			str = str.replaceAll("�", "'");
			dataGrid.getCell(rowContactName, columnNumber).setWrap(true).setMargin(Direction.LEFT, leftMargin).setMargin(Direction.RIGHT, rightMargin)
				.setForeground(new FormattedText(str, Text.TABLE_TEXT));
			dataGrid.getCell(rowPhone, columnNumber).setWrap(true).setMargin(Direction.LEFT, leftMargin).setMargin(Direction.RIGHT, rightMargin)
				.setForeground(new FormattedText(StringFormatter.PHONENUMBER.formatString(partyData.getPhoneNumber()), Text.TABLE_TEXT));	
			SetLicenseIdentifier(columnNumber, partyData, rowContactNMLS_ID, rowContactStateLicense);
			String emailString = formatWrapEmail(columnNumber, partyData.getEmailAddress());
			//String emailString = partyData.getEmailAddress();
			dataGrid.getCell(rowEmail, columnNumber).setWrap(true).setMargin(Direction.LEFT, leftMargin).setMargin(Direction.RIGHT, rightMargin)
				.setForeground(new FormattedText(emailString, Text.TABLE_TEXT));	
		}
	}

	public void SetLicenseIdentifier(int columnNumber, PartyData partyData, int rowNMLS, int rowState)
	{
	    boolean stateLicense = false;
	    switch (partyData.getRoleType()) {
            case PartyRoleTypes.SettlementAgent:
                stateLicense = true;
                break;
            case PartyRoleTypes.Lender:
                stateLicense = false;
                break;
            case PartyRoleTypes.RealEstateAgent:
                stateLicense = true;
                break;
            case PartyRoleTypes.MortgageBroker:
                stateLicense = false;
                break;
        }
		if (!stateLicense) {
			dataGrid.getCell(rowNMLS, columnNumber).setWrap(true).setMargin(Direction.LEFT, leftMargin).setMargin(Direction.RIGHT, rightMargin)
				.setForeground(new FormattedText(partyData.getLicenseIdentifier(), Text.TABLE_TEXT));
		} else {
			String license = partyData.getLicenseIdentifier();
			if (!partyData.getLicenseIssuingStateCode().equals(""))
				license = partyData.getLicenseIssuingStateCode() + " " + license;
			dataGrid.getCell(rowState, columnNumber).setWrap(true).setMargin(Direction.LEFT, leftMargin).setMargin(Direction.RIGHT, rightMargin)
				.setForeground(new FormattedText(license, Text.TABLE_TEXT));
		}
	}
	
	public String formatWrapEmail(int columnNumber, String emailString) throws IOException {

		// Check if there's a problem
		float columnWidth = dataGrid.getSize(page, Dimension.COLUMN, columnNumber) - leftMargin - rightMargin;	
		FormattedText text = new FormattedText(emailString, Text.TABLE_TEXT);
		float textWidth = text.width(page);
		if (textWidth <= columnWidth)
			return emailString;
		
		// There's a problem. See if section before '@' fit's on one line.
		if (emailString.indexOf('@') != -1) {
			text = new FormattedText(emailString.substring(0, emailString.indexOf('@')+1), Text.TABLE_TEXT);
			textWidth = text.width(page);
			if (textWidth <= columnWidth){
				return emailString.substring(0, emailString.indexOf('@')+1)+ ""
					+ formatWrapEmailRemainder(columnWidth, emailString.substring(emailString.indexOf('@')+1));
			}
		}
		
		// Name is also too long. Split everything.
		return formatWrapEmailRemainder(columnWidth, emailString);
	}
	
	// insert spaces between common email address delimiters for wrapping guidance
	public String formatWrapEmailRemainder(float columnWidth, String emailString) throws IOException {

		// break email into parts
		String[] emailParts = emailString.split("((?<=@)|(?=@)|(?<=\\.)|(?=\\.)|(?<=_)|(?=_))");

		// Check first part
		FormattedText emailTextSection = new FormattedText(emailParts[0], Text.TABLE_TEXT);
		float emailWidth = emailTextSection.width(page);
		if (emailWidth > columnWidth) {
			int splitIndex = forceSplitIndex(columnWidth, emailParts[0]);
			return emailString.substring(0, splitIndex) + " " + formatWrapEmailRemainder(columnWidth, emailString.substring(splitIndex));
		}
		
		String finalString = "";
		String currentSection = "";
		for (String emailPart : emailParts) {
			emailTextSection = new FormattedText(currentSection + emailPart, Text.TABLE_TEXT);
			emailWidth = emailTextSection.width(page);
			if (emailWidth <= columnWidth) {
				currentSection = currentSection + emailPart;
			} else {
				finalString = finalString + currentSection + " ";
				currentSection = emailPart;
			}
		}
		finalString = finalString + currentSection;
		
		return finalString;							
	}
	
	public int forceSplitIndex(float columnWidth, String emailString) throws IOException {		
		for (int i = 0; i < emailString.length(); i++) {
			FormattedText emailTextSection = new FormattedText(emailString.substring(0, i), Text.TABLE_TEXT);
			float emailWidth = emailTextSection.width(page);
			if (emailWidth > columnWidth)
				return i;
		}
		return emailString.length();
	}

	public void draw(Page page, Object d) throws IOException {
		this.page = page;
		InputData data = (InputData)d;
		ClosingMap closingMap = data.getClosingMap();		

		initializeDataGrid(data);
		initializeTitleGrid();
		
		insertText(data);
		formatDataGrid();
		float xloc = closingMap.getClosingMapValue("DOCUMENT_CLASSIFICATION_DETAIL.DocumentSignatureRequiredIndicator").equals("false") ? location - 0.5f : location;
		titleGrid.draw(page, 0.5f, xloc + dataGrid.height(page));
		dataGrid.draw(page, 0.5f, xloc);
	}

	public float height(Page page) throws IOException {
		return titleGrid.height(page) + dataGrid.height(page);
	}
}