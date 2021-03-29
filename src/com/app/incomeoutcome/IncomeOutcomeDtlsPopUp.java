package com.app.incomeoutcome;

import java.util.HashMap;

import com.app.core.CoreMgr;

public class IncomeOutcomeDtlsPopUp extends CoreMgr{
	public IncomeOutcomeDtlsPopUp () {
		MainSql = "select '' as net, '' as otherparty, '' as dummy from dual";
		userDefinedGroupByCol = "dummy";
		userDefinedGridCols.clear();
		userDefinedSumCols.add("net");
		groupSumCaption = "المجموع";
		UserDefinedPageRows = 100;
		
		userDefinedGridCols.add("net");
		userDefinedGridCols.add("otherparty");
		userDefinedColLabel.put("net", "المبلغ");
		userDefinedColLabel.put("otherparty", " ");
		
	}
	
	public void initialize(HashMap smartyStateMap) {
		super.initialize(smartyStateMap);
		// build the search
		String trancode = replaceVarsinString("{trancode}", arrayGlobals);
		String trandate = replaceVarsinString("{trandate}", arrayGlobals);
		String todate = replaceVarsinString("{todate}", arrayGlobals);
		String accttranuserid = replaceVarsinString("{accttranuserid}", arrayGlobals);
		boolean userIdFound = true;
		if(accttranuserid.isEmpty()) {
			userIdFound = false;
		}
		if (trancode.equalsIgnoreCase("DLVAGENT")) {
			if(!userIdFound) {
				MainSql = "select  sum((c_receiptamt - c_agentshare)) as net, us_name as otherparty, '' as dummy  "
						+ " from p_agent_payments "
						+ " join p_cases on ap_id = c_agentpmtid join p_queue on (c_id= q_caseid and q_status !='CLS')"
						+ " left join kbusers on us_id = c_assignedagent "
						+ " where  c_agentsharesettled ='FULL' "
						+ " and ( (q_stage='dlv_stg' and q_step='delivered')  or (c_shipmentpaidbycustomer='Y' and q_stage='cncl') ) "
						+ " and  ap_paymentdt >= '"+trandate+"' and ap_paymentdt < date_add('"+todate+"',interval 1 day ) "
						+ "group by us_name  ";
			}else {
				MainSql = "select  sum((c_receiptamt - c_agentshare)) as net, us_name as otherparty, '' as dummy  "
						+ " from p_agent_payments "
						+ " join p_cases on ap_id = c_agentpmtid join p_queue on (c_id= q_caseid and q_status !='CLS')"
						+ " left join kbusers on us_id = c_assignedagent "
						+ " where  c_agentsharesettled ='FULL' "
						+ " and ( (q_stage='dlv_stg' and q_step='delivered')  or (c_shipmentpaidbycustomer='Y' and q_stage='cncl') ) "
						+ " and ap_createdby='"+accttranuserid+"' and ap_paymentdt >= '"+trandate+"' and ap_paymentdt < date_add('"+todate+"',interval 1 day ) "
						+ "group by us_name  ";
			}
		}else if (trancode.equalsIgnoreCase("SENDCOMPPMT")) {
			if(!userIdFound) {
				MainSql = " SELECT ifnull(cppc_amount_paid,0) as net, comp_name as otherparty, '' as dummy "
						+ " FROM p_customer_payments_company"
						+ " join kbcompanies on comp_id = cppc_companyid "
						+ " where cppc_paymentdt>='"+trandate+"' and cppc_paymentdt < date_add('"+todate+"',interval 1 day )  ";
			}else {
				MainSql = " SELECT ifnull(cppc_amount_paid,0) as net, comp_name as otherparty, '' as dummy "
						+ " FROM p_customer_payments_company"
						+ " join kbcompanies on comp_id = cppc_companyid "
						+ " where cppc_createdby='"+accttranuserid+"'  and cppc_paymentdt>='"+trandate+"' and cppc_paymentdt < date_add('"+todate+"',interval 1 day )  ";
			}
		
		}else if (trancode.equalsIgnoreCase("CUSTPMT")) {
			if(!userIdFound) {
				MainSql = " SELECT ifnull(cp_amount_paid,0) as net, c_name as otherparty, '' as dummy "
						+ " FROM p_customer_payments "
						+ " left join kbcustomers on c_id = cp_custid "
						+ " where cp_paymentdt >= '"+trandate+"'  and cp_paymentdt < date_add('"+todate+"',interval 1 day ) ";
			}else {
				MainSql = " SELECT ifnull(cp_amount_paid,0) as net, c_name as otherparty, '' as dummy "
						+ " FROM p_customer_payments "
						+ " left join kbcustomers on c_id = cp_custid "
						+ " where cp_createdby ='"+accttranuserid+"' and cp_paymentdt >= '"+trandate+"'  and cp_paymentdt < date_add('"+todate+"',interval 1 day ) ";
			}
		}else if (trancode.equalsIgnoreCase("RCVAGENTPMT")) {
			if(!userIdFound) {
				MainSql =" SELECT ifnull(cppa_amount_paid,0) as net, us_name as otherparty, '' as dummy   "
						+ " FROM p_customer_payments_pickupagents "
						+ " left join kbusers on us_id = cppa_pickupagentid "
						+ " where  cppa_paymentdt >= '"+trandate+"' and cppa_paymentdt < date_add('"+todate+"',interval 1 day ) ";
			}else {
				MainSql =" SELECT ifnull(cppa_amount_paid,0) as net, us_name as otherparty , '' as dummy  "
						+ " FROM p_customer_payments_pickupagents "
						+ " left join kbusers on us_id = cppa_pickupagentid "
						+ " where cppa_createdby='"+accttranuserid+"' and cppa_paymentdt >= '"+trandate+"' and cppa_paymentdt < date_add('"+todate+"',interval 1 day ) ";
			}
		}else if (trancode.equalsIgnoreCase("ADVPMT")) {
			if(!userIdFound) {
			MainSql =" SELECT ifnull(advpmt_amt,0) as net, c_name as otherparty, '' as dummy "
					+ " FROM p_inadvance_cust_pmt "
					+ " left join kbcustomers on c_id = advpmt_custid "
					+ " where advpmt_date >= '"+trandate+"' and advpmt_date < date_add('"+todate+"',interval 1 day ) ";
			}else {
				MainSql =" SELECT ifnull(advpmt_amt,0) as net, c_name as otherparty, '' as dummy "
						+ " FROM p_inadvance_cust_pmt "
						+ " left join kbcustomers on c_id = advpmt_custid "
						+ " where advpmt_createdby='"+accttranuserid+"' and advpmt_date >= '"+trandate+"' and advpmt_date < date_add('"+todate+"',interval 1 day ) ";
			}
		}else if (trancode.equalsIgnoreCase("GENEXP")) {
			if(!userIdFound) {
				MainSql =" SELECT ifnull(ou_price,0) as net ,  co_name as otherparty, '' as dummy "
						+ " FROM p_outcomes "
						+ " left join kbcost_type on co_id = ou_item "
						+ " where ou_date >= '"+trandate+"'  and ou_date < date_add('"+todate+"',interval 1 day )";
			}else {
				MainSql =" SELECT ifnull(ou_price,0) as net ,  co_name as otherparty, '' as dummy "
						+ " FROM p_outcomes "
						+ " left join kbcost_type on co_id = ou_item "
						+ " where ou_createdby = '"+accttranuserid+"' and ou_date >= '"+trandate+"'  and ou_date < date_add('"+todate+"',interval 1 day )";
			}
		}
	}
	
}
