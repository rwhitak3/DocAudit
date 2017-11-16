package edu.kennesaw.cs4850.docaudit.ingest;

import edu.kennesaw.cs4850.docaudit.model.Document;

import java.io.File;

/**
 *
 * @author rwhitak3
 */
public interface ReadPDF {
    public String readPDF( File inputFile);
    public Document createDocumentFromPDF (File inputFile);
}
