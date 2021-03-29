package com.app.bussframework;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.app.core.CoreMgr;

public class AgentShipmentsPopUp extends CoreMgr {
	public AgentShipmentsPopUp() {
		MainSql= "select q_id, c_name, p_cases.c_id, c_rcv_name, c_rcv_hp,c_rcv_city, concat(ct_name_ar,' - ',ifnull(c_rcv_addr_rmk,'')) as addr , "
				+ " c_qty, c_rmk, c_weight, c_shipment_cost, c_receiptamt, c_bringitemsback, c_fragile , c_sendmoney, c_custreceiptnoori"
				+ " from p_cases  "
				+ " join p_queue on (q_caseid = p_cases.c_id and q_stage ='init' and q_step='prt_manifest' and q_status = 'ACTV')"
				+ " left join kbcustomers on kbcustomers.c_id = c_custid "
				+ " left join kbcity on ct_code = c_rcv_city"
				+ " where c_assignedagent={c_assignedagent} and c_branchcode='{userstorecode}' order by c_rcv_city ";
		
		
		userDefinedGridCols.add("c_name");
		userDefinedGridCols.add("c_custreceiptnoori");
		userDefinedGridCols.add("p_cases.c_id");
		userDefinedGridCols.add("c_rcv_name");
		userDefinedGridCols.add("addr");
		userDefinedGridCols.add("c_rcv_hp");
		userDefinedGridCols.add("c_receiptamt");
		userDefinedGridCols.add("c_qty");
		
		mainTable = "p_queue";
		keyCol = "q_id";
		canDelete = true;
		
	}

	@Override
	public String doDelete(HttpServletRequest rqs) {
		String keyVal = rqs.getParameter(keyCol);
		PreparedStatement pst = null;
		ResultSet rs = null;
		FlowUtils fu = new FlowUtils();
		String userId = replaceVarsinString(" {useridlogin} ", arrayGlobals).trim();
		try {
			//CHNGE_AGENT
			pst = conn.prepareStatement("update p_queue set q_action=?, q_action_takenby=? where q_id=?");
			pst.setString(1, "CHNGE_AGENT");
			pst.setString(2, userId);
			pst.setString(3, keyVal);
			pst.executeUpdate();
			
			fu.MoveDecisionStepNext(conn, Integer.parseInt(keyVal));
			
			conn.commit();

		} catch (Exception e) {
			try {conn.rollback();} catch (Exception eRollBack) {/*ignore*/}
			logErrorMsg = "";
			e.printStackTrace();
		} finally {
			try {pst.close();} catch (Exception e) {}
		}

		return "";
	}// end of doDelete*/
}
