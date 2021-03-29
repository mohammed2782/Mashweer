package com.app.cases;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Servlet implementation class PrintDriverManifest
 */
@WebServlet("/TLKprint/PrintDriverManifestExcel")
public class PrintDriverManifestExcelSRVL extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PrintDriverManifestExcelSRVL() {
        super();
        // TODO Auto-generated constructor stub
    }
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		BufferedInputStream buf = null;
		ServletOutputStream stream = null;
		try {
		String driverId =request.getParameter("driverid");
		String stgCode =request.getParameter("stg_code");
		String stpCode =request.getParameter("stp_code");
		String storeCode =request.getParameter("storecode");
		String dt ="ALL";
		if (request.getParameter("stdate")!=null && !request.getParameter("stdate").trim().isEmpty())
			dt = request.getParameter("stdate");
		
		if (storeCode ==null)
			storeCode = "BGHD_1";
		String docType = "xls";
		DriverManifestExcel driverManifestExcel =  new DriverManifestExcel();
		String contentType = "application/vnd.ms-excel";
		response.setContentType(contentType);
		
		String fileName = "manifest_"+driverId+"_"+storeCode+"_.xls";
		response.setHeader("Content-Disposition","attachment;filename="+fileName);
		
		String ctxPath = this.getClass().getResource("/").getPath();
		ctxPath = ctxPath.replaceAll("WEB-INF/classes/", "");
		ctxPath = ctxPath.replaceAll("%20", " ");
		System.out.println(ctxPath); 
		
		File file = driverManifestExcel.getManifestTable( driverId, stgCode, stpCode,  storeCode, dt , ctxPath);
		response.setContentLength((int) file.length());
		FileInputStream fileIn = new FileInputStream(file);
		
	    buf = new BufferedInputStream(fileIn);
	    int readBytes = 0;
	   stream = response.getOutputStream();
	   
	    while ((readBytes = buf.read()) != -1)// while there is still data in the buffer.loops byte by byte
	        stream.write(readBytes);//write it.
	    } catch (IOException ioe) {
	    	ioe.printStackTrace();
	      throw new ServletException(ioe.getMessage());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			
			  if (stream != null){
				  stream.close();
			  }
		      if (buf != null){    
		    	  buf.close();
		      }
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
