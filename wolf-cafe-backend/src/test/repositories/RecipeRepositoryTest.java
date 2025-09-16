/**
 *
 */
package edu.ncsu.csc326.coffee_maker.repositories;

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

import edu.ncsu.csc326.coffee_maker.entity.Ingredient;
// import edu.ncsu.csc326.coffee_maker.entity.Ingredient;
import edu.ncsu.csc326.coffee_maker.entity.Recipe;
// import edu.ncsu.csc326.coffee_maker.entity.enums.IngredientType;

/**
 * Tests Recipe repository
 */
@DataJpaTest
@AutoConfigureTestDatabase ( replace = Replace.NONE )
class RecipeRepositoryTest {

    /** Reference to recipe repository */
    @Autowired
    private RecipeRepository recipeRepository;

    /**
     * Sets up the test case.
     *
     * @throws java.lang.Exception
     *             if error
     */
    @BeforeEach
    public void setUp () throws Exception {
        recipeRepository.deleteAll();

        final Recipe recipe1 = new Recipe( 1L, "Coffee", 50 );
        final Recipe recipe2 = new Recipe( 2L, "Latte", 100 );

        recipeRepository.save( recipe1 );
        recipeRepository.save( recipe2 );
    }

    @Test
    public void testGetRecipeByName () {
        final Optional<Recipe> recipe = recipeRepository.findByName( "Coffee" );
        final Recipe actualRecipe = recipe.get();
        assertAll( "Recipe contents", () -> assertEquals( "Coffee", actualRecipe.getName() ),
                () -> assertEquals( 50, actualRecipe.getPrice() ) );

        final Optional<Recipe> recipe2 = recipeRepository.findByName( "Latte" );
        final Recipe actualRecipe2 = recipe2.get();
        assertAll( "Recipe contents", () -> assertEquals( "Latte", actualRecipe2.getName() ),
                () -> assertEquals( 100, actualRecipe2.getPrice() ) );
    }

    @Test
    public void testGetRecipeByNameInvalid () {
        final Optional<Recipe> recipe = recipeRepository.findByName( "Unknown" );
        assertTrue( recipe.isEmpty() );
    }

    @Test
    public void testAddIngredients () {
        final Recipe recipe1 = new Recipe( 1L, "Coffee", 500 );
        recipe1.addIngredient( new Ingredient( "Coffee", 3 ) );
        recipe1.addIngredient( new Ingredient( "Pumpkin Spice", 2 ) );
        recipe1.addIngredient( new Ingredient( "Milk", 1 ) );

        final Recipe savedRecipe = recipeRepository.save( recipe1 );
        final Optional<Recipe> retrievedRecipe = recipeRepository.findById( savedRecipe.getId() );
        assertAll( "Recipe contents", () -> assertEquals( savedRecipe.getId(), retrievedRecipe.get().getId() ),
                () -> assertEquals( "Coffee", retrievedRecipe.get().getName() ),
                () -> assertEquals( 500, retrievedRecipe.get().getPrice() ),
                () -> assertEquals( 3, retrievedRecipe.get().getIngredients().size() ) );

        final Ingredient i1 = retrievedRecipe.get().getIngredients().get( 0 );
        final Ingredient i2 = retrievedRecipe.get().getIngredients().get( 1 );
        final Ingredient i3 = retrievedRecipe.get().getIngredients().get( 2 );

        assertAll( "Ingredient contents", () -> assertEquals( "Coffee", i1.getName() ),
                () -> assertEquals( 3, i1.getAmount() ) );

        assertAll( "Ingredient contents", () -> assertEquals( "Pumpkin Spice", i2.getName() ),
                () -> assertEquals( 2, i2.getAmount() ) );

        assertAll( "Ingredient contents", () -> assertEquals( "Milk", i3.getName() ),
                () -> assertEquals( 1, i3.getAmount() ) );
    }

}
