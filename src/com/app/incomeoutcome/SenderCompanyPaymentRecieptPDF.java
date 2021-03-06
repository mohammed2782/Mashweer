package com.app.incomeoutcome;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.logging.Level;

import com.app.cases.CaseInformation;
import com.app.core.smartyLogAndErrorHandling;
import com.app.db.mysql;
import com.app.incomeoutcome.PickUpAgentPaymentRecieptPDF.MyFooter;
import com.app.util.Utilities;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.GrayColor;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

public class SenderCompanyPaymentRecieptPDF {
	private Font fontHeaders;
	private  String docPath;
	private String docsDir;
	private String fullFilePathName;
	private String fileName;
	public String docDir="";// the document directory
	public String docLang = "en-US";// arabic kuwait
	public String docExtension = ".pdf";
	String ctxPath = "";
	PickUpAgentPaymentBean papb;
	public DecimalFormat numFormat = new DecimalFormat("#,###,###.##");
	public static final Font FONT = new Font(FontFamily.HELVETICA, 12, Font.NORMAL, GrayColor.GRAYWHITE);
	 BaseFont baseFont = null;
	boolean newPage = true;
	 int totalShimpments=0;
	 int center = 0;
	 int rural = 0;
	class MyFooter extends PdfPageEventHelper {
        Font ffont = new Font(Font.FontFamily.UNDEFINED, 6, Font.ITALIC);
        private String imgPath;
        private Image image;
        int pageNo = 0;
       
        public void setImagePath(String imgPath){
        	this.imgPath = imgPath;
        	  try {
					 image = Image.getInstance(imgPath);
				} catch (BadElementException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				} catch (MalformedURLException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
		        image.scaleAbsolute(PageSize.A4);
		        image.setAbsolutePosition(0, 0);
		        //PdfContentByte canvas = writer.getDirectContentUnder();
		        image.scaleToFit(100,100);
        }
        
        public void onEndPage(PdfWriter writer, Document document) {
        	pageNo ++;
        	PdfContentByte cb = writer.getDirectContent();
            try {
				//cb.addImage(image ,PageSize.A4.getWidth()-400,0,0,80,0,document.top()+20 );
            	cb.addImage(image,PageSize.A4.getWidth()-520,0,0,60,40,document.top()+10);
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            Date dt = new Date();
            Phrase header = new Phrase(dt.toString(), ffont);
            Phrase footer = new Phrase("Page "+pageNo, ffont);
            ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
                    header,
                    (document.right() - document.left()) + document.leftMargin(),
                    document.top() +50, 0);
            
            ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
            		new Phrase(" Delivery on Time System (DoTS), is a system developed by Softecha, www.softecha.com", ffont),
                    (document.right() - document.left())/3  + document.leftMargin(),
                    document.bottom() - 20, 0);
        }
        public void onStartPage(PdfWriter writer, Document document) {
        	
        	newPage = true;
        }
    }
	public void prepareDocument(  int senderCompanyPmtId, String ctxPath) {
		this.ctxPath = ctxPath;
		String imgPath = ctxPath+"/smartyresources/img/logo_xsm.png";
		docsDir = ctxPath+"/"+"PDFDocs";
		fileName= "smarty_test"+docExtension;
		fullFilePathName = docsDir+"//"+fileName; 
		createDir(docsDir);
		setDocPath(fullFilePathName);
		Connection conn =null;
		Utilities ut = new Utilities();
		try {
			conn = mysql.getConn();
			papb =  ut.getSenderCompanyPaymentInfo(conn, senderCompanyPmtId);
			
			Rectangle pageSize = new Rectangle(PageSize.A4.rotate());
			//pageSize.setBackgroundColor(new BaseColor(245, 245, 245));
			Document document = new Document(pageSize, 50, 50, 85, 50);
			
		    PdfWriter pdf = null;
		    MyFooter event = new MyFooter();
		    event.setImagePath(imgPath);
		    
		    try {
		    	pdf = PdfWriter.getInstance(document, new FileOutputStream(fullFilePathName));
			} catch (DocumentException e) {
				e.printStackTrace();
			}
		    event.onStartPage(pdf, document);
		    pdf.setPageEvent(event);
		    pdf.getPageNumber();
		    document.open();		       
			try {
				//baseFont = BaseFont.createFont("../../Fonts/ARIALUNI.TTF", BaseFont.IDENTITY_H, true);//this cause jvm out of memory
				baseFont = BaseFont.createFont("../../Fonts/arial.ttf", BaseFont.IDENTITY_H, true);
			} catch (DocumentException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}catch (Exception e){
				System.out.println("at the font level");
				e.printStackTrace();
			}
			
	        Font font  = new Font(baseFont); 
	        fontHeaders  = new Font(baseFont); 
	        Font font2 = new Font(baseFont);
	        Font font3 = new Font(baseFont);
	        Font font4 = new Font(baseFont);
	        font2.setSize(12);
	        font2.setColor(new BaseColor(252, 252,252));
	        font2.setStyle(1);
	        font3.setSize(12);
	        font3.setColor(new BaseColor(252, 252,252));
	        font3.setStyle(2);
	        ColumnText ct = new ColumnText(pdf.getDirectContent());

	        ct.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
	        
	        font4.setSize(16);
	        font4.setStyle(1);
	        //font4.setColor(new BaseColor(92, 38,140));
	        font4.setColor(new BaseColor(252, 252,252));
	        Paragraph par = new Paragraph("???????? ???????? ??????????????",font4);
	        par.setAlignment(Element.ALIGN_CENTER);
	       // ct.addElement(par);
 
	        
	        Rectangle rect = new Rectangle(/*starting x point*/10, /*starting y point*/570 , /*width*/850 , /*height*/100);
	        rect.setBorder(Rectangle.BOX);
	        ct.setSimpleColumn(rect);
	        ct.go();
	        
	        float [] relativeWidths = null;
        	int noOfHeaders = 5;
	        relativeWidths = new float[noOfHeaders]; 
	       
	        relativeWidths [0] = 10; // status
	        relativeWidths [1] = 10; // qty
	        relativeWidths [2] = 30; // net amount
	        relativeWidths [3] = 30;
	        relativeWidths [4] = 10; 
	        PdfPTable table = new  PdfPTable(relativeWidths);
	        
	        PdfPTable tableShipments ;
	        table.setWidthPercentage(100);
	       
	        table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
	        PdfPCell cell;
	       
	        
	        BaseColor bc = new BaseColor(0,0,139); 
	        par = new Paragraph("???????????? ??????????????: ",font2);
	        cell = new PdfPCell(par);
 			//cell.setPaddingTop(0);
 			//cell.setPaddingBottom(5);
 			cell.setPadding(5);
 			cell.setBackgroundColor(bc);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            
            par = new Paragraph(papb.getPickUpAgentName(),font3);
	        cell = new PdfPCell(par);
	        cell.setPadding(5);
 			cell.setBackgroundColor(bc);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
 
            par = new Paragraph("",font);
	        cell = new PdfPCell(par);
 			cell.setBackgroundColor(bc);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            
            
            par = new Paragraph("?????????? ??????????: ",font2);
	        cell = new PdfPCell(par);
 			cell.setBackgroundColor(bc);
 			cell.setPadding(5);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            par = new Paragraph(papb.getPmtDate(),font3);
	        cell = new PdfPCell(par);
	        cell.setPadding(5);
 			cell.setBackgroundColor(bc);
 			cell.setPaddingBottom(5);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            
            par = new Paragraph("???????????? ????????????:",font2);
	        cell = new PdfPCell(par);
	        cell.setPadding(5);
 			cell.setBackgroundColor(bc);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            par = new Paragraph(""+numFormat.format(papb.getPmtAmt()),font3);
	        cell = new PdfPCell(par);
	        cell.setPadding(5);
 			cell.setBackgroundColor(bc);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            
            par = new Paragraph("???????? ???????? ??????????????",font4);
	        cell = new PdfPCell(par);
 			cell.setBackgroundColor(bc);
 			cell.setPadding(5);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            
            par = new Paragraph("?????????? ?????? ??????: ",font2);
	        cell = new PdfPCell(par);
	        cell.setPadding(5);
 			cell.setBackgroundColor(bc);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            par = new Paragraph(""+senderCompanyPmtId,font3);
	        cell = new PdfPCell(par);
	        cell.setPadding(5);
 			cell.setBackgroundColor(bc);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            
            
            
             
            tableShipments = getShipmentsTable(conn, papb,font);
            
            par = new Paragraph("?????? ??????????????:",font2);
	        cell = new PdfPCell(par);
	        cell.setPadding(5);
 			cell.setBackgroundColor(bc);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            
            par = new Paragraph(""+numFormat.format(totalShimpments),font3);
	        cell = new PdfPCell(par);
	        cell.setPadding(5);
 			cell.setBackgroundColor(bc);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
              
            par = new Paragraph("",font);
	        cell = new PdfPCell(par);
 			cell.setPaddingTop(5);
 			cell.setPaddingBottom(5);
 			cell.setBackgroundColor(bc);
 			cell.setVerticalAlignment(Element.ALIGN_RIGHT);
 			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            
            par = new Paragraph("",font);
	        cell = new PdfPCell(par);
 			cell.setPaddingTop(5);
 			cell.setPaddingBottom(5);
 			cell.setBackgroundColor(bc);
 			cell.setVerticalAlignment(Element.ALIGN_RIGHT);
 			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            
            par = new Paragraph("",font);
	        cell = new PdfPCell(par);
 			cell.setPaddingTop(5);
 			cell.setPaddingBottom(5);
 			cell.setBackgroundColor(bc);
 			cell.setVerticalAlignment(Element.ALIGN_RIGHT);
 			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            
            
            
            
            par = new Paragraph("?????????? ????????????",font2);
	        cell = new PdfPCell(par);
	        cell.setPadding(5);
 			cell.setBackgroundColor(bc);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            
            par = new Paragraph(""+numFormat.format(center),font3);
	        cell = new PdfPCell(par);
	        cell.setPadding(5);
 			cell.setBackgroundColor(bc);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
              
            par = new Paragraph("",font);
	        cell = new PdfPCell(par);
 			cell.setPaddingTop(5);
 			cell.setPaddingBottom(5);
 			cell.setBackgroundColor(bc);
 			cell.setVerticalAlignment(Element.ALIGN_RIGHT);
 			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            
            par = new Paragraph("",font);
	        cell = new PdfPCell(par);
 			cell.setPaddingTop(5);
 			cell.setPaddingBottom(5);
 			cell.setBackgroundColor(bc);
 			cell.setVerticalAlignment(Element.ALIGN_RIGHT);
 			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            
            par = new Paragraph("",font);
	        cell = new PdfPCell(par);
 			cell.setPaddingTop(5);
 			cell.setPaddingBottom(5);
 			cell.setBackgroundColor(bc);
 			cell.setVerticalAlignment(Element.ALIGN_RIGHT);
 			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            
            
            
            
            
            
            
            par = new Paragraph("?????????? ??????????????",font2);
	        cell = new PdfPCell(par);
	        cell.setPadding(5);
 			cell.setBackgroundColor(bc);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            
            par = new Paragraph(""+numFormat.format(rural),font3);
	        cell = new PdfPCell(par);
	        cell.setPadding(5);
 			cell.setBackgroundColor(bc);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
              
            par = new Paragraph("",font);
	        cell = new PdfPCell(par);
 			cell.setPaddingTop(5);
 			cell.setPaddingBottom(5);
 			cell.setBackgroundColor(bc);
 			cell.setVerticalAlignment(Element.ALIGN_RIGHT);
 			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            
            par = new Paragraph("",font);
	        cell = new PdfPCell(par);
 			cell.setPaddingTop(5);
 			cell.setPaddingBottom(5);
 			cell.setBackgroundColor(bc);
 			cell.setVerticalAlignment(Element.ALIGN_RIGHT);
 			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            
            par = new Paragraph("",font);
	        cell = new PdfPCell(par);
 			cell.setPaddingTop(5);
 			cell.setPaddingBottom(5);
 			cell.setBackgroundColor(bc);
 			cell.setVerticalAlignment(Element.ALIGN_RIGHT);
 			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
          
           
            
            document.add(table);
            par = new Paragraph(" ",font);
            document.add(par);
            
            
	        document.add(tableShipments);
	       /* 
        	table = new PdfPTable(2);
        	table.setWidthPercentage(90);
        	font.setSize(12);
 	        table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
 	        
 	        if (cpb.getPmtRmk()!=null && cpb.getPmtRmk().trim().length()>0) {
	 	       font2.setColor(BaseColor.RED);
	 	        par = new Paragraph("?????????????? : "+cpb.getPmtRmk(),font2);
		        cell = new PdfPCell(par);
				cell.setPadding(10);
				cell.setColspan(2);
				cell.setVerticalAlignment(Element.ALIGN_LEFT);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell.setBorder(cell.NO_BORDER);
	           table.addCell(cell);
 	        }
 	        Phrase ph;
         	ph = new Paragraph("?????????? ?????????????? : ..................",font);
 			cell = new PdfPCell(ph);
 			cell.setPaddingTop(40);
 			cell.setPaddingBottom(20);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            
            ph = new Paragraph("",font);
 			cell = new PdfPCell(ph);
 			cell.setPaddingTop(40);
 			cell.setPaddingBottom(20);
 			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
 			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            
          
        	document.add(table);
	        */
	        document.close();
	        pdf.flush();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch(Exception e){
			System.out.println("general error");
			e.printStackTrace();
		}finally{
			try{conn.close();}catch(Exception e){}
		}
	}
	

	public void createDir(String dirName){
	    File theDir = new File(dirName);
	    // if the directory does not exist, create it
	    if (!theDir.exists()) {
	        boolean result = false;
	        try{
	            theDir.mkdir();
	            result = true;
	        }catch(Exception e){
				String logErrorMsg = "class=>PDFResults,Exception Msg=>"+e.getMessage(); 
				smartyLogAndErrorHandling.logError("PDFResults", Level.SEVERE, logErrorMsg , e);
				logErrorMsg = "";
	            e.printStackTrace();
	        }        
	        if(!result) {    
	            System.out.println("DIR Failed to be created");  
	        }
	    }
	}
	
	private  PdfPTable getShipmentsTable(Connection conn ,PickUpAgentPaymentBean papb,Font font){
		ArrayList<CaseInformation> shipments=null;
	      
        PdfPTable table = null;
       
        try{
        	shipments = papb.getShipments();
	      
	        int noOfHeaders = 0;
	        float [] relativeWidths = null;
	       
        	noOfHeaders = 13;
	        relativeWidths = new float[noOfHeaders]; 
	       
	        relativeWidths [0] = 15; // notes
	        relativeWidths [1] = 11; // date entry
	        relativeWidths [2] = 5; // qty
	        relativeWidths [3] = 14; //status
	        relativeWidths [4] = 8; // net amt
	        relativeWidths [5] = 7; // shipment cost
	        relativeWidths [6] = 8; // receipt amt
	        relativeWidths [7] = 13; // rcv ph
	        //relativeWidths[8] = 11; // cust name
	        relativeWidths [8] = 15; // address
	        relativeWidths [9] = 10; // rcv name
	        relativeWidths [10] = 9; // cust name
	        relativeWidths [11] = 7; // receipt no 
	        relativeWidths [12] = 4; // seq
	        
	        table = new PdfPTable(relativeWidths);
	        table.setPaddingTop(20);
	        table.setWidthPercentage(100);
	        
	        font.setSize(10);
	        table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
	        // headers
	        //insertHeaders(PdfPTable table, LinkedList<String> headersList, Font font)
	        LinkedList<String> headers = new LinkedList<String>();
	        headers.add("#");
	        headers.add("?????? ??????????");
	        headers.add("?????? ???????? ??????????");
	        headers.add("?????? ??????????????");
	        headers.add("??????????????");
	        headers.add("?????? ????????????");
	        headers.add("???????? ??????????");
	        headers.add("???????? ??????????????");
	        headers.add("????????????");
	        headers.add("????????????");
	        headers.add("??????");
	        headers.add("?????????? ??????????????");
	        headers.add("??????????????");
	        
	       
	        insertHeaders (table, headers, font);
	        font.setColor(0,0,0);
			table.setHeaderRows(1);
			Phrase ph; 
			PdfPCell cell;
			int i=1;
            double totNetAmount = 0;
            double netAmtPerOrder = 0;
            BaseColor bcOdd = new BaseColor(241, 253, 241); 
            BaseColor bcEven = new BaseColor(253, 253, 253); 
            BaseColor bc;
        	for (CaseInformation ci  : shipments){
        		totalShimpments ++;
        		if (i%2==1)
        			bc = bcOdd;
        		else
        			bc = bcEven;
        		netAmtPerOrder = 0;
            	ph = new Phrase(Integer.toString(i),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);
				
				if (ci.getRural().equalsIgnoreCase("Y")) {
					rural ++;
				}else {
					center ++;
				}
				
				ph = new Phrase(ci.getCustReceiptNoOri(),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);
				
				ph = new Phrase(ci.getCustName(),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);
				
				if(ci.getName() != null && ci.getName().trim().length()>0)
					ph = new Phrase(ci.getName(),font);
				else
					ph = new Phrase("-",font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);
				
				ph = new Phrase(ci.getLocationDetails(),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);
				
				ph = new Phrase(ci.getHp(),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);
				
				//reciept amount
				if(ci.getStatus().equalsIgnoreCase("dlv") || ci.getShipmentChargesPaidByCustomer().equalsIgnoreCase("Y"))
					ph = new Phrase(numFormat.format(ci.getReceiptAmt()),font);
				else
					ph = new Phrase("-",font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);
				
				//shipment charges
				//shipment charges
				if(ci.getStatus().equalsIgnoreCase("dlv"))
					ph = new Phrase(numFormat.format(ci.getShipmentCharge()),font);
				else if (ci.getStatus().equalsIgnoreCase("canceled") && ci.getShipmentChargesPaidByCustomer().equalsIgnoreCase("Y"))
					ph = new Phrase(numFormat.format(ci.getShipmentCharge()),font);
				else
					ph = new Phrase("ERROR",font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);
				
				
				
				if(ci.getStatus().equalsIgnoreCase("dlv"))
					netAmtPerOrder = ci.getReceiptAmt() - ci.getShipmentCharge();
				else if (ci.getStatus().equalsIgnoreCase("canceled") && ci.getShipmentChargesPaidByCustomer().equalsIgnoreCase("Y"))
					netAmtPerOrder = ci.getReceiptAmt() - ci.getShipmentCharge();
				else
					netAmtPerOrder = 0;
				
				ph = new Phrase(numFormat.format(netAmtPerOrder),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);
				
				totNetAmount +=netAmtPerOrder;
				
				//status
				if(ci.getStatus().equalsIgnoreCase("dlv"))
					if(ci.getChangedPrice().equalsIgnoreCase("Y") && ci.getReceiptAmtB4Change() != ci.getReceiptAmt())
						ph = new Phrase("???? ?????????????? ???? ?????????? ???????? ?????????? ???? "+numFormat.format((int)ci.getReceiptAmtB4Change())+" "
								+ "?????? "+numFormat.format((int)ci.getReceiptAmt()),font);
					else
						ph = new Phrase("???? ??????????????",font);
				
				else if (ci.getStatus().equalsIgnoreCase("canceled") && ci.getShipmentChargesPaidByCustomer().equalsIgnoreCase("Y"))
					ph = new Phrase("???????? ???? ?????? ???????? ?????????? ???? ?????????????? ?????????? ?????????? ???? ???????????? ??????????????",font);
				else
					ph = new Phrase("ERROR",font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);
				
				
				//qty
				ph = new Phrase(Integer.toString(ci.getQty()),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);
				
				ph = new Phrase(ci.getCreateddt(),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);
				
				ph = new Phrase(ci.getRmk(),font);
				cell = new PdfPCell(ph);
				
				cell.setBackgroundColor(bc);
				cell.setPaddingBottom(10);
				table.addCell(cell);
				
				i++;
        	}
        	BaseColor bcTotal = new BaseColor(0,0,139);
        	
        	Font fontTotal = new Font(baseFont);
	        
        	fontTotal.setSize(12);
        	fontTotal.setColor(new BaseColor(252, 252,252));
        	fontTotal.setStyle(1);
        	ph = new Phrase("???????????? ????????????",fontTotal);
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(10);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
 			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
 			cell.setBackgroundColor(bcTotal);
			cell.setColspan(5);
			table.addCell(cell);
			
			ph = new Phrase(numFormat.format(totNetAmount),fontTotal);
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(10);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
 			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
 			cell.setBackgroundColor(bcTotal);
 			
 			cell.setColspan(7);
			table.addCell(cell);
			
			ph = new Phrase(" ",fontTotal);
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(10);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
 			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
 			cell.setBackgroundColor(bcTotal);
 			cell.setColspan(3);
			table.addCell(cell);
			

        }catch (Exception e){
			e.printStackTrace();
		}
        return table;
	}
	
	
	private void insertHeaders(PdfPTable table, LinkedList<String> headersList, Font font){
		Phrase ph;
    	PdfPCell cell;
    	BaseColor bcHeader = new BaseColor(0,0,139);
    	fontHeaders.setColor(252, 252,252);
    	fontHeaders.setSize(11);
        for (String header : headersList){
            ph = new Phrase(header,fontHeaders);
			cell = new PdfPCell(ph);
			cell.setBackgroundColor(bcHeader);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
 			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setPaddingBottom(5);
			table.addCell(cell);		           
        }
	}
	public String getDocPath() {
		return docPath;
	}


	public void setDocPath(String docPath) {
		this.docPath = docPath;
	}

}
