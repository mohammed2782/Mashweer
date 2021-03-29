
package com.app.cases;

import java.util.HashMap;

import com.app.core.CoreMgr;

public class HistoryRtnSender extends CoreMgr {
	public HistoryRtnSender() {
		MainSql = "select  date(q_enterdate) as rtndate, count(*) as cases,c_company_sender, '' as printbtn "
				+ " from p_queue " + 
				"join p_cases on (c_id = q_caseid)  " + 
				"where q_stage = 'cncl' and q_step = 'delv_back_to_shipper' and q_status ='END' and c_company_sender is not null "
				+ " group by date(q_enterdate), c_company_sender order by q_enterdate desc";
		
		userDefinedColLabel.put("rtndate", "تاريخ الإرجاع");
		userDefinedColLabel.put("cases" , "عدد الشحنات");
		userDefinedColLabel.put("c_company_sender" , "الشركة المرسلة");
		
		userDefinedColLabel.put("printbtn" , "طباعة المنفيست للراجع");
		
		
		userDefinedGridCols.add("c_company_sender");
		userDefinedGridCols.add("rtndate");
		userDefinedGridCols.add("cases");
		userDefinedGridCols.add("printbtn");
		
		userDefinedFilterCols.add("c_company_sender");
		
		UserDefinedPageRows = 100;
		
		userModifyTD.put("printbtn", "displayPrintButton({rtndate},{c_company_sender})");
		
		userDefinedCaption = "قوائم المرتجعات";
		
		canFilter = true;
		userDefinedLookups.put("c_company_sender","select comp_id , comp_name from kbcompanies");
	}
	
	public String displayPrintButton(HashMap<String, String> hashy) {
		String btn = "<a href=\"../TLKPrintReturnedItmesPerDateSenderCompanySRVL?c_company_sender="+hashy.get("c_company_sender")+"&rtndate="+hashy.get("rtndate")+"\" "
				+ " class='btn btn-xs btn-danger' >طباعة المنفيست للراجع<i class=\"fa fa-print fa-lg\"></i></a>";
		return "<td>" + btn + "</td>";
	}


}
