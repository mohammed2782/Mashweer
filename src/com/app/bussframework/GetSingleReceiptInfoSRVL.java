package com.app.bussframework;

import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.app.cases.CaseInformation;
import com.app.db.mysql;
import com.app.util.Utilities;
import com.mysql.jdbc.authentication.MysqlClearPasswordPlugin;

/**
 * Servlet implementation class GetSingleReceiptInfoSRVL
 */
@WebServlet("/GetSingleReceiptInfoSRVL")
public class GetSingleReceiptInfoSRVL extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetSingleReceiptInfoSRVL() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String c_custreceiptnoori = request.getParameter("c_custreceiptnoori");
		String step = request.getParameter("step");
		String stage = request.getParameter("stage");
		response.setContentType("application/json;charset=UTF-8");
		response.setHeader("Cache-Control","no-cache");
		Utilities ut = new Utilities ();
		
		JSONArray array = new JSONArray();
		JSONObject dataObj = new JSONObject();
		
		Writer out = response.getWriter();
		Connection conn = null;
		CaseInformation caseInformation= new CaseInformation();
		try {
			conn = mysql.getConn();
			
				caseInformation = ut.getSingleReceiptInfoInQueue(conn, c_custreceiptnoori, stage, step);
				
				dataObj.put("q_id",caseInformation.getQid());
				//dataObj.put("stepname", caseInformation.getStepName());
				dataObj.put("stepcode", caseInformation.getStepCode());
				dataObj.put("custName",caseInformation.getCustName());
				dataObj.put("agentname",caseInformation.getAssignedAgentName());
				dataObj.put("hp", caseInformation.getHp());
				dataObj.put("receiptamt", caseInformation.getReceiptAmt());
				dataObj.put("address", caseInformation.getLocationDetails());
				dataObj.put("rcvname", caseInformation.getName());
				dataObj.put("caseid", caseInformation.getCaseid());
				dataObj.put("qty", caseInformation.getQty());
				dataObj.put("qid", caseInformation.getQid());
				dataObj.put("createddt", caseInformation.getCreateddt());
				dataObj.put("rural", caseInformation.getRural());
				array.put(0, dataObj);
				out.append(array.toString());
			out.flush();
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {conn.close();}catch(Exception e) {/**/}
			out.close();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
