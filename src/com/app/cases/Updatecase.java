package com.app.cases;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.app.core.CoreMgr;
import com.app.core.smartyLogAndErrorHandling;
import com.app.db.mysql;
import com.app.util.Utilities;
import com.mysql.jdbc.Statement;
import com.sun.xml.internal.bind.v2.model.core.ID;

public class Updatecase extends CoreMgr{
	protected String QueueDataSpanClass = "QueueDataSpanClass";
	MasterCaseInformation caseMaster = new MasterCaseInformation();

	/**
	 * 
	 */
	public Updatecase (){
//(c_belongtostore='{userstorecode}' or '{superRank}'='Y')
		MainSql = "select concat (ifnull(c_pmtid,''), ifnull(c_company_senderpmtid,''), ifnull(c_pickupagentpmtid,'')) as c_pmtid, '' as fromdt, '' as todate , '' as del_edit, c_receiptfromsystem, q_previous_action_taken_by , c_createdby, q_stage, q_step, stp_name, date(c_createddt) as c_createddt , c_id , c_custid     , c_custhp		 , "
					  + " c_rcv_name 	 , c_rcv_hp      , c_rcv_state		 , "
					  + " c_rural        , concat (ifnull(cdi_name,''),' ' ,ifnull(c_rcv_addr_rmk,'')) c_rcv_addr_rmk, c_rmk     		 , "
					  + " c_qty		     , c_receiptamt  , c_shipment_cost   ,c_assignedagent , "
					  + " c_fragile	   , c_agentsharesettled,  "
					  + " c_branchcode	 		 , c_custreceiptnoori, c_specialcase,"
					  + "  '' as others  , '' as custname    , "
					  + " '' as custprimaryphone, c_settled , c_rcv_district, c_company_sender "
					  + " from p_cases "
					  + " join p_queue on (p_cases.c_id = q_caseid and q_status !='CLS')"
					  + " join kbstep on stp_code= q_step "
					  + " left join kbcity_district on (cdi_code =c_rcv_district and cdi_stcode=c_rcv_state) "
					  + " where 1=0";
		

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
		//userDefinedExportCols.add("stp_name");
		userDefinedExportCols.add("c_rcv_state");
		userDefinedExportCols.add("c_rcv_addr_rmk");
		userDefinedExportCols.add("c_rcv_hp");
		userDefinedExportCols.add("c_rmk");
		
		userDefinedArabicCols.add("c_custid");
		userDefinedArabicCols.add("c_createddt");
		userDefinedArabicCols.add("c_custreceiptnoori");
		userDefinedArabicCols.add("stp_name");
		userDefinedArabicCols.add("c_rcv_state");
		userDefinedArabicCols.add("c_rcv_addr_rmk");
		userDefinedArabicCols.add("c_rcv_hp");
		userDefinedArabicCols.add("c_assignedagent");
		userDefinedArabicCols.add("c_rmk");
		userDefinedExportLandScape = true;
		//userDefineda
		
		userDefinedEditFormColNo = 3;
		userDefinedEditCols.add("c_company_sender");
		userDefinedEditCols.add("c_custid");
		userDefinedEditCols.add("c_specialcase");
		userDefinedEditCols.add("c_receiptfromsystem");
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
		
		userDefinedGridCols.add("c_company_sender");
		userDefinedGridCols.add("c_custid");
		userDefinedGridCols.add("c_custreceiptnoori");
		userDefinedGridCols.add("c_receiptamt");
		userDefinedGridCols.add("c_rcv_state");
		userDefinedGridCols.add("c_rcv_addr_rmk");
		userDefinedGridCols.add("c_rcv_name");
		userDefinedGridCols.add("c_pmtid");
		userDefinedGridCols.add("c_createddt");
		userDefinedGridCols.add("stp_name");
		userDefinedGridCols.add("q_previous_action_taken_by");
		userDefinedGridCols.add("c_rcv_hp");
		
		
		userDefinedGridCols.add("c_rural");
		userDefinedGridCols.add("c_rmk");
		userDefinedGridCols.add("c_qty");
		userDefinedGridCols.add("c_assignedagent");
		userDefinedGridCols.add("c_shipment_cost");
		userDefinedGridCols.add("c_fragile");
		
		userDefinedGridCols.add("c_createdby");
		userDefinedGridCols.add("c_specialcase");
		userDefinedGridCols.add("del_edit");
		
		userDefinedColLabel.put("custname", "إسم العميل");
		userDefinedColLabel.put("c_pmtid", "رقم محاسبة الزبون");
		userDefinedColLabel.put("c_specialcase", "شحنه خاصه؟");
		
		userDefinedColLabel.put("c_company_sender", "الشركة المرسلة");
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
		userDefinedColLabel.put("del_edit"," ");
		userDefinedColLabel.put("q_previous_action_taken_by", "اخر من حدث الحاله");
		userDefinedColLabel.put("c_receiptfromsystem", "متولد من النظام؟");
		
		
		userDefinedFilterCols.add("c_custid");
		userDefinedLookups.put("c_custid", "select c_id , c_name from kbcustomers order by c_name asc");
		userDefinedLookups.put("c_company_sender", "select comp_id, comp_name from kbcompanies");
		userDefinedLookups.put("c_rcv_district", "select cdi_code, cdi_name from kbcity_district");
		userDefinedLookups.put("c_specialcase", "select kbcode, kbdesc from kbgeneral where kbcat1 = 'YESNO' ");
		userModifyTD.put("del_edit", "showDel_Edit({c_id},{c_settled},{c_agentsharesettled})");
		
		userDefinedFilterCols.add("c_rcv_hp");
		userDefinedFilterCols.add("c_id");
		
		userDefinedColLabel.put("fromdt","بتاريخ");
		userDefinedColLabel.put("todate","إلى تاريخ");
		
		
		
		userDefinedFilterCols.add("c_rcv_state");
		userDefinedFilterCols.add("c_custreceiptnoori");
		userDefinedFilterCols.add("q_step");
		userDefinedFilterCols.add("c_settled");
		userDefinedFilterCols.add("c_receiptamt");
		userDefinedFilterCols.add("c_assignedagent");
		userDefinedFilterCols.add("fromdt");
		userDefinedFilterCols.add("todate");
		userDefinedFilterCols.add("c_company_sender");
		userDefinedFilterColsHtmlType.put("fromdt", "DATE");
		userDefinedFilterColsHtmlType.put("todate", "DATE");
		
		userDefinedEditColsHtmlType.put("c_createddt", "DATE");
		userDefinedNewColsHtmlType.put("c_createddt", "TIMESTAMP");
		userDefinedColLabel.put("c_createddt","تاريخ الشحنه");

		
		userDefinedReadOnlyEditCols.add("userDefinedEditCols");
		
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
		userDefinedLookups.put("c_assignedagent", "select us_id , us_name from kbusers  where us_rank = 'DLVAGENT'  ");
		
		userDefinedEditColsHtmlType.put("custname" , "DROPLIST");
		userDefinedEditColsHtmlType.put("custprimaryphone" , "TEXT");
		userDefinedEditColsHtmlType.put("c_fragile" , "RADIO");
		userDefinedEditColsHtmlType.put("c_bringitemsback" , "RADIO");
		userDefinedEditColsHtmlType.put("c_rural" , "DROPLIST");
		
		userDefinedEditColsHtmlType.put("c_qty" , "NUMBER");
		userDefinedEditColsHtmlType.put("c_rmk" , "TEXTAREA");
		userDefinedEditColsHtmlType.put("c_specialcase" , "TEXT");
		userDefinedEditColsHtmlType.put("c_rcv_addr_rmk" , "TEXTAREA");
		

		userDefinedReadOnlyEditCols.add("c_receiptfromsystem");
		//userDefinedReadOnlyEditCols.add("c_shipment_cost");
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
		userModifyTD.put("stp_name", "modifyStepName({c_custreceiptnoori},{c_id},{stp_name},{c_settled},{q_stage},{q_step})");
		//userModifyTD.put("stp_name", "modifyStepName({stp_name},{c_settled},{q_stage},{q_step})");
		userDefinedColsTypes.put("c_custreceiptnoori", "VARCHAR");//to remove the comma
		userDefinedEditCols.add("c_assignedagent");
		
	}//end of no-arg constructor Updatecase
	
	
	@Override
	public void initialize(HashMap smartyStateMap){
		if (getDisplayMode().equalsIgnoreCase("EDITSINGLE")) {
			//System.out.println("heelllooo");
			Connection conn2 = null;
			PreparedStatement pst = null;
			ResultSet rs = null;
			try {
				String caseid = httpSRequest.getParameter(keyCol);
				conn2 = mysql.getConn();
				pst = conn2.prepareStatement("select c_agentsharesettled, c_specialcase, c_receiptfromsystem from p_cases where c_id = ?");
				pst.setString(1, caseid);
				rs = pst.executeQuery();
				if (rs.next()) {
					if (rs.getString("c_specialcase").equalsIgnoreCase("Y")) {
						userDefinedReadOnlyEditCols.remove("c_shipment_cost");
					}else {
						userDefinedEditCols.remove("c_agentshare");
					}
					if (rs.getString("c_receiptfromsystem").equalsIgnoreCase("Y")) {
						userDefinedReadOnlyEditCols.add("c_custreceiptnoori");
					}
					if(rs.getString("c_agentsharesettled").equalsIgnoreCase("FULL")) {
						userDefinedReadOnlyEditCols.add("c_receiptamt");
						userDefinedReadOnlyEditCols.add("c_receiptfromsystem");
						userDefinedReadOnlyEditCols.add("c_rural");
						userDefinedReadOnlyEditCols.add("c_shipment_cost");
						userDefinedReadOnlyEditCols.add("c_qty");
						userDefinedReadOnlyEditCols.add("c_receiptamt");
						userDefinedReadOnlyEditCols.add("c_assignedagent");
						userDefinedEditCols.add("c_agentsharesettled");
						userDefinedReadOnlyEditCols.add("c_agentsharesettled");
						userDefinedColLabel.put("c_agentsharesettled", "تم محاسبة المندوب");
						userDefinedLookups.put("c_agentsharesettled", "select 'FULL' , 'نعم' from dual union select 'لا' , 'No' from dual");
					}
				}
				try {rs.close();} catch (Exception e) {}
				try {pst.close();} catch (Exception e) {}
				
				pst = conn2.prepareStatement("select b_custid from p_books where b_id in (select br_bid from p_books_rcp where br_cid = ?)");
				pst.setString(1, caseid);
				rs = pst.executeQuery();
				if (rs.next()) {
					if (rs.getInt("b_custid")>0) {
						userDefinedReadOnlyEditCols.add("c_custid");
						
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
		
		if (search_paramval!=null && !search_paramval.isEmpty()) {
			MainSql = "select c_agentsharesettled, concat (ifnull(c_pmtid,''), ifnull(c_company_senderpmtid,''), ifnull(c_pickupagentpmtid,'')) as c_pmtid, '' as del_edit, c_receiptfromsystem, q_previous_action_taken_by , c_createdby, q_stage, q_step, stp_name, date(c_createddt) as c_createddt , c_id , c_custid     , c_custhp		 , "
					  + " c_rcv_name 	 , c_rcv_hp      , c_rcv_state		 , "
					  + " c_rural        , concat (ifnull(cdi_name,''),' ' ,ifnull(c_rcv_addr_rmk,'')) c_rcv_addr_rmk, c_rmk     		 , "
					  + " c_qty		     , c_receiptamt  , c_shipment_cost   ,c_assignedagent , "
					  + " c_fragile	   , "
					  + " c_branchcode	 		 , c_custreceiptnoori, c_specialcase,"
					  + "  '' as others  , '' as custname    , "
					  + " '' as custprimaryphone, c_settled , c_rcv_district, c_company_sender "
					  + " from p_cases "
					  + " join p_queue on (p_cases.c_id = q_caseid and q_status !='CLS')"
					  + " join kbstep on stp_code= q_step "
					  + " left join kbcity_district on (cdi_code =c_rcv_district and cdi_stcode=c_rcv_state) ";
		}
		
		String fromdt = "ALL";
		String todt = "ALL";
		boolean foundSearch = false;
		for (String parameter : search_paramval.keySet()) {
			for (String value : search_paramval.get(parameter)) {
				if (!parameter.equals("filter") && (value != null) && (!value.equals(""))) {
					if (parameter.equals("fromdt")) {
						fromdt =  value;
						foundSearch = true;
						//search_paramval.remove("fromdt");
					} 
					if (parameter.equals("todate")) {
						todt =  value;
						//search_paramval.remove("todate");
						//foundSearch = true;
					} 
				}
			}
		}
	
	
		if (foundSearch) {
			if (todt.equalsIgnoreCase("ALL") && !fromdt.equalsIgnoreCase("ALL")) {
				todt = fromdt;
			}
			if (!fromdt.equalsIgnoreCase("ALL")) {
				MainSql +=" where  (date(c_createddt)>='"+fromdt+"') and (date(c_createddt)<='"+todt+"' ) ";
			}
		}
		
		
	}
	@Override 
	public StringBuilder genListing() {
		//System.out.println("calling gen listing---------------------");
		search_paramval.remove("fromdt");
		search_paramval.remove("todate");
		return super.genListing();
	}
	
	@Override
	public String doDelete(HttpServletRequest rqs){
		String Msg ="";
		PreparedStatement pst = null;
		String keyVal= rqs.getParameter(keyCol);
		String userid = replaceVarsinString(" {useridlogin} ", arrayGlobals).trim();
		Connection conn = null;
		try {
			conn = mysql.getConn();
			//bakcup first
			pst = conn.prepareStatement("insert into p_cases_deleted select p_cases.* , ?, now() from p_cases where c_id = ?");
			pst.setString(1,userid );
			pst.setString(2,keyVal );
			pst.executeUpdate();
			try {pst.close();}catch(Exception e) {}
			//free the receipt
			pst = conn.prepareStatement("update p_books_rcp set  br_custid =0, br_groupid=0,br_cid=0 where br_cid =? ");
			pst.setString(1, keyVal);
			pst.executeUpdate();
			try {pst.close();}catch(Exception e) {}
			
			pst = conn.prepareStatement("delete from p_cases where c_id = ?");
			pst.setString(1, keyVal);
			pst.executeUpdate();
			conn.commit();
		}catch (Exception e) {
			try{conn.rollback();}catch (Exception eRollBack){eRollBack.printStackTrace();}
			e.printStackTrace();
		    Msg = "Error";
		    deleteErrorFlag = true;
		    
		}finally {
			try {pst.close();}catch(Exception e) {}
			try {conn.close();}catch(Exception e) {}
		}
		return Msg;
	}
	
	public String showDel_Edit (HashMap<String,String> hashy) {
		if (hashy.get("c_settled").equalsIgnoreCase("FULL"))
			return "<td></td>";
		
		return "<td align='center' style='vertical-align: middle;'>"
			+"<button type='button' "
			+ " onclick=\"link=false; "
			+ " var rs =doDeleteSmarty(this,'هل تريد حذف هذه الشحنه ؟' ,'c_id','"+hashy.get("c_id")+"' , 'com.app.cases.Updatecase' ); return rs;\" class='btn btn-danger btn-xs'>"
					+ "<li class='fa fa-trash'></li></button>"
					+ "<a href='?myClassBean=com.app.cases.Updatecase&c_id="+hashy.get("c_id")+"&op=upd' class='btn btn-edit btn-xs'>"
					+ "<li class='fa fa-pencil'></li></a></td>";
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
		String buttonPop = "<button type=\"button\" class=\"btn btn-xs btn-info\" "
				+ "	onclick=\"popitup ('caseflowaudit.jsp?auditreceiptnoori="+hashy.get("c_custreceiptnoori")+"&auditcaseid="+hashy.get("c_id")+"' , '' , 1000 ,600);\">عرض تاريخ العمليات</button>";
		
		desc +="</br>"+buttonPop;
		String html = "<td style='"+color+"'>"+desc;
		
		html+= "</td>";
		return html;
	}
	@Override
	public String doUpdate(HttpServletRequest rqs, boolean autoCommit) {
		PreparedStatement pst = null; 
		ResultSet rs = null;
		String caseid = parseUpdateRqs(rqs);
		String msg = "تم التعديبل بنجاح";
		String custid = "";
		
		String userid = replaceVarsinString(" {useridlogin} ", arrayGlobals).trim();
		double  receiptAmtFromScreen = 0 ;
		boolean agentShareSettled = false;
		HashMap <String , String> dataMapFromDB = new HashMap<String, String>();
		try{
			/*for(String key:inputMap_ori.keySet())
				System.out.println("key = "+key+"	value = "+inputMap_ori.get(key)[0]);
				*/
            Utilities ut = new Utilities();

            pst = conn.prepareStatement("select * from p_cases where c_id =?");
            pst.setString(1, caseid);
            rs = pst.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            if (rs.next()) {
            	for(int i=1 ; i<=rsmd.getColumnCount();i++) {
            		dataMapFromDB.put(rsmd.getColumnName(i), rs.getString(rsmd.getColumnName(i)));
            	}
            	//agentId = rs.getString("c_assignedagent");
            	//receiptAmtDb = rs.getDouble("c_receiptamt");
            }
            try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception ex) {}
			/*System.out.println("------------------------------------------------------------------");
			for(String key:dataMapFromDB.keySet())
				System.out.println("key = "+key+"	value = "+dataMapFromDB.get(key));
				*/
            //check if the case was delivered then don't change4
            boolean allowEdit = true;
            pst = conn.prepareStatement("select 1 from p_cases where c_id =? and (ifnull(c_pmtid,0)>0)  ");
            pst.setString(1, caseid);
            rs = pst.executeQuery();
            if (rs.next())
            	allowEdit = false;
            
            try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception ex) {}
			if (allowEdit) {
				String state= inputMap_ori.get("c_rcv_state")[0];
				String district = "";
				if (state.equalsIgnoreCase("BAS")) {
					if (inputMap_ori.containsKey("c_rcv_district") && inputMap_ori.get("c_rcv_district")[0]!=null
							&&  !inputMap_ori.get("c_rcv_district")[0].trim().equalsIgnoreCase("")) {
						district = inputMap_ori.get("c_rcv_district")[0];
					}else {
						setUpdateErrorFlag(false);
						return "يجب أختيار المنطقه";
					}
				}
				boolean ruralArea = false;
				
				if (inputMap_ori.get("c_rural")[0]!=null && inputMap_ori.get("c_rural")[0].equalsIgnoreCase("Y"))
					ruralArea = true;
				int compId = 0;
				if (inputMap_ori.containsKey("c_company_sender")  && inputMap_ori.get("c_company_sender")[0]!=null)
					compId = Integer.parseInt(inputMap_ori.get("c_company_sender")[0]);
				double agentShareAmt = 0.0;
				if (inputMap_ori.containsKey("c_agentshare")  && inputMap_ori.get("c_agentshare")[0]!=null)
					agentShareAmt = Double.parseDouble(inputMap_ori.get("c_agentshare")[0]);
				else
					agentShareAmt = ut.calcAgentShipmentChargesShare(conn,compId, state, district , ruralArea, dataMapFromDB.get("c_assignedagent"));
				
				
				custid = inputMap_ori.get("c_custid")[0];
				double shipmentCost = 0.0;
				if (inputMap_ori.containsKey("c_shipment_cost")  && inputMap_ori.get("c_shipment_cost")[0] !=null)
					shipmentCost = Double.parseDouble(inputMap_ori.get("c_shipment_cost")[0]);
				else
					shipmentCost = ut.calcShipmentChargesBasedOnDestCity(conn, state,ruralArea,Integer.parseInt(custid), Integer.parseInt(inputMap_ori.get("c_company_sender")[0]));
				
				// get the pickup agent id
				String pickUpAgent = "";
				boolean pickupagentfound= false;
				pst =conn.prepareStatement("select c_assigned_pickup_agent from kbcustomers where c_id=? and c_assigned_pickup_agent >0 ");
				pst.setString(1, custid);
				rs = pst.executeQuery();
				if (rs.next()) {
					pickUpAgent = rs.getString("c_assigned_pickup_agent");
					pickupagentfound = true;
				}
				try {rs.close();}catch(Exception e) {/*ignore*/}
				try {pst.close();}catch(Exception e) {/*ignore*/}
				
				receiptAmtFromScreen = Double.parseDouble(inputMap_ori.get("c_receiptamt")[0]);
				if (receiptAmtFromScreen != Double.parseDouble(dataMapFromDB.get("c_receiptamt")))
					ut.changeReceiptPrice(conn, Integer.parseInt(caseid), receiptAmtFromScreen, userid);
				
				//check if agent share settled
				if(dataMapFromDB.get("c_agentsharesettled").equalsIgnoreCase("FULL"))
					agentShareSettled = true;
				
				if (pickupagentfound)
					pst = conn.prepareStatement("update p_cases set "
	            		+ " c_rcv_name=?	 	, c_rcv_hp=?	    	, c_rcv_state=? 	, c_rcv_district=?	, c_rural=?, "
	            		+ " c_rcv_addr_rmk=? 	, c_rmk=?				, c_qty=?			, c_receiptamt=?	, c_shipment_cost=?	, "
	            		+ " c_fragile=?			, c_custreceiptnoori=?	, c_agentshare=?	, c_custid=?	 	, c_assignedagent=?,"
	            		+ " c_pickupagent= ?    , c_company_sender = ? "
	            		+ " where c_id = ?");
				else
					pst = conn.prepareStatement("update p_cases set "
		            		+ " c_rcv_name=?	 	, c_rcv_hp=?	    	, c_rcv_state=? 	, c_rcv_district=?	, c_rural=?, "
		            		+ " c_rcv_addr_rmk=? 	, c_rmk=?				, c_qty=?			, c_receiptamt=?	, c_shipment_cost=?	, "
		            		+ " c_fragile=?			, c_custreceiptnoori=?	, c_agentshare=?	, c_custid=?	 	, c_assignedagent=? ,"
		            		+ " c_company_sender = ? "
		            		+ " where c_id = ?");
				
				pst.setString(1, inputMap_ori.get("c_rcv_name")[0]);
	            pst.setString(2, inputMap_ori.get("c_rcv_hp")[0]);
	            pst.setString(3, state);
	            pst.setString(4, district);
	            pst.setString(5, inputMap_ori.get("c_rural")[0]);
	            pst.setString(6, inputMap_ori.get("c_rcv_addr_rmk")[0]);
	            pst.setString(7, inputMap_ori.get("c_rmk")[0]);
	            pst.setString(8, inputMap_ori.get("c_qty")[0]);
	            if(agentShareSettled)
	            	pst.setString(9, dataMapFromDB.get("c_receiptamt"));
	            else
	            	pst.setString(9, inputMap_ori.get("c_receiptamt")[0]);
	            if(agentShareSettled)
	            	 pst.setString(10, dataMapFromDB.get("c_shipment_cost"));
	            else
	            	pst.setDouble(10, shipmentCost);
	            pst.setString(11, inputMap_ori.get("c_fragile")[0]);
	            pst.setString(12, inputMap_ori.get("c_custreceiptnoori")[0]);
	            if(agentShareSettled)
	            	pst.setString(13, dataMapFromDB.get("c_agentshare"));
	            else
	            	pst.setDouble(13, agentShareAmt);
	            pst.setString(14, custid);
	            pst.setString(15, inputMap_ori.get("c_assignedagent")[0]);
	            if (pickupagentfound) {
	            	pst.setString(16, pickUpAgent);
	            	pst.setString(17, inputMap_ori.get("c_company_sender")[0]);
	            	pst.setString(18, caseid);
	            	
	            }else {
	            	pst.setString(16, inputMap_ori.get("c_company_sender")[0]);
	            	pst.setString(17, caseid);
	            	
	            }
	            pst.executeUpdate();
	          //calculate shipment profit and partner share
	            ut.calcShipmentProfitAndPartnerShare(conn, Integer.parseInt(caseid));
	            conn.commit();
	            setUpdateErrorFlag(false);
			}else {
				 setUpdateErrorFlag(true);
				 msg = "لا يمكن التعديل على هذه الشحنه لانها تم المحاسبه عليها مع العميل";
			}
		}catch(Exception e){
			e.printStackTrace();
			msg = "Error at updated data, error("+ e.getMessage() +") ";
			 try{conn.rollback();}catch(Exception ex){}//end of inner catch
			 setUpdateErrorFlag(true);
		}finally{
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception ex) {}		
		}//end of finally
		
		return msg;
	}
}//end of class Updatecase
