package edu.kennesaw.cs4850.docaudit;

import edu.kennesaw.cs4850.docaudit.compare.CompareDocs;
import edu.kennesaw.cs4850.docaudit.compare.CompareDocsImpl;
import edu.kennesaw.cs4850.docaudit.compare.MatchScoresComparator;
import edu.kennesaw.cs4850.docaudit.ingest.ReadPDF;
import edu.kennesaw.cs4850.docaudit.ingest.ReadPDFPDFBox;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import edu.kennesaw.cs4850.docaudit.model.*;
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
    @Option(name="--directory", usage="Sets a directory to read")
    private String directoryName;
    
    @Option(name="--file", usage="Sets a file to read")
    private String fileName;

    @Option(name="--learn", usage="Learn New Docs Mode")
    private boolean learn;

    @Option(name="--img", usage="Make image file")
    private boolean imgTest;

    @Option(name="--savefile", usage="Save File location, defaults to temporary location")
    private String saveFile;

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

        boolean tryLoad = false;
        // Setup save file if present
        if (saveFile == null && learn) {
            Path dir;
            File sFile;
            try {
                dir = Files.createTempDirectory("DocAudit");
                sFile = new File(dir.toFile(), "DocAudit.savefile");
                saveFile = sFile.getAbsolutePath();
            } catch (IOException e) {
                logger.error("Unable to create learned documents file, unable to continue", e);
                return;
            }
        } else if (saveFile != null) {
            File sFile = new File(saveFile);
            if (learn) {
                if (sFile.exists() && sFile.isFile()){
                    tryLoad = true;
                } else {
                    sFile.getParentFile().mkdirs();
                }
            } else {
                if (!sFile.exists()) {
                    logger.error("Specifiy a valid file name for the save file");
                    parser.printUsage(System.err);
                    return;
                }
                if (!sFile.isFile()) {
                    logger.error("Specifiy a valid file name for the save file");
                    parser.printUsage(System.err);
                    return;
                }
                tryLoad = true;
            }
        } else {
            logger.error("You must specify a save file, or the --learn option");
            parser.printUsage(System.err);
            return;
        }

        /* Load saved file data if present */
        SaveFile.setFileName(saveFile);
        Docs nuDocs = null;
        if ( tryLoad) {
            logger.debug("Trying to load Docs");
            nuDocs = SaveFile.load();
        }

        if ( nuDocs != null ) {
            docs = nuDocs;
            for ( Document d : docs.getDocumentList()) {
                   logger.debug("Loaded " + d.getName());
            }
        } else {
            if ( this.learn != true) {
                logger.error("Unable to load already learned documents file.  Did you mean to --learn?");
                parser.printUsage(System.err);
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
            parser.printUsage(System.err);
            return;
        }

        // Start reading the files
        for ( File f : files) {
            if (this.imgTest) {
                if (docs.getDocumentList() == null) {
                    docs.setDocumentList(new LinkedList<Document>());
                }
                try {
                    Document doc = pdfReader.createDocumentFromPDF(f);
                    if (this.learn) {
                        docs.getDocumentList().add(doc);
                    } else {
                        CompareDocs cmp = new CompareDocsImpl();
                        cmp.setImageMode(this.imgTest);

                        LinkedList<MatchScore> scores = new LinkedList<>();

                        for (Document compareTo : docs.getDocumentList()) {

                            MatchScore m = cmp.compare(doc, compareTo);
                            if ( m.getScore() != null) {
                                scores.add(m);
                            }
                        }


                        MatchScoresComparator matchScoresComparator = new MatchScoresComparator();
                        //Sort
                        scores.sort(matchScoresComparator);

                        logger.debug("Found possible Matches:");
                        for ( MatchScore m : scores) {
                            logger.debug("Possible Match: " + m.getDoc().getName() + ":" + m.getScore() );
                            for (PageScore ps : m.getPageScores()) {
                                logger.debug("Page: " + ps.getSourcePage().getPageNumber() + " -> " + ps.getMatchPage().getPageNumber() + " Score:" + ps.getScore());
                            }
                        }

                        if ( scores.size() > 0) {
                            MatchScore best = scores.getFirst();
                            if ( best.getScore() == null ) {
                                logger.info(doc.getName() + ": No Match Found");
                                return;
                            }
                            if ( best.getScore() <= 0.40 ) {
                                logger.info(doc.getName() + ": No match found, Closest Match: " + best.getDoc().getName() + " Score:" + best.getScore()*100 + "%");
                            } else if ( best.getScore() <= 0.60 ) {
                                logger.info(doc.getName() + ": Unlikely match found, Closest Match: " + best.getDoc().getName() + " Score:" + best.getScore()*100 + "%");
                            } else if ( best.getScore() <= 0.80) {
                                logger.info(doc.getName() + ": Possible match found, Closest Match: " + best.getDoc().getName() + " Score:" + best.getScore()*100 + "%");
                            } else if ( best.getScore() <= 0.90) {
                                logger.info(doc.getName() + ": Likely match found: " + best.getDoc().getName() + " Score:" + best.getScore()*100 + "%");
                            } else if ( best.getScore() <= 0.95) {
                                logger.info(doc.getName() + ": Very Likely match found: " + best.getDoc().getName() + " Score:" + best.getScore()*100 + "%");
                            } else if ( best.getScore() > 0.95) {
                                logger.info(doc.getName() + ": Match found: " + best.getDoc().getName() + " Score:" + best.getScore()*100 + "%");
                            }
                        }
                    }

                } catch (Exception e) {
                        logger.error("Error reading document", e);
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
            logger.info("Saved documents to save file=" + saveFile);
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
