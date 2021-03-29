package com.app.setup;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.app.core.CoreMgr;

public class ReAssignCasesFromCustomers extends CoreMgr {
	public ReAssignCasesFromCustomers () {
		MainSql = "select '' as pullfromcust , '' as pushtocust from dual";
		
		canNew = true;
		mainTable = "kbcustomers";
		
		userDefinedNewCols.add("pullfromcust");
		userDefinedNewCols.add("pushtocust");
		userDefinedColLabel.put("pullfromcust", "نقل الشحنات من هذا الزبون");
		userDefinedColLabel.put("pushtocust", "إلى هذا الزبون");
		
		userDefinedLookups.put("pullfromcust", "select c_id , c_name from kbcustomers where c_id !={custidreassign} and c_deleted='N'");
		userDefinedLookups.put("pushtocust", "select c_id , c_name from kbcustomers");
		
		userDefinedReadOnlyNewCols.add("pushtocust");
		userDefinedNewColsDefualtValues.put("pushtocust", new String [] {"{custidreassign}"});
		
		userDefinedColsMustFill.add("pullfromcust");
		userDefinedColsMustFill.add("pushtocust");
		
		displayMode = "NEWSINGLE";
		
		newCaption = "دمج شنات زبائن";
	}
	
	@Override
	public String doInsert(HttpServletRequest rqs , boolean autoCommit){
		String statusMsg = "Cases moved";
		PreparedStatement pst = null;
		ResultSet rs = null;
		String fromCust= rqs.getParameter("pullfromcust");
		String toCust= rqs.getParameter("pushtocust");
		String newHP1 ="", newHP2 = "";
		try{
			pst = conn.prepareStatement("select c_phone1 , c_phone2 from kbcustomers where c_id=? and c_deleted='N'");
			pst.setString(1,toCust);
			rs = pst.executeQuery();
			if (rs.next()) {
				newHP1 = rs.getString("c_phone1");
				newHP2 = rs.getString("c_phone2");
			}
			try{rs.close();}catch(Exception e){/* ignore*/}
			try{pst.close();}catch(Exception e){/* ignore*/}
			
			pst = conn.prepareStatement("update p_cases set c_custid=? , c_custhp=? where c_custid = ? ");
			pst.setString(1, toCust);
			pst.setString(2, newHP1);
			pst.setString(3, fromCust);
			pst.executeUpdate();
			try{pst.close();}catch(Exception e){/* ignore*/}
			
			
			
			pst = conn.prepareStatement("update p_customer_payments set cp_custid=? where cp_custid = ? ");
			pst.setString(1, toCust);
			pst.setString(2, fromCust);
			pst.executeUpdate();
			try{pst.close();}catch(Exception e){/* ignore*/}
			
			pst = conn.prepareStatement("delete from kbcustomers where c_id=?");
			pst.setString(1, fromCust);
			pst.executeUpdate();
			conn.commit();
		}catch(Exception e){
			/*Log Error*/
			statusMsg = "Error at moving cases, "+e.getMessage();
			e.printStackTrace();
			try{conn.rollback();}catch(Exception eRoll){/*ignore*/}
		}finally{
			try{rs.close();}catch(Exception e){/* ignore*/}
			try{pst.close();}catch(Exception e){/* ignore*/}
		}
		return statusMsg;
	}
}
