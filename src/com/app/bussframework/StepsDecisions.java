package com.app.bussframework;

import com.app.core.CoreMgr;

public class StepsDecisions extends CoreMgr{
	public StepsDecisions() {
		MainSql = "select * from kbstep_decision where stpd_stpid='{stp_id}' and stpd_deleted='N'";
		userDefinedGridCols.add("stpd_desc");
		userDefinedGridCols.add("stpd_code");
		userDefinedGridCols.add("stpd_gotostep1");
		userDefinedGridCols.add("stpd_createddt"); 
		userDefinedGridCols.add("stpd_createdby");
		userDefinedGridCols.add("stpd_onlymbapp");
		
		userDefinedColLabel.put("stpd_desc", "Decision");
		userDefinedColLabel.put("stpd_code", "Code");
		userDefinedColLabel.put("stpd_createddt", "Created Date");
		userDefinedColLabel.put("stpd_createdby", "Created By");
		userDefinedColLabel.put("stpd_gotostep1", "go to step");
		userDefinedColLabel.put("stpd_onlymbapp", "only for mbapp");
		
		userDefinedNewCols.add("stpd_stpid");
		userDefinedNewCols.add("stpd_desc");
		userDefinedGridCols.add("stpd_code");
		userDefinedNewCols.add("stpd_gotostep1");
		userDefinedNewCols.add("stpd_onlymbapp");
		
		userDefinedColsMustFill.add("stpd_desc");
		userDefinedColsMustFill.add("stpd_code");
		userDefinedColsMustFill.add("stpd_gotostep1");
		
		userDefinedReadOnlyNewCols.add("stpd_stpid");
		userDefinedNewColsDefualtValues.put("stpd_stpid",new String[] {"{stp_id}"});
		
		userDefinedLookups.put("stpd_gotostep1", "select stp_id, stp_name from kbstep");
		userDefinedLookups.put("stpd_onlymbapp", "select kbcode, kbdesc from kbgeneral where kbcat1='YESNO'");
		userDefinedNewColsDefualtValues.put("stpd_onlymbapp", new String[] {"N"});
		
		userDefinedEditCols.add("stpd_desc");
		userDefinedEditCols.add("stpd_code");
		userDefinedEditCols.add("stpd_gotostep1");
		userDefinedEditCols.add("stpd_onlymbapp");
		
		canNew = true;
		canDelete = true;
		canEdit = true;
		
		mainTable = "kbstep_decision";
		keyCol = "stpd_id";
		
	}
}
