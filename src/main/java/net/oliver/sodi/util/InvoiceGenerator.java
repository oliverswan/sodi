package net.oliver.sodi.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import net.oliver.sodi.model.Invoice;
import net.oliver.sodi.model.InvoiceItem;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.DecimalFormat;

@Component
public class InvoiceGenerator {

	private BaseFont bfBold;
	private BaseFont bf;
	
	private int pageNumber = 0;

	public static void main(String[] args) {
//		String pdfFilename = "aaa.pdf";
//		InvoiceGenerator invoiceGenerator = new InvoiceGenerator();
//		invoiceGenerator.createPDF(pdfFilename);
	}


	// 0 width 612
	class MyFooter extends PdfPageEventHelper {
	    public void onEndPage(PdfWriter writer, Document document) {
	    	
	        PdfContentByte cb = writer.getDirectContent();
	        
	        cb.rectangle(40, 80, 532, 50);
			cb.setColorFill(BaseColor.LIGHT_GRAY);
			cb.fill();
			cb.setColorFill(BaseColor.BLACK);
	        ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, footer(),
	            (document.right() - document.left()) / 2 + document.leftMargin(),
	           110, 0);
	        ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, footer2(),
		            (document.right() - document.left()) / 2 + document.leftMargin(),
		           95, 0);
	        line(cb, 40, 65,572, 65);
	        
	        ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,footer3(),
		            (document.right() - document.left()) / 2 + document.leftMargin(),
		           50, 0);

	        int y = 190;
			int x =40;
			createHeadings(cb, x, y, "Bank deposit details:",10);
			createText(cb, x, y-10, "Account Name: Australian Karting Promotions P/L",10);
			createText(cb, x, y-20, "Bank: NAB",10);
			createText(cb, x, y-30, "BSB: 083-266",10);
			createText(cb, x, y-40, "Account No: 83-721-7121",10);
			
			int wid = 250;
			createHeadings(cb, x+wid, y , "No Statements issued.",10);
			createHeadings(cb, x+wid, y-10, "Please pay on this Invoice, Thanks! ",10);
			
	        
	    }
	    private Phrase footer() {
	        Font ffont = new Font(Font.FontFamily.UNDEFINED, 10, Font.ITALIC);
	        Phrase p = new Phrase("Orders will not be despatched until cleared payment has been received.",ffont);
	        return p;
	    }
	    private Phrase footer2() {
	        Font ffont = new Font(Font.FontFamily.UNDEFINED, 8, Font.ITALIC);
	        Phrase p = new Phrase( "Please email bank deposit confirmation to sales@sodirentalkarts.com.au",ffont);
	        return p;
	    }
	    private Phrase footer3() {
	    	Font ffont = new Font(Font.FontFamily.UNDEFINED, 8, Font.ITALIC);
	        Phrase p = new Phrase( "Australian Karting Promotions P/L ABN 21 647 290 743 trading as SodiKart Australasia",ffont);
	        return p;
	    }
	}

	class MyFooter2 extends PdfPageEventHelper {
		public void onEndPage(PdfWriter writer, Document document) {

			PdfContentByte cb = writer.getDirectContent();

			line(cb, 40, 65,572, 65);

			ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,footer3(),
					(document.right() - document.left()) / 2 + document.leftMargin(),
					50, 0);

		}
		private Phrase footer() {
			Font ffont = new Font(Font.FontFamily.UNDEFINED, 10, Font.ITALIC);
			Phrase p = new Phrase("Orders will not be despatched until cleared payment has been received.",ffont);
			return p;
		}
		private Phrase footer2() {
			Font ffont = new Font(Font.FontFamily.UNDEFINED, 8, Font.ITALIC);
			Phrase p = new Phrase( "Please email bank deposit confirmation to sales@sodirentalkarts.com.au",ffont);
			return p;
		}
		private Phrase footer3() {
			Font ffont = new Font(Font.FontFamily.UNDEFINED, 8, Font.ITALIC);
			Phrase p = new Phrase( "Australian Karting Promotions P/L ABN 21 647 290 743 trading as SodiKart Australasia",ffont);
			return p;
		}
	}

	public void createDelivery(Document doc, PdfWriter writer, Invoice invoice) {
		doc.open();
		initializeFonts();
		try {
			writer.setPageEvent(new MyFooter2());
			doc.addAuthor("oliver");
			doc.addCreationDate();
			doc.addProducer();
			doc.addCreator("sodirentalkarts.com.au");
			doc.addTitle("Delivery Note");
			doc.setPageSize(PageSize.LETTER);
			doc.open();
			PdfContentByte cb = writer.getDirectContent();
			header(doc, cb);
			deliveryContent(doc, cb,invoice);
		}  catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (doc != null) {
				doc.close();
			}
			if (writer != null) {
				writer.close();
			}
		}
	}

	public void createInvoice(Document doc, PdfWriter writer, Invoice invoice) {
		doc.open();
		initializeFonts();
		try {
			writer.setPageEvent(new MyFooter());
			doc.addAuthor("oliver");
			doc.addCreationDate();
			doc.addProducer();
			doc.addCreator("sodirentalkarts.com.au");
			doc.addTitle("Invoice");
			doc.setPageSize(PageSize.LETTER);
			doc.open();
			
			PdfContentByte cb = writer.getDirectContent();
			boolean beginPage = true;
			int y = 0;

//			for (int i = 0; i < 1; i++) {
				if (beginPage) {
					beginPage = false;
					header(doc, cb);
					generateHeader(doc, cb,invoice);
					y = 615;
				}
//				generateDetail(doc, cb, i, y);
				y = y - 15;
				if (y < 50) {
					printPageNumber(cb);
					doc.newPage();
					beginPage = true;
				}
//			}
//			printPageNumber(cb);

		}  catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (doc != null) {
				doc.close();
			}
			if (writer != null) {
				writer.close();
			}
		}
	}

	private void line(PdfContentByte cb, float x, float y,  float x2, float y2)
	{
		cb.moveTo(x,y);
		cb.lineTo(x2,y2);
		cb.stroke();
	}

	private void header(Document doc, PdfContentByte cb) {

		try {

			cb.setLineWidth(1f);
			cb.moveTo(40, 670);
			cb.lineTo(560, 670);

			cb.stroke();

			Image companyLogo = Image.getInstance("logo.gif");
			companyLogo.setAbsolutePosition(25, 700);
			companyLogo.scalePercent(25);
			doc.add(companyLogo);
			PdfPTable invoiceMetaDataTable = new PdfPTable(1);
			invoiceMetaDataTable.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
			invoiceMetaDataTable.getDefaultCell().setHorizontalAlignment( PdfPCell.ALIGN_RIGHT);
			Font fontH1 = new Font(bfBold, 12, Font.BOLD);
			PdfPCell cell =new PdfPCell(new Phrase("SodiKart Australasia",fontH1));
			cell.setHorizontalAlignment( PdfPCell.ALIGN_RIGHT);
			cell.setBorder(PdfPCell.NO_BORDER);
			invoiceMetaDataTable.addCell(cell);
			invoiceMetaDataTable.addCell("261 Governor Road");
			invoiceMetaDataTable.addCell("Braeside VIC 3195");   // ���� ����
			invoiceMetaDataTable.addCell("Tel 0402 84 83 82");

			invoiceMetaDataTable.addCell("Email sales@sodirentalkarts.com.au");
			invoiceMetaDataTable.addCell("www.sodirentalkarts.com.au");
			invoiceMetaDataTable.setTotalWidth(250.0f);

			PdfPTable invoiceMetaDataTable0 = new PdfPTable(1);
			invoiceMetaDataTable0.setTotalWidth(250.0f);
			invoiceMetaDataTable0.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
//	          invoiceMetaDataTable0.getDefaultCell().setBorderWidth(1.5f);
			invoiceMetaDataTable0.addCell(invoiceMetaDataTable);
			invoiceMetaDataTable0.writeSelectedRows(0, 3, 300, 780, cb);

		}

		catch (DocumentException dex) {
			dex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	private void deliveryContent(Document doc, PdfContentByte cb,Invoice invoice) {

		try {
			int leftX = 40;
			int y = 630;
			createHeadings(cb, leftX, y, "Shipping Address:",10);
			createText(cb, leftX, y-10, invoice.getContactName(),10);
			createText(cb, leftX, y-20, invoice.getContactPerson(),10);
			createText(cb, leftX, y-30, invoice.getPoaddressline1(),10);
			createText(cb, leftX, y-40, invoice.getPoaddressline2(),10);
			createText(cb, leftX, y-50, invoice.getPoaddressline3(),10);

			int x = 300;


			int invnY = 550;
			createText(cb, leftX, invnY+20, invoice.getMoblie(),10);
			createText(cb, leftX+400, invnY+20, invoice.getTel(),10);
			createHeadings(cb, leftX, invnY, "DELIVERY NOTE"+invoice.getInvoiceNumber(),14);

//			Invoice date: 20/08/2018
//			Payment type: Bank Deposit
			createText(cb, x+100, invnY, "Date:    "+invoice.getInvoiceDate(),10);

//			Qty. Part No. Product Unit Price Total GST Subtotal

			line(cb,leftX,490,560,490);

//			createText(cb, 40, invnY-50, "Qty.       Part No.                       Product                                                      Unit Price        Total        GST        Subtotal",10);

			createText(cb, leftX, invnY-40,"Qty.",10);
			createText(cb, leftX+40, invnY-40,"SKU",10);
			createText(cb, leftX+110, invnY-40,"Product Name",10);



			int currentheight = invnY-50-20;
			for(InvoiceItem item : invoice.getItems())
			{
				currentheight -= 15;
				addItem2(cb,String.valueOf(item.getQuantity()),
						item.getInventoryItemCode(),
						item.getDescription(),
						String.valueOf(item.getUnitAmount()),
						String.valueOf(item.getTotalamount()),String.valueOf(item.getGst()),"$"+String.valueOf(item.getSubtotal()),currentheight);
			}
			int customNoteY= currentheight-50;
			if(currentheight-50<400)
			{
				customNoteY = currentheight;
			}
			createHeadings(cb, leftX, customNoteY,"Custom Note:",10);
			createText(cb,leftX+30,customNoteY,invoice.getCustomerNote(),8);


			if(!StringUtils.isBlank(invoice.getOrderNote()))
			{
				String[] arty = invoice.getOrderNote().split(",");
				for(int i =0;i<arty.length;i++)
				{
					createText(cb,leftX,currentheight-60-10*i,arty[i],8);
				}
				createText(cb,leftX,currentheight-60-10*arty.length,"On back order.",8);
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void generateHeader(Document doc, PdfContentByte cb,Invoice invoice) {

		try {
			int leftX = 40;
			int y = 630;
			createHeadings(cb, leftX, y, "Billing Address:",10);
			createText(cb, leftX, y-10, invoice.getContactName(),10);
			createText(cb, leftX, y-20, invoice.getContactPerson(),10);
			createText(cb, leftX, y-30, invoice.getPoaddressline1(),10);
			createText(cb, leftX, y-40, invoice.getPoaddressline2(),10);
//			createText(cb, leftX, y-50, invoice.getPoaddressline3(),10);
			createText(cb, leftX ,y-50, invoice.getPocountry(),10);
			
			int x = 300;
			
			createHeadings(cb, x, y , "Shipping Address:",10);
			createText(cb, x, y-10, invoice.getContactName(),10);
			createText(cb, x, y-20, invoice.getContactPerson(),10);
			createText(cb, x, y-30, invoice.getPoaddressline1(),10);
			createText(cb, x, y-40, invoice.getPoaddressline2(),10);
//			createText(cb, x, y-50, invoice.getPoaddressline3(),10);
			createText(cb, x, y-50, invoice.getPocountry(),10);
			
			int invnY = 550;
			createHeadings(cb, leftX, invnY, "Tax Invoice"+invoice.getInvoiceNumber(),14);
			
//			Invoice date: 20/08/2018
//			Payment type: Bank Deposit
			createText(cb, x+100, invnY, "Invoice date:    "+invoice.getInvoiceDate().substring(0,10),10);
			createText(cb, x+100, invnY-10, "Payment type:    Bank Deposit",10);
			
//			Qty. Part No. Product Unit Price Total GST Subtotal
			
			line(cb,leftX,490,560,490);
			
//			createText(cb, 40, invnY-50, "Qty.       Part No.                       Product                                                      Unit Price        Total        GST        Subtotal",10);
			
			createText(cb, leftX, invnY-50,"Qty.",10);
			createText(cb, leftX+40, invnY-50,"Part No.",10);
			createText(cb, leftX+110, invnY-50,"Product",10);
			createText(cb, leftX+340, invnY-50,"Unit Price",10);
			createText(cb, leftX+400, invnY-50,"Total",10);
			createText(cb, leftX+440, invnY-50,"GST",10);
			createText(cb, leftX+480, invnY-50,"Subtotal",10);
			
			
			int currentheight = invnY-50-20;
			for(InvoiceItem item : invoice.getItems())
			{
				currentheight -= 15;
				addItem(cb,String.valueOf(item.getQuantity()),
						item.getInventoryItemCode(),
						item.getDescription(),
						MathUtil.df.format(item.getUnitAmount()),
						item.getTotalamounts(),item.getGsts(),"$"+item.getSubtotals(),currentheight);
			}
			currentheight -= 20;
			createText(cb, leftX+340, currentheight,"Subtotal:",10);
			createText(cb, leftX+400, currentheight,String.valueOf(invoice.getTotalamount()),10);
			createText(cb, leftX+440, currentheight,String.valueOf(invoice.getGst()),10);
			createText(cb, leftX+480, currentheight,"$"+String.valueOf(invoice.getSubtotal()),10);
			line(cb,leftX+335,currentheight-10,leftX+505,currentheight-10);

			currentheight -= 30;
			createText(cb, leftX+340, currentheight,"Total:",10);
			createText(cb, leftX+400, currentheight,String.valueOf(invoice.getTotalamount()),10);
			createText(cb, leftX+440, currentheight,String.valueOf(invoice.getGst()),10);
			createText(cb, leftX+480, currentheight,"$"+String.valueOf(invoice.getSubtotal()),10);

			int customNoteY= currentheight-50;
			if(currentheight-50<400)
			{
				customNoteY = currentheight;
			}
			createHeadings(cb, leftX, customNoteY,"Custom Note:",10);
			createText(cb,leftX+30,customNoteY,invoice.getCustomerNote(),8);


			if(!StringUtils.isBlank(invoice.getOrderNote()))
			{
				String[] arty = invoice.getOrderNote().split(",");
				for(int i =0;i<arty.length;i++)
				{
					createText(cb,leftX,currentheight-60-10*i,arty[i],8);
				}
				createText(cb,leftX,currentheight-60-10*arty.length,"On back order.",8);
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	private void addItem2(PdfContentByte cb,String qty,String code,String name,String price,String total,String gst,String subtotal,int y)
	{
		createText(cb, 40, y,qty,10);
		createText(cb, 80, y,code,10);
		createText(cb, 150,y,name,10);
	}

	private void addItem(PdfContentByte cb,String qty,String code,String name,String price,String total,String gst,String subtotal,int y)
	{
		createText(cb, 40, y,qty,10);
		createText(cb, 80, y,code,10);
		createText(cb, 150,y,name,10);
		createText(cb, 380,y,price,10);
		createText(cb, 440,y,total,10);
		createText(cb, 480, y,gst,10);
		createText(cb, 520,y,subtotal,10);
	}

	private void generateDetail(Document doc, PdfContentByte cb, int index,
			int y) {
		DecimalFormat df = new DecimalFormat("0.00");

		try {

			createContent(cb, 48, y, String.valueOf(index + 1),
					PdfContentByte.ALIGN_RIGHT);
			createContent(cb, 52, y, "ITEM" + String.valueOf(index + 1),
					PdfContentByte.ALIGN_LEFT);
			createContent(cb, 152, y,
					"Product Description - SIZE " + String.valueOf(index + 1),
					PdfContentByte.ALIGN_LEFT);

			double price = Double.valueOf(df.format(Math.random() * 10));
			double extPrice = price * (index + 1);
			createContent(cb, 498, y, df.format(price),
					PdfContentByte.ALIGN_RIGHT);
			createContent(cb, 568, y, df.format(extPrice),
					PdfContentByte.ALIGN_RIGHT);

		}

		catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	
	private void createHeadings(PdfContentByte cb, float x, float y, String text,int size) {

		cb.beginText();
		cb.setFontAndSize(bfBold, size);
		cb.setTextMatrix(x, y);
		cb.showText(text.trim());
		cb.endText();

	}
	
	private void createText(PdfContentByte cb, float x, float y, String text,int size) {
		if(text == null)
			text ="";
		cb.beginText();
		cb.setFontAndSize(bf, size);
		cb.setTextMatrix(x, y);
		cb.showText(text);
		cb.endText();

	}
	
	private void printPageNumber(PdfContentByte cb) {

		cb.beginText();
		cb.setFontAndSize(bfBold, 8);
		cb.showTextAligned(PdfContentByte.ALIGN_RIGHT, "Page No. "
				+ (pageNumber + 1), 570, 25, 0);
		cb.endText();

		pageNumber++;

	}

	private void createContent(PdfContentByte cb, float x, float y,
			String text, int align) {

		cb.beginText();
		cb.setFontAndSize(bf, 8);
		cb.showTextAligned(align, text.trim(), x, y, 0);
		cb.endText();

	}

	private void initializeFonts() {

		try {
			bfBold = BaseFont.createFont(BaseFont.HELVETICA_BOLD,
					BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
			bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252,
					BaseFont.NOT_EMBEDDED);

		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}