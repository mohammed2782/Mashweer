package com.app.setup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.app.core.CoreMgr;
import com.app.db.mysql;

public class CompainesSpecialPrices extends CoreMgr {
		public CompainesSpecialPrices() {
			MainSql = "select  *  from kbcompany_special_prices where csp_compid='{compidspecialprice}'";
			canNew = true;
			canDelete = true;
			
			mainTable = "kbcompany_special_prices";
			keyCol = "csp_id";
			
			//userDefinedNewCols.add("sp_custid");
			userDefinedNewCols.add("csp_statecode");
			userDefinedNewCols.add("csp_price");
			userDefinedNewCols.add("csp_ruralprice");
			userDefinedNewCols.add("csp_charges");
			userDefinedNewCols.add("csp_ruralcharges");
			
			userDefinedGridCols.add("csp_statecode");
			userDefinedGridCols.add("csp_price");
			userDefinedGridCols.add("csp_ruralprice");
			userDefinedGridCols.add("csp_charges");
			userDefinedGridCols.add("csp_ruralcharges");
			
			
			userDefinedColLabel.put("csp_statecode","المحافظة");
			userDefinedColLabel.put("csp_price","مبلغ الشحن");
			userDefinedColLabel.put("csp_ruralprice","مبلغ الشحن للأقضيه");
			userDefinedColLabel.put("csp_charges","أجرة المندوب");
			userDefinedColLabel.put("csp_ruralcharges","أجرة المندوب للأقضيه");
			
			userDefinedColsMustFill.add("csp_statecode");
			userDefinedColsMustFill.add("csp_price");
			userDefinedColsMustFill.add("csp_charges");
			userDefinedColsMustFill.add("csp_ruralcharges");
			userDefinedColsMustFill.add("csp_ruralprice");
			userDefinedNewColsDefualtValues.put("csp_compid", new String[] {"{compidspecialprice}"});
			myhtmlmgr.refreshPageOnDelete = true;
			userDefinedLookups.put("csp_statecode", "select st_code, st_name_ar from kbstate");
		}
		
		@Override
		public void initialize(HashMap smartyStateMap){
			String compidspecialprice = replaceVarsinString(" {compidspecialprice} ", arrayGlobals).trim();
			userDefinedNewLookups.put("csp_statecode", "select st_code, st_name_ar from kbstate where st_code not in "
					+ " (select csp_statecode from kbcompany_special_prices where csp_compid='"+compidspecialprice+"')");
			super.initialize(smartyStateMap);
		}
		
		 @Override 
		 public String doInsert (HttpServletRequest rqs , boolean commit) {
			 Connection conn = null;
			 PreparedStatement pst = null;
			 String compidspecialprice = replaceVarsinString(" {compidspecialprice} ", arrayGlobals).trim();
			 String userId = replaceVarsinString(" {useridlogin} ", arrayGlobals).trim();
			 try {
				 conn = mysql.getConn();
				 pst = conn.prepareStatement("insert into kbcompany_special_prices (csp_compid , csp_statecode, csp_price, csp_ruralprice, csp_charges, csp_ruralcharges, csp_createdby) "
				 		+ " values (? , ? , ? , ?, ? ,? ,? )");
				 pst.setString(1, compidspecialprice);
				 pst.setString(2, rqs.getParameter("csp_statecode"));
				 pst.setString(3, rqs.getParameter("csp_price"));
				 pst.setString(4, rqs.getParameter("csp_ruralprice"));
				 pst.setString(5, rqs.getParameter("csp_charges"));
				 pst.setString(6, rqs.getParameter("csp_ruralcharges"));
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
				pst = conn.prepareStatement("delete from kbcompany_special_prices where csp_id=?");
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

