package edu.kennesaw.cs4850.docaudit.compare;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import edu.kennesaw.cs4850.docaudit.model.PiecePosition;
import org.languagetool.JLanguageTool;
import org.languagetool.language.AmericanEnglish;
import org.languagetool.rules.RuleMatch;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.slf4j.LoggerFactory;

public class CompareDocsImpl implements  CompareDocs{
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(CompareDocsImpl.class);
    private static JLanguageTool langTool = new JLanguageTool(new AmericanEnglish());
    static {
        nu.pattern.OpenCV.loadLibrary();
        System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);

    }

    @Override
    public Double compare(String a, String b) {
        //QGram dig = new QGram(2);
        //return dig.distance(a,b);

        List<String> stringsA = Arrays.asList(a.split("\\s"));
        List<String> stringsB = Arrays.asList(b.split("\\s"));
        LinkedList<String> outA = new LinkedList<>();
        LinkedList<String> outB = new LinkedList<>();
        for (String s : stringsA) {
            try {
                String n = spellCheck(s);
                outA.add(n);
            } catch (IOException e) {
                logger.error("Fail", e);
            }

        }
        for (String s : stringsB) {
            try {
                String n = spellCheck(s);
                outB.add(n);
            } catch (IOException e) {
                logger.error("Fail", e);
            }

        }

        LinkedList<String> notInA = new LinkedList<>();
        LinkedList<String> notInB = new LinkedList<>();
        //Intersection
        LinkedList<String> intersection = new LinkedList<>();

        for (String s : outA) {
            if ( outB.contains(s) ) {
                intersection.add(s);
            } else {
                notInB.add(s);
            }
        }

         for (String s : outB) {
            if ( outA.contains(s) ) {
                intersection.add(s);
            } else {
                notInA.add(s);
            }
        }
        logger.debug("Intesection:" + intersection.size());
        logger.debug("Not in A:" + notInA.size());
        logger.debug("Not in B:" + notInB.size());
        return Double.valueOf(intersection.size()/(outA.size()+outB.size()));
     }

    private String spellCheck(String input) throws IOException {
        List<RuleMatch> matches = langTool.check(input);
        logger.debug("Input String = " + input);
        for (RuleMatch m : matches) {
            logger.debug("Input String = " + input);
            if (m.getSuggestedReplacements().size() > 0) {
                String before = input.substring(0, m.getFromPos());
                String after = "";
                if (input.length() > m.getToPos()) {
                    input.substring(m.getToPos() + 1, input.length());
                }
                String output = before + m.getSuggestedReplacements().get(0).toLowerCase() + after;
                logger.debug("Output String = " + output);
                input = output;
            }
        }
        return input;
    }

    @Override
    public Double compare (BufferedImage a, BufferedImage b) {

        double hist = compareHistogram(a, b);
        double template = 0.00;
        if ( hist > .8) {
            template = compareTemplate(a, b);
        }
        return (hist*.2)+(template*.8);
    }

    public Double compareTemplate(BufferedImage a, BufferedImage b) {

        /* The idea here is that we need to take the source image and break it into pieces, take each piece and see if we can find where it goes
            most likely we can skip pieces, and compute our matches.  If say we get above a certain threshold, we can call it a match
         */

        try {
            Mat matA = bufferedImageToMat(a);

            List<PiecePosition> Atemplates = matToPieces(matA, 9);
            Mat matB = bufferedImageToMat(b);

            double totalDist =  0.00;
            for ( PiecePosition p : Atemplates) {

                Core.MinMaxLocResult res = findTemplate(p.getImg(), matB);
                logger.debug("Expecting around: (" + p.getX() + "," + p.getY()+")");
                logger.debug("Found result for Template at(" + res.maxLoc.x + "," + res.maxLoc.y + ")");

                double hDist = p.getX()-res.maxLoc.x;
                double vDist = p.getY()-res.maxLoc.y;

                totalDist += Math.sqrt((hDist*hDist)+(vDist*vDist));
            }

            //Perfect score would be 0
            // i.e all chunks found where we expect them to be
            // totalDist/perimiter of image
            double averageDiff = (totalDist/9);
            double worst = Math.sqrt((matB.height()*matB.height()) + (matB.width()+matB.width()));
            double score = 1-(averageDiff/worst);
            logger.debug("Total Score: " + score);
            return score;
        } catch (Exception ex) {
            logger.error("Error occurred while trying to compare Hisograms", ex);
            return null;
        }
    }

    /* examples from https://stackoverflow.com/questions/17001083/opencv-template-matching-example-in-android */
    private Core.MinMaxLocResult findTemplate(Mat template, Mat input) {
        // / Create the result matrix
        int result_cols = input.cols() - template.cols() + 1;
        int result_rows = input.rows() - template.rows() + 1;
        Mat result = new Mat(result_rows, result_cols, CvType.CV_32FC1);

        // / Do the Matching and Normalize
        Imgproc.matchTemplate(input, template, result, Imgproc.TM_CCOEFF);
        Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());

        // / Localizing the best match with minMaxLoc
        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
        return mmr;
    }




    private List<PiecePosition> matToPieces(Mat input, int pieces) {
        int rows = input.rows();
        int cols = input.cols();

        int rowsPer = rows/pieces;
        int colsPer = cols/pieces;

        LinkedList<PiecePosition> outputs = new LinkedList<>();
        int lastcol = 0;
        int lastrow = 0;

        int posX = 0;
        int posY = 0;

        for (int i =0; i < pieces; i++ ) {
            Mat out = input.submat(lastrow,lastrow+rowsPer, lastcol,lastcol+colsPer);
            lastcol = lastcol+colsPer;
            lastrow = lastrow+rowsPer;

            PiecePosition thisChunk = new PiecePosition();
            thisChunk.setImg(out);
            thisChunk.setHeight(out.height());
            thisChunk.setWidth(out.width());
            thisChunk.setX(posX);
            thisChunk.setY(posY);

            posX+=out.width();
            posY+=out.height();

            outputs.add(thisChunk);
        }

        return outputs;
    }

    /* Found: http://4answered.com/questions/view/deb75f/Comparing-pictures-using-Android-OpenCV */
    public Double compareHistogram(BufferedImage a, BufferedImage b) {
        try {
            Mat matA = bufferedImageToMat(a);
            Mat matB = bufferedImageToMat(b);



            int hist_bins = 30;           //number of histogram bins
            int hist_range[]= {0,180};//histogram range

            Mat hist0 = new Mat();
            Mat hist1 = new Mat();

            MatOfFloat ranges = new MatOfFloat(0f, 256f);
            MatOfInt histSize = new MatOfInt(25);

            Imgproc.calcHist(Arrays.asList(matA), new MatOfInt(0), new Mat(), hist0, histSize, ranges);
            Imgproc.calcHist(Arrays.asList(matB), new MatOfInt(0), new Mat(), hist1, histSize, ranges);

            return Imgproc.compareHist(hist0, hist1, Imgproc.CV_COMP_CORREL);
        } catch (Exception ex) {
            logger.error("Error occurred while trying to compare Hisograms",ex);
            return null;
        }
    }

    /* Found: https://stackoverflow.com/questions/14958643/converting-bufferedimage-to-mat-in-opencv */
    private Mat bufferedImageToMat(BufferedImage input) {
        input = toBufferedImageOfType(input,BufferedImage.TYPE_3BYTE_BGR );
        Mat output = new Mat(input.getHeight(), input.getWidth(), CvType.CV_8UC3);
        byte[] data = ((DataBufferByte) input.getRaster().getDataBuffer()).getData();
        output.put(0, 0, data);
        return output;
    }


    /* Found: https://stackoverflow.com/questions/21740729/converting-bufferedimage-to-mat-opencv-in-java */
    private static BufferedImage toBufferedImageOfType(BufferedImage original, int type) {
        if (original == null) {
            throw new IllegalArgumentException("original == null");
        }

        // Don't convert if it already has correct type
        if (original.getType() == type) {
            return original;
        }

        // Create a buffered image
        BufferedImage image = new BufferedImage(original.getWidth(), original.getHeight(), type);

        // Draw the image onto the new buffer
        Graphics2D g = image.createGraphics();
        try {
            g.setComposite(AlphaComposite.Src);
            g.drawImage(original, 0, 0, null);
        }
        finally {
            g.dispose();
        }

        return image;
    }
}
