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
import java.util.logging.Level;

import com.app.core.smartyLogAndErrorHandling;
import com.app.db.mysql;
import com.app.setup.BookOfRecieptsPDF.MyFooter;
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

public class BookOfRecieptsPDF_OLD {
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
            try {
				//cb.addImage(image ,PageSize.A4.getWidth()-400,0,0,80,0,document.top()+20 );
            	cb.addImage(image,PageSize.A5.rotate().getWidth()-535,0,0,50,50,document.top()+25);
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            Date dt = new Date();
            Phrase header = new Phrase(dt.toString(), ffont);
            Phrase footer = new Phrase("Page "+pageNo, ffont);
            Barcode128 code128 = new Barcode128();
		    code128.setGenerateChecksum(true);
		   
		    code128.setCode(rcpNoList.get(rcpSeq));
		    try {
		    	Image barCodeImage = code128.createImageWithBarcode(writer.getDirectContent(), null,BaseColor.WHITE);
		    	barCodeImage.setBorderColor(BaseColor.BLACK);
		    	barCodeImage.setAbsolutePosition(100, 50);
		    	cb.addImage(barCodeImage,PageSize.A5.rotate().getWidth()-460,0,0,50,50,document.bottom()-50);
				 //cb.addImage(barCodeImage,140,0,0,60,257,document.top());
		    	 
			} catch (BadElementException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
           
		    ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
            		new Phrase("شركة خط الناقل للتجارة العامة والنقل العام - الكرخ", bigTitleFont),
                    (document.right() - document.left())/2+144,
                    document.top()+60, 0, PdfWriter.RUN_DIRECTION_RTL,0);
		    ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
            		new Phrase("Transporter line company", mediumTitleFont),
                    (document.right() - document.left())/2+112,
                    document.top()+42, 0, PdfWriter.RUN_DIRECTION_RTL,0);
		    
		    ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
            		new Phrase("بغداد - المنصور - شارع 14 رمضان", smallTitleFont),
                    (document.right() - document.left())/2+110,
                    document.top()+25, 0, PdfWriter.RUN_DIRECTION_RTL,0);
		    
		    ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
            		new Phrase("خدمة الزبائن : 07735932691", ffont),
                    (document.right() - document.left())+50,
                    document.bottom()-5, 0, PdfWriter.RUN_DIRECTION_RTL,0);
		    
		    ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
            		new Phrase("الحسابات : 07901401242", ffont),
                    (document.right() - document.left())+50,
                    document.bottom()-15, 0, PdfWriter.RUN_DIRECTION_RTL,0);
		    
		    ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
            		new Phrase("الإدارة : 07513218737", ffont),
                    (document.right() - document.left())+50,
                    document.bottom()-25, 0, PdfWriter.RUN_DIRECTION_RTL,0);
		    
		    ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
            		new Phrase("المدير المفوض : 07705860581", ffont),
                    (document.right() - document.left())+50,
                    document.bottom()-35, 0, PdfWriter.RUN_DIRECTION_RTL,0);
		    
		    ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
            		footer,
            		(document.right() - document.left())/2+60,
                    document.bottom()-25, 0, PdfWriter.RUN_DIRECTION_RTL,0);
		    
		    
            ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
            		new Phrase(" Delivery on Time System (DoTS), is a system developed by Softecha.", ffont),
                    (document.right() - document.left())/2+170,
                    document.bottom() - 47, 0);
            
           
            
		    BarcodeQRCode qrCode = new BarcodeQRCode(rcpNoList.get(rcpSeq),450,250, null);
		    
		    Image codeQrImage;
			try {
				codeQrImage = qrCode.getImage();
				codeQrImage.setBorderColor(BaseColor.BLACK);
				 codeQrImage.scaleAbsolute(100, 100);
				 cb.addImage(codeQrImage,130,0,0,60,450,document.top()+20);
				 
			} catch (BadElementException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
           
            // getting the canvas covering the existing content
            float x = PageSize.A5.getLeft();
            // middle of the height
            float y = ( PageSize.A5.rotate().getTop() + PageSize.A5.rotate().getBottom())-50;
            // getting the canvas covering the existing content
            
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                new Phrase("دفتر رقم "+bookPrint,sideFont),
                x + 18, y, 90 , PdfWriter.RUN_DIRECTION_RTL,0);
            
           
            cb.setColorStroke(BaseColor.GRAY);
            cb.moveTo(document.left(), document.bottom()+10);
            cb.lineTo(document.right(), document.bottom()+10);
            cb.setLineWidth(1);
            cb.closePathStroke();
            
            cb.setColorStroke(BaseColor.GRAY);
            cb.moveTo(document.left(), document.top()+15);
            cb.lineTo(document.right(), document.top()+15);
            cb.setLineWidth(1);
            cb.closePathStroke();
        }
        public void onStartPage(PdfWriter writer, Document document) {
        	
        	newPage = true;
        }
    }
	public void prepareDocument( int bookId, String ctxPath) {
		this.ctxPath = ctxPath;
		String imgPath = ctxPath+"/smartyresources/img/logo_krchxsm.png";
		docsDir = ctxPath+"/"+"PDFDocs";
		fileName= "smarty_test"+docExtension;
		fullFilePathName = docsDir+"//"+fileName; 
		int i = 1;
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
	        String months[] = {"كانون الثاني", "شباط", "أذار", "نيسان", "أيار", "حزيران", "تموز", "أب", "أيلول", 
	                "تشرين الأول", "تشرين الثاني", "كانون الأول"};
	        GregorianCalendar gcalendar = new GregorianCalendar();
	        int date = gcalendar.get(Calendar.DATE);
	        int year = gcalendar.get(Calendar.YEAR);
	        String arMonth = months[gcalendar.get(Calendar.MONTH)];
	        String datestr = Integer.toString(date)+" "+arMonth+" "+Integer.toString(year);
	       
	        ct.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
	        int paddingBottom = 7;
	        int paddingTop = 7;
	     
	        for ( rcpSeq = 0 ; rcpSeq <rcpNoList.size(); rcpSeq++) {
		        Rectangle rect = new Rectangle(/*starting x point*/30, /*starting y point*/810 , /*width*/600 , /*height*/70);
		        rect.setBorder(Rectangle.BOX);
		        ct.setSimpleColumn(rect);
		        ct.go();
		        
		        PdfPTable table = new  PdfPTable(3);
		        table.setWidthPercentage(100);
		        table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
		        PdfPCell cell;
		        Paragraph par;
		        par = new Paragraph("إسم صاحب المتجر : ",font);
		        cell = new PdfPCell(par);
	 			cell.setPaddingTop(0);
	 			cell.setPaddingBottom(paddingBottom);
	 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
	 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
	 			cell.setColspan(2);
	 			cell.setBorder(cell.NO_BORDER);
	            table.addCell(cell);
	            
	            
	            par = new Paragraph("رقم الكود : ",font);
		        cell = new PdfPCell(par);
	 			cell.setPaddingTop(0);
	 			cell.setPaddingBottom(paddingBottom);
	 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
	 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
	 			cell.setBorder(cell.NO_BORDER);
	            table.addCell(cell);
	            
	            par = new Paragraph("رقم الوصل : "+rcpNoList.get(rcpSeq),font);
		        cell = new PdfPCell(par);
	 			cell.setPaddingTop(0);
	 			cell.setPaddingBottom(paddingBottom);
	 			cell.setColspan(2);
	 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
	 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
	 			cell.setBorder(cell.NO_BORDER);
	            table.addCell(cell);
	            
	            par = new Paragraph("بتاريخ : "+"       -     -    20",font);
		        cell = new PdfPCell(par);
	 			cell.setPaddingTop(0);
	 			cell.setPaddingBottom(paddingBottom);
	 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
	 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
	 			cell.setBorder(cell.NO_BORDER);
	            table.addCell(cell);
	            
	          
	            document.add(table);
	            
		        float [] relativeWidths = new float[6]; 
		        relativeWidths [0] = 15; 
		        relativeWidths [1] = 13; 
		        relativeWidths [2] = 15; 
		        relativeWidths [3] = 8; 
		        relativeWidths [4] = 25; 
		        relativeWidths [5] = 8; 
		        table = new  PdfPTable(relativeWidths);
	            table.setWidthPercentage(100);
	            paddingTop = 5;
	            paddingBottom = 5;
		        table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
	            Phrase ph;
	            BaseColor bcGrey = new BaseColor(245,245,245);
	            ph = new Phrase("الزبون",font);
				cell = new PdfPCell(ph);
				cell.setBackgroundColor(bcGrey);
				cell.setPadding(paddingTop);
				cell.setPaddingBottom(paddingBottom);
				table.addCell(cell);
				
				ph = new Phrase("",font);
				cell = new PdfPCell(ph);
				cell.setPaddingTop(paddingTop);
				cell.setPaddingBottom(paddingBottom);
				table.addCell(cell);
				
				ph = new Phrase("الهاتف",font);
				cell = new PdfPCell(ph);
				cell.setBackgroundColor(bcGrey);
				cell.setPadding(paddingTop);
				cell.setPaddingBottom(paddingBottom);
				table.addCell(cell);
				
				ph = new Phrase("",font);
				cell = new PdfPCell(ph);
				//cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
				cell.setPaddingTop(paddingTop);
				cell.setPaddingBottom(paddingBottom);
				table.addCell(cell);
				document.add(table);
				
				ph = new Phrase("رقم هاتف أخر",font);
				cell = new PdfPCell(ph);
				cell.setBackgroundColor(bcGrey);
				cell.setPadding(paddingTop);
				cell.setPaddingBottom(paddingBottom);
				table.addCell(cell);
				
				ph = new Phrase("",font);
				cell = new PdfPCell(ph);
				//cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
				cell.setPaddingTop(paddingTop);
				cell.setPaddingBottom(paddingBottom);
				table.addCell(cell);
				document.add(table);
				
				 
		        relativeWidths = new float[4]; 
			       
		        relativeWidths [0] = 30; // fragile, color red
		        relativeWidths [1] = 15; // bring items back, color blue
		        relativeWidths [2] = 10; // fragile, color red
		        relativeWidths [3] = 25; // bring items back, color blue
		        
		        table = new  PdfPTable(relativeWidths);
		        table.setWidthPercentage(100);
		        table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
		        table.setSpacingBefore(5f);
				
				ph = new Phrase("تفاصيل العنوان",font);
				cell = new PdfPCell(ph);
				cell.setVerticalAlignment(Element.ALIGN_CENTER);
	 			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setBackgroundColor(bcGrey);
				cell.setColspan(2);
				cell.setBorderWidthTop(3);
				cell.setPaddingTop(paddingTop);
				cell.setPaddingBottom(paddingBottom);
				cell.setBorderWidthLeft(0);
				table.addCell(cell);
				
				
				ph = new Phrase("المحافظه",font);
				cell = new PdfPCell(ph);
				cell.setBorderWidthTop(3);
				cell.setBackgroundColor(bcGrey);
				cell.setPadding(paddingTop);
				table.addCell(cell);
				
				ph = new Phrase("",font);
				cell = new PdfPCell(ph);
				cell.setBorderWidthTop(3);
				cell.setPaddingTop(paddingTop);
				cell.setPaddingBottom(paddingBottom);
				table.addCell(cell);
				
				ph = new Phrase(" ",font);
				cell = new PdfPCell(ph);
				//cell.setBackgroundColor(bcGrey);
				cell.setColspan(4);
				cell.setBorderWidthBottom(0);
				cell.setBorderWidthTop(0);
				cell.setPaddingTop(paddingTop);
				cell.setPaddingBottom(paddingBottom);
				table.addCell(cell);
				
				ph = new Phrase(" ",font);
				cell = new PdfPCell(ph);
				//cell.setBackgroundColor(bcGrey);
				cell.setColspan(4);
				cell.setBorderWidthTop(0);
				cell.setPaddingTop(paddingTop);
				cell.setPaddingBottom(paddingBottom);
				table.addCell(cell);
				
				document.add(table);
				
				
				
				relativeWidths = new float[6];    
		        relativeWidths [0] = 20; // fragile, color red
		        relativeWidths [1] = 10; // bring items back, color blue
		        relativeWidths [2] = 20; // fragile, color red
		        relativeWidths [3] = 10; // bring items back, color blue
		        relativeWidths [4] = 20; // fragile, color red
		        relativeWidths [5] = 10; // bring items back, color blue
		        table = new  PdfPTable(relativeWidths);
	            table.setWidthPercentage(100);
		        table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
		        table.setSpacingBefore(5f);
		        
				ph = new Phrase("تفاصيل الطلب",font);
				cell = new PdfPCell(ph);
				//cell.setBackgroundColor(bcGrey);
				cell.setColspan(6);
				cell.setBorderWidthTop(3);
				cell.setPadding(paddingTop+5);
				cell.setPaddingTop(paddingTop+5);
				cell.setPaddingBottom(paddingBottom);
				table.addCell(cell);
				
				ph = new Phrase("العدد",font);
				cell = new PdfPCell(ph);
				cell.setBackgroundColor(bcGrey);
				cell.setBorderWidthTop(0);
				cell.setPadding(paddingTop);
				table.addCell(cell);
				
				ph = new Phrase("",font);
				cell = new PdfPCell(ph);
				cell.setBorderWidthTop(0);
				cell.setPaddingTop(paddingTop);
				cell.setPaddingBottom(paddingBottom);
				table.addCell(cell);
				
				ph = new Phrase("الوزن",font);
				cell = new PdfPCell(ph);
				cell.setBackgroundColor(bcGrey);
				cell.setBorderWidthTop(0);
				cell.setPadding(paddingTop);
				table.addCell(cell);
				
				ph = new Phrase("",font);
				cell = new PdfPCell(ph);
				cell.setBorderWidthTop(0);
				cell.setPaddingTop(paddingTop);
				cell.setPaddingBottom(paddingBottom);
				table.addCell(cell);
				
				ph = new Phrase("النوع",font);
				cell = new PdfPCell(ph);
				cell.setBackgroundColor(bcGrey);
				cell.setBorderWidthTop(0);
				cell.setPadding(paddingTop);
				table.addCell(cell);
				
				ph = new Phrase("",font);
				cell = new PdfPCell(ph);
				cell.setBorderWidthTop(0);
				cell.setPaddingTop(paddingTop);
				cell.setPaddingBottom(paddingBottom);
				table.addCell(cell);
				//////////////////////////////////
				
		        
				
				ph = new Phrase("الملاحظات",font);
				cell = new PdfPCell(ph);
				cell.setBorderWidthTop(0);
				cell.setBorderWidthLeft(0);
				cell.setBorderWidthBottom(0);
				cell.setPadding(paddingTop);
				table.addCell(cell);
				
				ph = new Phrase("",font);
				cell = new PdfPCell(ph);
				cell.setColspan(5);
				cell.setBorderWidthTop(0);
				cell.setBorderWidthBottom(0);
				cell.setBorderWidthRight(0);
				cell.setPaddingTop(paddingTop);
				cell.setPaddingBottom(paddingBottom);
				table.addCell(cell);
				
				ph = new Phrase(" ",font);
				cell = new PdfPCell(ph);
				cell.setColspan(6);
				cell.setBorderWidthTop(0);
				cell.setPaddingTop(paddingTop);
				cell.setPaddingBottom(paddingBottom);
				table.addCell(cell);
				document.add(table);
				
				
				relativeWidths = new float[4];    
		        relativeWidths [0] = 20; // fragile, color red
		        relativeWidths [1] = 10; // bring items back, color blue
		        relativeWidths [2] = 20; // fragile, color red
		        relativeWidths [3] = 15; // bring items back, color blue
		        table = new  PdfPTable(relativeWidths);
	            table.setWidthPercentage(100);
		        table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
		        table.setSpacingBefore(5f);
				
				ph = new Phrase("المبلغ شامل التوصبل",font);
				cell = new PdfPCell(ph);
				cell.setBackgroundColor(bcGrey);
				cell.setBorderWidthTop(2);
				cell.setPadding(paddingTop);
				table.addCell(cell);
				
				ph = new Phrase("",font);
				cell = new PdfPCell(ph);
				cell.setBorderWidthTop(2);
				cell.setPadding(paddingTop);
				table.addCell(cell);
				
				ph = new Phrase("تاريخ الأستلام",font);
				cell = new PdfPCell(ph);
				cell.setBackgroundColor(bcGrey);
				cell.setBorderWidthTop(2);
				cell.setPadding(paddingTop);
				table.addCell(cell);
				
				ph = new Phrase("",font);
				cell = new PdfPCell(ph);
				cell.setBorderWidthTop(2);
				cell.setPadding(paddingTop);
				table.addCell(cell);
				
		        document.add(table);
		        
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
	
	
	private void insertHeaders(PdfPTable table, LinkedList<String> headersList, Font font){
		Phrase ph;
    	PdfPCell cell;
        for (String header : headersList){
            ph = new Phrase(header,font);
			cell = new PdfPCell(ph);
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
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
