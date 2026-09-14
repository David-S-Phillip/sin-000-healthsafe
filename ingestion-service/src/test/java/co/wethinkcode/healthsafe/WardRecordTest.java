package co.wethinkcode.healthsafe;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WardRecordTest {

    @Test
    void testWardRecordInitializationAndGetters() {
        WardRecord record = new WardRecord("W-05", "East Wing", "Paediatrics", null, "bedsAvailable was non-numeric");

        assertEquals("W-05", record.wardId());
        assertEquals("East Wing", record.wing());
        assertEquals("Paediatrics", record.getDepartment());
        assertNull(record.bedsAvailable());
        assertEquals("bedsAvailable was non-numeric", record.notes());
    }
}