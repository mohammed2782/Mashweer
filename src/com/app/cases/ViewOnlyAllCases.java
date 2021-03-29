package com.app.cases;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import com.app.core.CoreMgr;
import com.app.core.smartyLogAndErrorHandling;
import com.app.db.mysql;
import com.app.util.Utilities;
import com.mysql.jdbc.Statement;
import com.sun.xml.internal.bind.v2.model.core.ID;

public class ViewOnlyAllCases extends CoreMgr{
	protected String QueueDataSpanClass = "QueueDataSpanClass";
	MasterCaseInformation caseMaster = new MasterCaseInformation();

	/**
	 * 
	 */
	public ViewOnlyAllCases (){
//(c_belongtostore='{userstorecode}' or '{superRank}'='Y')
		MainSql = "select  c_rtnreason,'' as del_edit, c_createdby, q_stage, q_step, stp_name, date(c_createddt) as c_createddt , c_id, c_custid     , c_custhp		 , "
					  + " c_rcv_name 	 , c_rcv_hp      , c_rcv_state		 , "
					  + " c_rural        , concat (ifnull(cdi_name,''),' ' ,ifnull(c_rcv_addr_rmk,'')) c_rcv_addr_rmk, c_rmk     		 , "
					  + " c_qty		     , c_receiptamt  , c_shipment_cost   , "
					  + " c_fragile	   , "
					  + " c_branchcode	 		 , c_custreceiptnoori, c_specialcase,"
					  + "  '' as others  , '' as custname    , "
					  + " '' as custprimaryphone, c_settled , c_rcv_district "
					  + " from p_cases "
					  + " join p_queue on (p_cases.c_id = q_caseid and q_status !='CLS')"
					  + " join kbstep on stp_code= q_step "
					  + " left join kbcity_district on (cdi_code =c_rcv_district and cdi_stcode=c_rcv_state) "
					  + " where c_company_sender='{globallogincompid}'";
		//where c_company_sender='{globallogincompid}'
		

		mainTable = "p_cases";
		keyCol = "c_id";
		//orderByCols = "c_rcv_city, c_id desc";
		canFilter = true;
		//canEdit   = true;
		//canDelete = true;		
		canExport = true;
		pdfExport = true;
		userDefinedCaption = "عرض كل الشحنات";
		updCaption = "تعديل بيانات شحنه";
		
		userDefinedExportCols.add("c_custid");
		userDefinedExportCols.add("c_createddt");
		userDefinedExportCols.add("c_custreceiptnoori");
		userDefinedExportCols.add("c_rcv_state");
		userDefinedExportCols.add("c_rcv_addr_rmk");
		userDefinedExportCols.add("c_rcv_hp");
		userDefinedExportCols.add("c_rmk");
		
		
		userDefinedArabicCols.add("c_custid");
		userDefinedArabicCols.add("c_createddt");
		userDefinedArabicCols.add("c_custreceiptnoori");
		userDefinedArabicCols.add("c_rcv_state");
		userDefinedArabicCols.add("c_rcv_addr_rmk");
		userDefinedArabicCols.add("c_rcv_hp");
		userDefinedArabicCols.add("c_rmk");
		userDefinedExportLandScape = true;
		//userDefineda
		
		userDefinedEditFormColNo = 3;
		//userDefinedEditCols.add("c_branchcode");
		userDefinedEditCols.add("custname");
		userDefinedEditCols.add("c_specialcase");
		
		userDefinedEditCols.add("c_rcv_name");
		userDefinedEditCols.add("c_rcv_hp");
		userDefinedEditCols.add("c_rcv_state");
		userDefinedEditCols.add("c_rcv_district");
		userDefinedEditCols.add("c_rural");
		userDefinedEditCols.add("c_rcv_addr_rmk");
		userDefinedEditCols.add("c_shipment_cost");
		userDefinedEditCols.add("c_agentshare");
		userDefinedEditCols.add("c_rmk");
		userDefinedEditCols.add("c_qty");
		userDefinedEditCols.add("c_receiptamt");
		userDefinedEditCols.add("c_custreceiptnoori");
		userDefinedEditCols.add("c_fragile");
		
		
		userDefinedReadOnlyEditCols.add("c_specialcase");
		//userDefinedEditCols.add("c_createddt");
		
		userDefinedGridCols.add("c_custid");
		userDefinedGridCols.add("c_custreceiptnoori");
		userDefinedGridCols.add("c_receiptamt");
		userDefinedGridCols.add("c_rcv_state");
		
		userDefinedGridCols.add("c_rcv_addr_rmk");
		userDefinedGridCols.add("c_rcv_name");
		userDefinedGridCols.add("c_createddt");
		userDefinedGridCols.add("stp_name");
		userDefinedGridCols.add("c_rtnreason");
		userDefinedGridCols.add("c_rcv_hp");
		
		userDefinedGridCols.add("c_rural");
		userDefinedGridCols.add("c_rmk");
		userDefinedGridCols.add("c_qty");
		
		userDefinedGridCols.add("c_shipment_cost");
		userDefinedGridCols.add("c_fragile");
		
		//userDefinedGridCols.add("c_createdby");
		userDefinedGridCols.add("c_specialcase");
		
		
		userDefinedColLabel.put("custname", "إسم العميل");
		userDefinedColLabel.put("c_specialcase", "شحنه خاصه؟");
		
		userDefinedColLabel.put("stp_name", "المرحله");
		userDefinedColLabel.put("c_custid", "إسم العميل");
		userDefinedColLabel.put("c_rcv_name","المستلم");
		userDefinedColLabel.put("c_rcv_hp", "هاتف المستلم");
		userDefinedColLabel.put("c_rcv_state", "المحافظه");
		userDefinedColLabel.put("c_rcv_addr_rmk", "تفاصيل العنوان");
		userDefinedColLabel.put("c_rural", "أطراف");
		userDefinedColLabel.put("c_rmk", "ملاحظات");
		userDefinedColLabel.put("c_qty", "عدد");
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
		userDefinedColLabel.put("c_rtnreason", "سبب الارجاع");
		
		
		
		userDefinedFilterCols.add("c_custid");
		userDefinedLookups.put("c_custid", "select c_id , c_name from kbcustomers order by c_name asc");
		userDefinedLookups.put("c_rcv_district", "select cdi_code, cdi_name from kbcity_district");
		userDefinedLookups.put("c_specialcase", "select kbcode, kbdesc from kbgeneral where kbcat1 = 'YESNO' ");
		userDefinedLookups.put("c_rtnreason", "SELECT rtn_code, rtn_desc FROM kbrtn_reasons");
		
		
		userDefinedFilterCols.add("c_rcv_hp");
		userDefinedFilterCols.add("c_id");
		
		userDefinedFilterCols.add("c_createddt");
		userDefinedFilterCols.add("c_rcv_state");
		userDefinedFilterCols.add("c_custreceiptnoori");
		userDefinedFilterCols.add("q_step");
		userDefinedFilterCols.add("c_settled");
		
		userDefinedFilterColsHtmlType.put("c_createddt", "DATE");
		userDefinedColLabel.put("c_createddt","تاريخ الشحنه");

		
		
		//userDefinedEditMockUpCols.put("custprimaryphone", "(select cm_cust_hp from p_casesmaster where cm_id = c_cmid)");
		//userDefinedLookups.put("c_branchcode", "select store_code , store_name from kbstores where store_deleted='N'");
		userDefinedLookups.put("custname", "select c_id , c_name from kbcustomers order by c_name ASC");
		userDefinedLookups.put("custprimaryphone", "!select c_primaryHP as ph, c_primaryHP from kbcustomers where c_id = '{custname}'");
		userDefinedLookups.put("c_rcv_state", "select st_code , st_name_ar from kbstate  order by st_order");
		//userDefinedEditLookups.put("c_rcv_city", "select ct_code , ct_name_ar from kbcity where ct_active='Y' order by ct_order");
		userDefinedLookups.put("c_fragile", "select kbcode , kbdesc from kbgeneral where kbcat1='YESNO'");
		userDefinedLookups.put("c_rural", "select kbcode , kbdesc from kbgeneral where kbcat1='YESNO'");
		userDefinedLookups.put("c_settled", "select kbcode , kbdesc from kbgeneral where kbcat1='SETTLED'");
		//userDefinedLookups.put("c_bringitemsback", "select kbcode , kbdesc from kbgeneral where kbcat1='YESNO'");
		userDefinedLookups.put("q_step", "select stp_code , stp_name from kbstep");

		userDefinedEditColsHtmlType.put("custname" , "DROPLIST");
		userDefinedEditColsHtmlType.put("custprimaryphone" , "TEXT");
		userDefinedEditColsHtmlType.put("c_fragile" , "RADIO");
		userDefinedEditColsHtmlType.put("c_bringitemsback" , "RADIO");
		userDefinedEditColsHtmlType.put("c_rural" , "DROPLIST");
		
		userDefinedEditColsHtmlType.put("c_qty" , "NUMBER");
		userDefinedEditColsHtmlType.put("c_rmk" , "TEXTAREA");
		userDefinedEditColsHtmlType.put("c_specialcase" , "TEXT");
		userDefinedEditColsHtmlType.put("c_rcv_addr_rmk" , "TEXTAREA");
		

		userDefinedReadOnlyEditCols.add("c_shipment_cost");
		//userDefinedReadOnlyEditCols.add("custname");
		//userDefinedReadOnlyEditCols.add("c_branchcode");

		userDefinedColsMustFill.add("custname");
		userDefinedColsMustFill.add("c_rcv_hp");
		//userDefinedColsMustFill.add("c_rcv_name");
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
		//userDefinedEditMockUpCols.put("custname", "(select cm_custid from p_casesmaster where cm_id = c_cmid)");
		
	}//end of no-arg constructor Updatecase
	
	
	@Override
	public void initialize(HashMap smartyStateMap){
		if (getDisplayMode().equalsIgnoreCase("EDITSINGLE")) {
			
			Connection conn2 = null;
			PreparedStatement pst = null;
			ResultSet rs = null;
			try {
				String caseid = httpSRequest.getParameter(keyCol);
				conn2 = mysql.getConn();
				pst = conn2.prepareStatement("select c_specialcase from p_cases where c_id = ?");
				pst.setString(1, caseid);
				rs = pst.executeQuery();
				if (rs.next()) {
					if (rs.getString("c_specialcase").equalsIgnoreCase("Y")) {
						userDefinedReadOnlyEditCols.remove("c_shipment_cost");
					}else {
						userDefinedEditCols.remove("c_agentshare");
					}
				}
					
				
			}catch(Exception e) {
				e.printStackTrace();
			}finally {
				try {rs.close();} catch (Exception e) {}
				try {pst.close();} catch (Exception e) {}
				try {conn2.close();} catch (Exception e) {}
				
			}
		}
		
		
		super.initialize(smartyStateMap);
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

}//end of class Updatecase
