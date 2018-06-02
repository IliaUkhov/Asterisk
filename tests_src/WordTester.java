import com.company.model.Word;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class WordTester {

    private static String[] testStringsForWordCreation = {
            "аденозинтрифосфатсинтаза", "льюльяйльяко",
            "ого", "безоар", "тттдддккк", "V", "а"};

    @Test
    public void testSyllableCountComputedProperty() {
        int[] expectedResults = {10, 4, 2, 3, 0, 0, 1};

        int i = 0;
        for (String testString : testStringsForWordCreation) {
            Word word = new Word(testString, 1);
            assertSame(expectedResults[i], word.getSyllableCount().invoke());
            ++i;
        }
    }

    @Test
    public void testAsSyllableSequenceMethod() {
        int[][] stressesToSet = {{0, 1, 2, 3, 4}, {0, 0, 0}, {1, 999}, {5}, {1}, {0}, {2}};
        String[] expectedResults = {"????........", "....", "?.", "..!", "!", "", "."};

        int i = 0;
        for (String testString : testStringsForWordCreation) {
            Word word = new Word(testString, stressesToSet[i]);
            assertEquals(expectedResults[i], word.asSyllableSequence());
            ++i;
        }
    }

    @Test
    public void testToStringMethod() {
        int[][] stressesToSet = {{5, 13}, {3, 6}, {1}, {0}, {999}, {0}, {1}};
        String[] expectedResults = {
                "аден́озинтриф́осфатсинтаза", "ль́юль́яйльяко",
                "́ого", "безоар", "тттдддккк", "V", "́а"};

        int i = 0;
        try {
            for (String testString : testStringsForWordCreation) {
                Word word = new Word(testString, stressesToSet[i]);
                assertEquals(expectedResults[i], word.toString());
                ++i;
            }
            fail("No expected StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException e) {  }
    }
}
