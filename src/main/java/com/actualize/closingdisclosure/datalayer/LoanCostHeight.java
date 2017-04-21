package com.actualize.closingdisclosure.datalayer;

public class LoanCostHeight {
	
	float loadGridHeight;
	String footeradjust;

	
	
	
	private static LoanCostHeight loanCostHeight = null;
	
	private LoanCostHeight()
	{}
	
	public static LoanCostHeight getLoanCostHeight (){
		
		if(loanCostHeight == null){
			loanCostHeight = new LoanCostHeight();
		}
		return loanCostHeight;
	}

	public String getFooteradjust() {
		return footeradjust;
	}

	public void setFooteradjust(String footeradjust) {
		this.footeradjust = footeradjust;
	}
	
	public float getLoadGridHeight() {
		return loadGridHeight;
	}

	public void setLoadGridHeight(float loadGridHeight) {
		this.loadGridHeight = loadGridHeight;
	}
}
