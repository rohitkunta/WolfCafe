package edu.ncsu.csc326.wolfcafe.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents the tax rate used for all sales in the WolfCafe.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table ( name = "taxRate" )
public class TaxRate {
	
	/**
     * The unique identifier for the tax rate entry in the database.
     * This ID is automatically generated.
     */
	@Id
    private Long             id;
	/**
     * The tax rate value used for all sales.
     * This value cannot be null and is initialized to 0.0 by default.
     */
    @Column ( nullable = false )
    private double          taxRate = 0.0; // default value
    
    

}
