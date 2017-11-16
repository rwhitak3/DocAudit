package edu.kennesaw.cs4850.docaudit.compare;

import java.awt.image.BufferedImage;

public interface CompareDocs {
    public Double compare(String a, String b);
    public Double compare(BufferedImage a, BufferedImage b);
}
