package com.app.setup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.app.core.CoreMgr;
import com.app.db.mysql;
import com.mysql.jdbc.Statement;

public class AssignBookReceipts extends CoreMgr {
	public AssignBookReceipts () {
		MainSql = "select br_groupid, br_custid, min(br_rcp_no)as startrange, max(br_rcp_no) as torange ,br_bid,'' as checkused "
				+ " from p_books_rcp where br_bid = {bookid} group by br_groupid,br_custid order by min(br_id)";
		userDefinedGridCols.add("br_custid");
		userDefinedGridCols.add("startrange");
		userDefinedGridCols.add("torange");
		canNew = true;
		canDelete = true;
		mainTable = "p_books_rcp";
		keyCol    = "br_groupid";
		userDefinedNewCols.add("br_custid");
		userDefinedNewCols.add("startrange");
		userDefinedNewCols.add("torange");
		
		userDefinedColsMustFill.add("br_custid");
		userDefinedColsMustFill.add("startrange");
		userDefinedColsMustFill.add("torange");
		userDefinedLookups.put("br_custid", "select c_id , c_name from kbcustomers");
		myhtmlmgr.refreshPageOnDelete = true;
		UserDefinedPageRows = 1000;
	}
	 @Override 
	 public String doInsert (HttpServletRequest rqs , boolean commit) {
		 Connection conn = null;
		 PreparedStatement pst = null;
		 ResultSet rs =null;
		 String userId = replaceVarsinString(" {useridlogin} ", arrayGlobals).trim();
		 String bookid = replaceVarsinString(" {bookid} ", arrayGlobals).trim();
		 int maxGroup = 0;
		 try {
			conn = mysql.getConn();
			int noOfCustomerInSingleGroup = 0;
			int customer = 0;
			pst = conn.prepareStatement("select br_custid from p_books_rcp where br_bid=? and br_rcp_no>=? and br_rcp_no<=?   group by br_custid");
			pst.setString(1, bookid);
			pst.setString(2, rqs.getParameter("startrange") );
			pst.setString(3, rqs.getParameter("torange") );
			rs = pst.executeQuery();
			while (rs.next()) {
				noOfCustomerInSingleGroup ++;
				customer = rs.getInt("br_custid");
			}
			
			if (noOfCustomerInSingleGroup>1) {
				return "هذا المدى محجوز لزبون أخر";
			}
			if (customer !=0) {
				return "هذا المدى محجوز لزبون أخر";
			}
			
			maxGroup ++;
			try {rs.close();}catch(Exception e) {/**/}
			try {pst.close();}catch(Exception e) {/**/}
			
			pst = conn.prepareStatement("select max(br_groupid) from p_books_rcp where br_bid=?");
			pst.setString(1, bookid);
			rs = pst.executeQuery();
			if (rs.next())
				maxGroup = rs.getInt(1);
			maxGroup ++;
			try {rs.close();}catch(Exception e) {/**/}
			try {pst.close();}catch(Exception e) {/**/}
			 
			 
			pst = conn.prepareStatement("update p_books_rcp  set br_groupid=?, br_custid=? where br_rcp_no>=? and br_rcp_no<=? and br_bid=?");
			pst.setInt(1, maxGroup );
			pst.setString(2, rqs.getParameter("br_custid") );
			pst.setString(3, rqs.getParameter("startrange") );
			pst.setString(4, rqs.getParameter("torange") );
			pst.setString(5, bookid);
			pst.executeUpdate();
			conn.commit();
			 
		 }catch(Exception e) {
			 try {conn.rollback();}catch(Exception eRoll) {/**/}
			 e.printStackTrace();
		 }finally {
			 try {rs.close();}catch(Exception e) {/**/}
			 try {pst.close();}catch(Exception e) {/**/}
			 try {conn.close();}catch(Exception e) {/**/}
		 }
		 return "";
	 }
	 
 
	 @Override 
	 public String doDelete (HttpServletRequest rqs) {
		 String keyVal = rqs.getParameter(keyCol);
			PreparedStatement pst = null;
			String userId = replaceVarsinString(" {useridlogin} ", arrayGlobals).trim();
			String bookid = replaceVarsinString(" {bookid} ", arrayGlobals).trim();
			try {
				pst = conn.prepareStatement("update p_books_rcp  set br_groupid=0, br_custid=0 where br_bid=? and br_groupid=? and br_cid =0");
				pst.setString(1, bookid);
				pst.setString(2, keyVal );
				pst.executeUpdate();
				conn.commit();
	
			} catch (Exception e) {
				try {conn.rollback();} catch (Exception eRollBack) {/*ignore*/}
				logErrorMsg = "";
				e.printStackTrace();
			} finally {
				try {pst.close();} catch (Exception e) {}
				try {pst.close();} catch (Exception e) {}
			}
	
			return "";
	 }
}
