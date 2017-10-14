package edu.kennesaw.cs4850.docaudit;

import edu.kennesaw.cs4850.docaudit.ingest.ReadPDF;
import edu.kennesaw.cs4850.docaudit.ingest.ReadPDFPDFBox;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.slf4j.LoggerFactory;
/**
 *
 * @author rwhitak3
 */
public class Main {

    private org.slf4j.Logger logger = LoggerFactory.getLogger(Main.class);
    @Option(name="-directory", usage="Sets a directory to read")
    private String directoryName;
    
    @Option(name="-file", usage="Sets a file to read")
    private String fileName;
    
    public String getDirectoryName() {
        return directoryName;
    }

    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public void audit(String[] args) {
        final CmdLineParser parser = new CmdLineParser(this);  
        if (args.length < 1)  
        {  
           parser.printUsage(System.out);
           return;  
        }  
         try  
        {  
           parser.parseArgument(args);  
        }  
        catch (CmdLineException ex)  
        {  
           logger.debug("ERROR: Unable to parse command-line options: ", ex);  
        }  
       
        
        ReadPDF pdfReader = new ReadPDFPDFBox();
        List<File> files = new LinkedList<>();
        
        if ( this.directoryName != null && this.directoryName.length() > 0) {
            //Good directory String, lets try that
            files.addAll(getFilesInDirectory(this.directoryName));
        } else if (this.fileName != null && this.fileName.length() > 0 ) {
            files.add(new File(this.fileName));
        } else {
            logger.error("No files specified");
            return;
        }
        for ( File f : files) {
            pdfReader.readPDF(f);
        }

    }
    
    private List<File> getFilesInDirectory(String dirName) {
        File directory = new File(dirName);
        return getFilesInDirectory(directory);
    }
    
    private List<File> getFilesInDirectory(File dir) {
        try {
            logger.debug("Reading Directory" + dir.getCanonicalPath());
            LinkedList<File> output = new LinkedList<>();
            
            if ( dir.isDirectory() ) {
                for ( File f : dir.listFiles() ) {
                    if ( f.isDirectory() )  {
                        List<File> files = getFilesInDirectory(f);
                        if ( files != null ) {
                            output.addAll(files);
                        }
                    } else if ( f.isFile() ) {
                        output.add(f);
                    }
                }
            } else {
                logger.debug(dir.getAbsolutePath() + " is not a directory");
            }
            return output;
        } catch (IOException ex) {
            logger.debug("IO Error reading file", ex);
            return null;
        }
        
    }
    public static void main(final String[] args) {
     
        final Main mainObj = new Main();
        mainObj.audit(args);
    }
}
