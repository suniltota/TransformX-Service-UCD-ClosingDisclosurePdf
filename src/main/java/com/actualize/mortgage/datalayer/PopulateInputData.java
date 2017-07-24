package com.actualize.mortgage.datalayer;

/* Copyright (C) 2015 Actualize Consulting, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Ed Berger <eberger@actualizeconsulting.com>, September 2015
 */
/**
 *
 * @author EdBerger
 * Created for the UCD version based on MISMO 3.3
 */
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
//import java.util.UUID;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.actualize.mortgage.domainmodels.StringFormatter;

public class PopulateInputData {
	private static String NS = "";
	private static List<String> nameSpaces = null;
	private static Document closingXmlIn = null;
	
	private static Pattern ptrnNumbers = Pattern.compile(" [2-9]+");
	private static Matcher matches = null;
	private static String strPrepend = "zzz";
	private static String strLabel = "";

	private static int ToInteger(String str) {
		int value = 0;
		try {
			value = Integer.parseInt(str);
		} catch (Exception e) {
		}
		return value;
	}

	private static List<String> findNamespaces(Node root) {
		List<String> namespaces = new ArrayList<String>();
		NamedNodeMap attributes = root.getAttributes();
		if (attributes != null)
			for (int i = 0; i < attributes.getLength(); i++) {
				Node node = attributes.item(i);
				if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
					String ns = node.getNamespaceURI();
					if (ns != null)
						namespaces.add(node.getLocalName() + ":");
				}
			}
		namespaces.add("");
		return namespaces;
	}
	//this replaces the string (space)(number) with (space)"zzz"(number to make fees with 2's etc. sort nicer
	private static String fixSorts(String strIn){
	        // get a matcher object
	        matches = ptrnNumbers.matcher(strIn);
	        while (matches.find()){
	        	strIn = strIn.substring(0,matches.start())+" "+strPrepend+strIn.substring(matches.start()+1);
	        }
	        //System.out.println(">>>"+strIn);
		return strIn;
	}

	private static Comparator<Escrows> EscrowComparator = new Comparator<Escrows>() {
		public int compare(Escrows escrow1, Escrows escrow2) {
			if (escrow1.getType().equals("HomeownersInsurancePremium")
					&& !escrow2.getType().equals("HomeownersInsurancePremium"))
				return -1;
			if (!escrow1.getType().equals("HomeownersInsurancePremium")
					&& escrow2.getType().equals("HomeownersInsurancePremium"))
				return 1;
			if (escrow1.getType().equals("MortgageInsurancePremium")
					&& !escrow2.getType().equals("MortgageInsurancePremium"))
				return -1;
			if (!escrow1.getType().equals("MortgageInsurancePremium")
					&& escrow2.getType().equals("MortgageInsurancePremium"))
				return 1;
			if (escrow1.getType().equals("PrepaidInterest")
					&& !escrow2.getType().equals("PrepaidInterest"))
				return -1;
			if (!escrow1.getType().equals("PrepaidInterest")
					&& escrow2.getType().equals("PrepaidInterest"))
				return 1;
			if (escrow1.getType().equals("PropertyTax")
					&& escrow2.getType().equals("PropertyTax"))
				return -1;
			if (escrow1.getType().equals("PropertyTax")
					&& escrow2.getType().equals("PropertyTax"))
				return 1;
			return escrow1.getLabel().compareToIgnoreCase(escrow2.getLabel());
		}
	};

	private static Comparator<Fees> FeeComparator = new Comparator<Fees>() {
		public int compare(Fees fee1, Fees fee2) {

			// Loan discount points always sorted first
			if (fee1.getType().equals("LoanDiscountPoints")
					&& !fee2.getType().equals("LoanDiscountPoints"))
				return -1;
			if (!fee1.getType().equals("LoanDiscountPoints")
					&& fee2.getType().equals("LoanDiscountPoints"))
				return 1;

			// Treat special characters the same as a space when comparing labels
			String label1 = fee1.getLabel().replaceAll("/", " ");
			String label2 = fee2.getLabel().replaceAll("/", " ");
			int cmp = label1.compareToIgnoreCase(label2);
			if (cmp != 0)
				return cmp;
			
			// Labels are equal, sort by paid to entity
			return fee1.getPaymentToEntity().compareToIgnoreCase(fee2.getPaymentToEntity());			
		}
	};

	private static Comparator<Prepaids> PrepaidComparator = new Comparator<Prepaids>() {
		public int compare(Prepaids prepaid1, Prepaids prepaid2) {
			if (prepaid1.getType().equals("HomeownersInsurancePremium")
					&& !prepaid2.getType().equals("HomeownersInsurancePremium"))
				return -1;
			if (!prepaid1.getType().equals("HomeownersInsurancePremium")
					&& prepaid2.getType().equals("HomeownersInsurancePremium"))
				return 1;
			if (prepaid1.getType().equals("MortgageInsurancePremium")
					&& !prepaid2.getType().equals("MortgageInsurancePremium"))
				return -1;
			if (!prepaid1.getType().equals("MortgageInsurancePremium")
					&& prepaid2.getType().equals("MortgageInsurancePremium"))
				return 1;
			if (prepaid1.getType().equals("PrepaidInterest")
					&& !prepaid2.getType().equals("PrepaidInterest"))
				return -1;
			if (!prepaid1.getType().equals("PrepaidInterest")
					&& prepaid2.getType().equals("PrepaidInterest"))
				return 1;
			if (prepaid1.getType().equals("PropertyTax")
					&& prepaid2.getType().equals("PropertyTax"))
				return -1;
			if (prepaid1.getType().equals("PropertyTax")
					&& prepaid2.getType().equals("PropertyTax"))
				return 1;
			return prepaid1.getLabel().compareToIgnoreCase(prepaid2.getLabel());
		}
	};

	public List<InputData> getData(InputStream stream) {
		// convert input stream to an XML document
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			InputSource is = new InputSource(stream);
			closingXmlIn = db.parse(is);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return getData(closingXmlIn);
	}

	public List<InputData> getData(Document closingXmlIn) {
		List<InputData> inputDealList = new ArrayList<InputData>();

		// retrieve root
		Element message = closingXmlIn.getDocumentElement();
		// System.err.println("Root element " + message.getNodeName());
		NS = message.lookupPrefix("http://www.mismo.org/residential/2009/schemas") + ":";
		// System.err.println("prefix:"+NS);
		nameSpaces = findNamespaces(message);
		
		// Get DOCUMENT level informations. As per UCD 1.3 Spec, for split disclosure there are two DOCUMENT trees in DOCUMENT_SET
		// Check if DOCUMENT_SET is present. If so, new style MISMO UCD, otherwise old style.
		NodeList documentList = message.getElementsByTagName(NS + "DOCUMENT_SET");
		if (documentList.getLength() == 0)
			documentList = message.getElementsByTagName(NS + "DEAL_SET");
		else
			documentList = message.getElementsByTagName(NS + "DOCUMENT");
		
		for (int i = 0; i < documentList.getLength(); i++) {
		    Element document = (Element) documentList.item(i);
		    NodeList dealList = document.getElementsByTagName(NS + "DEAL");
		    NodeList documentClassification = document.getElementsByTagName(NS + "DOCUMENT_CLASSIFICATION");
	    
		    for (int j = 0; j < dealList.getLength(); j++) {
                Element deal = (Element) dealList.item(j);
                InputData inputDeal = populateDeal(deal, documentClassification.getLength() == 0 ? null
                                : (Element) documentClassification.item(0));
                
                helperGetContainer(message, inputDeal.getClosingMap(), "ABOUT_VERSION", "AboutVersionIdentifier","CreatedDatetime","DataVersionIdentifier");
                if (inputDeal.getClosingMap().getClosingMapValue("ABOUT_VERSION.AboutVersionIdentifier").equalsIgnoreCase("DDOFileNumber")){
                    inputDeal.setDocsDirect(true);
                }
                
                // if there are exactly two DEAL trees in a DEAL_SET
                // then we assume the first is the borrower view of a split
                // and the second is the seller view of a split
                if (dealList.getLength() == 2) {
                    if (j == 0) {
                        inputDeal.setBorrowerOnly(true);
                    } else if (j == 1) {
                        inputDeal.setSellerOnly(true);
                    }
                } else if (dealList.getLength() == 1 && documentList.getLength() > 1) {
                    NodeList documentClassNodeList = document.getElementsByTagName(NS + "DOCUMENT_CLASS");
                    if (documentClassNodeList.getLength() > 0) {
                        Element documentClassElement = (Element) documentClassNodeList.item(0);
                        String documentType = helperGetElementValues(documentClassElement, "DocumentTypeOtherDescription");
                        if (documentType.equalsIgnoreCase("ClosingDisclosure:BorrowerOnly"))
                            inputDeal.setBorrowerOnly(true);
                        else if(documentType.equalsIgnoreCase("ClosingDisclosure:SellerOnly")) 
                            inputDeal.setSellerOnly(true);
                        else if(j == 0 && i == 0)
                            inputDeal.setBorrowerOnly(true);
                        else if (j == 0 && i == 1)
                            inputDeal.setSellerOnly(true);
                    }
                }   else if (dealList.getLength() == 1) {
                    NodeList documentClassNodeList = document.getElementsByTagName(NS + "DOCUMENT_CLASS");
                    if (documentClassNodeList.getLength() > 0) {
                        Element documentClassElement = (Element) documentClassNodeList.item(0);
                        String documentType = helperGetElementValues(documentClassElement,
                                "DocumentTypeOtherDescription");
                        if (documentType.equalsIgnoreCase("ClosingDisclosure:BorrowerOnly"))
                            inputDeal.setBorrowerOnly(true);
                    }
                }
                inputDealList.add(inputDeal);
		    }
		}
		return inputDealList;
	}

	private InputData populateDeal(Element thisRoot,
			Element documentClassification) throws DOMException,
			NumberFormatException {
		InputData inputData = new InputData();
		ClosingMap closingMap = new ClosingMap();
		inputData.setClosingMap(closingMap);

		populateClosingMap(closingMap, documentClassification, thisRoot);
		// print Map Values
		// closingMap.printClosingMap();

		populateParties(inputData, thisRoot);

		populateDataObjects(inputData, thisRoot);

		// optional counts
		return inputData;
	}

	private void populateDataObjects(InputData inputData, Element thisRoot)
			throws DOMException, NumberFormatException {
		// get pi adjustment rules
		List<PIadjustments> piAdjustmentsList = new ArrayList<PIadjustments>();
		NodeList piAdjustList = thisRoot.getElementsByTagName(NS
				+ "PRINCIPAL_AND_INTEREST_PAYMENT_PER_CHANGE_ADJUSTMENT_RULE");
		NodeList piLifetimeAdjustList = thisRoot.getElementsByTagName(NS
				+ "PRINCIPAL_AND_INTEREST_PAYMENT_LIFETIME_ADJUSTMENT_RULE");
		String firstChangeMonthsCount = null;
		for(int i=0; i<piLifetimeAdjustList.getLength(); i++) {
			Element adjustElement = (Element) piLifetimeAdjustList.item(i);
			if(null!=adjustElement) {
				firstChangeMonthsCount = helperGetElementValues(adjustElement,
						"FirstPrincipalAndInterestPaymentChangeMonthsCount");
				break;
			}
		}
		for (int fl = 0; fl < piAdjustList.getLength(); fl++) {
			Element adjustElement = (Element) piAdjustList.item(fl);
			if (adjustElement != null) {
				PIadjustments adjustRuleLocal = new PIadjustments();
				adjustRuleLocal.setAdjustmentRuleType(helperGetElementValues(
						adjustElement, "AdjustmentRuleType"));
				adjustRuleLocal
						.setFirstChangeMonthsCount(firstChangeMonthsCount);
				adjustRuleLocal
						.setPerChangeAdjustmentFrequencyMonthsCount(helperGetElementValues(
								adjustElement,
								"PerChangePrincipalAndInterestPaymentAdjustmentFrequencyMonthsCount"));
				adjustRuleLocal
						.setPerChangeMaximumPaymentAmount(helperGetElementValues(
								adjustElement,
								"PerChangeMaximumPrincipalAndInterestPaymentAmount"));
				adjustRuleLocal
						.setPerChangeMinimumPaymentAmount(helperGetElementValues(
								adjustElement,
								"PerChangeMinimumPrincipalAndInterestPaymentAmount"));
				piAdjustmentsList.add(adjustRuleLocal);
				// System.err.println("Adjustment Rule: "+
				// adjustRuleLocal.getAdjustmentRuleType());
			}
		}
		// add local list to input data
		inputData.setPiAdjustmentsList(piAdjustmentsList);

		// get interest rate rules
		// System.err.println("Liabilities----------------------------------------------------");
		List<InterestRule> interestRulesList = new ArrayList<InterestRule>();
		NodeList interestRuleList = thisRoot.getElementsByTagName(NS
				+ "INTEREST_RATE_PER_CHANGE_ADJUSTMENT_RULE");
		for (int fl = 0; fl < interestRuleList.getLength(); fl++) {
			Element interestRule = (Element) interestRuleList.item(fl);
			if (interestRule != null) {
				InterestRule interestRuleLocal = new InterestRule();
				interestRuleLocal.setAdjustmentRuleType(helperGetElementValues(
						interestRule, "AdjustmentRuleType"));
				interestRuleLocal
						.setPerChangeMaximumIncreaseRatePercent(helperGetElementValues(
								interestRule,
								"PerChangeMaximumIncreaseRatePercent"));
				interestRuleLocal
						.setPerChangeRateAdjustmentFrequencyMonthsCount(helperGetElementValues(
								interestRule,
								"PerChangeRateAdjustmentFrequencyMonthsCount"));
				// System.err.println("Interest Rule: "+
				// interestRuleLocal.getAdjustmentRuleType());
				interestRulesList.add(interestRuleLocal);
			}
		}
		// add local list to input data
		inputData.setInterestRuleList(interestRulesList);

		// get liabilities
		// System.err.println("Liabilities----------------------------------------------------");
		List<Liabilities> liabilitiesList = new ArrayList<Liabilities>();
		NodeList liabilitiesNodeList = thisRoot.getElementsByTagName(NS
				+ "LIABILITY");
		for (int fl = 0; fl < liabilitiesNodeList.getLength(); fl++) {
			Element liability = (Element) liabilitiesNodeList.item(fl);
			if (liability != null) {
				Liabilities liabilityLocal = new Liabilities();
				liabilityLocal.setPayoffAmount(helperGetElementValues(
						liability, "PayoffAmount"));
				if (helperGetElementValues(liability,
						"PayoffPartialIndicator")
						.equalsIgnoreCase("true")) {
					liabilityLocal.setPayoffPartialIndicator(true);
				} else if(helperGetElementValues(liability,
						"PayoffPartialIndicator")
						.equalsIgnoreCase("false")) {
							liabilityLocal.setPayoffPartialIndicator(false);
						}
				liabilityLocal.setIDSection(helperGetElementValues(liability,
						"IntegratedDisclosureSectionType", "gse:"));
				liabilityLocal.setDescription(helperGetElementValues(liability,
						"LiabilityDescription"));
				liabilityLocal
						.setSecuredBySubjectProperty(helperGetElementValues(
								liability,
								"LiabilitySecuredBySubjectPropertyIndicator"));
				liabilityLocal.setFullName(helperGetElementValues(liability,
						"FullName"));
				// System.err.println("section:"+liabilityLocal.getIDSection());
				
				liabilityLocal.setType(helperGetElementValues(liability,"LiabilityType"));
				
				strLabel = helperGetElementAttribute(liability,"LiabilityType", "DisplayLabelText");
				if (strLabel.isEmpty() || strLabel.equals(""))
					strLabel = helperGetElementAttribute(liability,"LiabilityType", "gse:DisplayLabelText");
				
				if (strLabel.isEmpty() || strLabel.equals("")) {
					if ("Other".equalsIgnoreCase(liabilityLocal.getType())){
						liabilityLocal.setLabel(StringFormatter.CAMEL.formatString(helperGetElementValues(liability,"LiabilityTypeOtherDescription")));
					} else {
						liabilityLocal.setLabel(StringFormatter.CAMEL.formatString(liabilityLocal.getType()));	
					}
					//System.out.println("Liability from type:"+liabilityLocal.getLabel());
				} else {
						liabilityLocal.setLabel(StringFormatter.CAMEL.formatString(strLabel));
						//System.out.println("Liability from label:"+liabilityLocal.getLabel());
				}
				
				liabilitiesList.add(liabilityLocal);
			}
		}
		// add local list to input data
		inputData.setLiabilitiesList(liabilitiesList);

		// get property cost components
		List<PropertyCostComponents> costList = new ArrayList<PropertyCostComponents>();
		NodeList costComponentList = thisRoot.getElementsByTagName(NS
				+ "ESTIMATED_PROPERTY_COST_COMPONENT");
		NodeList estimatedPropertyCostComponents = thisRoot.getElementsByTagName(NS+ "ESTIMATED_PROPERTY_COST");
		for(int i =0 ;i<estimatedPropertyCostComponents.getLength();i++){
		    Element ele = (Element) estimatedPropertyCostComponents.item(i);
		    strLabel = helperGetElementAttribute(ele,"ESTIMATED_PROPERTY_COST_COMPONENTS", "gse:DisplayLabelText");
            for (int fl = 0; fl < costComponentList.getLength(); fl++) {
                Element cost = (Element) costComponentList.item(fl);
                if (cost != null) {
                    PropertyCostComponents costLocal = new PropertyCostComponents();
                    costLocal.setEscrowedType(helperGetElementValues(cost, "ProjectedPaymentEscrowedType"));
                    costLocal.setEstimatedTaxesInsuranceAssessmentComponentType(helperGetElementValues(cost, "ProjectedPaymentEstimatedTaxesInsuranceAssessmentComponentType"));
                    costLocal.setEstimatedTaxesInsuranceAssessmentComponentTypeOtherDescription(helperGetElementValues(cost, "ProjectedPaymentEstimatedTaxesInsuranceAssessmentComponentTypeOtherDescription"));

                    if (strLabel.isEmpty() || strLabel.equals("")) {
                    	//commenting below if and else condition to fix USB_UCD-108 - From Tim: Can't do this as it breaks other clients. Will need to discuss.
                        if ("Other".equalsIgnoreCase(costLocal.getEstimatedTaxesInsuranceAssessmentComponentType())) {
                            costLocal.setLabel(StringFormatter.CAMEL.formatString(helperGetElementValues(cost, "ProjectedPaymentEstimatedTaxesInsuranceAssessmentComponentTypeOtherDescription")));
                        } else {
                            costLocal.setLabel(StringFormatter.CAMEL.formatString(helperGetElementValues(cost, "ProjectedPaymentEstimatedTaxesInsuranceAssessmentComponentType")));
                        }
                        // System.out.println("Liability from
                        // type:"+liabilityLocal.getLabel());
                    } else {
                        costLocal.setLabel(StringFormatter.CAMEL.formatString(strLabel));
                        // System.out.println("Liability from
                        // label:"+liabilityLocal.getLabel());
                    }
                    costList.add(costLocal);
                    // System.err.println("Cost: "+
                    // costLocal.getEscrowedType());
                }
            }
		}
		// add local list to input data
		inputData.setPropertyCostList(costList);

		// get cash to close
		List<CashToClose> cashList = new ArrayList<CashToClose>();
		NodeList cashItemList = thisRoot.getElementsByTagName(NS
				+ "CASH_TO_CLOSE_ITEM");
		for (int fl = 0; fl < cashItemList.getLength(); fl++) {
			Element cashElement = (Element) cashItemList.item(fl);
			if (cashElement != null) {
				CashToClose cashLocal = new CashToClose();
				if (helperGetElementValues(cashElement,
						"IntegratedDisclosureCashToCloseItemAmountChangedIndicator")
						.equalsIgnoreCase("true")) {
					cashLocal.setAmountChangedIndicator(true);
				} else if (helperGetElementValues(cashElement,
						"IntegratedDisclosureCashToCloseItemAmountChangedIndicator")
						.equalsIgnoreCase("false")) {
					cashLocal.setAmountChangedIndicator(false);
				}
				cashLocal.setItemPaymentType(helperGetElementValues(
						cashElement,
						"IntegratedDisclosureCashToCloseItemPaymentType"));
				cashLocal
						.setItemChangeDescription(helperGetElementValues(
								cashElement,
								"IntegratedDisclosureCashToCloseItemChangeDescription"));
				cashLocal.setItemEstimatedAmount(helperGetElementValues(
						cashElement,
						"IntegratedDisclosureCashToCloseItemEstimatedAmount"));
				cashLocal.setItemFinalAmount(helperGetElementValues(
						cashElement,
						"IntegratedDisclosureCashToCloseItemFinalAmount"));
				cashLocal.setItemType(helperGetElementValues(cashElement,
						"IntegratedDisclosureCashToCloseItemType"));
				cashList.add(cashLocal);
				// System.err.println("Cash: "+ cashLocal.getItemType());
			}
		}
		// add local list to input data
		inputData.setCashList(cashList);

		// get projected payments
		List<ProjectedPayments> paymentsList = new ArrayList<ProjectedPayments>();
		NodeList paymentsItemList = thisRoot.getElementsByTagName(NS
				+ "PROJECTED_PAYMENT");
		for (int fl = 0; fl < paymentsItemList.getLength(); fl++) {
			Element paymentElement = (Element) paymentsItemList.item(fl);
			if (paymentElement != null) {
				ProjectedPayments paymentLocal = new ProjectedPayments();
				paymentLocal
						.setPaymentNumber(ToInteger(helperGetAttributeValues(
								paymentElement, "SequenceNumber")));
				paymentLocal.setFrequencyType(helperGetElementValues(
						paymentElement, "PaymentFrequencyType"));
				paymentLocal
						.setCalculationPeriodEndNumber(helperGetElementValues(
								paymentElement,
								"ProjectedPaymentCalculationPeriodEndNumber"));
				paymentLocal
						.setCalculationPeriodStartNumber(helperGetElementValues(
								paymentElement,
								"ProjectedPaymentCalculationPeriodStartNumber"));
				paymentLocal
						.setCalculationPeriodTermType(helperGetElementValues(
								paymentElement,
								"ProjectedPaymentCalculationPeriodTermType"));
				if (paymentLocal.getCalculationPeriodTermType()
						.equalsIgnoreCase("Other")) {
					paymentLocal
							.setCalculationPeriodTermType(helperGetElementValues(
									paymentElement,
									"ProjectedPaymentCalculationPeriodTermTypeOtherDescription"));
				}
				paymentLocal
						.setEstimatedEscrowPaymentAmount(helperGetElementValues(
								paymentElement,
								"ProjectedPaymentEstimatedEscrowPaymentAmount"));
				paymentLocal
						.setEstimatedTotalMaximumPaymentAmount(helperGetElementValues(
								paymentElement,
								"ProjectedPaymentEstimatedTotalMaximumPaymentAmount"));
				paymentLocal
						.setPrincipalAndInterestMaximumPaymentAmount(helperGetElementValues(
								paymentElement,
								"ProjectedPaymentPrincipalAndInterestMaximumPaymentAmount"));
				paymentLocal
						.setEstimatedTotalMinimumPaymentAmount(helperGetElementValues(
								paymentElement,
								"ProjectedPaymentEstimatedTotalMinimumPaymentAmount"));
				paymentLocal
						.setPrincipalAndInterestMinimumPaymentAmount(helperGetElementValues(
								paymentElement,
								"ProjectedPaymentPrincipalAndInterestMinimumPaymentAmount"));
				paymentLocal
						.setProjectedPaymentMIPaymentAmount(helperGetElementValues(
								paymentElement,
								"ProjectedPaymentMIPaymentAmount"));
				paymentLocal.setInterestOnlyIndicator(helperGetElementValues(
						paymentElement, "InterestOnlyIndicator")
						.equalsIgnoreCase("true"));
				// System.err.println("Payment no:"+paymentLocal.getPaymentNumber());
				paymentsList.add(paymentLocal);
			}
		}
		// add local list to input data
		inputData.setPaymentsList(paymentsList);

		// get pro rations
		List<Prorations> prorationsList = new ArrayList<Prorations>();
		NodeList prorationItemList = thisRoot.getElementsByTagName(NS
				+ "PRORATION_ITEM");
		for (int fl = 0; fl < prorationItemList.getLength(); fl++) {
			Element prorationElement = (Element) prorationItemList.item(fl);
			if (prorationElement != null) {
				Prorations prorationLocal = new Prorations();
				
				prorationLocal.setPaymentAmount(helperGetElementValues(
						prorationElement, "ProrationItemAmount"));
				prorationLocal
						.setIntegratedDisclosureSubsectionType(helperGetElementValues(
								prorationElement,
								"IntegratedDisclosureSubsectionType"));
				prorationLocal
						.setIntegratedDisclosureSectionType(helperGetElementValues(
								prorationElement,
								"IntegratedDisclosureSectionType"));
				prorationLocal
						.setProrationItemPaidFromDate(helperGetElementValues(
								prorationElement, "ProrationItemPaidFromDate"));
				prorationLocal
						.setProrationItemPaidThroughDate(helperGetElementValues(
								prorationElement,
								"ProrationItemPaidThroughDate"));
				prorationLocal.setType(helperGetElementValues(prorationElement, "ProrationItemType"));
				
				prorationLocal.setTypeOtherDescription(helperGetElementValues(prorationElement, "ProrationItemTypeOtherDescription"));
				
				
				strLabel = helperGetElementAttribute(prorationElement,"ProrationItemType", "DisplayLabelText");
				if (strLabel.isEmpty() || strLabel.equals(""))	{
					strLabel = helperGetElementAttribute(prorationElement,"ProrationItemType", "gse:DisplayLabelText");
				}
				if (strLabel.isEmpty() || strLabel.equals(""))	{
					if ("Other".equalsIgnoreCase(prorationLocal.getType())) {
						prorationLocal.setLabel(StringFormatter.CAMEL.formatString(prorationLocal.getTypeOtherDescription()));
					} else {
						prorationLocal.setLabel(StringFormatter.CAMEL.formatString(prorationLocal.getType()));
					}
				} else {
					prorationLocal.setLabel(StringFormatter.CAMEL.formatString(strLabel));
					//System.out.println("Pro ration from label: "+prorationLocal.getLabel());
				}
				prorationsList.add(prorationLocal);
			}
		}
		// add local list to input data
		inputData.setProrationsList(prorationsList);

		// get closing cost funds
		List<ClosingCostFunds> fundsList = new ArrayList<ClosingCostFunds>();
		NodeList fundItemList = thisRoot.getElementsByTagName(NS
				+ "CLOSING_COST_FUND");
		for (int fl = 0; fl < fundItemList.getLength(); fl++) {
			Element fundElement = (Element) fundItemList.item(fl);
			if (fundElement != null) {
				ClosingCostFunds fundsLocal = new ClosingCostFunds();
				fundsLocal.setTotalAmount(helperGetElementValues(fundElement,
						"ClosingCostFundAmount"));
				fundsLocal.setType(helperGetElementValues(fundElement,
						"FundsType"));
				fundsLocal
						.setIntegratedDisclosureSectionType(helperGetElementValues(
								fundElement, "IntegratedDisclosureSectionType"));
				fundsList.add(fundsLocal);
				// System.err.println("Funds: "+fundsLocal.getType());
			}
		}
		// add local list to input data
		inputData.setClosingCostFunds(fundsList);

		// get Subject Property
		SubjectProperty propertyLocal = new SubjectProperty();
		NodeList propertyList = thisRoot.getElementsByTagName(NS
				+ "SUBJECT_PROPERTY");
		Element propertyElement = (Element) propertyList.item(0);
		propertyLocal.setPersonalPropertyAmount(helperGetElementValues(
				propertyElement, "PersonalPropertyAmount"));
		propertyLocal
				.setPersonalPropertyIncludedIndicator(helperGetElementValues(
						propertyElement, "PersonalPropertyIncludedIndicator"));
		propertyLocal.setRealPropertyAmount(helperGetElementValues(
				propertyElement, "RealPropertyAmount"));
		// address data
		helperGetAddress(propertyLocal, propertyElement);
		// now store in input data
		inputData.setSubjectProperty(propertyLocal);

		// get Adjustments
		List<Adjustments> adjustmentList = new ArrayList<Adjustments>();
		NodeList adjustmentItemList = thisRoot.getElementsByTagName(NS
				+ "CLOSING_ADJUSTMENT_ITEM");
		for (int an = 0; an < adjustmentItemList.getLength(); an++) {
			Element adjustmentElement = (Element) adjustmentItemList.item(an);
			if (adjustmentElement != null) {
				Adjustments adjustmentLocal = new Adjustments();
				adjustmentLocal.setPaymentAmount(helperGetElementValues(
						adjustmentElement, "ClosingAdjustmentItemAmount"));
				if (helperGetElementValues(adjustmentElement,
						"ClosingAdjustmentItemPaidOutsideOfClosingIndicator")
						.equalsIgnoreCase("false")) {
					adjustmentLocal.setPaidOutsideOfClosingIndicator(false);
				} else if (helperGetElementValues(adjustmentElement,
						"ClosingAdjustmentItemPaidOutsideOfClosingIndicator")
						.equalsIgnoreCase("true")) {
					adjustmentLocal.setPaidOutsideOfClosingIndicator(true);
				}
				adjustmentLocal
						.setIntegratedDisclosureSectionType(helperGetElementValues(
								adjustmentElement,
								"IntegratedDisclosureSectionType"));
				adjustmentLocal
						.setIntegratedDisclosureSubsectionType(helperGetElementValues(
								adjustmentElement,
								"IntegratedDisclosureSubsectionType"));
				
				NodeList adjustmentPaidByList = adjustmentElement.getElementsByTagName("mismo:"
                        + "CLOSING_ADJUSTMENT_ITEM_PAID_BY");
                Element adjustmentPaidByEle = (Element) adjustmentPaidByList.item(0);
                if(null!=adjustmentPaidByEle) {
                    adjustmentLocal.setPaymentPaidByType(helperGetElementValues(
                        adjustmentPaidByEle, "FullName"));
                }
				
				NodeList adjustmentPaidToList = adjustmentElement.getElementsByTagName("gse:"
		                + "CLOSING_ADJUSTMENT_ITEM_PAID_TO");
			    Element adjustmentPaidToEle = (Element) adjustmentPaidToList.item(0);
			    if(null!=adjustmentPaidToEle) {
				    adjustmentLocal.setPaymentToEntity(helperGetElementValues(
				            adjustmentPaidToEle, "FullName"));
				}
				
				adjustmentLocal.setType(helperGetElementValues(adjustmentElement, "ClosingAdjustmentItemType"));
				adjustmentLocal.setTypeOtherDescription(helperGetElementValues(adjustmentElement, "ClosingAdjustmentItemTypeOtherDescription"));
				
				strLabel = helperGetElementAttribute(adjustmentElement,"ClosingAdjustmentItemType", "DisplayLabelText");
				if (strLabel.isEmpty() || strLabel.equals(""))	{
					strLabel = helperGetElementAttribute(adjustmentElement,"ClosingAdjustmentItemType", "gse:DisplayLabelText");
				}
				if (strLabel.isEmpty() || strLabel.equals(""))	{
					if ("Other".equalsIgnoreCase(adjustmentLocal.getType() )) {
						adjustmentLocal.setLabel(StringFormatter.CAMEL.formatString(adjustmentLocal.getTypeOtherDescription()));
					} else {
						adjustmentLocal.setLabel(StringFormatter.CAMEL.formatString(adjustmentLocal.getType()));
					}
				} else {
					adjustmentLocal.setLabel(StringFormatter.CAMEL.formatString(strLabel));
				}
				//System.out.println("Adjustment label:"+ adjustmentLocal.getLabel());
				adjustmentList.add(adjustmentLocal);
			}
		}
		// add local list to input data
		inputData.setAdjustmentList(adjustmentList);

		// get relationships
		List<RelationshipData> relationshipList = new ArrayList<RelationshipData>();

		NodeList relationshipNodeList = thisRoot.getElementsByTagName(NS
				+ "RELATIONSHIP");
		for (int s = 0; s < relationshipNodeList.getLength(); s++) {
			Element relationshipElement = (Element) relationshipNodeList
					.item(s);
			if (relationshipElement != null) {
				RelationshipData relationshipLocal = new RelationshipData();
				NamedNodeMap mapAttributes = relationshipElement
						.getAttributes();
				if (mapAttributes != null) {
					Node nodeFromAttribute = mapAttributes
							.getNamedItem("xlink:from");
					if (nodeFromAttribute != null) {
						relationshipLocal.setFrom(nodeFromAttribute
								.getTextContent());
					}
					Node nodeToAttribute = mapAttributes
							.getNamedItem("xlink:to");
					if (nodeToAttribute != null) {
						relationshipLocal.setTo(nodeToAttribute
								.getTextContent());
					}
					Node nodeRelationshipAttribute = mapAttributes
							.getNamedItem("xlink:arcrole");
					if (nodeRelationshipAttribute != null) {
						relationshipLocal
								.setRelationship(nodeRelationshipAttribute
										.getTextContent());
					}
				}

				// add relationship to local list
				relationshipList.add(relationshipLocal);
			}
		}

		// add local list to input data
		inputData.setRelationshipList(relationshipList);

		// Populate Fees
		// -------------------------------------------------------------------------------------------------------------

		List<Fees> feeList = new ArrayList<Fees>();
		NodeList feeNodeList = thisRoot.getElementsByTagName(NS + "FEE");
		for (int fl = 0; fl < feeNodeList.getLength(); fl++) {
			Element feeElement = (Element) feeNodeList.item(fl);
			if (feeElement != null) {
				Fees feeLocal = new Fees();
				
				feeLocal.setPaidToType(helperGetElementValues(feeElement,
						"FeePaidToType"));
				feeLocal.setPercentBasisType(helperGetElementValues(feeElement,
						"FeePercentBasisType"));
				feeLocal.setTotalPercent(helperGetElementValues(feeElement,
						"FeeTotalPercent"));
				feeLocal.setPaymentAmount(helperGetElementValues(feeElement,
						"FeeActualPaymentAmount"));
				feeLocal.setTotalAmount(helperGetElementValues(feeElement,
						"FeeActualTotalAmount"));
				feeLocal.setIntegratedDisclosureSectionType(helperGetElementValues(
						feeElement, "IntegratedDisclosureSectionType"));
				feeLocal.setPaymentPaidByType(helperGetElementValues(
						feeElement, "FeePaymentPaidByType"));
				feeLocal.setPaymentToEntity(helperGetElementValues(feeElement,
						"FullName"));
				if (helperGetElementValues(feeElement,
						"FeePaymentPaidOutsideOfClosingIndicator")
						.equalsIgnoreCase("true")) {
					feeLocal.setPaidOutsideOfClosingIndicator(true);
				} else if (helperGetElementValues(feeElement,
						"FeePaymentPaidOutsideOfClosingIndicator")
						.equalsIgnoreCase("false")) {
					feeLocal.setPaidOutsideOfClosingIndicator(false);
				}
				if (helperGetElementValues(feeElement, "OptionalCostIndicator")
						.equalsIgnoreCase("true")) {
					feeLocal.setOptionalCostIndicator(true);
				} else {
					feeLocal.setOptionalCostIndicator(false);
				}
				// label - type
				//feeLocal.setType(helperGetElementValues(feeElement, "FeeType"));
				String strType = helperGetElementValues( feeElement, "FeeType");
				if ("Other".equalsIgnoreCase(strType) && !helperGetElementValues( feeElement, "FeeTypeOtherDescription").isEmpty())
					strType = helperGetElementValues( feeElement, "FeeTypeOtherDescription");
				feeLocal.setType(strType);
				
				strLabel = helperGetElementAttribute(feeElement, "FeeType", "DisplayLabelText");
				if (strLabel.isEmpty() || strLabel.equals(""))
					strLabel = helperGetElementAttribute(feeElement, "FeeType", "gse:DisplayLabelText");
				if (strLabel.isEmpty() || strLabel.equals(""))
					strLabel = helperGetElementAttribute(feeElement, "FEE_DETAIL", "DisplayLabelText");
				if (strLabel.isEmpty() || strLabel.equals(""))
					strLabel = helperGetElementAttribute(feeElement, "FEE_DETAIL", "gse:DisplayLabelText");
				if (strLabel.isEmpty() || strLabel.equals("")){
					feeLocal.setLabel(fixSorts(StringFormatter.CAMEL.formatString(strType)));
					//System.out.println("Fee Label from type:"+feeLocal.getLabel());
				} else {
//					feeLocal.setLabel(fixSorts(StringFormatter.CAMEL.formatString(strLabel)));
					feeLocal.setLabel(fixSorts(strLabel));
					//System.out.println("Fee Label from label:"+feeLocal.getLabel());
				}	
				
				
				NodeList paymentNodeList = feeElement.getElementsByTagName(NS
						+ "FEE_PAYMENT");
				for (int pl = 0; pl < paymentNodeList.getLength(); pl++) {
					Element paymentElement = (Element) paymentNodeList.item(pl);
					if (paymentElement != null) {
						String payment = helperGetElementValues(paymentElement,
								"FeeActualPaymentAmount");
						if (helperGetElementValues(paymentElement,
								"FeePaymentPaidByType").equalsIgnoreCase(
								"Buyer")) {
							if (helperGetElementValues(paymentElement,
									"FeePaymentPaidOutsideOfClosingIndicator")
									.equalsIgnoreCase("true"))
								feeLocal.setBuyerOutsideClosingAmount(payment);
							else
								feeLocal.setBuyerAtClosingAmount(payment);
						} else if (helperGetElementValues(paymentElement,
								"FeePaymentPaidByType").equalsIgnoreCase(
								"Seller")) {
							if (helperGetElementValues(paymentElement,
									"FeePaymentPaidOutsideOfClosingIndicator")
									.equalsIgnoreCase("true"))
								feeLocal.setSellerOutsideClosingAmount(payment);
							else
								feeLocal.setSellerAtClosingAmount(payment);
						} else {
							feeLocal.setOtherEntity(helperGetElementValues(
									paymentElement, "FeePaymentPaidByType"));
							feeLocal.setOtherAmount(payment);
						}
					}
				}
				// System.err.println("this fee:"+feeLocal.getType()+" amt:"+feeLocal.getPaymentAmount());
				//feeLocal.setLabel(fixSorts(feeLocal.getLabel()));
				feeList.add(feeLocal);
			}
		}
		feeList.sort(FeeComparator);
		for (Fees freeLocal:feeList)
			freeLocal.setLabel(freeLocal.getLabel().replaceAll(strPrepend,""));
		inputData.setFeeList(feeList);
		// System.err.println("FEES-------------------------------------------------------");

		// Populate Prepaids
		// -------------------------------------------------------------------------------------------------------------
		List<Prepaids> prepaidList = new ArrayList<Prepaids>();
		NodeList prepaidNodeList = thisRoot.getElementsByTagName(NS
				+ "PREPAID_ITEM");
		for (int fl = 0; fl < prepaidNodeList.getLength(); fl++) {
			Element prepaidElement = (Element) prepaidNodeList.item(fl);
			if (prepaidElement != null) {
				Prepaids prepaidLocal = new Prepaids();
				
				prepaidLocal.setPaidToType(helperGetElementValues(
						prepaidElement, "FeePaidToType"));
				prepaidLocal
						.setIntegratedDisclosureSectionType(helperGetElementValues(
								prepaidElement,
								"IntegratedDisclosureSectionType"));
				prepaidLocal
						.setPrepaidItemPerDiemAmount(helperGetElementValues(
								prepaidElement, "PrepaidItemPerDiemAmount"));
				prepaidLocal.setPrepaidItemPaidFromDate(helperGetElementValues(
						prepaidElement, "PrepaidItemPaidFromDate"));
				prepaidLocal
						.setPrepaidItemPaidThroughDate(helperGetElementValues(
								prepaidElement, "PrepaidItemPaidThroughDate"));
				prepaidLocal
						.setPrepaidItemMonthsPaidCount(helperGetElementValues(
								prepaidElement, "PrepaidItemMonthsPaidCount"));
				prepaidLocal.setPaymentAmount(helperGetElementValues(
						prepaidElement, "PrepaidItemActualPaymentAmount"));
				prepaidLocal.setPaymentPaidByType(helperGetElementValues(
						prepaidElement, "PrepaidItemPaymentPaidByType"));
				if (helperGetElementValues(prepaidElement,
						"PrepaidItemPaymentTimingType").equals("AtClosing")) {
					prepaidLocal.setPaidOutsideOfClosingIndicator(false);
				} else {
					prepaidLocal.setPaidOutsideOfClosingIndicator(true);
				}
				
				String strType = helperGetElementValues(prepaidElement,"PrepaidItemType");
				if (strType.equalsIgnoreCase("other"))
					strType = helperGetElementValues( prepaidElement, "PrepaidItemTypeOtherDescription");					
				prepaidLocal.setType(strType);
				
				strLabel = helperGetElementAttribute(prepaidElement, "PrepaidItemType", "DisplayLabelText");
				if (strLabel.isEmpty() || strLabel.equals(""))
					strLabel = helperGetElementAttribute(prepaidElement, "PrepaidItemType", "gse:DisplayLabelText");
				if (strLabel.isEmpty() || strLabel.equals(""))
					strLabel = helperGetElementAttribute(prepaidElement, "PREPAID_ITEM_DETAIL", "DisplayLabelText");
				if (strLabel.isEmpty() || strLabel.equals(""))
					strLabel = helperGetElementAttribute(prepaidElement, "PREPAID_ITEM_DETAIL", "gse:DisplayLabelText");
				if (strLabel.isEmpty() || strLabel.equals("")){
					prepaidLocal.setLabel(fixSorts(StringFormatter.CAMEL.formatString(strType)));
					//System.out.println("Prepaid label from type:"+prepaidLocal.getLabel());
				} else {
					prepaidLocal.setLabel(fixSorts(StringFormatter.CAMEL.formatString(strLabel)));
					//System.out.println("Prepaid label from label:"+prepaidLocal.getLabel());
				}
				
				prepaidLocal.setPaymentToEntity(helperGetElementValues(
						prepaidElement, "FullName"));
				NodeList paymentNodeList = prepaidElement
						.getElementsByTagName(NS + "PREPAID_ITEM_PAYMENT");
				for (int pl = 0; pl < paymentNodeList.getLength(); pl++) {
					Element paymentElement = (Element) paymentNodeList.item(pl);
					if (paymentElement != null) {
						String payment = helperGetElementValues(paymentElement,
								"PrepaidItemActualPaymentAmount");
						if (helperGetElementValues(paymentElement,
								"PrepaidItemPaymentPaidByType")
								.equalsIgnoreCase("Buyer")) {
							if (helperGetElementValues(paymentElement,
									"PrepaidItemPaymentTimingType")
									.equalsIgnoreCase("BeforeClosing"))
								prepaidLocal
										.setBuyerOutsideClosingAmount(payment);
							else
								prepaidLocal.setBuyerAtClosingAmount(payment);
						} else if (helperGetElementValues(paymentElement,
								"PrepaidItemPaymentPaidByType")
								.equalsIgnoreCase("Seller")) {
							if (helperGetElementValues(paymentElement,
									"PrepaidItemPaymentTimingType")
									.equalsIgnoreCase("BeforeClosing"))
								prepaidLocal
										.setSellerOutsideClosingAmount(payment);
							else
								prepaidLocal.setSellerAtClosingAmount(payment);
						} else {
							prepaidLocal.setOtherEntity(helperGetElementValues(
									paymentElement,
									"PrepaidItemPaymentPaidByType"));
							prepaidLocal.setOtherAmount(payment);
						}
					}
				}
				//prepaidLocal.setLabel(fixSorts(prepaidLocal.getLabel()));
				//System.out.println("Prepaid:"+prepaidLocal.getLabel());
				prepaidList.add(prepaidLocal);
			}
		}
		prepaidList.sort(PrepaidComparator);
		for (Prepaids prepaidLocal:prepaidList)
			prepaidLocal.setLabel(prepaidLocal.getLabel().replaceAll(strPrepend,""));
		inputData.setPrepaidList(prepaidList);

		// Populate Escrow
		List<Escrows> escrowList = new ArrayList<Escrows>();
		NodeList escrowNodeList = thisRoot.getElementsByTagName(NS
				+ "ESCROW_ITEM");
		for (int fl = 0; fl < escrowNodeList.getLength(); fl++) {
			Element escrowElement = (Element) escrowNodeList.item(fl);
			if (escrowElement != null) {
				Escrows escrowLocal = new Escrows();
				escrowLocal
						.setIntegratedDisclosureSectionType(helperGetElementValues(
								escrowElement,
								"IntegratedDisclosureSectionType"));
				escrowLocal
						.setCollectedNumberOfMonthsCount(helperGetElementValues(
								escrowElement,
								"EscrowCollectedNumberOfMonthsCount"));
				
				escrowLocal.setMonthlyPaymentAmount(helperGetElementValues(
						escrowElement, "EscrowMonthlyPaymentAmount"));
				escrowLocal.setPaidToType(helperGetElementValues(escrowElement,
						"FeePaidToType"));
				escrowLocal.setPaymentAmount(helperGetElementValues(
						escrowElement, "EscrowItemActualPaymentAmount"));
				escrowLocal.setPaymentPaidByType(helperGetElementValues(
						escrowElement, "EscrowItemPaymentPaidByType"));
				if (!helperGetElementValues(escrowElement,
						"EscrowItemPaymentTimingType").equals("AtClosing")) {
					escrowLocal.setPaidOutsideOfClosingIndicator(true);
				} else if (helperGetElementValues(escrowElement,
						"EscrowItemPaymentTimingType").equals("AtClosing")) {
					escrowLocal.setPaidOutsideOfClosingIndicator(false);
				}
				
				String strType = helperGetElementValues(escrowElement,	"EscrowItemType");
				if(strType.equalsIgnoreCase("other"))
					strType = helperGetElementValues( escrowElement, "EscrowItemTypeOtherDescription");
				escrowLocal.setType(strType);
				
				strLabel = helperGetElementAttribute(escrowElement,	"EscrowItemType", "DisplayLabelText");
				if (strLabel.isEmpty() || strLabel.equals(""))
					strLabel = helperGetElementAttribute(escrowElement,	"EscrowItemType", "gse:DisplayLabelText");	
				if (strLabel.isEmpty() || strLabel.equals(""))
					strLabel = helperGetElementAttribute(escrowElement,	"ESCROW_ITEM_DETAIL", "DisplayLabelText");
				if (strLabel.isEmpty() || strLabel.equals(""))
					strLabel = helperGetElementAttribute(escrowElement,	"ESCROW_ITEM_DETAIL", "gse:DisplayLabelText");	
				
				if (strLabel.isEmpty() || strLabel.equals("")){
						escrowLocal.setLabel(fixSorts(StringFormatter.CAMEL.formatString(strType)));
						//System.out.println("Escrow label from type:"+escrowLocal.getLabel());
					} else {
						escrowLocal.setLabel(fixSorts(StringFormatter.CAMEL.formatString(strLabel)));
						//System.out.println("Escrow label from label:"+escrowLocal.getLabel());
					}
				
				escrowLocal.setPaymentToEntity(helperGetElementValues(
						escrowElement, "FullName"));
				NodeList paymentNodeList = escrowElement
						.getElementsByTagName(NS + "ESCROW_ITEM_PAYMENT");
				for (int pl = 0; pl < paymentNodeList.getLength(); pl++) {
					Element paymentElement = (Element) paymentNodeList.item(pl);
					if (paymentElement != null) {
						String payment = helperGetElementValues(paymentElement,
								"EscrowItemActualPaymentAmount");
						if (helperGetElementValues(paymentElement,
								"EscrowItemPaymentPaidByType")
								.equalsIgnoreCase("Buyer")) {
							if (helperGetElementValues(paymentElement,
									"EscrowItemPaymentTimingType")
									.equalsIgnoreCase("AtClosing"))
								escrowLocal.setBuyerAtClosingAmount(payment);
							else
								escrowLocal
										.setBuyerOutsideClosingAmount(payment);
						} else if (helperGetElementValues(paymentElement,
								"EscrowItemPaymentPaidByType")
								.equalsIgnoreCase("Seller")) {
							if (helperGetElementValues(paymentElement,
									"EscrowItemPaymentTimingType")
									.equalsIgnoreCase("AtClosing"))
								escrowLocal.setSellerAtClosingAmount(payment);
							else
								escrowLocal
										.setSellerOutsideClosingAmount(payment);
						} else {
							escrowLocal.setOtherEntity(helperGetElementValues(
									paymentElement,
									"EscrowItemPaymentPaidByType"));
							escrowLocal.setOtherAmount(payment);
						}
					}
				}
				//escrowLocal.setLabel(fixSorts(escrowLocal.getLabel()));
				escrowList.add(escrowLocal);
			}
		}
		escrowList.sort(EscrowComparator);
		for (Escrows escrowLocal:escrowList){
			escrowLocal.setLabel(escrowLocal.getLabel().replaceAll(strPrepend,""));
			//System.out.println("Escrow label sorted:"+escrowLocal.getLabel());
		}
		inputData.setEscrowList(escrowList);
		// System.err.println("Escrow count:"+escrowList.size());

		// Integrated Disclosure Sections -------------------------------
		List<ID_Subsection> idsList = new ArrayList<ID_Subsection>();
		NodeList idsSectionList = thisRoot.getElementsByTagName(NS
				+ "INTEGRATED_DISCLOSURE_SECTION_SUMMARY");
		for (int il = 0; il < idsSectionList.getLength(); il++) {
			Element idsSectionElement = (Element) idsSectionList.item(il);
			if (idsSectionElement != null) {
				if (idsSectionElement != null) {
					String sectionType = helperGetElementValues(
							idsSectionElement,
							"IntegratedDisclosureSectionType");
					String subsectionType = helperGetElementValues(
							idsSectionElement,
							"IntegratedDisclosureSubsectionType");
					if (subsectionType.equals("Other"))
						subsectionType = helperGetElementValues(
								idsSectionElement,
								"IntegratedDisclosureSubsectionTypeOtherDescription");
					String tolerance = helperGetElementValues(
							idsSectionElement,
							"LenderCreditToleranceCureAmount");
					NodeList idsSubList = idsSectionElement
							.getElementsByTagName(NS
									+ "INTEGRATED_DISCLOSURE_SUBSECTION_PAYMENT");
					for (int idl = 0; idl < idsSubList.getLength(); idl++) {
						ID_Subsection idsSubSection = new ID_Subsection();
						idsSubSection
								.setIntegratedDisclosureSectionType(sectionType);
						idsSubSection
								.setIntegratedDisclosureSubsectionType(subsectionType);
						idsSubSection.setLenderTolerance(tolerance);
						Element idsSubElement = (Element) idsSubList.item(idl);
						idsSubSection
								.setPaymentPaidByType(helperGetElementValues(
										idsSubElement,
										"IntegratedDisclosureSubsectionPaidByType"));
						idsSubSection.setPaymentAmount(helperGetElementValues(
								idsSubElement,
								"IntegratedDisclosureSubsectionPaymentAmount"));
						if (helperGetElementValues(idsSubElement,
								"IntegratedDisclosureSubsectionPaymentTimingType")
								.equals("AtClosing")) {
							idsSubSection
									.setPaidOutsideOfClosingIndicator(false);
						} else if (helperGetElementValues(idsSubElement,
								"IntegratedDisclosureSubsectionPaymentTimingType")
								.equals("BeforeClosing")) {
							idsSubSection
									.setPaidOutsideOfClosingIndicator(true);
						}
						idsSubSection.setLabel(StringFormatter.CAMEL
								.formatString(helperGetElementAttribute(
										idsSubElement,
										"IntegratedDisclosureSubsectionType",
										"DisplayLabelText")));
						if (idsSubSection.getLabel().isEmpty()) {
							if (idsSubSection.getType().equalsIgnoreCase(
									"Other")) {
								idsSubSection
										.setLabel(StringFormatter.CAMEL
												.formatString(helperGetElementValues(
														idsSubElement,
														"IntegratedDisclosureSubsectionTypeOtherDescription")));
							} else {
								idsSubSection.setLabel(StringFormatter.CAMEL
										.formatString(idsSubSection.getType()));
							}
						}
						idsList.add(idsSubSection);
					}
				}
			}
		}
		inputData.setIdsList(idsList);
	}

	private void populateParties(InputData inputData, Element thisRoot)
			throws DOMException {
		// get parties
		List<PartyData> partyList = new ArrayList<PartyData>();

		NodeList partyNodeList = thisRoot.getElementsByTagName(NS + "PARTY");
		for (int s = 0; s < partyNodeList.getLength(); s++) {

			Node partyNode = partyNodeList.item(s);
			if (partyNode.getNodeType() == Node.ELEMENT_NODE) {
				PartyData partyLocal = new PartyData();
				Element partyElement = (Element) partyNode;
				partyLocal.setRoleType(helperGetElementValues(partyElement,
						"PartyRoleType"));

				// individual data
				String partyName = "";
				partyName = helperGetElementValues(partyElement, "FirstName");
				// System.err.println("Name: " + partyName);
				partyName = partyName + " "
						+ helperGetElementValues(partyElement, "MiddleName");
				// System.err.println("Name: " + partyName);
				partyName = partyName + " "
						+ helperGetElementValues(partyElement, "LastName");
				// System.err.println("Name: " + partyName);
				partyName = partyName + " "
						+ helperGetElementValues(partyElement, "SuffixName");
				// System.err.println("Name:" + partyName.trim()+":");
				partyLocal.setPartyName(partyName);

				// legal entity data
				if (partyName.trim().equals("")) {
					partyName = helperGetElementValues( partyElement, "FullName");
					if (!partyName.trim().equals("")){
						partyLocal.setPartyName(partyName);
						partyLocal.setIsLegalEntity(true);
					}
				}

				NodeList contactpointsNodeList = partyElement
						.getElementsByTagName(NS + "CONTACT_POINTS");
				for (int contactpointiteration = 0; contactpointiteration < contactpointsNodeList
						.getLength(); contactpointiteration++) {
					Node contactpointNode = contactpointsNodeList
							.item(contactpointiteration);
					if (contactpointNode.getNodeType() == Node.ELEMENT_NODE) {
						Element contactpointElement = (Element) contactpointNode;
						partyLocal.setPhoneNumber(helperGetElementValues(
								contactpointElement,
								"ContactPointTelephoneValue"));
						partyLocal.setEmailAddress(helperGetElementValues(
								contactpointElement, "ContactPointEmailValue"));
					}
				}

				// address data
				helperGetAddress(partyLocal, partyElement);

				NodeList rolesNodeList = partyElement.getElementsByTagName(NS
						+ "ROLES");
				for (int roleiteration = 0; roleiteration < rolesNodeList
						.getLength(); roleiteration++) {
					Node roleNode = rolesNodeList.item(roleiteration);
					if (roleNode.getNodeType() == Node.ELEMENT_NODE) {
						Element roleElement = (Element) roleNode;
						// role data - one per party for now
						partyLocal.setRoleLabel(helperGetElementAttribute(
								roleElement, "ROLE", "xlink:label"));
						partyLocal.setLicenseURI(helperGetElementAttribute(
								roleElement, "LicenseIdentifier",
								"IdentifierOwnerURI"));
						partyLocal.setLicenseIdentifier(helperGetElementValues(
								roleElement, "LicenseIdentifier"));
						partyLocal
								.setLicenseIssuingStateCode(helperGetElementValues(
										roleElement,
										"LicenseIssuingAuthorityStateCode"));
						partyLocal
								.setRealEstateAgentType(helperGetElementValues(
										roleElement, "RealEstateAgentType"));
					}
				}

				// add party to local list
				partyList.add(partyLocal);
			}
		}

		// add local list to input data
		inputData.setPartyList(partyList);
	}

	private void populateClosingMap(ClosingMap closingMap,
			Element documentClassification, Element thisRoot)
			throws DOMException {
		// For simple containers that do not have a type field
		// or have a type field and only one element
		// just pull the data and store it in a hash map
		// by convention the first string parameter is always the container
		// also for a typed container the second string parameter is always the
		// name of the type element
		helperGetContainer(thisRoot, closingMap, "AMORTIZATION_RULE",
				"AmortizationType");

		helperGetContainer(thisRoot, closingMap, "BUYDOWN",
				"BuydownInitialEffectiveInterestRatePercent",
				"BuydownChangeFrequencyMonthsCount",
				"BuydownDurationMonthsCount", "BuydownIncreaseRatePercent",
				"BuydownReflectedInNoteIndicator");

		helperGetContainer(thisRoot, closingMap, "CLOSING_INFORMATION_DETAIL",
				"ClosingDate", "DisbursementDate",
				"ClosingAgentOrderNumberIdentifier",
				"CashFromBorrowerAtClosingAmount",
				"CashToBorrowerAtClosingAmount", "CashToSellerAtClosingAmount",
				"CashFromSellerAtClosingAmount", "DocumentOrderClassificationType");
		// If "CLOSING_INFORMATION_DETAIL.DocumentOrderClassificationType" = "Preliminary" then show draft watermark

		helperGetContainer(thisRoot, closingMap, "CONSTRUCTION",
				"ConstructionLoanTotalTermMonthsCount", "ConstructionLoanType");

		helperGetContainer(documentClassification, closingMap,
				"DOCUMENT_CLASS", "DocumentType",
				"DocumentTypeOtherDescription");

		helperGetContainer(documentClassification, closingMap,
				"DOCUMENT_CLASSIFICATION_DETAIL",
				"DocumentFormIssuingEntityNameType",
				"DocumentFormIssuingEntityVersionIdentifier",
				"DocumentSignatureRequiredIndicator");

		helperGetContainer(thisRoot, closingMap, "ESCROW_DETAIL",
				"EscrowAggregateAccountingAdjustmentAmount");

		helperGetContainer(thisRoot, closingMap,
				"ESTIMATED_PROPERTY_COST_DETAIL",
				"ProjectedPaymentEstimatedTaxesInsuranceAssessmentTotalAmount");

		helperGetContainer(thisRoot, closingMap, "EXECUTION_DETAIL",
				"ExecutionDate");

		helperGetContainer(thisRoot, closingMap, "FEE_SUMMARY_DETAIL",
				"APRPercent", "FeeSummaryTotalAmountFinancedAmount",
				"FeeSummaryTotalFinanceChargeAmount",
				"FeeSummaryTotalInterestPercent",
				"FeeSummaryTotalOfAllPaymentsAmount");

		helperGetContainer(thisRoot, closingMap, "FORECLOSURE_DETAIL",
				"DeficiencyRightsPreservedIndicator");

		helperGetContainer(thisRoot, closingMap, "HIGH_COST_MORTGAGE",
				"AveragePrimeOfferRatePercent",
				"RegulationZExcludedBonaFideDiscountPointsIndicator",
				"RegulationZExcludedBonaFideDiscountPointsPercent",
				"RegulationZTotalAffiliateFeesAmount",
				"RegulationZTotalLoanAmount",
				"RegulationZTotalPointsAndFeesAmount");

		helperGetContainer(thisRoot, closingMap, "INDEX_RULE", "IndexType",
				"IndexTypeOtherDescription");

		helperGetContainer(thisRoot, closingMap,
				"INTEGRATED_DISCLOSURE_DETAIL",
				"IntegratedDisclosureDocumentType",
				"IntegratedDisclosureDocumentTypeOtherDescription",
				"IntegratedDisclosureHomeEquityLoanIndicator",
				"IntegratedDisclosureIssuedDate",
				"IntegratedDisclosureLoanProductDescription",
				"FirstYearTotalEscrowPaymentAmount",
				"FirstYearTotalNonEscrowPaymentAmount",
				"FirstYearTotalEscrowPaymentDescription",
				"FirstYearTotalNonEscrowPaymentDescription",
				"DocumentSignatureRequiredIndicator",
				"dd:InitialEscrowPaymentAtClosing");

		helperGetTypedContainer(thisRoot, closingMap,
				"INTEGRATED_DISCLOSURE_SECTION_SUMMARY_DETAIL",
				"IntegratedDisclosureSectionType",
				"IntegratedDisclosureSectionTotalAmount");

		helperGetTypedContainer(thisRoot, closingMap,
				"INTEGRATED_DISCLOSURE_SUBSECTION_PAYMENT",
				"IntegratedDisclosureSubSectionType",
				"IntegratedDisclosureSubSectionTotalAmount");

		helperGetContainer(thisRoot, closingMap, "INTEREST_ONLY",
				"InterestOnlyTermMonthsCount");

		helperGetContainer(thisRoot, closingMap,
				"INTEREST_RATE_LIFETIME_ADJUSTMENT_RULE",
				"CeilingRatePercentEarliestEffectiveMonthsCount",
				"CeilingRatePercent", "FirstRateChangeMonthsCount",
				"FloorRatePercent", "MarginRatePercent");

		helperGetContainer(thisRoot, closingMap, "gse:LATE_CHARGE_RULE",
				"gse:LateChargeAmount", "gse:LateChargeGracePeriodDaysCount",
				"gse:LateChargeMaximumAmount","gse:LateChargeMinimumAmount",
				"gse:LateChargeRatePercent", "gse:LateChargeType");

		helperGetContainer(thisRoot, closingMap, "LOAN_DETAIL",
				"AssumabilityIndicator", "BalloonIndicator",
				"BalloonPaymentAmount",
				"BuydownTemporarySubsidyFundingIndicator",
				"ConstructionLoanIndicator", "DemandFeatureIndicator",
				"EscrowAbsenceReasonType", "EscrowIndicator",
				"InterestOnlyIndicator", "InterestRateIncreaseIndicator",
				"LoanAmountIncreaseIndicator", "MIRequiredIndicator",
				"NegativeAmortizationIndicator", "PaymentIncreaseIndicator",
				"PrepaymentPenaltyIndicator",
				"SeasonalPaymentFeatureIndicator",
				"TotalSubordinateFinancingAmount");

		helperGetTypedContainer(thisRoot, closingMap, "LOAN_IDENTIFIER",
				"LoanIdentifierType", "LoanIdentifier");

		helperGetContainer(thisRoot, closingMap, "LOAN_PRICE_QUOTE_DETAIL",
				"LoanPriceQuoteInterestRatePercent");

		helperGetContainer(thisRoot, closingMap, "MATURITY_RULE",
				"LoanMaturityPeriodCount", "LoanMaturityPeriodType",
				"LoanTermMaximumMonthsCount");

		helperGetContainer(thisRoot, closingMap, "MI_DATA_DETAIL",
				"MICertificateIdentifier");

		helperGetContainer(thisRoot, closingMap, "NEGATIVE_AMORTIZATION_RULE",
				"NegativeAmortizationMaximumLoanBalanceAmount",
				"NegativeAmortizationLimitMonthsCount",
				"NegativeAmortizationType");

		helperGetContainer(thisRoot, closingMap, "PARTIAL_PAYMENT",
				"PartialPaymentApplicationMethodType");

		helperGetContainer(thisRoot, closingMap,
				"PREPAYMENT_PENALTY_LIFETIME_RULE",
				"PrepaymentPenaltyMaximumLifeOfLoanAmount",
				"PrepaymentPenaltyExpirationMonthsCount");

		helperGetContainer(thisRoot, closingMap, "PAYMENT_RULE",
				"InitialPrincipalAndInterestPaymentAmount",
				"FullyIndexedInitialPrincipalAndInterestPaymentAmount",
				"PartialPaymentAllowedIndicator", "PaymentFrequencyType",
				"PaymentOptionIndicator", "ScheduledFirstPaymentDate",
				"SeasonalPaymentPeriodEndMonth","gse:TotalOptionalPaymentCount",
				"gse:TotalStepPaymentCount","SeasonalPaymentPeriodStartMonth");

		helperGetContainer(thisRoot, closingMap, "PROJECTED_PAYMENT",
				"ProjectedPaymentPrincipalAndInterestMinimumPaymentAmount",
				"ProjectedPaymentPrincipalAndInterestMaximumPaymentAmount",
				"ProjectedPaymentPrincipalAndInterestMaximumPaymentAmount",
				"ProjectedPaymentEstimatedEscrowPaymentAmount",
				"ProjectedPaymentEstimatedTaxesInsuranceAssessmentTotalAmount",
				"ProjectedPaymentCalculationPeriodEndNumber",
				"ProjectedPaymentMIPaymentAmount", "PaymentFrequencyType",
				"ProjectedPaymentEstimatedTotalMinimumPaymentAmount",
				"ProjectedPaymentEstimatedTotalMaximumPaymentAmount");

		helperGetContainer(thisRoot, closingMap, "PROPERTY_DETAIL",
				"PropertyEstimatedValueAmount");

		helperGetContainer(thisRoot, closingMap, "PROPERTY_VALUATION_DETAIL",
				"PropertyEstimatedValueAmount", "PropertyValuationAmount");

		helperGetContainer(thisRoot, closingMap,
				"PRINCIPAL_AND_INTEREST_ADJUSTMENT_LIMITED_PAYMENT_OPTION",
				"LimitedPrincipalAndInterestPaymentEffectiveDate",
				"LimitedPrincipalAndInterestPaymentPeriodEndDate");

		helperGetContainer(
				thisRoot,
				closingMap,
				"PRINCIPAL_AND_INTEREST_PAYMENT_LIFETIME_ADJUSTMENT_RULE",
				"FinalPrincipalAndInterestPaymentChangeDate",
				"FirstPrincipalAndInterestPaymentChangeMonthsCount",
				"PrincipalAndInterestPaymentMaximumAmountEarliestEffectiveMonthsCount",
				"PrincipalAndInterestPaymentMaximumAmount");

		helperGetContainer(thisRoot, closingMap, "QUALIFIED_MORTGAGE_DETAIL",
				"AbilityToRepayMethodType");

		helperGetContainer(thisRoot, closingMap, "SALES_CONTRACT_DETAIL",
				"PersonalPropertyAmount", "PersonalPropertyIncludedIndicator",
				"RealPropertyAmount", "SalesContractAmount");

		helperGetContainer(thisRoot, closingMap, "SUBJECT_PROPERTY",
				"AddressLineText", "AddressUnitDesignatorType",
				"AddressUnitIdentifier", "CityName", "PostalCode", "StateCode");

		helperGetContainer(thisRoot, closingMap, "TERMS_OF_LOAN",
				"AssumedLoanAmount","DisclosedFullyIndexedRatePercent","LienPriorityType",
				"LoanPurposeType", "MortgageType",
				"MortgageTypeOtherDescription", "NoteAmount",
				"NoteRatePercent", "WeightedAverageInterestRatePercent");

	}

	void helperGetAddress(Object objectLocal, Element partyElement)
			throws DOMException {
		// get unparsed legal description if it exists
		((Address) objectLocal)
				.setUnparsedLegalDescription(helperGetElementValues(
						partyElement, "UnparsedLegalDescription"));
		((Address) objectLocal).setAddressLine(helperGetElementValues(
				partyElement, "AddressLineText"));
		((Address) objectLocal).setAddressType(helperGetElementValues(
				partyElement, "AddressType"));
		((Address) objectLocal)
				.setAddressUnitDesignatorType(helperGetElementValues(
						partyElement, "AddressUnitDesignatorType"));
		if (((Address) objectLocal).getAddressUnitDesignatorType().equals(
				"Other")) {
			((Address) objectLocal)
					.setAddressUnitDesignatorType(helperGetElementValues(
							partyElement,
							"AddressUnitDesignatorTypeOtherDescription"));
		}
		((Address) objectLocal).setAddressUnit(helperGetElementValues(
				partyElement, "AddressUnitIdentifier"));
		((Address) objectLocal).setCityName(helperGetElementValues(
				partyElement, "CityName"));
		((Address) objectLocal).setCountryCode(helperGetElementValues(
				partyElement, "CountryCode"));
		((Address) objectLocal).setPostalCode(helperGetElementValues(
				partyElement, "PostalCode"));
		((Address) objectLocal).setStateCode(helperGetElementValues(
				partyElement, "StateCode"));
	}

	String helperGetElementAttribute(Element entityTypeElmn, String xmlElement,
			String xmlAttribute) throws DOMException {
		if (entityTypeElmn != null) {
			NodeList tmpContainerList = entityTypeElmn.getElementsByTagName(NS + xmlElement);
			if (tmpContainerList != null) {
				Element tmpContainerElement = (Element)tmpContainerList.item(0);
				if (tmpContainerElement != null) {
					NamedNodeMap mapAttributes = tmpContainerElement.getAttributes();
					if (mapAttributes != null) {
						Node nodeAttribute = mapAttributes.getNamedItem(xmlAttribute);
						if (nodeAttribute != null) {
							return nodeAttribute.getTextContent();
						} else {
							// System.err.println("ATTRIBUTE NOT FOUND for ELEMENT:"+xmlAttribute+" "+xmlElement);
						}
					} else {
						// System.err.println("ATTRIBUTES NOT FOUND for ELEMENT:"+xmlElement);
					}
				} else {
					// System.err.println("ELEMENT NOT FOUND:"+xmlElement);
				}
			} else {
				// System.err.println("CONTAINER NOT FOUND:"+xmlElement);
			}
		}
		return "";
	}

	String helperGetAttributeValues(Element entityTypeElmn, String xmlElement)
			throws DOMException {
		if (entityTypeElmn != null) {
			String str = entityTypeElmn.getAttribute(xmlElement);
			if (str != null) {
				// System.err.println("CONTAINER NOT FOUND:"+xmlElement);
				return str;
			}
		}
		return "";
	}

	String helperGetElementValues(Element entityTypeElmn, String xmlElement)
			throws DOMException {
		if (entityTypeElmn != null) {
			NodeList tmpContainerList = entityTypeElmn.getElementsByTagName(NS
					+ xmlElement);
			if (tmpContainerList != null) {
				Element tmpContainerElement = (Element) tmpContainerList
						.item(0);
				if (tmpContainerElement != null) {
					NodeList nodeList = tmpContainerElement.getChildNodes();
					if (nodeList != null && nodeList.getLength() > 0) {
						return nodeList.item(0).getTextContent();
					} else {
						// System.err.println("CHILD NOT FOUND:"+xmlElement);
					}
				} else {
					// System.err.println("ELEMENT NOT FOUND:"+xmlElement);
				}
			} else {
				// System.err.println("CONTAINER NOT FOUND:"+xmlElement);
			}
		}
		return "";
	}

	String helperGetElementValues(Element entityTypeElmn, String xmlElement,
			String NS) throws DOMException {
		if (entityTypeElmn != null) {
			NodeList tmpContainerList = entityTypeElmn.getElementsByTagName(NS
					+ xmlElement);
			if (tmpContainerList != null) {
				Element tmpContainerElement = (Element) tmpContainerList
						.item(0);
				if (tmpContainerElement != null) {
					NodeList nodeList = tmpContainerElement.getChildNodes();
					if (nodeList != null) {
						return nodeList.item(0).getTextContent();
					} else {
						// System.err.println("CHILD NOT FOUND:"+xmlElement);
					}
				} else {
					// System.err.println("ELEMENT NOT FOUND:"+xmlElement);
				}
			} else {
				// System.err.println("CONTAINER NOT FOUND:"+xmlElement);
			}
		}
		return "";
	}

	void helperGetContainer(Element thisRoot, ClosingMap closingMap,
			String... xmlElements) throws DOMException {
		Element containerElement = null;
		NodeList containerList = null;
		// first array sting element is the name of the container subsequent
		// elements are the nodes
		if (thisRoot != null && xmlElements != null) {
			if(!xmlElements[0].contains(":"))
				containerList = thisRoot.getElementsByTagName(NS + xmlElements[0]);
			else
				containerList = thisRoot.getElementsByTagName(xmlElements[0]);
			// System.err.println("item:"+containerList.item(0));
			if (containerList != null && containerList.getLength() > 0)
				containerElement = (Element) containerList.item(0);
			if (containerElement != null) {
				// System.err.println("entityType"+containerElement.getNodeName());
				for (int s = 1; s < xmlElements.length; s++) {
					String tagName = xmlElements[s];
					if (!tagName.contains(":"))
						tagName = NS + xmlElements[s];
					NodeList elementList = containerElement
							.getElementsByTagName(tagName);
					if (elementList.getLength() > 0) {
						Element nodeElement = (Element) elementList.item(0);
						if (nodeElement != null) {
							NodeList nodeValue = nodeElement.getChildNodes();
							if (nodeValue != null && nodeValue.getLength() > 0) {
								closingMap.setClosingMapValue(xmlElements[0]
										+ "." + xmlElements[s],
										nodeValue.item(0).getTextContent());
							} else {
								// System.err.println("ELEMENT2 NOT FOUND:"+xmlElements[s]);
							}
						} else {
							// System.err.println("CHILD NOT FOUND:"+xmlElements[s]);
						}
					} else {
						for (String namespace : nameSpaces) {
							if (!namespace.equals(NS)) {
								elementList = containerElement
										.getElementsByTagName(namespace
												+ xmlElements[s]);
								if (elementList.getLength() > 0) {
									Element nodeElement = (Element) elementList
											.item(0);
									if (nodeElement != null) {
										NodeList nodeValue = nodeElement
												.getChildNodes();
										if (nodeValue != null) {
											closingMap.setClosingMapValue(
													xmlElements[0] + "."
															+ xmlElements[s],
													nodeValue.item(0)
															.getTextContent());
										} else {
											// System.err.println("ELEMENT2 NOT FOUND:"+xmlElements[s]);
										}
									} else {
										// System.err.println("CHILD NOT FOUND:"+xmlElements[s]);
									}
									break;
								} else {
									// System.err.println("ELEMENT1 NOT FOUND:"+xmlElements[s]);
								}
							}
						}
					}
				}
			} else {
				// System.err.println("CONTAINER NOT FOUND:"+xmlElements[0]);
			}
		}
		return;
	}

	void helperGetTypedContainer(Element thisRoot, ClosingMap closingMap,
			String... xmlElements) throws DOMException {
		// first array sting element is the name of the container subsequent
		// elements are the nodes
		if (xmlElements != null) {
			NodeList outerList = thisRoot.getElementsByTagName(NS
					+ xmlElements[0]);
			for (int c = 0; c < outerList.getLength(); c++) {
				Element containerElement = (Element) outerList.item(c);
				if (containerElement != null) {
					for (int s = 2; s < xmlElements.length; s++) {
						NodeList containerList = containerElement
								.getElementsByTagName(NS + xmlElements[s]);
						if (containerList != null) {
							Element nodeElement = (Element) containerList
									.item(0);
							if (nodeElement != null) {
								NodeList nodeValue = nodeElement
										.getChildNodes();
								if (nodeValue != null) {
									NodeList typeList = containerElement
											.getElementsByTagName(NS
													+ xmlElements[1]);
									if (typeList != null) {
										Element typeElement = (Element) typeList
												.item(0);
										if (typeElement != null) {
											NodeList typeValue = typeElement
													.getChildNodes();
											if (typeValue != null) {
												closingMap
														.setClosingMapValue(
																xmlElements[0]
																		+ "."
																		+ typeValue
																				.item(0)
																				.getTextContent(),
																nodeValue
																		.item(0)
																		.getTextContent());
											}
										} else {
											// System.err.println("Type2 NOT FOUND:"+xmlElements[1]);
										}
									} else {
										// System.err.println("Type1 NOT FOUND:"+xmlElements[1]);
									}
								} else {
									// System.err.println("Element NOT FOUND:"+xmlElements[s]);
								}
							}
						} else {
							// System.err.println("CONTAINER2 NOT FOUND:"+xmlElements[0]);
						}
					}
				} else {
					// System.err.println("CONTAINER1 NOT FOUND:"+xmlElements[0]);
				}
			}
		}
		return;
	}
}