/**
 *
 */
package edu.ncsu.csc326.coffee_maker.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc326.coffee_maker.dto.RecipeDto;
import edu.ncsu.csc326.coffee_maker.entity.Ingredient;
import edu.ncsu.csc326.coffee_maker.exception.ResourceNotFoundException;
import edu.ncsu.csc326.coffee_maker.repositories.RecipeRepository;

/**
 *
 */
@SpringBootTest
public class RecipeServiceTest {

    @Autowired
    private RecipeService    recipeService;

    /** Reference to recipe repository */
    @Autowired
    private RecipeRepository recipeRepository;

    /**
     * @throws java.lang.Exception
     */
    @BeforeEach
    void setUp () throws Exception {
        recipeRepository.deleteAll();
    }

    /**
     * Test method for
     * {@link edu.ncsu.csc326.coffee_maker.services.RecipeService#createRecipe(edu.ncsu.csc326.coffee_maker.dto.RecipeDto)}.
     */
    @Test
    @Transactional
    void testCreateRecipe () {
        final RecipeDto recipeDto = new RecipeDto( 0L, "Coffee", 50 );
        final Ingredient milk = new Ingredient( "Milk", 3 );
        final Ingredient sugar = new Ingredient( "Sugar", 2 );
        recipeDto.addIngredient( milk );
        recipeDto.addIngredient( sugar );

        final RecipeDto savedRecipe = recipeService.createRecipe( recipeDto );
        assertAll( "Recipe contents", () -> assertTrue( savedRecipe.getId() > 1L ),
                () -> assertEquals( "Coffee", savedRecipe.getName() ), () -> assertEquals( 50, savedRecipe.getPrice() ),
                () -> assertEquals( "Milk", savedRecipe.getIngredients().get( 0 ).getName() ),
                () -> assertEquals( 3, savedRecipe.getIngredients().get( 0 ).getAmount() ),
                () -> assertEquals( "Sugar", savedRecipe.getIngredients().get( 1 ).getName() ),
                () -> assertEquals( 2, savedRecipe.getIngredients().get( 1 ).getAmount() ) );

        final RecipeDto retrievedRecipe = recipeService.getRecipeById( savedRecipe.getId() );
        assertAll( "Recipe contents", () -> assertEquals( savedRecipe.getId(), retrievedRecipe.getId() ),
                () -> assertEquals( "Coffee", savedRecipe.getName() ), () -> assertEquals( 50, savedRecipe.getPrice() ),
                () -> assertEquals( "Milk", savedRecipe.getIngredients().get( 0 ).getName() ),
                () -> assertEquals( 3, savedRecipe.getIngredients().get( 0 ).getAmount() ),
                () -> assertEquals( "Sugar", savedRecipe.getIngredients().get( 1 ).getName() ),
                () -> assertEquals( 2, savedRecipe.getIngredients().get( 1 ).getAmount() ) );
        final RecipeDto retrievedRecipe2 = recipeService.getRecipeByName( savedRecipe.getName() );
        assertAll( "Recipe contents", () -> assertEquals( savedRecipe.getName(), retrievedRecipe2.getName() ),
                () -> assertEquals( "Coffee", savedRecipe.getName() ), () -> assertEquals( 50, savedRecipe.getPrice() ),
                () -> assertEquals( "Milk", savedRecipe.getIngredients().get( 0 ).getName() ),
                () -> assertEquals( 3, savedRecipe.getIngredients().get( 0 ).getAmount() ),
                () -> assertEquals( "Sugar", savedRecipe.getIngredients().get( 1 ).getName() ),
                () -> assertEquals( 2, savedRecipe.getIngredients().get( 1 ).getAmount() ) );
    }

    /**
     * Test method for
     * {@link edu.ncsu.csc326.coffee_maker.services.RecipeService#isDuplicateName(java.lang.String)}.
     */
    @Test
    void testIsDuplicateName () {
        final RecipeDto recipeDto = new RecipeDto( 0L, "Coffee", 50 );
        final RecipeDto savedRecipe = recipeService.createRecipe( recipeDto );
        assertTrue( recipeService.isDuplicateName( savedRecipe.getName() ) );
        assertFalse( recipeService.isDuplicateName( "flyers" ) );
    }

    /**
     * Test method for
     * {@link edu.ncsu.csc326.coffee_maker.services.RecipeService#getAllRecipes()}.
     */
    @Test
    void testGetAllRecipes () {
        final RecipeDto recipeDto = new RecipeDto( 0L, "Coffee", 50 );
        final RecipeDto savedRecipe = recipeService.createRecipe( recipeDto );
        final List<RecipeDto> recipes = recipeService.getAllRecipes();
        System.out.print( recipes.getFirst().getId() );
        assertEquals( "Coffee", recipes.getFirst().getName() );

    }

    /**
     * Test method for
     * {@link edu.ncsu.csc326.coffee_maker.services.RecipeService#updateRecipe(java.lang.Long, edu.ncsu.csc326.coffee_maker.dto.RecipeDto)}.
     */
    @Test
    void testUpdateRecipe () {
        final RecipeDto initialRecipeDto = new RecipeDto( 0L, "Coffee", 50 );
        final RecipeDto savedRecipe = recipeService.createRecipe( initialRecipeDto );
        final RecipeDto updatedRecipeDto = new RecipeDto( savedRecipe.getId(), "Espresso", 60 );
        final RecipeDto updatedRecipe = recipeService.updateRecipe( savedRecipe.getId(), updatedRecipeDto );
        assertNotNull( updatedRecipe );
        assertEquals( savedRecipe.getId(), updatedRecipe.getId() );
        assertEquals( "Espresso", updatedRecipe.getName() );
        final RecipeDto fetchedRecipe = recipeService.getRecipeById( savedRecipe.getId() );
        assertEquals( "Espresso", fetchedRecipe.getName() );
    }

    /**
     * Test method for
     * {@link edu.ncsu.csc326.coffee_maker.services.RecipeService#deleteRecipe(java.lang.Long)}.
     */
    @Test
    void testDeleteRecipe () {
        final RecipeDto recipeDto = new RecipeDto( 0L, "Coffee", 50 );
        final RecipeDto savedRecipe = recipeService.createRecipe( recipeDto );
        recipeService.deleteRecipe( savedRecipe.getId() );
        assertThrows( ResourceNotFoundException.class, () -> recipeService.getRecipeById( savedRecipe.getId() ) );
        assertFalse( recipeRepository.findById( savedRecipe.getId() ).isPresent() );
    }

}
