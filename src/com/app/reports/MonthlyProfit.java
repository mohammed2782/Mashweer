package com.app.reports;
import java.util.HashMap;

import com.app.core.CoreMgr;

public class MonthlyProfit  extends CoreMgr{
	public MonthlyProfit(){
		MainSql = "	select 'profits' as dummygroupy, sum(pr) as totinc, sum(ex) as totexp , (sum(pr)-sum(ex))  as netprofit, trandate , '' as fromdate,'' as todate from (" + 
				" select (case when trantype = 'shipprofit' then  amt else 0 end ) as pr," + 
				" (case when trantype = 'expense' then  amt else 0 end ) as ex," + 
				" trandate from (" + 
				" select 'shipprofit' as trantype, sum(c_shipment_cost) - sum(c_agentshare) as amt , date(c_createddt) trandate from p_cases"
				+ " join p_queue on (q_caseid = c_id )"
				+ " where ( (q_status!='CLS' and q_stage = 'dlv_stg' and q_step ='delivered') " + 
				" or ( q_stage = 'dlv_stg' and q_status!='CLS' and (c_shipmentpaidbycustomer ='Y' or c_shipmentpaidbysender = 'Y')))"
				+ " group by date(c_createddt)" + 
				" union " + 
				" select  'expense' as trantype , sum(ou_price) as amt , date(ou_date) trandate from p_outcomes group by date(ou_date)) lvl1) lvl2"
				+ " where 1=0 " + 
				" group by trandate" ;
		canFilter = true;
		userDefinedFilterCols.add("fromdate");
		userDefinedFilterCols.add("todate");
		userDefinedGroupColsOrderBy = "trandate";
		userDefinedGroupByCol = "dummygroupy";
		
		userDefinedNewColsHtmlType.put("fromdate", "DATE");
		userDefinedNewColsHtmlType.put("todate", "DATE");
		userDefinedColLabel.put("todate","  الى تاريخ");
		userDefinedColLabel.put("fromdate", "  من تاريخ");
		
		
		userDefinedColLabel.put("totexp","المصروفات");
		userDefinedColLabel.put("totinc","الايرادات ");
		userDefinedColLabel.put("netprofit","الربح ");
		userDefinedColLabel.put("trandate","تاريخ");

		userDefinedColsMustFillFilter.add("todate");
		userDefinedColsMustFillFilter.add("fromdate");
		
		userDefinedGridCols.add("trandate");
		userDefinedGridCols.add("totinc");
		userDefinedGridCols.add("totexp");
		userDefinedGridCols.add("netprofit");
		
		userDefinedSumCols.add("netprofit");
		userDefinedSumCols.add("totinc");
		userDefinedSumCols.add("totexp");
		
		UserDefinedPageRows = 1000;
		
		userDefinedCaption =" ";
	}
	
	public void initialize(HashMap smartyStateMap) {
		super.initialize(smartyStateMap);
		// build the search

		String fromExpDt = "", toExpDt = "" ,from="",todate="";
		boolean foundSearch = false;
		for (String parameter : search_paramval.keySet()) {
			for (String value : search_paramval.get(parameter)) {
				if (!parameter.equals("filter") && (value != null)
						&& (!value.equals(""))) {
					if (parameter.equals("fromdate")) {
						fromExpDt = " c_createddt >= date('" + value
								+ "', '%Y-%m-%d')";
						from=value;
						foundSearch = true;
					} else if (parameter.equals("todate")) {
						toExpDt = " c_createddt <= DATE_FORMAT('" + value
								+ "', '%Y-%m-%d')";
						todate=value;
					}
				}
			}

		}
		// the next statement is important so we can skip the generating of
		// where clause from search_paramval
		userDefinedWhere = " having 1=1";
		if (foundSearch) {
			MainSql = "	select  'مدخولات ومنصرفات' as dummygroupy , sum(pr) as totinc, sum(ex) as totexp , (sum(pr)-sum(ex))  as netprofit, trandate , '' as fromdate, '' as todate from (" + 
					" select (case when trantype = 'shipprofit' then  amt else 0 end ) as pr," + 
					" (case when trantype = 'expense' then  amt else 0 end ) as ex," + 
					" trandate from (" + 
					" select 'shipprofit' as trantype, sum(c_shipment_cost) - sum(c_agentshare) as amt , date(c_createddt) trandate from p_cases"
					+ " join p_queue on (q_caseid = c_id )"
					+ " where ( (q_status!='CLS' and q_stage = 'dlv_stg' and q_step ='delivered') " + 
					" or ( q_stage = 'dlv_stg' and q_status!='CLS' and (c_shipmentpaidbycustomer ='Y' or c_shipmentpaidbysender = 'Y')))"
					+ " and  c_createddt>='"+from+"' and  c_createddt<=adddate(date('"+todate+"'),1) group by date(c_createddt) " + 
					" union " + 
					" select  'expense' as trantype , sum(ou_price) as amt , date(ou_date) trandate from p_outcomes"
					+ " where ou_date>='"+from+"' and  ou_date<=adddate(date('"+todate+"'),1)  group by date(ou_date)) lvl1) lvl2" + 
					" group by trandate" ;

		}
		//System.out.println(MainSql);
		//System.out.println(fromExpDt);
		//System.out.println(toExpDt);

	}
}
