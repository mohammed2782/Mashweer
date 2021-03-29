package com.app.setup;

import com.app.core.CoreMgr;
public class KbgeneralSetup extends CoreMgr{
	public KbgeneralSetup(){
		MainSql   = "select * from kbgeneral";
		mainTable = "kbgeneral";
		keyCol    = "kbid";
		canNew    = true;
		canEdit   = true;
		canDelete = true;
		canFilter = true;
		
		userDefinedReadOnlyEditCols.add("kbid");
		
	}
}
