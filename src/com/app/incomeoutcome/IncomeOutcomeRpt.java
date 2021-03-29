package com.app.incomeoutcome;

import java.util.HashMap;

import com.app.core.CoreMgr;

public class IncomeOutcomeRpt extends CoreMgr{
	public double totCredit = 0;
	public double totDebit = 0;
	public double totNet = 0;
	
	public IncomeOutcomeRpt () {
		MainSql = "select '' as trandate, '' as todate, '' as userid, '' as net , ''  as trantype, '' as tranname, '' as trancode, '' as createdby from dual where 1=0";
		
		userDefinedGroupByCol = "userid";
		userDefinedGridCols.clear();
		
		userDefinedFilterCols.add("trandate");
		userDefinedFilterCols.add("todate");
		userDefinedFilterCols.add("userid");
		
		//userDefinedColsMustFillFilter.add("userid");
		userDefinedColsMustFillFilter.add("trandate");
		userDefinedColsMustFillFilter.add("todate");
		userDefinedNewColsHtmlType.put("trandate", "DATE");
		userDefinedNewColsHtmlType.put("todate", "DATE");
		
		userDefinedGridCols.add("tranname");
		userDefinedGridCols.add("net");
		//userDefinedGridCols.add("createdby");
		
		userDefinedColLabel.put("tranname", "العملية");
		userDefinedColLabel.put("net", "المبلغ");
		userDefinedColLabel.put("trandate", "من تاريخ");
		userDefinedColLabel.put("todate", "الى تاريخ");
		userDefinedColLabel.put("userid", "المستخدم");
		
		userDefinedLookups.put("userid", "select us_loginid, us_name from kbusers");
		canFilter = true;
		
		userModifyTD.put("net", "modifyNetAmt({net},{trantype},{trancode},{trandate}, {userid}, {todate})");
		userDefinedPageFooterFunction = "thisFooter()";
	}
	
	public String thisFooter(String colName) {
		if (colName.equalsIgnoreCase("net"))
			return "<td dir='ltr' align='center'>"+numFormat.format(totNet)+"</td>";
		else 
			return "<td>المبلغ المتبقي</td>";
	}
	public String modifyNetAmt(HashMap<String,String> hashy) {
		String s = "<td>";
		String button="<button type=\"button\" class=\"btn btn-xs btn-info\""
				+ "  onclick=\"popitup ('showTransactionsDtlsPopUp.jsp?trancode="+hashy.get("trancode")+"&accttranuserid="
				+hashy.get("userid")+"&trandate="+hashy.get("trandate")+"&todate="+hashy.get("todate")+"' , '' , 1000 ,600);\">تفاصيل</button>";
		double amt = Double.parseDouble(hashy.get("net"));
		
		if (hashy.get("trantype").equalsIgnoreCase("CR")) {
			totNet += amt ;
		}else if (hashy.get("trantype").equalsIgnoreCase("DB")){
			totNet -= amt;
		}
		if (amt > 0) {
			s +=button+"&nbsp;&nbsp;&nbsp;";
		}
		
		s +=numFormat.format(amt)+"</td>";
		return s;
	}
	
	public void initialize(HashMap smartyStateMap) {
		super.initialize(smartyStateMap);
		// build the search
		boolean foundSearch = false;
		boolean userIdFound = false;
		String userid = "", trandate = "", todate = "";
		for (String parameter : search_paramval.keySet()) {
			for (String value : search_paramval.get(parameter)) {
				if (!parameter.equals("filter") && (value != null)
						&& (!value.equals(""))) {
					if (parameter.equals("trandate")) {
						trandate=value;
						foundSearch = true;
					} else if (parameter.equals("todate")) {
						todate=value;
					} else if (parameter.equals("userid")) {
						userid=value;
						userIdFound = true;
					}
				}
			}
	
		}
		// the next statement is important so we can skip the generating of
		// where clause from search_paramval
		
		if (foundSearch) {
			userDefinedWhere = " and 1=1";
			if(userIdFound) {
				MainSql = "select '"+userid+"' userid,'"+trandate+"' as trandate, '"+todate+"' as todate , ifnull(sum((c_receiptamt - c_agentshare)),0) as net, 'CR' as trantype , 'DLVAGENT' as trancode ,'مستلمات من مندوبين التوصيل' as tranname  "
						+ " from p_agent_payments "
						+ " join p_cases on ap_id = c_agentpmtid join p_queue on (c_id= q_caseid and q_status !='CLS')"
						+ " where  c_agentsharesettled ='FULL' "
						+ " and ( (q_stage='dlv_stg' and q_step='delivered')  or (c_shipmentpaidbycustomer='Y' and q_stage='cncl') ) "
						+ " and ap_createdby='"+userid+"' and ap_paymentdt >= '"+trandate+"' and ap_paymentdt < date_add('"+todate+"',interval 1 day ) group by trantype "
						+ " union "
						+ " SELECT '"+userid+"' userid,'"+trandate+"' as trandate, '"+todate+"' as todate ,  ifnull(sum(ifnull(cppc_amount_paid,0)),0) as net, 'DB' as trantype, 'SENDCOMPPMT' as trancode , 'دفوعات إلى شركات التوصيل' as tranname "
						+ " FROM p_customer_payments_company where cppc_createdby='"+userid+"'  and cppc_paymentdt >= '"+trandate+"' and cppc_paymentdt < date_add('"+todate+"',interval 1 day )  "
						+ " union "
						+ " SELECT '"+userid+"' userid,'"+trandate+"' as trandate, '"+todate+"' as todate ,  ifnull(sum(ifnull(cp_amount_paid,0)),0) as net, 'DB' as trantype,'CUSTPMT' as trancode,  'دفوعات إلى أصحاب المحلات' as tranname  "
						+ " FROM p_customer_payments where cp_createdby ='"+userid+"' and cp_paymentdt >= '"+trandate+"' and cp_paymentdt < date_add('"+todate+"',interval 1 day ) "
						+ " union "
						+ " SELECT '"+userid+"' userid,'"+trandate+"' as trandate, '"+todate+"' as todate ,  ifnull(sum(ifnull(cppa_amount_paid,0)),0) as net, 'DB' as trantype, 'RCVAGENTPMT' as trancode, 'دفوعات إلى مندوب الإستلام' as tranname "
						+ " FROM p_customer_payments_pickupagents where cppa_createdby='"+userid+"' and cppa_paymentdt >= '"+trandate+"'  and cppa_paymentdt < date_add('"+todate+"',interval 1 day ) "
						+ " union "
						+ " SELECT '"+userid+"' userid,'"+trandate+"' as trandate, '"+todate+"' as todate ,  ifnull(sum(ifnull(advpmt_amt,0)),0) as net, 'DB' as trantype,'ADVPMT' as trancode , ' دفوعات إلى أصحاب المحلات مقدمأ' as tranname "
						+ " FROM p_inadvance_cust_pmt where advpmt_createdby='"+userid+"' and advpmt_date >= '"+trandate+"'  and advpmt_date < date_add('"+todate+"',interval 1 day ) "
						+ " union "
						+ " SELECT '"+userid+"' userid,'"+trandate+"' as trandate, '"+todate+"' as todate ,  ifnull(sum(ou_price),0) as net, 'DB' as trantype, 'GENEXP' as trancode , 'مصروفات عامة' as tranname "
						+ " FROM p_outcomes where ou_createdby = '"+userid+"' and ou_date >= '"+trandate+"' and ou_date < date_add('"+todate+"',interval 1 day ) ";
			}else {
				MainSql = "select '"+trandate+"' as trandate, '"+todate+"' as todate , ifnull(sum((c_receiptamt - c_agentshare)),0) as net, 'CR' as trantype , 'DLVAGENT' as trancode ,'مستلمات من مندوبين التوصيل' as tranname, ap_createdby as userid "
						+ " from p_agent_payments "
						+ " join p_cases on ap_id = c_agentpmtid join p_queue on (c_id= q_caseid and q_status !='CLS')"
						+ " where  c_agentsharesettled ='FULL' "
						+ " and ( (q_stage='dlv_stg' and q_step='delivered')  or (c_shipmentpaidbycustomer='Y' and q_stage='cncl') ) "
						+ " and  ap_paymentdt >= '"+trandate+"' and ap_paymentdt < date_add('"+todate+"',interval 1 day ) group by trantype,ap_createdby "
						+ " union "
						+ " SELECT '"+trandate+"' as trandate, '"+todate+"' as todate ,  ifnull(sum(ifnull(cppc_amount_paid,0)),0) as net, 'DB' as trantype, 'SENDCOMPPMT' as trancode , 'دفوعات إلى شركات التوصيل' as tranname, cppc_createdby as userid "
						+ " FROM p_customer_payments_company where  cppc_paymentdt >= '"+trandate+"' and cppc_paymentdt < date_add('"+todate+"',interval 1 day ) group by cppc_createdby "
						+ " union "
						+ " SELECT '"+trandate+"' as trandate, '"+todate+"' as todate ,  ifnull(sum(ifnull(cp_amount_paid,0)),0) as net, 'DB' as trantype,'CUSTPMT' as trancode,  'دفوعات إلى أصحاب المحلات' as tranname , cp_createdby as userid "
						+ " FROM p_customer_payments where cp_paymentdt >= '"+trandate+"' and cp_paymentdt < date_add('"+todate+"',interval 1 day ) group by cp_createdby "
						+ " union "
						+ " SELECT '"+trandate+"' as trandate, '"+todate+"' as todate ,  ifnull(sum(ifnull(cppa_amount_paid,0)),0) as net, 'DB' as trantype, 'RCVAGENTPMT' as trancode, 'دفوعات إلى مندوب الإستلام' as tranname, cppa_createdby as userid "
						+ " FROM p_customer_payments_pickupagents where  cppa_paymentdt >= '"+trandate+"'  and cppa_paymentdt < date_add('"+todate+"',interval 1 day ) group by cppa_createdby  "
						+ " union "
						+ " SELECT '"+trandate+"' as trandate, '"+todate+"' as todate ,  ifnull(sum(ifnull(advpmt_amt,0)),0) as net, 'DB' as trantype,'ADVPMT' as trancode , ' دفوعات إلى أصحاب المحلات مقدمأ' as tranname, advpmt_createdby as userid "
						+ " FROM p_inadvance_cust_pmt where advpmt_date >= '"+trandate+"'  and advpmt_date < date_add('"+todate+"',interval 1 day ) group by advpmt_createdby"
						+ " union "
						+ " SELECT '"+trandate+"' as trandate, '"+todate+"' as todate ,  ifnull(sum(ou_price),0) as net, 'DB' as trantype, 'GENEXP' as trancode , 'مصروفات عامة' as tranname, ou_createdby as userid "
						+ " FROM p_outcomes where  ou_date >= '"+trandate+"' and ou_date < date_add('"+todate+"',interval 1 day ) group by ou_createdby";
			}
		}
	}
}
