package com.app.setup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import com.app.core.CoreMgr;
import com.app.db.mysql;
import com.app.util.Utilities;

public class CustomersSpecialPrices extends CoreMgr {
	public CustomersSpecialPrices() {
		MainSql = "select  *  from kb_special_prices where sp_custid='{custidspecialprice}'";
		canNew = true;
		canDelete = true;
		
		mainTable = "kb_special_prices";
		keyCol = "sp_id";
		
		//userDefinedNewCols.add("sp_custid");
		userDefinedNewCols.add("sp_statecode");
		userDefinedNewCols.add("sp_price");
		userDefinedNewCols.add("sp_rural_price");
		userDefinedNewCols.add("sp_price_share");
		userDefinedNewColsDefualtValues.put("sp_price_share", new String[] {"0"});
		userDefinedNewCols.add("sp_rural_share");
		userDefinedNewColsDefualtValues.put("sp_rural_share", new String[] {"0"});
		
		userDefinedGridCols.add("sp_statecode");
		userDefinedGridCols.add("sp_price");
		userDefinedGridCols.add("sp_rural_price");
		userDefinedGridCols.add("sp_price_share");
		userDefinedGridCols.add("sp_rural_share");
		
		
		userDefinedColLabel.put("sp_statecode","المحافظة");
		userDefinedColLabel.put("sp_price","مبلغ الشحن");
		userDefinedColLabel.put("sp_rural_price","مبلغ الشحن للأقضيه");
		userDefinedColLabel.put("sp_price_share","حصة  الشريك للمركز");
		userDefinedColLabel.put("sp_rural_share","حصة الشريك للاطراف");
		
		userDefinedColsMustFill.add("sp_statecode");
		userDefinedColsMustFill.add("sp_price");
		userDefinedColsMustFill.add("sp_rural_price");
		userDefinedNewColsDefualtValues.put("sp_custid", new String[] {"{custidspecialprice}"});
		myhtmlmgr.refreshPageOnDelete = true;
		userDefinedLookups.put("sp_statecode", "select st_code, st_name_ar from kbstate");
	}
	
	@Override
	public void initialize(HashMap smartyStateMap){
		String custId = replaceVarsinString(" {custidspecialprice} ", arrayGlobals).trim();
		userDefinedNewLookups.put("sp_statecode", "select st_code, st_name_ar from kbstate where st_code not in "
				+ " (select sp_statecode from kb_special_prices where sp_custid='"+custId+"')");
		super.initialize(smartyStateMap);
	}
	
	 @Override 
	 public String doInsert (HttpServletRequest rqs , boolean commit) {
		 Connection conn = null;
		 PreparedStatement pst = null;
		 String custId = replaceVarsinString(" {custidspecialprice} ", arrayGlobals).trim();
		 String userId = replaceVarsinString(" {useridlogin} ", arrayGlobals).trim();
		 try {
			 conn = mysql.getConn();
			 pst = conn.prepareStatement("insert into kb_special_prices (sp_custid , sp_statecode, sp_price, sp_rural_price, sp_price_share, sp_rural_share, sp_createdby) "
			 		+ " values (? , ? , ? , ?, ?, ?, ? )");
			 pst.setString(1, custId);
			 pst.setString(2, rqs.getParameter("sp_statecode"));
			 pst.setString(3, rqs.getParameter("sp_price"));
			 pst.setString(4, rqs.getParameter("sp_rural_price"));
			 pst.setString(5, rqs.getParameter("sp_price_share"));
			 pst.setString(6, rqs.getParameter("sp_rural_share"));
			 pst.setString(7, userId);
			 pst.executeUpdate();
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
	 
	 @Override 
	 public String doDelete (HttpServletRequest rqs) {
		 String keyVal = rqs.getParameter(keyCol);
		 PreparedStatement pst = null;
		 try {
			pst = conn.prepareStatement("delete from kb_special_prices where sp_id=?");
			pst.setString(1, keyVal);
			pst.executeUpdate();
			try {pst.close();} catch (Exception e) {}
			conn.commit();
		 }catch (Exception e) {
			try {conn.rollback();} catch (Exception eRollBack) {/*ignore*/}
			logErrorMsg = "";
			e.printStackTrace();
		}finally {
			try {pst.close();} catch (Exception e) {}
		}
		 return ""; 
	}
}
