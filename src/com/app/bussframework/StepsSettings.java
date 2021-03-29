package com.app.bussframework;

import java.util.HashMap;

import com.app.core.CoreMgr;

public class StepsSettings extends CoreMgr{
	public StepsSettings() {
		MainSql = "select kbstep.*, '' as actions from kbstep where stp_stgcode= '{stg_code}'";
		canNew = true;
		canEdit = true;
		canDelete = true;
		
		mainTable = "kbstep";
		keyCol = "stp_id";
		
		userDefinedGridCols.add("stp_code");
		userDefinedGridCols.add("stp_name");
		userDefinedGridCols.add("stp_order");
		userDefinedGridCols.add("stp_rank");
		userDefinedGridCols.add("stp_type");
		userDefinedGridCols.add("actions");
		
		userDefinedNewCols.add("stp_stgcode");
		userDefinedNewCols.add("stp_code");
		userDefinedNewCols.add("stp_name");
		userDefinedNewCols.add("stp_order");
		userDefinedNewCols.add("stp_rank");
		userDefinedNewCols.add("stp_type");
		
		userDefinedEditCols.add("stp_name");
		userDefinedEditCols.add("stp_order");
		userDefinedEditCols.add("stp_rank");
		userDefinedEditCols.add("stp_type");
		
		userDefinedColLabel.put("stp_stgcode", "Stage code");
		userDefinedColLabel.put("stp_code", "Step code");
		userDefinedColLabel.put("stp_name", "Step name");
		userDefinedColLabel.put("stp_order", "order");
		userDefinedColLabel.put("stp_rank", "which rank can access");
		userDefinedColLabel.put("stp_type", "Type");
		
		userDefinedLookups.put("stp_type", "select kbcode , kbdesc from kbgeneral where kbcat1='STEPTYPE'");
		userDefinedNewColsHtmlType.put("stp_rank", "CHECKBOX");
		userDefinedLookups.put("stp_rank", "select rank_code, rank_name_ar from kbrank");
		userDefinedEditColsHtmlType.put("stp_rank", "CHECKBOX");
		userDefinedReadOnlyNewCols.add("stp_stgcode");
		userDefinedNewColsDefualtValues.put("stp_stgcode", new String [] {"{stg_code}"});
		
		userModifyTD.put("actions", "getDecisions({stp_id}");
	}
	public String getDecisions(HashMap<String,String> hashy) {
		String HTMLButton= "";
		String btnClass ="btn btn-sm btn-danger";
		String btnText = "Show Decisions";
		
		String url ="stepActions.jsp?stp_id="+hashy.get("stp_id");
		HTMLButton+="<td align='center'><button type='button' class='"+btnClass+"' "
				+ " onclick=\"popitup ('"+url+"' , 'Student Payments' , 700 ,600);\" >"+btnText+"</button></td>";
		
		return HTMLButton;
		
	}
}
