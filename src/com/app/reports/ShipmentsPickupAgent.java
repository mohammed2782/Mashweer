package com.app.reports;

import java.util.HashMap;

import com.app.core.CoreMgr;

public class ShipmentsPickupAgent extends CoreMgr{
	public ShipmentsPickupAgent() {
		// TODO Auto-generated constructor stub
		MainSql = "select c_pickupagent, " + 
				"sum(case when (q_stage = 'dlv_stg' and q_step = 'delivered') then 1 else 0 end)as dlv, "+ 
				"sum(case when (q_stage = 'cncl') then 1 else 0 end)as rtn, " +
				"sum(case when (q_stage = 'dlv_stg' and q_step != 'delivered') then 1 else 0 end)as totalinprc, " + 
				"count(*) as totalshipments " + 
				" from p_queue join p_cases on c_id  = q_caseid " +
				" where  q_status !='CLS' and 1=0 "+
				" GROUP BY c_pickupagent ";

		mainTable = "p_queue";
		// keyCol = "q_id";

		canFilter = true;

		userDefinedFilterCols.add("fromDate");
		userDefinedFilterCols.add("toDate");
		userDefinedFilterCols.add("c_pickupagent");

		userDefinedLookups.put("c_pickupagent",
						"select us_id ,us_name  From kbusers where us_rank='PICKUPAGENT'");
		
		

		userDefinedNewColsHtmlType.put("fromDate", "DATE");
		userDefinedNewColsHtmlType.put("toDate", "DATE");
		userDefinedColLabel.put("toDate", "  الى تاريخ");
		userDefinedColLabel.put("fromDate", "  من تاريخ");

		userDefinedColLabel.put("c_pickupagent", "اسم مندوب الاستلام");
		userDefinedColLabel.put("rtn", "عدد الراجع ");
		userDefinedColLabel.put("dlv", "عدد التسليمات الناجحة ");
		userDefinedColLabel.put("totalinprc", "قيد التسليم");
		userDefinedColLabel.put("totalshipments", "مجموع الشحنات");

		userDefinedColsMustFillFilter.add("toDate");
		
		userDefinedColsMustFillFilter.add("fromDate");
		//userDefinedColsMustFillFilter.add("us_loginid");

		userDefinedGridCols.add("c_pickupagent");
		userDefinedGridCols.add("rtn");
		userDefinedGridCols.add("dlv");
		userDefinedGridCols.add("totalinprc");
		userDefinedGridCols.add("totalshipments");

		userDefinedCaption = "تسلميات و توصيلات مندوبي الاستلام";

	}// end of con shipments_pickup

	public void initialize(HashMap smartyStateMap) {
		super.initialize(smartyStateMap);
		// build the search

		String fromExpDt = "", toExpDt = "", from = "", to = "", us_loginid = "";
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
					if (parameter.equals("c_pickupagent")) {
						// if(parameter.equals("us_loginid")){}
						us_loginid = value;
						
					}
					
				}
			}
		}
		// the next statement is important so we can skip the generating of
				// where clause from search_paramval
		userDefinedWhere = " having 1=0 "; // 
	
		String whereClause = "";
		if (foundSearch) {
			whereClause = " and "+fromExpDt+"and "+toExpDt+" "; // when you build the query yourself then empty this.
			if (us_loginid.equals("")) {
				;
				
			}
			else {
				whereClause +=" and c_pickupagent ='"+us_loginid+"' ";
			}
			// then use the new where clause inside the query 
			
			
			
			
			MainSql = "select c_pickupagent, " + 
					"sum(case when (q_stage = 'dlv_stg' and q_step = 'delivered') then 1 else 0 end)as dlv, "+ 
					"sum(case when (q_stage = 'cncl') then 1 else 0 end)as rtn, " +
					"sum(case when (q_stage = 'dlv_stg' and q_step != 'delivered') then 1 else 0 end)as totalinprc, " + 
					"count(*) as totalshipments " + 
					" from p_queue join p_cases on c_id  = q_caseid " +
					" where  q_status !='CLS' "+whereClause+
					" GROUP BY c_pickupagent ";

			
		
			userDefinedWhere = " having 1=1 "; // 
		}
		
	}

}
