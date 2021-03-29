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
import com.app.util.Utilities;

public class SingleQueue_dlv_stg_with_agent  extends SingleQueue {
	int i =1;
	Connection connReturnReason = null;
	PreparedStatement pstReturnReasonsList = null;
	ResultSet rsReturnReasonsList = null;
	HashMap<String,String> returnReasonsList;

	
	public SingleQueue_dlv_stg_with_agent(){
		returnReasonsList = new HashMap<String,String>();
		try {
			connReturnReason = mysql.getConn();
			pstReturnReasonsList = connReturnReason.prepareStatement("select rtn_code , rtn_desc from kbrtn_reasons");
			rsReturnReasonsList = pstReturnReasonsList.executeQuery();
			while (rsReturnReasonsList.next()) {
				returnReasonsList.put(rsReturnReasonsList.getString("rtn_code"), rsReturnReasonsList.getString("rtn_desc"));
			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {rsReturnReasonsList.close();}catch(Exception e) {}
			try {pstReturnReasonsList.close();}catch(Exception e) {}
			try {connReturnReason.close();}catch(Exception e) {}
		}
	
		String dirverButton = " concat(us_name, '<a href=\"../TLKprint/PrintDriverManifest?stdate=ALL&driverid=',c_assignedagent,'&stg_code=dlv_stg&stp_code=with_agent&storecode=',q_branch,'\" style=\"padding-right:20px;\" >"
				+ " <input type=\"button\" value=\" طباعة مانفيست الشحنات \"   class=\"btn btn-default btn-sm\" ></a>') as driver ";
		
		MainSql  = "select c_rcv_hp, c_company_sender, '' as fromdt, '' as todate, ifnull(c_mbapp_agent_status,'') as c_mbapp_agent_status ,  c_receiptamt, date(c_createddt) as c_createddt,c_branchcode, c_rcv_name, c_custreceiptnoori , c_qty,q_branch, p_cases.c_id, c_name, q_previous_action, "
				+ " concat(st_name_ar,' - ',ifnull(cdi_name,''),' ',ifnull(c_rcv_addr_rmk,''))  as address ,c_rcv_state,c_rcv_district, "
				+ " q_id, q_caseid, q_enterdate , q_stage, q_step , stp_id , q_action,"
				+ " q_assigned_to , c_assignedagent, c_rmk, "+dirverButton+" "
				+ " from p_queue " 
				+ " join p_cases on (c_id = q_caseid)"
				+ " join kbstep on (q_stage=stp_stgcode and q_step=stp_code) "
				+ " join kbstate on (c_rcv_state = st_code)  "
				+ " join kbcustomers on (c_custid = kbcustomers.c_id)"
				+ " left join kbusers on c_assignedagent = us_id "
				+ " left join kbcity_district on (cdi_code =c_rcv_district and cdi_stcode=c_rcv_state) "
				+ " where q_stage= '{stg_code}' and q_step='{stp_code}' and q_status='ACTV' "
				
				+ " and ( 'Y'='Y') ";
		
		userDefinedGroupByCol = "driver";
		userDefinedGroupColsOrderBy = "driver, c_createddt";
		userDefinedGridCols.clear();
		userDefinedGridCols.add("c_name");
		userDefinedGridCols.add("c_custreceiptnoori");
		userDefinedGridCols.add("c_receiptamt");
		userDefinedGridCols.add("c_company_sender");
		userDefinedGridCols.add("address");
		userDefinedGridCols.add("q_caseid");
		userDefinedGridCols.add("c_rcv_name");
		userDefinedGridCols.add("c_rcv_hp");
		userDefinedGridCols.add("c_qty");
		userDefinedGridCols.add("c_rmk");
		userDefinedGridCols.add("c_createddt");
		userDefinedGridCols.add("q_action");
		userDefinedGridCols.add("q_previous_action");
		
		userDefinedColLabel.put("c_mbapp_agent_status", "اشعار المندوب");
		userDefinedLookups.put("c_mbapp_agent_status", "SELECT stpd_code, stpd_desc FROM kbstep_decision where stpd_deleted  = 'N' and stpd_onlymbapp = 'Y'");
		userDefinedFilterCols.add("c_assignedagent");
		userDefinedFilterCols.add("c_rcv_hp");
		userDefinedLookups.put("c_assignedagent", "select us_id, us_name from kbusers where us_rank = 'DLVAGENT' and us_active = 'Y' ");
		
		userModifyTD.put("q_action", "modifyAction()");
		
		userDefinedFilterCols.clear();
		userDefinedFilterCols.add("q_caseid");
		userDefinedFilterCols.add("c_custreceiptnoori");
		userDefinedFilterCols.add("c_custid");
		userDefinedFilterCols.add("fromdt");
		userDefinedFilterCols.add("todate");
		userDefinedFilterCols.add("c_rcv_state");
		userDefinedFilterCols.add("c_assignedagent");
		userDefinedFilterCols.add("c_rcv_hp");
		userDefinedFilterCols.add("c_company_sender");
		userDefinedFilterCols.add("c_receiptamt");
		userDefinedColLabel.put("fromdt","بتاريخ");
		userDefinedColLabel.put("todate","إلى تاريخ");
		
		
		userDefinedColLabel.put("q_previous_action","الحالة السابقة");
		userDefinedLookups.put("q_previous_action", "select stpd_code, stpd_desc from kbstep_decision where  stpd_code != 'MOVETOAGENT' ");
		
		userDefinedFilterColsHtmlType.put("fromdt", "DATE");
		userDefinedFilterColsHtmlType.put("todate", "DATE");
	}
	public String modifyAction (HashMap<String,String> hashy) {
		StringBuilder sb = new StringBuilder("<td><table><tr><td>");
		sb.append("<select class='form-control' onchange=\"change_q_actionColor(this, '"+i+"')\" "
				+ " id='q_action_smartyrow_"+i+"' name='q_action_smartyrow_"+i+"' "
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
		sb.append("<tr padding-top:7px'><td>");
		sb.append("<input type='number' style='text-align: right;" + 
				"    background-color: #FFFFB8;" + 
				"    color: #424242;" + 
				"    width: 7em;"
				+ " display:none; line-height: 20px; margin-top:3px;' name= 'new_receiptamt_smartyrow_"+i+"' "
				+ " id = 'new_receiptamt_smartyrow_"+i+"' minval =0 >");
		sb.append("</tr>");
		sb.append("<tr id='returnreasons_"+i+"' style='display:none;padding-top:5px'><td>");
		sb.append("<select class='form-control' id='rtn_Reason_smartyrow_"+i+"' name='rtn_Reason_smartyrow_"+i+"' " 
				+ "style='text-align:right; background-color:#F0FFF0; padding: 0 10px 0 10px;  color: #424242; border: 1px solid #7dc6dd;'> "
				+"<option value=''></option>");
		for (String agentId : returnReasonsList.keySet()) {
			sb.append("<option value='"+agentId+"' >"+returnReasonsList.get(agentId)+"</option> \n");
		}
		sb.append("</td>");
		sb.append("<td>");
		sb.append("<input type='number' style='text-align: right;" + 
				"    background-color: #FFFFB8;" + 
				"    color: #424242;" + 
				"    width: 7em;"
				+ " display:none; line-height: 20px; margin-right:5px;' name= 'new_receiptamtrtn_smartyrow_"+i+"' "
				+ " id = 'new_receiptamtrtn_smartyrow_"+i+"' minval =0 >");
		sb.append("</tr></table></td>");
		i++;
		return sb.toString();
	}
	public void initialize(HashMap smartyStateMap) {
		userDefinedCaption= "<div class='col-md-9 col-sm-12 col-xs-12'>"+this.userDefinedCaption+"</div>"
				+ " <div class=\"checkbox checkbox-success\">" + 
				"                        <input id=\"allreturned\" class=\"\" onclick=\"changeToRecievedAll('all_received');\" type=\"checkbox\">" + 
				"                        <label for=\"allreturned\">" + 
				"                            تم التسليم للكل" + 
				"                        </label>" + 
				"                    </div>";
		super.initialize(smartyStateMap);
		String fromdt = "ALL";
		String todt = "ALL";
		boolean foundSearch = false;
		for (String parameter : search_paramval.keySet()) {
			for (String value : search_paramval.get(parameter)) {
				if (!parameter.equals("filter") && (value != null) && (!value.equals(""))) {
					if (parameter.equals("fromdt")) {
						fromdt =  value;
						foundSearch = true;
					} 
					if (parameter.equals("todate")) {
						todt =  value;
						//foundSearch = true;
					} 
				}
			}
		}
	
	
		if (foundSearch) {
			if (todt.equalsIgnoreCase("ALL") && !fromdt.equalsIgnoreCase("ALL")) {
				todt = fromdt;
			}
			String dirverButton = " concat(us_name, '<a href=\"../TLKprint/PrintDriverManifest?stdate="+fromdt+"&todate="+todt+"&driverid=',c_assignedagent,'&stg_code=dlv_stg&stp_code=with_agent&storecode=',q_branch,'\" style=\"padding-right:20px;\" >"
					+ " <input type=\"button\" value=\" طباعة مانفيست الشحنات \"   class=\"btn btn-default btn-sm\" ></a>') as driver ";
			
			MainSql  = "select c_rcv_hp, c_company_sender,'' as fromdt, '' as todate, ifnull(c_mbapp_agent_status,'') as c_mbapp_agent_status, c_receiptamt, date(c_createddt) as c_createddt,c_branchcode, c_rcv_name, c_custreceiptnoori , c_qty,q_branch, p_cases.c_id, c_name,q_previous_action,  "
					+ "  concat(st_name_ar,' - ',ifnull(cdi_name,''),' ',ifnull(c_rcv_addr_rmk,''))  as address ,c_rcv_state,c_rcv_district, "
					+ " q_id, q_caseid, q_enterdate , q_stage, q_step , stp_id , q_action,"
					+ " q_assigned_to , c_assignedagent, c_rmk, "+dirverButton+" "
					+ " from p_queue "
					+ " join p_cases on (c_id = q_caseid)"
					+ " join kbstep on (q_stage=stp_stgcode and q_step=stp_code) "
					+ " join kbstate on (c_rcv_state = st_code)  "
					+ " join kbcustomers on (c_custid = kbcustomers.c_id)"
					+ " left join kbusers on c_assignedagent = us_id "
					+ " left join kbcity_district on (cdi_code =c_rcv_district and cdi_stcode=c_rcv_state) "
					+ " where q_stage= 'dlv_stg' and q_step='with_agent' and q_status='ACTV' "
					+ " and ( 'Y'='Y') and (date(c_createddt)>='"+fromdt+"') and (date(c_createddt)<='"+todt+"' ) ";
		}
		
	}//end of method initialize
	
	@Override 
	public StringBuilder getMultiEditGrid() {
		//System.out.println("calling gen listing---------------------");
		search_paramval.remove("fromdt");
		search_paramval.remove("todate");
		return super.getMultiEditGrid();
	}
	
	@Override
	public String doUpdate (HttpServletRequest rqs , boolean commit) {
		PreparedStatement pst = null, pst2 = null , pst3 = null ;
		ResultSet rs = null;
		String userid = replaceVarsinString("{useridlogin}", arrayGlobals).trim();
		//super.doUpdate(rqs,false);
		keyVal = parseUpdateRqs(rqs);
		FlowUtils fu = new FlowUtils();
		Utilities ut = new Utilities();
		boolean everyThingIsOk = true;
		int rowsNo =0;
		if (inputMap_ori.get("smartyhiddenmultieditrowsno")!=null)
			rowsNo = Integer.parseInt(inputMap_ori.get("smartyhiddenmultieditrowsno")[0]);
		
		ArrayList<Integer> qIdList= new ArrayList<Integer>();
		HashMap <Integer , String> actionsMap = new HashMap<Integer, String>();
		HashMap <Integer, Double> newReceiptsAmtMap = new HashMap<Integer, Double>();
		HashMap <Integer , String> newReturnReasons = new HashMap<Integer, String>();
		HashMap <Integer, Double> newReceiptsAmtFromRtnShipmentCharge = new HashMap<Integer, Double>();
		String action = "";
		int id = 0;
		for (int i=1 ; i<=rowsNo ; i++){
			action = inputMap_ori.get("q_action_smartyrow_"+i)[0];
			if (action!=null && !action.trim().isEmpty() && !action.trim().equalsIgnoreCase("")
					&& !action.trim().equalsIgnoreCase("null")) {
				id =Integer.parseInt(inputMap_ori.get(hiddenKeyCol+"_smartyrow_"+i)[0]);
				actionsMap.put(id , action);
				if (action.equalsIgnoreCase("SUCS_DLV_CHANGEAMT")) {
					if (inputMap_ori.get("new_receiptamt_smartyrow_"+i)[0] !=null
							&& inputMap_ori.get("new_receiptamt_smartyrow_"+i)[0].length()>0)
						newReceiptsAmtMap.put(id, Double.parseDouble(inputMap_ori.get("new_receiptamt_smartyrow_"+i)[0]));
				}else if(action.equalsIgnoreCase("RTN_WITHSHP_CHARGE_SNDR")||action.equalsIgnoreCase("RTN_WITHSHIPMENT_CHRG")||action.equalsIgnoreCase("RTN_INSTORE")){
					if (inputMap_ori.get("rtn_Reason_smartyrow_"+i)[0] !=null
							&& inputMap_ori.get("rtn_Reason_smartyrow_"+i)[0].length()>0)
						newReturnReasons.put(id, inputMap_ori.get("rtn_Reason_smartyrow_"+i)[0]);
					if(action.equalsIgnoreCase("RTN_WITHSHIPMENT_CHRG")&&inputMap_ori.get("new_receiptamtrtn_smartyrow_"+i)[0] !=null
							&& inputMap_ori.get("new_receiptamtrtn_smartyrow_"+i)[0].length()>0)
						newReceiptsAmtFromRtnShipmentCharge.put(id, Double.parseDouble(inputMap_ori.get("new_receiptamtrtn_smartyrow_"+i)[0]));
				}
				qIdList.add(id);
				
			}
		}
		try{
			pst = conn.prepareStatement("update p_queue set q_action=?, q_action_takenby=? where q_id=? and q_status !='CLS'");
			pst2 = conn.prepareStatement("update p_cases set c_shipmentpaidbysender='N' , c_shipmentpaidbycustomer='Y' "
					+ " where c_id in (select q_caseid from p_queue where q_id =? and q_status !='CLS')");
			pst3 = conn.prepareStatement("update p_cases set c_shipmentpaidbysender='Y' , c_shipmentpaidbycustomer='N' "
					+ " where c_id in (select q_caseid from p_queue where q_id =? and q_status !='CLS')");
			int caseid = 0;
			for (int qid :qIdList){
				everyThingIsOk = true;
				caseid = ut.getCaseIdFromQid(conn, qid);
				//update the action is take by who
				pst.setString(1, actionsMap.get(qid));
				pst.setString(2, userid);
				pst.setInt(3, qid);
				pst.executeUpdate();
				pst.clearParameters();
				
				if (newReturnReasons !=null && newReturnReasons.get(qid)!=null && !newReturnReasons.get(qid).trim().isEmpty())
					ut.changeReturnReasons(conn, caseid, newReturnReasons.get(qid));
				
				// when returned but the shipment cost is paid by the sender
				if (actionsMap.get(qid).equalsIgnoreCase("RTN_WITHSHP_CHARGE_SNDR")) {
					pst3.setInt(1,qid);
					pst3.executeUpdate();
					pst3.clearParameters();
				}else if (actionsMap.get(qid).equalsIgnoreCase("SUCS_DLV_CHANGEAMT")) {
					if (newReceiptsAmtMap !=null && newReceiptsAmtMap.get(qid)!=null && newReceiptsAmtMap.get(qid)>-1) {
						ut.changeReceiptPrice(conn, caseid, newReceiptsAmtMap.get(qid), userid);
					}else {
						everyThingIsOk = false;
					}
					 	
				}else if (!actionsMap.get(qid).equalsIgnoreCase("RTN_INSTORE")) {
					if(actionsMap.get(qid).equalsIgnoreCase("RTN_WITHSHIPMENT_CHRG")) {
						if (newReceiptsAmtFromRtnShipmentCharge !=null && newReceiptsAmtFromRtnShipmentCharge.get(qid)!=null && newReceiptsAmtFromRtnShipmentCharge.get(qid)>-1) {
							ut.changeReceiptPriceFromRTN_WITHSHIPMENT_CHRG(conn, caseid, newReceiptsAmtFromRtnShipmentCharge.get(qid), userid);
						}else {
							everyThingIsOk = false;
							throw new Exception("Shipment qeue ID = "+qid+" the new Receipt Amt is "+newReceiptsAmtFromRtnShipmentCharge.get(qid)+"");
						}
					}// if not normal return then the shipment is paid by the receiver
					pst2.setInt(1,qid);
					pst2.executeUpdate();
					pst2.clearParameters();
				}
				if (everyThingIsOk)
					fu.MoveDecisionStepNext(conn, qid);
				
			}
			conn.commit();
		
		}catch(Exception e){
			e.printStackTrace();
			try{conn.rollback();}catch(Exception eRoll){}
			return "Error "+e;
		}finally{
			try{pst.close();}catch(Exception e){}
			try{pst2.close();}catch(Exception e){}
			try{pst3.close();}catch(Exception e){}
		}
				
		return "Saved";
	}
}
