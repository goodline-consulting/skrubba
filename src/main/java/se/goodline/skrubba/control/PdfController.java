package se.goodline.skrubba.control;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.PdfMerger;

import se.goodline.skrubba.model.Aspirant;
import se.goodline.skrubba.model.Tillsalu;
import se.goodline.skrubba.repository.AspirantRepository;
import se.goodline.skrubba.repository.KoloniLottRepository;
import se.goodline.skrubba.repository.TillSaluRepository;
import se.goodline.skrubba.service.EmailService;
import se.goodline.skrubba.service.PdfService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.servlet.http.HttpServletRequest;

@RestController
public class PdfController 
{
	@Autowired
	private AspirantRepository aspRepo;
	
	@Autowired
	KoloniLottRepository lottRepo;
	
	@Autowired
	TillSaluRepository saluRepo;
    private final PdfService pdfService;

    public PdfController(PdfService pdfService) 
    {
        this.pdfService = pdfService;
    }

    @GetMapping("/generate-pdf/{id}")
    public ResponseEntity<byte[]> generatePdf(@PathVariable int id, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) 
    {
    	
    	try 
    	{
    		Tillsalu salu = saluRepo.getById(id);
    		Aspirant asp = aspRepo.findById(salu.getAsp());
    		if (asp == null) 
    		{
    	            // Om posten inte finns, sätt ett felmeddelande och omdirigera tillbaka
    	            redirectAttributes.addFlashAttribute("errorMessage", salu.getSaldtill() + " har tagits bort ifrån kön så uppgifter för att skapa blanketter saknas!");
    	            String referer = request.getHeader("Referer"); // Hämta föregående URL
    	            return ResponseEntity.status(HttpStatus.FOUND).header("Location", referer).build();
    	    }
	    	byte[] pdf300 = pdfService.createBlankett300(id);        
	    	byte[] pdf301 = pdfService.createBlankett301(id);
	    	byte[] pdf302 = pdfService.createBlankett302(id);        
	    	byte[] pdf303 = pdfService.createBlankett303(id);
	    	
	    	ByteArrayOutputStream combinedPdfStream = new ByteArrayOutputStream();
	        PdfDocument combinedPdfDocument = new PdfDocument(new PdfWriter(combinedPdfStream));
	
	        PdfMerger merger = new PdfMerger(combinedPdfDocument);
	        PdfDocument pdfDocument1 = new PdfDocument(new PdfReader(new ByteArrayInputStream(pdf300)));
	        PdfDocument pdfDocument2 = new PdfDocument(new PdfReader(new ByteArrayInputStream(pdf301)));
	        PdfDocument pdfDocument3 = new PdfDocument(new PdfReader(new ByteArrayInputStream(pdf302)));
	        PdfDocument pdfDocument4 = new PdfDocument(new PdfReader(new ByteArrayInputStream(pdf303)));
	
	        merger.merge(pdfDocument1, 1, pdfDocument1.getNumberOfPages());
	        merger.merge(pdfDocument2, 1, pdfDocument2.getNumberOfPages());
	        merger.merge(pdfDocument3, 1, pdfDocument3.getNumberOfPages());
	        merger.merge(pdfDocument4, 1, pdfDocument4.getNumberOfPages());
	
	        combinedPdfDocument.close();
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_PDF);
	        headers.setContentDispositionFormData("attachment", "blanketter_ägarbyte_lott_" + salu.getLottnr() + ".pdf");
	
	        return ResponseEntity.ok()
	                .headers(headers)
	                .body(combinedPdfStream.toByteArray());
    	}
    	catch (Exception e) 
    	{
    		  e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}
