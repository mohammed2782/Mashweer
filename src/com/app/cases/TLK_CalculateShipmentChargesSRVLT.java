package com.app.cases;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.app.db.mysql;
import com.app.util.Utilities;

/**
 * Servlet implementation class CalculateShipmentChargesSRVLT
 */
@WebServlet("/TLK_CalculateShipmentChargesSRVLT")
public class TLK_CalculateShipmentChargesSRVLT extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TLK_CalculateShipmentChargesSRVLT() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Connection conn = null;
		Utilities ut = new Utilities();
		
		String destState = request.getParameter("destState");
		int senderCompany = Integer.parseInt(request.getParameter("senderCompany"));
		//Double weight = Double.parseDouble(request.getParameter("weight"));
		
		String custName = "";
		int custId = 0;
		boolean ruralArea = false;
		if (request.getParameter("rural")!=null && request.getParameter("rural").equalsIgnoreCase("Y"))
			ruralArea = true;
		double shipmentCharges = 0.0;
		try {
			
			conn = mysql.getConn();
			if (request.getParameter("custName")!=null && !request.getParameter("custName").trim().equalsIgnoreCase("")) {
				custName = request.getParameter("custName");
				shipmentCharges  = ut.calcShipmentChargesBasedOnDestCity(conn, destState,  ruralArea, custName,senderCompany); 
			}else{
				if (request.getParameter("custid")!=null && !request.getParameter("custid").trim().equalsIgnoreCase("")) {
					custId = Integer.parseInt(request.getParameter("custid"));
				}
				shipmentCharges  = ut.calcShipmentChargesBasedOnDestCity(conn, destState,  ruralArea, custId,senderCompany); 
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {conn.close();}catch(Exception e) {/**/}
		}
		PrintWriter out = response.getWriter();
		out.write(shipmentCharges + "");
		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
