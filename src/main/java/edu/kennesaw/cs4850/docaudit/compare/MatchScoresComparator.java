package edu.kennesaw.cs4850.docaudit.compare;

import edu.kennesaw.cs4850.docaudit.model.MatchScore;

import java.util.Comparator;

public class MatchScoresComparator implements Comparator<MatchScore> {

    @Override
    public int compare(MatchScore t1, MatchScore t2) {
        return Double.compare(t1.getScore(), t2.getScore());
    }
}
