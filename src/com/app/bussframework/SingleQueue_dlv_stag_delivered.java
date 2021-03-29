package com.app.bussframework;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

public class SingleQueue_dlv_stag_delivered extends SingleQueue {
	public SingleQueue_dlv_stag_delivered () {
		MainSql  = "select c_receiptamt, date(c_createddt) as c_createddt, c_assignedagent, c_custreceiptnoori, c_rcv_name , c_qty,q_branch,'' as attempts, p_cases.c_id, c_name,  "
				+ " concat(st_name_ar,' - ' ,ifnull(c_rcv_addr_rmk,'')) as address ,c_rcv_state, "
				+ " q_id, q_caseid, q_enterdate , q_stage, q_step , stp_id , q_action,"
				+ " q_assigned_to , c_rmk , c_agentsharesettled," + 
				"c_agentpmtid "
				+ " from p_queue "
				+ " join p_cases on (c_id = q_caseid)"
				+ " join kbstep on (q_stage=stp_stgcode and q_step=stp_code) "
				+ " join kbstate on (c_rcv_state = st_code)  "
				+ " join kbcustomers on (c_custid = kbcustomers.c_id)"
				+ " where q_stage= '{stg_code}' and q_step='{stp_code}' and q_status !='CLS' and c_settled !='FULL' "
				+ " and (c_branchcode='{userstorecode}' or '{superRank}'='Y') ";
		userDefinedFilterCols.add("c_assignedagent");
		
		userDefinedGridCols.clear();
		
		userDefinedGridCols.add("c_name");
		userDefinedGridCols.add("c_custreceiptnoori");
		userDefinedGridCols.add("c_receiptamt");
		userDefinedGridCols.add("address");
		userDefinedGridCols.add("q_caseid");
		userDefinedGridCols.add("c_rcv_name");
		
		userDefinedGridCols.add("c_qty");
		userDefinedGridCols.add("c_createddt");
		userDefinedGridCols.add("c_agentsharesettled");
		userDefinedGridCols.add("c_agentpmtid");
		
		userDefinedGridCols.add("q_action");
		
		
	}
	
	@Override
	public String doUpdate (HttpServletRequest rqs , boolean commit) {
		PreparedStatement pst = null , pst2 = null , pstCheckifSettledAgent=null;
		ResultSet rs = null;
		String userid = replaceVarsinString("{useridlogin}", arrayGlobals).trim();
		//super.doUpdate(rqs,false);
		keyVal = parseUpdateRqs(rqs);
		FlowUtils fu = new FlowUtils();
		int rowsNo =0;
		if (inputMap_ori.get("smartyhiddenmultieditrowsno")!=null)
			rowsNo = Integer.parseInt(inputMap_ori.get("smartyhiddenmultieditrowsno")[0]);
		
		ArrayList<Integer> qIdList= new ArrayList<Integer>();
		HashMap <Integer , String> actionsMap = new HashMap<Integer, String>();
		String action = "";
		int id = 0;
		for (int i=1 ; i<=rowsNo ; i++){
			action = inputMap_ori.get("q_action_smartyrow_"+i)[0];
			if (action!=null && !action.trim().isEmpty() && !action.trim().equalsIgnoreCase("")
					&& !action.trim().equalsIgnoreCase("null")) {
				id =Integer.parseInt(inputMap_ori.get(hiddenKeyCol+"_smartyrow_"+i)[0]);
				actionsMap.put(id , action);
				qIdList.add(id);
				
			}
		}
		try{
			pstCheckifSettledAgent = conn.prepareStatement("select ifnull(c_agentpmtid,0) as c_agentpmtid "
					+ " from p_cases where c_id in (select q_caseid from p_queue where q_id =? and q_status !='CLS') ");
			pst = conn.prepareStatement("update p_queue set q_action=?, q_action_takenby=? where q_id=?");
			pst2 = conn.prepareStatement("update p_cases set c_shipmentpaidbycustomer='N' , c_shipmentpaidbysender='N' where c_id in (select q_caseid from p_queue where q_id =? and q_status !='CLS')");
			int agentPmtId = 0;
			for (int qid :qIdList){
				agentPmtId = 0;
				pstCheckifSettledAgent.setInt(1, qid);
				rs = pstCheckifSettledAgent.executeQuery();
				if (rs.next()) {
					agentPmtId = rs.getInt("c_agentpmtid");
				}
				try {rs.close();}catch(Exception e) {}
				pstCheckifSettledAgent.clearParameters();
				
				if (agentPmtId ==0) {
					//update the action is take by who
					pst.setString(1, actionsMap.get(qid));
					pst.setString(2, userid);
					pst.setInt(3, qid);
					pst.executeUpdate();
					pst.clearParameters();
					
					// Either return to with agent or return to returned in storage, 
					//either way, we must put the flag of shipment paid by customer or sender to N
					pst2.setInt(1,qid);
					pst2.executeUpdate();
					pst2.clearParameters();
				
					
					fu.MoveDecisionStepNext(conn, qid);
				}
				
			}
			conn.commit();
		
		}catch(Exception e){
			e.printStackTrace();
			try{conn.rollback();}catch(Exception eRoll){}
			return "Error";
		}finally{
			try {rs.close();}catch(Exception e) {}
			try {pstCheckifSettledAgent.close();}catch(Exception e) {}
			try{pst.close();}catch(Exception e){}
			try{pst2.close();}catch(Exception e){}
		}
				
		return "Saved";
	}
}
