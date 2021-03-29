package com.app.setup;

import java.sql.PreparedStatement;

import javax.servlet.http.HttpServletRequest;

import com.app.core.CoreMgr;


public class PartnerShare extends CoreMgr{
	public PartnerShare() {
		MainSql = " select comp_id,comp_name,comp_partner_share from kbcompanies where comp_pickupagent = '{partnerid}'";
		
		canEdit = true;
		
		userDefinedCaption = "حصة الشريك";
		
		mainTable = "kbcompanies";
		keyCol = "comp_id";
		
		displayMode = "GRIDEDIT";
		userDefined_x_panelclass = "profitstable";
		
		userDefinedGridCols.add("comp_name");
		userDefinedGridCols.add("comp_partner_share");
		
		userDefinedColLabel.put("comp_name", "اسم الشركة");
		userDefinedColLabel.put("comp_partner_share", "حصة الشريك %");
		
		userDefinedEditCols.add("comp_partner_share");
		
	}
	
	@Override
	public String doUpdate (HttpServletRequest rqs , boolean commit) {
		PreparedStatement pst = null;
		int rowsNo = 0;
		inputMap_ori = filterRequest(rqs);
		if (inputMap_ori.get("smartyhiddenmultieditrowsno")!=null)
			rowsNo = Integer.parseInt(inputMap_ori.get("smartyhiddenmultieditrowsno")[0]);
		
		try {
			Double patnerShare = 0.0;
			int compId = 0;
			pst = conn.prepareStatement("update kbcompanies set comp_partner_share = ? where comp_id = ? ");
			for(int i=1 ; i<=rowsNo ; i++) {
				patnerShare = Double.parseDouble(inputMap_ori.get("comp_partner_share_smartyrow_"+i)[0]);
				compId = Integer.parseInt(inputMap_ori.get("smarty_comp_id_hidden_smartyrow_"+i)[0]);
				pst.setDouble(1, patnerShare);
				pst.setInt(2, compId);
				pst.executeUpdate();
			}
			
			conn.commit();
		}catch(Exception e){
			try{conn.rollback();}catch(Exception eRoll){/*ignore*/}
			e.printStackTrace();
			return "Error, "+e.getMessage();
		}finally{
			try{pst.close();}catch(Exception e){/*ignore*/}				
		}
			
			
		
		return "تم الحفظ";
		

	}

}
