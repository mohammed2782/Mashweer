package com.app.setup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.app.core.CoreMgr;
import com.app.db.mysql;
import com.mysql.jdbc.Statement;
import org.apache.commons.lang3.StringUtils;
public class CustomerBooks extends CoreMgr{
	boolean firstBook = true;
	public CustomerBooks () {
		MainSql = "select p_books.* , '' as printPDF , '' as del_edit, '' as showranges from p_books order by b_id desc";
		canNew = true;
		//canDelete = true;
		canFilter = true;
		//canEdit = true;
		
		mainTable = "p_books";
		keyCol = "b_id";
		
		userDefinedNewCols.add("b_bookbranch");
		userDefinedNewCols.add("b_noofrcp");
		userDefinedNewCols.add("b_rmk");
		
		userDefinedGridCols.add("b_id");
		userDefinedGridCols.add("b_bookbranch");
		userDefinedGridCols.add("b_usedinsystem");
		userDefinedGridCols.add("b_noofrcp");
		userDefinedGridCols.add("b_createddt");
		userDefinedGridCols.add("b_createdby");
		userDefinedGridCols.add("b_rmk");
		userDefinedGridCols.add("showranges");
		userDefinedGridCols.add("printPDF");
		userDefinedGridCols.add("del_edit");
		
		userDefinedColLabel.put("b_custid", "الزبون");
		userDefinedColLabel.put("b_bookbranch","الفرع");
		userDefinedColLabel.put("b_id","رقم الدفتر");
		userDefinedColLabel.put("b_noofrcp","عدد الإيصالات");
		userDefinedColLabel.put("b_createddt","تاريخ الخلق");
		userDefinedColLabel.put("b_createdby","المستخدم");
		userDefinedColLabel.put("b_rmk","ملاحظات");
		userDefinedColLabel.put("printPDF","طباعه");
		userDefinedColLabel.put("b_usedinsystem","تم إستخدامه في النظام؟");
		userDefinedColLabel.put("showranges", "التقسيمات");
		//userDefinedEditCols.add("b_custid");
		userDefinedEditCols.add("b_rmk");
		
		
		
		userDefinedColsMustFill.add("b_noofrcp");
		userDefinedColsMustFill.add("b_bookbranch");
		userDefinedLookups.put("b_custid", "select c_id , c_name from kbcustomers");
		userDefinedLookups.put("b_bookbranch", "select kbcode , kbdesc from kbgeneral where kbcat1='RCPBOOKS' and kbcat2='BRANCH'");
		userDefinedLookups.put("b_usedinsystem", "select kbcode , kbdesc from kbgeneral where kbcat1='YESNO'");
		myhtmlmgr.refreshPageOnDelete = true;
		
		userModifyTD.put("printPDF", "printReceiptsBook({b_id}, {totused})");
		userDefinedCaption ="دفاتر الوصولات";
		
		userDefinedFilterCols.add("b_custid");
		userDefinedFilterCols.add("b_id");
		userDefinedFilterCols.add("b_usedinsystem");
		
		userModifyTD.put("del_edit","showEditDeleteBtn({b_id},{b_usedinsystem})");
		userModifyTD.put("showranges","showRangesPopUp({b_id})");
		myhtmlmgr.refreshPageOnDelete = true;
	}
	public String printReceiptsBook(HashMap<String, String> hashy) {
		String btn = "<a href=\"../TLKPrintCustomerRcpBookSRVL?book_id="+hashy.get("b_id")+"\" "
				+ " class='btn btn-xs btn-success' > طباعة دفتر الأيصالات<i class=\"fa fa-print fa-lg\"></i></a>";
		return "<td>" + btn + "</td>";
	}
	
	public String showRangesPopUp(HashMap<String, String> hashy) {
		String html = "";
		html = "<td>";
		html +="<button type=\"button\" class=\"btn btn-xs btn-info\" onclick=\"popitup ('assignReceiptsPopUp.jsp?bookid="+hashy.get("b_id")+"' , '' , 1000 ,600);\">تقسيمات</button>";
		html +="</td>";
		return html;
	}
	
	public String showEditDeleteBtn(HashMap<String,String> hashy ) {
		String s="<td align=\"center\">";
		if (hashy.get("b_usedinsystem").equalsIgnoreCase("N") && firstBook) {
			s +="<button type='button' onclick=\"link=false; var rs =doDeleteSmarty(this,'هل تريد حذف هذ ا الدفتر؟' ,'b_id','"+hashy.get("b_id")+"' , 'com.app.setup.CustomerBooks' ); return rs;\" "
					+ " class='btn btn-danger btn-xs'><li class='fa fa-trash'></li></button>";
			firstBook = false;
		}
		return s+"</td>";
	}

	
	 @Override 
	 public String doInsert (HttpServletRequest rqs , boolean commit) {
		 Connection conn = null;
		 PreparedStatement pst = null;
		 ResultSet rs = null;
		 String userId = replaceVarsinString(" {useridlogin} ", arrayGlobals).trim();
		 try {
			 conn = mysql.getConn();
			 pst = conn.prepareStatement("insert into p_books (b_noofrcp, b_createdby, b_rmk) values ( ? , ? , ? )", 
					 Statement.RETURN_GENERATED_KEYS);
			 pst.setString(1, rqs.getParameter("b_noofrcp"));
			 pst.setString(2, userId);
			 pst.setString(3, rqs.getParameter("b_rmk"));
			 pst.executeUpdate();
			 rs = pst.getGeneratedKeys();
			 rs.next();
			 int bookid= rs.getInt(1);
			 
			 try {rs.close();}catch(Exception e) {/**/}
			 try {pst.close();}catch(Exception e) {/**/}
			 
			 pst = conn.prepareStatement("select max(br_rcp_no) from p_books_rcp");
			 rs = pst.executeQuery();
			 int currentMaxRcpNo = 1;
			 if (rs.next()) {
				 currentMaxRcpNo = rs.getInt(1);
			 }
			 try {rs.close();}catch(Exception e) {/**/}
			 try {pst.close();}catch(Exception e) {/**/}
			 
			 int noOfRcp = Integer.parseInt(rqs.getParameter("b_noofrcp"));
			 pst = conn.prepareStatement("insert into p_books_rcp (br_bid,br_rcp_no) values (?,?)");
			 for (int i=(currentMaxRcpNo+1) ; i <=(noOfRcp+currentMaxRcpNo); i++) {
				 pst.setInt(1, bookid );
				 pst.setInt(2, i);
				 pst.executeUpdate();
				 pst.clearParameters();
			 }
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
	 public String doUpdate (HttpServletRequest rqs , boolean commit) {
		 Connection conn = null;
		 PreparedStatement pst = null;
		 ResultSet rs = null;
		 String userId = replaceVarsinString(" {useridlogin} ", arrayGlobals).trim();
		 try {
			 conn = mysql.getConn();
			
			
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
			try {
				
				pst = conn.prepareStatement("delete from p_books_rcp where br_bid=?");
				pst.setString(1, keyVal);
				pst.executeUpdate();
				try {pst.close();} catch (Exception e) {}
				
				// second update the cases back to NO, and c_pmtid = 0
				pst = conn.prepareStatement("delete from p_books where b_id=?");
				pst.setString(1, keyVal);
				pst.executeUpdate();
				try {pst.close();} catch (Exception e) {}
				
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
