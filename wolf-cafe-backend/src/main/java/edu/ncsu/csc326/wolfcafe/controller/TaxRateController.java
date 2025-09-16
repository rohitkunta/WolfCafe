package edu.ncsu.csc326.wolfcafe.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc326.wolfcafe.service.TaxRateService;
import lombok.AllArgsConstructor;

/**
 * Controller for API endpoint for a TaxRate
 *
 * @author Riana Victoria
 */
@RestController
@RequestMapping ( "api/taxRate" )
@AllArgsConstructor
@CrossOrigin ( "*" )
public class TaxRateController {
	
	/** Link to TaxRate */
	@Autowired
	private final TaxRateService taxRateService;
	
	/**
     * Changes the tax rate in the system. This method is only accessible to users with the 'ADMIN' role.
     *
     * @param newTaxRate
     *            the new tax rate value to be set
     * @return a ResponseEntity containing a message indicating whether the change was successful
     *         or if there was an error (e.g., invalid input or server issue)
     * @throws IllegalArgumentException if the new tax rate is negative
     */
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/change")
	public ResponseEntity<String> changeTaxRate(@RequestBody double newTaxRate) {
        try {
            String response = taxRateService.changeTaxRate(newTaxRate);
            return new ResponseEntity<>(response, HttpStatus.OK);  // Success response
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);  // Invalid input
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred while changing the tax rate.", HttpStatus.INTERNAL_SERVER_ERROR);  // General error
        }
    }
	
	
	@PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER', 'STAFF')")
	@GetMapping
	public ResponseEntity<Double> getCurrentTaxRate() {
	    double currentRate = taxRateService.getCurrentTaxRate();
	    return new ResponseEntity<>(currentRate, HttpStatus.OK);
	}

	
}
