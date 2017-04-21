package com.actualize.closingdisclosure.domainmodels;

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

import com.actualize.closingdisclosure.datalayer.InputData;
import com.actualize.closingdisclosure.pdfbuilder.Page;

public class UniformDisclosureBuilder {
	private final float aggregateCostsTableHeight = 10.2f;
	private final float averageCostsTableHeight = aggregateCostsTableHeight/2f;
	private final float clearance = 0.45f;
	
	private List<Page> pages = new LinkedList<Page>();

	public void run(InputData data, OutputStream out) throws ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException, IOException, COSVisitorException {
		PDDocument doc = null;
		try {
			// Create document
			doc = new PDDocument();

			// Add page 1
			addPage(doc).addSection(new ClosingInformationSection())
	    		.addSection(new LoanTermsSection())
	    		.addSection(new ProjectedPaymentsSection())
	    		.addSection(new CostsAtClosingSection())
				.addSection(new Footer("CLOSING DISCLOSURE", "1", 5));

			// Add page 2
			Page page2 = addPage(doc);
			LoanCosts loanCosts = new LoanCosts(data);
			OtherCosts otherCosts = new OtherCosts(page2, data);
			float loanCostsHeight = loanCosts.getHeight(page2, data);
			float otherCostsHeight = otherCosts.getHeight(page2, data);
			if (loanCostsHeight + otherCostsHeight + 1.0f > aggregateCostsTableHeight) {
				//rebuild grids on 2A 2B to larger sizes
				data.setPages2A2B(true);
				loanCosts = new LoanCosts(data);
				otherCosts = new OtherCosts(page2, data);
				loanCostsHeight = loanCosts.getHeight(page2, data);
				otherCostsHeight = otherCosts.getHeight(page2, data);
				
				loanCosts.stretch(page2, data, aggregateCostsTableHeight);
				page2.addSection(loanCosts).addSection(new Footer("CLOSING DISCLOSURE", "2A", 5));
				otherCosts.setWithHeader(true);
				otherCosts.stretch(page2, data, aggregateCostsTableHeight);
	    		addPage(doc).addSection(otherCosts).addSection(new Footer("CLOSING DISCLOSURE", "2B", 5));
			} else {
				if (loanCostsHeight > averageCostsTableHeight) {
					otherCosts.stretch(page2, data, aggregateCostsTableHeight - loanCostsHeight-clearance);
					loanCosts.stretch(page2, data, aggregateCostsTableHeight - otherCosts.getHeight(page2, data)-clearance);
				} else {
					loanCosts.stretch(page2, data, aggregateCostsTableHeight - otherCostsHeight-clearance);
					otherCosts.stretch(page2, data, aggregateCostsTableHeight - loanCosts.getHeight(page2, data) + 0.1f);
				}
				otherCosts.setPosition(page2.height - page2.topMargin - loanCosts.getHeight(page2, data) - 0.15f );
				page2.addSection(loanCosts).addSection(otherCosts).addSection(new Footer("CLOSING DISCLOSURE", "2", 5));
			}

			// Add page 3
			if (LayoutPageThree.alternateView(data))
				addPage(doc).addSection(new WritePayoffsPayments(1.6f))
							.addSection(new CashToCloseSection(0.5f))
			                .addSection(new Footer("CLOSING DISCLOSURE", "3", 5));
			else
				addPage(doc).addSection(new CashToCloseSection())
	        		        .addSection(new SummariesOfTransactionsSection(.7f, data))
	                        .addSection(new Footer("CLOSING DISCLOSURE", "3", 5));
						
			// Add page 4
			addPage(doc).addSection(new LoanDisclosuresSection())
	    		        .addSection(new AdjustableRateSection())
				        .addSection(new AdjustablePaymentSection())
						.addSection(new Footer("CLOSING DISCLOSURE", "4", 5));
			
			// Add page 5
			addPage(doc).addSection(new LoanCalculationsSection())
	    		        .addSection(new OtherDisclosuresSection())
	    		        .addSection(new QuestionsSection(0.5f, 6f))
	    		        .addSection(new ContactInformationSection(2.1f))
	    		        .addSection(new ReceiptConfirmationSection(.8f))
	    				.addSection(new Footer("CLOSING DISCLOSURE", "5", 5));

			// Add addendum (if needed)
			int addendumPages = (TransactionInformationAddendumSection.IsSectionRequired(data) ? 1 : 0)
					+ (PropertyAddendumSection.IsSectionRequired(data) ? 1 : 0)
					+ (WritePayoffsPayments.IsAddendumRequired(data) ? 1 : 0);
			if (addendumPages > 0) {
				int addendumPage = 0;
				if (TransactionInformationAddendumSection.IsSectionRequired(data)){
					addPage(doc).addSection(new TransactionInformationAddendumSection())
							//.addSection(new ReceiptConfirmationSection(5f))
				            .addSection(new Footer("ADDENDUM", String.valueOf(++addendumPage), addendumPages));
				}
				if (PropertyAddendumSection.IsSectionRequired(data))
					addPage(doc).addSection(new PropertyAddendumSection())
				            .addSection(new Footer("ADDENDUM", String.valueOf(++addendumPage), addendumPages));
				if (WritePayoffsPayments.IsAddendumRequired(data))
					addPage(doc).addSection(new WritePayoffsPayments())
				            .addSection(new Footer("ADDENDUM", String.valueOf(++addendumPage), addendumPages));
			}

			// Render
	    	for (Page page : pages){
	    		
	    		page.render(doc, data);
	    	}
	    	
			// Save document
			doc.save(out);
			
		} catch (Exception e) {
			// Pass it on...
			throw e;
			
		} finally {
			if (doc != null)
				doc.close();
		}
	}
	
	public ByteArrayOutputStream run(InputData data) throws ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException, IOException, COSVisitorException {
        PDDocument doc = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            // Create document
            doc = new PDDocument();

            // Add page 1
            addPage(doc).addSection(new ClosingInformationSection())
                .addSection(new LoanTermsSection())
                .addSection(new ProjectedPaymentsSection())
                .addSection(new CostsAtClosingSection())
                .addSection(new Footer("CLOSING DISCLOSURE", "1", 5));

            // Add page 2
            Page page2 = addPage(doc);
            LoanCosts loanCosts = new LoanCosts(data);
            OtherCosts otherCosts = new OtherCosts(page2, data);
            float loanCostsHeight = loanCosts.getHeight(page2, data);
            float otherCostsHeight = otherCosts.getHeight(page2, data);
            if (loanCostsHeight + otherCostsHeight + 1.0f > aggregateCostsTableHeight) {
                //rebuild grids on 2A 2B to larger sizes
                data.setPages2A2B(true);
                loanCosts = new LoanCosts(data);
                otherCosts = new OtherCosts(page2, data);
                loanCostsHeight = loanCosts.getHeight(page2, data);
                otherCostsHeight = otherCosts.getHeight(page2, data);
                
                loanCosts.stretch(page2, data, aggregateCostsTableHeight);
                page2.addSection(loanCosts).addSection(new Footer("CLOSING DISCLOSURE", "2A", 5));
                otherCosts.setWithHeader(true);
                otherCosts.stretch(page2, data, aggregateCostsTableHeight);
                addPage(doc).addSection(otherCosts).addSection(new Footer("CLOSING DISCLOSURE", "2B", 5));
            } else {
                if (loanCostsHeight > averageCostsTableHeight) {
                    otherCosts.stretch(page2, data, aggregateCostsTableHeight - loanCostsHeight-clearance);
                    loanCosts.stretch(page2, data, aggregateCostsTableHeight - otherCosts.getHeight(page2, data)-clearance);
                } else {
                    loanCosts.stretch(page2, data, aggregateCostsTableHeight - otherCostsHeight-clearance);
                    otherCosts.stretch(page2, data, aggregateCostsTableHeight - loanCosts.getHeight(page2, data) + 0.1f);
                }
                otherCosts.setPosition(page2.height - page2.topMargin - loanCosts.getHeight(page2, data) - 0.15f );
                page2.addSection(loanCosts).addSection(otherCosts).addSection(new Footer("CLOSING DISCLOSURE", "2", 5));
            }

            // Add page 3
            if (LayoutPageThree.alternateView(data))
                addPage(doc).addSection(new WritePayoffsPayments(1.6f))
                            .addSection(new CashToCloseSection(0.5f))
                            .addSection(new Footer("CLOSING DISCLOSURE", "3", 5));
            else
                addPage(doc).addSection(new CashToCloseSection())
                            .addSection(new SummariesOfTransactionsSection(.7f, data))
                            .addSection(new Footer("CLOSING DISCLOSURE", "3", 5));
                        
            // Add page 4
            addPage(doc).addSection(new LoanDisclosuresSection())
                        .addSection(new AdjustableRateSection())
                        .addSection(new AdjustablePaymentSection())
                        .addSection(new Footer("CLOSING DISCLOSURE", "4", 5));
            
            // Add page 5
            addPage(doc).addSection(new LoanCalculationsSection())
                        .addSection(new OtherDisclosuresSection())
                        .addSection(new QuestionsSection(0.5f, 6f))
                        .addSection(new ContactInformationSection(2.1f))
                        .addSection(new ReceiptConfirmationSection(.8f))
                        .addSection(new Footer("CLOSING DISCLOSURE", "5", 5));

            // Add addendum (if needed)
            int addendumPages = (TransactionInformationAddendumSection.IsSectionRequired(data) ? 1 : 0)
                    + (PropertyAddendumSection.IsSectionRequired(data) ? 1 : 0)
                    + (WritePayoffsPayments.IsAddendumRequired(data) ? 1 : 0);
            if (addendumPages > 0) {
                int addendumPage = 0;
                if (TransactionInformationAddendumSection.IsSectionRequired(data)){
                    addPage(doc).addSection(new TransactionInformationAddendumSection())
                            //.addSection(new ReceiptConfirmationSection(5f))
                            .addSection(new Footer("ADDENDUM", String.valueOf(++addendumPage), addendumPages));
                }
                if (PropertyAddendumSection.IsSectionRequired(data))
                    addPage(doc).addSection(new PropertyAddendumSection())
                            .addSection(new Footer("ADDENDUM", String.valueOf(++addendumPage), addendumPages));
                if (WritePayoffsPayments.IsAddendumRequired(data))
                    addPage(doc).addSection(new WritePayoffsPayments())
                            .addSection(new Footer("ADDENDUM", String.valueOf(++addendumPage), addendumPages));
            }

            // Render
            for (Page page : pages){
                
                page.render(doc, data);
            }
            
            // Save document
            doc.save(out);
            
        } catch (Exception e) {
            // Pass it on...
            throw e;
            
        } finally {
            if (doc != null)
                doc.close();
        }
        return out;
    }
	
	private Page addPage(PDDocument doc) {
		Page page = new UCDPage(doc);
    	pages.add(page);
    	return page;
	}

}
