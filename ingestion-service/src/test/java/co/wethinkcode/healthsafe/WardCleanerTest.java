package co.wethinkcode.healthsafe;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNull;

public class WardCleanerTest {

    @Test
    @DisplayName("should normalize casing and trim extra whitespace from ward details")
    public void shouldNormalizeCasingAndTrimSpaces(){
        WardCleaner cleaner = new WardCleaner();

        WardRecord record = cleaner.cleanSingleLine("w-05,east wing ,PAEDIATRICS,10");
        Assertions.assertEquals("W-05", record.getWardId());
        Assertions.assertEquals("East Wing", record.getWing());
        Assertions.assertEquals("Paediatrics", record.getDepartment());
        Assertions.assertEquals(10, record.getBedsAvailable());
        assertNull(record.getNotes());
    }

    @Test
    @DisplayName("Should handle non-numeric bed counts and flag in notes")
    void shouldHandleNonNumericBeds() {
        WardCleaner cleaner = new WardCleaner();

        WardRecord record = cleaner.cleanSingleLine("w-05,east wing ,PAEDIATRICS,five");

        Assertions.assertEquals("W-05", record.getWardId());
        assertNull(record.getBedsAvailable());
        Assertions.assertNotNull(record.getNotes());
        Assertions.assertTrue(record.getNotes().contains("non-numeric"));
    }

    @Test
    @DisplayName("Should convert placeholders like N/A or TBD to null")
    void shouldConvertPlaceholdersToNull() {
        WardCleaner cleaner = new WardCleaner();

        WardRecord record = cleaner.cleanSingleLine("w-05, N/A ,PAEDIATRICS,TBD");

        Assertions.assertNull(record.getWing());
        Assertions.assertNull(record.getBedsAvailable());
    }

}
