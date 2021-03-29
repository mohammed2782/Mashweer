package com.app.reports;

import java.util.HashMap;

import com.app.core.CoreMgr;

public class RcvStatesDtlsRpt extends CoreMgr{
	public RcvStatesDtlsRpt(){
		/*
		 * to define main sql statement, key column and main table that do this operations
		 */
		MainSql = "SELECT `st_name_ar`, COUNT(*) AS `value_occurrence` FROM `p_cases` join `kbstate` on `st_code` =  `c_rcv_state`"
				+ "where 1=0 GROUP BY `c_rcv_state`";
	
		mainTable = "p_cases";

		/*
		 * to define user grid views caption
		 */
		userDefinedCaption = "تفاصيل الشحنات لكل محافظة علي حسب أكتر طلب إستلام";
		
		/*
		 * to enable/disable basic operations 
		 */
		canFilter = true;
		
		/*
		 * to define grid view columns that want to show to user
		 */
		userDefinedGridCols.add("st_name_ar");
		userDefinedGridCols.add("value_occurrence");

		/*
		 * to define grid view label that want to show to user
		 */
		userDefinedColLabel.put("st_name_ar", "إسم المحافظة");
		userDefinedColLabel.put("value_occurrence", "العدد الكلي للطلبيات");

		/*
		 * to define filter columns for search operation
		 */
		userDefinedFilterCols.add("fromDate");
		userDefinedFilterCols.add("toDate");
		userDefinedFilterCols.add("st_name_ar");

		userDefinedFilterLookups.put("st_name_ar","select st_name_ar ,st_name_ar  From kbstate order by st_name_ar ASC");

		userDefinedNewColsHtmlType.put("fromDate", "DATE");
		userDefinedNewColsHtmlType.put("toDate", "DATE");
		
		userDefinedColLabel.put("fromDate", "من تاريخ");
		userDefinedColLabel.put("toDate", "الى تاريخ");
		userDefinedColLabel.put("st_name_ar", "إسم المحافظة");
		
		userDefinedColsMustFillFilter.add("toDate");
		userDefinedColsMustFillFilter.add("fromDate");		
	
	}//end of no-arg constructor RcvStatesDtlsRpt
	
	public void initialize(HashMap smartyStateMap) {
		super.initialize(smartyStateMap);

		String fromExpDt = "", toExpDt = "", from = "", to = "", st_name_ar = "";
		boolean foundSearch = false;
		for (String parameter : search_paramval.keySet()) {
			for (String value : search_paramval.get(parameter)) {
				if (!parameter.equals("filter") && (value != null) && (!value.equals(""))) {

					if (parameter.equals("fromDate")) {
						fromExpDt = " c_createddt >= DATE_FORMAT('" + value
								+ "', '%Y-%m-%d')";
						from = value;
						foundSearch = true;
					} else if (parameter.equals("toDate")) {
						toExpDt = " c_createddt <= DATE_FORMAT('" + value
								+ "', '%Y-%m-%d')";
						to = value;
					} 
					if (parameter.equals("st_name_ar")) {
						st_name_ar = value;					
					}					
				}
			}
		}
		userDefinedWhere = " having 1=0 ORDER BY `value_occurrence` DESC"; 
	
		String whereClause = "";
		if (foundSearch) {
			whereClause = fromExpDt+"and "+toExpDt+" ";
			if (st_name_ar.equals("")) {
				;			
			}
			else {
			    whereClause +=" and st_name_ar ='"+st_name_ar+"' ";				
			}
			
			MainSql =  "SELECT `st_name_ar`, COUNT(*) AS `value_occurrence` FROM `p_cases` join `kbstate` on `st_code` =  `c_rcv_state`"
					 + "where" +whereClause+ "GROUP BY `c_rcv_state`";
			userDefinedWhere = " having 1=1 ORDER BY `value_occurrence` DESC"; 
		}
		
		System.out.println(MainSql);
	}//end of method initialize
}//end of class RcvStatesDtlsRpt
