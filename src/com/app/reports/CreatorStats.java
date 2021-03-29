package com.app.reports;

import java.util.HashMap;

import com.app.core.CoreMgr;

public class CreatorStats extends CoreMgr {
	public CreatorStats() {
		//
		MainSql = "SELECT c_createdby, date(c_createddt) as crtd, count(*)as tot, '' as fromdate,'' as todate  from p_cases where 1=0 group by c_createdby, date(c_createddt) " ;
		canFilter = true;
		userDefinedFilterCols.add("fromdate");
		userDefinedFilterCols.add("todate");
		userDefinedGroupColsOrderBy = "crtd";
		
		
		userDefinedNewColsHtmlType.put("fromdate", "DATE");
		userDefinedNewColsHtmlType.put("todate", "DATE");
		userDefinedColLabel.put("todate","  الى تاريخ");
		userDefinedColLabel.put("fromdate", "  من تاريخ");
		userDefinedColLabel.put("tot","العدد");
		userDefinedColLabel.put("c_createdby","أدخل بواسطة ");
		userDefinedColLabel.put("crtd","تاريخ");
	
		userDefinedColsMustFillFilter.add("todate");
		userDefinedColsMustFillFilter.add("fromdate");
		
		userDefinedGridCols.add("crtd");
		userDefinedGridCols.add("c_createdby");
		userDefinedGridCols.add("tot");
		
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
			MainSql = "	SELECT c_createdby, date(c_createddt) as crtd, count(*)as tot, '' as fromdate,'' as todate  from p_cases"
					+ " where c_createddt>='"+from+"' and  c_createddt<=adddate(date('"+todate+"'),1) group by c_createdby, date(c_createddt) ";
					
		}
		System.out.println(MainSql);
		
	}
}
