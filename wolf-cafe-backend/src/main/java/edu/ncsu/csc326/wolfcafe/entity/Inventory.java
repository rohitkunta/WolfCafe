package edu.ncsu.csc326.wolfcafe.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

/**
 * Inventory for the coffee maker. Inventory is a Data Access Object (DAO) is
 * tied to the database using Hibernate libraries. InventoryRepository provides
 * the methods for database CRUD operations.
 */
@Entity
public class Inventory {

    /** id for inventory entry */
    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY )
    private Long                   id;

    @OneToMany ( cascade = CascadeType.ALL, fetch = FetchType.EAGER )
    private final List<Ingredient> ingredients;

    // need to add a units field

    /**
     * Empty constructor for Hibernate
     */
    public Inventory () {
        // Intentionally empty so that Hibernate can instantiate
        // Inventory object.
        this.ingredients = new ArrayList<Ingredient>();
    }

    /**
     * Creates an Inventory with all fields
     *
     * @param id
     *            inventory's id
     * @param coffee
     *            inventory's amount coffee
     * @param milk
     *            inventory's amount milk
     * @param sugar
     *            inventory's amount sugar
     * @param chocolate
     *            inventory's amount chocolate
     */
    public Inventory ( final Long id ) {
        super();
        this.id = id;
        this.ingredients = new ArrayList<Ingredient>();
    }

    /**
     * Returns the ID of the entry in the DB
     *
     * @return long
     */
    public Long getId () {
        return id;
    }

    /**
     * Set the ID of the Inventory (Used by Hibernate)
     *
     * @param id
     *            the ID
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Gets the ingredients list
     *
     * @return the ingredients
     */
    public List<Ingredient> getIngredients () {
        return ingredients;
    }

    /**
     * @param ingredients
     *            the ingredients to set
     */
    public void addIngredient ( final Ingredient ingredient ) {
        this.ingredients.add( ingredient );
    }

    /**
     * Checks for the amount of a specific ingredient in the inventory
     *
     * Might break hibernate, but we will see
     *
     * @param name
     *            the name of the ingredient
     * @return the amount of said ingredient in the inventory. If the ingredient
     *         is not in the inventory, return -1.
     */
    public int amountOfIngredient ( final String name ) {

        int amount = -1;

        for ( int i = 0; i < ingredients.size(); i++ ) {
            if ( name.equals( ingredients.get( i ).getName() ) ) {
                amount = ingredients.get( i ).getAmount();
            }
        }

        return amount;
    }

    /**
     * Sets the Amount for a specified ingredient in the inventory
     *
     * @param name
     *            the name of the ingredient
     * @param amount
     *            the amount to be set for that ingredient
     */
    public void setIngredientAmount ( final String name, final int amount ) {

        // runs through all ingredients list until it finds one
        for ( int i = 0; i < ingredients.size(); i++ ) {
            if ( name.equals( ingredients.get( i ).getName() ) ) {
                ingredients.get( i ).setAmount( amount );
            }
        }

    }

}
