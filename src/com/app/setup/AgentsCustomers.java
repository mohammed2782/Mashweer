package com.app.setup;

import java.sql.PreparedStatement;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.app.core.CoreMgr;

public class AgentsCustomers extends CoreMgr {
	public AgentsCustomers () {
		MainSql = "select * from kbcustomers where c_assigned_pickup_agent='{customersusid}'";
		userDefinedCaption = "زبائن المندوب";
		userDefinedColLabel.put("c_name","الزبون");
		canDelete = true;
		canNew = true;
		
		mainTable = "kbcustomers";
		keyCol = "c_id";
		
		userDefinedNewCols.add("c_id");
		userDefinedGridCols.add("c_id");
		//userDefinedNewCols.add("agdi_districtcode");
		
		//userDefinedNewColsDefualtValues.put("agdi_usid", new String[] {"{districtsusid}"});
		
		//userDefinedReadOnlyNewCols.add("agdi_usid");
		
		userDefinedColLabel.put("c_id", "الزبون");
		//userDefinedColLabel.put("agdi_districtcode", "المنطقه");
		
		//userDefinedNewLookups.put("agdi_districtcode", "select cdi_code, cdi_name from kbcity_district  ");
		userDefinedLookups.put("c_id", "select c_id, c_name from kbcustomers");
		myhtmlmgr.refreshPageOnDelete = true;
		
		userDefinedNewLookups.put("c_id", "select c_id, c_name from kbcustomers where (c_assigned_pickup_agent is null or c_assigned_pickup_agent = '0')");
		myhtmlmgr.refreshPageOnDelete = true;
	}
	
	@Override
	public String doInsert(HttpServletRequest rqs , boolean autoCommit){
		String statusMsg= "customer added";
		String agentId =  replaceVarsinString(" {customersusid} ", arrayGlobals).trim();
		Map <String , String[]> inputMap = filterRequest (rqs);
		PreparedStatement pst = null;
		
		try{
			pst = conn.prepareStatement(" update kbcustomers set c_assigned_pickup_agent =? where c_id=?");
			pst.setString(1, agentId);
			pst.setString(2, inputMap.get("c_id")[0]);
			pst.executeUpdate();
			conn.commit();
		}catch(Exception e){
			/*log error*/
			statusMsg = "Error at user creation, error ("+e.getMessage()+")";
			try{conn.rollback();}catch(Exception ignoreE){}
			e.printStackTrace();
		}finally{
			try{pst.close();}catch(Exception e){/* ignore*/}
		}
		
		return statusMsg;
	}
	
	@Override
	public String doDelete(HttpServletRequest rqs){
		String statusMsg= "customer removed";
		Map <String , String[]> inputMap = filterRequest (rqs);
		PreparedStatement pst = null;
		
		try{
			pst = conn.prepareStatement(" update kbcustomers set c_assigned_pickup_agent =null where c_id=?");
			pst.setString(1, inputMap.get("c_id")[0]);
			pst.executeUpdate();
			conn.commit();
		}catch(Exception e){
			/*log error*/
			statusMsg = "Error at user creation, error ("+e.getMessage()+")";
			try{conn.rollback();}catch(Exception ignoreE){}
			e.printStackTrace();
		}finally{
			try{pst.close();}catch(Exception e){/* ignore*/}
		}
		
		return statusMsg;
	}
}
