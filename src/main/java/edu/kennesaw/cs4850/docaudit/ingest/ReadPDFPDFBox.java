package edu.kennesaw.cs4850.docaudit.ingest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.LoggerFactory;

/**
 *
 * @author rwhitak3
 */
public class ReadPDFPDFBox implements ReadPDF {
    private org.slf4j.Logger logger = LoggerFactory.getLogger(ReadPDFPDFBox.class);
    private PDFParser parser;
    private PDFTextStripper pdfStripper;
    private PDDocument pdDoc ;
    private COSDocument cosDoc ;
   
    @Override
    public String readPDF(File inputFile) {
        
        try {
            this.parser = new PDFParser(new RandomAccessFile(inputFile,"r")); // update for PDFBox V 2.0
            
            parser.parse();
            cosDoc = parser.getDocument();
            pdfStripper = new PDFTextStripper();
            pdDoc = new PDDocument(cosDoc);
            pdfStripper.setStartPage(1);
            pdfStripper.setEndPage(pdDoc.getNumberOfPages());
            
            String output = pdfStripper.getText(pdDoc);
            logger.debug("Output: " + output);
            return output;
            
        } catch (FileNotFoundException ex) {
            logger.error("File Not found", ex);
            return null;
        } catch (IOException ex) {
            logger.error("IO Error:", ex);
            return null;
        }
    }
    
}
