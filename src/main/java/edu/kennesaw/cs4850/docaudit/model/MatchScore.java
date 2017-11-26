package edu.kennesaw.cs4850.docaudit.model;

import java.util.Comparator;
import java.util.List;

public class MatchScore  implements Comparator<MatchScore>  {
    private Double score;
    private Document doc;
    private boolean missingPages;
    private boolean badPageOrder;
    private Integer duplicates;
    private List<PageScore> pageScores;


    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Document getDoc() {
        return doc;
    }

    public void setDoc(Document doc) {
        this.doc = doc;
    }

    public boolean isMissingPages() {
        return missingPages;
    }

    public void setMissingPages(boolean missingPages) {
        this.missingPages = missingPages;
    }

    public boolean isBadPageOrder() {
        return badPageOrder;
    }

    public void setBadPageOrder(boolean badPageOrder) {
        this.badPageOrder = badPageOrder;
    }

    public Integer getDuplicates() {
        return duplicates;
    }

    public void setDuplicates(Integer duplicates) {
        this.duplicates = duplicates;
    }

    @Override
    public int compare(MatchScore t1, MatchScore t2) {
        return Double.compare(t1.getScore(), t2.getScore());
    }

    public List<PageScore> getPageScores() {
        return pageScores;
    }

    public void setPageScores(List<PageScore> pageScores) {
        this.pageScores = pageScores;
    }
}
