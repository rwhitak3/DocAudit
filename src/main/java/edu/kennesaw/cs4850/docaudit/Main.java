package edu.kennesaw.cs4850.docaudit;

import edu.kennesaw.cs4850.docaudit.compare.CompareDocs;
import edu.kennesaw.cs4850.docaudit.compare.CompareDocsImpl;
import edu.kennesaw.cs4850.docaudit.ingest.ReadPDF;
import edu.kennesaw.cs4850.docaudit.ingest.ReadPDFPDFBox;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import edu.kennesaw.cs4850.docaudit.model.Document;
import edu.kennesaw.cs4850.docaudit.model.Page;
import edu.kennesaw.cs4850.docaudit.save.SaveFile;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;

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

    @Option(name="-img", usage="Make image file")
    private boolean imgTest;

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

            logger.info("Trying to load Docs");
            /*if (docs.documentList != null ) {
                logger.info("Found Documents");
                for (Document d : docs.documentList ) {
                    logger.debug("Name:" + d.getName());
                    logger.debug("File Name:" + d.getFileName());
                    if (d.getPages() != null) {
                        logger.debug("Found Pages!");
                        for (Page p : d.getPages()) {
                            logger.debug("Found Page:" + p.getPageNumber());
                            BufferedImage b = p.getImgContents();
                            if (b != null) {
                                String fileName = d.getName() + "-" + Integer.toString(p.getPageNumber()) + ".png";
                                logger.info("Writing out File:" + fileName);
                                File outputfile = new File(fileName);
                                try {
                                    ImageIO.write(b, "png", outputfile);
                                } catch (IOException e) {
                                    logger.error("Failed to write out image:", e);
                                }
                            }
                        }
                    }
                }
            }*/
        } else {
            if ( this.learn != true) {
                logger.error("Unable to load already learned documents file.  Did you mean to -learn?");
                return;
            }
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
            if (this.imgTest) {
                if (docs.getDocumentList() == null) {
                    docs.setDocumentList(new LinkedList<Document>());
                }
                Document doc = pdfReader.createDocumentFromPDF(f);
                if (this.learn) {
                    docs.getDocumentList().add(doc);
                } else {
                    CompareDocs cmp = new CompareDocsImpl();
                    Document highestDoc;
                    for (Page p : doc.getPages()) {
                        logger.debug("Reading Page: " + p.getPageNumber());
                        Double highestScore = 0.00;
                        Page highestPage = null;
                        for (Document d : docs.getDocumentList()) {
                            logger.debug("Comparing to document:" + d.getName());
                            for (Page p2 : d.getPages()) {
                                Double score = cmp.compare(p.getImgContents(), p2.getImgContents());
                                if (score > highestScore) {
                                    highestPage = p2;
                                    highestScore = score;
                                }
                                logger.debug("Image Compare Score Page " + p.getPageNumber() + " to page: " + p2.getPageNumber() + ": " + score);
                            }
                        }
                        if (highestScore > 0.00) {
                            logger.info("Highest score:" + highestScore + " Found on " + highestPage.getParentDocument().getName() + " page: " + highestPage.getPageNumber());
                        }

                        if (highestScore >= 0.98) {
                            logger.info("Found Match to: "+ highestPage.getParentDocument().getName()+ ":" + highestPage.getPageNumber() + " score: " +highestScore +  " (Perfect Score 1.0)");
                        }
                    }
        }

            } else {
                String text = pdfReader.readPDF(f);
                if (learn) {
                    if (docs.getDocs() == null) {
                        docs.setDocs(new HashMap<String,String>());
                    }
                    docs.getDocs().put(f.getName(), text);
                }
                CompareDocs cmp = new CompareDocsImpl();
                for (String s : docs.getDocs().values()) {
                    Double output = cmp.compare(s, text);
                    logger.debug("Compare Text score is " + output);
                }
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
        if (docs.getDocs() == null) {
            docs.setDocs(new HashMap<String, String>());
        }
        for (int i = 0; i < 24; i++) {
            docs.getDocs().put(String.valueOf((char)(65+i)), Integer.toString(i));
        }
        SaveFile.save(docs);
    }

    public void load() {
        Docs d = new Docs();
        d=SaveFile.load();
        logger.info("Found");
        for ( String s : d.getDocs().values()) {
            logger.info("Found: " + s);
        }
    }

    public static void main(final String[] args) {
     
        final Main mainObj = new Main();

        mainObj.audit(args);
    }
}
