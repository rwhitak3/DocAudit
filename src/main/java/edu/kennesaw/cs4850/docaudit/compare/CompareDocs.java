package edu.kennesaw.cs4850.docaudit.compare;

import edu.kennesaw.cs4850.docaudit.model.Document;
import edu.kennesaw.cs4850.docaudit.model.MatchScore;

import java.awt.image.BufferedImage;
import java.util.List;

public interface CompareDocs {
    public Double compare(String a, String b);
    public Double compare(BufferedImage a, BufferedImage b);
    public MatchScore compare(Document a, Document b);
    public void setImageMode(boolean mode);
}
