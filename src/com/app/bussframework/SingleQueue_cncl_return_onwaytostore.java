package com.app.bussframework;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

public class SingleQueue_cncl_return_onwaytostore  extends SingleQueue{
	public SingleQueue_cncl_return_onwaytostore() {
		userDefinedGridCols.add("c_company_sender");
		userDefinedGridCols.add("c_assignedagent");
		userDefinedGridCols.add("c_rtnreason");
		userDefinedFilterCols.add("c_assignedagent");
	}
	
	public String doUpdate (HttpServletRequest rqs , boolean commit) {
		PreparedStatement pst = null, pstCancelPay = null ;
		String userid = replaceVarsinString("{useridlogin}", arrayGlobals).trim();
		boolean moveStep = true;
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
			pst = conn.prepareStatement("update p_queue set q_action=?, q_action_takenby=? where q_id=?");
			pstCancelPay = conn.prepareStatement("update p_cases set c_shipmentpaidbycustomer=? , c_shipmentpaidbysender='N',c_agentpmtid=0,c_agentsharesettled='NO',  c_pmtid=0 , c_settled='NO' where c_id = (select q_caseid from p_queue where q_id=?)");
			for (int qid :qIdList){
				// if the action is move to recieved successfully then remove the payment
				if (actionsMap.get(qid).equalsIgnoreCase("ERR_SENDDLV_SUCC")|| actionsMap.get(qid).equalsIgnoreCase("ERR_WITHAGENT")) {
					if(actionsMap.get(qid).equalsIgnoreCase("ERR_WITHAGENT"))
						pstCancelPay.setString(1, "N");
					else if(actionsMap.get(qid).equalsIgnoreCase("ERR_SENDDLV_SUCC"))
							pstCancelPay.setString(1, "Y");
					pstCancelPay.setInt(2, qid);
					pstCancelPay.executeUpdate();
					pstCancelPay.clearParameters();
				}
				//update the action is take by who
				pst.setString(1, actionsMap.get(qid));
				pst.setString(2, userid);
				pst.setInt(3, qid);
				pst.executeUpdate();
				pst.clearParameters();
				
				fu.MoveDecisionStepNext(conn, qid);
			}
			conn.commit();
		}catch(Exception e){
			e.printStackTrace();
			try{conn.rollback();}catch(Exception eRoll){}
			return "Error";
		}finally{
			try{pst.close();}catch(Exception e){}
		}
				
		return "Saved";
	}

}
