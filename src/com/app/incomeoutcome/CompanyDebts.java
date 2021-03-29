package com.app.incomeoutcome;

import java.util.HashMap;

import com.app.core.CoreMgr;

public class CompanyDebts extends CoreMgr {
	public CompanyDebts() {
		MainSql = "select 'debts' as debt,  comp_name, " + 
				" '' as todate, SUM((case when (q_stage='dlv_stg' and q_step='delivered') then c_receiptamt else 0 end)) as c_receiptamt, " +  
				"SUM((case when (q_stage='dlv_stg' and q_step='delivered')  then c_shipment_cost else 0  end)) as c_shipment_cost, " + 
				"SUM( (case when (q_stage='dlv_stg' and q_step='delivered')  then (c_receiptamt -  c_sendmoney - c_shipment_cost) else 0  end)) as netamt " + 
				" from p_cases  join p_queue on (c_id= q_caseid and q_status !='CLS') " + 
				" join kbcompanies on comp_id = c_company_sender where c_settled !='FULL'  and (q_stage='dlv_stg' and q_step='delivered') " + 
				"  and 1=0" + 
				" group by comp_id ";
		 
		UserDefinedPageRows = 1000;
		canFilter = true;
		userDefinedFilterCols.add("todate");
		userDefinedColsMustFillFilter.add("todate");
		
		userDefinedColLabel.put("todate","الى تاريخ");
		userDefinedColLabel.put("c_receiptamt", "مبلغ الوصولات");
		userDefinedColLabel.put("c_shipment_cost", " مجموع  مبلغ الشحن ");
		userDefinedColLabel.put("netamt", "مجموع المبلغ الصافي للعميل ");
		userDefinedColLabel.put("comp_name", "الشركة");
		
		userDefinedSumCols.add("c_receiptamt");
		userDefinedSumCols.add("c_shipment_cost");
		userDefinedSumCols.add("netamt");
		
		userDefinedGridCols.add("comp_name");
		userDefinedGridCols.add("c_receiptamt");
		userDefinedGridCols.add("c_shipment_cost");
		userDefinedGridCols.add("netamt");
		
		userDefinedGroupByCol = "debt";
		
		userDefinedNewColsHtmlType.put("todate", "DATE");
	}

	public void initialize(HashMap smartyStateMap) {
		super.initialize(smartyStateMap);
		// build the search

		String todate="";
		boolean foundSearch = false;
		for (String parameter : search_paramval.keySet()) {
			for (String value : search_paramval.get(parameter)) {
				if (!parameter.equals("filter") && (value != null)
						&& (!value.equals(""))) {
					if (parameter.equals("todate")) {
						/*toExpDt = " c_createddt <= DATE_FORMAT('" + value
								+ "', '%Y-%m-%d')";
						*/
						todate=value;
						foundSearch = true;
					} 
				}
			}

		}
		// the next statement is important so we can skip the generating of
		// where clause from search_paramval
		userDefinedWhere = " having 1=1";
		if (foundSearch) {
			MainSql = "select 'debts' as debt,  comp_name, " + 
					" '' as todate, SUM((case when (q_stage='dlv_stg' and q_step='delivered') then c_receiptamt else 0 end)) as c_receiptamt, " + 
					"SUM((case when (q_stage='dlv_stg' and q_step='delivered') then c_sendmoney else 0 end) )as c_sendmoney, " + 
					"SUM((case when (q_stage='dlv_stg' and q_step='delivered')  then c_shipment_cost else 0  end)) as c_shipment_cost, " + 
					"SUM( (case when (q_stage='dlv_stg' and q_step='delivered')  then (c_receiptamt -  c_sendmoney - c_shipment_cost) else 0  end)) as netamt " + 
					" from p_cases  join p_queue on (c_id= q_caseid and q_status !='CLS') " + 
					" join kbcompanies on comp_id = c_company_sender where c_settled !='FULL'  and (q_stage='dlv_stg' and q_step='delivered') " + 
					" and q_enterdate<=adddate(date('"+todate+"'),1)" + 
					" group by comp_id ";
			
			
		}
		System.out.println(MainSql);
		//System.out.println(fromExpDt);
		//System.out.println(toExpDt);

	}
}
