package edu.kennesaw.cs4850.docaudit.model;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;

public class Page implements KryoSerializable {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(Page.class);
    private String textContents;
    private BufferedImage imgContents;
    private int pageNumber;
    private Document parentDocument;

    public String getTextContents() {
        return textContents;
    }

    public void setTextContents(String textContents) {
        this.textContents = textContents;
    }

    public BufferedImage getImgContents() {
        return imgContents;
    }

    public void setImgContents(BufferedImage imgContents) {
        this.imgContents = imgContents;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Document getParentDocument() {
        return parentDocument;
    }

    public void setParentDocument(Document parentDocument) {
        this.parentDocument = parentDocument;
    }

    @Override
    public void write(Kryo kryo, Output output) {
        int height = imgContents.getHeight();
        int width = imgContents.getWidth();
        int[] outrgb = new int[width*height];
        imgContents.getRGB(0,0, width,height,outrgb,0,width);
        int type = imgContents.getType();

        output.writeString(textContents);
        output.writeInt(pageNumber);
        output.writeInt(height);
        output.writeInt(width);
        output.writeInt(type);
        output.writeInts(outrgb);
    }

    @Override
    public void read(Kryo kryo, Input input) {
        this.textContents = input.readString();
        logger.trace("Found text" + this.textContents);
        this.pageNumber = input.readInt();
        logger.trace("Found page Number" + this.pageNumber);
        int height = input.readInt();
        logger.trace("Found Height" + height);
        int width = input.readInt();
        logger.trace("Found Width" + width);
        int type = input.readInt();
        int[] inputRGB = input.readInts(width*height);
        this.imgContents = new BufferedImage(width, height, type);
        this.imgContents.setRGB(0,0,width,height,inputRGB,0,width);
    }
}
