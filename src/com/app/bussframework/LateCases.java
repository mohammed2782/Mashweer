package com.app.bussframework;

import java.util.ArrayList;
import java.util.HashMap;

import com.app.core.CoreMgr;


public class LateCases extends CoreMgr {
	public LateCases() {
		
		MainSql = "select  c_assignedagent , count(*) as tot "
				+ " from p_queue "
				+ " join p_cases on (c_id = q_caseid) where q_status='ACTV' " 
				+ " and q_stage = 'dlv_stg' and q_step = 'with_agent'"
				+ " and q_enterdate <= now() - INTERVAL 36 HOUR "
				+ " group by c_assignedagent ";
		
		
		userDefinedGridCols.add("c_assignedagent");
		userDefinedGridCols.add("tot");
		
		
		userDefinedColLabel.put("c_assignedagent", "المندوب");
		userDefinedColLabel.put("tot" , "عدد الشحنات");
		
		userDefinedCaption = "طلبات متأخره أكثر من 36 ساعة عند المندوب";
		
		userDefinedLookups.put("c_assignedagent", "select us_id , us_name from kbusers");
	}
	
}
		/*
		MainSql = "select c_createddt,time_to_sec(TIMEDIFF(sysdate(), c_createddt))/3600 as intime, c_id,'' as dtls, '' as pickuploc ,"
				+ " '' as rcv_loc  , stg_code ,stp_code  from p_queue join kbstage on (q_stage = stg_code) " + 
				" join kbstep on (q_step = stp_code and stp_stgcode = stg_code) " + 
				" join p_cases on (c_id = q_caseid) where q_status='ACTV' " + 
				" and stp_finaldestination != 'Y' " + 
				" and c_createddt < (SELECT sysdate() - INTERVAL kbcode HOUR from kbgeneral where kbcat1='GEN' and kbcat2 = 'DELAYALRT' )"
				+ " and  (c_branchcode='{userstorecode}' or '{superRank}'='Y')"
				+ " and q_stage = 'dlv_stg' and q_step = 'with_agent'  order by c_id asc";
		
		userDefinedGridCols.add("intime");
		userDefinedGridCols.add("dtls");
		userDefinedGridCols.add("c_id");
		
		userDefinedColLabel.put("intime", "منذ");
		userDefinedColLabel.put("dtls" , "عرض التفاصيل");
		userDefinedColLabel.put("c_id" , " ");
		
		userModifyTD.put("intime", "calcTimeDiff({intime})");
		userModifyTD.put("c_id", "clickableCase({c_id},{stg_code}, {stp_code})");
		userModifyTD.put("dtls", "displayCaseDtlsBtn({c_id})");
		
		userDefinedCaption = "طلبات متأخره أكثر من 48 ساعه";
	
	}
	public String displayCaseDtlsBtn (HashMap<String, String> hashy) {
		String HTMLButton="";
		String btnText = "عرض تفاصيل الشحنه";
		String url ="../cases/displayCaseDtls.jsp?c_id="+hashy.get("c_id");
		HTMLButton+="<td align='center'><button type='button' class='btn btn-sm btn-warning' "
				+ " onclick=\"popitup ('"+url+"' , 'ModifyResults' , 1000 ,600);\" >"+btnText+"</button></td>";
		return HTMLButton;
		
	}
	public String clickableCase(HashMap<String, String> hashy) {
		String HTMLButton = "";
		String btnClass ="btn btn-sm btn-danger";
		String btnText = "رقم الشحنه :"+hashy.get("c_id");
		String url ="casesinqueue.jsp?c_id="+hashy.get("c_id")+"&stg_code="+hashy.get("stg_code")+"&stp_code="+hashy.get("stp_code");
		HTMLButton+="<td align='center'><a class='"+btnClass+"' "
				+ " href='"+url+"'>"+btnText+"</a></td>";
	
		return HTMLButton;
	}
	public String  calcTimeDiff (HashMap<String,String> hashy) {
		String msg ="<td>";
		int time = (int) Double.parseDouble(hashy.get("intime"));
		if (time>=72)//week equals to 
			msg +="<img src='../smartyresources/img/emicon/20.gif' style='width:20%'></img>";
		else if (time>=60)
			msg +="<img src='../smartyresources/img/emicon/6279.gif' style='width:20%'></img>";
		else 
			msg +="<img src='../smartyresources/img/emicon/6281.gif' style='width:20%'></img>";
		
		msg += time+" ساعه ";
		msg +="</td>";
		return msg;
	}
}
*/
