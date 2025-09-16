/**
 * Test class for TaxRateService
 */
package edu.ncsu.csc326.wolfcafe.services;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc326.wolfcafe.repository.TaxRateRepository;
import edu.ncsu.csc326.wolfcafe.service.TaxRateService;

@SpringBootTest
public class TaxRateServiceTest {

    @Autowired
    private TaxRateService taxRateService;

    @Autowired
    private TaxRateRepository taxRateRepository;

    @BeforeEach
    void setUp() throws Exception {
        taxRateRepository.deleteAll();
    }

    /**
     * Test getting the default tax rate when none is set.
     */
    @Test
    @Transactional
    void testGetDefaultTaxRate() {
        final double taxRate = taxRateService.getCurrentTaxRate();
        assertEquals(0.0, taxRate, "Default tax rate should be 0.0");
    }

    /**
     * Test setting a new valid tax rate.
     */
    @Test
    @Transactional
    void testChangeTaxRate() {
        final String result = taxRateService.changeTaxRate(7.5);
        final double updatedRate = taxRateService.getCurrentTaxRate();

        assertAll("Tax rate updated successfully",
            () -> assertEquals("Successfully changed tax rate!", result),
            () -> assertEquals(7.5, updatedRate)
        );
    }

    /**
     * Test changing the tax rate multiple times.
     */
    @Test
    @Transactional
    void testChangeTaxRateMultipleTimes() {
        taxRateService.changeTaxRate(5.0);
        assertEquals(5.0, taxRateService.getCurrentTaxRate());

        taxRateService.changeTaxRate(8.25);
        assertEquals(8.25, taxRateService.getCurrentTaxRate());
    }

    /**
     * Test setting a negative tax rate, which should throw an exception.
     */
    @Test
    void testChangeTaxRateNegative() {
        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            taxRateService.changeTaxRate(-1.0);
        });

        assertEquals("Tax rate cannot be negative.", thrown.getMessage());
    }

    /**
     * Test that only one TaxRate object is stored.
     */
    @Test
    @Transactional
    void testSingleTaxRatePersistence() {
        taxRateService.changeTaxRate(4.5);
        taxRateService.changeTaxRate(6.0);
        assertEquals(1, taxRateRepository.count(), "There should only be one TaxRate object in the DB.");
    }
}
