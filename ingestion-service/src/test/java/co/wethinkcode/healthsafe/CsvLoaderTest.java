package co.wethinkcode.healthsafe;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CsvLoaderTest {

    @Test
    @DisplayName("Should succesfully load lines from wards-outdated.csv")
    public void shouldLoadLinesFromValidResources(){
        CsvLoader loader = new CsvLoader("wards-outdated.csv");

        List<String> lines = loader.loadLines();

        Assertions.assertNotNull(lines, "returned list should not be null");
        Assertions.assertFalse(lines.isEmpty(), "returned list should contain rows");
        Assertions.assertTrue(lines.get(0).toLowerCase().contains("ward"));
    }

    @Test
    @DisplayName("Should not load a file that does not exist in the resources folder")
    public void shouldNotLoadAFileThatDoesNotExist(){
        CsvLoader loader = new CsvLoader("doesNotExistBob.csv");

        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> loader.loadLines(),
                "Expected loadlines() method to throw a illegalArgumentException for a file that doesnt exist"
        );
    }
}
