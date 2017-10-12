package edu.kennesaw.cs4850.docaudit.ingest;

import java.io.File;
import java.net.URL;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.slf4j.LoggerFactory;

/**
 *
 * @author rwhitak3
 */
public class ReadPDFPDFBoxTest {
    private org.slf4j.Logger logger = LoggerFactory.getLogger(ReadPDFPDFBoxTest.class);
    
    @Test
    public void ReadPDFPDFBoxTest() {
        logger.info("Reading Text PDF");
        ReadPDFPDFBox p = new ReadPDFPDFBox();
        URL u = this.getClass().getResource("/fw4.pdf");
        File f = new File(u.getFile());
        String out = p.readPDF(f);
        //Read the output of the w4 form
        assertTrue("Error Reading Text Form W-4", out.startsWith("Form W-4 (2017)"));
        //logger.debug("Text = " + out);
        logger.info("Reading Image PDF");
        ReadPDFPDFBox p1 = new ReadPDFPDFBox();
        URL u2 = this.getClass().getResource("/fw4-image.pdf");
        File f2 = new File(u2.getFile());
        String out2 = p.readPDF(f2);
        assertTrue("Error Reading image Form W-4", out2.startsWith("The exceptions don't ply"));
        
        
    }
}
