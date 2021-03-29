package com.app.reports;

import java.util.HashMap;

import com.app.core.CoreMgr;

public class ShipmentsDtlsRpt extends CoreMgr{

	public ShipmentsDtlsRpt(){
		/*
		 * to define main sql statement, key column and main table that do this operations
		 */		
		MainSql = "select kbcustomers.c_name, count(distinct q_caseid) as ttlNumOfCases,"
				+ "sum(case when (q_code in ('delevired__final_stage') and (q_status in ('END'))) then 1 else 0 end) as ttlNumOfSuccessCases,"
				+ "sum(case when  q_status in ('ACTV') then 1 else 0 end) as ttlNumOfPendCases,"
				+ "sum(case when (q_code in ('delv_back_to_shipper__cncl') and (q_status in ('END'))) then 1 else 0 end) as ttlNumOfBackCases "
				+ "FROM p_queue inner join p_cases on q_caseid = p_cases.c_id "
				+ "inner join p_casesmaster on c_cmid = cm_id "
				+ "inner join kbcustomers on cm_custid = kbcustomers.c_id "
				+ "where 1=0 group by kbcustomers.c_id ";
		
		mainTable = "p_queue";
		
		/*
		 * to define user grid views caption
		 */
		userDefinedCaption = "تفاصيل الشحنات لكل زبون";
		
		/*
		 * to enable/disable basic operations 
		 */
		canFilter = true;
		
		/*
		 * to define grid view columns that want to show to user
		 */
		userDefinedGridCols.add("c_name");
		userDefinedGridCols.add("ttlNumOfCases");
		userDefinedGridCols.add("ttlNumOfSuccessCases");
		userDefinedGridCols.add("ttlNumOfPendCases");
		userDefinedGridCols.add("ttlNumOfBackCases");

		/*
		 * to define grid view label that want to show to user
		 */
		userDefinedColLabel.put("c_name", "إسم الزبون");
		userDefinedColLabel.put("ttlNumOfCases", "العدد الكلي للشحنات");
		userDefinedColLabel.put("ttlNumOfSuccessCases", "عدد الشحنات الناجحة");
		userDefinedColLabel.put("ttlNumOfPendCases", "عدد الشحنات المعلقة");
		userDefinedColLabel.put("ttlNumOfBackCases", "عدد الشحنات المرجعة");

		/*
		 * to define filter columns for search operation
		 */
		userDefinedFilterCols.add("fromDate");
		userDefinedFilterCols.add("toDate");
		userDefinedFilterCols.add("c_name");

		userDefinedFilterLookups.put("c_name","select c_name ,c_name  From kbcustomers order by c_name ASC");

		userDefinedNewColsHtmlType.put("fromDate", "DATE");
		userDefinedNewColsHtmlType.put("toDate", "DATE");
		
		userDefinedColLabel.put("fromDate", "من تاريخ");
		userDefinedColLabel.put("toDate", "الى تاريخ");
		userDefinedColLabel.put("us_loginid", "إسم الزبون");
		
		userDefinedColsMustFillFilter.add("toDate");
		userDefinedColsMustFillFilter.add("fromDate");
		
	}//end of no-arg constructor ShipmentsDtlsRpt
	
	public void initialize(HashMap smartyStateMap) {
		super.initialize(smartyStateMap);

		String fromExpDt = "", toExpDt = "", from = "", to = "", c_name = "";
		boolean foundSearch = false;
		for (String parameter : search_paramval.keySet()) {
			for (String value : search_paramval.get(parameter)) {
				if (!parameter.equals("filter") && (value != null) && (!value.equals(""))) {

					if (parameter.equals("fromDate")) {
						fromExpDt = " q_enterdate >= DATE_FORMAT('" + value
								+ "', '%Y-%m-%d')";
						from = value;
						foundSearch = true;
					} else if (parameter.equals("toDate")) {
						toExpDt = " q_enterdate <= DATE_FORMAT('" + value
								+ "', '%Y-%m-%d')";
						to = value;
					} 
					if (parameter.equals("c_name")) {
						c_name = value;					
					}					
				}
			}
		}
		userDefinedWhere = " having 1=0 order by c_name ASC"; 
	
		String whereClause = "";
		if (foundSearch) {
			whereClause = fromExpDt+"and "+toExpDt+" ";
			if (c_name.equals("")) {
				;			
			}
			else {
			    whereClause +=" and c_name ='"+c_name+"' ";				
			}
			
			MainSql = "select kbcustomers.c_name, count(distinct q_caseid) as ttlNumOfCases,"
					+ "sum(case when (q_code in ('delevired__final_stage') and (q_status in ('END'))) then 1 else 0 end) as ttlNumOfSuccessCases,"
					+ "sum(case when  q_status in ('ACTV') then 1 else 0 end) as ttlNumOfPendCases,"
					+ "sum(case when (q_code in ('delv_back_to_shipper__cncl') and (q_status in ('END'))) then 1 else 0 end) as ttlNumOfBackCases "
					+ "FROM p_queue inner join p_cases on q_caseid = p_cases.c_id "
					+ "inner join p_casesmaster on c_cmid = cm_id "
					+ "inner join kbcustomers on cm_custid = kbcustomers.c_id "
					+ "where" +whereClause+"group by kbcustomers.c_id";
			userDefinedWhere = " having 1=1 order by c_name ASC"; 			
		}
		
		System.out.println(MainSql);
	}//end of method initialize
}//end of class ShipmentsDtlsRpt
