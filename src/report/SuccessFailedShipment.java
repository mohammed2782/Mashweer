package report;

import java.util.HashMap;

import com.app.core.CoreMgr;

public class SuccessFailedShipment extends CoreMgr {
	public SuccessFailedShipment() {
		/*
		 * to define main sql statement, key column and main table that do this
		 * operations
		 */
		//select q_id ,(select count(q_id) from p_queue where q_code ='delevired__final_stage')as success, (select count(q_id) from p_queue where q_code ='delv_back_to_shipper__cncl' )as failed from p_queue where q_enterdate >= DATE_FORMAT('2018-08-04', '%Y-%m-%d')and q_enterdate <= DATE_FORMAT('2018-08-05', '%Y-%m-%d') group by DATE_FORMAT(q_enterdate, '%Y-%m-%d') 
		MainSql = "select q_id ,(select count(q_id) from p_queue where q_code ='delevired__final_stage')as " +
				"success, (select count(q_id) from p_queue where q_code ='delv_back_to_shipper__cncl' )as" +
				" failed from p_queue  where 1!=1 " +
				"group by DATE_FORMAT(q_enterdate, '%Y-%m-%d') ";

		mainTable = "p_queue";

		userDefinedGridCols.add("success");
		userDefinedGridCols.add("failed");

		canFilter = true;

		userDefinedFilterCols.add("fromDate");
		userDefinedFilterCols.add("toDate");

		userDefinedNewColsHtmlType.put("fromDate", "DATE");
		userDefinedNewColsHtmlType.put("toDate", "DATE");
		userDefinedColLabel.put("toDate", "  الى تاريخ");
		userDefinedColLabel.put("fromDate", "  من تاريخ");

		userDefinedCaption = " الشحنات الناجحة والراجعة  ";
		userDefinedColLabel.put("success", "عدد الشحنات الناحجة ");
		userDefinedColLabel.put("failed", "عدد الشحنات لراجعة");
		userDefinedColsMustFillFilter.add("toDate");
		userDefinedColsMustFillFilter.add("fromDate");

	}

	public void initialize(HashMap smartyStateMap) {
		super.initialize(smartyStateMap);
		// build the search

		String fromExpDt = "", toExpDt = "" ,from="",to="";
		boolean foundSearch = false;
		for (String parameter : search_paramval.keySet()) {
			for (String value : search_paramval.get(parameter)) {
				if (!parameter.equals("filter") && (value != null)
						&& (!value.equals(""))) {
					if (parameter.equals("fromDate")) {
						fromExpDt = " q_enterdate >= DATE_FORMAT('" + value
								+ "', '%Y-%m-%d')";
						from=value;
						foundSearch = true;
					} else if (parameter.equals("toDate")) {
						toExpDt = " q_enterdate <= DATE_FORMAT('" + value
								+ "', '%Y-%m-%d')";
						to=value;
					}
				}
			}

		}
		//select q_id ,(select count(q_id) from p_queue where q_code ='delevired__final_stage')as success, (select count(q_id) from p_queue where q_code ='delv_back_to_shipper__cncl' )as failed from p_queue where q_enterdate >= DATE_FORMAT('2018-08-04', '%Y-%m-%d')and q_enterdate <= DATE_FORMAT('2018-08-05', '%Y-%m-%d') group by DATE_FORMAT(q_enterdate, '%Y-%m-%d') 
		// the next statement is important so we can skip the generating of
		// where clause from search_paramval
		userDefinedWhere = " and 1=0";
		if (foundSearch) {
			MainSql = "select q_id ,(select count(q_id) from p_queue where q_code ='delevired__final_stage')as success, " +
					"(select count(q_id) from p_queue where q_code ='delv_back_to_shipper__cncl' )as failed " +
					"from p_queue where"
					+ fromExpDt + "and" + toExpDt +"group by DATE_FORMAT(q_enterdate, '%Y-%m-%d')";
			userDefinedWhere = " and 1=1";

		}
		System.out.println(MainSql);
		System.out.println(fromExpDt);
		System.out.println(toExpDt);

	}
 

}
