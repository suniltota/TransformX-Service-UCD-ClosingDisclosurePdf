package com.actualize.closingdisclosure.datalayer;

import java.util.HashMap;

public class PageTwo {
	public final static int ORIGINATION_CHARGES = 0;
	public final static int DID_NOT_SHOP_FOR = 1;
	public final static int DID_SHOP_FOR = 2;
	public final static int TOTAL_LOAN_COSTS = 3;
	public final static int TAXES_GOVERNMENT = 4;
	public final static int PREPAIDS = 5;
	public final static int ESCROWS = 6;
	public final static int OTHER_FEES = 7;
	public final static int TOTAL_OTHER_FEES = 8;
	public final static int TOTAL_CLOSING = 9;
	
	private HashMap<String, Boolean> doubleLines = null;
	private String currentSection = "";
	
	private boolean expandedFees = false;
	
	private int[] starts = new int[10];

	private int[] slacks = new int[] {7,10,8,0,1,3,5,8,0,0};
	//the below are the standard values for the cfpb form
//	slacks[ORIGINATION_CHARGES] = 7;
//	slacks[DID_NOT_SHOP_FOR] 	= 10;
//	slacks[DID_SHOP_FOR] 		= 8;
//	slacks[TOTAL_LOAN_COSTS]	= 0;	
//	slacks[TAXES_GOVERNMENT] 	= 1;
//	slacks[PREPAIDS]			= 3;
//	slacks[ESCROWS] 			= 5;
//	slacks[OTHER_FEES]  		= 8;
//	slacks[TOTAL_OTHER_FEES] 	= 0;
//	slacks[TOTAL_CLOSING] 		= 0;
	
	private int[] required =new int[] {2,1,1,3,2,4,4,1,3,2}; 
	//the below are the standard values for the cfpb form
//	required[ORIGINATION_CHARGES] = 2;
//	required[DID_NOT_SHOP_FOR] 	= 1;
//	required[DID_SHOP_FOR] 		= 1;
//	required[TOTAL_LOAN_COSTS]	= 3;	
//	required[TAXES_GOVERNMENT] 	= 2;
//	required[PREPAIDS]			= 4;
//	required[ESCROWS] 			= 4;
//	required[OTHER_FEES]  		= 1;
//	required[TOTAL_OTHER_FEES] 	= 3;
//	required[TOTAL_CLOSING] 	= 2;		

	private int loanCostsGridHeight = 0;
	private int otherCostsGridHeight = 0;
	
	static public final float defaultWidthLabel1 = 120f/72f;
	static public final float defaultWidthLabel2 = 120f/72f;

	private float widthLabel1  = defaultWidthLabel1;
	private float widthLabel2  = defaultWidthLabel2;
	private float widthBuyer1  =  60f/72f;
	private float widthBuyer2  =  60f/72f;
	private float widthSeller1 =  60f/72f;
	private float widthSeller2 =  60f/72f;
	private float widthOther   =  60f/72f;
	private float widthPage    =  7.5f;
	
	public void setStart(Integer i,Integer v) {
		starts[i] = v;
	}
	public void setRequired(Integer i,Integer v) {
		required[i] = v;
	}
	public int getRequired(Integer i) {
		return required[i];
	}
	public int getStart(Integer i) {
		return starts[i];
	}
	public void setSlack(Integer i,Integer v) {
		slacks[i] = v;
	}
	public int getSlack(Integer i) {
		return slacks[i];
	}
	public void useSlack(Integer i, Integer amount){
		slacks[i]   = slacks[i]   - amount;
		required[i] = required[i] + amount;
	}
	public void decrementSlack(Integer i){
		slacks[i]--;
	}
	public void decrementStart(Integer i){
		starts[i]--;
	}
	public void incrementSlack(Integer i){
		slacks[i]++;
	}
	public void incrementStart(Integer i){
		starts[i]++;
	}
	public void incrementRequired(Integer i){
		required[i]++;
	}
	public int getLoanCostsGridHeight() {
		return loanCostsGridHeight;
	}
	public int getOtherCostsGridHeight() {
		return otherCostsGridHeight;
	}
	public void setLoanCostsGridHeight(Integer v) {
		loanCostsGridHeight = v;
	}
	public void setOtherCostsGridHeight(Integer v) {
		otherCostsGridHeight = v;
	}
	public void incrementLoanCostsHeight(){
		loanCostsGridHeight++;
	}
	public void decrementLoanCostsHeight(){
		loanCostsGridHeight--;
	}
	public void incrementOtherCostsHeight(){
		otherCostsGridHeight++;
	}
	public void decrementOtherCostsHeight(){
		otherCostsGridHeight--;
	}
	public boolean isExpandedFees() {
		return expandedFees;
	}
	public void setExpandedFees(boolean expandedFees) {
		this.expandedFees = expandedFees;
	}
	public float getWidthLabel1() {
		return widthLabel1;
	}
	public void setWidthLabel1(float widthLabel1) {
		this.widthLabel1 = widthLabel1;
	}
	public float getWidthLabel2() {
		return widthLabel2;
	}
	public void setWidthLabel2(float widthColumn2) {
		this.widthLabel2 = widthColumn2;
	}
	public float getWidthBuyer1() {
		return widthBuyer1;
	}
	public void setWidthBuyer1(float widthBuyer1) {
		this.widthBuyer1 = widthBuyer1;
	}
	public float getWidthBuyer2() {
		return widthBuyer2;
	}
	public void setWidthBuyer2(float widthBuyer2) {
		this.widthBuyer2 = widthBuyer2;
	}
	public float getWidthSeller1() {
		return widthSeller1;
	}
	public void setWidthSeller1(float widthSeller1) {
		this.widthSeller1 = widthSeller1;
	}
	public float getWidthSeller2() {
		return widthSeller2;
	}
	public void setWidthSeller2(float widthSeller2) {
		this.widthSeller2 = widthSeller2;
	}
	public float getWidthOther() {
		return widthOther;
	}
	public void setWidthOther(float widthOther) {
		this.widthOther = widthOther;
	}
	public float getWidthPage() {
		return widthPage;
	}
	public void setWidthPage(float widthPage) {
		this.widthPage = widthPage;
	}
	public boolean getDoubleLines(int row) {
		if (doubleLines != null && doubleLines.get(currentSection + String.valueOf(row)) != null){
			return doubleLines.get(currentSection + String.valueOf(row));
		}
		return false;
	}
	public void setDoubleLines(int row) {
		if (doubleLines == null){
			doubleLines = new HashMap<String, Boolean>();
		}
		doubleLines.put(currentSection + String.valueOf(row), true);
	}
	public void printDoubleOtherLines(){
		//Iterate over HashMap
		  for(String key: doubleLines.keySet()){
		      System.out.println(key  +" :: "+ doubleLines.get(key));}
		 }
	public String getCurrentSection() {
		return currentSection;
	}
	public void setCurrentSection(String currentSection) {
		this.currentSection = currentSection;
	}
}
