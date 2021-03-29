package com.app.bussframework;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.app.db.mysql;
import com.app.util.Utilities;
 
public class SingleQueue_Init_InStorage extends SingleQueue {
	public SingleQueue_Init_InStorage() {
		super();
		userDefinedGridCols.clear();
		
		userDefinedGridCols.add("c_company_sender");
		userDefinedGridCols.add("c_receiptamt");
		userDefinedGridCols.add("c_custreceiptnoori");
		userDefinedGridCols.add("c_rcv_hp");
		userDefinedColLabel.put("q_caseid", "رقم الشحنة");
		userDefinedColLabel.put("q_branch", "الفرع");
		userDefinedColLabel.put("c_rcv_name","أسم المستلم");
		//userDefinedGridCols.add("q_caseid");
		userDefinedGridCols.add("c_name");
		userDefinedGridCols.add("c_rcv_name");
		userDefinedGridCols.add("address");
		userDefinedGridCols.add("c_qty");
		userDefinedGridCols.add("c_rmk");
		userDefinedGridCols.add("q_action");
		userDefinedGridCols.add("c_rural");
		
		userDefinedEditColsDefualtValues.put("q_action", new String[] {"ASSGN_AGENT"});
		userDefinedGridCols.add("c_assignedagent");
		userDefinedGridCols.add("c_createddt");
		
		
		userDefinedFilterLookups.put("c_assignedagent", "select us_id , us_name from kbusers where us_rank = 'DLVAGENT' ");
		
		//smarty have problem with the hotlookup when we have single quote ' , it makes an issue with the javascript
		userDefinedLookups.put("c_assignedagent", "!select us_id , us_name from kbusers where us_id =\'{c_assignedagent}\' union "
				+ " select us_id , us_name from kbusers where us_id in " + 
				"	(select us_id   from kbusers where us_rank = \'DLVAGENT\' and us_to_state like \'%{c_rcv_state}%\'" + 
				"    and  us_id in (select agdi_usid from kbagent_district 	join kbcity_district on (cdi_stcode like \'%{c_rcv_state}%\' and agdi_districtcode = cdi_code)" + 
					"    where agdi_districtcode=\'{c_rcv_district}\')" + 
				"   union " +
				"	select us_id from kbusers where us_rank = \'DLVAGENT\'  and us_to_state like \'%{c_rcv_state}%\' and us_to_state not like \'%BAS%\'" + 
				"    ) and us_active=\'Y\'");
		userDefinedLookups.put("c_rural", "select kbcode, kbdesc from kbgeneral where kbcat1 = 'YESNO'");
		
		
		
		userDefinedEditCols.add("c_assignedagent");
		userDefinedEditCols.add("c_rural");
	
		userDefinedColLabel.put("c_assignedagent", "مندوب التوصيل");
		userDefinedLookups.put("q_branch", "select st_code, st_name_ar from kbstate");
		
		userDefinedFilterCols.add("c_rcv_hp");
		//userDefinedFilterCols.add("c_assignedagent");
		//userModifyTD.put("q_assigned_to", "modifyAgentList({c_assignedagent})");
	}
	
	
	@Override
	public void initialize(HashMap smartyStateMap){
		super.initialize(smartyStateMap);
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		StringBuilder DropDownHtml= new StringBuilder("");
		try {
			conn = mysql.getConn();
			pst = conn.prepareStatement("select us_id , us_name from kbusers where us_rank = 'DLVAGENT'");
			rs = pst.executeQuery();
			DropDownHtml.append("<select id='globalagentselect' onchange='doGlobalSelectForAgents()'>");
			DropDownHtml.append("<option value=''></option> \n");
			while (rs.next()) {
				DropDownHtml.append("<option value='"+rs.getString("us_id")+"'>"+rs.getString("us_name")+"</option> \n");
			}
			DropDownHtml.append("</select> \n");
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
			pst = conn.prepareStatement("select kbcode, kbdesc from kbgeneral where kbcat1 = 'YESNO'");
			rs = pst.executeQuery();
			DropDownHtml.append("<select style='margin-right:25px;' id='globalruralselect' onchange='doGlobalSelectForRural()'>");
			DropDownHtml.append("<option value=''></option> \n");
			while (rs.next()) {
				DropDownHtml.append("<option value='"+rs.getString("kbcode")+"'>"+rs.getString("kbdesc")+"</option> \n");
			}
			DropDownHtml.append("</select> \n");
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
			try {conn.close();}catch(Exception e) {/*ignore*/}
		}
		
		userDefinedCaption= "<div class='row'><div class='col-md-8 col-sm-12 col-xs-12'>"+this.userDefinedCaption+"</div>"
					+ " <div class='col-md-4 col-sm-12 col-xs-12'>أختيار مندوب للكل &nbsp&nbsp&nbsp&nbsp&nbsp&nbspأختيار اطراف للكل"+DropDownHtml+" </div></div>";

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
		PreparedStatement pst = null , pstUpdateCase=null;
		ResultSet rs = null;
		//System.out.println("inside the initinstorage");
		String userid = replaceVarsinString("{useridlogin}", arrayGlobals).trim();
		keyVal = parseUpdateRqs(rqs);
		FlowUtils fu = new FlowUtils();
		Utilities ut = new Utilities();
		int rowsNo =0;
		if (inputMap_ori.get("smartyhiddenmultieditrowsno")!=null)
			rowsNo = Integer.parseInt(inputMap_ori.get("smartyhiddenmultieditrowsno")[0]);
		
		ArrayList<Integer> qIdList= new ArrayList<Integer>();
		ArrayList<Integer> qIdListRural= new ArrayList<Integer>();
		HashMap <Integer , String> actionsMap = new HashMap<Integer, String>();
		HashMap <Integer,String> agentsMap = new HashMap <Integer,String>();
		HashMap <Integer,String> ruralMap = new HashMap <Integer,String>();
		String action = "";
		int id = 0;
		for (int i=1 ; i<=rowsNo ; i++){
			action = inputMap_ori.get("q_action_smartyrow_"+i)[0];
			if (action!=null && !action.trim().isEmpty() && !action.trim().equalsIgnoreCase("")
					&& !action.trim().equalsIgnoreCase("null")) {
				id =Integer.parseInt(inputMap_ori.get(hiddenKeyCol+"_smartyrow_"+i)[0]);
				agentsMap.put(id, inputMap_ori.get("c_assignedagent_smartyrow_"+i)[0]);
				
				if (action.equalsIgnoreCase("ASSGN_AGENT")) {
					
					if (inputMap_ori.get("c_assignedagent_smartyrow_"+i) !=null 
							&& inputMap_ori.get("c_assignedagent_smartyrow_"+i)[0] !=null 
								&& !inputMap_ori.get("c_assignedagent_smartyrow_"+i)[0].trim().equalsIgnoreCase("")) {
						//System.out.println("inside inputMap_ori.get(\"q_assigned_to_smartyrow_\"+i)[0]=====>"+inputMap_ori.get("q_assigned_to_smartyrow_"+i)[0]);
						actionsMap.put(id , action);
						qIdList.add(id);
						
					}
				}
				if (inputMap_ori.get("c_rural_smartyrow_"+i)[0].trim().equalsIgnoreCase("Y")
						|| inputMap_ori.get("c_rural_smartyrow_"+i)[0].trim().equalsIgnoreCase("N")) {
					ruralMap.put(id, inputMap_ori.get("c_rural_smartyrow_"+i)[0]);
					qIdListRural.add(id);
					
				}
			}
		}
		
		try{
			pst = conn.prepareStatement("update p_queue set q_action=?, q_action_takenby=? where q_id=?");
			pstUpdateCase = conn.prepareStatement("update p_cases set c_assignedagent=?, c_rural=? where c_id=(select q_caseid from p_queue where q_id=?)");
			HashMap <String,String> colToEdit;
			for (int qid :qIdListRural){
				if (ruralMap.get(qid)!=null) {
					pstUpdateCase.setString(1,agentsMap.get(qid) );
					pstUpdateCase.setString(2,ruralMap.get(qid) );
					pstUpdateCase.setInt(3, qid);
					pstUpdateCase.executeUpdate();
					pstUpdateCase.clearParameters();
				}else {
					throw new Exception("In SingleQueue_Init_InStorage haven't rural value please contact softica.");
				}
				
			}
			for (int qid :qIdList){
				//update the action is take by who
				pst.setString(1, actionsMap.get(qid));
				pst.setString(2, userid);
				pst.setInt(3, qid);
				pst.executeUpdate();
				pst.clearParameters();
				if (agentsMap.get(qid) != null) {
					//System.out.println("qid==>"+qid);
					//System.out.println("agentid==>"+agentsMap.get(qid));
					colToEdit = new HashMap <String,String>();
					colToEdit.put("q_assigned_to", agentsMap.get(qid));
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
			try{pstUpdateCase.close();}catch(Exception e){}
			try{rs.close();}catch(Exception e){}
			
		}
				
		return "Saved";
	}
}