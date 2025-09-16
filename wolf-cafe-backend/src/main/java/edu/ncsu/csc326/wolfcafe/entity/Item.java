package edu.ncsu.csc326.wolfcafe.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents an item for sale in the WolfCafe.
 */
@Getter
@Setter
@AllArgsConstructor
@Entity
@Table ( name = "items" )
public class Item {

    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY )
    private Long             id;
    @Column ( nullable = false, unique = true )
    private String           name;
    private String           description;
    @Column ( nullable = false )
    private double           price;
    @OneToMany ( cascade = CascadeType.ALL, fetch = FetchType.EAGER )
    @Column ( nullable = false )
    private List<Ingredient> ingredients;

    public Item () {
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
