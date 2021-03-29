package com.app.incomeoutcome;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.app.core.CoreMgr;
import com.app.db.mysql;
import com.app.util.Utilities;

public class SenderCompanyPaymentsBarCode extends CoreMgr{
	public SenderCompanyPaymentsBarCode() {

		/*
		 * to define main sql statement, key column and main table that do this
		 * operations
		 */
		MainSql = " select cppc_id ,cppc_companyid, cppc_amount_paid , cppc_paymentdt , cppc_createdby , cppc_rmk, '' as fake, '' as profit, '' as partnerprofit "
				+ " from  p_customer_payments_company where cppc_companyid = '{senderCompanyAcctBarCode}'  order by  cppc_id desc ";

		keyCol = "cppc_id";
		mainTable = "p_customer_payments_company";

		// ///////////////
		userDefinedGridCols.add("cppc_id");
		//userDefinedGridCols.add("cppa_pickupagentid");
		userDefinedGridCols.add("cppc_amount_paid");
		userDefinedGridCols.add("cppc_paymentdt");
		userDefinedGridCols.add("cppc_createdby");
		userDefinedGridCols.add("cppc_rmk");
		userDefinedGridCols.add("fake");
		userDefinedGridCols.add("partnerprofit");
		userDefinedGridCols.add("profit");

		// //////////////
		userDefinedCaption = "تسديدات شركات التوصيل";
		userDefinedColLabel.put("cppc_id", "رقم الأيصال");
		userDefinedColLabel.put("cppc_amount_paid", "المبلغ المدفوع");
		userDefinedColLabel.put("cppc_companyid", "الشركة المرسلة");
		userDefinedColLabel.put("cppc_paymentdt", "تاريخ الدفع");
		userDefinedColLabel.put("cppc_rmk", " ملاحظات");
		userDefinedColLabel.put("cppc_createdby", "أنشئ بواسطة ");
		userDefinedColLabel.put("fake", "طباعة ايصال الدفع ");
		userDefinedColLabel.put("profit", "طباعة ايصال الارباح");
		userDefinedColLabel.put("partnerprofit", "أرباح الشريك");
		
		//userDefinedLookups.put("partnerprofit", "select sum(c_partnershare), sum(c_partnershare) from p_cases where c_company_senderpmtid = '{cppc_id}'");

		canDelete = true;
		userModifyTD.put("fake", "printPmtReceipt({cppc_id})");
		userModifyTD.put("profit", "printProfiReceipt({cppc_id})");
		userModifyTD.put("partnerprofit", "partnerProfit({cppc_id})");
	
		//userDefinedLookups.put("cp_custid","select c_id ,c_name  From kbcustomers");
		myhtmlmgr.refreshPageOnDelete = true;
		myhtmlmgr.tableClass = "table table-striped  table-bordered blue_table";
	}// end of constructor customer_payment

	
	public String printPmtReceipt(HashMap<String, String> hashy) {
		String btn = "<a href=\"../SenderCompanyPaymentReceiptSRVL?cppc_id="+hashy.get("cppc_id")+"\" "
				+ " class='btn btn-xs btn-warning' >طباعة أيصال دفع <i class=\"fa fa-print fa-lg\"></i></a>";
		return "<td>" + btn + "</td>";
	}
	public String printProfiReceipt(HashMap<String, String> hashy) {
		String btn = "<a href=\"../SenderCompanyProfitReceiptSRVL?cppc_id="+hashy.get("cppc_id")+"\" "
				+ " class='btn btn-xs btn-info' >طباعة أيصال الارباح <i class=\"fa fa-print fa-lg\"></i></a>";
		return "<td>" + btn + "</td>";
	}
	public String partnerProfit(HashMap<String, String> hashy) {
		Utilities ut = new Utilities();
		int companyPaymentId = Integer.parseInt(hashy.get("cppc_id"));
		Connection conn2 = null;
		double profit = 0;
		try {
			conn2 = mysql.getConn();
			profit = ut.calcPartnerShareProfit(conn2, companyPaymentId);
		}catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}finally {
			try {conn2.close();}catch(Exception e) {}
		}
		
		return "<td>" + (int)profit + "</td>";
	}
	@Override
	public String doDelete(HttpServletRequest rqs) {
		String keyVal = rqs.getParameter(keyCol);
		PreparedStatement pst = null;
		String userId = replaceVarsinString(" {useridlogin} ", arrayGlobals).trim();
		try {
			// first backup the cases that had payment and the payment also
	
			pst = conn.prepareStatement("insert into  p_deletedpayment_cases (dpc_dpid , dpc_cid, dpc_createdby , dpd_from_sendercompany_acct) "
					+ " select c_company_senderpmtid , c_id , ? , ? from p_cases where c_company_senderpmtid =?");
			pst.setString(1, userId);
			pst.setString(2, "Y");
			pst.setString(3, keyVal);
			
			pst.executeUpdate();
			try {pst.close();} catch (Exception e) {}
			
			pst = conn.prepareStatement("insert into p_customer_payments_company_dlt "
					+ " (cppa_id, cppa_pickupagentid, cppa_amount_paid, cppa_paymentdt, cppa_createdby, cppa_createddt, cppa_rmk, dpp_deletedby)" + 
					" SELECT cppc_id, cppc_companyid, cppc_amount_paid, cppc_paymentdt, cppc_createdby, cppc_createddt, cppc_rmk, ? "
					+ "  from p_customer_payments_company  where cppc_id = ?");
			pst.setString(1, userId);
			pst.setString(2, keyVal);
			pst.executeUpdate();
			try {pst.close();} catch (Exception e) {}
			
			// second update the cases back to NO, and c_pickupagentpmtid = 0
			pst = conn.prepareStatement("update p_cases set c_company_senderpmtid=0 , c_settled='NO' where c_company_senderpmtid =?");
			pst.setString(1, keyVal);
			pst.executeUpdate();
			try {pst.close();} catch (Exception e) {}
			
			// last thing , delete
			pst = conn.prepareStatement("delete from p_customer_payments_company  where cppc_id = ?");
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
		}

		return "";
	}// end of doDelete*/

	

}// end of class customer_payment
