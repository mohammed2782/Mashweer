package com.app.core.html;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class DateField extends  HTMLShapes{

	public DateField(String extraMultiEditName, int multiEditRowNum , String colName, String txtAlign) {
		super(extraMultiEditName , multiEditRowNum , colName, txtAlign);
	}

	@Override
	public StringBuilder getHtmlInput(	HashMap<String , Integer> sqlColsSizes , 
			HashMap <String , String > userDefinedColsHtmlType, 
			LinkedHashMap<String ,  LinkedHashMap<String , String>> colMapValues, 
			  ArrayList<String> defValue ,
			  String Readonly,
			  HtmlInputConfig htmlInputConfig) {
		int size = 17;
		String calFormat = "%Y-%m-%d %H:%M:%S";
		if (userDefinedColsHtmlType.get(colName).equalsIgnoreCase("DATE")){
			size = 10;
			calFormat = "%Y-%m-%d";
		}
		
		String colNameToSave=colName;
		if (htmlInputConfig.isMultiEdit())
			colNameToSave += extraMultiEditName;
		
		String DisplayValue="";
    	String style ="";
    	
    	if (defValue!=null){
    		if (!defValue.isEmpty())
    			DisplayValue = defValue.get(0); 
    		if (DisplayValue==null){
    			DisplayValue="";
    		}
    	}
    	String req="";
    	if (htmlInputConfig.isRequired())
    		req= "required='required'";
    	
    	
    	if (Readonly !=null && Readonly.trim().length()>0)
    		;
    	else if (htmlInputConfig.isRequired())
    		Readonly = "readonly='readonly'";
    	
    	
    	style = "text-align:"+textAlign+"; background-color:"+htmlInputConfig.getBGcolor()+"; color: #424242;   ";
    	 
         
         
         
    	StringBuilder HtmlDateBox= new StringBuilder(""
    			+ "<input type='text' id='"+colNameToSave+"' size='"+size+"' name='"+colNameToSave+"' class='form-control js-datetimepicker' "+Readonly+" "
    					+ " style='"+style+"' "+req+" value='"+DisplayValue+"' />"
    					+ ""); 	
    	HtmlDateBox.append("<script> $('#"+colNameToSave+"').datetimepicker({" + 
    			" icons:" 
    		     +"   {"
    		     +"       next: 'fa fa-angle-left',"
    		     +"      previous: 'fa fa-angle-right'"
    		     +"   },"
    			+ "format: 'YYYY-MM-DD',"+
    			"  ignoreReadonly: true," + 
    			"  allowInputToggle: true");
    	if (htmlInputConfig.getUserDefinedMinDateSelect() !=null && htmlInputConfig.getUserDefinedMinDateSelect().get(colName) !=null && htmlInputConfig.getUserDefinedMinDateSelect().get(colName).length()>0) {
    		HtmlDateBox.append(", minDate: "+htmlInputConfig.getUserDefinedMinDateSelect().get(colName));
    	}
    	if (htmlInputConfig.getUserDefinedMaxDateSelect() !=null && htmlInputConfig.getUserDefinedMaxDateSelect().get(colName) !=null && htmlInputConfig.getUserDefinedMaxDateSelect().get(colName).length()>0) {
    		HtmlDateBox.append(", maxDate: "+htmlInputConfig.getUserDefinedMaxDateSelect().get(colName));
    	}
    	HtmlDateBox.append("});"
    			+ ""
    			+ "$('#"+colNameToSave+"').on('keydown paste', function(e){" + 
    			"        e.preventDefault();" + 
    			"    });"
    			+ "</script>"); // to behave like readonly
    	
    	return HtmlDateBox;
	}


}
