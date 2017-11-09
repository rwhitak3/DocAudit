package edu.kennesaw.cs4850.docaudit;

import edu.kennesaw.cs4850.docaudit.compare.CompareDocs;
import edu.kennesaw.cs4850.docaudit.compare.CompareDocsImpl;
import edu.kennesaw.cs4850.docaudit.ingest.ReadPDF;
import edu.kennesaw.cs4850.docaudit.ingest.ReadPDFPDFBox;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import edu.kennesaw.cs4850.docaudit.save.SaveFile;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.slf4j.LoggerFactory;
/**
 *
 * @author rwhitak3
 */
public class Main {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(Main.class);
    private static Docs docs = new Docs();
    @Option(name="-directory", usage="Sets a directory to read")
    private String directoryName;
    
    @Option(name="-file", usage="Sets a file to read")
    private String fileName;

    @Option(name="-l", usage="Test Loading Data")
    private boolean loadTest;

    @Option(name="-s", usage="Test Saving Data")
    private boolean saveTest;

    @Option(name="-learn", usage="Learn New Docs Mode")
    private boolean learn;
    
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
       

        if ( this.loadTest || this.saveTest) {
            if (this.loadTest && this.saveTest) {
                logger.error("Both load and save cannot be used");
                return;
            }
            if (this.loadTest) {
                logger.debug("Testing loading");
                this.load();
            }
            if (this.saveTest) {
                logger.debug("Testing Saving");
                this.save();
            }
            return;
        }

        Docs nuDocs = SaveFile.load();
        if ( nuDocs != null ) {
            docs = nuDocs;
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
            String text = pdfReader.readPDF(f);
            if (learn) {
                if (docs.docs == null) {
                    docs.docs = new HashMap<>();
                }
                docs.docs.put(f.getName(), text);
            }
            CompareDocs cmp = new CompareDocsImpl();
            for (String s: docs.docs.values())  {
                Double output = cmp.compare(s, text);
                logger.debug("Compare Text score is " + output);
            }
        }

        if (this.learn) {
        SaveFile.save(docs);
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
    public void save() {
        //Docs d = new Docs();
        if (docs.docs == null) {
            docs.docs = new HashMap<>();
        }
        for (int i = 0; i < 24; i++) {
            docs.docs.put(String.valueOf((char)(65+i)), Integer.toString(i));
        }
        SaveFile.save(docs);
    }

    public void load() {
        Docs d = new Docs();
        d=SaveFile.load();
        logger.info("Found");
        for ( String s : d.docs.values()) {
            logger.info("Found: " + s);
        }
    }

    public static void main(final String[] args) {
     
        final Main mainObj = new Main();

        mainObj.audit(args);
    }
}
