package com.app.core.html;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class UploadButton extends HTMLShapes{

	public UploadButton(String extraMultiEditName, int multiEditRowNum ,String colName, String txtAlign) {
		super(extraMultiEditName , multiEditRowNum, colName, txtAlign);
	}

	@Override
	public StringBuilder getHtmlInput(HashMap<String, Integer> sqlColsSizes,
									  HashMap <String , String > userDefinedColsHtmlType, 
									  LinkedHashMap<String, LinkedHashMap<String, String>> colMapValues,
									  ArrayList<String> defValue, 
									  String Readonly,
									  HtmlInputConfig htmlInputConfig) {
	
		String req="";
    	if (htmlInputConfig.isRequired())
    		req= "required='required'";
    	
    	String placeHolder = "";
    	if (defValue!=null && defValue.size()>0)
    		if (defValue.get(0)!=null && !defValue.get(0).isEmpty()){
    			req ="";
    			if (defValue.size()>1)
    				placeHolder = defValue.get(1);
    		}
    	//System.out.println(colName+"========="+placeHolder);
		StringBuilder sb = new StringBuilder();
		String colNameToSave=colName;
		if (htmlInputConfig.isMultiEdit())
			colNameToSave += extraMultiEditName;
		sb.append( "<div><a class='close fileinput-exists' onclick=\"clearFieStyleSelector('"+colNameToSave+"','"+placeHolder+"' , '"+htmlInputConfig.isRequired()+"');\" style='float: none' data-dismiss='fileinput' href='#'>×</a>"
				+ "<input type='file' name='"+colNameToSave+"' id='"+colNameToSave+"' data-placeholder='"+placeHolder+"'"
						+ "class='filestyle' data-classButton='btn btn-primary' data-size='sm'"
				+ " data-classIcon='icon-plus' data-buttonText='Upload file.' "+req+" >"
				+ ""
				+ "</div>");
		return sb;
	}

}
