package edu.kennesaw.cs4850.docaudit.convert;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.bytedeco.javacpp.*;
import static org.bytedeco.javacpp.lept.*;
import static org.bytedeco.javacpp.tesseract.*;
import org.slf4j.LoggerFactory;

/*
 *
 * @author rwhitak3
 */
public class ImageToTextTesseract implements ImageToText {
    private org.slf4j.Logger logger = LoggerFactory.getLogger(ImageToTextTesseract.class);
    private TessBaseAPI api;
    
    public ImageToTextTesseract() {
        api = new TessBaseAPI();
        // Initialize tesseract-ocr with English, without specifying tessdata path
        if (api.Init(null, "eng") != 0) {
            logger.error("Could not initialize tesseract.");
            //Should throw
        }
    }
    
    public String convert(BufferedImage img) {
        try {
            
            if (this.api.Init(null, "eng") != 0) {
                logger.error("Could not initialize tesseract.");
                return null;
                
            }
            BytePointer outText;
            logger.debug("Converting input Image to tiff byte array");
            //ImageIO.createImageInputStream(img);
            ByteArrayOutputStream output =  new ByteArrayOutputStream();   
            ImageIO.write(img, "png", output);
            output.flush();
            
            Byte[] b = null;
            logger.debug("Creating PIX object");
            
            PIX inputImage = pixReadMem(output.toByteArray(), output.size());
            //PIX inputImage = pixReadMem();
            /* ByteBuffer buf = toTiffBuffer(img);
            buf.position(0);
            image = pixReadMemTiff(buf.array(), buf.capacity(), 0);
            PIX inputImage = pixReadMem(img);*/
            logger.debug("Sending image to Tesseract API");
            this.api.SetImage(inputImage);
            logger.debug("Fetching text from tesseract");
            outText = this.api.GetUTF8Text();
            String out = outText.getString();
            logger.debug("OCR output:\n" + out);
            
            // Destroy used object and release memory
            this.api.End();
            outText.deallocate();
            pixDestroy(inputImage);
            return out;
        } catch (IOException ex) {
            logger.error("Error Occurred reading Data", ex);
            return null;
        } catch (Exception ex) {
            logger.error("Unknown Error occured", ex);
            return null;
        }
    }
}
