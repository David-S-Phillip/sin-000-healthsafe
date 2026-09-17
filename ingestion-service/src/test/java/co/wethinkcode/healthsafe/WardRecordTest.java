package co.wethinkcode.healthsafe;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WardRecordTest {

    @Test
    void testWardRecordInitializationAndGetters() {
        WardRecord record = new WardRecord("W-05", "East Wing", "Paediatrics", null, "bedsAvailable was non-numeric");

        assertEquals("W-05", record.getWardId());
        assertEquals("East Wing", record.getWing());
        assertEquals("Paediatrics", record.getDepartment());
        assertNull(record.getBedsAvailable());
        assertEquals("bedsAvailable was non-numeric", record.getNotes());
    }
}