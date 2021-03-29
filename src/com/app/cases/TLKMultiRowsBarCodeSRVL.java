package com.app.cases;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class TLKMultiRowsBarCodeSRVL
 */
@WebServlet("/TLKMultiRowsBarCodeSRVL")
public class TLKMultiRowsBarCodeSRVL extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TLKMultiRowsBarCodeSRVL() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	NewCasesBarCode nc = new NewCasesBarCode();
    	boolean errorFlag = false;
    	String errorMsg = "";
		try {
			response.getWriter().append(nc.getRCVDetailsRow(Integer.parseInt(request.getParameter("loadRcvRow")),request.getParameter("barCodeRcpNo")));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			errorFlag = true;
			errorMsg = e.getMessage();
		}
		if (errorFlag) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().append(errorMsg);
		}else
			response.setStatus(HttpServletResponse.SC_OK);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
