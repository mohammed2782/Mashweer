package com.app.reports;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import com.app.core.CoreMgr;

public class NoOfShipment extends CoreMgr{
	public NoOfShipment() {
		MainSql = "select '' as fromdate, '' as todate, c_custid , c_company_sender,  " + 
				"sum(case when (q_stage = 'cncl') then 1 else 0 end)as totalrtn, " + 
				"sum(case when (q_stage = 'dlv_stg' and q_step = 'delivered') then 1 else 0 end)as totaldlv, " + 
				"sum(case when (q_stage = 'dlv_stg' and q_step != 'delivered') then 1 else 0 end)as totalinprc, " + 
				"count(*) as totalshipments " + 
				"from p_cases " + 
				"join p_queue on (q_status !='CLS' and c_id = q_caseid)  where 1=0 ";
		
		userDefinedGroupByCol = "c_company_sender";
		
		
		mainTable = "p_cases";
		
		canFilter = true;
		
		userDefinedGridCols.add("totalinprc");
		userDefinedGridCols.add("totaldlv");
		userDefinedGridCols.add("totalrtn");
		userDefinedGridCols.add("totalshipments");
		
		userDefinedColLabel.put("totalinprc", "قيد المعالجة");
		userDefinedColLabel.put("totaldlv", "شحنات سلمت بنجاح");
		userDefinedColLabel.put("totalrtn", "شحنات راجعة");
		userDefinedColLabel.put("totalshipments", "مجموع الشحنات");
		userDefinedColLabel.put("c_custid", "الزبون");
		userDefinedColLabel.put("fromdate", "من تاريخ");
		userDefinedColLabel.put("todate", "الى تاريخ");
		userDefinedColLabel.put("c_company_sender", "ألشركة");
		
		userDefinedLookups.put("c_custid", "select c_id, c_name from kbcustomers");
		userDefinedLookups.put("c_company_sender", "select comp_id, comp_name from kbcompanies");
		
		
		userDefinedFilterCols.add("c_custid");
		userDefinedFilterCols.add("fromdate");
		userDefinedFilterCols.add("todate");
		userDefinedFilterCols.add("c_company_sender");
		
		userDefinedFilterColsHtmlType.put("todate", "DATE");
		userDefinedFilterColsHtmlType.put("fromdate", "DATE");
		userDefinedFilterColsHtmlType.put("custid", "DROPLIST");
		userDefinedFilterColsHtmlType.put("c_company_sender", "DROPLIST");
		
		userDefinedCaption = "اعداد الشحنات لكل زبون";
		
		UserDefinedPageRows = 1000;
		
	}
	
	@Override
	public void initialize(HashMap smartyStateMap){
		boolean foundSearch = false, companyFound = false;
		super.initialize(smartyStateMap);
		String fromDate = "", toDate = "";
		if (search_paramval !=null ) {

			if (search_paramval.get("todate")!=null && search_paramval.get("fromdate")!=null||search_paramval.get("lim_itemid")!=null) {
				for (String parameter : search_paramval.keySet()) {
					for (String value : search_paramval.get(parameter)) {
						if (!parameter.equals("filter") && (value != null)
								&& (!value.equals(""))) {
							if (parameter.equals("fromdate")) {
								fromDate=value;
								
							} else if (parameter.equals("todate")) {
								toDate=value;
								
							} else if (parameter.equals("c_custid")) {
								foundSearch = true;
								
							}else if (parameter.equals("c_company_sender")) {
								foundSearch = true;
								companyFound = true;
								
							}
						}
					}
				}
			}
		}
		if(toDate.isEmpty()) {
			DateTimeFormatter Dateformat =  DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDateTime now = LocalDateTime.now();
			toDate = Dateformat.format(now);			
		}
		if(fromDate.isEmpty()) {
			String startTime = "2018-04-04T17:48:23.558";
			LocalDateTime localDateTime = LocalDateTime.parse(startTime);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			fromDate = localDateTime.format(formatter);	
		}
		
		if (foundSearch) {
			MainSql = "select '' as fromdate, '' as todate, c_custid, c_company_sender,  " + 
					"sum(case when (q_stage = 'cncl') then 1 else 0 end)as totalrtn, " + 
					"sum(case when (q_stage = 'dlv_stg' and q_step = 'delivered') then 1 else 0 end)as totaldlv, " + 
					"sum(case when (q_stage = 'dlv_stg' and q_step != 'delivered') then 1 else 0 end)as totalinprc, " + 
					"count(*) as totalshipments " + 
					"from p_cases " + 
					"join p_queue on (q_status !='CLS' and c_id = q_caseid) "+
					"where  (c_createddt >= '"+fromDate+"' and c_createddt<adddate(date('"+toDate+"'),1)) ";
		}
	}
	@Override
	public StringBuilder genListing(){
		search_paramval.remove("fromdate");
		search_paramval.remove("todate");
		return super.genListing();
	}
	

}