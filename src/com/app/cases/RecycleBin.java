package com.app.cases;

import javax.servlet.http.HttpServletRequest;



import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import com.app.core.CoreMgr;
import com.app.db.mysql;
import com.app.util.Utilities;
import com.mysql.jdbc.Statement;


public class RecycleBin extends CoreMgr{
	protected String QueueDataSpanClass = "QueueDataSpanClass";
	MasterCaseInformation caseMaster = new MasterCaseInformation();

	/**
	 * 
	 */
	public RecycleBin (){
//(c_belongtostore='{userstorecode}' or '{superRank}'='Y')
		MainSql = "select  '' as restore , c_deletedby,c_deleteddt,  c_createdby, q_stage, q_step, stp_name, date(c_createddt) as c_createddt , c_id  , c_custid, "
					  + " c_rcv_name 	 , c_rcv_hp      , c_rcv_state		 , "
					  + " c_rural        , concat (ifnull(cdi_name,''),' ' ,c_rcv_addr_rmk) c_rcv_addr_rmk, c_rmk     		 , "
					  + " c_qty		     , c_receiptamt  , c_shipment_cost   ,c_assignedagent , "
					  + " c_fragile	   , "
					  + " c_branchcode	 		 , c_custreceiptnoori, c_specialcase,"
					  + "  '' as others  , '' as custname    , "
					  + " '' as custprimaryphone, c_settled , c_rcv_district "
					  + " from p_cases_deleted "
					  + " join p_queue on (p_cases_deleted.c_id = q_caseid and q_status !='CLS')"
					  + " join kbstep on stp_code= q_step "
					  + " left join kbcity_district on (cdi_code =c_rcv_district and cdi_stcode=c_rcv_state) "
					  + " where (c_deleted='N')";
		

		mainTable = "p_cases_deleted";
		keyCol = "c_id";
		//orderByCols = "c_rcv_city, c_id desc";
		canFilter = true;
		//canEdit   = true;
		//canDelete = true;		
		//canExport = true;
		//pdfExport = true;
		userDefinedCaption = "عرض كل الشحنات الممسوحه";

		userDefinedGridCols.add("c_custid");
		userDefinedGridCols.add("c_custreceiptnoori");
		userDefinedGridCols.add("c_receiptamt");
		userDefinedGridCols.add("c_rcv_state");
		userDefinedGridCols.add("c_rcv_addr_rmk");
		userDefinedGridCols.add("c_rcv_name");
		userDefinedGridCols.add("c_createddt");
		userDefinedGridCols.add("stp_name");
		userDefinedGridCols.add("c_rcv_hp");
		userDefinedGridCols.add("c_rural");
		userDefinedGridCols.add("c_rmk");
		userDefinedGridCols.add("c_qty");
		userDefinedGridCols.add("c_assignedagent");
		userDefinedGridCols.add("c_shipment_cost");
		userDefinedGridCols.add("c_fragile");
		userDefinedGridCols.add("c_createdby");
		userDefinedGridCols.add("c_specialcase");
		userDefinedGridCols.add("c_deletedby");
		userDefinedGridCols.add("c_deleteddt");
		userDefinedGridCols.add("restore");
		
		userDefinedColLabel.put("custname", "إسم صاحب المحل");
		userDefinedColLabel.put("c_deletedby", "مسح من خلال");
		userDefinedColLabel.put("c_deleteddt", "تاريخ المسح");
		userDefinedColLabel.put("c_specialcase", "شحنه خاصه؟");
		userDefinedColLabel.put("stp_name", "المرحله");
		userDefinedColLabel.put("c_custid", "إسم صاحب المحل");
		userDefinedColLabel.put("c_rcv_name","المستلم");
		userDefinedColLabel.put("c_rcv_hp", "هاتف المستلم");
		userDefinedColLabel.put("c_rcv_state", "المحافظه");
		userDefinedColLabel.put("c_rcv_addr_rmk", "تفاصيل العنوان");
		userDefinedColLabel.put("c_rural", "أطراف");
		userDefinedColLabel.put("c_rmk", "ملاحظات");
		userDefinedColLabel.put("c_qty", "عدد");
		userDefinedColLabel.put("c_assignedagent","مندوب التوصيل ");
		userDefinedColLabel.put("c_receiptamt", "مبلغ الوصل");
		userDefinedColLabel.put("c_shipment_cost","مبلغ الشحن");
		userDefinedColLabel.put("c_sendmoney","مبلغ مرسل");
		userDefinedColLabel.put("c_fragile","قابل للكسر");
		userDefinedColLabel.put("c_bringitemsback","جلب  بضاعه");
		userDefinedColLabel.put("c_branchcode","مخزن");
		userDefinedColLabel.put("c_weight","الوزن");
		userDefinedColLabel.put("c_custreceiptnoori","رقم الوصل");
		userDefinedColLabel.put("c_id","رقم الشحنه");
		userDefinedColLabel.put("q_step","المرحله");
		userDefinedColLabel.put("c_createdby","المدخل");
		userDefinedColLabel.put("c_settled","تم التحاسب ؟");
		userDefinedColLabel.put("c_rcv_district", "المنظقه");
		userDefinedColLabel.put("c_agentshare", "مبلغ الشحن للمندوب");
		userDefinedColLabel.put("restore","إسترجاع");
		
		userDefinedFilterCols.add("c_custid");
		userDefinedLookups.put("c_custid", "select c_id , c_name from kbcustomers order by c_name asc");
		userDefinedLookups.put("c_rcv_district", "select cdi_code, cdi_name from kbcity_district");
		userDefinedLookups.put("c_specialcase", "select kbcode, kbdesc from kbgeneral where kbcat1 = 'YESNO' ");
		userModifyTD.put("restore", "showRestoreBtn({c_id})");
		
		userDefinedFilterCols.add("c_rcv_hp");
		userDefinedFilterCols.add("c_id");
		
		userDefinedFilterCols.add("c_createddt");
		userDefinedFilterCols.add("c_rcv_state");
		userDefinedFilterCols.add("c_custreceiptnoori");
		userDefinedFilterCols.add("q_step");
		userDefinedFilterCols.add("c_settled");
		
		userDefinedFilterColsHtmlType.put("c_createddt", "DATE");
		userDefinedColLabel.put("c_createddt","تاريخ الشحنه");

		userDefinedLookups.put("custname", "select c_id , c_name from kbcustomers order by c_name ASC");
		userDefinedLookups.put("custprimaryphone", "!select c_primaryHP as ph, c_primaryHP from kbcustomers where c_id = '{custname}'");
		userDefinedLookups.put("c_rcv_state", "select st_code , st_name_ar from kbstate  order by st_order");
		
		userDefinedLookups.put("c_fragile", "select kbcode , kbdesc from kbgeneral where kbcat1='YESNO'");
		userDefinedLookups.put("c_rural", "select kbcode , kbdesc from kbgeneral where kbcat1='YESNO'");
		userDefinedLookups.put("c_settled", "select kbcode , kbdesc from kbgeneral where kbcat1='SETTLED'");
		
		userDefinedLookups.put("q_step", "select stp_code , stp_name from kbstep");
		userDefinedLookups.put("c_assignedagent", "select us_id , us_name from kbusers  ");
		
		userDefinedColsMustFill.add("custname");
		userDefinedColsMustFill.add("c_rcv_hp");
		
		userDefinedColsMustFill.add("c_qty");
		userDefinedColsMustFill.add("c_weight");
		userDefinedColsMustFill.add("c_receiptamt");
		userDefinedColsMustFill.add("c_custreceiptnoori");
		userDefinedColsMustFill.add("c_shipment_cost");
		userDefinedColsMustFill.add("c_agentshare");
		userDefinedColsMustFill.add("c_rcv_state");
		
		UserDefinedPageRows = 50;
		userModifyTD.put("stp_name", "modifyStepName({stp_name},{c_settled},{q_stage},{q_step})");
		userDefinedColsTypes.put("c_custreceiptnoori", "VARCHAR");//to remove the comma
		
		
	}//end of no-arg constructor Updatecase
	
	
	@Override
	public void initialize(HashMap smartyStateMap){
		if (httpSRequest.getParameter("dorestore")!=null 
				&& httpSRequest.getParameter("dorestore").equalsIgnoreCase("restore")) {
			Connection conn2 = null;
			PreparedStatement pst = null;
			
			try {
				conn2 = mysql.getConn();
				String c_id = httpSRequest.getParameter("c_id");
				pst = conn2.prepareStatement("insert into p_cases (c_id,c_company_sender, c_createddt, c_createdby, c_rcv_name, c_rcv_hp, c_rcv_state, c_rcv_district,"
						+ " c_rcv_addr_rmk, c_rmk, c_deleted, c_status, c_receiptamt, c_qty, c_rcv_paid, c_shipment_cost, c_shipmentcostpaid,"
						+ " c_paid_tocust, c_goodscostbalance, c_fragile, c_rural, c_weight, c_bringitemsback, c_sendmoney, c_assignedagent, c_branchcode,"
						+ " c_custreceiptnoori, c_settled, c_pmtid, c_pickupagentpmtid, c_sendmoneyflag, c_agentshare, c_agentsharesettled, c_agentpmtid, "
						+ " c_server_createddt, c_mbapp_agent_status, c_mbapp_agent_rmk, c_mbapp_agent_updatedt, c_rtnreason, c_custid, c_custhp, c_specialcase,"
						+ " c_pickupagent, c_shipmentpaidbycustomer, c_shipmentpaidbysender, c_paidinadvance, c_advancepmtid, c_receiptfromsystem, c_excelnumber, c_specialsendercode, c_priceb4change, c_changedprice,"
						+ " c_changedpriceby,c_changedpriceat, c_shipmentprofit, c_partnershare, c_receivedfrom_system, c_receivedfrom_caseid, c_int_batchid, c_sentto_system, c_sentto_caseid, c_productinfo)  "
						+ " select "
						+ " c_id, c_company_sender, c_createddt, c_createdby, c_rcv_name, c_rcv_hp, c_rcv_state, c_rcv_district, "
						+ " c_rcv_addr_rmk, c_rmk, c_deleted, c_status, c_receiptamt, c_qty, c_rcv_paid, c_shipment_cost, c_shipmentcostpaid,"
						+ " c_paid_tocust, c_goodscostbalance, c_fragile, c_rural, c_weight, c_bringitemsback, c_sendmoney, c_assignedagent, c_branchcode,"
						+ " c_custreceiptnoori, c_settled, c_pmtid, c_pickupagentpmtid, c_sendmoneyflag, c_agentshare, c_agentsharesettled, c_agentpmtid,"
						+ " c_server_createddt, c_mbapp_agent_status, c_mbapp_agent_rmk, c_mbapp_agent_updatedt, c_rtnreason, c_custid, c_custhp, c_specialcase,"
						+ " c_pickupagent, c_shipmentpaidbycustomer, c_shipmentpaidbysender , c_paidinadvance, c_advancepmtid, c_receiptfromsystem, c_excelnumber, c_specialsendercode, c_priceb4change, c_changedprice,"
						+ " c_changedpriceby,c_changedpriceat, c_shipmentprofit, c_partnershare, c_receivedfrom_system, c_receivedfrom_caseid, c_int_batchid, c_sentto_system, c_sentto_caseid, c_productinfo "
						+ " from p_cases_deleted where c_id =?");
				pst.setString(1, c_id);
				pst.executeUpdate();
				try {pst.close();} catch (Exception e) {}
				
				pst = conn2.prepareStatement("delete from p_cases_deleted where c_id =?");
				pst.setString(1, c_id);
				pst.executeUpdate();
				conn2.commit();
			}catch(Exception e) {
				
				e.printStackTrace();
				try {conn2.rollback();}catch(Exception eRoll) {}
			}finally {
				
				try {pst.close();} catch (Exception e) {}
				try {conn2.close();} catch (Exception e) {}
				
			}
		}
		
		
		super.initialize(smartyStateMap);
	}
	
	
	public String showRestoreBtn (HashMap<String,String> hashy) {
		
		return "<td align='center' style='vertical-align: middle;'>"
				+ "<a href='?myClassBean=com.app.cases.RecycleBin&c_id="+hashy.get("c_id")+"&dorestore=restore' class='btn btn-edit btn-xs'>"
					+ "<li class='fa fa-undo'></li></a></td>";
	}
	
	
	public String modifyStepName(HashMap<String,String>hashy) {
		String desc = hashy.get("stp_name");
		String color = "";
		if (hashy.get("q_stage").equalsIgnoreCase("cncl")){
			 
			 color = "background-color:red;color:white;";
		}else if(hashy.get("q_step").equalsIgnoreCase("delivered")) {
			if (hashy.get("c_settled").equalsIgnoreCase("FULL")) {
				desc += "</br>تم التحاسب";
				color = "background-color:green;color:white;";
			}else {
				desc += "</br>لم يتم التحاسب";
				color = "background-color:blue;color:white;";
			} 
		}else {
			;
		}
		String html = "<td style='"+color+"'>"+desc;
		
		html+= "</td>";
		return html;
	}
	
}//end of class RecycleBin
