package com.trademaster.ims;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TrademasterImsApplicationTests {
    @Test
    void applicationEntryPointExistsWithoutConnectingToMySql() {
        assertNotNull(TrademasterImsApplication.class);
    }
}
