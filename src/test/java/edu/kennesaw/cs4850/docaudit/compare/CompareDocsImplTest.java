package edu.kennesaw.cs4850.docaudit.compare;

import org.junit.Test;

import static org.junit.Assert.*;

public class CompareDocsImplTest {

    @Test
    public void compare() throws Exception {
        CompareDocsImpl comp = new CompareDocsImpl();
        comp.compare("The exceptions don't apply", "The exceptions don't ply");
    }

}