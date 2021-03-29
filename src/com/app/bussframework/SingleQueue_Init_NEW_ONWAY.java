package com.app.bussframework;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.app.db.mysql;

public class SingleQueue_Init_NEW_ONWAY extends SingleQueue{
	public void initialize(HashMap smartyStateMap) {
		
		MainSql  = "select c_rcv_hp, c_rural, c_pickupagent, c_rtnreason, c_receiptamt, date(c_createddt) as c_createddt,c_branchcode, c_assignedagent, c_custreceiptnoori, "
				+ "c_rcv_name , c_qty,q_branch,'' as attempts, p_cases.c_id, c_name,  "
				+ " concat(st_name_ar,' - ', ifnull(cdi_name,''),' ' ,c_rcv_addr_rmk) as address ,c_rcv_state,ifnull(c_rcv_district,'NA') as c_rcv_district,"
				+ "  q_id, q_caseid, (q_enterdate+INTERVAL 9 HOUR) as q_enterdate  , q_stage, q_step , stp_id , q_action,"
				+ " q_assigned_to , c_rmk "
				+ " from p_queue "
				+ " join p_cases on (c_id = q_caseid)"
				+ " join kbstep on (q_stage=stp_stgcode and q_step=stp_code) "
				+ " join kbstate on (c_rcv_state = st_code)  "
				+ " join kbcustomers on (c_custid = kbcustomers.c_id)"
				+ " left join kbcity_district on (cdi_stcode =st_code and cdi_code = c_rcv_district) "
				+ " where q_stage= '{stg_code}' and q_step='{stp_code}' and q_status !='CLS'"
				+ " and (c_branchcode='{userstorecode}' or '{superRank}'='Y')  ";
		
		userDefinedFilterColsUsingLike.add("c_rcv_addr_rmk");
		userDefinedGridCols.clear();
		userDefinedGridCols.add("c_name");
		
		userDefinedGridCols.add("c_receiptamt");
		userDefinedGridCols.add("address");
		userDefinedGridCols.add("q_caseid");
		userDefinedGridCols.add("c_rcv_name");
		userDefinedGridCols.add("c_rcv_hp");
		userDefinedGridCols.add("c_qty");
		userDefinedGridCols.add("c_createddt");
		userDefinedGridCols.add("c_custreceiptnoori");
		userDefinedGridCols.add("c_rcv_district");
		userDefinedGridCols.add("c_rural");
		
		userDefinedEditCols.add("c_rural");
		userDefinedEditCols.add("c_rcv_district");
		
		userDefinedEditColsHtmlType.put("c_rural", "DROPLIST");
		userDefinedEditColsHtmlType.put("c_rcv_district", "DROPLIST");
		
		userDefinedLookups.put("c_rural", "select kbcode, kbdesc from kbgeneral where kbcat1 = 'YESNO'");
		
		canFilter = true;
		userDefinedFilterCols.clear();
		userDefinedColLabel.put("c_rcv_addr_rmk", "العنوان");
		userDefinedFilterCols.add("c_rcv_addr_rmk");
		userModifyTD.put("c_custreceiptnoori", "modifythis({c_custreceiptnoori}");
		super.initialize(smartyStateMap);
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		StringBuilder DropDownHtml= new StringBuilder("");
		try {
			conn = mysql.getConn();
			pst = conn.prepareStatement("select cdi_code , cdi_name from kbcity_district ");
			rs = pst.executeQuery();
			DropDownHtml.append("<select id='globalDistrictSelect' onchange='doGlobalDistrictSelect()'>");
			DropDownHtml.append("<option value=''></option> \n");
			while (rs.next()) {
				DropDownHtml.append("<option value='"+rs.getString("cdi_code")+"'>"+rs.getString("cdi_name")+"</option> \n");
			}
			DropDownHtml.append("</select> \n");
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
			try {conn.close();}catch(Exception e) {/*ignore*/}
		}
		
		userDefinedCaption= "<div class='row'><div class='col-md-8 col-sm-12 col-xs-12'>"+this.userDefinedCaption+"</div>"
					+ " <div class='col-md-4 col-sm-12 col-xs-12'>أختيار منطقة  للكل"+DropDownHtml+" </div></div>";

	}
	
	public String modifythis (HashMap<String,String> hashy) {
		return "<td align='right' id = '"+hashy.get("c_custreceiptnoori")+"'>"+hashy.get("c_custreceiptnoori")+"</td>";
	}
	
	@Override
	public String doUpdate (HttpServletRequest rqs , boolean commit) {
		PreparedStatement pst = null , pstUpdateCase=null ;
		String userid = replaceVarsinString("{useridlogin}", arrayGlobals).trim();
		//super.doUpdate(rqs,false);
		keyVal = parseUpdateRqs(rqs);
		FlowUtils fu = new FlowUtils();
		int rowsNo =0;
		String msg = "تم الحفظ بنجاح";
		
		if (inputMap_ori.get("smartyhiddenmultieditrowsno")!=null)
			rowsNo = Integer.parseInt(inputMap_ori.get("smartyhiddenmultieditrowsno")[0]);
		
		ArrayList<Integer> qIdList= new ArrayList<Integer>();
		
		HashMap <Integer , String> ruralMap = new HashMap<Integer, String>();
		HashMap <Integer , String> districtMap = new HashMap<Integer, String>();
		HashMap <Integer , String> receiptNoMap = new HashMap<Integer, String>();
		String action = "PUSH_TOSTORE";
		int id = 0;
		
		for (int i=1 ; i<=rowsNo ; i++){
			if ( inputMap_ori.containsKey(hiddenKeyCol+"_smartyrow_"+i) && 
					inputMap_ori.get(hiddenKeyCol+"_smartyrow_"+i)[0] !=null &&
					inputMap_ori.containsKey("c_rcv_district_smartyrow_"+i) 
					&& inputMap_ori.get("c_rcv_district_smartyrow_"+i)[0]!=null
					&& inputMap_ori.get("c_rcv_district_smartyrow_"+i)[0].length()>0) {
				id =Integer.parseInt(inputMap_ori.get(hiddenKeyCol+"_smartyrow_"+i)[0]);
				ruralMap.put(id, inputMap_ori.get("c_rural_smartyrow_"+i)[0]);
				districtMap.put(id, inputMap_ori.get("c_rcv_district_smartyrow_"+i)[0]);
				qIdList.add(id);
			}
		}
		
		try{
			pst = conn.prepareStatement("update p_queue set q_action=?, q_action_takenby=? where q_id=? and q_status = 'ACTV'");
			pstUpdateCase = conn.prepareStatement("update p_cases set c_rural =?, c_rcv_district=? where c_id in (select q_caseid from p_queue where q_id=? "
					+ " and q_status='ACTV' and q_step='NEW_ONWAY' and q_stage='init')");
			for (int qid :qIdList){
				System.out.println("--->"+qid);
				try {
					pstUpdateCase.setString(1, ruralMap.get(qid));
					pstUpdateCase.setString(2, districtMap.get(qid));
					pstUpdateCase.setInt(3, qid);
					pstUpdateCase.executeUpdate();
					pstUpdateCase.clearParameters();
					
					//update the action is take by who
					pst.setString(1, action);
					pst.setString(2, userid);
					pst.setInt(3, qid);
					pst.executeUpdate();
					pst.clearParameters();
					
					fu.MoveDecisionStepNext(conn, qid);
					conn.commit();
				}catch(Exception e){
					e.printStackTrace();
					msg = "خطأ في حفظ الوصل رقم "+receiptNoMap.get(qid)+"</br>";
					try{conn.rollback();}catch(Exception eRoll){}
				}
			}
			
		
		}catch(Exception e){
			e.printStackTrace();
			try{conn.rollback();}catch(Exception eRoll){}
			return "Error";
		}finally{
			try{pst.close();}catch(Exception e){}
			try{pstUpdateCase.close();}catch(Exception e){}
		}
				
		return "Saved";
	}
}
