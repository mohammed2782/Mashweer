package com.app.setup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.app.core.CoreMgr;
import com.app.db.mysql;

public class SetupSenderCompanies extends CoreMgr {
	public SetupSenderCompanies(){		

		/*
		 * to define main sql statement, key column and main table that do this operations
		 */	
		MainSql ="select kbcompanies.* , '' as specialprice  From kbcompanies";
		mainTable ="kbcompanies";
		keyCol = "comp_id";
		orderByCols = "comp_id";
		
		/*
		 * to define user grid view caption
		 */
		userDefinedCaption = "إعدادات شركات التوصيل";
		newCaption = "إضافة بيانات شركة توصيل";
		updCaption = "تعديل بيانات شركة توصيل";
		
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
		userDefinedGridCols.add("comp_id");
		userDefinedGridCols.add("comp_name");
		userDefinedGridCols.add("comp_active");
		userDefinedGridCols.add("specialprice");
		userDefinedGridCols.add("comp_createddt");
		userDefinedGridCols.add("comp_createdby");
		userDefinedGridCols.add("comp_pickupagent");
		userDefinedGridCols.add("comp_loginid");
		
		/*
		 * to define grid view label that want to show to user
		 */
		userDefinedColLabel.put("comp_name", "إسم الشركة المرسلة");		
		userDefinedColLabel.put("comp_active", "نشط");		
	    userDefinedColLabel.put("comp_createdby", "أنشئ بواسطة");
	    userDefinedColLabel.put("comp_createddt", "تاريخ الإنشاء");
	    userDefinedColLabel.put("comp_pickupagent", "مندوب الإستلام");
	    userDefinedColLabel.put("specialprice", "إسعار خاصه");
	    userDefinedColLabel.put("comp_loginid", "إسم المستخدم للدخول للنظام");
	    userDefinedColLabel.put("comp_id", "كود الشركة");
	    
	    userModifyTD.put("specialprice", "ShowSpecialPricesList({comp_id})");
	    
	    userDefinedLookups.put("comp_pickupagent", "select us_id , us_name from kbusers where us_rank='PICKUPAGENT'");
	    
	    /*
		 * to define user must fill columns 
		 */
	    userDefinedColsMustFill.add("comp_name");
	    userDefinedColsMustFill.add("comp_createdby");
	    userDefinedColsMustFill.add("comp_pickupagent");
	    userDefinedColsMustFill.add("comp_loginid");
	    userDefinedColsMustFill.add("comp_pwd");
	    /*
		 * to define new columns for insert operation
		 */ 
		userDefinedNewCols.add("comp_name");
		
	    userDefinedNewCols.add("comp_createdby");
	    userDefinedNewCols.add("comp_loginid");
	    userDefinedNewCols.add("comp_pwd");

	    userDefinedReadOnlyNewCols.add("comp_createdby");
		userDefinedNewColsDefualtValues.put("comp_createdby", new String[] {"{useridlogin}"});
		
	    /*
		 * to define filter columns for search operation
		 */
		userDefinedFilterCols.add("comp_name");
		userDefinedFilterLookups.put("comp_name", "select comp_id , comp_name from kbcompanies");
		userDefinedFilterColsHtmlType.put("comp_name", "DROPLIST");
		
		userDefinedLookups.put("comp_active", "select kbcode, kbdesc from kbgeneral where kbcat1='YESNO'");
		/*
		 * to define edit columns for update operation
		 */	
		userDefinedEditCols.add("comp_name");
		userDefinedEditCols.add("comp_pickupagent");
		userDefinedEditCols.add("comp_active");
		userDefinedEditCols.add("comp_loginid");
		userDefinedEditCols.add("comp_pwd");
		/*
		 * to pop up sub menu for main menu
		 */
		//userDefinedGlobalClickRowID="c_id";

	}//end of constructor setup_customers
	
	@Override
	public String doUpdate(HttpServletRequest rqs , boolean autoCommit){
		String statusMsg= "Company Updated";
		
		String keyCol = parseUpdateRqs(rqs);
		PreparedStatement pst = null;
		ResultSet rs = null;
		String oldPassword="" , newPassword_MD5="";
		System.out.println(inputMap_ori);
		String newPassword=inputMap_ori.get("comp_pwd")[0];
		try{
			pst = conn.prepareStatement("select comp_pwd , MD5(?) as newpass_MD5 from kbcompanies where comp_id=?");
			pst.setString(1, newPassword);
			pst.setString(2, keyCol);
			rs = pst.executeQuery();
			while(rs.next()){
				oldPassword = rs.getString("comp_pwd");
				newPassword_MD5 = rs.getString("newpass_MD5");
			}
			
			if (oldPassword==null) {
				newPassword = newPassword_MD5;
			}else {
				if (!oldPassword.equals(newPassword)){
					newPassword = newPassword_MD5;
				}else{
					newPassword = oldPassword;
				}
			}
			try{rs.close();}catch(Exception e){/* ignore*/}
			try{pst.close();}catch(Exception e){/* ignore*/}
			
			
			pst = conn.prepareStatement("update kbcompanies set comp_name =?, comp_pickupagent =? , comp_active=? , comp_loginid=?, comp_pwd=? ,"
					+ " comp_pwdb4=?  where comp_id=?");
			pst.setString(1, inputMap_ori.get("comp_name")[0]);
			pst.setString(2, inputMap_ori.get("comp_pickupagent")[0]);
			pst.setString(3, inputMap_ori.get("comp_active")[0]);
			pst.setString(4, inputMap_ori.get("comp_loginid")[0]);
			pst.setString(5, newPassword);
			pst.setString(6, inputMap_ori.get("comp_pwd")[0]);
			pst.setString(7, keyCol);
			pst.executeUpdate();
			conn.commit();
		}catch(Exception e){
			/*Log Error*/
			statusMsg = "Error at updating Company "+e.getMessage();
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
	@Override
	public String doInsert(HttpServletRequest rqs , boolean autoCommit){
		String statusMsg= "Company Created";
		Map <String , String[]> inputMap = filterRequest (rqs);
		PreparedStatement pst = null;
		
		try{
			pst = conn.prepareStatement("insert into kbcompanies (comp_name, comp_pwd, comp_loginid , comp_pwdb4, comp_createdby) "
					+ " values (?, MD5(?), ?, ? , ? )");
			pst.setString(1, inputMap.get("comp_name")[0]);
			pst.setString(2, inputMap.get("comp_pwd")[0]);
			pst.setString(3, inputMap.get("comp_loginid")[0]);
			pst.setString(4, inputMap.get("comp_pwd")[0]);
			pst.setString(5, inputMap.get("comp_createdby")[0]);
			pst.executeUpdate();
			conn.commit();
		}catch(Exception e){
			/*log error*/
			statusMsg = "Error at Company creation, error ("+e.getMessage()+")";
			try{conn.rollback();}catch(Exception ignoreE){}
			e.printStackTrace();
		}finally{
			try{pst.close();}catch(Exception e){/* ignore*/}
		}
		
		return statusMsg;
	}
	
	public String ShowSpecialPricesList(HashMap<String, String>hashy) {
		String html = "";
		
			html = "<td>";
			html +="<button type=\"button\" class=\"btn btn-xs btn-danger\" "
					+ "onclick=\"popitup ('specialPricesCompainesPopUp.jsp?compidspecialprice="+hashy.get("comp_id")+"' , '' , 1000 ,600);\">إسعار خاصه</button>";
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
			pst = conn.prepareStatement("select 1 from p_cases where c_company_sender = ?");
			pst.setString(1, keyVal);
			rs = pst.executeQuery();
			if (rs.next()) 
				allowDelete = false;
			else
				allowDelete = true;
			
			
			try {pst.close();}catch(Exception e) {}
			if (!allowDelete)
				return "هذه الشركه لا يمكن مسحه لأن لديه شحنات";
			else {
				pst = conn.prepareStatement("delete from kbcompanies where comp_id = ?");
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
	

}//end of class setup_companies
