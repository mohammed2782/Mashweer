package com.app.bussframework;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.app.core.CoreMgr;

public abstract class SingleQueue extends CoreMgr{
	SingleQueue(){
		MainSql  = "select c_rural,c_company_sender, c_pickupagent, c_rtnreason, c_receiptamt, date(c_createddt) as c_createddt,c_branchcode, c_assignedagent, c_custreceiptnoori, "
				+ "c_rcv_name , c_qty,q_branch,'' as attempts, p_cases.c_id, c_name,  "
				+ " concat(st_name_ar,' - ', ifnull(cdi_name,''),' ' ,ifnull(c_rcv_addr_rmk,'')) as address ,c_rcv_state,ifnull(c_rcv_district,'NA') as c_rcv_district,"
				+ "  q_id, q_caseid, (q_enterdate+INTERVAL 9 HOUR) as q_enterdate  , q_stage, q_step , stp_id , q_action,"
				+ " q_assigned_to , c_rmk, c_rcv_hp, c_agentsharesettled , c_agentpmtid"
				+ " from p_queue "
				+ " join p_cases on (c_id = q_caseid)"
				+ " join kbstep on (q_stage=stp_stgcode and q_step=stp_code) "
				+ " join kbstate on (c_rcv_state = st_code)  "
				+ " join kbcustomers on (c_custid = kbcustomers.c_id)"
				+ " left join kbcity_district on (cdi_stcode =st_code and cdi_code = c_rcv_district) "
				+ " where q_stage= '{stg_code}' and q_step='{stp_code}' and q_status !='CLS'";
		
		userDefinedGridCols.add("c_name");
		userDefinedGridCols.add("c_custreceiptnoori");
		userDefinedGridCols.add("c_receiptamt");
		userDefinedGridCols.add("address");
		userDefinedGridCols.add("q_caseid");
		userDefinedGridCols.add("c_rcv_name");
		
		userDefinedGridCols.add("c_qty");
		userDefinedGridCols.add("c_createddt");
		userDefinedGridCols.add("q_action");
		//userDefinedGridCols.add("c_branchcode");
		canEdit = true;
		canFilter = true;
		
		userDefinedFilterCols.add("c_company_sender");
		userDefinedFilterCols.add("q_caseid");
		userDefinedFilterCols.add("c_custreceiptnoori");
		userDefinedFilterCols.add("c_custid");
		userDefinedFilterCols.add("c_createddt");
		userDefinedFilterCols.add("c_rcv_state");
		userDefinedFilterCols.add("c_rcv_district");
		userDefinedFilterCols.add("c_receiptamt");
		
		userDefinedColLabel.put("c_createddt","تاريخ الشحنه");
		userDefinedColLabel.put("c_receiptamt","مبلغ الوصل");
		userDefinedColLabel.put("c_name","صاحب المحل");
		userDefinedColLabel.put("c_custid","صاحب المحل");
		userDefinedColLabel.put("c_rcv_name","أسم المستلم");
		userDefinedColLabel.put("q_action","العمليه");
		userDefinedColLabel.put("address","العنوان");
		userDefinedColLabel.put("c_qty","عدد القطع");
		userDefinedColLabel.put("c_rcv_state","المدينه");
		userDefinedColLabel.put("c_rmk","ملاحظات");
		userDefinedColLabel.put("c_custreceiptnoori","رقم الوصل");
		userDefinedColLabel.put("c_assignedagent","مندوب التوصيل");
		userDefinedColLabel.put("c_rcv_hp","هاتف المستلم");
		userDefinedColLabel.put("c_rtnreason","سبب الأرجاع");
		userDefinedColLabel.put("c_pickupagent", "مندوب الإستلام");
		userDefinedColLabel.put("c_company_sender", "الشركة المرسلة");
		userDefinedColLabel.put("c_rcv_district", "مناطق");
		userDefinedColLabel.put("c_agentsharesettled", "تم محاسبة المندوب");
		userDefinedColLabel.put("c_agentpmtid", "رقم دفعة المندوب");
		userDefinedColLabel.put("c_rural", "أطراف");
		
		userDefinedLookups.put("c_agentsharesettled", "select kbcode, kbdesc from kbgeneral where kbcat1= 'SETTLED'");
		userDefinedLookups.put("c_rcv_district", "SELECT cdi_code, cdi_name from kbcity_district ");
		
		userDefinedColsTypes.put("c_custreceiptnoori", "VARCHAR");//to remove the comma
		//userDefinedColsTypes.put("q_enterdate", "DATE");
		userDefinedFilterColsHtmlType.put("c_createddt", "DATE");
		userDefinedFilterColsHtmlType.put("c_company_sender", "DROPLIST");
		userDefinedNewColsHtmlType.put("c_createddt", "TIMESTAMP");
		
		userDefinedLookups.put("q_assigned_to", "!select us_id , us_name from kbusers where us_id in " + 
				"	(select us_id   from kbusers where us_rank = 'DLVAGENT' and us_to_state like '%{c_rcv_state}%'" + 
				"    and  us_id in (select agdi_usid from kbagent_district 	join kbcity_district on (cdi_stcode like '%{c_rcv_state}%' and agdi_districtcode = cdi_code)" + 
					"    where agdi_districtcode='{c_rcv_district}')" + 
				"	union" + 
				"	select us_id   from kbusers where us_rank = 'DLVAGENT'  and us_to_state like '%{c_rcv_state}%' and us_to_state not like '%BAS%'" + 
				"    ) and us_active='Y'");
		userDefinedLookups.put("c_assignedagent", "select us_id , us_name from kbusers where us_rank = 'DLVAGENT' ");
		userDefinedLookups.put("c_pickupagent", "select us_id , us_name from kbusers");
		userDefinedLookups.put("c_rcv_state", "select st_code , st_name_ar from kbstate  order by st_order");
		userDefinedLookups.put("c_company_sender", "select comp_id , comp_name from kbcompanies");
		
		userDefinedLookups.put("c_rtnreason", "SELECT rtn_code, rtn_desc FROM kbrtn_reasons");
		userDefinedEditColsHtmlType.put("q_assigned_to", "DROPLIST");
		
		userDefinedLookups.put("c_custid", "select c_id , c_name from kbcustomers");
		
		userDefinedFilterColsHtmlType.put("c_custid", "DROPLIST");
		
		
		
		userDefinedColLabel.put("q_caseid", "رقم الشحنه");
		userDefinedColLabel.put("q_enterdate", "تاريخ ووقت الحاله");
		
		mainTable = "p_queue";
		keyCol = "q_id";
		displayMode = "GRIDEDIT";
		
		userDefinedEditCols.add("q_action");
		
		userDefinedLookups.put("q_action", "select stpd_code, stpd_desc from kbstep_decision where "
				+ " stpd_stpid in (select stp_id from kbstep where stp_code='{stp_code}' and stp_stgcode='{stg_code}') and stpd_onlymbapp='N' ");
		
		UserDefinedPageRows = 200;
	}
	
	
	
	
	@Override
	public void initialize(HashMap smartyStateMap){
		super.initialize(smartyStateMap);
		if (arrayGlobals.get("c_id")!=null) {
			String q_caseid = (String) arrayGlobals.get("c_id");
			search_paramval.put("q_caseid", new String[] {q_caseid});
		}
		String stp_code = replaceVarsinString("{stp_code}", arrayGlobals).trim();
		if (stp_code.equalsIgnoreCase("rtn_to_archv"))
			userDefinedCaption= "<div class='col-md-9 col-sm-12 col-xs-12'>"+this.userDefinedCaption+"</div>"
					+ " <div class=\"checkbox checkbox-success\">" + 
					"                        <input id=\"allreturned\" class=\"\" onclick=\"changeToArchiveAll('all_archived');\" type=\"checkbox\">" + 
					"                        <label for=\"allreturned\">" + 
					"                            أرشف الكل" + 
					"                        </label>" + 
					"                    </div>";
	}
	@Override
	public String doUpdate (HttpServletRequest rqs , boolean commit) {
		PreparedStatement pst = null ;
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
			pst = conn.prepareStatement("update p_queue set q_action=?, q_action_takenby=? where q_id=?");
			
			for (int qid :qIdList){
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
