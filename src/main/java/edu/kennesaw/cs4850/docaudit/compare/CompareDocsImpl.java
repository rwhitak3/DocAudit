package edu.kennesaw.cs4850.docaudit.compare;
import info.debatty.java.stringsimilarity.*;

public class CompareDocsImpl implements  CompareDocs{

    @Override
    public Double compare(String a, String b) {
        QGram dig = new QGram(2);
        return dig.distance(a,b);
    }
}
