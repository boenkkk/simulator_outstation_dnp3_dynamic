package dev.boenkkk.simulator_outstation_dnp3_dynamic.config;

import static org.joou.Unsigned.ubyte;

import org.springframework.context.annotation.Configuration;

import io.stepfunc.dnp3.AttributeVariations;
import io.stepfunc.dnp3.Database;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class DatabaseConfigImpl {

    // ANCHOR: database_init_function
    public void initializeDatabaseDefault(Database db) {
        // define device attributes made available to the master
        db.defineStringAttr(ubyte(0), false, AttributeVariations.USER_ASSIGNED_OWNER_NAME, "BOENKKK.DEV");
        db.defineStringAttr(ubyte(0), true, AttributeVariations.PRODUCT_NAME_AND_MODEL, "SIMULATOR DNP3 OUTSTATION");
    }
    // ANCHOR_END: database_init_function
}
