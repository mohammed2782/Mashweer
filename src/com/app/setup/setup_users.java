package com.app.setup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.app.core.CoreMgr;
import com.app.core.smartyLogAndErrorHandling;
import com.app.db.mysql;

public class setup_users extends CoreMgr{
	public setup_users(){
		MainSql = "select '' as changeprofit, '' as prtnershare, us_hp,'' as customer, '' as  popup, us_id,us_createddt, us_loginid, us_password, us_active, us_p_b4_enc, us_rank ,us_to_state as stt,"
				+ "  us_to_state, us_name, ifnull(us_storecode,'') as us_storecode from kbusers";

		mainTable = "kbusers";
		keyCol   = "us_id";
		
		canNew = true;
		canFilter =  true;
		canEdit = true;
		canDelete = true;

	   userDefinedCaption = "إعدادات المستخدم";
	   newCaption = "إضافة بيانات المستخدم";
	   updCaption = "تعديل بيانات المستخدم";
				
		
		userDefinedGridCols.add("us_loginid");
		userDefinedGridCols.add("us_name");
		userDefinedGridCols.add("us_rank");
		userDefinedGridCols.add("us_active");
		userDefinedGridCols.add("us_hp");
		userDefinedGridCols.add("us_to_state");
		//userDefinedGridCols.add("us_storecode");
		userDefinedGridCols.add("popup");
		userDefinedGridCols.add("prtnershare");
		userDefinedGridCols.add("changeprofit");
		//userDefinedGridCols.add("customer");
		userDefinedGridCols.add("us_createddt");
		//userDefinedGridCols.add("stt");
		
		userDefinedColLabel.put("us_loginid", "user id");
		userDefinedColLabel.put("us_name", "إسم المستخدم");
		
		userDefinedColLabel.put("us_from_state", "من محافظه");
		userDefinedColLabel.put("popup", " ");
		userDefinedColLabel.put("prtnershare", " ");
		userDefinedColLabel.put("changeprofit", "تعديل الارباح باٌثر رجعي");
		userDefinedColLabel.put("us_to_state", "إلى محافظه");
		userDefinedColLabel.put("us_password", "كلمة المرور");
		userDefinedColLabel.put("us_storecode", "يعمل في مخزن");
		userDefinedColLabel.put("us_rank", "إسم المرتبة");
		userDefinedColLabel.put("us_active", "نشط");
		userDefinedColLabel.put("us_createddt", "تاريخ الإنشاء");
		userDefinedColLabel.put("us_lastlogindt", "تاريخ أخر دخول");
		userDefinedColLabel.put("us_hp", "هاتف");
		//userDefinedColLabel.put("customer", " ");
		

		userDefinedNewCols.add("us_loginid");
		userDefinedNewCols.add("us_name");
		userDefinedNewCols.add("us_password");
		userDefinedNewCols.add("us_rank");
		userDefinedNewCols.add("us_hp");
		userDefinedNewCols.add("us_active");
		//userDefinedNewCols.add("us_storecode");
		//userDefinedNewCols.add("us_from_state");
		userDefinedNewCols.add("us_to_state");
		
		userDefinedNewColsHtmlType.put("us_password" , "PASSWORD");
		
		userDefinedColsMustFill.add("us_loginid");
		userDefinedColsMustFill.add("us_name");
		userDefinedColsMustFill.add("us_password");
		userDefinedColsMustFill.add("us_rank");
		userDefinedColsMustFill.add("us_active");
		
		userDefinedFilterCols.add("us_name");
		userDefinedFilterColsHtmlType.put("us_name", "DROPLIST");
		userDefinedFilterLookups.put("us_name", "select us_name, us_name from kbusers");
		userDefinedFilterCols.add("us_rank");
		userDefinedFilterColsHtmlType.put("us_rank", "DROPLIST");
		
		userDefinedEditCols.add("us_loginid");
		userDefinedEditCols.add("us_name");
		userDefinedEditCols.add("us_password");
		userDefinedEditCols.add("us_rank");
		userDefinedEditCols.add("us_hp");
		userDefinedEditCols.add("us_active");
		
		//userDefinedEditCols.add("us_from_state");
		userDefinedEditCols.add("us_to_state");
		//userDefinedEditCols.add("us_storecode");
		//userDefinedEditColsHtmlType.put("us_password" , "PASSWORD");
		
		userDefinedLookups.put("us_active", "select 'Y' , 'Yes' from dual union select 'N' , 'No' from dual ");
		//userDefinedLookups.put("us_from_state", "select st_code, st_name_ar from kbstate ");
		userDefinedLookups.put("us_to_state", "select st_code, st_name_ar FROM kbstate");
		//userDefinedLookups.put("us_storecode", "select store_code, store_name from kbstores");
		userDefinedLookups.put("us_rank", "select rank_code ,rank_name_ar From kbrank order by rank_level desc" );
		userModifyTD.put("popup", "showLinkPopup({us_active},{us_rank},{us_id})");
		userModifyTD.put("prtnershare", "partnerShare({us_active},{us_rank},{us_id})");
		 userModifyTD.put("changeprofit", "changeProfitAndPartnerShareBackDated({us_id},{us_active},{us_rank})");
		userDefinedNewColsHtmlType.put("us_to_state", "CHECKBOX");
	}
	public String changeProfitAndPartnerShareBackDated(HashMap<String,String> hashy) {
		String html = "";
		html = "<td>";
		if (hashy.get("us_active").equalsIgnoreCase("Y") && hashy.get("us_rank").equalsIgnoreCase("PICKUPAGENT"))
			html +="<button type=\"button\" class=\"btn btn-xs btn-dark\" onclick=\"changeProfitAndPartnerShareBackDated("+hashy.get("us_id")+");\">تعديل الارباح بأثر رجعي</button>";
		html +="</td>";
		return html;
	}
	public String partnerShare(HashMap<String,String>hashy) {
		String html = "<td></td>";
		if (hashy.get("us_active").equalsIgnoreCase("Y") && hashy.get("us_rank").equalsIgnoreCase("PICKUPAGENT")) {
				html = "<td>";
				html +="<button type=\"button\" class=\"btn btn-xs btn-danger\" onclick=\"popitup ('partnerPopUp.jsp?partnerid="+hashy.get("us_id")+"' , '' ,  1000 ,600);\">حصة الشريك</button>";
				html +="</td>";
				return html;
		}else {
			return html;
		}
		
	}
	
	public String showLinkPopup(HashMap<String,String>hashy) {
		String html = "<td></td>";
		if (hashy.get("us_active").equalsIgnoreCase("N") || !hashy.get("us_rank").equalsIgnoreCase("DLVAGENT"))
			if (hashy.get("us_rank").equalsIgnoreCase("PICKUPAGENT")) {
				html = "<td>";
				html +="<button type=\"button\" class=\"btn btn-xs btn-info\" onclick=\"popitup ('agentCustomersPopUp.jsp?customersusid="+hashy.get("us_id")+"' , '' , 1000 ,600);\">زبائن</button>";
				html +="</td>";
				return html;
			}else {
				return html;
			}
		else {
			html = "<td>";
			html +="<button type=\"button\" class=\"btn btn-xs btn-warning\" onclick=\"popitup ('agentDistrictPopUp.jsp?districtsusid="+hashy.get("us_id")+"' , '' , 1000 ,600);\">مناطق</button>";

			html +="<button style='display:block;margin-top:5px;' type=\"button\" class=\"btn btn-xs btn-primary\" onclick=\"changeAgentShareBackDated("+hashy.get("us_id")+");\">تعديل اجرة المندوب بأثر رجعي</button>";
			html +="</td>";
			return html;
		}
	}
	
	@Override
	public String doUpdate(HttpServletRequest rqs , boolean autoCommit){
		String statusMsg= "User Updated";
		//System.out.println(rqs.getParameter("us_to_state"));
		String keyCol = parseUpdateRqs(rqs);
		PreparedStatement pst = null;
		ResultSet rs = null;
		String oldPassword="" , newPassword_MD5="";
		//System.out.println(inputMap_ori);
		String newPassword=inputMap_ori.get("us_password")[0];
		try{
			pst = conn.prepareStatement("select us_password , MD5(?) as newpass_MD5 from kbusers where us_id=?");
			pst.setString(1, newPassword);
			pst.setString(2, keyCol);
			rs = pst.executeQuery();
			while(rs.next()){
				oldPassword = rs.getString("us_password");
				newPassword_MD5 = rs.getString("newpass_MD5");
			}
			if (!oldPassword.equals(newPassword)){
				newPassword = newPassword_MD5;
			}else{
				newPassword = oldPassword;
			}
			try{rs.close();}catch(Exception e){/* ignore*/}
			try{pst.close();}catch(Exception e){/* ignore*/}
			String states = "";
			if (inputMap_ori.containsKey("us_to_state") && inputMap_ori.get("us_to_state") !=null ) {
				for (int i = 0; i<inputMap_ori.get("us_to_state").length; i++)
					states +=inputMap_ori.get("us_to_state")[i]+":";
			}
			pst = conn.prepareStatement("update kbusers set us_loginid =?, us_password =? , us_active=? , us_p_b4_enc=?, us_rank=? ,"
					+ " us_to_state=? ,  us_name=? , us_hp=? where us_id=?");
			pst.setString(1, inputMap_ori.get("us_loginid")[0]);
			pst.setString(2, newPassword);
			pst.setString(3, inputMap_ori.get("us_active")[0]);
			pst.setString(4, newPassword);
			pst.setString(5, inputMap_ori.get("us_rank")[0]);
			pst.setString(6, states);
			pst.setString(7, inputMap_ori.get("us_name")[0]);
			pst.setString(8, inputMap_ori.get("us_hp")[0]);
			pst.setString(9, keyCol);
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
	@Override
	public String doInsert(HttpServletRequest rqs , boolean autoCommit){
		String statusMsg= "User Created";
		Map <String , String[]> inputMap = filterRequest (rqs);
		PreparedStatement pst = null;
		String states = "";
		if (inputMap.containsKey("us_to_state") && inputMap.get("us_to_state") !=null ) {
			for (int i = 0; i<inputMap.get("us_to_state").length; i++)
				states +=inputMap.get("us_to_state")[i]+":";
		}
		try{
			pst = conn.prepareStatement("insert into kbusers (us_loginid, us_password, us_active, us_p_b4_enc, us_rank , us_to_state, us_name , us_hp) "
					+ " values (?, MD5(?), ?, ?, ? , ? , ?, ? )");
			pst.setString(1, inputMap.get("us_loginid")[0]);
			pst.setString(2, inputMap.get("us_password")[0]);
			pst.setString(3, inputMap.get("us_active")[0]);
			pst.setString(4, inputMap.get("us_password")[0]);
			pst.setString(5, inputMap.get("us_rank")[0]);
			pst.setString(6,states);
			pst.setString(7, inputMap.get("us_name")[0]);
			pst.setString(8, inputMap.get("us_hp")[0]);
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
	public String doDelete(HttpServletRequest rqs) {
		PreparedStatement pst = null;
		String userid = replaceVarsinString("{useridlogin}", arrayGlobals).trim();
		String usId= rqs.getParameter(keyCol);
		boolean creatRecordDelTabel = false;
		try {
			pst = conn.prepareStatement("insert into kbusers_deleted "
									+ "		select * , ?, now() from kbusers where kbusers.us_id = ?");
			pst.setString(1, userid);
			pst.setString(2, usId);
			pst.executeUpdate();
			conn.commit();
			creatRecordDelTabel = true;
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{pst.close();}catch(Exception e){/*ignore*/}
		}
		if(creatRecordDelTabel)
			return super.doDelete(rqs);
		else
			return "Error";
	}
}
