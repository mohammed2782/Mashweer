package com.app.setup;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.logging.Level;

import com.app.cases.CaseInformation;

import com.app.core.smartyLogAndErrorHandling;
import com.app.db.mysql;
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
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.BarcodeQRCode;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.GrayColor;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.qrcode.QRCode;

public class BookOfRecieptsPDF {
	private  String docPath;
	private String docsDir;
	private String fullFilePathName;
	private String fileName;
	public String docDir="";// the document directory
	public String docLang = "en-US";// arabic kuwait
	public String docExtension = ".pdf";
	String ctxPath = "";
	BaseFont baseFont = null;
	Font font;
	Font sideFont;
	Font bigTitleFont;
	Font smallTitleFont;
	Font mediumTitleFont;
	int rcpSeq = 0;
	Font ffont;
	int bookPrint=0;
	ArrayList<String> rcpNoList ;
	public DecimalFormat numFormat = new DecimalFormat("#,###,###.##");
	public static final Font FONT = new Font(FontFamily.HELVETICA, 12, Font.NORMAL, GrayColor.GRAYWHITE);

	boolean newPage = true;
	 int totalStdNo=0;
	class MyFooter extends PdfPageEventHelper {
      
        
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
		        image.scaleAbsolute(PageSize.A5.rotate());
		        image.setAbsolutePosition(0, 0);
		        //PdfContentByte canvas = writer.getDirectContentUnder();
		        image.scaleToFit(100,100);
        }
        
        public void onEndPage(PdfWriter writer, Document document) {
        	pageNo ++;
        	PdfContentByte cb = writer.getDirectContent();
            Barcode128 code128 = new Barcode128();
		    code128.setGenerateChecksum(true);
		   
		    code128.setCode(rcpNoList.get(rcpSeq));
		    try {
		    	Image barCodeImage = code128.createImageWithBarcode(writer.getDirectContent(), null,BaseColor.WHITE);
		    	barCodeImage.setBorderColor(BaseColor.BLACK);
		    	barCodeImage.setAbsolutePosition(100, 50);
		    	cb.addImage(barCodeImage,PageSize.A5.rotate().getWidth()-460,0,0,50,220,document.bottom()-50);		    	 
			} catch (BadElementException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    BarcodeQRCode qrCode = new BarcodeQRCode(rcpNoList.get(rcpSeq),1000,1000, null);
		    
		    Image codeQrImage;
			try {
				codeQrImage = qrCode.getImage();
				codeQrImage.setBorderColor(BaseColor.BLACK);
				codeQrImage.scaleToFit(100, 100);
				codeQrImage.scaleAbsolute(100, 100);
				cb.addImage(codeQrImage,50,0,0,50,340,document.top()-10);
				 
			} catch (BadElementException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
	            		new Phrase(rcpNoList.get(rcpSeq), smallTitleFont),
	                    (document.right() - document.left())/4+ 7,
	                    document.top()-1, 0, PdfWriter.RUN_DIRECTION_RTL,0);
			
        }
        public void onStartPage(PdfWriter writer, Document document) {
        	
        	newPage = true;
        }
    }
	public void prepareDocument( int bookId, String ctxPath) {
		this.ctxPath = ctxPath;
		String IMAGE =  ctxPath+"/smartyresources/img/invoice2.jpg";
		String imgPath = ctxPath+"/smartyresources/img/logo_krchxsm.png";
		docsDir = ctxPath+"/"+"PDFDocs";
		fileName= "smarty_test"+docExtension;
		fullFilePathName = docsDir+"//"+fileName; 
		
		createDir(docsDir);
		setDocPath(fullFilePathName);
		Connection conn =null;
		Utilities ut = new Utilities();
		
		try {
			conn = mysql.getConn();
			bookPrint = bookId;
			Document document = new Document(PageSize.A5.rotate(), 50, 50, 85, 50);
		    PdfWriter pdf = null;
		    MyFooter event = new MyFooter();
		    event.setImagePath(imgPath);
		    rcpNoList  = ut.getRcpNoList (conn, bookId );
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
			
			ffont =  new Font (baseFont);
	        font  = new Font(baseFont);
	        font.setSize(11);
	        //font for title of document
	        bigTitleFont = new Font (baseFont);
	        bigTitleFont.setSize(13);
	        
	        mediumTitleFont = new Font (baseFont);
	        mediumTitleFont.setSize(12);
	        
	        smallTitleFont = new Font (baseFont);
	        smallTitleFont.setSize(11);
	        
	        ffont.setColor(BaseColor.DARK_GRAY);
		    ffont.setSize(7);
	        
	        sideFont = new Font(baseFont);
	        sideFont.setSize(8);
	        sideFont.setColor(new BaseColor(140, 3, 12 ));
	        BaseColor fontColor = new BaseColor (33,33,37);
	        font.setColor(fontColor);
	        Font font2 = new Font();
	        font2.setSize(30);
	        
	        ColumnText ct = new ColumnText(pdf.getDirectContent());
	       
	       
	        ct.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
	       
	        PdfContentByte canvas = pdf.getDirectContentUnder();
	        Image image = Image.getInstance(IMAGE);
	        image.scaleAbsolute(PageSize.A5.rotate());
	        image.setAbsolutePosition(0, 0);
	       
	        for ( rcpSeq = 0 ; rcpSeq <rcpNoList.size(); rcpSeq++) {
	        	canvas.addImage(image);
				document.newPage();
	        }
	        
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
	
	public String getDocPath() {
		return docPath;
	}


	public void setDocPath(String docPath) {
		this.docPath = docPath;
	}

}
