package com.app.cases;

import java.sql.Connection;
import java.util.LinkedHashMap;

import com.app.core.CoreMgr;

public class AuditTrailPopUp  extends CoreMgr{
	
	public AuditTrailPopUp () {
		MainSql = "select  concat(concat(stg_name,' - '), stp_name) as que , (q_enterdate+INTERVAL 9 HOUR) as q_enterdate, q_action_takenby, q_action "
				+ "  from p_queue_hist "
				+" join kbstage on q_stage = stg_code  join kbstep on q_step = stp_code and q_stage = stp_stgcode "
				+ "where q_caseid = {auditcaseid} order by q_id";
		userDefinedGridCols.add("q_enterdate");
		userDefinedGridCols.add("que");
		userDefinedGridCols.add("q_action");
		userDefinedGridCols.add("q_action_takenby");
		
		userDefinedColLabel.put("q_enterdate", "تاريخ و وقت الدخول لهذه المرحله");
		userDefinedColLabel.put("que", "المرحله");
		userDefinedColLabel.put("q_action", "العملية");
		userDefinedColLabel.put("q_action_takenby", "المستخدم الذي قام بالعملية");
		userDefinedColsTypes.put("q_enterdate", "TIMESTAMP");
		
		userDefinedLookups.put("q_action", "select stpd_code, stpd_desc from kbstep_decision");
		
		userDefinedCaption = "تسلسل الأحداث";
	}
}
