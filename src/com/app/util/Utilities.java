package com.app.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.app.cases.CaseInformation;
import com.app.incomeoutcome.AgentPaymentBean;
import com.app.incomeoutcome.CompanyBatchBean;
import com.app.incomeoutcome.CustomerPaymentBean;
import com.app.incomeoutcome.PickUpAgentPaymentBean;


public class Utilities {

	public CompanyBatchBean  getBatchInfo (Connection conn, HashMap<String,String> filterMap) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		CompanyBatchBean cpb = new CompanyBatchBean();
		 ArrayList<CaseInformation>  casesList= new  ArrayList<CaseInformation>();
		try {
			pst = conn.prepareStatement("select comp_name from kbcompanies  where comp_id=?");
			pst.setString(1, filterMap.get("c_company_sender"));
			rs = pst.executeQuery();
			if (rs.next()) {
				cpb.setCompanyName(rs.getString("comp_name"));
			}
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			String sql =" select c_rural, c_shipmentpaidbycustomer,c_rcv_state, c_shipment_cost , c_createddt, c_custid, c_name , c_custreceiptnoori, "
					+ "c_receiptamt, c_rcv_hp,c_rmk, "
					 + " concat(st_name_ar,' - ',ifnull(cdi_name,''),' ' ,ifnull(c_rcv_addr_rmk,'')) as addr , "
					+ " (case when q_stage='cncl' then 'rtn'"
					+ "   when q_stage='dlv_stg' and q_step = 'delivered'  then 'dlv' "
					+ "  else 'inprocess' end) as status, '' as net "
					+ " from p_cases"
					+ " join p_queue on q_caseid = c_id and q_status !='CLS'"
					+ " join kbcustomers on kbcustomers.c_id = c_custid "
					+ " left join kbcompanies on c_company_sender = comp_id "
					+ " left join kbstate on st_code = c_rcv_state"
					+ " left join kbcity_district on (cdi_code =c_rcv_district and cdi_stcode=c_rcv_state) "
					+ " where 1=1  ";
			
			for (String col : filterMap.keySet()) {
				if (col.equalsIgnoreCase("c_createddt")) {
					sql += " and date(c_createddt)=?";
				}else {
					sql += " and "+col+"=?";
				}
			}
			pst = conn.prepareStatement(sql);
			int i=1;
			for (String col : filterMap.keySet()) {
				pst.setString(i, filterMap.get(col));
				i++;
			}
			rs = pst.executeQuery();
			CaseInformation caseInfo;
			while (rs.next()) {
				caseInfo= new CaseInformation();
				caseInfo.setShipmentChargesPaidByCustomer(rs.getString("c_shipmentpaidbycustomer"));
				caseInfo.setName(rs.getString("c_name"));
				caseInfo.setHp(rs.getString("c_rcv_hp"));
				caseInfo.setState(rs.getString("c_rcv_state"));
				caseInfo.setLocationDetails(rs.getString("addr"));
				caseInfo.setRmk(rs.getString("c_rmk"));
				caseInfo.setReceiptAmt(rs.getInt("c_receiptamt"));
				caseInfo.setShipmentCharge(rs.getInt("c_shipment_cost"));
				caseInfo.setStatus(rs.getString("status"));
				caseInfo.setCreateddt(rs.getString("c_createddt"));
				caseInfo.setCustReceiptNoOri(rs.getString("c_custreceiptnoori"));
				caseInfo.setRural(rs.getString("c_rural"));
				casesList.add(caseInfo);
			}
			cpb.setShipments(casesList);
		}catch(Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return cpb;
	}
	
	
	public AgentPaymentBean getAgentPaymentInfo (Connection conn, int pmtId) throws Exception {
	
		PreparedStatement pst = null;
		ResultSet rs = null;
		AgentPaymentBean cpb = new AgentPaymentBean();
		 ArrayList<CaseInformation>  casesList= new  ArrayList<CaseInformation>();
		try {
			pst = conn.prepareStatement("select us_name, ap_paymentdt, ap_amount_paid, ap_rmk "
					+ " from p_agent_payments join kbusers on ap_agentid= us_id  where ap_id=?");
			pst.setInt(1, pmtId);
			rs = pst.executeQuery();
			if (rs.next()) {
				cpb.setPmtAmt(rs.getDouble("ap_amount_paid"));
				cpb.setPmtDate(rs.getString("ap_paymentdt"));
				cpb.setAgentName(rs.getString("us_name"));
				cpb.setPmtRmk(rs.getString("ap_rmk"));
			}
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			
			pst = conn.prepareStatement("select c_changedprice, c_priceb4change, c_shipmentpaidbycustomer, c_shipmentpaidbysender , c_name, date(c_createddt) as c_createddt, p_cases.c_id, c_rcv_name, c_rcv_hp,c_rcv_state, "
					+ " concat(st_name_ar,' - ',ifnull(cdi_name,''),' ' ,ifnull(c_rcv_addr_rmk,'')) as addr , "
					+ " c_qty, c_rmk, c_weight, c_receiptamt, c_bringitemsback, c_fragile , c_sendmoney,"
					+" (case when (q_stage='dlv_stg' and q_step='delivered')  then 'dlv' else 'canceled'  end) as status,"
					+ " c_custreceiptnoori, "
					+ " (case when ((q_stage='dlv_stg' and q_step='delivered') "
					+ "				or  (q_stage ='cncl' and c_shipmentpaidbycustomer='Y') "
					+ "				or  (q_stage ='cncl' and c_shipmentpaidbysender='Y' )) then c_agentshare else 0 end) as c_agentshare,"
					+ " (case when (q_stage='dlv_stg' and q_step='delivered' ) then c_shipment_cost  "
					+"        when (q_stage='cncl' and (c_shipmentpaidbysender='Y' or c_shipmentpaidbycustomer='Y') ) then c_shipment_cost  "
					+ "		  when (q_stage ='cncl' and c_shipmentpaidbycustomer ='Y' and c_shipmentpaidbysender='N' and c_rural='Y') then st_ruralcharges "
					+ "       when (q_stage ='cncl' and c_shipmentpaidbycustomer ='Y' and c_shipmentpaidbysender='N' and c_rural='N') then st_charges "
					+ "			 else 0 end )as c_shipment_cost "
					+ " from p_cases  "
					+ " join p_queue on (q_caseid = c_id and q_status != 'CLS')"
					+ " left join kbcustomers on kbcustomers.c_id = c_custid "
					+ " left join kbstate on st_code = c_rcv_state"
					+ " left join kbcity_district on (cdi_code =c_rcv_district and cdi_stcode=c_rcv_state) "
					+ " where c_agentpmtid=? and c_agentsharesettled ='FULL' order by c_rcv_state ");
			pst.setInt(1, pmtId);
			rs = pst.executeQuery();
			CaseInformation caseInfo;
			while (rs.next()) {
				caseInfo= new CaseInformation();
				caseInfo.setCaseid(rs.getInt("p_cases.c_id"));
				caseInfo.setShipmentChargesPaidByCustomer(rs.getString("c_shipmentpaidbycustomer"));
				caseInfo.setShipmentChargesPaidBysender(rs.getString("c_shipmentpaidbysender"));
				caseInfo.setName(rs.getString("c_name"));
				caseInfo.setHp(rs.getString("c_rcv_hp"));
				caseInfo.setState(rs.getString("c_rcv_state"));
				caseInfo.setLocationDetails(rs.getString("addr"));
				caseInfo.setQty(rs.getInt("c_qty"));
				caseInfo.setRmk(rs.getString("c_rmk"));
				
				caseInfo.setReceiptAmt(rs.getInt("c_receiptamt"));
				caseInfo.setShipmentCharge(rs.getInt("c_shipment_cost"));
				caseInfo.setFragile(rs.getString("c_fragile"));
				
				caseInfo.setReceiptAmtB4Change(rs.getInt("c_priceb4change"));
				caseInfo.setChangedPrice(rs.getString("c_changedprice"));
				
				caseInfo.setStatus(rs.getString("status"));
				caseInfo.setCreateddt(rs.getString("c_createddt"));
				caseInfo.setCustReceiptNoOri(rs.getString("c_custreceiptnoori"));
				caseInfo.setAgentShare(rs.getDouble("c_agentshare"));
				casesList.add(caseInfo);
			}
			cpb.setShipments(casesList);
		}catch(Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return cpb;
		
	}
	
	
	public CustomerPaymentBean getAdvancedPaymentOfCustomer (Connection conn, int advpmt_id) throws Exception {
	
		PreparedStatement pst = null;
		ResultSet rs = null;
		CustomerPaymentBean cpb = new CustomerPaymentBean();
		 ArrayList<CaseInformation>  casesList= new  ArrayList<CaseInformation>();
		try {
			pst = conn.prepareStatement("select c_name, advpmt_date, advpmt_amt, advpmt_rmk "
					+ " from p_inadvance_cust_pmt join kbcustomers on advpmt_custid= c_id  where advpmt_id=?");
			pst.setInt(1, advpmt_id);
			rs = pst.executeQuery();
			if (rs.next()) {
				cpb.setPmtAmt(rs.getDouble("advpmt_amt"));
				cpb.setPmtDate(rs.getString("advpmt_date"));
				cpb.setCustomerName(rs.getString("c_name"));
				cpb.setPmtRmk(rs.getString("advpmt_rmk"));
			}
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			
			pst = conn.prepareStatement("select  date(c_createddt) as c_createddt, c_id, c_rcv_name, c_rcv_hp,c_rcv_state, "
					+ " concat(st_name_ar,' - ',ifnull(cdi_name,''),' ' ,ifnull(c_rcv_addr_rmk,'')) as addr , "
					+ " c_qty, c_rmk, c_weight, c_shipment_cost, c_receiptamt,   c_sendmoney,"
					+ " c_custreceiptnoori"
					+ " from p_cases  "
					+ " left join kbstate on st_code = c_rcv_state"
					+ " left join kbcity_district on (cdi_code =c_rcv_district and cdi_stcode=c_rcv_state) "
					+ " where c_advancepmtid=? order by c_createddt, c_custreceiptnoori ");
			pst.setInt(1, advpmt_id);
			rs = pst.executeQuery();
			CaseInformation caseInfo;
			while (rs.next()) {
				caseInfo= new CaseInformation();
				caseInfo.setCaseid(rs.getInt("c_id"));
				caseInfo.setName(rs.getString("c_rcv_name"));
				caseInfo.setHp(rs.getString("c_rcv_hp"));
				caseInfo.setState(rs.getString("c_rcv_state"));
				caseInfo.setLocationDetails(rs.getString("addr"));
				caseInfo.setQty(rs.getInt("c_qty"));
				caseInfo.setRmk(rs.getString("c_rmk"));
				caseInfo.setReceiptAmt(rs.getInt("c_receiptamt"));
				caseInfo.setShipmentCharge(rs.getInt("c_shipment_cost"));
				caseInfo.setCreateddt(rs.getString("c_createddt"));
				caseInfo.setCustReceiptNoOri(rs.getString("c_custreceiptnoori"));
				
				casesList.add(caseInfo);
			}
			cpb.setShipments(casesList);
		}catch(Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return cpb;
		
	}
	
	
public CustomerPaymentBean getCustomerPaymentInfo (Connection conn, int pmtId) throws Exception {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		CustomerPaymentBean cpb = new CustomerPaymentBean();
		 ArrayList<CaseInformation>  casesList= new  ArrayList<CaseInformation>();
		try {
			pst = conn.prepareStatement("select c_name, cp_paymentdt, cp_amount_paid, cp_rmk "
					+ " from p_customer_payments join kbcustomers on cp_custid= c_id  where cp_id=?");
			pst.setInt(1, pmtId);
			rs = pst.executeQuery();
			if (rs.next()) {
				cpb.setPmtAmt(rs.getDouble("cp_amount_paid"));
				cpb.setPmtDate(rs.getString("cp_paymentdt"));
				cpb.setCustomerName(rs.getString("c_name"));
				cpb.setPmtRmk(rs.getString("cp_rmk"));
			}
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			
			pst = conn.prepareStatement("select c_shipmentprofit, c_partnershare, c_rural, c_priceb4change, c_changedprice,  c_paidinadvance, c_advancepmtid, c_shipmentpaidbysender, date(c_createddt) as c_createddt, "
					+ " c_id, c_rcv_name, c_rcv_hp,c_rcv_state, "
					+ " concat(st_name_ar,' - ',ifnull(cdi_name,''),' ' ,ifnull(c_rcv_addr_rmk,'')) as addr , "
					+ " c_qty, c_rmk, c_weight, "
					+ " (case when (c_paidinadvance='NO' and c_advancepmtid=0 and c_settled ='FULL') then c_receiptamt"
					+ "       when  ( c_paidinadvance='REFUNDED' and c_advancepmtid>0 and c_settled ='FULL' and c_changedprice='N') then c_receiptamt*-1 "
					+ "       when  ( c_paidinadvance='REFUNDED' and c_advancepmtid>0 and c_settled ='FULL' and c_changedprice='Y') then (c_receiptamt) "
					+ "				else 0 end) as c_receiptamt,"
					+ "  c_shipment_cost, "
					+ " (	case "
					+ "			when (c_paidinadvance='NO' and c_advancepmtid=0 and c_settled ='FULL')  then (c_receiptamt - c_shipment_cost) "
					+ "			when (c_paidinadvance='REFUNDED' and c_advancepmtid>0 and c_settled ='FULL' and c_changedprice='Y') then (c_receiptamt - c_priceb4change) "
					+ "			when (c_shipmentpaidbysender='N' and  c_paidinadvance='REFUNDED' and c_advancepmtid>0 and c_settled ='FULL') then (c_shipment_cost - c_receiptamt) "
					+ "			when (c_shipmentpaidbysender='Y' and  c_paidinadvance='REFUNDED' and c_advancepmtid>0 and c_settled ='FULL') then (c_receiptamt*-1) "
					+ " 		else (1*-c_shipment_cost)  end) as netamt, "
					+ "c_bringitemsback, c_fragile , c_sendmoney,"
					+" (case when (q_stage='dlv_stg' and q_step='delivered')  then 'dlv' else 'canceled'  end) as status,"
					+ " c_custreceiptnoori"
					+ " from p_cases  "
					+ " join p_queue on (q_caseid = c_id and q_status != 'CLS')"
					+ " left join kbstate on st_code = c_rcv_state"
					+ " left join kbcity_district on (cdi_code =c_rcv_district and cdi_stcode=c_rcv_state) "
					+ " where c_pmtid=? order by c_custreceiptnoori, c_createddt  ");
			pst.setInt(1, pmtId);
			rs = pst.executeQuery();
			CaseInformation caseInfo;
			while (rs.next()) {
				caseInfo= new CaseInformation();
				caseInfo.setChangedPrice(rs.getString("c_changedprice"));
				caseInfo.setReceiptAmtB4Change(rs.getDouble("c_priceb4change"));
				caseInfo.setCaseid(rs.getInt("c_id"));
				caseInfo.setName(rs.getString("c_rcv_name"));
				caseInfo.setHp(rs.getString("c_rcv_hp"));
				caseInfo.setState(rs.getString("c_rcv_state"));
				caseInfo.setLocationDetails(rs.getString("addr"));
				caseInfo.setQty(rs.getInt("c_qty"));
				caseInfo.setRmk(rs.getString("c_rmk"));
				caseInfo.setAdvancedPaymentStatus(rs.getString("c_paidinadvance"));
				caseInfo.setAdvancedPmtId(rs.getInt("c_advancepmtid"));
				caseInfo.setReceiptAmt(rs.getInt("c_receiptamt"));
				caseInfo.setShipmentCharge(rs.getInt("c_shipment_cost"));
				caseInfo.setFragile(rs.getString("c_fragile"));
				caseInfo.setRemainingAmt(rs.getDouble("netamt"));
				caseInfo.setStatus(rs.getString("status"));
				caseInfo.setCreateddt(rs.getString("c_createddt"));
				caseInfo.setCustReceiptNoOri(rs.getString("c_custreceiptnoori"));
				caseInfo.setShipmentChargesPaidBysender(rs.getString("c_shipmentpaidbysender"));
				
				caseInfo.setShipmentprofit(rs.getInt("c_shipmentprofit"));
				caseInfo.setPartnershare(rs.getInt("c_partnershare"));
				caseInfo.setRural(rs.getString("c_rural"));
				casesList.add(caseInfo);
			}
			cpb.setShipments(casesList);
		}catch(Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return cpb;
		
	}
	
	/*
	 * use the following method to collect the data of the payment from the pick up agent acct screen 
	 */
	
	public PickUpAgentPaymentBean getPickUpAgentPaymentInfo (Connection conn, int pickUpAgentPmtId) throws Exception {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		PickUpAgentPaymentBean papb = new PickUpAgentPaymentBean();
		 ArrayList<CaseInformation>  casesList= new  ArrayList<CaseInformation>();
		try {
			pst = conn.prepareStatement("select us_name, cppa_paymentdt, cppa_amount_paid, cppa_rmk "
					+ " from p_customer_payments_pickupagents join kbusers on us_id= cppa_pickupagentid  where cppa_id=?");
			pst.setInt(1, pickUpAgentPmtId);
			rs = pst.executeQuery();
			if (rs.next()) {
				papb.setPmtAmt(rs.getDouble("cppa_amount_paid"));
				papb.setPmtDate(rs.getString("cppa_paymentdt"));
				papb.setPickUpAgentName(rs.getString("us_name"));
				papb.setPmtRmk(rs.getString("cppa_rmk"));
			}
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			
			pst = conn.prepareStatement("select c_shipmentpaidbysender, c_name, date(c_createddt) as c_createddt, p_cases.c_id as caseid, c_rcv_name, c_rcv_hp,c_rcv_state, "
					+ " concat(st_name_ar,' - ',ifnull(cdi_name,''),' ' ,ifnull(c_rcv_addr_rmk,'')) as addr , "
					+ " c_qty, c_rmk, c_weight, c_shipment_cost, c_receiptamt, c_bringitemsback, c_fragile , c_sendmoney,"
					+" (case when (q_stage='dlv_stg' and q_step='delivered')  then 'dlv' else 'canceled'  end) as status,"
					+ " c_custreceiptnoori"
					+ " from p_cases  "
					+ " join kbcustomers on kbcustomers.c_id= c_custid"
					+ " join p_queue on (q_caseid = p_cases.c_id and q_status != 'CLS')"
					+ " left join kbstate on st_code = c_rcv_state"
					+ " left join kbcity_district on (cdi_code =c_rcv_district and cdi_stcode=c_rcv_state) "
					+ " where c_pickupagentpmtid=? order by c_name, c_createddt, c_custreceiptnoori ");
			pst.setInt(1, pickUpAgentPmtId);
			rs = pst.executeQuery();
			CaseInformation caseInfo;
			while (rs.next()) {
				caseInfo= new CaseInformation();
				caseInfo.setCustName(rs.getString("c_name"));
				caseInfo.setCaseid(rs.getInt("caseid"));
				caseInfo.setName(rs.getString("c_rcv_name"));
				caseInfo.setHp(rs.getString("c_rcv_hp"));
				caseInfo.setState(rs.getString("c_rcv_state"));
				caseInfo.setLocationDetails(rs.getString("addr"));
				caseInfo.setQty(rs.getInt("c_qty"));
				caseInfo.setRmk(rs.getString("c_rmk"));
				
				caseInfo.setReceiptAmt(rs.getInt("c_receiptamt"));
				caseInfo.setShipmentCharge(rs.getInt("c_shipment_cost"));
				caseInfo.setFragile(rs.getString("c_fragile"));
				
				caseInfo.setStatus(rs.getString("status"));
				caseInfo.setCreateddt(rs.getString("c_createddt"));
				caseInfo.setCustReceiptNoOri(rs.getString("c_custreceiptnoori"));
				caseInfo.setShipmentChargesPaidBysender(rs.getString("c_shipmentpaidbysender"));
				casesList.add(caseInfo);
			}
			papb.setShipments(casesList);
		}catch(Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return papb;
		
	}
	
	
	
	/*
	 * use the following method to collect the data of the payment from the Sender Company up agent acct screen 
	 */
	
	public PickUpAgentPaymentBean getSenderCompanyPaymentInfo (Connection conn, int senderCompanyPmtId) throws Exception {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		PickUpAgentPaymentBean papb = new PickUpAgentPaymentBean();
		 ArrayList<CaseInformation>  casesList= new  ArrayList<CaseInformation>();
		try {
			pst = conn.prepareStatement("select comp_name, cppc_paymentdt, cppc_amount_paid, cppc_rmk "
					+ " from p_customer_payments_company join kbcompanies on comp_id= cppc_companyid  where cppc_id=?");
			pst.setInt(1, senderCompanyPmtId);
			rs = pst.executeQuery();
			if (rs.next()) {
				papb.setPmtAmt(rs.getDouble("cppc_amount_paid"));
				papb.setPmtDate(rs.getString("cppc_paymentdt"));
				papb.setPickUpAgentName(rs.getString("comp_name"));
				papb.setPmtRmk(rs.getString("cppc_rmk"));
			}
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			
			pst = conn.prepareStatement("select c_shipmentprofit, c_partnershare, c_changedprice, c_priceb4change, c_rural, c_shipmentpaidbycustomer, c_shipmentpaidbysender, c_name, date(c_createddt) as c_createddt, p_cases.c_id as caseid, c_rcv_name, c_rcv_hp,c_rcv_state, "
					+ " concat(st_name_ar,' - ',ifnull(cdi_name,''),' ' ,ifnull(c_rcv_addr_rmk,'')) as addr , "
					+ " c_qty,  concat(ifnull(c_specialsendercode,''),c_rmk)as c_rmk, c_weight, c_shipment_cost, c_receiptamt, c_bringitemsback, c_fragile , c_sendmoney,"
					+" (case when (q_stage='dlv_stg' and q_step='delivered')  then 'dlv' else 'canceled'  end) as status,"
					+ " c_custreceiptnoori"
					+ " from p_cases  "
					+ " join kbcustomers on kbcustomers.c_id= c_custid"
					+ " join p_queue on (q_caseid = p_cases.c_id and q_status != 'CLS')"
					+ " left join kbstate on st_code = c_rcv_state"
					+ " left join kbcity_district on (cdi_code =c_rcv_district and cdi_stcode=c_rcv_state) "
					+ " where c_company_senderpmtid=? order by p_cases.c_id ");
			pst.setInt(1, senderCompanyPmtId);
			rs = pst.executeQuery();
			CaseInformation caseInfo;
			while (rs.next()) {
				caseInfo= new CaseInformation();
				caseInfo.setCustName(rs.getString("c_name"));
				caseInfo.setCaseid(rs.getInt("caseid"));
				caseInfo.setName(rs.getString("c_rcv_name"));
				caseInfo.setHp(rs.getString("c_rcv_hp"));
				caseInfo.setState(rs.getString("c_rcv_state"));
				caseInfo.setLocationDetails(rs.getString("addr"));
				caseInfo.setQty(rs.getInt("c_qty"));
				caseInfo.setRmk(rs.getString("c_rmk"));
				caseInfo.setRural(rs.getString("c_rural"));
				caseInfo.setReceiptAmt(rs.getInt("c_receiptamt"));
				caseInfo.setShipmentCharge(rs.getInt("c_shipment_cost"));
				caseInfo.setFragile(rs.getString("c_fragile"));
				
				caseInfo.setChangedPrice(rs.getString("c_changedprice"));
				caseInfo.setReceiptAmtB4Change(rs.getInt("c_priceb4change"));
				
				caseInfo.setShipmentprofit(rs.getInt("c_shipmentprofit"));
				caseInfo.setPartnershare(rs.getInt("c_partnershare"));
				
				caseInfo.setStatus(rs.getString("status"));
				caseInfo.setCreateddt(rs.getString("c_createddt"));
				caseInfo.setCustReceiptNoOri(rs.getString("c_custreceiptnoori"));
				caseInfo.setShipmentChargesPaidBysender(rs.getString("c_shipmentpaidbysender"));
				caseInfo.setShipmentChargesPaidByCustomer(rs.getString("c_shipmentpaidbycustomer"));
				casesList.add(caseInfo);
			}
			papb.setShipments(casesList);
		}catch(Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return papb;
		
	}
	
	
	public CustomerPaymentBean getCustomerReturnedItems (Connection conn, int custId ) throws Exception {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		CustomerPaymentBean cpb = new CustomerPaymentBean();
		 ArrayList<CaseInformation>  casesList= new  ArrayList<CaseInformation>();
		try {
			pst = conn.prepareStatement("select c_name from kbcustomers  where c_id=?");
			pst.setInt(1,custId);
			rs = pst.executeQuery();
			if (rs.next()) {
				cpb.setCustomerName(rs.getString("c_name"));
			}
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}


			String MainSql = "select c_rmk, c_qty, q_stage, q_step,'' as status , c_custid,c_custreceiptnoori,'' as totamt,'' as pmtrmk, '' as pmtdate, "
					+ " date(c_createddt) as c_createddt , c_weight, concat(st_name_ar,' - ', ifnull(cdi_name,''),' ' ,ifnull(c_rcv_addr_rmk,'')) as addr,"
					+ " c_id, "
					+ " c_rcv_name , c_rcv_hp, '' as fromdate, '' as todate,"
					+ " c_receiptamt,"
					+ " c_sendmoney,"
					+ " c_shipment_cost "
					+ " from p_cases "
					+ " join p_queue on (c_id= q_caseid and q_status !='CLS')"
					+ " left join kbstate on st_code = c_rcv_state"
					+ " left join kbcity_district on (cdi_code =c_rcv_district and cdi_stcode=c_rcv_state) "
					+ " where c_custid =?"
					+ " and (q_stage='cncl' and q_step='return_to_cust')   order by c_custreceiptnoori ";
			pst = conn.prepareStatement(MainSql);
			pst.setInt(1, custId);
			rs = pst.executeQuery();
			CaseInformation caseInfo;
			while (rs.next()) {
				caseInfo= new CaseInformation();
				caseInfo.setCaseid(rs.getInt("c_id"));
				caseInfo.setName(rs.getString("c_rcv_name"));
				caseInfo.setHp(rs.getString("c_rcv_hp"));
				caseInfo.setLocationDetails(rs.getString("addr"));
				caseInfo.setQty(rs.getInt("c_qty"));
				caseInfo.setRmk(rs.getString("c_rmk"));
				caseInfo.setReceiptAmt(rs.getInt("c_receiptamt"));
				caseInfo.setShipmentCharge(rs.getInt("c_shipment_cost"));
				
				caseInfo.setCreateddt(rs.getString("c_createddt"));
				caseInfo.setCustReceiptNoOri(rs.getString("c_custreceiptnoori"));
				casesList.add(caseInfo);
			}
			cpb.setShipments(casesList);
		}catch(Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return cpb;
		
	}
	
	
	public CustomerPaymentBean getCustomerReturnedItems (Connection conn, int custId , String branchCode, String rtnDate) throws Exception {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		CustomerPaymentBean cpb = new CustomerPaymentBean();
		 ArrayList<CaseInformation>  casesList= new  ArrayList<CaseInformation>();
		try {
			pst = conn.prepareStatement("select c_name from kbcustomers  where c_id=?");
			pst.setInt(1,custId);
			rs = pst.executeQuery();
			if (rs.next()) {
				cpb.setCustomerName(rs.getString("c_name"));
			}
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}


			String MainSql = "select c_rmk, c_qty, q_stage, q_step,'' as status , c_custid,c_custreceiptnoori,'' as totamt,'' as pmtrmk, '' as pmtdate, "
					+ " date(c_createddt) as c_createddt , c_weight, concat(st_name_ar,' - ',ifnull(cdi_name,''),' ' ,ifnull(c_rcv_addr_rmk,'')) as addr, c_id, "
					+ " c_rcv_name , c_rcv_hp, '' as fromdate, '' as todate,"
					+ " c_receiptamt,"
					+ " c_sendmoney,"
					+ " c_shipment_cost "
					+ " from p_cases  "
					+ " join p_queue on (c_id= q_caseid )"
					+ " left join kbstate on st_code = c_rcv_state"
					+ " left join kbcity_district on (cdi_code =c_rcv_district and cdi_stcode=c_rcv_state) "
					+ " where c_custid =?"
					+ "  and q_stage = 'cncl' and q_step = 'delv_back_to_shipper' and q_status ='END'  and c_branchcode=? and  (q_enterdate >=STR_TO_DATE(?,'%Y-%m-%d') " + 
					"	 and q_enterdate<adddate(STR_TO_DATE(?,'%Y-%m-%d'),1)) order by c_custreceiptnoori ";
			pst = conn.prepareStatement(MainSql);
			pst.setInt(1, custId);
			pst.setString(2, branchCode);
			pst.setString(3, rtnDate);
			pst.setString(4, rtnDate);
			rs = pst.executeQuery();
			CaseInformation caseInfo;
			
			while (rs.next()) {
				//System.out.println("found");
				caseInfo= new CaseInformation();
				caseInfo.setCaseid(rs.getInt("c_id"));
				caseInfo.setName(rs.getString("c_rcv_name"));
				caseInfo.setHp(rs.getString("c_rcv_hp"));
				caseInfo.setLocationDetails(rs.getString("addr"));
				caseInfo.setQty(rs.getInt("c_qty"));
				caseInfo.setRmk(rs.getString("c_rmk"));
				caseInfo.setReceiptAmt(rs.getInt("c_receiptamt"));
				caseInfo.setShipmentCharge(rs.getInt("c_shipment_cost"));
				
				caseInfo.setCreateddt(rs.getString("c_createddt"));
				caseInfo.setCustReceiptNoOri(rs.getString("c_custreceiptnoori"));
				casesList.add(caseInfo);
			}
			cpb.setShipments(casesList);
		}catch(Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return cpb;
		
	}
	
	
public CustomerPaymentBean getSenderCompanyReturnedItems (Connection conn, String senderCompanyId, String rtnDate) throws Exception {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		CustomerPaymentBean cpb = new CustomerPaymentBean();
		 ArrayList<CaseInformation>  casesList= new  ArrayList<CaseInformation>();
		try {
			pst = conn.prepareStatement("select comp_name from kbcompanies  where comp_id=?");
			pst.setString(1,senderCompanyId);
			rs = pst.executeQuery();
			if (rs.next()) {
				cpb.setCustomerName(rs.getString("comp_name"));
			}
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}


			String MainSql = "select c_name, c_rmk, c_qty, q_stage, q_step,'' as status , c_custid,c_custreceiptnoori,"
					+ "'' as totamt,'' as pmtrmk, '' as pmtdate, "
					+ " date(c_createddt) as c_createddt , c_weight, "
					+ "concat(st_name_ar,' - ',ifnull(cdi_name,''),' ' ,ifnull(c_rcv_addr_rmk,'')) as addr, p_cases.c_id as caseid, "
					+ " c_rcv_name , c_rcv_hp, '' as fromdate, '' as todate,"
					+ " c_receiptamt,"
					+ " c_sendmoney,"
					+ " c_shipment_cost "
					+ " from p_cases  "
					+ " join p_queue on (c_id= q_caseid )"
					+ " left join kbstate on st_code = c_rcv_state"
					+ " left join kbcustomers on kbcustomers.c_id = c_custid  "
					+ " left join kbcity_district on (cdi_code =c_rcv_district and cdi_stcode=c_rcv_state) "
					+ " where c_company_sender =?"
					+ "  and q_stage = 'cncl' and q_step = 'delv_back_to_shipper' and q_status ='END'  "
					+ " and  (q_enterdate >=STR_TO_DATE(?,'%Y-%m-%d') " + 
					"	 and q_enterdate<adddate(STR_TO_DATE(?,'%Y-%m-%d'),1)) order by c_custreceiptnoori ";
			pst = conn.prepareStatement(MainSql);
			pst.setString(1, senderCompanyId);
			pst.setString(2, rtnDate);
			pst.setString(3, rtnDate);
			rs = pst.executeQuery();
			CaseInformation caseInfo;
			
			while (rs.next()) {
				//System.out.println("found");
				caseInfo= new CaseInformation();
				caseInfo.setCaseid(rs.getInt("caseid"));
				caseInfo.setName(rs.getString("c_rcv_name"));
				caseInfo.setHp(rs.getString("c_rcv_hp"));
				caseInfo.setLocationDetails(rs.getString("addr"));
				caseInfo.setQty(rs.getInt("c_qty"));
				caseInfo.setRmk(rs.getString("c_rmk"));
				caseInfo.setReceiptAmt(rs.getInt("c_receiptamt"));
				caseInfo.setShipmentCharge(rs.getInt("c_shipment_cost"));
				caseInfo.setCustName(rs.getString("c_name"));
				caseInfo.setCreateddt(rs.getString("c_createddt"));
				caseInfo.setCustReceiptNoOri(rs.getString("c_custreceiptnoori"));
				casesList.add(caseInfo);
			}
			cpb.setShipments(casesList);
		}catch(Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return cpb;
		
	}
	
	
	public ArrayList<CaseInformation> getItemsPerDriver(Connection conn, String driverid, String stgCode, String stpCode , String storeCode, 
			String fromdt, String todt)throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		 ArrayList<CaseInformation>  deliveryList= new  ArrayList<CaseInformation>();
		try {
			if (fromdt.equalsIgnoreCase("ALL")) {
				pst = conn.prepareStatement("select c_custid,c_rcv_district, c_rural, comp_name, c_company_sender, date(c_createddt) as c_createddt ,q_id, c_name, p_cases.c_id, c_rcv_name, c_rcv_hp,c_rcv_state, "
						+ " concat(st_name_ar,' - ',ifnull(cdi_name,''),' ',ifnull(c_rcv_addr_rmk,'')) as addr , "
						+ " c_qty, c_rmk, c_weight, c_shipment_cost, c_receiptamt, c_bringitemsback, c_fragile , c_sendmoney, c_custreceiptnoori"
						+ " from p_cases  "
						+ " join p_queue on (q_caseid = p_cases.c_id and q_stage =? and q_step=? and q_status = 'ACTV')"
						+ " left join kbcustomers on kbcustomers.c_id = c_custid "
						+ " left join kbstate on st_code = c_rcv_state"
						+ " left join kbcity_district on (cdi_code =c_rcv_district and cdi_stcode=c_rcv_state) "
						+ " left join kbcompanies on comp_id = c_company_sender  "
						+ " where c_assignedagent=? and c_branchcode=? order by c_rcv_state ,c_rcv_district, c_id ");
				pst.setString(1, stgCode);
				pst.setString(2, stpCode);
				pst.setString(3, driverid);
				pst.setString(4, storeCode);
			}else {
				pst = conn.prepareStatement("select c_custid,c_rcv_district, c_rural, comp_name, c_company_sender, date(c_createddt) as c_createddt, q_id, c_name, p_cases.c_id, c_rcv_name, c_rcv_hp,c_rcv_state, "
						+ " concat(st_name_ar,' - ',ifnull(cdi_name,''),' ',ifnull(c_rcv_addr_rmk,'')) as addr , "
						+ " c_qty, c_rmk, c_weight, c_shipment_cost, c_receiptamt, c_bringitemsback, c_fragile , c_sendmoney, c_custreceiptnoori"
						+ " from p_cases  "
						+ " join p_queue on (q_caseid = p_cases.c_id and q_stage =? and q_step=? and q_status = 'ACTV')"
						+ " left join kbcustomers on kbcustomers.c_id = c_custid "
						+ " left join kbstate on st_code = c_rcv_state"
						+ " left join kbcity_district on (cdi_code =c_rcv_district and cdi_stcode=c_rcv_state) "
						+ " left join kbcompanies on comp_id = c_company_sender  "
						+ " where c_assignedagent=? and c_branchcode=? and  (date(c_createddt)>=? ) and (date(c_createddt)<=? ) "
						+ " order by c_rcv_state,c_rcv_district, c_id  ");
				pst.setString(1, stgCode);
				pst.setString(2, stpCode);
				pst.setString(3, driverid);
				pst.setString(4, storeCode);
				pst.setString(5, fromdt);
				pst.setString(6, todt);
			}
			rs = pst.executeQuery();
			CaseInformation caseInfo;
			while (rs.next()) {
				caseInfo= new CaseInformation();
				caseInfo.setQid(rs.getInt("q_id"));
				caseInfo.setCaseid(rs.getInt("c_id"));
				caseInfo.setName(rs.getString("c_rcv_name"));
				caseInfo.setHp(rs.getString("c_rcv_hp"));
				caseInfo.setState(rs.getString("c_rcv_state"));
				caseInfo.setLocationDetails(rs.getString("addr"));
				caseInfo.setQty(rs.getInt("c_qty"));
				caseInfo.setRmk(rs.getString("c_rmk"));
				caseInfo.setCreateddt(rs.getString("c_createddt"));
				caseInfo.setReceiptAmt(rs.getInt("c_receiptamt"));
				caseInfo.setShipmentCharge(rs.getInt("c_shipment_cost"));
				caseInfo.setFragile(rs.getString("c_fragile"));
				caseInfo.setCustReceiptNoOri(rs.getString("c_custreceiptnoori"));
				caseInfo.setSenderName(rs.getString("c_name"));
				caseInfo.setSenderCompanyId(rs.getString("c_company_sender"));
				caseInfo.setSenderCompanyName(rs.getString("comp_name"));
				caseInfo.setRural(rs.getString("c_rural"));
				caseInfo.setDistrict(rs.getString("c_rcv_district"));
				caseInfo.setCustId(rs.getInt("c_custid"));
				
				deliveryList.add(caseInfo);
			}
			
		}catch(Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return deliveryList;
	}
	
	
	public ArrayList<CaseInformation> getRtnWithRcvAgent(Connection conn, String driverid, String stgCode, String stpCode , String storeCode, 
			HashMap<String,String> filterMap)throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		 ArrayList<CaseInformation>  deliveryList= new  ArrayList<CaseInformation>();
		try {
			String sql = "select ifnull(comp_name,'') as comp_name, c_company_sender, date(q_enterdate) as q_enterdate, q_id, c_name, p_cases.c_id, c_rcv_name, c_rcv_hp,c_rcv_state, "
					+ " concat(st_name_ar,' - ',ifnull(cdi_name,''),' ',ifnull(c_rcv_addr_rmk,'')) as addr , "
					+ " c_qty, c_rmk, c_weight, c_shipment_cost, c_receiptamt, c_bringitemsback,(q_enterdate+INTERVAL 9 HOUR) as qdate, "
					+ " (q_enterdate+INTERVAL 9 HOUR) as qdate, c_custreceiptnoori"
					+ " from p_cases  "
					+ " join p_queue on (q_caseid = p_cases.c_id and q_stage =? and q_step=? and q_status = 'ACTV')"
					+ " left join kbcustomers on kbcustomers.c_id = c_custid "
					+ " left join kbstate on st_code = c_rcv_state"
					+ " left join kbcity_district on (cdi_code =c_rcv_district and cdi_stcode=c_rcv_state) "
					+ " left join kbcompanies on comp_id = c_company_sender "
					+ " where c_branchcode=? ";
			
			for (String col : filterMap.keySet()) {
				if (col.equalsIgnoreCase("q_enterdate")) {
					sql += " and (date((q_enterdate+INTERVAL 9 HOUR))=?)";
				}else {
					sql += " and "+col+"=?";
				}
			}
			sql += " order by c_company_sender, c_rcv_state,c_rcv_district, c_id  ";
			pst = conn.prepareStatement(sql);
			pst.setString(1, stgCode);
			pst.setString(2, stpCode);
			pst.setString(3, storeCode);
			int i =4;
			for (String col : filterMap.keySet()) {
				pst.setString(i, filterMap.get(col));
				i++;
			}
			rs = pst.executeQuery();
			CaseInformation caseInfo;
			while (rs.next()) {
				caseInfo= new CaseInformation();
				caseInfo.setQid(rs.getInt("q_id"));
				caseInfo.setCaseid(rs.getInt("c_id"));
				caseInfo.setName(rs.getString("c_rcv_name"));
				caseInfo.setHp(rs.getString("c_rcv_hp"));
				caseInfo.setState(rs.getString("c_rcv_state"));
				caseInfo.setLocationDetails(rs.getString("addr"));
				caseInfo.setQty(rs.getInt("c_qty"));
				caseInfo.setRmk(rs.getString("c_rmk"));
				caseInfo.setCreateddt(rs.getString("qdate"));
				caseInfo.setReceiptAmt(rs.getInt("c_receiptamt"));
				caseInfo.setShipmentCharge(rs.getInt("c_shipment_cost"));
				caseInfo.setCustReceiptNoOri(rs.getString("c_custreceiptnoori"));
				caseInfo.setSenderName(rs.getString("c_name"));
				caseInfo.setSenderCompanyId(rs.getString("c_company_sender"));
				caseInfo.setSenderCompanyName(rs.getString("comp_name"));
				deliveryList.add(caseInfo);
			}
			
		}catch(Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return deliveryList;
	}
	
	/*
	 * get driver name
	 */
	
	public String getDriverName(Connection conn, String driverid) throws Exception {
		String driverName = "";
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = conn.prepareStatement("select us_name from kbusers  where us_id=? ");
			pst.setString(1, driverid);
			rs = pst.executeQuery();
			if (rs.next())
				driverName = rs.getString("us_name");
			
		}catch(Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return driverName;
	}
	
	/*
	 * get driver city destination
	 */
	
	public String getDriverCityDestination(Connection conn, String driverid) throws Exception {
		String cityName = "";
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = conn.prepareStatement("select st_name_ar from kbusers join kbstate on us_to_state = st_code where us_id=? ");
			pst.setString(1, driverid);
			rs = pst.executeQuery();
			if (rs.next())
				cityName = rs.getString("st_name_ar");
			
		}catch(Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return cityName;
	}
	
	
	
	/*
	 *  get the customer name using the customer id
	 */
	public String getCustomerName(Connection conn , int custid)throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		String custName ="";
		try{
			pst = conn.prepareStatement("select c_name from kbcustomers where c_id=?");
			pst.setInt(1, custid);
			rs = pst.executeQuery();
			if (rs.next())
				custName = rs.getString ("c_name");
		}catch (Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
		}
		return custName;
	}
	
	
	/*
	 *  get the customer Phone number using the customer id
	 */
	public String getCustomerHP(Connection conn , int custid)throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		String custHp ="";
		try{
			pst = conn.prepareStatement("select c_phone1 from kbcustomers where c_id=?");
			pst.setInt(1, custid);
			rs = pst.executeQuery();
			if (rs.next())
				custHp = rs.getString ("c_phone1");
		}catch (Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
		}
		return custHp;
	}
	
	
	public double getCustomerDebt(Connection  conn, int custid) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		double remAmt = 0.0;
		try{
			pst = conn.prepareStatement(" select sum(amt) from vw_custbalance where pcust =?");
			pst.setInt(1, custid);
			rs = pst.executeQuery();
			if (rs.next()) {
				remAmt = rs.getDouble(1);
			}
		}catch (Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
		}
		return remAmt;
	}
	public double getTotCost(Connection  conn, int cp_id) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		double remAmt = 0.0;
		try{
			pst = conn.prepareStatement(" select cpay_pctot from p_custpay where cpay_pcid =?");
			pst.setInt(1, cp_id);
			rs = pst.executeQuery();
			if (rs.next()) {
				remAmt = rs.getDouble("cpay_pctot");
			}
		}catch (Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
		}
		return remAmt;
	}
	
	/* I removed the kbcity table
	public String getStateOfCity(Connection conn, String city) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		String stateCode="";
		try{
			//System.out.println("---->"+city);
			pst = conn.prepareStatement("SELECT ct_statecode FROM kbcity where ct_code=? ");
			pst.setString(1, city);
			rs = pst.executeQuery();
			if (rs.next()) {
				stateCode = rs.getString("ct_statecode");
			}
		}catch (Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {s}
		}
		return stateCode;
	}
	*/
	
	// get hashMap of district by state
	public LinkedHashMap<String,String > getDistrictOfState(Connection conn, String destState)throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		LinkedHashMap<String,String> dist = new LinkedHashMap<String,String>();
		try{
			pst = conn.prepareStatement("SELECT cdi_code, cdi_name from kbcity_district where cdi_stcode=? order by cdi_name");
			pst.setString(1, destState);
			rs = pst.executeQuery();
			while (rs.next()) {
				dist.put(rs.getString("cdi_code") , rs.getString("cdi_name"));
			}
		}catch (Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
		}
		return dist;
	}
	
	/*
	 * calculate the shipment charges
	 */
	public double calcShipmentChargesBasedOnDestCity(Connection conn,  String destState  , boolean rural , String custName, int senderCompanyId ) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		double shipmentCharge = 0.0;
		try{
			pst = conn.prepareStatement(" select st_charges, st_ruralcharges from kbstate where st_code =? and st_active='Y'");
			pst.setString(1, destState);
			rs = pst.executeQuery();
			if (rs.next()) {
				if (rural )
					shipmentCharge = rs.getDouble("st_ruralcharges");
				else
					shipmentCharge = rs.getDouble("st_charges");
			}
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
			
			//get the shipment cost per company
			if (senderCompanyId > 0 ) {
				pst = conn.prepareStatement(" select csp_price, csp_ruralprice from kbcompany_special_prices where csp_statecode =?  and csp_compid=?");
				pst.setString(1, destState);
				pst.setDouble(2, senderCompanyId);
				rs = pst.executeQuery();
				if (rs.next()) {
					if (rural )
						shipmentCharge = rs.getDouble("csp_ruralprice");
					else
						shipmentCharge = rs.getDouble("csp_price");
				}
				try {rs.close();}catch(Exception e) {/*ignore*/}
				try {pst.close();}catch(Exception e) {/*ignore*/}
			}
			
			if (custName!=null) {
				pst = conn.prepareStatement(" select sp_price, sp_rural_price "
						+ " from kbcustomers join kb_special_prices on c_id = sp_custid   where c_name =? and sp_statecode=?");
				pst.setString(1, custName);
				pst.setString(2, destState);
				rs = pst.executeQuery();
				if (rs.next()) {
					if (rural)
						shipmentCharge = rs.getDouble("sp_rural_price");
					else
						shipmentCharge = rs.getDouble("sp_price");
				}
			}
		}catch (Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
		}
		return shipmentCharge;
	}
	/*
	 * calculate the shipment charges
	 */
	public double calcShipmentChargesBasedOnDestCity(Connection conn, String destState ,boolean rural , int custid, int senderCompanyId ) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		double shipmentCharge = 0.0;
		/*System.out.println("destState = "+destState);
		System.out.println("rural = "+rural);
		System.out.println("custid = "+custid);
		System.out.println("senderCompanyId = "+senderCompanyId);*/

		try{
			
			pst = conn.prepareStatement(" select st_charges, st_ruralcharges from kbstate where st_code =? and st_active='Y'");
			pst.setString(1, destState);
			//System.out.println("destState->"+destState);
			rs = pst.executeQuery();
			if (rs.next()) {
				if (rural )
					shipmentCharge = rs.getDouble("st_ruralcharges");
				else
					shipmentCharge = rs.getDouble("st_charges");
				
				//System.out.println("in rs, shipmentCharge->"+shipmentCharge);
			}
			//System.out.println("here in calc");
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
			
			//get the shipment cost per company
			if (senderCompanyId > 0 ) {
				pst = conn.prepareStatement(" select csp_price, csp_ruralprice from kbcompany_special_prices where csp_statecode =?  and csp_compid=?");
				pst.setString(1, destState);
				pst.setDouble(2, senderCompanyId);
				rs = pst.executeQuery();
				if (rs.next()) {
					if (rural )
						shipmentCharge = rs.getDouble("csp_ruralprice");
					else
						shipmentCharge = rs.getDouble("csp_price");
				}
				try {rs.close();}catch(Exception e) {/*ignore*/}
				try {pst.close();}catch(Exception e) {/*ignore*/}
			}
			
			// check if the customer have discount
			if (custid >0) {
				
				pst = conn.prepareStatement(" select sp_price, sp_rural_price "
						+ " from  kb_special_prices  where sp_custid =?  and sp_statecode=?");
				pst.setInt(1, custid);
				pst.setString(2, destState);
				rs = pst.executeQuery();
				
				if (rs.next()) {
					if (rural)
						shipmentCharge = rs.getDouble("sp_rural_price");
					else
						shipmentCharge = rs.getDouble("sp_price");
				}
			}
		}catch (Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
		}
		return shipmentCharge;
	}
	
	/*
	 * calculate the agent shipment charges share
	 */
	public double calcAgentShipmentChargesShare(Connection conn,int compId, String destState, String districtCode , boolean rural,  String agentId ) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		double agentshipmentCharge = 0.0;
		boolean priceFound=false;
		try{
			//first check special price for companies
			if(districtCode!=null && compId>0) {
				pst = conn.prepareStatement("select ifnull(csp_charges,0) as agentshare, ifnull(csp_ruralcharges,0) as agentsharerural from kbcompany_special_prices"
							+ "  where csp_compid = ? and csp_statecode = ?");
				pst.setInt(1, compId);
				pst.setString(2, destState);
				rs = pst.executeQuery();
				if(rs.next()) {
					if(rural) {
						if(rs.getDouble("agentsharerural")>0) {
							agentshipmentCharge = rs.getDouble("agentsharerural");
							priceFound=true;
						}
					}else if(!rural) {
						if(rs.getDouble("agentshare")>0) {
							agentshipmentCharge = rs.getDouble("agentshare");
							priceFound=true;
						}
					}
				}
			}
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
			
			// first check if there is share specified for agent and district (become second)
			if (agentId !=null && agentId.trim().length()>0) {
				pst = conn.prepareStatement("select ifnull(agdi_agentshare,0) as agentshare, ifnull(agdi_agentsharepriority,'N') as priority "
						+ "  from kbagent_district where agdi_usid = ? and agdi_districtcode=?");
				pst.setString(1, agentId);
				pst.setString(2, districtCode);	
				rs = pst.executeQuery();
				if (rs.next()) {
					if (rs.getString("priority").equalsIgnoreCase("Y")) {
						priceFound = false;
					}
					if (rs.getDouble("agentshare")>0 && !priceFound) {
						agentshipmentCharge = rs.getDouble("agentshare");
						priceFound = true;
					}
				}
			}
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
			
			//System.out.println("agentshipmentCharge---->"+agentshipmentCharge);
		
			
			// 2 if not found then check if there is district and there is price for it (become 3)
			if (districtCode!=null && !(agentshipmentCharge>0)) {
				pst = conn.prepareStatement(" select cdi_agentshare from kbcity_district where cdi_code=?  and cdi_agentshare >0 and cdi_stcode=?");
				pst.setString(1, districtCode);	
				pst.setString(2, destState);	
				rs = pst.executeQuery();
				if (rs.next()) {
					agentshipmentCharge = rs.getDouble("cdi_agentshare");
					priceFound=true;
				}
				try {rs.close();}catch(Exception e) {/*ignore*/}
				try {pst.close();}catch(Exception e) {/*ignore*/}
			
			}
		//	System.out.println("agentshipmentCharge---->"+agentshipmentCharge);
			// 3 last we get the basic price in states (become 4)
			if (!priceFound) {
				pst = conn.prepareStatement(" select st_agent_share, st_agent_share_rural from kbstate where st_code=? and st_active='Y'");
				pst.setString(1, destState);			
				rs = pst.executeQuery();
				if (rs.next()) {
					if (rural)
						agentshipmentCharge = rs.getDouble("st_agent_share_rural");
					else
						agentshipmentCharge = rs.getDouble("st_agent_share");
				}
			}
		//	System.out.println("agentshipmentCharge---->"+agentshipmentCharge);
		}catch (Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
		}
		return agentshipmentCharge;
	}
	
	
	
	/*
	 * get list of pick up agents
	 */
	
	public LinkedHashMap<String,String> getListOfPickUpAgents (Connection conn ) throws Exception{
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		LinkedHashMap<String,String> pickUpAgentsList = new LinkedHashMap<String,String>();
		try{
			pst = conn.prepareStatement("select us_id , us_name from kbusers where us_rank = 'PICKUPAGENT' order by us_name");
			//pst.setString(1, branchCode);
			rs = pst.executeQuery();
			while (rs.next()){
				pickUpAgentsList.put(rs.getString("us_id"), rs.getString("us_name"));
			}
		}catch (Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
		}
		return pickUpAgentsList;
	}
	
	
	/*
	 * get list of pick up agents
	 */
	
	public LinkedHashMap<String,String> getListOfCompanies (Connection conn ) throws Exception{
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		LinkedHashMap<String,String> senderCompanyList = new LinkedHashMap<String,String>();
		try{
			pst = conn.prepareStatement("select comp_id , comp_name from kbcompanies ");
			//pst.setString(1, branchCode);
			rs = pst.executeQuery();
			while (rs.next()){
				senderCompanyList.put(rs.getString("comp_id"), rs.getString("comp_name"));
			}
		}catch (Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
		}
		return senderCompanyList;
	}
	
	/*
	 * get list of customers
	 */
	public LinkedHashMap<String,String> getListOfcustomers(Connection conn, String branchCode ) throws Exception{
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		LinkedHashMap<String,String> customersList = new LinkedHashMap<String,String>();
		try{
			pst = conn.prepareStatement("select c_id , c_name from kbcustomers order by c_name");
			//pst.setString(1, branchCode);
			rs = pst.executeQuery();
			while (rs.next()){
				customersList.put(rs.getString("c_id"), rs.getString("c_name"));
			}
		}catch (Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
		}
		return customersList;
	}
	
	/*
	 * get list of agent alpha ordered
	 */
	public LinkedHashMap<String,String> getListOfAgents(Connection conn , String branchCode ) throws Exception{
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		LinkedHashMap<String,String> agentsList = new LinkedHashMap<String,String>();
		try{
			pst = conn.prepareStatement("select us_id , us_name from kbusers where us_rank='DLVAGENT' order by us_name");
			rs = pst.executeQuery();
			while (rs.next()){
				agentsList.put(rs.getString("us_id"), rs.getString("us_name"));
			}
		}catch (Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
		}
		return agentsList;
	}
	
	/*
	 * get list of agent alpha ordered
	 */
	public LinkedHashMap<String,String> getListOfAgentsPerState(Connection conn , String state ) throws Exception{
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		LinkedHashMap<String,String> agentsList = new LinkedHashMap<String,String>();
		try{
			pst = conn.prepareStatement("select us_id , us_name from kbusers where us_rank='DLVAGENT' and us_to_state like ? order by us_name");
			pst.setString(1, "%"+state+"%");
			rs = pst.executeQuery();
			while (rs.next()){
				agentsList.put(rs.getString("us_id"), rs.getString("us_name"));
			}
		}catch (Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
		}
		return agentsList;
	}
	
	/*
	 * get pickup agent info
	 */
	public LinkedHashMap<String,String> getPickUpAgentInfo(Connection conn , String c_id ) throws Exception{
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		LinkedHashMap<String,String> pickUpAgentInfo = new LinkedHashMap<String,String>();
		try{
			pst = conn.prepareStatement("select us_name , us_hp from kbusers   where us_id=?");
			pst.setString(1, c_id);
			rs = pst.executeQuery();
			while (rs.next()){
				pickUpAgentInfo.put("name", rs.getString("us_name"));
				pickUpAgentInfo.put("hp", rs.getString("us_hp"));
				//customerInfo.put("joineddate", rs.getString("joineddate"));
			}
		}catch (Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
		}
		return pickUpAgentInfo; 
	}
	
	/*
	 * get sender companies info
	 */
	public LinkedHashMap<String,String> getSenderCompanyInfo(Connection conn , String c_id ) throws Exception{
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		LinkedHashMap<String,String> pickUpAgentInfo = new LinkedHashMap<String,String>();
		try{
			pst = conn.prepareStatement("select us_name , us_hp from kbusers   where us_id=?");
			pst.setString(1, c_id);
			rs = pst.executeQuery();
			while (rs.next()){
				pickUpAgentInfo.put("name", rs.getString("us_name"));
				pickUpAgentInfo.put("hp", rs.getString("us_hp"));
				//customerInfo.put("joineddate", rs.getString("joineddate"));
			}
		}catch (Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
		}
		return pickUpAgentInfo; 
	}
	
	
	/*
	 * get customer info
	 */
	public LinkedHashMap<String,String> getcustomerInfo(Connection conn , String c_id ) throws Exception{
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		LinkedHashMap<String,String> customerInfo = new LinkedHashMap<String,String>();
		try{
			pst = conn.prepareStatement("select c_name , c_phone1, date(cust_createddt) as joineddate from kbcustomers  where c_id=?");
			pst.setString(1, c_id);
			rs = pst.executeQuery();
			while (rs.next()){
				customerInfo.put("name", rs.getString("c_name"));
				customerInfo.put("hp", rs.getString("c_phone1"));
				customerInfo.put("joineddate", rs.getString("joineddate"));
			}
		}catch (Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
		}
		return customerInfo;
	}
	
	/*
	 * get agent info
	 */
	public LinkedHashMap<String,String> getAgentInfo(Connection conn , String agentId ) throws Exception{
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		LinkedHashMap<String,String> agentInfo = new LinkedHashMap<String,String>();
		try{
			pst = conn.prepareStatement("select us_name , us_createddt from kbusers where  us_id=?");
			pst.setString(1, agentId);
			rs = pst.executeQuery();
			while (rs.next()){
				agentInfo.put("name", rs.getString("us_name"));
				//agentInfo.put("hp", rs.getString("c_phone1"));
				agentInfo.put("joineddate", rs.getString("us_createddt"));
			}
		}catch (Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
		}
		return agentInfo;
	}

	/*
	 * get list of rcp numbers
	 */
	public ArrayList<String> getRcpNoList (Connection conn, int bookId ) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		ArrayList<String> rcpIdList = new ArrayList<String>();
		try{
			pst = conn.prepareStatement("select br_rcp_no from p_books_rcp where br_bid=?");
			pst.setInt(1, bookId);
			rs = pst.executeQuery();
			while (rs.next()){
				rcpIdList.add(rs.getString("br_rcp_no"));
			}
		}catch (Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
		}
		return rcpIdList;
	}
	
	/*@ changes comma seperated by seperator like : or ,  to list
	 * new method added on 15/Mar/2017 by Nafie
	 */
	public static ArrayList<String> SplitStringToArrayList(String StrWithSeperator , String seperator){
		ArrayList<String> convertedList = new ArrayList<String>();
		if (StrWithSeperator!=null && StrWithSeperator.trim()!=null && !StrWithSeperator.trim().equals("")){
			//System.out.println("StrWithSeperator===>"+StrWithSeperator);
			String [] myArr = StrWithSeperator.split(seperator.trim());
			for (int i=0 ; i<myArr.length ; i++)
				convertedList.add(myArr[i]);		
		}
		return convertedList;
	}
	/*@ change array list string to 'str1','str2',..etc_
	 */
	public StringBuilder getSingleQuoteCommaSeperated(ArrayList<String> array){
		boolean first = true;
		StringBuilder sb = new StringBuilder("");
		for (String item : array){
			if (!first)
				sb.append(",");
			
			sb.append("'"+item+"'");
			first = false;
		}
		return sb;
	}
	
	public boolean checkGeneratedReceipt(Connection conn, String generatedReceiptNo, int custId) throws Exception{
		boolean ok = true;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			//check if the user exists
			pst = conn.prepareStatement("select 1 from p_books_rcp where br_rcp_no=?");
			pst.setString(1, generatedReceiptNo);
			rs = pst.executeQuery();
			if (rs.next()){
				ok = true;
			}else {
				ok = false;
				throw new Exception ("الوصل رقم "+generatedReceiptNo+" غير متولد من النظام");
			}
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
			
			//check if the receipt is used before
			pst = conn.prepareStatement("select br_cid from p_books_rcp where br_rcp_no=? and br_cid >0");
			pst.setString(1, generatedReceiptNo);
			rs = pst.executeQuery();
			if (rs.next()){
				ok = false;
				throw new Exception ("الوصل رقم "+generatedReceiptNo+" تم أستعماله سابقا");
			}
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
			
			// check if the receipt is under another customer
			pst = conn.prepareStatement("select br_custid from p_books_rcp where br_rcp_no=?");
			pst.setString(1, generatedReceiptNo);
			rs = pst.executeQuery();
			if (rs.next()){
				
				if (rs.getInt("br_custid")>0)
					if (custId != rs.getInt("br_custid") ) {
						ok = false;
						throw new Exception ("هذا الوصل "+generatedReceiptNo+" ملك لزبون أخر  ");
					}
			}
			
		}catch (Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
		}
		return ok;
	}
	
	public int getOwnerOfReceipt(Connection conn, String receiptNo)throws Exception{
	
		PreparedStatement pst = null;
		ResultSet rs = null;
		int custId = 0;
		try{
			// check if the receipt is under another customer
			pst = conn.prepareStatement("select br_custid from p_books_rcp where br_rcp_no=?");
			pst.setString(1, receiptNo);
			rs = pst.executeQuery();
			if (rs.next()){
				custId = rs.getInt("br_custid");
			}else {
				new Exception ("الوصل رقم "+receiptNo+" لم يتولد من النظام");
			}
			
		}catch (Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
		}
		return custId;
	}
	
	public boolean checkIfReceiptGeneratedFromSystem(Connection conn, String receiptNo)throws Exception{
		boolean ok = true;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			pst = conn.prepareStatement("select 1 from p_books_rcp where br_rcp_no=?");
			pst.setString(1, receiptNo);
			rs = pst.executeQuery();
			if (rs.next()){
				ok = true;
			}else {
				ok = false;
				throw new Exception ("الوصل رقم "+receiptNo+" غير متولد من النظام");
			}
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
			
		}catch (Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
		}
		return ok;
	}
	public boolean checkIfReceiptUsedBefore(Connection conn, String receiptNo)throws Exception{
		boolean ok = false;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try{
			pst = conn.prepareStatement("select br_cid from p_books_rcp where br_rcp_no=? and br_cid >0");
			pst.setString(1, receiptNo);
			rs = pst.executeQuery();
			if (rs.next()){
				ok = true;
				throw new Exception ("الوصل رقم "+receiptNo+" تم أستعماله سابقا");
			}
			
		}catch (Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
		}
		return ok;
	}
	
	
	public String writeToFileServer(InputStream inputStream, String fileName, String updDir) throws Exception {
        String qualifiedUploadFilePath = updDir + fileName;
        Path folder = Paths.get(updDir);
        if (!Files.exists(folder))
        	throw new Exception ("folder does no existe-->"+folder);
     
        Path file = Files.createTempFile(folder, "-"+fileName,fileName);
        qualifiedUploadFilePath = file.getFileName().toString();
        try (InputStream input = inputStream) {
		    Files.copy(input, file, StandardCopyOption.REPLACE_EXISTING);
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
        return qualifiedUploadFilePath;
    }
	
	
	public HashMap<String,String> getCaseFinInfo(Connection conn, String caseId) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		HashMap<String,String> caseFinInfo = new  HashMap<String,String>();
		try {
			pst = conn.prepareStatement("select ifnull(us_name,' ') as us_name , ifnull(c_advancepmtid,'') as c_advancepmtid, "
					+ "ifnull(c_agentpmtid,'') as c_agentpmtid, ifnull(c_pmtid,'')as c_pmtid , ifnull(c_pickupagentpmtid, '') as c_pickupagentpmtid,"
					+ " ifnull(c_company_senderpmtid,'') as c_company_senderpmtid "
					+ " from p_cases  "
					+ " left join kbusers on us_id = c_pickupagent "
					+ " where c_id = ?");
			pst.setString(1, caseId);
			rs = pst.executeQuery();
			if (rs.next()) {
				caseFinInfo.put("pickupagent", rs.getString("us_name"));
				caseFinInfo.put("c_advancepmtid", rs.getString("c_advancepmtid"));
				caseFinInfo.put("c_agentpmtid", rs.getString("c_agentpmtid"));
				caseFinInfo.put("c_pmtid", rs.getString("c_pmtid"));
				caseFinInfo.put("c_pickupagentpmtid", rs.getString("c_pickupagentpmtid"));
				caseFinInfo.put("c_company_senderpmtid", rs.getString("c_company_senderpmtid"));
			}
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return caseFinInfo;
	}
	

	/**
	 * M.Nafie
	 * @param conn
	 * @param qid
	 * @return
	 * @throws Exception
	 */
	public int getCaseIdFromQid(Connection conn,int qid)throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		int caseId=0;
		try {
			pst = conn.prepareStatement("select q_caseid from p_queue where q_id=?");
			pst.setInt(1, qid);
			rs = pst.executeQuery();
			rs.next();
			caseId = rs.getInt("q_caseid");
			
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return caseId;
	}
	
	
	public void changeReceiptPrice(Connection conn, int caseId , double newPrice, String userId)throws Exception{
		
		PreparedStatement pst = null;
		try{
			pst = conn.prepareStatement("update p_cases set c_priceb4change = c_receiptamt , c_receiptamt=? , c_changedprice='Y',"
					+ " c_changedpriceby=? , c_changedpriceat=current_timestamp() where c_id = ? ");
			pst.setDouble(1, newPrice);
			pst.setString(2, userId);
			pst.setInt(3, caseId);
			pst.executeUpdate();
		}catch (Exception e) {
			throw e;
		}finally {
			try {pst.close();}catch(Exception e) {/*ignore*/}
		}
	
	}
	public void changeReceiptPriceFromRTN_WITHSHIPMENT_CHRG(Connection conn, int caseId , double newPrice, String userId)throws Exception{
		
		PreparedStatement pst = null;
		try{
			pst = conn.prepareStatement("update p_cases set c_priceb4change = c_receiptamt , c_receiptamt=?, c_changedprice='Y',"
					+ " c_changedpriceby=? , c_changedpriceat=current_timestamp() where c_id = ? ");
			pst.setDouble(1, newPrice);
			pst.setString(2, userId);
			pst.setInt(3, caseId);
			pst.executeUpdate();
		}catch (Exception e) {
			throw e;
		}finally {
			try {pst.close();}catch(Exception e) {/*ignore*/}
		}
	
	}
	/***
	 * Feqar
	 * @param conn
	 * @param caseId
	 * @param newReason
	 * @throws Exception
	 */
	public void changeReturnReasons(Connection conn, int caseId , String newReason)throws Exception{
		
		PreparedStatement pst = null;
		try{
			pst = conn.prepareStatement("update p_cases set c_rtnreason=?  where c_id = ? ");
			pst.setString(1, newReason);
			pst.setInt(2, caseId);
			pst.executeUpdate();
		}catch (Exception e) {
			throw e;
		}finally {
			try {pst.close();}catch(Exception e) {/*ignore*/}
		}
	
	}
	//calculate Shipment Profit and partner share
	/**
	 * Feqar
	 * @param conn
	 * @param caseId
	 * @throws Exception
	 */
	public void calcShipmentProfitAndPartnerShare(Connection conn, int caseId) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		double partnerShareRural = 0.0, partnerShareCenter = 0.0 ,shipmentCost = 0.0, agentShare = 0.0;
		boolean foundPickupAgent = false, rural = false, foundSharePriceInSpecialPrices = false, foundSharePriceInPartnerShare = false ;
		String companySender = "", pickupAgent="";
		
		try{
			//check pickup agent is found
			pst = conn.prepareStatement("select 1 from p_cases where c_pickupagent is not null and c_id = ? ");
			pst.setInt(1, caseId);
			rs = pst.executeQuery();
			if(rs.next())
				foundPickupAgent = true;
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
			
			if(foundPickupAgent) {
				pst = conn.prepareStatement("select sp_price_share, sp_rural_share from kb_special_prices join p_cases on (sp_custid = c_custid and sp_statecode = c_rcv_state) where c_id = ?");
				pst.setInt(1, caseId);
				rs = pst.executeQuery();
				if(rs.next()) {
					partnerShareRural   = rs.getDouble("sp_rural_share");
					partnerShareCenter  = rs.getDouble("sp_price_share");
					if(partnerShareCenter > 0 || partnerShareRural>0)
						foundSharePriceInSpecialPrices = true;
				}
				
				
				try {rs.close();}catch(Exception e) {/*ignore*/}
				try {pst.close();}catch(Exception e) {/*ignore*/}
				pst = conn.prepareStatement("select c_company_sender, c_pickupagent,c_shipment_cost,c_agentshare, c_rural from p_cases where c_id = ?");
				pst.setInt(1, caseId);
				rs = pst.executeQuery();
				if(rs.next()) {
					shipmentCost 		= rs.getDouble("c_shipment_cost");
					agentShare          = rs.getDouble("c_agentshare");
					companySender 		= rs.getString("c_company_sender");
					pickupAgent          = rs.getString("c_pickupagent");
					if(rs.getString("c_rural").equalsIgnoreCase("Y"))
						rural = true;
				}
				try {rs.close();}catch(Exception e) {/*ignore*/}
				try {pst.close();}catch(Exception e) {/*ignore*/}
				if(!foundSharePriceInSpecialPrices) {
					pst = conn.prepareStatement("select ps_share_center, ps_share_rural from kbpartner_share where ps_userid=? and ps_compid=?");
					pst.setString(1, pickupAgent);
					pst.setString(2, companySender);
					rs = pst.executeQuery();
					if(rs.next()) {
						partnerShareRural   = rs.getDouble("ps_share_rural");
						partnerShareCenter  = rs.getDouble("ps_share_center");
						foundSharePriceInPartnerShare = true;
					}
					
					try {rs.close();}catch(Exception e) {/*ignore*/}
					try {pst.close();}catch(Exception e) {/*ignore*/}
				}
				/*System.out.println("shipmentCost="+shipmentCost+" agentShare="+agentShare+" partnerShareRural="+partnerShareRural+" partnerShareCenter="+partnerShareCenter);
				System.out.println("rural="+rural+" foundSharePriceInSpecialPrices="+foundSharePriceInSpecialPrices);*/
				pst = conn.prepareStatement("update p_cases set c_shipmentprofit=?, c_partnershare=? where c_id=?");
				if(foundSharePriceInSpecialPrices&&rural) {
					pst.setDouble(1, (shipmentCost-agentShare)-partnerShareRural);
				}else if(foundSharePriceInSpecialPrices&&!rural) {
						pst.setDouble(1, (shipmentCost-agentShare)-partnerShareCenter);
					}
				else
					pst.setDouble(1, (shipmentCost-agentShare));						 //shipment profit
				if(rural) {
					if(foundSharePriceInSpecialPrices)
						pst.setDouble(2, partnerShareRural);
					else
						pst.setDouble(2, ((shipmentCost-agentShare)*(partnerShareRural/100)));
				}else { if(!rural) {
					if(foundSharePriceInSpecialPrices)
						pst.setDouble(2, partnerShareCenter);
					else
						pst.setDouble(2, ((shipmentCost-agentShare)*(partnerShareCenter/100)));
					    }
				}
				pst.setInt(3, caseId);
				pst.executeUpdate();
				
			}
			// pickup agent not found or share price not found just update shipment profit
			if(!foundSharePriceInSpecialPrices&&!foundSharePriceInPartnerShare || !foundPickupAgent) {
				try {rs.close();}catch(Exception e) {/*ignore*/}
				try {pst.close();}catch(Exception e) {/*ignore*/}
				pst = conn.prepareStatement("update p_cases set c_shipmentprofit=(c_shipment_cost - c_agentshare), c_partnershare=? where c_id=?");
				pst.setInt(1, caseId);
				pst.setDouble(2, 0);
				pst.executeUpdate();
			}
		}catch (Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
		}
		
	}
	/**
	 * Feqar
	 * @param conn
	 * @param userId
	 * @throws Exception
	 */
	public void ChangerProfitAndPartnerShare(Connection conn,String userId) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Utilities  ut = new Utilities();
		try{
			if(userId.isEmpty())
				throw new Exception("In Utilities : ChangerProfitAndPartnerShare user ID is Empty please contact softica.");
			pst = conn.prepareStatement("select c_id from p_cases where c_pickupagent = ? and c_settled !='FULL'");
			pst.setString(1, userId);
			rs = pst.executeQuery();
			while(rs.next()) {				
				ut.calcShipmentProfitAndPartnerShare(conn, rs.getInt("c_id"));
			}
		}catch (Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
		}
		
	}
	public void changerAgentShipmentShareBackDated(Connection conn , String userId) throws Exception {
		PreparedStatement pst = null, pstUpdateAgentShare = null ;
		ResultSet rs = null;
		Utilities  ut = new Utilities();
		boolean rural = false;
		double agentShare = 0;
		try{
			//calcAgentShipmentChargesShare(Connection conn,int compId, String destState, String districtCode , boolean rural,  String agentId )
			pst = conn.prepareStatement("select c_id, c_company_sender, c_rcv_state, c_rcv_district, c_rural from p_cases where "
					+ " c_assignedagent=? and c_agentsharesettled='NO'");
			pstUpdateAgentShare = conn.prepareStatement("update p_cases set c_agentshare=? where c_id=?");
			
			pst.setString(1, userId);
			rs = pst.executeQuery();
			while(rs.next()) {
				if(rs.getString("c_rural").equalsIgnoreCase("Y"))
					rural = true;
				else
					rural = false;
				agentShare = ut.calcAgentShipmentChargesShare(conn, Integer.parseInt(rs.getString("c_company_sender")), rs.getString("c_rcv_state"),  rs.getString("c_rcv_district"), rural, userId);
				pstUpdateAgentShare.setDouble(1, agentShare);
				pstUpdateAgentShare.setInt(2, rs.getInt("c_id"));
				pstUpdateAgentShare.executeUpdate();
				pstUpdateAgentShare.clearParameters();
				ut.calcShipmentProfitAndPartnerShare(conn,  rs.getInt("c_id"));
			}
			
		}catch (Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
			try {pstUpdateAgentShare.close();}catch(Exception e) {/*ignore*/}
		}
	}
		public double calcPartnerShareProfit(Connection conn, int companyPaymentId ) throws Exception {
			PreparedStatement pst = null;
			ResultSet rs = null;
			double profit = 0;
			try {
				pst = conn.prepareStatement("select sum(c_partnershare) from p_cases where c_company_senderpmtid =?");
				pst.setInt(1, companyPaymentId);
				rs = pst.executeQuery();
				if(rs.next())
					profit = rs.getDouble(1);
				
			}catch (Exception e) {
				throw e;
			}finally {
				try {rs.close();}catch(Exception e) {/*ignore*/}
				try {pst.close();}catch(Exception e) {/*ignore*/}
		}
			return profit;
		}
		
		public double getshipmentCostFromCaseId(Connection conn, int caseId) throws Exception {
			double shipmentCost = 0;
			PreparedStatement pst = null;
			ResultSet rs = null;
			try {
				pst = conn.prepareStatement("select c_shipment_cost from p_cases where c_id=?");
				pst.setInt(1, caseId);
				rs = pst.executeQuery();
				if(rs.next())
					shipmentCost = rs.getDouble(1);
				
			}catch (Exception e) {
				throw e;
			}finally {
				try {rs.close();}catch(Exception e) {/*ignore*/}
				try {pst.close();}catch(Exception e) {/*ignore*/}
		}
			return shipmentCost;
		}
		
		
	/**
	 * Nafie 
	 */

		public String getOriginationSystemCodeOfCases (Connection conn, String caseid)throws Exception{
			PreparedStatement pst = null;
			ResultSet rs = null;
			String originatingSystemCode = "";
			try {
				pst = conn.prepareStatement("select c_originatingsystem from p_cases where c_id=?");
				pst.setString(1, caseid);
				rs = pst.executeQuery();
				if(rs.next()) {
					if (rs.getString("c_originatingsystem") !=null && !rs.getString("c_originatingsystem").trim().isEmpty()) {
						originatingSystemCode = rs.getString("c_originatingsystem");
					}
				}
			}catch(Exception e) {
				e.printStackTrace();
			}finally {
				try {rs.close();}catch(Exception e) {}
				try {pst.close();}catch(Exception e) {}
			}
			return originatingSystemCode;
		}
		
		/**
		 * Nafie
		 * 
		 */
		public String getReceiptNoOri (Connection conn, String caseid)throws Exception{
			PreparedStatement pst = null;
			ResultSet rs = null;
			String custReceiptNoOri = "";
			try {
				pst = conn.prepareStatement("select c_custreceiptnoori from p_cases where c_id=?");
				pst.setString(1, caseid);
				rs = pst.executeQuery();
				if(rs.next()) {
					custReceiptNoOri = rs.getString("c_custreceiptnoori");
				}
			}catch(Exception e) {
				e.printStackTrace();
			}finally {
				try {rs.close();}catch(Exception e) {}
				try {pst.close();}catch(Exception e) {}
			}
			return custReceiptNoOri;
		}
		
		/**
		 * Nafie
		 */
		public CaseInformation getSingleReceiptInfoInQueue (Connection conn, String c_custreceiptnoori, String stage, String step) throws Exception {
			PreparedStatement pst = null;
			ResultSet rs = null;
			CaseInformation caseInformation = new CaseInformation();
			try {
				pst = conn.prepareStatement("select  q_id, q_caseid, us_name, q_step,  "
						+ " c_name , c_receiptamt , concat(st_name_ar,' - ' ,c_rcv_addr_rmk)  as address ,c_rcv_addr_rmk,"
						+ " c_rcv_name, c_rcv_hp, c_qty, date(c_createddt) as c_createddt, c_rural"
						+ " from p_cases "
						+ " join p_queue on q_caseid = c_id and q_status ='ACTV'  and q_stage=? and q_step=? "
						+ " join kbcustomers on kbcustomers.c_id = c_custid"
						+ " left join kbusers on us_id = c_assignedagent "
						+ " left join  kbstate on (c_rcv_state = st_code)"
						+ " where c_custreceiptnoori=?");
				pst.setString(1, stage);
				pst.setString(2, step);
				pst.setString(3, c_custreceiptnoori);
				
				rs = pst.executeQuery();
				if (rs.next()) {
					caseInformation.setStepCode(rs.getString("q_step"));
					caseInformation.setQid(rs.getInt("q_id"));
					caseInformation.setCaseid(rs.getInt("q_caseid"));
					caseInformation.setCustName(rs.getString("c_name"));
					caseInformation.setName(rs.getString("c_rcv_name"));
					caseInformation.setHp(rs.getString("c_rcv_hp"));
					caseInformation.setQty(rs.getInt("c_qty"));
					caseInformation.setCreateddt(rs.getString("c_createddt"));
					caseInformation.setReceiptAmt(rs.getDouble("c_receiptamt"));
					caseInformation.setLocationDetails(rs.getString("address"));
					caseInformation.setAssignedAgentName(rs.getString("us_name"));
					caseInformation.setRural(rs.getString("c_rural"));
					System.out.println(caseInformation.getLocationDetails()+", "+rs.getString("c_rcv_addr_rmk"));
				}
			}catch(Exception e) {
				e.printStackTrace();
				throw e;
			}finally {
				try {rs.close();}catch(Exception e) {}
				try {pst.close();}catch(Exception e) {}
			}
			return caseInformation;
		}
}
