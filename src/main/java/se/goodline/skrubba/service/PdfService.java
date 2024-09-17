package se.goodline.skrubba.service;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;

import se.goodline.skrubba.model.Aspirant;
import se.goodline.skrubba.model.Kolonilott;
import se.goodline.skrubba.model.Param;
import se.goodline.skrubba.model.Tillsalu;
import se.goodline.skrubba.repository.AspirantRepository;
import se.goodline.skrubba.repository.KoloniLottRepository;
import se.goodline.skrubba.repository.ParamRepository;
import se.goodline.skrubba.repository.TillSaluRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@Service
public class PdfService 
{
	@Autowired
	private KoloniLottRepository lottRepo;
	
	@Autowired
	private AspirantRepository aspRepo;
	
	@Autowired
	private TillSaluRepository saluRepo;
	
	@Autowired
	private ParamRepository params;
	
	private static final int RADAVST = 5;
	
    public void createBlankettHeader(Document document, String rubrik) 
    {
        try 
        {            
            document.setMargins(50, 57, 40, 57); // top, right, bottom, left         
            
            // Lägg till loggan
            ClassLoader classLoader = getClass().getClassLoader();
            // Hämta resursens filväg
            String imagePath = classLoader.getResource("bilagor/sklogga.png").getPath();
	        Image logo = new Image(ImageDataFactory.create(imagePath));
	        logo.setHeight(78);
	        logo.setWidth(78);
	        
	        // Lägg till en rubrik
	        Paragraph title = new Paragraph(rubrik)
                    .setFontSize(12)                      
                    .setFontColor(ColorConstants.BLACK)
                    .setMarginTop(8)
                    .setPadding(0);
           
	        SolidLine solidLine = new SolidLine(1f);  // 1f is the thickness of the line
            LineSeparator lineSeparator = new LineSeparator(solidLine)
            		.setWidth(UnitValue.createPercentValue(100))  // Make the line take up 100% of the available width
                    .setStrokeColor(ColorConstants.BLACK)
                    .setCharacterSpacing(0);
                    
	        		
            Paragraph titleWithLine = new Paragraph()
                    .add(title)
                    .add(lineSeparator)
                    .setMarginTop(0)
                    .setMarginBottom(0)
                    .setFixedLeading(8);
            
            Cell logoCell = new Cell().add(logo).setBorder(Border.NO_BORDER)
            									.setPadding(0);
            									
            
            Cell titleCell = new Cell().add(titleWithLine).setBorder(Border.NO_BORDER)
            		                                      .setTextAlignment(TextAlignment.LEFT)
            		                                      .setPadding(0);
	        										  	
            Table table = new Table(UnitValue.createPercentArray(new float[]{1, 5}));
            table.setWidth(UnitValue.createPercentValue(100)); // Make the table width 100% of the page		
	                        
            table.addCell(logoCell);
	        table.addCell(titleCell);
            document.add(table);	        	        	               
            return;
        } 
        catch (Exception e) 
        {
            throw new RuntimeException("Error generating PDF", e);
        }
    }
    public byte[] createBlankett300(int saluId)
    {    
    	 Tillsalu salu = saluRepo.getById(saluId);
    	 Kolonilott lott = lottRepo.getByLottnr(salu.getLottnr());
    	 String saljDat = new SimpleDateFormat("yyyy-MM-dd").format(salu.getSaljdatum());
    	 Optional<Param> OptParam =  params.findByParamName("Föreningsnamn");
    	 String koloniNamn = OptParam.isPresent() ? OptParam.get().getParamValue() : "Skrubba koloniträdgårdsförening";
    	 OptParam =  params.findByParamName("Föreningsort");
    	 String koloniOrt = OptParam.isPresent() ? OptParam.get().getParamValue() : "Skrubba";
    	 
    	 try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) 
    	 {
    		    PdfFont specialFont = PdfFontFactory.createFont("Courier-Oblique", "WinAnsi", false);
    	        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
    	        PdfDocument pdfDocument = new PdfDocument(writer);
    	        Document document = new Document(pdfDocument);
    	        createBlankettHeader(document, "300. Försäljning av stuglott/hembud");
    	       
                document.add(new Paragraph("Till styrelsen för").setPaddingTop(20));
                document.add(new Paragraph(koloniNamn).setFont(specialFont).setFontSize(12)); 
                Paragraph paragraph = new Paragraph();
    	        paragraph.add(new Text("Undertecknad medlem i koloniträdgårdsföreningen med arrenderätt till lott nr"));
    	        paragraph.add(new Text("" + lott.getLottnr()).setFont(specialFont).setFontSize(12)); 
    	        paragraph.add(new Text(" önskar\nfrånträda arrenderätten."));
    	        document.add(paragraph.setPaddingTop(20));
    	        
    	        document.add(new Paragraph("I enlighet med medlemsavtalets §§ 4 och 5 hembjuder jag härmed till föreningen de bygg-\nnader och anläggningar som markägaren tillåtit för att lotten ska kunna brukas för koloni-\nträdgårdsändamål.").setPaddingTop(5));
                document.add(new Paragraph("Samtidigt begär jag mitt utträde ur föreningen.").setPaddingTop(5));
                
                Table table = new Table(UnitValue.createPercentArray(new float[]{3, 1}));
                table.setWidth(UnitValue.createPercentValue(100)); // Make the table width 100% of the page		
    	                        
                table.addCell(new Cell().add(new Paragraph("Ort: ").add(new Text(koloniOrt).setFont(specialFont).setFontSize(12))).setBorder(Border.NO_BORDER));
                table.addCell(new Cell().add(new Paragraph("Datum:").add(new Text(saljDat).setFont(specialFont).setFontSize(12))).setBorder(Border.NO_BORDER));
                document.add(table.setPaddingTop(30));
                
               
                document.add(new Paragraph("Namnteckning:....................................................................................................................").setPaddingTop(10));
                
                paragraph = new Paragraph();
                paragraph.add(new Text("Namnförtydligande: "));
                paragraph.add(new Text(lott.getAgare()).setFont(specialFont).setFontSize(12));
                document.add(paragraph.setPaddingTop(10));
    	        
                SolidLine solidLine2 = new SolidLine(2f);  // 1f is the thickness of the line
                LineSeparator lineSeparator2 = new LineSeparator(solidLine2)
                		.setWidth(UnitValue.createPercentValue(100))  // Make the line take up 100% of the available width
                        .setStrokeColor(ColorConstants.BLACK);                       
                document.add(new Paragraph().add(lineSeparator2).setPaddingTop(30));
                
                document.add(new Paragraph("Styrelsens beslut ska enligt lag meddelas inom 30 dagar efter erhållandet av denna upp-\nsägning.").setPaddingTop(10));
                document.add(new Paragraph("Uppsägningen inkom till styrelsen: ").add(new Text(saljDat).setFont(specialFont).setFontSize(12)).setPaddingTop(10));
                
                paragraph = new Paragraph();
                paragraph.add(new Text("Styrelsens beslut; "));
                paragraph.add(new Text("Tillstyrks").setFont(specialFont).setFontSize(12));
                document.add(paragraph.setPaddingTop(10));
                
                
                //document.add(new Paragraph("Styrelsens beslut; Tillstyrks.................................................................................................").setPaddingTop(10));
                document.add(new Paragraph("............................................................................................................................................").setPaddingTop(10));
                document.add(new Paragraph("............................................................................................................................................").setPaddingTop(10));
                document.add(new Paragraph("Detta beslut har meddelats lottinnehavaren den .............................").setPaddingTop(10));
                document.close();
    	        return byteArrayOutputStream.toByteArray();
    	 } 
    	 catch (Exception e) 
    	 {
    	        throw new RuntimeException("Error generating PDF", e);
    	 }    	
    }
    public byte[] createBlankett301(int saluId)
    {
    	Tillsalu salu = saluRepo.getById(saluId);
    	Kolonilott lott = lottRepo.getByLottnr(salu.getLottnr());
    	Aspirant aspirant = aspRepo.findById(salu.getAsp());
    	String saljDat = new SimpleDateFormat("yyyy-MM-dd").format(salu.getSaljdatum());
    	Optional<Param> OptParam =  params.findByParamName("Föreningsnamn");
    	String koloniNamn = OptParam.isPresent() ? OptParam.get().getParamValue() : "Skrubba koloniträdgårdsförening";
    	OptParam =  params.findByParamName("Föreningsort");
    	String koloniOrt = OptParam.isPresent() ? OptParam.get().getParamValue() : "Skrubba";
   	 
    	try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) 
   	 	{
    		PdfFont specialFont = PdfFontFactory.createFont("Courier-Oblique", "WinAnsi", false);
    		PdfWriter writer = new PdfWriter(byteArrayOutputStream);
	        PdfDocument pdfDocument = new PdfDocument(writer);
	        Document document = new Document(pdfDocument);
	        createBlankettHeader(document, "301. Köpeavtal för stuglott");
    		
	        document.add(new Paragraph("KÖPEAVTAL").setPaddingTop(25).setFontSize(24));
	        
	        Paragraph paragraph = new Paragraph();
	        paragraph.add(new Text("Säljaren/säljarna "));
	        paragraph.add(new Text(lott.getAgare()).setFont(specialFont).setFontSize(12));
	        document.add(paragraph.setPaddingTop(RADAVST));
	        
	        document.add(new Paragraph("............................................................................................................................................").setPaddingTop(RADAVST));
	        paragraph = new Paragraph();
	        paragraph.add(new Text("Överlåter härmed lott nr "));
	        paragraph.add(new Text("" + lott.getLottnr() + " i ").setFont(specialFont).setFontSize(12)); 
	        paragraph.add(new Text(koloniNamn).setFont(specialFont).setFontSize(12)); 
	        document.add(paragraph.setPaddingTop(RADAVST));
	        
	        paragraph = new Paragraph();
	        paragraph.add(new Text("till köparen/köparna "));
	        paragraph.add(new Text(aspirant.getFnamn() + " " + aspirant.getEnamn()).setFont(specialFont).setFontSize(12)); 	         
	        document.add(paragraph.setPaddingTop(RADAVST));
	        document.add(new Paragraph("............................................................................................................................................").setPaddingTop(RADAVST));
	        
	        paragraph = new Paragraph().setFixedLeading(RADAVST + 9);
	        paragraph.add(new Text("Det fastställda och överenskomna priset är "));
	        paragraph.add(new Text(salu.getPris() + " ").setFont(specialFont).setFontSize(12)); 	
	        paragraph.add(new Text(" kr, varav "));
	        paragraph.add(new Text(salu.getPrisbyggnad() + " ").setFont(specialFont).setFontSize(12));
	        paragraph.add(new Text(" kr är\n"));
	        paragraph.add(new Text("byggnadens värde. Full likvid erläggs enligt överenskommelse mellan köpare och säljare.\n"));
	        paragraph.add(new Text("Byggnader och anläggningar överlåtes i befintligt skick."));
	        document.add(paragraph.setPaddingTop(RADAVST));
	        
	        paragraph = new Paragraph();
	        paragraph.add(new Text("Köparen/köparna tillträder lotten "));
	        paragraph.add(new Text(new SimpleDateFormat("yyyy-MM-dd").format(salu.getTilltrdatum())).setFont(specialFont).setFontSize(12)); 	
	        document.add(paragraph.setPaddingTop(RADAVST - 1));
	        
	        document.add(new Paragraph("I och med detta avtal frånträder säljaren/säljarna arrendet, och försäkrar att byggnaderna\n" +
	        		                   "inte är belånade. Samtidigt överlämnar säljaren/säljarna lottens Arrendekontrakt-Medlems-\n" +
	        		                   "avtal till köparen/köparna.").setPaddingTop(RADAVST - 1).setFixedLeading(RADAVST + 9).setPaddingBottom(20));
	        
	        
	        Table table = new Table(UnitValue.createPercentArray(new float[]{3, 1}));
            table.setWidth(UnitValue.createPercentValue(100)); // Make the table width 100% of the page		
	                        
            table.addCell(new Cell().add(new Paragraph().setMultipliedLeading(0.7f).add(new Text(koloniOrt).setFont(specialFont).setFontSize(12))).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add(new Paragraph().setMultipliedLeading(0.7f).add(new Text(saljDat).setFont(specialFont).setFontSize(12))).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add(new Paragraph().setMultipliedLeading(0.7f).add(new Text("Ort"))).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add(new Paragraph().setMultipliedLeading(0.7f).add(new Text("Datum"))).setBorder(Border.NO_BORDER));
            document.add(table.setPaddingTop(40));
	        
        
            
	        document.add(new Paragraph(".......................................................................    .....................................................................\n" +
	                   "Köpare                                                                säljare").setPaddingTop(RADAVST + 16).setFixedLeading(RADAVST + 8));
	        document.add(new Paragraph(".......................................................................    .....................................................................\n" +
	                   "Köpare                                                                säljare").setPaddingTop(RADAVST + 16).setFixedLeading(RADAVST + 8));
	        SolidLine solidLine = new SolidLine(1.6f);  // 1f is the thickness of the line
            LineSeparator lineSeparator = new LineSeparator(solidLine)
            		.setWidth(UnitValue.createPercentValue(100))  // Make the line take up 100% of the available width
                    .setStrokeColor(ColorConstants.BLACK);                       
            document.add(new Paragraph().add(lineSeparator).setPaddingTop(4).setPaddingBottom(0)
            		                    .add(new Text("Härmed godkänns försäljningen enligt ovan."))
            		                    .setFixedLeading(RADAVST + 8));
            paragraph = new Paragraph().setFixedLeading(RADAVST + 6);
	        paragraph.add(new Text(koloniNamn).setFont(specialFont).setFontSize(12));
	        paragraph.add(new Text("\n.........................................................................................................................................."));
	        paragraph.add(new Text("\nföreningens namn"));            
	        document.add(paragraph.setPaddingTop(RADAVST - 1));
	        
	        paragraph = new Paragraph().setFixedLeading(RADAVST + 6);	        
	        paragraph.add(new Text("\n.........................................................................................................................................."));
	        paragraph.add(new Text("\nordförande/behörig ställföreträdare"));            
	        document.add(paragraph.setPaddingTop(RADAVST - 1));
            document.close();
	        
	        return byteArrayOutputStream.toByteArray();
   	 	}
    	catch (Exception e) 
   	 	{
   	        throw new RuntimeException("Error generating PDF", e);
   	 	}   
    }
    public byte[] createBlankett302(int saluId)
    {
    	Tillsalu salu = saluRepo.getById(saluId);
    	Kolonilott lott = lottRepo.getByLottnr(salu.getLottnr());
    	Aspirant aspirant = aspRepo.findById(salu.getAsp());
    	Optional<Param> OptParam =  params.findByParamName("Firmatecknare");
   	    String firmaTecknare = OptParam.isPresent() ? OptParam.get().getParamValue() : "Robert Levin";
   	    OptParam =  params.findByParamName("Föreningsnamn");
	    String koloniNamn = OptParam.isPresent() ? OptParam.get().getParamValue() : "Skrubba koloniträdgårdsförening";
	    //OptParam =  params.findByParamName("Föreningsort");
	    //String koloniOrt = OptParam.isPresent() ? OptParam.get().getParamValue() : "Skrubba";
    	
	    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) 
   	 	{
    		PdfFont specialFont = PdfFontFactory.createFont("Courier-Oblique", "WinAnsi", false);
    		//PdfFont unicodeFont = PdfFontFactory.createFont("/Users/roblen/Library/Fonts/DuruSans-Regular.ttf", PdfEncodings.IDENTITY_H, true);
    		PdfFont unicodeFont = PdfFontFactory.createFont("fonts/DejaVuSans.ttf", PdfEncodings.IDENTITY_H, true);
    		PdfWriter writer = new PdfWriter(byteArrayOutputStream);
	        PdfDocument pdfDocument = new PdfDocument(writer);
	        Document document = new Document(pdfDocument);
	        createBlankettHeader(document, "302. Överlåtelse av arrenderätt till stuglott");
    		      	        
	        Paragraph paragraph = new Paragraph().setFixedLeading(RADAVST + 6);
	        paragraph.add(new Text("Arrenderätten till lott nr"));	        
	        paragraph.add(new Text(lott.getLottnr() + " ").setFont(specialFont).setFontSize(12));
	        paragraph.add(new Text("i "));
	        paragraph.add(new Text(koloniNamn + "\n").setFont(specialFont).setFontSize(12));
	        paragraph.add(new Text(".                                           (föreningsn namn)"));	      
	        document.add(paragraph.setPaddingTop(RADAVST + 30));
	        
	        paragraph = new Paragraph().setFixedLeading(RADAVST + 20);
	        paragraph.add(new Text("överlåts från och med "));	        
	        paragraph.add(new Text(new SimpleDateFormat("yyyy-MM-dd").format(salu.getSaljdatum()) + "\n").setFont(specialFont).setFontSize(12));
	        paragraph.add(new Text("till "));
	        paragraph.add(new Text(aspirant.getFnamn() + " " + aspirant.getEnamn()).setFont(specialFont).setFontSize(12));
	        document.add(paragraph.setPaddingTop(RADAVST));
	        
	        paragraph = new Paragraph().setFixedLeading(RADAVST + 20).setPaddingLeft(30);
	        paragraph.add(new Text("Personnr: ................................\n"));
	        paragraph.add(new Text("Adress: "));
	        paragraph.add(new Text(aspirant.getAdress() + "\n").setFont(specialFont).setFontSize(12));
	        paragraph.add(new Text("Postnr o -ort: "));
	        paragraph.add(new Text(aspirant.getPostnr() + " " + aspirant.getPostAdress() + "\n").setFont(specialFont).setFontSize(12));
	        document.add(paragraph.setPaddingTop(0));
	        
	        document.add(new Paragraph("som därmed på oförändrade villkor övertar arrendeavtalet, vars upplåtelse räknas från och\n" +
	        		                   "med den 1 oktober 2010.").setFixedLeading(RADAVST + 9));
	        document.add(new Paragraph("Den tillträdande arrendatorn är medveten om att Arrendenämnden i Stockholm har god-\n" +
	        		                   "känt att arrendekontraktet/medlemsavtalet innehåller avvikelser i 1 och 5 §§ från lagregler\n" +
	        		                   "om bostadsarrende.").setFixedLeading(RADAVST + 9));
	        paragraph = new Paragraph();
	        paragraph.add(new Text("Datum: "));	        
	        paragraph.add(new Text(new SimpleDateFormat("yyyy-MM-dd").format(salu.getSaljdatum()) + "\n").setFont(specialFont).setFontSize(12));	              
	        document.add(paragraph.setPaddingTop(RADAVST));
	        
	        paragraph = new Paragraph().setFixedLeading(RADAVST + 6);	        
	        paragraph.add(new Text(".......................................................................    .....................................................................\n"));	         
	        paragraph.add(new Text("Frånträdande arrendator                                       Tillträdande arrendator"));
	        document.add(paragraph.setPaddingTop(RADAVST + 20));
	        
	        document.add(new Paragraph("Ovanstående överlåtelse godkänns av koloniträdgårdsföreningen.").setPaddingTop(RADAVST + 40));
	        
	        document.add(new Paragraph("Datum ............................."));
	        document.add(new Paragraph("Föreningens firmatecknare: .................................................................................................."));
	        
	        paragraph = new Paragraph();
	        paragraph.add(new Text("Namnförtydligande: "));	        
	        paragraph.add(new Text(firmaTecknare).setFont(specialFont).setFontSize(12));	              
	        document.add(paragraph.setPaddingTop(RADAVST));
	        
	        String checkboxTom = "\u2610";   // ☐
	        paragraph = new Paragraph();
	        paragraph.add(new Text(checkboxTom).setFont(unicodeFont).setFontSize(24));
	        paragraph.add(new Text("   Arrendekontrakt/medlemsavtal i original ").setBold());
	        paragraph.add(new Text("är överlämnat till tillträdande arrendator."));
	        document.add(paragraph.setPaddingTop(RADAVST + 20));
	        
	        paragraph = new Paragraph().setFixedLeading(RADAVST + 12);
	        paragraph.add(new Text(checkboxTom).setFont(unicodeFont).setFontSize(24));
	        paragraph.add(new Text("   Tillträdande arrendator har informerats om hur föreningen tillämpar "));
	        paragraph.add(new Text("GDPR och").setBold());
	        document.add(paragraph.setPaddingTop(RADAVST).setPaddingBottom(0));
	        
	        paragraph = new Paragraph().setPaddingLeft(32).setMarginTop(0).setPaddingTop(0);
	        paragraph.add(new Text("accepterar detta.").setBold());
	        document.add(paragraph);
	        
            document.close();
	        
	        return byteArrayOutputStream.toByteArray();
   	 	}
    	catch (Exception e) 
   	 	{
   	        throw new RuntimeException("Error generating PDF", e);
   	 	}   
    }
    public byte[] createBlankett303(int saluId)
    {    
    	 Tillsalu salu     = saluRepo.getById(saluId);
    	 Kolonilott lott   = lottRepo.getByLottnr(salu.getLottnr());
    	 Aspirant aspirant = aspRepo.findById(salu.getAsp());
    	 String saljDat  = new SimpleDateFormat("yyyy-MM-dd").format(salu.getSaljdatum());
    	 Optional<Param> OptParam =  params.findByParamName("Firmatecknare");
    	 String firmaTecknare = OptParam.isPresent() ? OptParam.get().getParamValue() : "Robert Levin";
    	 OptParam =  params.findByParamName("Föreningsnamn");
 	     String koloniNamn = OptParam.isPresent() ? OptParam.get().getParamValue() : "Skrubba koloniträdgårdsförening";
 	     OptParam =  params.findByParamName("Föreningsort");
 	     String koloniOrt = OptParam.isPresent() ? OptParam.get().getParamValue() : "Skrubba";
    	 
    	 try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) 
    	 {
    		    PdfFont specialFont = PdfFontFactory.createFont("Courier-Oblique", "WinAnsi", false);
    	        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
    	        PdfDocument pdfDocument = new PdfDocument(writer);
    	        Document document = new Document(pdfDocument);
    	        createBlankettHeader(document, "303. Ansökan om medlemskap och arrenderätt");
    	       
    	        Paragraph paragraph = new Paragraph().setPaddingTop(20);
    	        paragraph.add(new Text("Till styrelsen för\n"));
    	        paragraph.add(new Text(koloniNamn).setFont(specialFont).setFontSize(12)); 
                document.add(paragraph);
                
                document.add(new Paragraph("Jag anhåller härmed om medlemskap i koloniträdgårdsföreningen").setPaddingTop(10));
                
                paragraph = new Paragraph();
    	        paragraph.add(new Text("samt att få överta arrenderätten till lott nr"));
    	        paragraph.add(new Text("" + lott.getLottnr()).setFont(specialFont).setFontSize(12)); 
    	        document.add(paragraph.setPaddingTop(5));
    	        
    	        document.add(new Paragraph("Jag förbinder mig att följa föreningens stadgar, ordningsföreskrifter, stadgeenliga\nbeslut och bestämmelserna i arrendekontraktet.").setPaddingTop(5));
    	        
    	        paragraph = new Paragraph();
    	        paragraph.add(new Text("Jag är myndig och folkbokförd i en av de kommuner som anges i arrendeavtalet.\n"));
    	        paragraph.add(new Text("(Botkyrka, Danderyd, Ekerö, Haninge, Huddinge, Järfälla, Lidingö, Nacka, Sollentuna, Solna, Stockholm, Sundbyberg,\n"
    	        		+ "Tyresö och Täby.)").setFontSize(9)); 
    	        document.add(paragraph.setPaddingTop(5));
    	        
    	        Table table = new Table(UnitValue.createPercentArray(new float[]{3, 1}));
                table.setWidth(UnitValue.createPercentValue(100)); // Make the table width 100% of the page		
    	                        
                table.addCell(new Cell().add(new Paragraph("Ort: ").add(new Text(koloniOrt).setFont(specialFont).setFontSize(12))).setBorder(Border.NO_BORDER));
                table.addCell(new Cell().add(new Paragraph("Datum:").add(new Text(saljDat).setFont(specialFont).setFontSize(12))).setBorder(Border.NO_BORDER));
                document.add(table.setPaddingTop(30));
    	        /*
    	        paragraph = new Paragraph();
                paragraph.add(new Text("Ort: "));
                paragraph.add(new Text(koloniOrt).setFont(specialFont).setFontSize(12)); 
                paragraph.add(new Text("......................................................................................Datum: "));
                paragraph.add(new Text(saljDat).setFont(specialFont).setFontSize(12));
                document.add(paragraph.setPaddingTop(10));
    	        */
                document.add(new Paragraph("Namnteckning:........................................................................................................................").setPaddingTop(10));
                
                paragraph = new Paragraph();
                paragraph.add(new Text("Namnförtydligande: "));
                paragraph.add(new Text(aspirant.getFnamn() + " " + aspirant.getEnamn()).setFont(specialFont).setFontSize(12));
                document.add(paragraph.setPaddingTop(10));
                
                paragraph = new Paragraph();
                paragraph.add(new Text("Personnr:....................................................... "));
                paragraph.add(new Text("Tel/mobil: "));
                paragraph.add(new Text(aspirant.getTelefon()).setFont(specialFont).setFontSize(12));
                document.add(paragraph.setPaddingTop(10));
                
                paragraph = new Paragraph();	       
    	        paragraph.add(new Text("Adress: "));
    	        paragraph.add(new Text(aspirant.getAdress() + "\n").setFont(specialFont).setFontSize(12));
    	        document.add(paragraph.setPaddingTop(10));
    	        
    	        paragraph = new Paragraph();
    	        paragraph.add(new Text("Postnr o -ort: "));
    	        paragraph.add(new Text(aspirant.getPostnr() + " " + aspirant.getPostAdress()).setFont(specialFont).setFontSize(12));
    	        document.add(paragraph.setPaddingTop(10));
    	        
    	        paragraph = new Paragraph();
    	        paragraph.add(new Text("E-postadress: "));
    	        paragraph.add(new Text(aspirant.getEmail()).setFont(specialFont).setFontSize(12));
    	        document.add(paragraph.setPaddingTop(10));
    	        
    	        SolidLine solidLine2 = new SolidLine(2f);  // 1f is the thickness of the line
                LineSeparator lineSeparator2 = new LineSeparator(solidLine2)
                		.setWidth(UnitValue.createPercentValue(100))  // Make the line take up 100% of the available width
                        .setStrokeColor(ColorConstants.BLACK);                       
                document.add(new Paragraph().add(lineSeparator2).setPaddingTop(0));
    	        
                paragraph = new Paragraph();
                paragraph.add(new Text("Inkom till styrelsen den...................................................... "));
                document.add(paragraph.setPaddingTop(0));
                
                paragraph = new Paragraph();
    	        paragraph.add(new Text("Styrelsens beslut: "));
    	        paragraph.add(new Text("Tillstyrks ").setFont(specialFont).setFontSize(12));
    	        paragraph.add(new Text("..........................................................................................."));
    	        document.add(paragraph.setPaddingTop(5));
                
    	        table = new Table(UnitValue.createPercentArray(new float[]{3, 1}));
                table.setWidth(UnitValue.createPercentValue(100)); // Make the table width 100% of the page		
    	                        
                table.addCell(new Cell().add(new Paragraph("Ort: ").add(new Text(koloniOrt).setFont(specialFont).setFontSize(12))).setBorder(Border.NO_BORDER));
                table.addCell(new Cell().add(new Paragraph("Datum:  .....................")).setBorder(Border.NO_BORDER));
                document.add(table.setPaddingTop(30));
    	       
                document.add(new Paragraph("Föreningens firmatecknare.....................................................................................................").setPaddingTop(5));
    	       
                paragraph = new Paragraph();
                paragraph.add(new Text("Namnförtydligande: "));
                paragraph.add(new Text(firmaTecknare).setFont(specialFont).setFontSize(12));                                
                document.add(paragraph);
                
                document.close();
    	        return byteArrayOutputStream.toByteArray();
    	 } 
    	 catch (Exception e) 
    	 {
    	        throw new RuntimeException("Error generating PDF", e);
    	 }    	
    }
}
