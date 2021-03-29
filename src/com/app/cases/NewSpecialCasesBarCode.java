package com.app.cases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.app.bussframework.FlowUtils;
import com.app.core.CoreMgr;
import com.app.db.mysql;
import com.app.util.Utilities;
import com.mysql.jdbc.Statement;

public class NewSpecialCasesBarCode extends CoreMgr{
	
	private LinkedList <CaseInformation> cases ;
	
	public NewSpecialCasesBarCode () {
		// I NEED TO CREATE MULTI-INSERT-FORM
		
		setDisplayMode("NEWSINGLE");
		MainSql =  "select '' as barcode,  '' as assignagent, '' as createddtls, '' as c_cust_hp, '' as c_cust_name, '' as c_pickup_lat ,'' as c_pickup_longt , '' as usemap, "
				+ " '' as c_pickup_state , '' as c_pickup_city, '' as c_pickup_district , '' as c_pickup_more_location,"
				+ " '' as item1 , ''  "
				+ " from p_cases where 1=0"; 
		canNew = true;
		mainTable = "p_cases";
		cases = new LinkedList<CaseInformation>();
		userDefinedNewFormColNo = 2;
		userDefinedNewCols.add("barcode");
		userDefinedNewCols.add("c_pickup_state");
		userDefinedNewColsHtmlType.put("c_cust_hp", "TEXT");
		userDefinedNewColsHtmlType.put("c_cust_name","EDITABLE_SELECT");
		
		userDefinedLookups.put("rcv_state_1", "select st_code , st_name_ar from kbstate where st_active='Y' order by st_order");
		userDefinedLookups.put("rcv_broken_1", "select kbcode , kbdesc from kbgeneral where kbcat1='YESNO'");
		userDefinedLookups.put("c_cust_hp", "!select c_phone1, c_phone1 as ph  from kbcustomers where c_id='{c_cust_name}' ");
		
		userDefinedNewColsDefualtValues.put("c_pickup_state", new String[] {"%select store_code , store_name from kbstores where store_deleted='N' and ('{superRank}'='Y' or store_code='{userstorecode}') order by store_order"});
		
		userDefinedColLabel.put("c_cust_name", "أسم العميل");
		userDefinedColLabel.put("c_cust_hp", "رقم الهاتف");
			
		userDefinedColLabel.put("c_pickup_state","مخزن");
		userDefinedHiddenNewCols.add("c_pickup_state");
		
		userDefinedColsMustFill.add("c_cust_name");
		userDefinedColsMustFill.add("c_cust_hp");
		userDefinedColsMustFill.add("c_pickup_state");
		 userDefinedLookups.put("c_cust_name", "select c_id , c_name from kbcustomers where  c_id>0");
		newCaption = "خلق شحنه جديده";
	}
	@Override
	public void initialize(HashMap smartyStateMap){
		userDefinedLookups.put("c_cust_name", "select c_id , c_name from kbcustomers where  c_id>0");
		super.initialize(smartyStateMap);
		sqlColsSizes.put("c_cust_hp", 11);
	}
	
	public StringBuilder getNewForm(){//Generte THe New Form
		if (userDefinedNewCols.isEmpty())//To check if the userDefinedNewCols Had not been Set
			return new StringBuilder("The New Columns List is Not Set");
		String labelName=null;
		int rowNum = 0 ;
		boolean required=false;
		jsmgr.userDefinedColsHtmlType = userDefinedNewColsHtmlType;
		StringBuilder newForm = new StringBuilder("");
		String jsValidatorNumeric="" , enctype="", BackGroundColor="";
		ArrayList<String> displayValue = new ArrayList<String>();
		HashMap<String , String> tipsList = new HashMap<String ,String>();
		String Readonly="";
		boolean Disabled = false , hidden =false;
		String labelClass ="control-label col-md-2 col-sm-2 col-xs-12";
		String inputClass ="col-md-4 col-sm-4 col-xs-12";
		if (userDefinedNewFormColNo>=3){
			labelClass="control-label col-md-1 col-sm-1 col-xs-12";
			inputClass = "col-md-3 col-sm-3 col-xs-12";
		}
		for (String key :userDefinedNewCols){
			 if (blobList.contains(allSqlColsTypes.get(key)) || blobList.contains(userDefinedNewColsHtmlType.get(key))){
				 if (!userDefinedStoreFileNameColumns.containsKey(key))
					 return new StringBuilder("Col=>"+key+", is a blob , and does not have symetric col to save the name"
					 		+ "</br> please set the userDefinedStoreFileNameColumns");
				 enctype = "enctype='multipart/form-data'";
				 break;
			 }
		}
		newForm.append("<div class='row'><div class='col-md-12 col-sm-12 col-xs-12' style='padding-left:0px;padding-right:0px;'><div class='x_panel'>");
		newForm.append("<div class='x_title'> <h2>"+newCaption+"</h2><div class='clearfix'></div></div>");
		newForm.append("<div class='x_content'><br />"
		+ "<form id='"+myClassBean.replace(".", "_dot_")+"' name='"+myClassBean+"' action='?myClassBean="+myClassBean+"&new=1' method='POST'"
		+ " data-parsley-validate class='form-horizontal form-label-left' "+enctype+">");
		boolean startFieldSet = false;
	
		
		for (String removeFileNameCol : userDefinedStoreFileNameColumns.keySet()){
			if (userDefinedNewCols.contains(removeFileNameCol)){
				if (userDefinedNewCols.contains(userDefinedStoreFileNameColumns.get(removeFileNameCol)))
					userDefinedNewCols.remove(userDefinedStoreFileNameColumns.get(removeFileNameCol));
				else
					return new StringBuilder(removeFileNameCol+" does not have userDefinedStoreFileNameColumns column");
			}
		}
		
		for (String key :userDefinedNewCols){
			 if (!userDefinedHiddenNewCols.contains(key)){// if not hidden	
				 
				 if(userDefinedFieldSetCols!=null)
					 if (userDefinedFieldSetCols.containsKey(key)){
						 newForm.append("<fieldset class='scheduler-border'>");
						 newForm.append("<legend class='scheduler-border'>"+userDefinedFieldSetCols.get(key)+"</legend>");
						 startFieldSet = true;
					 }
				 if (userDefinedNewFormColNo==1){
					 newForm.append("<div class='form-group'>");
				 }else if (rowNum%userDefinedNewFormColNo==0 || rowNum==0){
					 newForm.append("<div class='form-group'>");
				 }	 
				 rowNum++; 
				if (!userDefinedColLabel.containsKey(key))
					labelName = key;
				else
					labelName = userDefinedColLabel.get(key);

				if (numberList.contains(allSqlColsTypes.get(key)))
					jsValidatorNumeric = jsValidatorNumeric +jsmgr.genJSNumericValidation(key , userDefinedColLabel.get(key));
				
				required = false;
				if (userDefinedColsMustFill.contains(key)){//check for the must fill
					required = true;
					//HtmlInForm = HtmlInForm + " <font size='4' color='red'>*</font>";
					//jsValidatorMustFill= jsValidatorMustFill+jsmgr.genJSMustFill(key , userDefinedColLabel.get(key));
				}
				newForm.append("<label id='"+key+"_label' class='"+labelClass+"' >"+labelName);
				if(required)
					newForm.append("<span class='required'> *</span>");
				newForm.append("</label>");
				newForm.append("<div class='"+inputClass+"' div_fornew_input_smarty='smarty_newcol_"+key+"'>");
				if (userColHintEDIT !=null){
					if (userColHintEDIT.containsKey(key)){
						    tipsList.put(key, "a_left_"+key);
						    newForm.append("<a id='a_left_"+key+"' href='#' >"+
									"<img src='../img/help.jpg' height =17 width=15 border=0></img></a>"+
									"<div id='tip1_left_"+key+"' style='display:none;'>"+
									"<pre class='tip'>"+userColHintEDIT.get(key)+"</pre></div>");
					}
				}
				displayValue.clear();
				if (userDefinedNewColsDefualtValues.containsKey(key))
					if(userDefinedNewColsDefualtValues.get(key)!=null)
						for (String val : userDefinedNewColsDefualtValues.get(key))
							displayValue.add(val);
				
				Readonly="";
				if(userDefinedReadOnlyNewCols.contains(key))
					Readonly = "readonly";
				else
					Readonly = "";
					
				if (userDefinedDisabledNewCols.contains(key))
					Disabled = true;
				else
					Disabled = false;
				
				hidden = false;
				
				if (userDefinedColsMustFill.contains(key)){//check for the must fill
					BackGroundColor="#FFFFB8";
				}else{
					BackGroundColor="#FFFAFF";
				}
				newForm.append(myhtmlmgr.GetHtmlInput(userDefinedNewColsHtmlType, colMapValues,
												   key						 , displayValue , 
												   sqlColsSizes 			 , Readonly , 
												   Disabled				     , userDefinedLookups,
												   BackGroundColor			 , hidden,
												   null						 , required,
												   false					 , 0,
												   userDefinedMinDateSelect  , userDefinedMaxDateSelect));		
				newForm.append("</div>");
				if (userDefinedNewFormColNo==1){
					 newForm.append("</div>"); //end of form group	 
				 }else if ((rowNum)%userDefinedNewFormColNo==0 && rowNum>1){
					 newForm.append("</div>");
				 }
				 if (startFieldSet){
					 if (userDefinedFieldSetEndWithCols.contains(key)){
						 startFieldSet = false;
						 newForm.append("</fieldset>");
						 rowNum = userDefinedNewFormColNo;
					 }
				 }
			}else{// if hidden
				displayValue.clear();
				if (userDefinedNewColsDefualtValues.containsKey(key))
					if(userDefinedNewColsDefualtValues.get(key)!=null)
						for (String val : userDefinedNewColsDefualtValues.get(key))
							displayValue.add(val);
				hidden = true;
				newForm.append( 
						myhtmlmgr.GetHtmlInput(userDefinedNewColsHtmlType, colMapValues,
											   key						 , displayValue , 
											   sqlColsSizes 			 , Readonly , 
											   Disabled				     , userDefinedLookups,
											   BackGroundColor			 , hidden,
											   null						 , false,
											   false					 , 0,
											   userDefinedMinDateSelect  , userDefinedMaxDateSelect));
			}//end of hidden
			 newForm.append(genHotLookupsjs(key , required , false));
			 
		}//end of cols loop
		if (!((rowNum)%userDefinedNewFormColNo==0 && rowNum>1)){
			 newForm.append("</div>");
		}
		if (startFieldSet){
			newForm.append("</fieldset>");
		}
		
		//add the customized table here
		 newForm.append("<fieldset class='scheduler-border' style='width:99%'>");
		 newForm.append("<legend class='scheduler-border'>تفاصيل الشحنات</legend>");
		 //System.out.println("----->"+cases);
		 newForm.append("<table class='table table-bordered table-striped' id='rcv_dtls'><tr></tr>");
		 
		 for (int c = 1 ; c<=cases.size(); c++)
			try {
				newForm.append(getRCVDetailsRow(c, cases.get(c).getCustReceiptNoOri()));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 newForm.append("</table>");
		 //newForm.append(getRCVTableDtls());
		
		 newForm.append("</fieldset>");
		
		
		newForm.append("<div class='ln_solid'></div>");
		newForm.append(" <div class='form-group'>");
			newForm.append("<div class='col-md-6 col-sm-6 col-xs-12 col-md-offset-5'>");
				
				newForm.append("<button type='submit' id='save_new_form_"+myClassBean+"' value='save' class='btn btn-success save_form_btn'>خلق الشحنه</button>");
		newForm.append("</div>");
		newForm.append("</div>");
		newForm.append("</form>");//End of Form
		
		newForm.append("<script> var frmvalidator  = new Validator('"+myClassBean+"');\n");  
		newForm.append(jsValidatorNumeric);
		for (String param : jsmgr.colsUsedInLookupsForOtherCols.keySet()){
			newForm.append(jsmgr.getHotLookupCallingScript(param));
		}
		
		newForm.append("</script>");
		if (tipsList !=null){
			newForm.append("<script>$(document).ready(function() {");
			for (String key : tipsList.keySet()){
			//"a1_left_"+key
				newForm.append("$('#"+tipsList.get(key)+"').bubbletip($('#tip1_left_"+key+"'), {"+
							"deltaDirection: 'right',"+
							"animationDuration: 100,"+
							"offsetLeft: -20"+
						"});");
			}
			newForm.append("});</script>");
		}
		newForm.append("</div></div></div>");
		return newForm;
	}
		
	public StringBuilder getRCVDetailsRow(int rcvSeq, String rcpNo) throws Exception {
		StringBuilder sb = new StringBuilder();
		
		userDefinedMultiNewRowExtension = "smartyNewRow_"+rcvSeq;
		String stylediv = "style=''";
		Utilities ut = new Utilities();
		boolean ruralArea = false;
		int custId =0;
		//Receiver Name
		String defaultRcvName = "", fragile = "" , rcvPhone="07", destState="BGD", rcvDistrict="", rural ="N" , rmk="", locDtls="", brinBackItmes="", 
				receiptAmt="", qty="1", shipmentCost = "0" , receiptNo=rcpNo, custName = "", agentShare="" ;
		
		int smarty_new_row_seq= rcvSeq;
		if (!cases.isEmpty() && cases.get(rcvSeq-1)!=null) {
			CaseInformation ci = cases.get(rcvSeq-1);
			defaultRcvName = ci.getName();
			rcvPhone = ci.getHp();
			
			rural = ci.getRural();
			locDtls = ci.getLocationDetails();
			rmk = ci.getRmk();
			
			qty=ci.getQty()+"";
			fragile = ci.getFragile();
			
			receiptAmt = ci.getReceiptAmt()+"";
			shipmentCost = ci.getShipmentCharge()+"";
			receiptNo = ci.getCustReceiptNoOri();
			smarty_new_row_seq = ci.getSmarty_new_row_seq();
			agentShare   = ci.getAgentShare()+"";
			rcvDistrict = ci.getDistrict();
			destState = ci.getState();
		}
		
		Connection conn1 = null;
		boolean receiptInSystem = false, receiptUsedBefore = false;
		try {
			conn1 = mysql.getConn();
			receiptInSystem = ut.checkIfReceiptGeneratedFromSystem(conn1, receiptNo);
			if (!receiptInSystem)
				throw new Exception ("الوصل رقم "+receiptNo+" غير متولد من النظام");
			
			receiptUsedBefore = ut.checkIfReceiptUsedBefore(conn1, receiptNo);
			if (receiptUsedBefore)
				throw new Exception ("الوصل رقم "+receiptNo+" تم أستعماله سابقا");
			
			custId = ut.getOwnerOfReceipt(conn1, receiptNo);
			if (custId>0)
				custName = ut.getCustomerName(conn1, custId);
			if (rural.equalsIgnoreCase("Y"))
				ruralArea = true;
			//shipmentCost = Double.toString(ut.calcShipmentChargesBasedOnDestCity(conn1,  destState, ruralArea, custId));
		} catch (Exception e) {
			throw e;
		}finally {
			try {conn1.close();}catch(Exception e) {}
		}
		sb.append( "<tr id='"+userDefinedMultiNewRowExtension+"' rcv_no = '"+smarty_new_row_seq+"' style=\"border-bottom: 2px solid;\">");
		sb.append("<input type='hidden' id='smarty_new_row_seq__"+smarty_new_row_seq+"' name='smarty_new_row_seq__"+smarty_new_row_seq+"'  value='"+smarty_new_row_seq+"'/>");
		
		sb.append("<div class='form-horizontal form-label-right'>");
		
		// goods cost
		sb.append("<td style='padding-top:15px;'>"
						+ "<div class='form-group'>"
						+ " <div class='col-md-1 col-sm-1 col-xs-3' style='margin-left:0%;margin-right:1%;'><label class='control-label'>مبلغ الوصل</label>"
								+ "<input type='text' value='"+receiptAmt+"'  class='form-control'   "
										+ "style='text-align:right; background-color:#FFFFB8; color: #424242;width:5em;' name='c_receiptamt_"+userDefinedMultiNewRowExtension+"'"
										+ " id ='c_receiptamt_"+userDefinedMultiNewRowExtension+"' required onkeyup='formatMe(this);'/>"
												+ ""
												+ "</div>");
		// Receipt no	
		sb.append("<div class='col-md-1 col-sm-1 col-xs-3 ' style='margin-left:1%;margin-right:0%;'><label class='control-label'>رقم الوصل</label>"
								+ "<input type='number'  class='form-control' readonly value='"+receiptNo+"' min='0' style='text-align:right; background-color:#eee; color: #424242;width:10em;' "
										+ " oninput=\"this.value = Math.abs(this.value)\" size='10' onblur='getCustomerOfReceipt(this.value,"+rcvSeq+")' name='c_custreceiptnoori_"+userDefinedMultiNewRowExtension+"' "
												+ "id ='c_custreceiptnoori_"+userDefinedMultiNewRowExtension+"' required /></div>");
		
		String style = "text-align:right; background-color:#FFFFB8; padding: 0 10px 0 10px;"+
    			"  color: #424242; border: 1px solid #7dc6dd;min-width:150px";
		
		try {
			conn1 = mysql.getConn();
			colMapValues= mysqlmgr.loadAllLookups(conn1,userDefinedLookups);
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {conn1.close();}catch(Exception e) {}
		}
		
		if (custId>0) {
			sb.append("<div class='col-md-1 col-sm-1 col-xs-3 ' style='margin-left:2%;margin-right:2%;'><label class='control-label'>الزبون</label>"
					+ "<input type='text'  class='form-control' readonly value='"+custName+"' style='text-align:right; background-color:#eee; color: #424242;width:12em;' "
							+ "  name='c_custreceiptnoori_"+userDefinedMultiNewRowExtension+"' id ='c_custreceiptnoori_"+userDefinedMultiNewRowExtension+"' />");
			sb.append("<input type='hidden' class='form-control'  name='custid_"+userDefinedMultiNewRowExtension+"' id ='custid_"+userDefinedMultiNewRowExtension+"'  "
							+ " value='"+custId+"'  /></div>");
		}else {
			//customer drop list
			sb.append("<div class='col-md-1 col-sm-1 col-xs-3 ' style='margin-left:7%;margin-right:2%;'><label class='control-label'>الزبون</label>"
						+ "<select class='form-control select2_single'   id='custid_"+userDefinedMultiNewRowExtension+"'" + 
							"  name='custid_"+userDefinedMultiNewRowExtension+"' style='"+style+"' required>");
			
			Map <String , String> lookupsmap = colMapValues.get("c_cust_name");
			sb.append("<option value='' selected></option> \n");
			if (lookupsmap !=null){
				if (!lookupsmap.isEmpty()){
					for (String code : lookupsmap.keySet()){
						sb.append("<option value='"+code+"' >"+lookupsmap.get(code)+"</option> \n");
					}
				}
			}
			sb.append("</select></div> \n");
	}
				
		//state
		sb.append("<div class='col-md-1 col-sm-1 col-xs-3 ' style='margin-left:3%;margin-right:3%;'><label class='control-label'>المدينه</label>"
				+ ""
				+ "<select class='form-control select2_single'  onchange='loadDistrict("+rcvSeq+");' id='rcv_city_"+userDefinedMultiNewRowExtension+"'" + 
				"  name='rcv_city_"+userDefinedMultiNewRowExtension+"' style='"+style+"' required>");
		
		
		LinkedHashMap<String, String>lookupsmap = colMapValues.get("rcv_state_1");
		if (lookupsmap !=null){
			if (!lookupsmap.isEmpty()){
				for (String code : lookupsmap.keySet()){
					if (destState.equalsIgnoreCase(code))
						sb.append("<option value='"+code+"' selected>"+lookupsmap.get(code)+"</option> \n");
					else
						sb.append("<option value='"+code+"' >"+lookupsmap.get(code)+"</option> \n");
				}
			}
		}
		sb.append("</select></div> \n");
		
		// district inside state
		LinkedHashMap<String,String> district = new LinkedHashMap<String,String> ();
		try {
			try {conn1.close();}catch(Exception e) {}
			conn1 = mysql.getConn();
			district  = ut.getDistrictOfState(conn1,"BGD");
			try {conn1.close();}catch(Exception e) {}
		} catch (Exception e) {
			try {conn1.close();}catch(Exception eClose) {}
			e.printStackTrace();
			throw e;
		}finally {
			try {conn1.close();}catch(Exception e) {}
		}
		if (destState.equalsIgnoreCase("BGD"))
			sb.append("<div class='col-md-1 col-sm-1 col-xs-3' style='margin-right:3%;margin-left:3%;'><label class='control-label'>المنطقه</label>"
					+ "<select class='form-control select2_single'   id='rcv_district_"+userDefinedMultiNewRowExtension+"'" + 
					"  name='rcv_district_"+userDefinedMultiNewRowExtension+"' style='"+style+"' required >");
		else
			sb.append("<div class='col-md-1 col-sm-1 col-xs-3' style='margin-right:3%;margin-left:3%;'><label class='control-label'>المنطقه</label>"
					+ "<select class='form-control select2_single'   id='rcv_district_"+userDefinedMultiNewRowExtension+"'" + 
					"  name='rcv_district_"+userDefinedMultiNewRowExtension+"' style='"+style+"' >");
			
		sb.append("<option value='' selected></option> \n");
		for (String code : district.keySet()){
			
			if (rcvDistrict.equalsIgnoreCase(code))
				sb.append("<option value='"+code+"' selected>"+district.get(code)+"</option> \n");
			else
				sb.append("<option value='"+code+"' >"+district.get(code)+"</option> \n");
		}
		sb.append("</select></div> \n");
				
		//location details
		sb.append("<div class='col-md-1 col-sm-1 col-xs-3' style='margin-right:3%;margin-left:3%;'><label class='control-label'>تفاصيل العنوان</label>"
				+"<textarea  class='form-control'  style='width:150px;'  name='rcv_more_loc_"+userDefinedMultiNewRowExtension+"' "
						+ "id ='rcv_more_loc_"+userDefinedMultiNewRowExtension+"'>"+locDtls+"</textarea></div>");

		//Notes
		sb.append("<div class='col-md-1 col-sm-1 col-xs-3' style='margin-right:3%'><label class='control-label'>ملاحظات</label>"
				+ "<textarea  class='form-control' style='width:150px'  name='rcv_rmk_"+userDefinedMultiNewRowExtension+"' "
						+ "id ='rcv_rmk_"+userDefinedMultiNewRowExtension+"'>"+rmk+"</textarea></div></div>");
		
		String checked = "";
		//Fragile
		if (fragile.equalsIgnoreCase("Y"))
			checked="checked";
		sb.append("<div class='form-group'><div class='col-md-1 col-sm-1 col-xs-3' style='margin-left:1%;margin-right:0%;'><label class='control-label'>قابل للكسر</label>"
						+ "</br>"
						+ "<input type='checkbox' "+checked+" class='form-check-input' value='Y' name='c_fragile_"+userDefinedMultiNewRowExtension+"' id ='c_fragile_"+userDefinedMultiNewRowExtension+"' /></div>");
		//get back items
		checked = "";
		if (brinBackItmes.equalsIgnoreCase("Y"))
			checked="checked";
		
		/* for TransportLine we don't need this 
		sb.append("<div class='col-md-1 col-sm-1 col-xs-3' "+stylediv+"><label class='control-label'>جلب  بضاعه</label>"+ "</br>"
				+ "<input type='checkbox'  "+checked+" class='form-check-input' value='Y' name='c_bringitemsback_"+userDefinedMultiNewRowExtension+"' id ='c_bringitemsback_"+userDefinedMultiNewRowExtension+"' /></div>");
		*/				
		//rural areas
		checked = "";
		if (rural.equalsIgnoreCase("Y"))
			checked="checked";
		
		sb.append("<div class='col-md-1 col-sm-1 col-xs-3' "+stylediv+"><label class='control-label'>أطراف</label>"
				+ "</br>"
				+ "<input type='checkbox' class='form-check-input' name='c_rural_"+userDefinedMultiNewRowExtension+"' "
						+ "id ='c_rural_"+userDefinedMultiNewRowExtension+"' "+checked+" value='Y' onclick=\"calcShipmentCost("+rcvSeq+");\" /></div>");
		
		
		// send money
		/*
		sb.append("<div class='col-md-1 col-sm-1 col-xs-3' style='margin-left:2%;margin-right:2%;'><label class='control-label'>إرسال مبلغ</label>"
						+ "<input type='number'  class='form-control' value='"+amountToSend+"' min='0'  style='width:8em;' "
								+ " oninput=\"this.value = Math.abs(this.value)\" size='10' name='c_sendmoney_"+userDefinedMultiNewRowExtension+"' id ='c_sendmoney_"+userDefinedMultiNewRowExtension+"'  /></div>");
		*/

		sb.append("<div class='col-md-1 col-sm-2 col-xs-3' "+stylediv+"><label class='control-label'>المستلم</label>"
				+ ""
				+ "<input type='text' class='form-control'  style='width:135px;text-align:right; color: #424242;' "
				+ " name='rcv_name_"+userDefinedMultiNewRowExtension+"' id ='rcv_name_"+userDefinedMultiNewRowExtension+"' size='15'  "
						+ " value='"+defaultRcvName+"'  /></div>");

		//Receiver Phone
		sb.append("<div class='col-md-1 col-sm-1 col-xs-3 col-md-offset-1' "+stylediv+"><label class='control-label'>هاتف</label>"
				+ ""
				+ "<input type='text' class='form-control' maxlength='13'  size='11'"
				+ " data-inputmask=\"'mask': '9999-999-9999'\" style='text-align:left; background-color:#FFFFB8; color: #424242;direction: ltr;'"
				+ "  size='11' value='"+rcvPhone+"' name='rcv_phone_"+userDefinedMultiNewRowExtension+"' id ='rcv_phone_"+userDefinedMultiNewRowExtension+"' required='required' /></div>");
		
		
		//No of Pieces
		sb.append("<div class='col-md-1 col-sm-1 col-xs-3 col-md-offset-1'><label class='control-label'>عدد القطع</label>"
				+ ""
				+ "<input type='number' class='form-control' min='1' value='"+qty+"' size='1' style='width:7em;text-align:right; background-color:#FFFFB8; color: #424242;'"
				+ " oninput=\"this.value = Math.abs(this.value)\"  name='rcv_qty_"+userDefinedMultiNewRowExtension+"' id ='rcv_qty_"+userDefinedMultiNewRowExtension+"' required='required' /></div>");
		
		
		//weight (Transport Line don't use weight) so we hide it in the screen but we keep the calculations as we might need it in future
			/*
			sb.append("<div class='col-md-1 col-sm-1 col-xs-3 col-md-offset-1'><label class='control-label'>الوزن</label>"
						+ ""
						+ "<input type='number' min='0' value='"+weight+"' step = 'any' class='flat form-control' "
						+ " name='c_weight_"+userDefinedMultiNewRowExtension+"' onkeyup=\"this.onchange();\" "
						+ " oninput=\"this.value = Math.abs(this.value)\" onchange='calcShipmentCost("+rcvSeq+");'"
								+ " style='width:7em;text-align:right; background-color:#FFFFB8; color: #424242;width:7em;' id ='c_weight_"+userDefinedMultiNewRowExtension+"' /></div>");
			*/
		// shipment cost
		
		//list of agents per state
		LinkedHashMap<String,String> agents = new LinkedHashMap<String,String> ();
		try{
			try {conn1.close();}catch(Exception e) {}
			conn1 = mysql.getConn();
			agents  = ut.getListOfAgentsPerState(conn1,destState);
			try {conn1.close();}catch(Exception e) {}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {conn1.close();}catch(Exception e) {}
		}
		if (destState.equalsIgnoreCase("BGD")) {
			sb.append("<div class='col-md-2 col-sm-1 col-xs-3' style='margin-right:1%;margin-left:1%;'><label class='control-label'>مندوب التوصيل بغداد فقط</label>"
					+ "<select class='form-control select2_single'   id='c_assignedagent_"+userDefinedMultiNewRowExtension+"'" + 
					"  name='c_assignedagent_"+userDefinedMultiNewRowExtension+"' style='"+style+"' required >");
		}else {
			sb.append("<div class='col-md-2 col-sm-1 col-xs-3' style='margin-right:1%;margin-left:1%;'><label class='control-label'>مندوب التوصيل بغداد فقط</label>"
					+ "<select class='form-control select2_single'   id='c_assignedagent_"+userDefinedMultiNewRowExtension+"'" + 
					"  name='c_assignedagent_"+userDefinedMultiNewRowExtension+"' style='"+style+"' >");
		}
			
		sb.append("<option value='' selected></option> \n");
		for (String code : agents.keySet()){
			sb.append("<option value='"+code+"' >"+agents.get(code)+"</option> \n");
		}
		sb.append("</select></div> \n");
		
		// agent share cost	
		sb.append("<div class='col-md-1 col-sm-1 col-xs-3' style=''><label class='control-label'>أجور المندوب</label>"
						+ "<input type='number' min='0'   class='form-control' value='"+agentShare+"' style='width:8em;background-color:#FFFFB8;'  size='10' name='c_agentshare_"+userDefinedMultiNewRowExtension+"' "
								+ "id ='c_agentshare_"+userDefinedMultiNewRowExtension+"' required  /></div>");
		
		// shipment cost	
		sb.append("<div class='col-md-1 col-sm-1 col-xs-3' style='margin-right:1%;'><label class='control-label'>مبلغ الشحن</label>"
						+ "<input type='number' min='0'   class='form-control' value='"+shipmentCost+"' style='width:8em;background-color:#FFFFB8;'  size='10' name='c_shipment_cost_"+userDefinedMultiNewRowExtension+"' "
								+ "id ='c_shipment_cost_"+userDefinedMultiNewRowExtension+"' required  /></div>");
			
		sb.append("</div>");
			
		sb.append("</div>");
		sb.append("<td style='width:4em;vertical-align:top;'><label class='control-label'>"+smarty_new_row_seq+"</label></br></br><button type='button' onclick='remove_row("+smarty_new_row_seq+")' class='btn btn-danger btn-xs'><li class='fa fa-trash'></li></button></td></tr>");
		try {conn1.close();}catch(Exception e) {}
		return sb;
	}
	
	
	@Override
	public String doInsert(HttpServletRequest rqs, boolean autocommit) { 
		FlowUtils fu = new FlowUtils();
		Connection conn = null;
		PreparedStatement pst = null, pstUpdateBook = null, pstUpdateReciept=null;
		ResultSet rs = null;
		String userid = replaceVarsinString(" {useridlogin} ", arrayGlobals).trim();
		String msg = "تم خلق الطلبيه بنجاح";
		Utilities ut = new Utilities();
		boolean ruralArea = false;
		try{
			inputMap_ori = filterRequest(rqs);
			LinkedList <Integer> availableCases= new LinkedList <Integer>();
			for (String key:inputMap_ori.keySet()){//loop to get all the cases from the grid
			    if(key.startsWith("smarty_new_row_seq__")) {
			    	availableCases.add(Integer.parseInt(inputMap_ori.get(key)[0]));
				}
			}
			conn = mysql.getConn();
			
			pst =conn.prepareStatement("select c_assigned_pickup_agent from kbcustomers where c_id=?");
			for (Integer j : availableCases) {
				
				CaseInformation ci = new CaseInformation();
				ci.setSmarty_new_row_seq(j);
				ci.setCustId(Integer.parseInt(inputMap_ori.get("custid_"+userDefinedMultiNewRowExtension+"_"+j)[0])); //custid
				ci.setName(inputMap_ori.get("rcv_name_"+userDefinedMultiNewRowExtension+"_"+j)[0]); //rcv name
				ci.setHp(inputMap_ori.get("rcv_phone_"+userDefinedMultiNewRowExtension+"_"+j)[0]); // rcv phone
				
				ci.setState(inputMap_ori.get("rcv_city_"+userDefinedMultiNewRowExtension+"_"+j)[0]);// rcv city
				if (ci.getState().equalsIgnoreCase("BGD")) {
					if (inputMap_ori.get("c_assignedagent_"+userDefinedMultiNewRowExtension+"_"+j) !=null 
							&& inputMap_ori.get("c_assignedagent_"+userDefinedMultiNewRowExtension+"_"+j)[0]!=null
								&& inputMap_ori.get("c_assignedagent_"+userDefinedMultiNewRowExtension+"_"+j)[0].trim().length()>0)
						ci.setAssignedDLVAgent(inputMap_ori.get("c_assignedagent_"+userDefinedMultiNewRowExtension+"_"+j)[0]);
				}else {
					ci.setAssignedDLVAgent("0");
				}
				ci.setDistrict(inputMap_ori.get("rcv_district_"+userDefinedMultiNewRowExtension+"_"+j)[0]);
				ci.setLocationDetails(inputMap_ori.get("rcv_more_loc_"+userDefinedMultiNewRowExtension+"_"+j)[0]);//location dtls
				ci.setRmk(inputMap_ori.get("rcv_rmk_"+userDefinedMultiNewRowExtension+"_"+j)[0]); // remarks
				ci.setQty(Integer.parseInt(inputMap_ori.get("rcv_qty_"+userDefinedMultiNewRowExtension+"_"+j)[0])); //no of items
				
				ci.setReceiptAmt(Double.parseDouble(inputMap_ori.get("c_receiptamt_"+userDefinedMultiNewRowExtension+"_"+j)[0].replace(",","")));//c_goods_cost_
				
				ci.setCustReceiptNoOri(inputMap_ori.get("c_custreceiptnoori_"+userDefinedMultiNewRowExtension+"_"+j)[0]);
				if (inputMap_ori.containsKey("c_rural_"+userDefinedMultiNewRowExtension+"_"+j)&&(inputMap_ori.get("c_rural_"+userDefinedMultiNewRowExtension+"_"+j)[0] !=null))
					ci.setRural("Y"); // rural
				else
					ci.setRural("N");
				
				if (inputMap_ori.containsKey("c_fragile_"+userDefinedMultiNewRowExtension+"_"+j)&&(inputMap_ori.get("c_fragile_"+userDefinedMultiNewRowExtension+"_"+j)[0] !=null))
					ci.setFragile("Y"); // fragile
				else
					ci.setFragile("N");
				
				ruralArea = false;
				if (ci.getRural().equalsIgnoreCase("Y"))
					ruralArea = true;
				
				ci.setAgentShare(Double.parseDouble(inputMap_ori.get("c_agentshare_"+userDefinedMultiNewRowExtension+"_"+j)[0]));
				ci.setShipmentCharge(Double.parseDouble(inputMap_ori.get("c_shipment_cost_"+userDefinedMultiNewRowExtension+"_"+j)[0]));
				ci.setRemainingAmt(ci.getReceiptAmt()-ci.getShipmentCharge());//we use this to set the balance same as goods cost
				
				
				pst.setInt(1, ci.getCustId());
				rs = pst.executeQuery();
				if (rs.next())
					ci.setPickupAgent(rs.getInt("c_assigned_pickup_agent"));
				try {rs.close();}catch(Exception e) {/*ignore*/}
				pst.clearParameters();
				
				cases.add(ci);
			}
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
			
			
			pst = conn.prepareStatement("insert into p_cases "
					+ " (c_createdby	    , c_rcv_name	 	 , c_rcv_hp	  		, c_rcv_state		, c_rural		, "
					+ "	 c_rcv_addr_rmk	    , c_rmk 		 	 , c_qty  		 	, c_receiptamt		, c_shipment_cost, "
					+ "  c_goodscostbalance , c_fragile  		 , c_branchcode		, c_custreceiptnoori, c_agentshare	, "
					+ "  c_rcv_district	    , c_custid		 	 , c_custhp			, c_pickupagent     , c_assignedagent, "
					+ "  c_receiptfromsystem, c_specialcase		 , c_createddt	 	)"
			+ " values  (?			    	, ?			     	 , ?		 		, ?					, ?				, "
			+ "			 ?				    , ?			    	 , ?		 		, ?					, ?				, "
			+ "			 ?				    , ?              	 , ?	 			, ?					, ?				, "
			+ "			 ?				 	, ?		 			 , ?	 			, ?					, ? 			, "
			+ "			'Y'				 	, 'Y'				 , (now()+INTERVAL 9 HOUR) 	 )",
			Statement.RETURN_GENERATED_KEYS);
			
			pstUpdateReciept 	= conn.prepareStatement("update p_books_rcp set br_cid=?, br_custid=? where br_rcp_no=? ");
			pstUpdateBook	 	= conn.prepareStatement("update p_books set b_usedinsystem='Y' where b_id in"
													+ "  (select br_bid from p_books_rcp where br_rcp_no=?) ");
			
			CaseInformation ci = new CaseInformation();
			LinkedList <CaseInformation> casesWithIssueList = new LinkedList <CaseInformation>() ;
			HashMap<String ,String> casesWithIssues = new HashMap<String ,String>();
			for (int i =0; i<cases.size(); i++) {
				try {
					ut.checkGeneratedReceipt(conn,cases.get(i).getCustReceiptNoOri(), cases.get(i).getCustId());
				}catch(Exception e) {
					casesWithIssueList.add(cases.get(i));
					casesWithIssues.put(cases.get(i).getCustReceiptNoOri(), e.getMessage());
				}
			}
			if (casesWithIssueList.isEmpty()) {
				for (int i =0; i<cases.size(); i++) {
					ci = cases.get(i);
					
					pst.setString(1,userid);
					pst.setString(2, ci.getName());
					pst.setString(3, ci.getHp());
					pst.setString(4, ci.getState());
					pst.setString(5, ci.getRural());
					pst.setString(6, ci.getLocationDetails());
					pst.setString(7, ci.getRmk());
					pst.setInt(8, ci.getQty());
					pst.setDouble(9, ci.getReceiptAmt()*1000);//this is special req, so the user can insert without adding the 000
					pst.setDouble(10, ci.getShipmentCharge());
					pst.setDouble(11, ci.getRemainingAmt()*1000);
					pst.setString(12, ci.getFragile());
					pst.setString(13, "BGD");
					pst.setString(14, ci.getCustReceiptNoOri());
					pst.setDouble(15, ci.getAgentShare());
					pst.setString(16, ci.getDistrict());
					pst.setInt(17, ci.getCustId());
					pst.setString(18, "");
					pst.setInt(19, ci.getPickupAgent());
					pst.setString(20, ci.getAssignedDLVAgent());
					pst.executeUpdate();
	
					rs = pst.getGeneratedKeys();
					if (rs.next())
						ci.setCaseid(rs.getInt(1));
					else
						throw new Exception ("No case id generate");
						
						
					
					pstUpdateBook.setString(1, ci.getCustReceiptNoOri());
					pstUpdateBook.executeUpdate();
					pstUpdateBook.clearParameters();
					
					//update the receipt to map with caseid
					pstUpdateReciept.setInt(1, ci.getCaseid());
					pstUpdateReciept.setInt(2, ci.getCustId());
					pstUpdateReciept.setString(3, ci.getCustReceiptNoOri());
					pstUpdateReciept.executeUpdate();
					pstUpdateReciept.clearParameters();
					
					fu.createNewCaseInQueue(conn,ci.getCaseid(), "BGD");
					try {rs.close();}catch(Exception e) {/*ignore*/}
					pst.clearParameters();
				}
				conn.commit();
				cases.clear();
			}else {
				String errors = "";
				for (String cid : casesWithIssues.keySet()) {
					errors += casesWithIssues.get(cid)+"</br>";
				}
				throw new Exception (errors);
			}
		}catch(Exception e) {
			try {conn.rollback();}catch(Exception eRoll) {/**/}
			e.printStackTrace();
			msg = "Error "+e.getMessage()+"";
			setInsertErrorFlag(true);
		}finally {
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
			try {pstUpdateReciept.close();}catch(Exception e) {/*ignore*/}
			try {pstUpdateBook.close();}catch(Exception e) {/*ignore*/}
			
			
			try {conn.close();}catch(Exception e) {/*ignore*/}
		}
		
		 return msg;
	}
}

