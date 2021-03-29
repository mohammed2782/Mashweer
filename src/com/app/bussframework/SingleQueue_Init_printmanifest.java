package com.app.bussframework;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.app.cases.CaseInformation;
import com.app.util.IntegrationFactory;
import com.app.util.IntegrationUtil;
import com.app.util.SystemsIntegration;
import com.app.util.Utilities;

public class SingleQueue_Init_printmanifest extends SingleQueue {
	 
	public SingleQueue_Init_printmanifest() {
		
		MainSql  = "select us_name, c_branchcode, q_branch, c_assignedagent,concat(c_assignedagent,'_myspecialkey_',q_branch) as specialkey , count(*) as  totcases,  '' as  q_action, '' as printmanifest"
				+ " from p_queue "
				+ " join p_cases on (c_id = q_caseid)"
				+ " join kbstep on (q_stage=stp_stgcode and q_step=stp_code) "
				+ " join kbstate on (c_rcv_state = st_code)  "
				+ " left join kbusers on c_assignedagent = us_id "
				+ " where q_stage= '{stg_code}' and q_step='{stp_code}' and q_status='ACTV' "
				+ " and (c_branchcode='{userstorecode}' or '{superRank}'='Y')"
				+ " group by c_assignedagent ";
		mainTable = "p_queue";
		keyCol = "specialkey";
		userDefinedGridCols.clear();
		userDefinedGridCols.add("us_name");
		userDefinedGridCols.add("totcases");
		userDefinedGridCols.add("printmanifest");
		userDefinedGridCols.add("q_action");
		canFilter = false;
		UserDefinedPageRows = 1000;
		
		userDefinedColLabel.put("totcases","عدد الشحنات");
		userDefinedColLabel.put("us_name","المندوب");
		userDefinedColLabel.put("printmanifest","طباعة المنفيست");
		
		userModifyTD.put("printmanifest", "printManifest({c_assignedagent},{c_branchcode})");
		//userModifyTD.put("totcases", "showPopUp({totcases},{c_branchcode},{c_assignedagent})");
		
		userDefinedLookups.put("q_action", "select stpd_code, stpd_desc from kbstep_decision where "
				+ " stpd_stpid in (select stp_id from kbstep where stp_code='{stp_code}' and stp_stgcode='{stg_code}') and stpd_code !='CHNGE_AGENT' ");
		
	}
	

	public String showPopUp(HashMap<String,String>hashy) {
		String HTMLButton= "";
		String btnClass ="btn btn-sm btn-dark";
		String btnText = "عرض جميع الشحنات - العدد "+hashy.get("totcases");
			
		String url ="showManifestShipmentPopUp.jsp?c_assignedagent="+hashy.get("c_assignedagent")+"&q_branch="+hashy.get("c_branchcode");
		HTMLButton+="<td align='center'><button type='button' class='"+btnClass+"' "
				+ " onclick=\"popitup ('"+url+"' , '' , 800 ,700);\" >"+btnText+"</button></td>";
		return HTMLButton;
	}
	public String printManifest(HashMap<String,String> hashy) {
		
		String button = "<td><a href='../TLKprint/PrintDriverManifest?driverid="+
		hashy.get("c_assignedagent")+"&stg_code=init&stp_code=prt_manifest&storecode="+hashy.get("c_branchcode")+"'>"
					+"<input type='button'  class='btn btn-danger btn-sm' value='PDF طباعة المنفبست' /></a> &nbsp;";
		
		button += "<a href='../TLKprint/PrintDriverManifestExcel?driverid="+
				hashy.get("c_assignedagent")+"&stg_code=init&stp_code=prt_manifest&storecode="+hashy.get("c_branchcode")+"'>"
				+"<input type='button'  class='btn btn-success btn-sm' value='طباعة المنفبست excel' /></a>";
		
		button+= "</td>";
		
		
		return button;
	}
	
	@Override
	public String doUpdate (HttpServletRequest rqs , boolean commit) {
		PreparedStatement pst = null, pstUpdateRural = null, pstUpdateAgentShare = null;
		
		HashMap<String, ArrayList<String>> integrationSystemCases = new HashMap<String, ArrayList<String>> ();
		String integrationSystemCode = "";
		
		IntegrationUtil iu = new IntegrationUtil();
		String userid = replaceVarsinString("{useridlogin}", arrayGlobals).trim();
		//super.doUpdate(rqs,false);
		keyVal = parseUpdateRqs(rqs);
		FlowUtils fu = new FlowUtils();
		Utilities ut = new Utilities();
		int rowsNo =0;
		try{
			if (inputMap_ori.get("smartyhiddenmultieditrowsno")!=null)
				rowsNo = Integer.parseInt(inputMap_ori.get("smartyhiddenmultieditrowsno")[0]);
			
			HashMap<String, String> driverList  = new HashMap<String, String>();
			HashMap<String, String> actionsMap  = new HashMap<String, String>();
			HashMap<String, String> ruralMap  = new HashMap<String, String>();
			String action = "";
			String specialKey ="";
			for (int i=1 ; i<=rowsNo ; i++){
				action = inputMap_ori.get("q_action_smartyrow_"+i)[0];
				if (action!=null && !action.trim().isEmpty() && !action.trim().equalsIgnoreCase("")
						&& !action.trim().equalsIgnoreCase("null")) {
					//if (action.equalsIgnoreCase("MOVETOAGENT")) {
						specialKey = inputMap_ori.get(hiddenKeyCol+"_smartyrow_"+i)[0];
						String  [] agentBranch= specialKey.split("_myspecialkey_");
						driverList.put(agentBranch[0], agentBranch[1]);
						//System.out.println("specialKey==>"+specialKey+", agentid-->"+agentBranch[0]+", branch==>"+agentBranch[1]);
						actionsMap.put(agentBranch[0], action);
						if (action.equalsIgnoreCase("CHANG_ALL_RURAL")) {
							ruralMap.put(agentBranch[0],"Y");
						}
						else if (action.equalsIgnoreCase("CHANG_ALL_CENTER")) {
							ruralMap.put(agentBranch[0],"N");
						}
				}
			}
			boolean rural  = false;
			ArrayList<CaseInformation> dlvs = new ArrayList<CaseInformation>();
			double agentShare = 0.0;
			double shipmentCost;
			
			pst = conn.prepareStatement("update p_queue set q_action=?, q_action_takenby=? where q_id=?");
			pstUpdateRural = conn.prepareStatement("update p_cases set c_rural=? where c_id=? and c_assignedagent=?");
			pstUpdateAgentShare = conn.prepareStatement("update p_cases set c_shipment_cost=?, c_agentshare=? where c_id=?  ");
			for (String driverId :driverList.keySet()){
				dlvs = ut.getItemsPerDriver(conn,  driverId,  "init",  "prt_manifest", driverList.get(driverId),"ALL", "ALL");
				ArrayList<String> casesToPush = new ArrayList<String> ();
				integrationSystemCode = iu.getIntegratingSystemBasedOnDlvAgent(conn, driverId);
				for (CaseInformation ci  : dlvs){
					// if there is integration and we moved to the with agent step
					if (integrationSystemCode!=null && integrationSystemCode.length()>0) {
						if (actionsMap.get(driverId).equalsIgnoreCase("MOVETOAGENT")) {
							casesToPush.add(ut.getCaseIdFromQid(conn, ci.getQid())+"");
						}
					}
					
					//System.out.println("ci case id==>"+ci.getCaseid()+", qid-->"+ci.getQid());
					pst.setString(1, actionsMap.get(driverId));
					pst.setString(2, userid);
					pst.setInt(3, ci.getQid());
					pst.executeUpdate();
					pst.clearParameters();
					
					//Update rural if changed
					if ( ruralMap != null && ruralMap.containsKey(driverId)) {
						pstUpdateRural.setString(1, ruralMap.get(driverId));
						pstUpdateRural.setInt(2, ci.getCaseid());
						pstUpdateRural.setString(3, driverId);
						pstUpdateRural.executeUpdate();
						pstUpdateRural.clearParameters();
					}
					//re calculate agent share for all
					agentShare = 0.0;
					rural = false;
					if(ci.getRural().equalsIgnoreCase("Y"))
						rural = true;
					agentShare = ut.calcAgentShipmentChargesShare(conn,Integer.parseInt(ci.getSenderCompanyId()), ci.getState() ,ci.getDistrict(), rural , driverId );
					shipmentCost = ut. calcShipmentChargesBasedOnDestCity(conn, ci.getState(), rural, ci.getCustId(), Integer.parseInt(ci.getSenderCompanyId()));
					pstUpdateAgentShare.setDouble(1, shipmentCost);
					pstUpdateAgentShare.setDouble(2, agentShare);
					pstUpdateAgentShare.setInt(3, ci.getCaseid());
					pstUpdateAgentShare.executeUpdate();
					pstUpdateAgentShare.clearParameters();
					
					//calculate shipment profit and partner share
					ut.calcShipmentProfitAndPartnerShare(conn,ci.getCaseid());
					
					fu.MoveDecisionStepNext(conn, ci.getQid());
					
					if (integrationSystemCode!=null && integrationSystemCode.length()>0) {
						if (casesToPush.size()>0)
							integrationSystemCases.put(integrationSystemCode, casesToPush);
					}
				}
			}
			
			// now the integration process kicks in
			IntegrationFactory integrationFactory = new IntegrationFactory();
			SystemsIntegration systemsIntegration;
			for (String IntSysCode : integrationSystemCases.keySet()) {
				systemsIntegration = integrationFactory.getSystemsIntegrationClass(IntSysCode);
				System.out.println("systemsIntegration----->"+systemsIntegration+", cases--->"+integrationSystemCases.get(IntSysCode));
				Map<String, String> integratedCases =  systemsIntegration.pushCases(conn, integrationSystemCases.get(IntSysCode));
				if (integratedCases.size()>0) {
					iu.updateIntegrationSentCases(conn, integratedCases, IntSysCode);
				}
			}
			conn.commit();
		}catch(Exception e){
			e.printStackTrace();
			try{conn.rollback();}catch(Exception eRoll){}
			return "Error";
		}finally{
			try{pst.close();}catch(Exception e){}
			try{pstUpdateRural.close();}catch(Exception e){}
			try{pstUpdateAgentShare.close();}catch(Exception e){}
		}
				
		return "Saved";
	}
}
