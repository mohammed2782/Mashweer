package com.app.setup;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;


import com.app.core.CoreMgr;

public class Partners extends CoreMgr{
	public Partners() {
		MainSql = "select kbpartner_share.* from kbpartner_share where ps_userid = '{partnerid}'";
		mainTable = "kbpartner_share";
		keyCol = "ps_id";
		
		canNew = true;
		canDelete = true;
		
		//userDefinedGridCols.add("ps_userid");
		userDefinedGridCols.add("ps_compid");
		userDefinedGridCols.add("ps_share_center");
		userDefinedGridCols.add("ps_share_rural");
		
		
		userDefinedColLabel.put("ps_userid", "اسم الشريك");
		userDefinedColLabel.put("ps_compid", "اسم الشركة المرسلة");
		userDefinedColLabel.put("ps_share_center", "نسبة السنتر %");
		userDefinedColLabel.put("ps_share_rural", "نسبة الاطراف %");
		
		userDefinedNewCols.add("ps_userid");
		userDefinedNewColsDefualtValues.put("ps_userid", new String[] {"%select us_id from kbusers where us_id = '{partnerid}'"});
		userDefinedReadOnlyNewCols.add("ps_userid");
		userDefinedNewCols.add("ps_compid");
		userDefinedNewCols.add("ps_share_center");
		userDefinedNewCols.add("ps_share_rural");
		userDefinedNewCols.add("ps_createdby");
		userDefinedNewColsDefualtValues.put("ps_createdby", new String[] {"{useridlogin}"});
		userDefinedReadOnlyNewCols.add("ps_createdby");
		userDefinedHiddenNewCols.add("ps_createdby");
		
		//userDefinedEditCols.add("ps_name");
		//userDefinedEditCols.add("ps_partner_share");
		//userDefinedEditCols.add("ps_createdby");
		//userDefinedEditColsDefualtValues.put("ps_createdby",new String[] {"{useridlogin}"} );
		//userDefinedReadOnlyEditCols.add("ps_createdby");
		
		//userDefinedFilterCols.add("ps_userid");
		//userDefinedFilterCols.add("ps_compid");
		//userDefinedFilterColsHtmlType.put("ps_userid", "DROPLIST");
		//userDefinedFilterColsHtmlType.put("ps_compid", "DROPLIST");
		
		userDefinedLookups.put("ps_userid", "select us_id, us_name from kbusers where us_id = '{partnerid}' ");
		userDefinedLookups.put("ps_compid", " select comp_id,comp_name from kbcompanies where comp_pickupagent = '{partnerid}'");

		userDefinedColsMustFill.add("ps_userid");
		userDefinedColsMustFill.add("ps_compid");
		userDefinedColsMustFill.add("ps_share_center");
		userDefinedColsMustFill.add("ps_share_rural");
		
		userDefinedNewColsHtmlType.put("ps_share_center", "TEXT");
		userDefinedNewColsHtmlType.put("ps_share_rural", "TEXT");
		

		userDefinedCaption = "حصة الشريك";
		newCaption = "اضافة نسبة شراكة لشركة جديدة";
		
	}
	@Override
	public String doInsert(HttpServletRequest rqs , boolean autoCommit) {
		String Msg = "";
	    inputMap_ori = filterRequest(rqs);
	    PreparedStatement pst = null;
	    ResultSet rs = null;
	    boolean foundSameRecord = false;
	    double shareCenter = 0;
	    double shareRural = 0;

	    
	    try {
	    	shareCenter = Double.parseDouble(inputMap_ori.get("ps_share_center")[0]);
	    	shareRural = Double.parseDouble(inputMap_ori.get("ps_share_rural")[0]);
	    	if(shareCenter<0 || shareCenter>100 || shareRural<0 || shareRural>100) {
	    		return "هناك مشكلة بالنسبة المئوية";
	    	}
	    	pst = conn.prepareStatement("select 1 from kbpartner_share where ps_userid=? and ps_compid=?");
	    	pst.setString(1, inputMap_ori.get("ps_userid")[0]);
	    	pst.setString(2, inputMap_ori.get("ps_compid")[0]);
	    	rs = pst.executeQuery();
	    	if(rs.next())
	    		foundSameRecord = true;
	    	if(foundSameRecord) {
	    		Msg = "هذه الشركة موجودة بالفعل.";
	    	}else {
	    		Msg = super.doInsert(rqs, autoCommit);
	    	}
	    	
	    }catch(Exception e){
			try{conn.rollback();}catch(Exception eRoll){/*ignore*/}
			e.printStackTrace();
			return "Error, "+e.getMessage();
		}finally{
			try{pst.close();}catch(Exception e){/*ignore*/}	
			try{rs.close();}catch(Exception e){/*ignore*/}	
		}
		
		
		
		
		
		
		return Msg;
	}

}
