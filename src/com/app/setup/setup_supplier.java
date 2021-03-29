package com.app.setup;

import com.app.core.CoreMgr;
public class setup_supplier extends CoreMgr {
	
	public setup_supplier(){
		MainSql=" select * from kbsupplier order by supp_name asc";
		
		canNew 	  = true;
		canDelete = true;
		canEdit   = true;
		
		userDefinedNewCols.add("supp_name");
		userDefinedNewCols.add("supp_country");
		mainTable ="kbsupplier";
		keyCol = "supp_id";
		userDefinedLookups.put("supp_country", "select id_country , country_name from kbcountry order by country_name asc" );
		userDefinedNewColsHtmlType.put("supp_country" , "DROPLIST");
		userDefinedColLabel.put("supp_id", "رقم المجهز");
		userDefinedColLabel.put("supp_name", "أسم المجهز");
		userDefinedColLabel.put("supp_country", "الدولة");
		userDefinedCaption = "أعدادات المجهزين";
	}
}
