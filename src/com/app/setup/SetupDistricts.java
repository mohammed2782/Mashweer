package com.app.setup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.app.core.CoreMgr;
import com.app.db.mysql;

public class SetupDistricts extends CoreMgr{
	public SetupDistricts () {
		MainSql = "select * from kbcity_district";
		
		//userDefinedEditCols.add("cdi_code");
		userDefinedEditCols.add("cdi_name");
		userDefinedEditCols.add("cdi_agentshare");
		
		userDefinedNewCols.add("cdi_stcode");
		userDefinedNewCols.add("cdi_code");
		userDefinedNewCols.add("cdi_name");
		
		canEdit = true;
		canNew = true;
		canDelete = true;
		userDefinedFilterCols.add("cdi_stcode");
		
		canFilter = true;
		mainTable = "kbcity_district";
		keyCol = "cdi_id";
		UserDefinedPageRows = 500;
		
		userDefinedGridCols.add("cdi_stcode");
		userDefinedGridCols.add("cdi_name");
		userDefinedGridCols.add("cdi_agentshare");
		
		userDefinedColsMustFill.add("cdi_stcode");
		userDefinedColsMustFill.add("cdi_name");
		userDefinedColsMustFill.add("cdi_code");
		
		
		userDefinedColLabel.put("cdi_code","شفره");
		userDefinedColLabel.put("cdi_name","إسم المنطقه");
		userDefinedColLabel.put("cdi_stcode","المدينه");
		userDefinedColLabel.put("cdi_agentshare","أجرة مندوب التوصيل" );
		
		
		userDefinedLookups.put("cdi_stcode", "select st_code, st_name_ar from kbstate where st_active='Y'");
		
		userDefinedNewColsDefualtValues.put("cdi_stcode", new String [] {"BAS"});
		
		
	}
	@Override
	public String doInsert(HttpServletRequest rqs , boolean autoCommit) {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean availablecode = false;
		try {
			conn = mysql.getConn();
			pst = conn.prepareStatement("select 1 from kbcity_district where cdi_code=?");
			pst.setString(1, rqs.getParameter("cdi_code"));
			rs = pst.executeQuery();
			if(rs.next())
				availablecode = true;
			
			if (availablecode)
				return "يوجد منطقه لها نفس الشفره";
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			
			pst = conn.prepareStatement("insert into kbcity_district (cdi_code, cdi_name, cdi_stcode) values (?, ?, ?)");
			pst.setString(1, rqs.getParameter("cdi_code"));
			pst.setString(2, rqs.getParameter("cdi_name"));
			pst.setString(3, rqs.getParameter("cdi_stcode"));
			pst.executeUpdate();
			conn.commit();
		}catch(Exception e) {
			try {conn.rollback();}catch(Exception eRoll) {}
			e.printStackTrace();
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			try {conn.close();}catch(Exception e) {}
		}
		return "تم الحفظ";
	}
}
