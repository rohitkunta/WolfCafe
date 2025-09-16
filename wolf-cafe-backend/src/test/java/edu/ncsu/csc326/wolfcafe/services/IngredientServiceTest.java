package edu.ncsu.csc326.wolfcafe.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc326.wolfcafe.dto.IngredientDto;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.service.IngredientService;

@SpringBootTest
public class IngredientServiceTest {

    @Autowired
    private IngredientService ingredientService;

    @BeforeEach
    public void setUp () throws Exception {
        ingredientService.deleteAllIngredients();
    }

    @Test
    @Transactional
    public void testCreateIngredient () {

        final IngredientDto ingredient1 = new IngredientDto( "Coffee", 5 );

        final IngredientDto createdIngredient1 = ingredientService.createIngredient( ingredient1 );
        assertAll( "Ingredient contents", () -> assertEquals( "Coffee", createdIngredient1.getName() ),
                () -> assertEquals( 5, createdIngredient1.getAmount() ) );

        final IngredientDto ingredient2 = new IngredientDto( "Pumpkin Spice", 10 );
        final IngredientDto createdIngredient2 = ingredientService.createIngredient( ingredient2 );
        assertAll( "Ingredient contents", () -> assertEquals( "Pumpkin Spice", createdIngredient2.getName() ),
                () -> assertEquals( 10, createdIngredient2.getAmount() ) );

    }

    @Test
    @Transactional
    public void testGetIngredientById () {

        final IngredientDto ingredient1 = new IngredientDto( "Coffee", 5 );

        final IngredientDto createdIngredient1 = ingredientService.createIngredient( ingredient1 );
        final IngredientDto fetchedIngredient1 = ingredientService.getIngredientById( createdIngredient1.getId() );
        assertAll( "Ingredient contents", () -> assertEquals( "Coffee", fetchedIngredient1.getName() ),
                () -> assertEquals( 5, fetchedIngredient1.getAmount() ) );

        final IngredientDto ingredient2 = new IngredientDto( "Pumpkin Spice", 10 );
        final IngredientDto createdIngredient2 = ingredientService.createIngredient( ingredient2 );
        final IngredientDto fetchedIngredient2 = ingredientService.getIngredientById( createdIngredient2.getId() );
        assertAll( "Ingredient contents", () -> assertEquals( "Pumpkin Spice", fetchedIngredient2.getName() ),
                () -> assertEquals( 10, fetchedIngredient2.getAmount() ) );
    }

    @Test
    @Transactional
    public void testDeleteIngredient () {
        final IngredientDto ingredient1 = new IngredientDto( "Coffee", 5 );

        final IngredientDto createdIngredient1 = ingredientService.createIngredient( ingredient1 );
        assertAll( "Ingredient contents", () -> assertEquals( "Coffee", createdIngredient1.getName() ),
                () -> assertEquals( 5, createdIngredient1.getAmount() ) );

        ingredientService.deleteIngredient( createdIngredient1.getId() );

        assertThrows( ResourceNotFoundException.class, () -> ingredientService.getIngredientById( 1L ) );
    }

    @Test
    @Transactional
    public void testGetAllIngredients () {

        final IngredientDto ingredient1 = new IngredientDto( "Coffee", 5 );

        final IngredientDto createdIngredient1 = ingredientService.createIngredient( ingredient1 );
        assertAll( "Ingredient contents", () -> assertEquals( "Coffee", createdIngredient1.getName() ),
                () -> assertEquals( 5, createdIngredient1.getAmount() ) );

        final List<IngredientDto> ingredients = ingredientService.getAllIngredients();

        assertEquals( "Coffee", ingredients.getFirst().getName() );

    }

}
