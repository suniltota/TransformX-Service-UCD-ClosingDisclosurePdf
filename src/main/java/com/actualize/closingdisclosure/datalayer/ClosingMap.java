package com.actualize.closingdisclosure.datalayer;

import java.util.HashMap;
import java.util.Map;

public class ClosingMap {
    private Map<String , String> closingMap = new HashMap<String, String>();
    
	public Map<String , String> getClosingMap() {
		return closingMap;
	}
	public String getClosingMapValue(String key) {
		if (closingMap.get(key) != null) {
			return closingMap.get(key);
		}else {
			return "";
		}
	}
	public void setClosingMapValue(String key, String value) {
		this.closingMap.put(key,value);
	}
	public void printClosingMap(){
	//Iterate over HashMap
	  for(String key: closingMap.keySet()){
		  if(key.equalsIgnoreCase("CLOSING_INFORMATION_DETAIL.DocumentOrderClassificationType"))
			  System.out.println(key  +" :: "+ closingMap.get(key));}
	 }
}
