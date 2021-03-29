package com.app.cases;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class TLKPrintRtnWithRcvAgentSRVL
 */
@WebServlet("/TLKPrintRtnWithRcvAgentSRVL")
public class TLKPrintRtnWithRcvAgentSRVL extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TLKPrintRtnWithRcvAgentSRVL() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		BufferedInputStream buf = null;
		ServletOutputStream stream = null;
		try{
			String rcvAgent =request.getParameter("c_pickupagent");
			String stgCode = "cncl";//request.getParameter("stg_code");
			String stpCode ="RTN_WITHRCV_AGENT";//request.getParameter("stp_code");
			String storeCode = null;
			
			HashMap<String,String> filterMap = new HashMap<String,String>();
			for (String col : request.getParameterMap().keySet()) {
				filterMap.put(col, request.getParameter(col));
			}
			
			
			if (storeCode ==null)
				storeCode = "BAS";
			String docType = "pdf";
			RtnWithRcvAgentManifestPDF driverManifestPDF =  new RtnWithRcvAgentManifestPDF();
			String contentType = "application/pdf";
			
			response.setContentType(contentType);
			
			String fileName = "rtn_with_rcv_Agent_manifest_"+rcvAgent+"_"+storeCode+"_.pdf";
			response.setHeader("Content-Disposition","attachment;filename="+fileName);
			
			String ctxPath = this.getClass().getResource("/").getPath();
			ctxPath = ctxPath.replaceAll("WEB-INF/classes/", "");
			ctxPath = ctxPath.replaceAll("%20", " ");
			System.out.println(ctxPath);
			File file = getFileExported (driverManifestPDF , rcvAgent, stgCode, stpCode,  storeCode, filterMap  , ctxPath);
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
			} finally {
				
			  if (stream != null){
				  stream.close();
			  }
		      if (buf != null){    
		    	  buf.close();
		      }
		   }
		
	}
	
		public File getFileExported(RtnWithRcvAgentManifestPDF pdfRs , String driverId,String stgCode, String stpCode, String storeCode, HashMap<String,String> filterMap, String ctxPath){
		// move the file to be input stream.
		
		pdfRs.prepareDocument (driverId,stgCode, stpCode,storeCode, filterMap,  ctxPath);
		// get the file location, and prepare file object refers to it.
		File file = null;
		try{ 
			file = new File(pdfRs.getDocPath());}
		catch (Exception e){e.printStackTrace();}
		return file;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
