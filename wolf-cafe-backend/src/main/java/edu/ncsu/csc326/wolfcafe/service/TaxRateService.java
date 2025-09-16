package edu.ncsu.csc326.wolfcafe.service;

/**
 * TaxRate Service interface, outlines methods for creating and modifying TaxRate
 * objects
 *
 * @author Riana Victoria
 */
public interface TaxRateService {
	
	/**
     * Changes the current tax rate to a new value.
     *
     * @param newTaxRate
     *            the new tax rate to be set
     * @return a string indicating whether the tax rate was successfully changed
     */
	String changeTaxRate(double newTaxRate);
	
	/**
     * Retrieves the current tax rate.
     *
     * @return the current tax rate
     */
	double getCurrentTaxRate();
}
