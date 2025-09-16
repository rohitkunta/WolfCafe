package edu.ncsu.csc326.wolfcafe.dto;

import java.util.ArrayList;
import java.util.List;

import edu.ncsu.csc326.wolfcafe.entity.Ingredient;

/**
 * Used to transfer Inventory data between the client and server. This class
 * will serve as the response in the REST API.
 */
public class InventoryDto {

    private Long                   id;

    private final List<Ingredient> ingredients;

    // need to add a units field

    /**
     * Default InventoryDto constructor.
     */
    public InventoryDto () {
        this.ingredients = new ArrayList<Ingredient>();

    }

    /**
     * Constructs an InventoryDto object from field values.
     *
     * @param id
     *            inventory id
     * @param coffee
     *            amount coffee in inventory
     * @param milk
     *            amount milk in inventory
     * @param sugar
     *            amount sugar in inventory
     * @param chocolate
     *            amount chocolate in inventory
     */
    public InventoryDto ( final Long id ) {
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
