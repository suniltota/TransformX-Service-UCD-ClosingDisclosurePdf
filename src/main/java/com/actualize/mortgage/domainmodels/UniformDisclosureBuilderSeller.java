package com.actualize.mortgage.domainmodels;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;

import com.actualize.mortgage.datalayer.InputData;
import com.actualize.mortgage.pdfbuilder.Page;

public class UniformDisclosureBuilderSeller {
	private final float aggregateCostsTableHeight = 9.50f;
	private final float averageCostsTableHeight = aggregateCostsTableHeight/2f;
	
	private List<Page> pages = new LinkedList<Page>();

	public void run(InputData data, OutputStream out) throws ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException, IOException, COSVisitorException {
		// Create document
		PDDocument doc = new PDDocument();

		// Add page 1
		addPage(doc).addSection(new ClosingInformationSection())
    		.addSection(new SummariesOfTransactionsSection(0.7f, data))
    		.addSection(new ContactInformationSeller(1.95f))
    		.addSection(new QuestionsSection(4.5f, 0.7f))
			.addSection(new Footer("CLOSING DISCLOSURE", "1", 2));

		// Add page 2
		Page page2 = addPage(doc);
		data.setPages2A2B(false);
		LoanCosts loanCosts = new LoanCosts(data);
		OtherCosts otherCosts = new OtherCosts(page2, data);
		float loanCostsHeight = loanCosts.getHeight(page2, data);
		float otherCostsHeight = otherCosts.getHeight(page2, data);
		if (loanCostsHeight + otherCostsHeight > aggregateCostsTableHeight) {
			//rebuild grids on 2A 2B to larger sizes
			data.setPages2A2B(true);
			loanCosts = new LoanCosts(data);
			otherCosts = new OtherCosts(page2, data);
			loanCostsHeight = loanCosts.getHeight(page2, data);
			otherCostsHeight = otherCosts.getHeight(page2, data);
			
			loanCosts.stretch(page2, data, aggregateCostsTableHeight);
			page2.addSection(loanCosts).addSection(new Footer("CLOSING DISCLOSURE", "2A", 2));
			otherCosts.setWithHeader(true);
			otherCosts.stretch(page2, data, aggregateCostsTableHeight);
    		addPage(doc).addSection(otherCosts).addSection(new Footer("CLOSING DISCLOSURE", "2B", 2));
		} else {
			if (loanCostsHeight > averageCostsTableHeight) {
				otherCosts.stretch(page2, data, aggregateCostsTableHeight - loanCostsHeight);
			}
			if (otherCosts.getHeight(page2, data) > averageCostsTableHeight - 0.5f)
				loanCosts.stretch(page2, data, aggregateCostsTableHeight - otherCosts.getHeight(page2, data));
			else {
				loanCosts.stretch(page2, data, averageCostsTableHeight);
				otherCosts.stretch(page2, data, averageCostsTableHeight);
			}
			otherCosts.setPosition(page2.height - page2.topMargin - loanCosts.getHeight(page2, data) - 0.15f);
			page2.addSection(loanCosts).addSection(otherCosts).addSection(new Footer("CLOSING DISCLOSURE", "2", 2));
		}
//		Page page2 = addPage(doc);
//		if (LayoutPageTwo.sections(page2, data)) { // Pre-count and steal lines as necessary for page 2 sections;
//			LayoutPageTwo.expandFees(data);
//			LayoutPageTwo.sections(page2, data);
//			page2.addSection(new WriteLoanCosts()).addSection(new Footer("CLOSING DISCLOSURE", "2A",4));
//    		addPage(doc).addSection(new WriteOtherCosts()).addSection(new Footer("CLOSING DISCLOSURE", "2B", 4));
//		} else {
//			page2.addSection(new WriteLoanCosts())
//        		 .addSection(new WriteOtherCosts());
//			page2.addSection(new Footer("CLOSING DISCLOSURE", "2", 2));
//		}
//		//page2.addSection(new Footer("CLOSING DISCLOSURE", "2", 2));
		
		
		// Add addendum (if needed)
		if (TransactionInformationAddendumSection.IsSectionRequired(data) == true)
    		addPage(doc).addSection(new TransactionInformationAddendumSection())
			            .addSection(new Footer("ADDENDUM", "1", 1));

		// Add footers
    	for (Page page : pages){
    		//System.out.println("Watermark seller");
    		page.render(doc, data);
    	}
    	
		// Save document
		doc.save(out);
	}
	
	public ByteArrayOutputStream run(InputData data) throws ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException, IOException, COSVisitorException {
        // Create document
        PDDocument doc = new PDDocument();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // Add page 1
        addPage(doc).addSection(new ClosingInformationSection())
            .addSection(new SummariesOfTransactionsSection(0.7f, data))
            .addSection(new ContactInformationSeller(1.95f))
            .addSection(new QuestionsSection(4.5f, 0.7f))
            .addSection(new Footer("CLOSING DISCLOSURE", "1", 2));

        // Add page 2
        Page page2 = addPage(doc);
        data.setPages2A2B(false);
        LoanCosts loanCosts = new LoanCosts(data);
        OtherCosts otherCosts = new OtherCosts(page2, data);
        float loanCostsHeight = loanCosts.getHeight(page2, data);
        float otherCostsHeight = otherCosts.getHeight(page2, data);
        if (loanCostsHeight + otherCostsHeight > aggregateCostsTableHeight) {
            //rebuild grids on 2A 2B to larger sizes
            data.setPages2A2B(true);
            loanCosts = new LoanCosts(data);
            otherCosts = new OtherCosts(page2, data);
            loanCostsHeight = loanCosts.getHeight(page2, data);
            otherCostsHeight = otherCosts.getHeight(page2, data);
            
            loanCosts.stretch(page2, data, aggregateCostsTableHeight);
            page2.addSection(loanCosts).addSection(new Footer("CLOSING DISCLOSURE", "2A", 2));
            otherCosts.setWithHeader(true);
            otherCosts.stretch(page2, data, aggregateCostsTableHeight);
            addPage(doc).addSection(otherCosts).addSection(new Footer("CLOSING DISCLOSURE", "2B", 2));
        } else {
            if (loanCostsHeight > averageCostsTableHeight) {
                otherCosts.stretch(page2, data, aggregateCostsTableHeight - loanCostsHeight);
            }
            if (otherCosts.getHeight(page2, data) > averageCostsTableHeight - 0.5f)
                loanCosts.stretch(page2, data, aggregateCostsTableHeight - otherCosts.getHeight(page2, data));
            else {
                loanCosts.stretch(page2, data, averageCostsTableHeight);
                otherCosts.stretch(page2, data, averageCostsTableHeight);
            }
            otherCosts.setPosition(page2.height - page2.topMargin - loanCosts.getHeight(page2, data) - 0.15f);
            page2.addSection(loanCosts).addSection(otherCosts).addSection(new Footer("CLOSING DISCLOSURE", "2", 2));
        }
//      Page page2 = addPage(doc);
//      if (LayoutPageTwo.sections(page2, data)) { // Pre-count and steal lines as necessary for page 2 sections;
//          LayoutPageTwo.expandFees(data);
//          LayoutPageTwo.sections(page2, data);
//          page2.addSection(new WriteLoanCosts()).addSection(new Footer("CLOSING DISCLOSURE", "2A",4));
//          addPage(doc).addSection(new WriteOtherCosts()).addSection(new Footer("CLOSING DISCLOSURE", "2B", 4));
//      } else {
//          page2.addSection(new WriteLoanCosts())
//               .addSection(new WriteOtherCosts());
//          page2.addSection(new Footer("CLOSING DISCLOSURE", "2", 2));
//      }
//      //page2.addSection(new Footer("CLOSING DISCLOSURE", "2", 2));
        
        
        // Add addendum (if needed)
        if (TransactionInformationAddendumSection.IsSectionRequired(data) == true)
            addPage(doc).addSection(new TransactionInformationAddendumSection())
                        .addSection(new Footer("ADDENDUM", "1", 1));

        // Add footers
        for (Page page : pages){
            //System.out.println("Watermark seller");
            page.render(doc, data);
        }
        
        // Save document
        doc.save(out);
        doc.close();
        return out;
    }
	
	private Page addPage(PDDocument doc) {
		Page page = new Page(doc);
    	pages.add(page);
    	return page;
	}
}