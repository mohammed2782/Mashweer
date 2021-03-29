package com.app.bussframework;
import java.util.HashMap;

import com.app.core.*;
public class CasesInQueue extends CoreMgr {
	public CasesInQueue() {
		MainSql = "SELECT concat(concat(stg_name,' - '), stp_name) as que , stg_code, stp_code , stg_name , stp_name, count(*) as cnt "
				+ " FROM p_queue join p_cases on c_id = q_caseid " 
				+ " join kbstage on q_stage = stg_code " + 
				  " join kbstep on q_step = stp_code and q_stage = stp_stgcode "
				+ " where "
				+ " stp_type='DECISION' and ((stp_rank like '%{userRank}%' and c_branchcode='{userstorecode}')  or'{superItRank}'='Y' or '{superRank}'='Y')"
				+ " and q_status !='CLS'"
				+ " and ( (c_settled !='FULL' and q_step !='return_to_cust' ) or (q_step ='return_to_cust') or (c_paidinadvance='YES' and  c_settled ='FULL'))" + 
				"group by stg_name, stp_name ";
		
		userDefinedGridCols.add("que");
		userDefinedGridCols.add("cnt");
		userDefinedCaption = "طلبات شحن";
		userDefinedColLabel.put("que", "الحالة الحالية");
		userDefinedColLabel.put("cnt", "عدد الطلبات");
		userDefinedColLabel.put("q_caseid", "رقم الشحنه");
		userDefinedColLabel.put("q_enterdate", "تاريخ ووقت الحاله");
		
		userModifyTD.put("cnt", "clickableQueue({cnt},{stg_code},{stp_code})");
	}
	public String clickableQueue(HashMap <String,String> hashy) {
		String HTMLButton = "";
		String btnClass ="btn btn-sm btn-warning";
		String btnText = hashy.get("cnt");
		String url ="casesinqueue.jsp?stg_code="+hashy.get("stg_code")+"&stp_code="+hashy.get("stp_code");
		HTMLButton+="<td align='center'><a class='"+btnClass+"' "
				+ " href='"+url+"'>"+btnText+"</a></td>";
		
		return HTMLButton;
		
		
	}
}
