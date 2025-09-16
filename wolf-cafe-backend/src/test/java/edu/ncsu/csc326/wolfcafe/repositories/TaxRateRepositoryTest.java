package edu.ncsu.csc326.wolfcafe.repositories;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import edu.ncsu.csc326.wolfcafe.entity.TaxRate;
import edu.ncsu.csc326.wolfcafe.repository.TaxRateRepository;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;

/**
 * Tests TaxRate repository
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class TaxRateRepositoryTest {

    /** Reference to TaxRate repository */
    @Autowired
    private TaxRateRepository taxRateRepository;
    
    /**
     * Sets up test data.
     */
    @BeforeEach
    public void setUp() throws Exception {
    	taxRateRepository.deleteAll();

        final TaxRate tax = new TaxRate();
        tax.setId(1L);
        tax.setTaxRate(0.07); // 7% tax
        taxRateRepository.save(tax);
    }

    /**
     * Verifies that the stored tax rate can be retrieved correctly by its ID.
     * Asserts that both ID and rate value match expected values.
     */
    @Test
    public void testGetTaxRate() {
        final TaxRate rate = taxRateRepository.findById(1L)
                .orElseThrow(() -> new ResourceNotFoundException("Tax rate not found"));
        assertAll("TaxRate contents",
                () -> assertEquals(1L, rate.getId()),
                () -> assertEquals(0.07, rate.getTaxRate(), 0.0001));
    }

    /**
     * Tests updating the tax rate value and verifies the update is persisted.
     */
    @Test
    public void testUpdateTaxRate() {
        final TaxRate rate = taxRateRepository.findById(1L)
                .orElseThrow(() -> new ResourceNotFoundException("Tax rate not found"));
        rate.setTaxRate(0.085);
        taxRateRepository.save(rate);

        final TaxRate updated = taxRateRepository.findById(1L)
                .orElseThrow(() -> new ResourceNotFoundException("Updated tax rate not found"));

        assertEquals(0.085, updated.getTaxRate(), 0.0001);
    }

    /**
     * Ensures that only one tax rate entry exists in the repository.
     */
    @Test
    public void testNoDuplicateTaxRate() {
        final long count = taxRateRepository.count();
        assertEquals(1, count); // there should only be one tax rate
    }

    /**
     * Verifies that when the tax rate is deleted, retrieval returns an empty result.
     */
    @Test
    public void testMissingTaxRate() {
        taxRateRepository.deleteAll();
        Optional<TaxRate> missing = taxRateRepository.findById(1L);
        assertTrue(missing.isEmpty());
    }

}
