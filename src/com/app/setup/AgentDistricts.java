package com.app.setup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.app.core.CoreMgr;
import com.app.db.mysql;

public class AgentDistricts extends CoreMgr{
	public AgentDistricts() {
		MainSql = "select kbagent_district.*, '' as  state from kbagent_district where agdi_usid='{districtsusid}'";
		userDefinedCaption = "مناطق المندوب";
		userDefinedColLabel.put("agdi_districtcode","المنطقه");
		canDelete = true;
		canNew = true;
		canEdit = true;
		
		mainTable = "kbagent_district";
		keyCol = "agdi_id";
		
		userDefinedGridCols.add("agdi_districtcode");
		userDefinedGridCols.add("agdi_agentshare");
		userDefinedGridCols.add("agdi_agentsharepriority");

		
		userDefinedNewCols.add("agdi_usid");
		userDefinedNewCols.add("state");
		userDefinedNewCols.add("agdi_districtcode");
		
		userDefinedNewColsDefualtValues.put("agdi_usid", new String[] {"{districtsusid}"});
		
		
		userDefinedReadOnlyNewCols.add("agdi_usid");
		userDefinedLookups.put("agdi_usid", "select us_id, us_name from kbusers");
		
		userDefinedColLabel.put("agdi_usid", "المندوب");
		userDefinedColLabel.put("agdi_districtcode", "المنطقه");
		userDefinedColLabel.put("agdi_agentshare", "حصة المندوب");
		userDefinedColLabel.put("state", "المحافظة");
		userDefinedColLabel.put("agdi_agentsharepriority", "الأولوية");
		
		userDefinedEditCols.add("agdi_agentshare");
		userDefinedEditCols.add("agdi_agentsharepriority");
		
		userDefinedLookups.put("agdi_agentsharepriority", "select 'Y' , 'أعلى أولوية' from dual");
		
		//userDefinedNewLookups.put("agdi_districtcode", "select cdi_code, cdi_name from kbcity_district  ");
		userDefinedLookups.put("agdi_districtcode", "select cdi_code, cdi_name from kbcity_district");
		userDefinedLookups.put("state","select st_code, st_name_ar from kbstate");
		
		userDefinedNewColsHtmlType.put("agdi_districtcode", "CHECKBOX");
	}
	
	
	@Override
	public void initialize(HashMap smartyStateMap){
		String districtsusid = replaceVarsinString(" {districtsusid} ", arrayGlobals).trim();
		userDefinedNewLookups.put("agdi_districtcode", "! select cdi_code, cdi_name from kbcity_district where cdi_code not in" + 
				" (select agdi_districtcode from kbagent_district where agdi_usid='"+districtsusid+"') and cdi_stcode ='{state}'");
		
		super.initialize(smartyStateMap);
	}
	
	 @Override 
	 public String doInsert (HttpServletRequest rqs , boolean commit) {
		 Connection conn = null;
		 PreparedStatement pst = null;
		 String districtsusid = replaceVarsinString(" {districtsusid} ", arrayGlobals).trim();
		 try {
			 conn = mysql.getConn();
			 inputMap_ori = filterRequest(rqs);
			 pst = conn.prepareStatement("insert into kbagent_district (agdi_usid , agdi_districtcode) values (?, ?)");
			 for (String agdi_districtcode : inputMap_ori.get("agdi_districtcode")){
				 pst.setString(1, districtsusid);
				 pst.setString(2, agdi_districtcode);
				 pst.executeUpdate();
			 }
			 conn.commit();
			 
		 }catch(Exception e) {
			 try {conn.rollback();}catch(Exception eRoll) {/**/}
			 e.printStackTrace();
		 }finally {
			 try {pst.close();}catch(Exception e) {/**/}
			 try {conn.close();}catch(Exception e) {/**/}
		 }
		 return "";
	 }
}
