package com.app.bussframework;

import java.util.HashMap;

import com.app.core.CoreMgr;

public class RedundantCases extends CoreMgr{
	public RedundantCases () {
		MainSql = " select  c_custid , c_custreceiptnoori , c_rcv_state , count(*) as totcases "
				+ " from p_cases  where c_createddt>=date_add(DATE(NOW()),INTERVAL -2 DAY) "
				+ " and c_receiptfromsystem='N' "
				+ " group by  c_custid , c_custreceiptnoori , c_rcv_state  having count(*)>1";
		userDefinedColLabel.put("c_custid","صاحب المحل");
		
		userDefinedColLabel.put("c_custreceiptnoori","رقم الوصل");
		userDefinedColLabel.put("c_rcv_state","محافظه");
		
		userDefinedColLabel.put("totcases","عدد");
		
		userDefinedGridCols.add("c_custid");
		
		userDefinedGridCols.add("c_custreceiptnoori");
		userDefinedGridCols.add("c_rcv_state");
		userDefinedGridCols.add("totcases");
		
		userDefinedCaption = "إيصالات متكرره";
		
		userDefinedLookups.put("c_custid", "select c_id, c_name from kbcustomers");
		userDefinedLookups.put("c_rcv_state", "select st_code, st_name_ar from kbstate");
		
		userModifyTD.put("c_custreceiptnoori", "modifyRecieptNo({c_custreceiptnoori})");
		
	}
	public String modifyRecieptNo(HashMap<String, String> hashy) {
		String s = "";
		String style= "";
		s +="<td style='"+style+"'>";
		s +=hashy.get("c_custreceiptnoori");
		s +="</td>";
		return s;	
	}
}
