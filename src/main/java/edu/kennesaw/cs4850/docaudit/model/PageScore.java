package edu.kennesaw.cs4850.docaudit.model;

public class PageScore {
    private Double score;
    private Page sourcePage;
    private Page matchPage;

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Page getSourcePage() {
        return sourcePage;
    }

    public void setSourcePage(Page sourcePage) {
        this.sourcePage = sourcePage;
    }

    public Page getMatchPage() {
        return matchPage;
    }

    public void setMatchPage(Page matchPage) {
        this.matchPage = matchPage;
    }
}
