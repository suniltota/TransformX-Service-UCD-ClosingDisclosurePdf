package com.actualize.mortgage.domainmodels;

import java.io.IOException;

import com.actualize.mortgage.datalayer.InputData;
import com.actualize.mortgage.pdfbuilder.Bullet;
import com.actualize.mortgage.pdfbuilder.FormattedText;
import com.actualize.mortgage.pdfbuilder.Page;
import com.actualize.mortgage.pdfbuilder.Paragraph;
import com.actualize.mortgage.pdfbuilder.Section;
import com.actualize.mortgage.pdfbuilder.Text;
import com.actualize.mortgage.pdfbuilder.TextBox;
import com.actualize.mortgage.pdfbuilder.TextBox.HorizontalAlignment;

public class Footer implements Section {
	private int totalPages;
	private String pageNumber, section;

	private final TextBox left = new TextBox();
	private final TextBox center = new TextBox().setHorizontalAlignment(HorizontalAlignment.CENTER);
	private final TextBox right = new TextBox().setHorizontalAlignment(HorizontalAlignment.RIGHT);

	public Footer(String section, String pageNumber, int totalPages) {
		this.section = section;
		this.pageNumber = pageNumber;
		this.totalPages = totalPages;
	}

	public void draw(Page page, Object d) throws IOException {
		InputData data = (InputData)d;
		this.left.setText(new FormattedText("CLOSING DISCLOSURE", Text.PAGE_FOOTER));
		
		//data.getClosingMap().printClosingMap();
		
		//if (data.getClosingMap().getClosingMapValue("ABOUT_VERSION.AboutVersionIdentifier").equalsIgnoreCase("DDOFileNumber"))
		if (data.isDocsDirect())
			this.center.setText(new FormattedText("***", Text.PAGE_FOOTER));
		String str = "";
		if (!section.equals("CLOSING DISCLOSURE"))
			str = str + section + " ";
		str = str + "PAGE " + pageNumber + " OF " + totalPages;
		Paragraph pgnum = new Paragraph()
			.append(new FormattedText(str, Text.PAGE_FOOTER))
			.append(Bullet.BULLET)
			.append(new FormattedText(" LOAN ID # " + data.getClosingMap().getClosingMapValue("LOAN_IDENTIFIER.LenderLoan"), Text.PAGE_FOOTER));
		this.right.setText(pgnum);
		left.draw(page, page.rightMargin, page.bottomMargin);
		right.draw(page, page.width-page.rightMargin, page.bottomMargin);
		center.draw(page, page.width/2, page.bottomMargin);
	}
}
