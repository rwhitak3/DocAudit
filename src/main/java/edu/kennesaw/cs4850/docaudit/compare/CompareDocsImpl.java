package edu.kennesaw.cs4850.docaudit.compare;
import info.debatty.java.stringsimilarity.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.Arrays;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;
import org.slf4j.LoggerFactory;

import static org.opencv.imgproc.Imgproc.TM_CCOEFF_NORMED;

public class CompareDocsImpl implements  CompareDocs{
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(CompareDocsImpl.class);
    static {
        nu.pattern.OpenCV.loadLibrary();
        System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
    }

    @Override
    public Double compare(String a, String b) {
        QGram dig = new QGram(2);
        return dig.distance(a,b);
    }


    @Override
    public Double compare (BufferedImage a, BufferedImage b) {
        return compareHistogram(a, b);
    }

    public Double compareTemplate(BufferedImage a, BufferedImage b) {

        /* The idea here is that we need to take the source image and break it into pieces, take each piece and see if we can find where it goes
            most likely we can skip pieces, and compute our matches.  If say we get above a certain threshold, we can call it a match
         */

        try {
            Mat matA = bufferedImageToMat(a);
            Mat matB = bufferedImageToMat(b);

            int hist_bins = 30;           //number of histogram bins
            int hist_range[]= {0,180};//histogram range

            Mat hist0 = new Mat();
            Mat hist1 = new Mat();
            Mat result = new Mat();
            MatOfFloat ranges = new MatOfFloat(0f, 256f);
            MatOfInt histSize = new MatOfInt(25);

            Imgproc.calcHist(Arrays.asList(matA), new MatOfInt(0), new Mat(), hist0, histSize, ranges);
            Imgproc.calcHist(Arrays.asList(matB), new MatOfInt(0), new Mat(), hist1, histSize, ranges);

            Imgproc.matchTemplate(hist0, hist1, result, Imgproc.TM_CCOEFF_NORMED);

            return 0.00;
            //return Imgproc.comp.compareHist(hist0, hist1, Imgproc.CV_COMP_CORREL);
        } catch (Exception ex) {
            logger.error("Error occurred while trying to compare Hisograms",ex);
            return null;
        }
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
