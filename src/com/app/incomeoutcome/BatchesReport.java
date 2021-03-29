package com.app.incomeoutcome;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import com.app.core.CoreMgr;
import com.app.db.mysql;

public class BatchesReport extends CoreMgr {
	public int inprocess = 0;
	public int rtn = 0;
	public int dlv = 0;
	public int rural = 0;
	public int center = 0;
	public int totCases = 0;
	double netAmtTotal = 0;
	boolean errorFlag = false;
	public BatchesReport () {
		MainSql= "select c_rmk, c_assignedagent, '' as groupby, '' as c_company_sender,c_rural,  c_createddt, c_id, c_shipmentpaidbycustomer, c_shipment_cost, '' net   from p_cases where 1=0";
		
		userDefinedFilterCols.add("c_company_sender");
		userDefinedFilterCols.add("c_createddt");
		userDefinedFilterCols.add("c_assignedagent");
		
		userDefinedLookups.put("c_company_sender", "select comp_id , comp_name from kbcompanies");
		userDefinedLookups.put("c_custid", "select c_id , c_name from kbcustomers");
		canFilter = true;
		
		userDefinedGroupColsOrderBy = " groupby, c_id";
		userDefinedGridCols.add("c_createddt");
		userDefinedGridCols.add("c_custid");
		userDefinedGridCols.add("c_custreceiptnoori");
		userDefinedGridCols.add("c_receiptamt");
		userDefinedGridCols.add("c_shipment_cost");
		userDefinedGridCols.add("net");
		userDefinedGridCols.add("c_rcv_hp");
		userDefinedGridCols.add("status");
		userDefinedGridCols.add("c_assignedagent");
		userDefinedGridCols.add("c_rmk");
		
		UserDefinedPageRows = 5000;
		userDefinedColsMustFillFilter.add("c_createddt");
		userDefinedColsMustFillFilter.add("c_company_sender");
		
		userDefinedColLabel.put("c_createddt", "تاريخ");
		userDefinedColLabel.put("c_company_sender","الشركة المرسلة");
		userDefinedColLabel.put("c_custreceiptnoori", "رقم الوصل");
		userDefinedColLabel.put("c_custid","صاحب المحل");
		userDefinedColLabel.put("c_receiptamt", "مبلغ الوصل");
		userDefinedColLabel.put("c_rcv_hp","هاتف المستلم");
		userDefinedColLabel.put("status", "الحالة");
		userDefinedColLabel.put("net", "الصافي للشركة");
		userDefinedColLabel.put("c_rmk", "ملاحظات");
		userDefinedColLabel.put("c_assignedagent", "مندوب التوصيل");
		userDefinedColLabel.put("c_shipment_cost", "مبلغ الشحن");
		userDefinedGroupByCol = "groupby";
		
		userModifyTD.put("status", "modifyStatus({status}, {c_shipmentpaidbycustomer},{c_rural})");
		userModifyTD.put("net", "modifyNetAmt({status}, {c_shipmentpaidbycustomer}, {c_receiptamt},{c_shipment_cost})");
		userModifyTD.put("c_shipment_cost", "modifyShipmentCost({status}, {c_shipmentpaidbycustomer},{c_shipment_cost})");
		userModifyTD.put("c_receiptamt", "modifyReceiptAmt({status}, {c_shipmentpaidbycustomer}, {c_receiptamt})");
		userDefinedLookups.put("c_assignedagent", "select us_id , us_name from kbusers");
		userDefinedPageFooterFunction = "myFooterFunction()";
	}
	
	public String myFooterFunction(String colName) {
		if (colName.equalsIgnoreCase("c_custid") ) {
			if (!errorFlag) {
				String style=" style='padding:7px;'";
				return "<td colspan='4' stye='font-size: 15px;' align='center'><table><tr><td colspan='4' ><label>ملخص الوجبه</label></td></tr>"
						+ "<tr><td "+style+">عدد الشحنات الكلي</td><td "+style+">"+totCases+"</td><td></td><td></td></tr>"
						+ "<tr><td "+style+">عدد الشحنات المسلمة بنجاح</td><td "+style+">"+dlv+"</td><td "+style+">المبلغ الصافي للشركة</td><td "+style+">"+numFormat.format(netAmtTotal)+"</td></tr>"
						+ "<tr><td "+style+">المسلمة مركز</td><td "+style+">"+numFormat.format(center)+"</td><td "+style+">المسلمة أطراف</td><td "+style+">"+numFormat.format(rural)+"</td></tr>"
						+ "<tr><td "+style+">عدد الشحنات الراجعة</td><td "+style+">"+rtn+"</td><td></td><td></td></tr>"
						+ "<tr><td "+style+">عدد الشحنات قيد التسليم</td><td "+style+">"+inprocess+"</td><td></td><td></td></tr>"
						+ "</table></td>";
			}else {
				return "<td colspan='3' stye='font-size: 15px;' align='center'>هنالك خطأ في النظام </td>";
			}
		}else if(colName.equalsIgnoreCase("c_custreceiptnoori") || colName.equalsIgnoreCase("c_receiptamt") || colName.equalsIgnoreCase("c_shipment_cost"))
			return "";
		else
			return "<td></td>";
	}
	
	public String modifyReceiptAmt (HashMap<String,String> hashy) {
		String s= "<td>";
		double net = 0;
		if (hashy.get("status").equalsIgnoreCase("inprocess")) {
			s +="-";
		}else if (hashy.get("status").equalsIgnoreCase("dlv")) {
			net  = Double.parseDouble(hashy.get("c_receiptamt"));
			s +=numFormat.format(net);
		}else if (hashy.get("status").equalsIgnoreCase("rtn")) {
			if (hashy.get("c_shipmentpaidbycustomer").equalsIgnoreCase("Y")) {
				net = Double.parseDouble(hashy.get("c_receiptamt"));
				s +=numFormat.format(net);
			}else
				s +="-";
		}
		return s+="</td>";
	}
	
	
	public String modifyShipmentCost (HashMap<String,String> hashy) {
		String s= "<td>";
		double net = 0;
		if (hashy.get("status").equalsIgnoreCase("inprocess")) {
			s +="-";
		}else if (hashy.get("status").equalsIgnoreCase("dlv")) {
			net  = Double.parseDouble(hashy.get("c_shipment_cost"));
			s +=numFormat.format(net);
		}else if (hashy.get("status").equalsIgnoreCase("rtn")) {
			if (hashy.get("c_shipmentpaidbycustomer").equalsIgnoreCase("Y")) {
				net = Double.parseDouble(hashy.get("c_shipment_cost"));
				s +=numFormat.format(net);
			}else
				s +="-";
		}
		return s+="</td>";
	}
	
	public String modifyNetAmt (HashMap<String,String> hashy) {
		String s= "<td>";
		double net = 0;
		if (hashy.get("status").equalsIgnoreCase("inprocess")) {
			s +="-";
		}else if (hashy.get("status").equalsIgnoreCase("dlv")) {
			net = Double.parseDouble(hashy.get("c_receiptamt")) - Double.parseDouble(hashy.get("c_shipment_cost"));
			s +=numFormat.format(net);
		}else if (hashy.get("status").equalsIgnoreCase("rtn")) {
			if (hashy.get("c_shipmentpaidbycustomer").equalsIgnoreCase("Y")) {
				net = Double.parseDouble(hashy.get("c_receiptamt")) - Double.parseDouble(hashy.get("c_shipment_cost"));
				s +=numFormat.format(net);
			}else
				s +="-";
		}
		netAmtTotal += net;
		return s+="</td>";
	}
	public String modifyStatus(HashMap<String,String> hashy) {
		
		String s= "<td>";
		totCases ++;
		if (hashy.get("status").equalsIgnoreCase("inprocess")) {
			s +="قيد التسليم";
			inprocess ++;
		}else if (hashy.get("status").equalsIgnoreCase("dlv")) {
			s +="تم التسليم";
			dlv++;
			if (hashy.get("c_rural").equalsIgnoreCase("Y")) {
				rural ++;
			}else {
				center ++;
			}
		}else if (hashy.get("status").equalsIgnoreCase("rtn")) {
			if (hashy.get("c_shipmentpaidbycustomer").equalsIgnoreCase("Y"))
				s +="راجع مع دفع أجور النقل من المستلم وأجور النقل هي مبلغ الوصل";
			else
				s +="راجع";
			rtn ++;
		}
		return s+="</td>";
		
	}
	
	@Override
	public void initialize(HashMap smartyStateMap){
		super.initialize(smartyStateMap);
		String searchQuery = "";
		boolean first= true;
		boolean foundSearch = false;
		boolean dateSearchFound = false;
		
		for (String parameter : search_paramval.keySet()) {
			for (String value : search_paramval.get(parameter)) {
				if (!parameter.equals("filter") && (value != null) && (!value.equals(""))) {
					foundSearch = true;
					if(!first)
						searchQuery +="&";
					searchQuery +=parameter+"="+value;
					first = false;
				}
			}
		}
		if (search_paramval!=null && !search_paramval.isEmpty()) {
			String groupButton = " concat(comp_name, "
					+ " '<a href=\"../PrintBatchStatusManifestSRVL?"+searchQuery+"\" style=\"padding-right:20px;\" >"
					+ " <input type=\"button\" value=\" طباعة كشف الوجبه \"   class=\"btn btn-default btn-sm\" ></a>') as groupby ";
			MainSql =" select c_rmk, c_assignedagent,  "+groupButton+", c_shipmentpaidbycustomer, c_shipment_cost, c_rural , c_createddt, c_custid, c_id, c_custreceiptnoori, c_receiptamt, c_rcv_hp, "
					+ " (case when q_stage='cncl' then 'rtn'"
					+ "   when q_stage='dlv_stg' and q_step = 'delivered'  then 'dlv' "
					+ "  else 'inprocess' end) as status, '' as net from p_cases"
					+ " join p_queue on q_caseid = c_id and q_status !='CLS'"
					+ " left join kbcompanies on c_company_sender = comp_id ";
			
		}
		
		
		
	}
}
