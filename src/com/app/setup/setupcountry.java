package com.app.setup;

import com.app.core.CoreMgr;

public class setupcountry extends CoreMgr {

	public setupcountry (){
			
		MainSql =" select * From kbcountry ";
		
		userDefinedNewCols.add("country_name");	
		
		userDefinedEditCols.add("city");
		userDefinedColsMustFill.add("country_name");			
		
		
		userDefinedGridCols.add("country_name");
		userDefinedGridCols.add("id_country");
		
		userDefinedColLabel.put("id_country", "رقم");
		userDefinedColLabel.put("country_name", "المدينة");
		
		keyCol = "id_country";
		mainTable ="kbcountry";
		
		search_paramval = null;
		canNew =true;
		canEdit = false;
		canDelete = true;
		userDefinedCaption = "أعدادات المدن";
	}
}
