package edu.ncsu.csc326.wolfcafe.dto;

import java.util.ArrayList;
import java.util.List;

import edu.ncsu.csc326.wolfcafe.entity.Ingredient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Item for data transfer.
 */
@Getter
@Setter
@AllArgsConstructor
public class ItemDto {
    private Long             id;
    private String           name;
    private String           description;
    private double           price;
    // @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Ingredient> ingredients;

    public ItemDto () {
        ingredients = new ArrayList<Ingredient>();
    }

    public void addIngredient ( final Ingredient ingredient ) {
        this.ingredients.add( ingredient );
    }

    public void removeIngredient ( final Ingredient ingredient ) {
        this.ingredients.remove( ingredient );
    }

    /**
     * Returns the list of ingredients.
     *
     * @return List of ingredients.
     */
    public List<Ingredient> getIngredients () {
        return ingredients;
    }
}
