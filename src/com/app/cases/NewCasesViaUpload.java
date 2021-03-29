package com.app.cases;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.app.bussframework.FlowUtils;
import com.app.core.CoreMgr;
import com.app.core.ExcelParser;
import com.app.core.smartyLogAndErrorHandling;
import com.app.db.mysql;
import com.app.util.Utilities;

public class NewCasesViaUpload extends CoreMgr{
	private boolean error;
	private String errorMsg;
	private String doneMsg;
	private boolean done;
	public NewCasesViaUpload() {
		MainSql = "select  *  from p_excelbatch order by 1 desc";
		
		canNew = true;
		canDelete = true;
		mainTable = "p_cases";
		keyCol= "eb_id";
		
		userDefinedNewColsHtmlType.put("eb_filename", "IMAGE");
		UserDefindNewFormEnctype = "enctype='multipart/form-data'";
		
		//userDefinedNewCols.add("eb_sendercompany");
		userDefinedNewCols.add("eb_shipmentdate");
		userDefinedNewCols.add("eb_filename");
		
		userDefinedColsMustFill.add("eb_sendercompany");
		userDefinedColsMustFill.add("eb_shipmentdate");
		userDefinedColsMustFill.add("eb_filename");
		
		userDefinedColLabel.put("eb_sendercompany", "الشركة المرسلة");
		userDefinedColLabel.put("eb_shipmentdate", "تاريخ الوجبة");
		userDefinedColLabel.put("eb_filename", "ملف الأكسل");
		userDefinedColLabel.put("eb_filenamebeforechange", "ملف الأكسل");
		userDefinedColLabel.put("eb_uploadedby", "تم الرفع عن طريق");
		
		userDefinedGridCols.add("eb_sendercompany");
		userDefinedGridCols.add("eb_shipmentdate");
		userDefinedGridCols.add("eb_filenamebeforechange");
		userDefinedGridCols.add("eb_uploadedby");
		
		userDefinedLookups.put("eb_sendercompany", "select comp_id, comp_name from kbcompanies");
		myhtmlmgr.refreshPageOnDelete = true;
		
	}
	
	@Override
	public String doDelete(HttpServletRequest rqs){
		String Msg ="";
		PreparedStatement pst = null;
		String keyVal= rqs.getParameter(keyCol);
		String userid = replaceVarsinString(" {useridlogin} ", arrayGlobals).trim();
		Connection conn = null;
		try {
			conn = mysql.getConn();
			//bakcup first
			pst = conn.prepareStatement("insert into p_cases_deleted select p_cases.* , ?, now() from p_cases where c_excelnumber = ? and c_settled = 'NO'");
			pst.setString(1,userid );
			pst.setString(2,keyVal );
			pst.executeUpdate();
			try {pst.close();}catch(Exception e) {}
			//free the receipt
			pst = conn.prepareStatement("update p_books_rcp set  br_custid =0, br_groupid=0,br_cid=0 where br_cid =? ");
			pst.setString(1, keyVal);
			pst.executeUpdate();
			try {pst.close();}catch(Exception e) {}
			
			//delete the excel
			pst = conn.prepareStatement("delete from p_excelbatch where eb_id=? and eb_id not in "
					+ " (select c_excelnumber from p_cases where c_excelnumber=? and c_settled='NO') ");
			pst.setString(1, keyVal);
			pst.setString(2, keyVal);
			pst.executeUpdate();
			try {pst.close();}catch(Exception e) {}
			
			pst = conn.prepareStatement("delete from p_cases where c_excelnumber = ? and c_settled='NO'");
			pst.setString(1, keyVal);
			pst.executeUpdate();
			conn.commit();
		}catch (Exception e) {
			try{conn.rollback();}catch (Exception eRollBack){eRollBack.printStackTrace();}
			e.printStackTrace();
		    Msg = "Error";
		    deleteErrorFlag = true;
		    
		}finally {
			try {pst.close();}catch(Exception e) {}
			try {conn.close();}catch(Exception e) {}
		}
		return Msg;
	}
	
	@Override 
	public String doInsert (HttpServletRequest rqs, boolean autoCommit ) {
		String msg = "";
		String upLocation = rqs.getServletContext().getInitParameter("mashaweer.excel.upload.location")+"/";
		FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory); 
        List<FileItem> items = null;
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        int batchid = 0, noOfRecsImported=0;
        ExcelParser ep = new ExcelParser();
        String userid = replaceVarsinString(" {useridlogin} ", arrayGlobals).trim();
        try {
        	int senderCommpany = 0;
        	conn = mysql.getConn();
			items = upload.parseRequest(rqs);
		
			Iterator<FileItem> iter = items.iterator();
			while (iter.hasNext()) {
				FileItem fileItem = iter.next();
				if (fileItem.isFormField()) {
					inputMap_ori.put(fileItem.getFieldName(), new String []{fileItem.getString("UTF-8")});
				} else {
					inputFilesMap.put(fileItem.getFieldName(),fileItem);
				}   	
			}
			String fileName = uploadFile (conn, inputFilesMap.get("eb_filename"),upLocation, userid,batchid );
			if (inputMap_ori.get("eb_sendercompany") !=null && inputMap_ori.get("eb_sendercompany")[0] !=null)
				senderCommpany = Integer.parseInt(inputMap_ori.get("eb_sendercompany")[0]);
			
			pst = conn.prepareStatement("insert into p_excelbatch(eb_uploadedby, eb_sendercompany, eb_filename, eb_shipmentdate, eb_filenamebeforechange) "
					+ " values(?, ? ,? , ? , ?)", Statement.RETURN_GENERATED_KEYS);
			pst.setString(1, userid);
			pst.setInt(2, senderCommpany);
			pst.setString(3, fileName);
			pst.setString(4, inputMap_ori.get("eb_shipmentdate")[0]);
			pst.setString(5,  inputFilesMap.get("eb_filename").getName());
			pst.executeUpdate();
			rs = pst.getGeneratedKeys();
			if (rs.next())
				batchid = rs.getInt(1);
			
		
			LinkedHashMap <Integer , LinkedHashMap<Integer,String>> excelData= ep.readBooksFromExcelFile(upLocation+""+fileName, 0);

			noOfRecsImported = importCases(conn, batchid , excelData , userid,inputMap_ori.get("eb_shipmentdate")[0],"BAS"  );
			if(noOfRecsImported>0)
				conn.commit();
			else {
				try {conn.rollback();}catch(Exception eRoll) {/**/}
				msg = "لم يتم إدخال إي شحنة";
			}
			
			msg = doneMsg;
        }catch(Exception e) {
        	e.printStackTrace();
        	msg = "Error "+e.getMessage();
        	try {conn.rollback();}catch(Exception eRoll) {/**/}
        }finally {
        	try{rs.close();}catch(Exception e){/*IGNORE*/}
			try{pst.close();}catch(Exception e){/*IGNORE*/}
			try{conn.close();}catch(Exception e){/*IGNORE*/}
        }
		return msg;
		
	}
	
	public String  uploadFile(Connection conn, FileItem myFile , String upLocation, String userid, int batchId) throws Exception{
		Utilities ut = new Utilities();
		String uploadedFileName ="";
		if (myFile.getName().endsWith("xls") || myFile.getName().endsWith("xlsx")){
			;
		}else{
			setError(true);
			throw new Exception (" this is not Excel File");
		}
		if (!isError()){
			
			InputStream is = myFile.getInputStream();
			try {
				String uploadName = new String( myFile.getName().getBytes(), "utf-8" );
				uploadedFileName = ut.writeToFileServer (is, uploadName,upLocation);
				
			} catch (IOException e) {
				
				e.printStackTrace();
				setError(true);
				throw new Exception(e.getMessage());
			}catch (Exception e) {
				setError(true);
				throw new Exception(e.getMessage());
			}finally {
				try {is.close();}catch(Exception e) {/*ignore*/}
			}
		}
		setDone(true);
		return uploadedFileName;
	}
	
	
	private int importCases(Connection conn, int batchid ,
							LinkedHashMap <Integer , LinkedHashMap<Integer,String>> excelData,
							String userid, String creationDate, String destState)throws Exception{
		PreparedStatement pst = null,pstCustomerId = null, pstCreateNewCustomer = null, pstGetDistrict=null, pstPickupAgent = null;
		ResultSet rs = null, rsPickUpAgent = null;
		int recNo = 0,excelRowNo=1, custId = 0, caseId = 0;
		String districtCode = "";
		boolean custFound = false, districtFound=false, rural = false;
		Utilities ut = new Utilities();
		FlowUtils fu = new FlowUtils();
		double shipmentCharges = 0, agentShareAmt= 0;
		int pickUpAgent = 0;
		int senderCompany = 0;
		try{
			pstPickupAgent = conn.prepareStatement("select comp_pickupagent from kbcompanies where comp_id = ?");
			pstCustomerId = conn.prepareStatement("select c_id from kbcustomers where c_name=?");
			pstGetDistrict = conn.prepareStatement("select cdi_code from kbcity_district where cdi_name=?");
			pstCreateNewCustomer = conn.prepareStatement("insert into kbcustomers (c_name, c_createdby)"
					+ "values(?, ?)", Statement.RETURN_GENERATED_KEYS);
			
			pst = conn.prepareStatement("insert into p_cases "
					+ " (c_createdby  , c_rcv_name	 		, c_rcv_hp	  		, c_rcv_state		, c_rural		 , "
					+ "  c_rmk 		  , c_qty	  			, c_receiptamt      , c_shipment_cost   , c_fragile  	 , "
					+ "  c_branchcode , c_custreceiptnoori	, c_agentshare		, c_rcv_district	, c_custid		 ,  "
					+ "	 c_excelnumber, c_company_sender    , c_createddt	 	, c_pickupagent )"
			+ " values  (?			    , ?			     	, ?		 			, ?					, ?				 , "
			+ "			 ?			    , ?			     	, ?		 			, ?					, ?				 , "
			+ "			 ?			    , ?              	, ?		 			, ?					, ?				 , "
			+ "			 ?				, ?		 		 	, ?	 				, ?)",
			Statement.RETURN_GENERATED_KEYS);
			/* Here we start the loop for question in excel data */
			for (int key : excelData.keySet()){
				excelRowNo++;
				custFound = false;
				custId = 0;
				caseId = 0;
				districtFound=false;
				rural = false;
				districtCode = "";
				
				if (excelData.get(key).get(9)!=null) {
					senderCompany = Integer.parseInt(excelData.get(key).get(12));
					pstPickupAgent.setInt(1,senderCompany);
					rsPickUpAgent = pstPickupAgent.executeQuery();
					if (rsPickUpAgent.next()) {
						pickUpAgent = rsPickUpAgent.getInt("comp_pickupagent");
					}
					pstPickupAgent.clearParameters();
					try{rsPickUpAgent.close();}catch(Exception e){/*IGNORE*/}
					
					pstGetDistrict.setString(1,excelData.get(key).get(7) );
					rs = pstGetDistrict.executeQuery();
					if (rs.next()) {
						districtFound = true;
						districtCode = rs.getString("cdi_code");
					}
					pstGetDistrict.clearParameters();
					try {rs.close();}catch(Exception e) {/*ignore*/}
					if (districtFound) {
						pstCustomerId.setString(1, excelData.get(key).get(10));
						rs = pstCustomerId.executeQuery();
						if (rs.next()) {
							if (rs.getInt("c_id") > 0) {
								custFound = true;
								custId = rs.getInt("c_id");
							}
						}
						pstCustomerId.clearParameters();
						try {rs.close();}catch(Exception e) {/*ignore*/}
						
						if (!custFound) {
							pstCreateNewCustomer.setString(1, excelData.get(key).get(10));
							pstCreateNewCustomer.setString(2, "EXCEL-"+userid);
							pstCreateNewCustomer.executeUpdate();
							rs = pstCreateNewCustomer.getGeneratedKeys();
							rs.next();
							custId = rs.getInt(1);
							pstCreateNewCustomer.clearParameters();
							try {rs.close();}catch(Exception e) {/*ignore*/}
						}
						try {rs.close();}catch(Exception e) {/*ignore*/}
						
						if (excelData.get(key).get(11).equalsIgnoreCase("Y"))
							rural = true;
						shipmentCharges = ut.calcShipmentChargesBasedOnDestCity(conn, destState,rural,custId,senderCompany);
						agentShareAmt   = ut.calcAgentShipmentChargesShare(conn,senderCompany,destState,districtCode , rural , "" );
						pst.setString(1,userid);
						pst.setString(2,excelData.get(key).get(5));
						pst.setString(3,excelData.get(key).get(4));
						pst.setString(4, destState);
						pst.setString(5,excelData.get(key).get(11));
						pst.setString(6,excelData.get(key).get(3));
						pst.setString(7,excelData.get(key).get(2));
						pst.setString(8,excelData.get(key).get(8));
						pst.setDouble(9,shipmentCharges);
						pst.setString(10,excelData.get(key).get(1));
						pst.setString(11,"BAS");
						pst.setString(12,excelData.get(key).get(9));
						pst.setDouble(13,agentShareAmt);
						pst.setString(14, districtCode);
						pst.setInt(15, custId);
						pst.setInt(16, batchid);
						pst.setInt(17, senderCompany);
						pst.setString(18, creationDate);
						pst.setInt(19, pickUpAgent);
						pst.executeUpdate();
						rs = pst.getGeneratedKeys();
						if (rs.next())
							caseId = rs.getInt(1);
						pst.clearParameters();
						try {rs.close();}catch(Exception e) {/*ignore*/}
						if (caseId>0) {
							fu.createNewCaseInQueue(conn,caseId, "BAS");
						}
						recNo++;
					}else {
						doneMsg +="السطر رقم "+excelRowNo+" في ملف الاكسل لا يحتوي على اسم منطقه صحيح"+"</br>";
					}
				}
			}
			doneMsg +="تم ادخال "+recNo+" شحنه</br>";
		}catch(Exception e){
			e.printStackTrace();
			//try{conn.rollback();}catch(Exception eRoll){/* IGNORE*/}
			throw new Exception ("خطا في السطر رقم "+excelRowNo+", "+e.getMessage());
		}finally{
			try{rs.close();}catch(Exception e){/*IGNORE*/}
			try{rsPickUpAgent.close();}catch(Exception e){/*IGNORE*/}
			try{pst.close();}catch(Exception e){/*IGNORE*/}
			try{pstCustomerId.close();}catch(Exception e){/*IGNORE*/}
			try{pstCreateNewCustomer.close();}catch(Exception e){/*IGNORE*/}
			try{pstGetDistrict.close();}catch(Exception e){/*IGNORE*/}
			try{pstPickupAgent.close();}catch(Exception e){/*IGNORE*/}
		}
		return recNo;
	}


	public boolean isError() {
		return error;
	}


	public void setError(boolean error) {
		this.error = error;
	}


	public boolean isDone() {
		return done;
	}


	public void setDone(boolean done) {
		this.done = done;
	}
}
