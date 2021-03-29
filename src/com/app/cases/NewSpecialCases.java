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
public class NewSpecialCases extends CoreMgr{
	
	private LinkedList <CaseInformation> cases ;
	
	public NewSpecialCases () {
		// I NEED TO CREATE MULTI-INSERT-FORM
		
		setDisplayMode("NEWSINGLE");
		MainSql =  "select '' as sendercompany,'' as c_createddt , '' as createddtls, '' as c_cust_hp, '' as c_cust_name, '' as c_pickup_lat ,'' as c_pickup_longt , '' as usemap, "
				+ " '' as c_pickup_state , '' as c_pickup_city, '' as c_pickup_district , '' as c_pickup_more_location,"
				+ " '' as item1 , ''  "
				+ " from p_cases where 1=0"; 
		canNew = true;
		mainTable = "p_cases";
		cases = new LinkedList<CaseInformation>();
		userDefinedNewFormColNo = 2;
		
		userDefinedNewCols.add("c_cust_name");
		userDefinedNewCols.add("c_cust_hp");
		userDefinedNewCols.add("sendercompany");
		userDefinedNewCols.add("c_createddt");
		
		//userDefinedNewColsHtmlType.put("c_pickup_state", "DROPLIST");		
		userDefinedNewColsHtmlType.put("c_cust_hp", "TEXT");
		userDefinedNewColsHtmlType.put("c_cust_name","EDITABLE_SELECT");
		userDefinedNewColsHtmlType.put("c_createddt","DATE");
		
		userDefinedLookups.put("rcv_state_1", "select st_code , st_name_ar from kbstate where st_active='Y' order by st_order");
		userDefinedLookups.put("rcv_broken_1", "select kbcode , kbdesc from kbgeneral where kbcat1='YESNO'");
		userDefinedLookups.put("sendercompany", "SELECT comp_id, comp_name FROM kbcompanies");
		//userDefinedLookups.put("c_cust_name", "!select c_id , c_name from kbcustomers where  c_belongtostor='{c_pickup_state}'");
		userDefinedLookups.put("c_cust_hp", "!select c_phone1, c_phone1 as ph  from kbcustomers where c_id='{c_cust_name}' ");
		//userDefinedLookups.put("cm_has_parties", "select kbcode,kbdesc from kbgeneral where kbcat1='YESNO'");
		
		userDefinedNewColsDefualtValues.put("c_pickup_state", new String[] {"%select store_code , store_name from kbstores where store_deleted='N' and ('{superRank}'='Y' or store_code='{userstorecode}') order by store_order"});
		//userDefinedReadOnlyNewCols.add("c_pickup_state");
		
		userDefinedColLabel.put("c_cust_name", "أسم العميل");
		userDefinedColLabel.put("c_cust_hp", "رقم الهاتف");
		userDefinedColLabel.put("c_createddt", "تاريخ الوجبة");
		
		userDefinedColLabel.put("c_pickup_state","مخزن");
		userDefinedColLabel.put("sendercompany", "الشركة المرسلة");
		
		userDefinedColsMustFill.add("c_cust_name");
		userDefinedColsMustFill.add("c_cust_hp");
		userDefinedColsMustFill.add("c_createddt");
		userDefinedColsMustFill.add("sendercompany");
		
		newCaption = "خلق شحنه خاصة";
		userDefinedNewColsDefualtValues.put("sendercompany", new String [] {"2"});
	}
	@Override
	public void initialize(HashMap smartyStateMap){
		 userDefinedLookups.put("c_cust_name", "select c_id , c_name from kbcustomers ");
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
		 newForm.append("<table class='table table-bordered table-striped' id='rcv_dtls'>");
		 newForm.append(getRCVDetailsRow(1));
		 for (int c = 2 ; c<=cases.size(); c++)
			 newForm.append(getRCVDetailsRow(c));
		 newForm.append("</table>");
		 //newForm.append(getRCVTableDtls());
		 newForm.append("</br><button type='button' id='add_rcv_dtls' class='btn btn-warning' >إضافة مستلم</button> ");
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
	// to return table representing the recevier and shipments details
	
	
	public StringBuilder getRCVDetailsRow(int rcvSeq) {
		StringBuilder sb = new StringBuilder();
		userDefinedMultiNewRowExtension += "_"+rcvSeq;
		
		String stylediv = "style=''";
		Utilities ut = new Utilities();
		
		
		//Receiver Name
		String defaultRcvName = "", fragile = "" , rcvPhone="07", destState="BAS", rcvDistrict="", rural ="N" , rmk="", locDtls="", 
				 receiptAmt="", qty="1", shipmentCost = "" , receiptNo="", agentShare="" ;
		
		
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
			agentShare   = ci.getAgentShare()+"";
			receiptNo = ci.getCustReceiptNoOri();
			smarty_new_row_seq = ci.getSmarty_new_row_seq();
			
			rcvDistrict = ci.getDistrict();
		}
		
		Connection conn2 = null;
		try {
			conn2 = mysql.getConn();
			
			//shipmentCost = Double.toString(ut.calcShipmentChargesBasedOnDestCity(conn2,  destState, ruralArea, custName));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}finally {
			try {conn2.close();}catch(Exception e) {}
		}
		sb.append( "<tr id='"+userDefinedMultiNewRowExtension+"' rcv_no = '"+smarty_new_row_seq+"' style=\"border-bottom: 2px solid;\">");
		sb.append("<input type='hidden' id='smarty_new_row_seq__"+smarty_new_row_seq+"' name='smarty_new_row_seq__"+smarty_new_row_seq+"'  value='"+smarty_new_row_seq+"'/>");
		
		sb.append("<div class='form-horizontal form-label-right'>");
		
		// goods cost
				sb.append("<td style='padding-top:15px;'>"
						+ "<div class='form-group'>"
						+ " <div class='col-md-1 col-sm-1 col-xs-3' style='margin-left:1%;margin-right:1%;'><label class='control-label'>مبلغ الوصل</label>"
								+ "<input type='text' value='"+receiptAmt+"'  class='form-control'  "
										+ "style='text-align:right; background-color:#FFFFB8; color: #424242;width:8em;' name='c_receiptamt_"+userDefinedMultiNewRowExtension+"'"
										+ " id ='c_receiptamt_"+userDefinedMultiNewRowExtension+"' required onkeyup='formatMe(this);'/>"
												+ ""
												+ "</div>");
		// reciept no	
				sb.append("<div class='col-md-1 col-sm-1 col-xs-3 ' style='margin-left:2%;margin-right:2%;'><label class='control-label'>رقم الوصل</label>"
								+ "<input type='number'  class='form-control' value='"+receiptNo+"' min='0' style='text-align:right; background-color:#FFFFB8; color: #424242;width:8em;' "
										+ " oninput=\"this.value = Math.abs(this.value)\" size='10' name='c_custreceiptnoori_"+userDefinedMultiNewRowExtension+"' "
												+ "id ='c_custreceiptnoori_"+userDefinedMultiNewRowExtension+"' required /></div>");
		//state
		String style = "text-align:right; background-color:#FFFFB8; padding: 0 10px 0 10px;"+
    			"  color: #424242; border: 1px solid #7dc6dd;min-width:150px";
		sb.append("<div class='col-md-1 col-sm-1 col-xs-3 ' style='margin-left:2%;margin-right:2%;'><label class='control-label'>المدينه</label>"
				+ ""
				+ "<select class='form-control select2_single'  onchange='loadDistrict("+rcvSeq+");' id='rcv_city_"+userDefinedMultiNewRowExtension+"'" + 
				"  name='rcv_city_"+userDefinedMultiNewRowExtension+"' style='"+style+"' required>");
		
		Connection conn1 = null;
		try {
			if (colMapValues==null) {
				conn1 = mysql.getConn();
				colMapValues= mysqlmgr.loadAllLookups(conn1,userDefinedLookups);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {conn1.close();}catch(Exception e) {}
		}
		Map <String , String> lookupsmap = colMapValues.get("rcv_state_1");
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
			conn1 = mysql.getConn();
			district  = ut.getDistrictOfState(conn1,"BAS");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {conn1.close();}catch(Exception e) {}
		}
		sb.append("<div class='col-md-1 col-sm-1 col-xs-3' style='margin-right:7%;margin-left:2%;'><label class='control-label'>المنطقه</label>"
				+ "<select class='form-control select2_single'   id='rcv_district_"+userDefinedMultiNewRowExtension+"'" + 
				"  name='rcv_district_"+userDefinedMultiNewRowExtension+"' style='"+style+"' required >");
		
		sb.append("<option value='' selected></option> \n");
		for (String code : district.keySet()){
			
			if (rcvDistrict.equalsIgnoreCase(code))
				sb.append("<option value='"+code+"' selected>"+district.get(code)+"</option> \n");
			else
				sb.append("<option value='"+code+"' >"+district.get(code)+"</option> \n");
		}
		sb.append("</select></div> \n");
				
		//location details
		sb.append("<div class='col-md-1 col-sm-1 col-xs-3 col-md-offset-1' ><label class='control-label'>تفاصيل العنوان</label>"
				+"<textarea  class='form-control'   name='rcv_more_loc_"+userDefinedMultiNewRowExtension+"' "
						+ "id ='rcv_more_loc_"+userDefinedMultiNewRowExtension+"'>"+locDtls+"</textarea></div>");

		//Notes
		sb.append("<div class='col-md-1 col-sm-1 col-xs-3' style='margin-right:13%'><label class='control-label'>ملاحظات</label>"
				+ "<textarea  class='form-control'  name='rcv_rmk_"+userDefinedMultiNewRowExtension+"' "
						+ "id ='rcv_rmk_"+userDefinedMultiNewRowExtension+"'>"+rmk+"</textarea></div></div>");
		
		String checked = "";
		//Fragile
		if (fragile.equalsIgnoreCase("Y"))
			checked="checked";
		sb.append("<div class='form-group'><div class='col-md-1 col-sm-1 col-xs-3' ><label class='control-label'>قابل للكسر</label>"
						+ "</br>"
						+ "<input type='checkbox' "+checked+" class='form-check-input' value='Y' name='c_fragile_"+userDefinedMultiNewRowExtension+"' id ='c_fragile_"+userDefinedMultiNewRowExtension+"' /></div>");
		//rural areas
		checked = "";
		if (rural.equalsIgnoreCase("Y"))
			checked="checked";
		
		sb.append("<div class='col-md-1 col-sm-1 col-xs-3' "+stylediv+"><label class='control-label'>أطراف</label>"
				+ "</br>"
				+ "<input type='checkbox' class='form-check-input' name='c_rural_"+userDefinedMultiNewRowExtension+"' "
						+ "id ='c_rural_"+userDefinedMultiNewRowExtension+"' "+checked+" value='Y' /></div>");
		
		sb.append("<div class='col-md-1 col-sm-2 col-xs-3' "+stylediv+"><label class='control-label'>المستلم</label>"
				+ ""
				+ "<input type='text' class='form-control'  style='text-align:right; color: #424242;' "
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
		
		//list of agents per state
		LinkedHashMap<String,String> agents = new LinkedHashMap<String,String> ();
		try {conn1.close();}catch(Exception e) {}
		try{
			conn1 = mysql.getConn();
			agents  = ut.getListOfAgentsPerState(conn1,destState);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {conn1.close();}catch(Exception e) {}
		}
		sb.append("<div class='col-md-2 col-sm-1 col-xs-3' style=''><label class='control-label'>مندوب التوصيل</label>"
				+ "<select class='form-control select2_single'   id='c_assignedagent_"+userDefinedMultiNewRowExtension+"'" + 
				"  name='c_assignedagent_"+userDefinedMultiNewRowExtension+"' style='"+style+"'  >");
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
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean custFound = false;
		int custid = 0;
		String userid = replaceVarsinString(" {useridlogin} ", arrayGlobals).trim();
		String msg = "تم خلق الطلبيه بنجاح";
		//Utilities ut = new Utilities();
		
		try{
			inputMap_ori = filterRequest(rqs);
			System.out.println(inputMap_ori);
			MasterCaseInformation caseMaster = new MasterCaseInformation();
			LinkedList <Integer> availableCases= new LinkedList <Integer>();
			for (String key:inputMap_ori.keySet()){//loop to get all the cases from the grid
			    if(key.startsWith("smarty_new_row_seq__")) {
			    	availableCases.add(Integer.parseInt(inputMap_ori.get(key)[0]));
			    	
				}
			}
			caseMaster.setState("BAS");
			if (inputMap_ori.get("c_cust_name")[0]==null)
				caseMaster.setCustName("");
			else
				caseMaster.setCustName(inputMap_ori.get("c_cust_name")[0]);
			
			
			caseMaster.setSenderCompanyId(Integer.parseInt(inputMap_ori.get("sendercompany")[0]));
			
			caseMaster.setHp(inputMap_ori.get("c_cust_hp")[0]);
			conn = mysql.getConn();
			// get cust info
			pst = conn.prepareStatement("select 1 from kbcustomers where c_id=?");
			pst.setString(1, inputMap_ori.get("c_cust_name")[0]);
			rs = pst.executeQuery();
			if (rs.next())
				if(rs.getString(1).equalsIgnoreCase("1")) {
					custFound = true;
					custid = Integer.parseInt(inputMap_ori.get("c_cust_name")[0]);
				}
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
			// when there is error and we reload the page the custoemr id will turn into name so check it also.
			if (!custFound) {
				pst = conn.prepareStatement("select c_id from kbcustomers where c_name=?");
				pst.setString(1, inputMap_ori.get("c_cust_name")[0]);
				rs = pst.executeQuery();
				if (rs.next())
					if( rs.getInt("c_id")>0) {
						custFound = true;
						custid = rs.getInt("c_id");
						//System.out.println("found by the name===>"+inputMap_ori.get("c_cust_name")[0]);
					}
			}
			
			if(!custFound) {//create new customer
				pst = conn.prepareStatement("insert into kbcustomers (c_name, c_phone1, c_createdby, c_belongtostore)"
						+ "values(?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
				pst.setString(1, caseMaster.getCustName());
				pst.setString(2, caseMaster.getHp());
				pst.setString(3, userid);
				pst.setString(4, caseMaster.getState());
				pst.executeUpdate();
				rs = pst.getGeneratedKeys();
				rs.next();
				custid = rs.getInt(1);
			}
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
		
			for (Integer j : availableCases) {
				CaseInformation ci = new CaseInformation();
				ci.setSmarty_new_row_seq(j);
				ci.setCustName(caseMaster.getCustName());
				ci.setName(inputMap_ori.get("rcv_name_"+userDefinedMultiNewRowExtension+"_"+j)[0]); //rcv name
				ci.setHp(inputMap_ori.get("rcv_phone_"+userDefinedMultiNewRowExtension+"_"+j)[0]); // rcv phone
				
				ci.setState(inputMap_ori.get("rcv_city_"+userDefinedMultiNewRowExtension+"_"+j)[0]);// rcv city
				ci.setDistrict(inputMap_ori.get("rcv_district_"+userDefinedMultiNewRowExtension+"_"+j)[0]);
				ci.setLocationDetails(inputMap_ori.get("rcv_more_loc_"+userDefinedMultiNewRowExtension+"_"+j)[0]);//location dtls
				ci.setRmk(inputMap_ori.get("rcv_rmk_"+userDefinedMultiNewRowExtension+"_"+j)[0]); // remarks
				ci.setQty(Integer.parseInt(inputMap_ori.get("rcv_qty_"+userDefinedMultiNewRowExtension+"_"+j)[0])); //no of items
				ci.setReceiptAmt(Double.parseDouble(inputMap_ori.get("c_receiptamt_"+userDefinedMultiNewRowExtension+"_"+j)[0].replace(",","")));
				
				ci.setCustReceiptNoOri(inputMap_ori.get("c_custreceiptnoori_"+userDefinedMultiNewRowExtension+"_"+j)[0]);
				if (inputMap_ori.containsKey("c_rural_"+userDefinedMultiNewRowExtension+"_"+j)&&(inputMap_ori.get("c_rural_"+userDefinedMultiNewRowExtension+"_"+j)[0] !=null))
					ci.setRural("Y"); // fragile
				else
					ci.setRural("N");
				
				if (inputMap_ori.containsKey("c_fragile_"+userDefinedMultiNewRowExtension+"_"+j)&&(inputMap_ori.get("c_fragile_"+userDefinedMultiNewRowExtension+"_"+j)[0] !=null))
					ci.setFragile("Y"); // fragile
				else
					ci.setFragile("N");
				
				ci.setAgentShare(Double.parseDouble(inputMap_ori.get("c_agentshare_"+userDefinedMultiNewRowExtension+"_"+j)[0]));
				ci.setShipmentCharge(Double.parseDouble(inputMap_ori.get("c_shipment_cost_"+userDefinedMultiNewRowExtension+"_"+j)[0]));
				ci.setRemainingAmt(ci.getReceiptAmt()-ci.getShipmentCharge());//we use this to set the balance same as goods cost
				
				if (inputMap_ori.get("c_assignedagent_"+userDefinedMultiNewRowExtension+"_"+j) !=null 
							&& inputMap_ori.get("c_assignedagent_"+userDefinedMultiNewRowExtension+"_"+j)[0]!=null
								&& inputMap_ori.get("c_assignedagent_"+userDefinedMultiNewRowExtension+"_"+j)[0].trim().length()>0) {
						ci.setAssignedDLVAgent(inputMap_ori.get("c_assignedagent_"+userDefinedMultiNewRowExtension+"_"+j)[0]);
				}else {
					ci.setAssignedDLVAgent("0");
				}
				cases.add(ci);
			}
			try {pst.close();}catch(Exception e) {/*ignore*/}
			
			// get the pickup agent id
			String pickUpAgent = "";
			pst =conn.prepareStatement("select c_assigned_pickup_agent from kbcustomers where c_id=?");
			pst.setInt(1, custid);
			rs = pst.executeQuery();
			if (rs.next())
				pickUpAgent = rs.getString("c_assigned_pickup_agent");
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
			
			if (pickUpAgent == null || pickUpAgent.equalsIgnoreCase("")) {
				pst =conn.prepareStatement("select comp_pickupagent  from kbcompanies where comp_id=? and comp_pickupagent is not null");
				pst.setInt(1, caseMaster.getSenderCompanyId());
				rs = pst.executeQuery();
				if (rs.next())
					pickUpAgent = rs.getString("comp_pickupagent");
				try {rs.close();}catch(Exception e) {/*ignore*/}
				try {pst.close();}catch(Exception e) {/*ignore*/}
			}
			
			pst = conn.prepareStatement("insert into p_cases "
					+ " (c_createdby	, c_rcv_name	 	, c_rcv_hp	  		, c_rcv_state, "
					+ "  c_rural		, c_rcv_addr_rmk 	, c_rmk 		 	, c_qty	  		 	, c_receiptamt, "
					+ "  c_shipment_cost, c_goodscostbalance, c_fragile  		, c_branchcode		, c_custreceiptnoori, "
					+ "	 c_agentshare	, c_rcv_district	, c_custid		 	, c_custhp			, c_specialcase	    ,"
					+ "	 c_pickupagent	, c_assignedagent	, c_company_sender	, c_createddt	 )"
			+ " values  (?				, ?			     	, ?		 			, ?,"
			+ "			 ?				, ?				    , ?			     	, ?		 			, ?,"
			+ "          ?				, ?				    , ?              	, ?		 			, ?,"
			+ "			 ?				, ?				 	, ?		 		 	, ?		 			, ?,"
			+ "			 ?				, ?					, ?					, ?	 )",
			Statement.RETURN_GENERATED_KEYS);
			CaseInformation ci = new CaseInformation ();
			
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
					pst.setString(13, caseMaster.getState());
					pst.setString(14, ci.getCustReceiptNoOri());
					pst.setDouble(15, ci.getAgentShare());
					pst.setString(16, ci.getDistrict());
					pst.setInt(17, custid);
					pst.setString(18, caseMaster.getHp());
					pst.setString(19, "Y");
					pst.setString(20, pickUpAgent);
					pst.setString(21, ci.getAssignedDLVAgent());
					pst.setInt(22, caseMaster.getSenderCompanyId());
					pst.setString(23, inputMap_ori.get("c_createddt")[0]);
					
					
					pst.executeUpdate();

					rs = pst.getGeneratedKeys();
					if (rs.next())
						ci.setCaseid(rs.getInt(1));
					else
						throw new Exception ("No case id generate");
					
					fu.createNewCaseInQueue(conn,ci.getCaseid(), caseMaster.getState());
					try {rs.close();}catch(Exception e) {/*ignore*/}
					pst.clearParameters();
				
			}
			conn.commit();
			cases.clear();
		}catch(Exception e) {
			try {conn.rollback();}catch(Exception eRoll) {/**/}
			e.printStackTrace();
			msg = "Error ("+e.getMessage()+")";
			setInsertErrorFlag(true);
		}finally {
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
			try {conn.close();}catch(Exception e) {/*ignore*/}
		}
		
		 return msg;
	}
}
