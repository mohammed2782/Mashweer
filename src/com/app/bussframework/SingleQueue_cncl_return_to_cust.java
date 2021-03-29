package com.app.bussframework;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.app.db.mysql;
 
public class SingleQueue_cncl_return_to_cust extends SingleQueue{
	int i =1;
	Connection connAgentList = null;
	PreparedStatement pstAgentList = null;
	ResultSet rsAgentList = null;
	HashMap<String,String> agentsList;
	public SingleQueue_cncl_return_to_cust () {
		userModifyTD.put("q_action", "modifyAction()");
		agentsList = new HashMap<String,String>();
		try {
			connAgentList = mysql.getConn();
			pstAgentList = connAgentList.prepareStatement("select us_id , us_name from kbusers where us_rank = 'DLVAGENT' ");
			rsAgentList = pstAgentList.executeQuery();
			while (rsAgentList.next()) {
				agentsList.put(rsAgentList.getString("us_id"), rsAgentList.getString("us_name"));
			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {rsAgentList.close();}catch(Exception e) {}
			try {pstAgentList.close();}catch(Exception e) {}
			try {connAgentList.close();}catch(Exception e) {}
		}
	}
	public void initialize(HashMap smartyStateMap){
		userDefinedCaption= "<div class='col-md-6 col-sm-12 col-xs-12'>"+this.userDefinedCaption+"</div>"
				+ " <div class=\"checkbox checkbox-success col-md-3\">" + 
				"                        <input id=\"allreturned\" class=\"\" onclick=\"changeActionReturnedAll('all_returned');\" type=\"checkbox\">" + 
				"                        <label for=\"allreturned\">" + 
				"                            تم الإرجاع إلى العميل" + 
				"                        </label>" + 
				"                    </div>"
				+ " <div class=\"checkbox checkbox-success col-md-3\"style=\"margin-top: 10px;\">" + 
				"                        <input id=\"allretorcag\" class=\"\" onclick=\"changeActionReturnedAllToRcAg('all_retorcag');\" type=\"checkbox\">" + 
				"                        <label for=\"allretorcag\">" + 
				"                           تم تسليم لمندوب الاستلام" + 
				"                        </label>" + 
				"                    </div>";
		String customerPrintRtnButton = " concat(c_name, '<a href=\"../TLKPrintReturnedItmesSRVL?branchCode=',c_branchcode,'&cust_id=',c_custid,'\" style=\"padding-right:20px;\" >"
				+ " <input type=\"button\" value=\" طباعة مانفيست المرتجعات \"   class=\"btn btn-danger btn-sm\" ></a>') as custrtn ";
		 
		MainSql  = "select c_rtnreason, c_receiptamt, date(c_createddt) as c_createddt, c_assignedagent, c_custreceiptnoori, c_rcv_district, c_rcv_name , c_qty,q_branch,'' as attempts, p_cases.c_id, c_name,  "
				+ " concat(st_name_ar,' - ' ,ifnull(c_rcv_addr_rmk,'')) as address ,c_rcv_state ,"
				+ " q_id, q_caseid, q_enterdate , q_stage, q_step , stp_id , q_action,"
				+ " q_assigned_to , c_rmk,c_agentsharesettled , c_agentpmtid, "+customerPrintRtnButton
				+ " from p_queue "
				+ " join p_cases on (c_id = q_caseid)"
				+ " join kbstep on (q_stage=stp_stgcode and q_step=stp_code) "
				+ " join kbstate on (c_rcv_state = st_code)  "
				+ " join kbcustomers on (c_custid = kbcustomers.c_id) "
				+ " where q_stage= '{stg_code}' and q_step='{stp_code}' and q_status !='CLS'"
				+ " and (c_branchcode='{userstorecode}' or '{superRank}'='Y') ";
		
		userDefinedGroupByCol = "custrtn";
		userDefinedGroupColsOrderBy = "custrtn, c_id";
		
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
		userDefinedGridCols.add("c_rtnreason");
		super.initialize (smartyStateMap);
	}
	
	
	public String modifyAction (HashMap<String,String> hashy) {
		StringBuilder sb = new StringBuilder("<td><table><tr><td>");
		sb.append("<select class='form-control' onChange='changeDropListRtn(this,"+i+")' id='q_action_smartyrow_"+i+"' name='q_action_smartyrow_"+i+"' "
				+ "style='text-align:right; background-color:#F0FFF0; padding: 0 10px 0 10px;  color: #424242; border: 1px solid #7dc6dd;'> "
		+"<option value=''></option>");
		Map <String , String> lookupsmap = colMapValues.get("q_action");
		String selectedItem="";
		if (lookupsmap !=null){
			if (!lookupsmap.isEmpty()){
				for (String code : lookupsmap.keySet()){
					selectedItem = "";
					
					sb.append("<option value='"+code+"' "+selectedItem+">"
					+lookupsmap.get(code)+"</option> \n");
				}
			}
		}
		sb.append("</select></td></tr>");
		sb.append("<tr id='agentlist_"+i+"' style='display:none;padding-top:5px'><td>");
		sb.append("<select class='form-control' id='dlv_agent_smartyrow_"+i+"' name='dlv_agent_smartyrow_"+i+"' " 
				+ "style='text-align:right; background-color:#F0FFF0; padding: 0 10px 0 10px;  color: #424242; border: 1px solid #7dc6dd;'> "
				+"<option value=''></option>");
		for (String agentId : agentsList.keySet()) {
			sb.append("<option value='"+agentId+"' >"+agentsList.get(agentId)+"</option> \n");
		}
		sb.append("</td>");
		sb.append("</tr></table></td>");
		i++;
		return sb.toString();
	}
	
	public String doUpdate (HttpServletRequest rqs , boolean commit) {
		PreparedStatement pst = null, pstCancelPay = null , pst2 = null , pstCancelPayForResend = null ;
		String userid = replaceVarsinString("{useridlogin}", arrayGlobals).trim();
		boolean moveStep = true;
		keyVal = parseUpdateRqs(rqs);
		FlowUtils fu = new FlowUtils();
		int rowsNo =0;
		if (inputMap_ori.get("smartyhiddenmultieditrowsno")!=null)
			rowsNo = Integer.parseInt(inputMap_ori.get("smartyhiddenmultieditrowsno")[0]);
		
		ArrayList<Integer> qIdList= new ArrayList<Integer>();
		HashMap <Integer , String> actionsMap = new HashMap<Integer, String>();
		HashMap <Integer, String> agentResendMap = new HashMap<Integer, String>();
		String action = "";
		int id = 0;
		for (int i=1 ; i<=rowsNo ; i++){
			action = inputMap_ori.get("q_action_smartyrow_"+i)[0];
			if (action!=null && !action.trim().isEmpty() && !action.trim().equalsIgnoreCase("")
					&& !action.trim().equalsIgnoreCase("null")) {
				id =Integer.parseInt(inputMap_ori.get(hiddenKeyCol+"_smartyrow_"+i)[0]);
				if (action.equalsIgnoreCase("RESEND")) {
					if (inputMap_ori.get("dlv_agent_smartyrow_"+i)[0] !=null
							&& inputMap_ori.get("dlv_agent_smartyrow_"+i)[0].length()>0)
						agentResendMap.put(id, inputMap_ori.get("dlv_agent_smartyrow_"+i)[0]);
				}
				actionsMap.put(id , action);
				qIdList.add(id);
			}
		}
		try{
			pst = conn.prepareStatement("update p_queue set q_action=?, q_action_takenby=? where q_id=?");
			pstCancelPay = conn.prepareStatement("update p_cases set c_shipmentpaidbycustomer='Y' , c_shipmentpaidbysender='N',c_agentpmtid=0,c_agentsharesettled='NO',  c_pmtid=0 , c_settled='NO',c_rtnreason = null where c_id = (select q_caseid from p_queue where q_id=?)");
			pstCancelPayForResend = conn.prepareStatement("update p_cases "
					+ "set c_assignedagent=?, c_shipmentpaidbycustomer='N' , c_shipmentpaidbysender='N',c_agentpmtid=0 , c_agentsharesettled='NO', c_rtnreason = null"
					+ " where c_id = (select q_caseid from p_queue where q_id=?)");
			pst2 = conn.prepareStatement("update p_cases set c_shipmentpaidbycustomer='N' , c_shipmentpaidbysender='N', c_agentpmtid=0,c_agentsharesettled='NO' where c_id in (select q_caseid from p_queue where q_id =? and q_status !='CLS')");
			for (int qid :qIdList){
				moveStep = true;
				// if the action is move to recieved successfully then remove the payment
				if (actionsMap.get(qid).equalsIgnoreCase("ERR_SENDDLV_SUCC")) {
					pstCancelPay.setInt(1, qid);
					pstCancelPay.executeUpdate();
					pstCancelPay.clearParameters();
				}else if (actionsMap.get(qid).equalsIgnoreCase("RTN_TO_AGENT")) {
					pst2.setInt(1, qid);
					pst2.executeUpdate();
					pst2.clearParameters();
				}else if (actionsMap.get(qid).equalsIgnoreCase("RESEND") ) {
					moveStep = false;
					if (agentResendMap !=null && agentResendMap.get(qid)!=null && agentResendMap.get(qid).length()>0) {
						pstCancelPayForResend.setString(1, agentResendMap.get(qid));
						pstCancelPayForResend.setInt(2, qid);
						pstCancelPayForResend.executeUpdate();
						pstCancelPayForResend.clearParameters();
						moveStep = true;
					}
				}
				//update the action is take by who
				pst.setString(1, actionsMap.get(qid));
				pst.setString(2, userid);
				pst.setInt(3, qid);
				pst.executeUpdate();
				pst.clearParameters();
				
				if (moveStep)
					fu.MoveDecisionStepNext(conn, qid);
			}
			conn.commit();
		}catch(Exception e){
			e.printStackTrace();
			try{conn.rollback();}catch(Exception eRoll){}
			return "Error";
		}finally{
			try{pst.close();}catch(Exception e){}
			try{pstCancelPay.close();}catch(Exception e){}
			try{pst2.close();}catch(Exception e){}
			try{pstCancelPayForResend.close();}catch(Exception e) {}
		}
				
		return "Saved";
	}
}
