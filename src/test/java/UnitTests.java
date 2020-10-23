
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UnitTests {

    @Test
    public void putErrorUrl() {
        String failUrl = "https://ichef.bbci.co.uk/news/624/cpsprodpb/11DB3/production/_102693137_281befe0-99d8-4d72-9dd8-beeafa6ce8c2333.jpg";
        assertEquals("q", failUrl);
    }


}
