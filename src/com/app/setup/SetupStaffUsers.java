package com.app.setup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.app.core.CoreMgr;
import com.app.core.smartyLogAndErrorHandling;
import com.app.db.mysql;

public class SetupStaffUsers extends CoreMgr{
	public SetupStaffUsers(){
		MainSql = "select * from kbusers";

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
		userDefinedGridCols.add("us_rank");
		userDefinedGridCols.add("us_active");
		userDefinedGridCols.add("us_from_state");
		userDefinedGridCols.add("us_to_state");
		userDefinedGridCols.add("us_createddt");
		userDefinedGridCols.add("us_lastlogindt");
		
		userDefinedColLabel.put("us_loginid", "إسم المستخدم");
		userDefinedColLabel.put("us_from_state", "من محافظه");
		userDefinedColLabel.put("us_to_state", "إلى محافظه");
		userDefinedColLabel.put("us_password", "كلمة المرور");
		userDefinedColLabel.put("us_rank", "إسم المرتبة");
		userDefinedColLabel.put("us_active", "نشط");
		userDefinedColLabel.put("us_createddt", "تاريخ الإنشاء");
		userDefinedColLabel.put("us_lastlogindt", "تاريخ أخر دخول");

		userDefinedNewCols.add("us_loginid");
		userDefinedNewCols.add("us_password");
		userDefinedNewCols.add("us_rank");
		userDefinedNewCols.add("us_active");
		userDefinedNewCols.add("us_from_state");
		userDefinedNewCols.add("us_to_state");
		userDefinedNewColsHtmlType.put("us_password" , "PASSWORD");
		
		userDefinedColsMustFill.add("us_loginid");
		userDefinedColsMustFill.add("us_password");
		userDefinedColsMustFill.add("us_rank");
		userDefinedColsMustFill.add("us_active");
		
		userDefinedFilterCols.add("us_loginid");
		
		userDefinedEditCols.add("us_loginid");
		userDefinedEditCols.add("us_password");
		userDefinedEditCols.add("us_rank");
		userDefinedEditCols.add("us_active");
		userDefinedEditCols.add("us_from_state");
		userDefinedEditCols.add("us_to_state");
		//userDefinedEditColsHtmlType.put("us_password" , "PASSWORD");
		
		userDefinedLookups.put("us_active", "select 'Y' , 'Yes' from dual union select 'N' , 'No' from dual ");
		userDefinedLookups.put("us_from_state", "select st_code, st_name_ar from kbstate ");
		userDefinedLookups.put("us_to_state", "select st_code, st_name_ar from kbstate");
		userDefinedLookups.put("us_rank", "select r_id,r_desc From kbranks order by r_level desc" );
		
	}
	@Override
	public String doUpdate(HttpServletRequest rqs , boolean autoCommit){
		String statusMsg= "User Updated";
		String keyCol = parseUpdateRqs(rqs);
		PreparedStatement pst = null;
		ResultSet rs = null;
		String oldPassword="" , newPassword_MD5="";
		System.out.println(inputMap_ori);
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
			
			pst = conn.prepareStatement("update kbusers set us_loginid =?, us_password =? , us_active=? , us_p_b4_enc=?, us_rank=? ,us_from_state=?,"
					+ " us_to_state=? where us_id=?");
			pst.setString(1, inputMap_ori.get("us_loginid")[0]);
			pst.setString(2, newPassword);
			pst.setString(3, inputMap_ori.get("us_active")[0]);
			pst.setString(4, newPassword);
			pst.setString(5, inputMap_ori.get("us_rank")[0]);
			pst.setString(6, inputMap_ori.get("us_from_state")[0]);
			pst.setString(7, inputMap_ori.get("us_to_state")[0]);
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
	@Override
	public String doInsert(HttpServletRequest rqs , boolean autoCommit){
		String statusMsg= "User Created";
		Map <String , String[]> inputMap = filterRequest (rqs);
		PreparedStatement pst = null;
		try{
			pst = conn.prepareStatement("insert into kbusers (us_loginid, us_password, us_active, us_p_b4_enc, us_rank , us_from_state, us_to_state) "
					+ " values (?, MD5(?), ?, ?, ? , ? , ?)");
			pst.setString(1, inputMap.get("us_loginid")[0]);
			pst.setString(2, inputMap.get("us_password")[0]);
			pst.setString(3, inputMap.get("us_active")[0]);
			pst.setString(4, inputMap.get("us_password")[0]);
			pst.setString(5, inputMap.get("us_rank")[0]);
			pst.setString(6, inputMap.get("us_from_state")[0]);
			pst.setString(7, inputMap.get("us_to_state")[0]);
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
