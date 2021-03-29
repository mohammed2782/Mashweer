package com.app.bussframework;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

/*
 * intial request muse be assigned to driver hence we added the assign to column based on branch
 */
public class SingleQueue__InitialRequest  extends SingleQueue {
	public SingleQueue__InitialRequest() {
		super();
		userDefinedColLabel.put("q_caseid", "رقم الشحنة");
		userDefinedColLabel.put("q_branch", "الفرع");
		userDefinedColLabel.put("c_rcv_name","أسم المستلم");
		userDefinedColLabel.put("attempts","عدد المحاولات السابقة");
		userDefinedGridCols.clear();
		
		userDefinedGridCols.add("q_caseid");
		
		userDefinedGridCols.add("c_name");
		
		
		userDefinedGridCols.add("c_rcv_name");
		userDefinedGridCols.add("q_branch");
		userDefinedGridCols.add("address");
		userDefinedGridCols.add("c_qty");
		userDefinedGridCols.add("q_enterdate");
		userDefinedGridCols.add("q_action");
		
		userDefinedGridCols.add("q_assigned_to");
		userDefinedEditCols.add("q_assigned_to");
		userDefinedEditCols.add("q_branch");
		
		
		userDefinedColsMustFill.add("q_assigned_to");
		
		userDefinedLookups.put("q_assigned_to","!select us_id , us_loginid from kbusers where us_rank_code='AGENT' and us_from_state='{cm_pickup_state}'");
		userDefinedLookups.put("q_branch", "select st_code, st_name_ar from kbstate");
		
		
	}
 
	public String checkNoOfAttempts (HashMap<String,String>hashy) {
		String attempts = "<td>";
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = conn.prepareStatement("select count(*) from p_queue where q_action = 'RETURN_STORE_TRY_AGAIN' and q_caseid =?");
			pst.setString(1, hashy.get("c_id"));
			rs = pst.executeQuery();
			if (rs.next()) {
				if (rs.getInt(1)>0)
					attempts +=rs.getInt(1);
			}
				
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {rs.close();}catch(Exception e) {/**/}
		}
		attempts +="</td>";
		return attempts;
	}
	
	@Override
	public String doUpdate (HttpServletRequest rqs , boolean commit) {
		PreparedStatement pst = null , pstNotATT=null;
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
		HashMap <Integer,String> branchsMap = new HashMap <Integer,String>();
		String action = "";
		int id = 0;
		for (int i=1 ; i<=rowsNo ; i++){
			action = inputMap_ori.get("q_action_smartyrow_"+i)[0];
			if (action!=null && !action.trim().isEmpty() && !action.trim().equalsIgnoreCase("")
					&& !action.trim().equalsIgnoreCase("null")) {
				id =Integer.parseInt(inputMap_ori.get(hiddenKeyCol+"_smartyrow_"+i)[0]);
				qIdList.add(id);
				actionsMap.put(id , action);
				if (action.equalsIgnoreCase("MOV_NEXT_STORE"))
					branchsMap.put(id, inputMap_ori.get("q_branch_smartyrow_"+i)[0]);
			}
		}// in case return to customer we should check the branch the customer sent from
		try{
			pst = conn.prepareStatement("update p_queue set q_action=?, q_action_takenby=? where q_id=?");
			HashMap <String,String> colToEdit;
			for (int qid :qIdList){
				//update the action is take by who
				pst.setString(1, actionsMap.get(qid));
				pst.setString(2, userid);
				pst.setInt(3, qid);
				pst.executeUpdate();
				pst.clearParameters();
				if (branchsMap.get(qid) != null) {
					colToEdit = new HashMap <String,String>();
					colToEdit.put("q_branch", branchsMap.get(qid));
					fu.MoveDecisionStepNext(conn, qid,colToEdit );
				}else {
					fu.MoveDecisionStepNext(conn, qid);
				}
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