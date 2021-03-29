/* class description: used to setup customers informations,
 * created by: lina - SMARTYJ FrameWork team member,
 * created date: 22/4/2018 7:26 AM.
 */
package com.app.setup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.app.core.CoreMgr;
import com.app.db.mysql;

public class setup_customers extends CoreMgr {
	public setup_customers(){		

		/*
		 * to define main sql statement, key column and main table that do this operations
		 */	
		MainSql ="select kbcustomers.* , '' as popUpBook, '' as specialprice , '' as movecasestocustomer From kbcustomers where (c_belongtostore='{userstorecode}' or '{superRank}'='Y')";
		mainTable ="kbcustomers";
		keyCol = "c_id";
		orderByCols = "c_id";
		
		/*
		 * to define user grid view caption
		 */
		userDefinedCaption = "إعدادت الزبائن";
		newCaption = "إضافة بيانات الزبائن";
		updCaption = "تعديل بيانات الزبائن";
		
		/*
		 * to enable/disable basic operations 
		 */		
		search_paramval = null;
		canNew = true;
		canFilter = true;
		canEdit = true;
		canDelete = true;
		//clickableRow =true;

		/*
		 * to define grid view columns that want to show to user
		 */
		userDefinedGridCols.add("c_name");
		userDefinedGridCols.add("c_phone1");
		userDefinedGridCols.add("c_phone2");
		userDefinedGridCols.add("popUpBook");
		//userDefinedGridCols.add("c_discount_bgd");
		userDefinedGridCols.add("specialprice");
		//userDefinedGridCols.add("c_discount_otherstates");
		//userDefinedGridCols.add("c_belongtostore");
		userDefinedGridCols.add("c_createdby");
		userDefinedGridCols.add("cust_createddt");
		userDefinedGridCols.add("c_loginid");
		userDefinedGridCols.add("c_pwdb4enc");
		userDefinedGridCols.add("movecasestocustomer");
		userDefinedGridCols.add("c_assigned_pickup_agent");
		
		//userDefinedEditColsHtmlType.put("c_pwd", "PASSWORD");

		/*
		 * to define grid view label that want to show to user
		 */
		userDefinedColLabel.put("c_name", "إسم الزبون");
		userDefinedColLabel.put("movecasestocustomer", "دمج");
	    userDefinedColLabel.put("c_phone1", "رقم الهاتف");
	    userDefinedColLabel.put("c_phone2", "رقم هاتف أخر");
	    userDefinedColLabel.put("c_createdby", "أنشئ بواسطة");
	    userDefinedColLabel.put("cust_createddt", "تاريخ الإنشاء");
	    userDefinedColLabel.put("c_belongtostore", "تابع لمخزن");
	    userDefinedColLabel.put("c_discount_bgd", "تخفيض (بغداد)");
	    userDefinedColLabel.put("c_discount_otherstates", "تخفيض محافظات");
	    userDefinedColLabel.put("popUpBook", "طباعة فواتير");
	    userDefinedColLabel.put("c_loginid", "إسم المستخدم");
	    userDefinedColLabel.put("c_pwd", "كلمة السر");
	    userDefinedColLabel.put("c_pwdb4enc", "كلمة السر");
	    userDefinedColLabel.put("specialprice", "إسعار خاصه");
	    userDefinedColLabel.put("c_assigned_pickup_agent", "مندوب استلام");
	    
	    
	    userModifyTD.put("popUpBook", "ShowBooks({c_id})");
	    userModifyTD.put("specialprice", "ShowSpecialPricesList({c_id})");
	    userModifyTD.put("movecasestocustomer", "ShowMoveCasesPopUp({c_id})");
	    
	    /*
		 * to define user must fill columns 
		 */
	    userDefinedColsMustFill.add("c_name");
	    userDefinedColsMustFill.add("c_phone1");
	    userDefinedColsMustFill.add("c_pwd");
	    userDefinedColsMustFill.add("c_loginid");
	    
	    /*
		 * to define new columns for insert operation
		 */ 
	    //userDefinedNewCols.add("c_belongtostore");
		userDefinedNewCols.add("c_name");
	    userDefinedNewCols.add("c_phone1");
	    userDefinedNewCols.add("c_phone2");
	    userDefinedNewCols.add("c_assigned_pickup_agent");
	    //userDefinedNewCols.add("c_discount_bgd");
	    //userDefinedNewCols.add("c_discount_otherstates");
	    userDefinedNewCols.add("c_createdby");
	    userDefinedReadOnlyNewCols.add("c_createdby");
		userDefinedNewColsDefualtValues.put("c_createdby", new String[] {"{useridlogin}"});
		userDefinedNewColsDefualtValues.put("c_belongtostore", new String[] {"{userstorecode}"});
        userDefinedReadOnlyNewCols.add("c_belongtostore");
        
	    /*
		 * to define filter columns for search operation
		 */
		userDefinedFilterCols.add("c_name");
		userDefinedFilterLookups.put("c_name", "select c_name , c_name from kbcustomers");
		userDefinedLookups.put("c_assigned_pickup_agent", "SELECT us_id, us_name FROM kbusers where us_rank = 'PICKUPAGENT' and us_active = 'Y'");
		userDefinedFilterColsHtmlType.put("c_name", "DROPLIST");
		userDefinedFilterCols.add("c_phone1");
		userDefinedFilterCols.add("c_phone2");
		userDefinedFilterCols.add("c_assigned_pickup_agent");
		
		/*
		 * to define edit columns for update operation
		 */	
		userDefinedEditCols.add("c_name");
		userDefinedEditCols.add("c_phone1");
		userDefinedEditCols.add("c_phone2");
		//userDefinedEditCols.add("c_discount_bgd");
		//userDefinedEditCols.add("c_discount_otherstates");
		userDefinedEditCols.add("c_loginid");
		userDefinedEditCols.add("c_pwd");
		userDefinedEditCols.add("c_assigned_pickup_agent");

		/*
		 * to pop up sub menu for main menu
		 */
		//userDefinedGlobalClickRowID="c_id";

	}//end of constructor setup_customers
	
	public String ShowMoveCasesPopUp(HashMap<String, String>hashy) {
		String html = "";
		html = "<td>";
		html +="<button type=\"button\" class=\"btn btn-xs btn-info\" onclick=\"popitup ('reassignCustomerCasesPopUp.jsp?custidreassign="+hashy.get("c_id")+"' , '' , 1000 ,600);\">دمج مع زبون</button>";
		html +="</td>";
		return html;
	}
	
	public String ShowBooks(HashMap<String, String>hashy) {
		String html = "";
		
			html = "<td>";
			html +="<button type=\"button\" class=\"btn btn-xs btn-warning\" onclick=\"popitup ('custBookPopUp.jsp?custidbook="+hashy.get("c_id")+"' , '' , 1000 ,600);\">الإيصالات</button>";
			html +="</td>";
			return html;
		
	}
	
	public String ShowSpecialPricesList(HashMap<String, String>hashy) {
		String html = "";
		
			html = "<td>";
			html +="<button type=\"button\" class=\"btn btn-xs btn-danger\" onclick=\"popitup ('specialPricesCustomersPopUp.jsp?custidspecialprice="+hashy.get("c_id")+"' , '' , 1000 ,600);\">إسعار خاصه</button>";
			html +="</td>";
			return html;
		
	}
	
	@Override
	public String doDelete(HttpServletRequest rqs){
		String Msg ="";
		PreparedStatement pst = null;
		ResultSet rs = null;
		String keyVal= rqs.getParameter(keyCol);
		String userid = replaceVarsinString(" {useridlogin} ", arrayGlobals).trim();
		Connection conn = null;
		boolean allowDelete = false;
		try {
			conn = mysql.getConn();
			pst = conn.prepareStatement("select 1 from p_cases where c_custid = ?");
			pst.setString(1, keyVal);
			rs = pst.executeQuery();
			if (rs.next()) 
				allowDelete = false;
			else
				allowDelete = true;
			
			
			try {pst.close();}catch(Exception e) {}
			if (!allowDelete)
				return "هذا الزبون لا يمكن مسحه لأن لديه شحنات";
			else {
				pst = conn.prepareStatement("insert into deleted_customers select kbcustomers.* , ?, now() from kbcustomers where c_id = ?");
				pst.setString(1,userid );
				pst.setString(2,keyVal );
				pst.executeUpdate();
				try {pst.close();}catch(Exception e) {}
				pst = conn.prepareStatement("delete from kbcustomers where c_id = ?");
				pst.setString(1, keyVal);
				pst.executeUpdate();
				conn.commit();
			}
		}catch (Exception e) {
			try{conn.rollback();}catch (Exception eRollBack){eRollBack.printStackTrace();}
			e.printStackTrace();
		    Msg = "Error";
		    deleteErrorFlag = true;
		    
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			try {conn.close();}catch(Exception e) {}
		}
		return Msg;
	}
	
	@Override
	public String doUpdate(HttpServletRequest rqs , boolean autoCommit){
		String statusMsg= "User Updated";
		String keyCol = parseUpdateRqs(rqs);
		PreparedStatement pst = null;
		ResultSet rs = null;
		String oldPassword="" , newPassword_MD5="";
		System.out.println(inputMap_ori);
		String newPassword=inputMap_ori.get("c_pwd")[0];
		String passwordB4Enc = "";
		
		try{
			pst = conn.prepareStatement("select c_pwd , MD5(?) as newpass_MD5, c_pwdb4enc from kbcustomers where c_id=?");
			pst.setString(1, newPassword);
			pst.setString(2, keyCol);
			rs = pst.executeQuery();
			while(rs.next()){
				oldPassword = rs.getString("c_pwd");
				newPassword_MD5 = rs.getString("newpass_MD5");
				passwordB4Enc = rs.getString("c_pwdb4enc");
			}
			if (oldPassword!=null && !oldPassword.trim().equalsIgnoreCase("")) {
				if (!oldPassword.equals(newPassword)){
					newPassword = newPassword_MD5;
					passwordB4Enc = inputMap_ori.get("c_pwd")[0];
				}else{
					newPassword = oldPassword;
				}
			}else {
				passwordB4Enc = inputMap_ori.get("c_pwd")[0];
				newPassword = newPassword_MD5;
			}
				
			try{rs.close();}catch(Exception e){/* ignore*/}
			try{pst.close();}catch(Exception e){/* ignore*/}
			
			pst = conn.prepareStatement("update kbcustomers set c_name =?, c_phone1 =? , c_phone2 =? , c_loginid=? "
					+ " ,c_pwd=?, c_pwdb4enc=?, c_assigned_pickup_agent=? where c_id=?");
			pst.setString(1, inputMap_ori.get("c_name")[0]);
			pst.setString(2, inputMap_ori.get("c_phone1")[0]);
			pst.setString(3, inputMap_ori.get("c_phone2")[0]);
			pst.setString(4, inputMap_ori.get("c_loginid")[0]);
			pst.setString(5, newPassword);
			pst.setString(6, passwordB4Enc);
			pst.setString(7, inputMap_ori.get("c_assigned_pickup_agent")[0]);
			pst.setString(8, keyCol);
			pst.executeUpdate();
			conn.commit();
		}catch(Exception e){
			/*Log Error*/
			statusMsg = "Error at updating User "+e.getMessage();
			e.printStackTrace();
			try{
				conn.rollback();
			}catch(Exception eRoll){
				/*ignore*/
			}
		}finally{
			try{rs.close();}catch(Exception e){/* ignore*/}
			try{pst.close();}catch(Exception e){/* ignore*/}
		}
		return statusMsg;
	}
}//end of class setup_customers