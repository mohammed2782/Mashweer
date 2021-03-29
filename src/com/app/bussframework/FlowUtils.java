package com.app.bussframework;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import com.app.db.mysql;
import com.mysql.jdbc.Statement;

public class FlowUtils {
	
	/**
	 * 1- create a new queue in p_queue for the case
	 * 
	 * @return q_id 
	 */
	public int createNewCaseInQueue (Connection conn,int caseid , String branch) throws Exception{
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		String stageCode="", stepCode="" , rankCode = "", q_code="";
		int qid =0;
		try {
		
			pst  = conn.prepareStatement("select stg_code,stp_code, stp_rank from kbstage join kbstep on (stg_code = stp_stgcode)"
					+ " where stg_order=1 and stp_order=1");//ge the first stage
			rs = pst.executeQuery();
			if (rs.next()) {
				stageCode = rs.getString("stg_code");
				stepCode  = rs.getString("stp_code");
				rankCode  = rs.getString("stp_rank");
			}
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
			
			q_code = stageCode+"__"+stepCode;
			
			pst  = conn.prepareStatement("insert into p_queue "
							+ "(q_code, q_caseid, q_stage, q_step, q_rank, q_branch)"
					+ "values  (?     , ?		, ?		 , ?	 , ?	 , ?)" , Statement.RETURN_GENERATED_KEYS);
			pst.setString(1, q_code);
			pst.setInt(2, caseid);
			pst.setString(3, stageCode);
			pst.setString(4, stepCode);
			pst.setString(5, rankCode);
			pst.setString(6, branch);
			pst.executeUpdate();
			rs = pst.getGeneratedKeys();
			if (rs.next())
				qid = rs.getInt(1);
		}catch(Exception e) {
			//log
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
			
		}
		return qid;
	}
	/*
	 * move next only for Decision Steps , takes the qid and move to the next step in the queue and change the branch
	 */
	public void MoveDecisionStepNext (Connection conn , int qid , HashMap<String,String> queueColsToUpdate) throws Exception{
		PreparedStatement pst1 = null;
		int newQid;
		try {
			newQid = MoveDecisionStepNext (conn , qid);
			String colToUpdate="";
			boolean firstCol = true;
			int colNo=1;
			for (String col :queueColsToUpdate.keySet()) {
				if (!firstCol)
					colToUpdate+=", ";
				colToUpdate += col+"=? ";
				firstCol = false;
			}
			String sql = "update p_queue set "+colToUpdate+" where q_id=?";
			pst1 = conn.prepareStatement(sql);
			for (String col :queueColsToUpdate.keySet()) {
				pst1.setString(colNo, queueColsToUpdate.get(col));
				colNo++;
			}
			pst1.setInt(colNo, newQid);
			//System.out.println("updating q_id=>"+newQid);
			pst1.executeUpdate();
		}catch(Exception e) {
			//log
			throw e;
		}finally {
			try {pst1.close();}catch(Exception e) {/*ignore*/}
			
		}
	}
	
	/*
	 * move next only for Decision Steps , takes the qid and move to the next step in the queue and change the branch
	 */
	public int MoveDecisionStepNext (Connection conn , int qid) throws Exception{
		PreparedStatement pst1 = null;
		ResultSet rs1 = null;
		String currStepCode, currStageCode, nextStepCode = null, nextStageCode = null, 
				currAction = null, currQ_Code = null , nextQ_Code, nextRank = null , branch=null, finalStep=null,
				prevActionTakenBy = null;
		
		int stepid = 0 , caseid = 0 , newQid=0;
		try {
			/*
			 * 1- get the current q info.
			 * 2- check the decision made
			 * 3- update the current q to CLS.
			 * 4- insert the new queue based on the decision.  
			 */
			pst1  = conn.prepareStatement("select q_action_takenby, q_branch , q_code, stp_id, q_stage, q_step , q_action , q_caseid from p_queue "
					+ " join kbstep on (q_stage= stp_stgcode and q_step = stp_code)  where q_id=?  and q_status !='CLS'");
			pst1.setInt(1, qid);
			rs1 = pst1.executeQuery();
			if (rs1.next()) {
				currQ_Code = rs1.getString("q_code");
				currStepCode = rs1.getString("q_step");
				currStageCode = rs1.getString("q_stage");
				currAction = rs1.getString("q_action");
				stepid = rs1.getInt("stp_id");
				caseid = rs1.getInt("q_caseid");
				branch = rs1.getString("q_branch");
				prevActionTakenBy = rs1.getString("q_action_takenby");
			}else {
				return 0;// nothing to do, means the queue already moved by another sesssion
			}
			try {rs1.close();}catch(Exception e) {/*ignore*/}
			try {pst1.close();}catch(Exception e) {/*ignore*/}
			
			pst1 = conn.prepareStatement("select stp_finaldestination, stpd_gotostep1 , stp_code, stp_stgcode, stp_rank "
					+ " from kbstep_decision "
					+ " join kbstep on (stpd_gotostep1 = stp_id) where stpd_deleted='N' and stpd_stpid =? and stpd_code=?");
			pst1.setInt(1, stepid);
			pst1.setString(2, currAction);
			//System.out.println("stepid==>"+stepid+", currAction=>"+currAction);
			rs1 = pst1.executeQuery();
			if (rs1.next()) {
				nextStepCode  = rs1.getString("stp_code");
				nextStageCode = rs1.getString("stp_stgcode");
				nextRank      = rs1.getString("stp_rank");
				finalStep	  = rs1.getString("stp_finaldestination");
			}	
			nextQ_Code = nextStageCode+"__"+nextStepCode;
			try {rs1.close();}catch(Exception e) {/*ignore*/}
			try {pst1.close();}catch(Exception e) {/*ignore*/}
			
			/*
			pst1 = conn.prepareStatement("update p_queue set q_status = ? where q_id=?");
			pst1.setString(1, "CLS");
			pst1.setInt(2, qid);
			pst1.executeUpdate();
			try {pst1.close();}catch(Exception e) {}
			try {rs1.close();}catch(Exception e) {}
			*/
			
			deleteQ (conn, qid);
			
			pst1  = conn.prepareStatement("insert into p_queue "
							+ "(q_code	, q_caseid			, q_stage		, q_step					, q_rank, "
							+ "	q_branch, q_previous_qcode  , q_previous_qid, q_previous_action_taken_by, q_previous_action)"
					+ "values  (?     	, ?					, ?		 		, ?	 						, ?	 , "
					+ "			?	   	, ?				  	, ?			  	, ?							, ?)",
					Statement.RETURN_GENERATED_KEYS);
			pst1.setString(1, nextQ_Code);
			pst1.setInt(2, caseid);
			pst1.setString(3, nextStageCode);
			pst1.setString(4, nextStepCode);
			pst1.setString(5, nextRank);
			pst1.setString(6, branch);
			pst1.setString(7, currQ_Code);
			pst1.setInt(8, qid);
			pst1.setString(9, prevActionTakenBy);
			pst1.setString(10, currAction);
			pst1.executeUpdate();
			rs1= pst1.getGeneratedKeys();
			if (rs1.next())
				newQid = rs1.getInt(1);
			
			try {pst1.close();}catch(Exception e) {/*ignore*/}
			try {rs1.close();}catch(Exception e) {/*ignore*/}
			if (finalStep.equalsIgnoreCase("Y")) {
				pst1 = conn.prepareStatement("update p_queue set q_status = ? where q_id=?");
				pst1.setString(1, "END");
				pst1.setInt(2, newQid);
				pst1.executeUpdate();
			}
			
		}catch(Exception e) {
			//log
			throw e;
		}finally {
			try {rs1.close();}catch(Exception e) {/*ignore*/}
			try {pst1.close();}catch(Exception e) {/*ignore*/}
			
		}
		return newQid;
	}
	
	public void  deleteQ (Connection conn, int qid)throws Exception{
		PreparedStatement pst = null;
		try {
			pst = conn.prepareStatement("insert into p_queue_hist select * from p_queue where q_id =? and q_status !='CLS'");
			pst.setInt(1, qid);
			pst.executeUpdate();
			try {pst.close();}catch(Exception e) {/*ignore*/}
			
			pst = conn.prepareStatement("delete from p_queue where q_id =? and q_status !='CLS'");
			pst.setInt(1, qid);
			pst.executeUpdate();
			
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			
			try {pst.close();}catch(Exception e) {/*ignore*/}
		}
	}
}
