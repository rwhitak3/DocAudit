package edu.kennesaw.cs4850.docaudit.ingest;

import edu.kennesaw.cs4850.docaudit.ingest.ReadPDFPDFBox;
import java.io.File;
import java.net.URL;
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
        ReadPDFPDFBox p = new ReadPDFPDFBox();
        URL u = this.getClass().getResource("/fw4.pdf");
        File f = new File(u.getFile());
        String out = p.readPDF(f);
        //Read the output of the w4 form
        assert(out.startsWith("Form W-4 (2017)"));
        //logger.debug("Text = " + out);
    }
}
