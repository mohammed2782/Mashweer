package com.app.bussframework;

import java.util.HashMap;
public class SingleQueue_cncl_return_withrcvagent extends SingleQueue{
	public SingleQueue_cncl_return_withrcvagent () {
		String rcvAgentButton = " concat("
				+ " ifnull(us_name,'لا يوجد مندوب') , "
				+ " '<a href=\"../TLKPrintRtnWithRcvAgentSRVL?c_pickupagent=',"
				+ " c_pickupagent,'\" style=\"padding-right:20px;\" >"
				+ " <input type=\"button\" value=\" طباعة مانفيست الراجع \"   class=\"btn btn-default btn-sm\" ></a>') as rtnrcvagent ";
		
		MainSql  = "select c_company_sender, date(q_enterdate) as q_enterdate,  c_pickupagent, c_rtnreason, c_receiptamt, date(c_createddt) as c_createddt,c_branchcode, c_assignedagent, c_custreceiptnoori, "
				+ "c_rcv_name , c_qty,q_branch,'' as attempts, p_cases.c_id, c_name,  "
				+ " concat(st_name_ar,' - ', ifnull(cdi_name,''),' ' ,ifnull(c_rcv_addr_rmk,'')) as address ,c_rcv_state,ifnull(c_rcv_district,'NA') as c_rcv_district,"
				+ "  q_id, q_caseid, (q_enterdate+INTERVAL 9 HOUR) as qdate  , q_stage, q_step , stp_id , q_action,"
				+ " q_assigned_to , c_rmk ,"+rcvAgentButton
				+ " from p_queue "
				+ " join p_cases on (c_id = q_caseid)"
				+ " join kbstep on (q_stage=stp_stgcode and q_step=stp_code) "
				+ " join kbstate on (c_rcv_state = st_code)  "
				+ " join kbcustomers on (c_custid = kbcustomers.c_id)"
				+ " left join kbcity_district on (cdi_stcode =st_code and cdi_code = c_rcv_district) "
				+ " left join kbusers on us_id = c_pickupagent "
				+ " where q_stage= '{stg_code}' and q_step='{stp_code}' and q_status !='CLS'"
				+ " and (c_branchcode='{userstorecode}' or '{superRank}'='Y')  ";
		
		userDefinedGroupByCol = "rtnrcvagent";
		userDefinedGridCols.clear();
		userDefinedGridCols.add("c_pickupagent");
		userDefinedGridCols.add("qdate");
		
		userDefinedGridCols.add("c_company_sender");
		userDefinedGridCols.add("c_name");
		userDefinedGridCols.add("c_custreceiptnoori");
		userDefinedGridCols.add("c_receiptamt");
		userDefinedGridCols.add("address");
		userDefinedGridCols.add("q_caseid");
		userDefinedGridCols.add("c_rcv_name");
		
		userDefinedGridCols.add("c_qty");
		userDefinedGridCols.add("c_createddt");
		userDefinedGridCols.add("q_action");
		
		userDefinedFilterCols.remove("c_createddt");
		userDefinedFilterCols.add("q_enterdate");
		userDefinedColLabel.put("qdate", "تاريخ و وقت تسليم مندوب الإستلام");
		userDefinedLookups.put("c_company_sender", "select comp_id , comp_name from kbcompanies");
		userDefinedFilterColsHtmlType.put("q_enterdate", "DATE");
		
		userDefinedFilterCols.add("c_pickupagent");
		userDefinedLookups.put("c_pickupagent", "select us_id , us_name from kbusers where us_rank = 'PICKUPAGENT'");
		userDefinedFilterColsHtmlType.put("c_pickupagent", "DROPLIST");
	}
	public void initialize(HashMap smartyStateMap) {
		userDefinedCaption= "<div class='col-md-9 col-sm-12 col-xs-12'>"+this.userDefinedCaption+"</div>"
				+ " <div class=\"checkbox checkbox-success\">" + 
				"                        <input id=\"allreturnedtooricust\" class=\"\" onclick=\"changeActionReturnedAllFromRcvAgent();\" type=\"checkbox\">" + 
				"                        <label for=\"allreturned\">" + 
				"                            تم إرجاع الكل إلى أصحاب المحلات" + 
				"                        </label>" + 
				"                    </div>";
		super.initialize(smartyStateMap);
		String qdate = "ALL";
		String todt = "ALL";
		String searchQuery = "";
		boolean first= true;
		boolean foundSearch = false;
		boolean dateSearchFound = false;
		for (String parameter : search_paramval.keySet()) {
			for (String value : search_paramval.get(parameter)) {
				if (!parameter.equals("filter") && (value != null) && (!value.equals(""))) {
					if (parameter.equals("q_enterdate")) {
						qdate =  value;
						dateSearchFound = true;
					}
					foundSearch = true;
					if(!first)
						searchQuery +="&";
					searchQuery +=parameter+"="+value;
					first = false;
				}
			}
		}
		if (foundSearch) {
			
			String rcvAgentButton = " concat("
					+ " ifnull(us_name,'لا يوجد مندوب') , "
					+ " '<a href=\"../TLKPrintRtnWithRcvAgentSRVL?"+searchQuery+"&c_pickupagent=',"
					+ " c_pickupagent,'\" style=\"padding-right:20px;\" >"
					+ " <input type=\"button\" value=\" طباعة مانفيست الراجع \"   class=\"btn btn-default btn-sm\" ></a>') as rtnrcvagent ";
			
			MainSql  = "select c_company_sender, date(q_enterdate) as q_enterdate,  c_pickupagent, c_rtnreason, c_receiptamt, date(c_createddt) as c_createddt,c_branchcode, c_assignedagent, c_custreceiptnoori, "
					+ "c_rcv_name , c_qty,q_branch,'' as attempts, p_cases.c_id, c_name,  "
					+ " concat(st_name_ar,' - ', ifnull(cdi_name,''),' ' ,ifnull(c_rcv_addr_rmk,'')) as address ,c_rcv_state,ifnull(c_rcv_district,'NA') as c_rcv_district,"
					+ "  q_id, q_caseid, (q_enterdate+INTERVAL 9 HOUR) as qdate  , q_stage, q_step , stp_id , q_action,"
					+ " q_assigned_to , c_rmk , "+rcvAgentButton
					+ " from p_queue "
					+ " join p_cases on (c_id = q_caseid)"
					+ " join kbstep on (q_stage=stp_stgcode and q_step=stp_code) "
					+ " join kbstate on (c_rcv_state = st_code)  "
					+ " join kbcustomers on (c_custid = kbcustomers.c_id)"
					+ " left join kbcity_district on (cdi_stcode =st_code and cdi_code = c_rcv_district) "
					+ " left join kbusers on us_id = c_pickupagent "
					+ " where q_stage= 'cncl' and q_step='RTN_WITHRCV_AGENT' and q_status !='CLS'";
			if (dateSearchFound)
				MainSql += " and (date((q_enterdate+INTERVAL 9 HOUR))='"+qdate+"')";	
		}
		
	}//end of method initialize
	
	@Override 
	public StringBuilder getMultiEditGrid() {
		search_paramval.remove("q_enterdate");
		return super.getMultiEditGrid();
	}
}
