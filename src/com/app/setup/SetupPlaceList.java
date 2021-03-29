package com.app.setup;

import com.app.core.CoreMgr;

public class SetupPlaceList extends CoreMgr{
	public SetupPlaceList(){
	
		System.out.println("you inside Setup place constructor");		
		MainSql ="select * from kbplace";

		userDefinedCaption = "إعدادت المحلة";
		newCaption = "إضافة بيانات المحلة";
		updCaption = "تعديل بيانات المحلة";
		
		keyCol = "p_id";
		mainTable ="kbplace";
		
		search_paramval = null;
		canNew = true;
		canFilter = true;
		canEdit = true;
		canDelete = true;
		clickableRow = false;
		
		userDefinedGridCols.add("p_id");
		userDefinedGridCols.add("p_name");
		
		userDefinedColLabel.put("p_id", "رقم المحلة");
		userDefinedColLabel.put("p_name", "إسم المحلة");

		userDefinedNewCols.add("p_name");

		userDefinedFilterCols.add("p_name");
		userDefinedFilterLookups.put("p_name", "select p_name , p_name from kbplace");

		userDefinedColsMustFill.add("p_name");

		userDefinedEditCols.add("p_name");
 }	
}


