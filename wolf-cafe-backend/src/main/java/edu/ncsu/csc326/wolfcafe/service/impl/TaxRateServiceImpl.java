package edu.ncsu.csc326.wolfcafe.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.ncsu.csc326.wolfcafe.entity.TaxRate;
import edu.ncsu.csc326.wolfcafe.repository.TaxRateRepository;
import edu.ncsu.csc326.wolfcafe.service.TaxRateService;
import lombok.AllArgsConstructor;

/**
 * Implemented TaxRateService interface
 *
 * @author Riana Victoria
 */
@Service
@AllArgsConstructor
public class TaxRateServiceImpl implements TaxRateService{
	
	/**
     * The repository that interacts with the database to manage tax rates.
     */
    @Autowired
    private final TaxRateRepository       taxRateRepository;
	
    /**
     * Changes the current tax rate to a new value.
     *
     * @param newTaxRate
     *            the new tax rate to be set
     * @return a string indicating whether the tax rate was successfully changed
     * @throws IllegalArgumentException if the new tax rate is negative
     */
	public String changeTaxRate(double newTaxRate) {
		if (newTaxRate < 0) {
            throw new IllegalArgumentException("Tax rate cannot be negative.");
        }

        TaxRate taxRate = getOrCreateTaxRate();
        taxRate.setTaxRate(newTaxRate);
        taxRateRepository.save(taxRate);

        return "Successfully changed tax rate!";
	}
	
	/**
     * Retrieves the current tax rate from the database.
     *
     * @return the current tax rate
     */
	public double getCurrentTaxRate() {
		return getOrCreateTaxRate().getTaxRate();
    }
	
	/**
     * Fetches the existing tax rate from the repository or creates a new tax rate 
     * with an initial value of 0.0 if none exists.
     * 
     * @return the existing or newly created TaxRate object
     */
	private TaxRate getOrCreateTaxRate() {
        return taxRateRepository.findAll().stream()
            .findFirst()
            .orElseGet(() -> {
                TaxRate newRate = new TaxRate();
                newRate.setId(1L);
                newRate.setTaxRate(0.0);
                return taxRateRepository.save(newRate);
            });
    }

}
