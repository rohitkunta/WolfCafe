package edu.ncsu.csc326.coffee_maker.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc326.coffee_maker.dto.InventoryDto;
import edu.ncsu.csc326.coffee_maker.dto.RecipeDto;
import edu.ncsu.csc326.coffee_maker.entity.Ingredient;
import edu.ncsu.csc326.coffee_maker.repositories.RecipeRepository;
import jakarta.persistence.EntityManager;

@SpringBootTest
class MakeRecipeServiceTest {

    /** Reference to InventoryService (and InventoryServiceImpl). */
    @Autowired
    private MakeRecipeService makeRecipeS;

    /** Reference to InventoryService (and InventoryServiceImpl). */
    @Autowired
    private InventoryService  inventoryService;

    /** Reference to EntityManager */
    @Autowired
    private EntityManager     entityManager;

    @Autowired
    private RecipeService     recipeService;

    /** Reference to recipe repository */
    @Autowired
    private RecipeRepository  recipeRepository;

    // DONE: change all these recipeDto's to work, look at Recipe*Test for
    // guidance

    /**
     * Recipe to be made that will work
     */
    final RecipeDto           recipeDto      = new RecipeDto( 0L, "Coffee", 50 );

    /**
     * Recipe to be made that will fail due to not enough coffee ingredient
     */
    final RecipeDto           recipeDtoFail1 = new RecipeDto( 0L, "Coffee2", 50 );

    /**
     * Recipe to be made that will fail due to not enough milk ingredient
     */
    final RecipeDto           recipeDtoFail2 = new RecipeDto( 0L, "Coffee3", 50 );

    /**
     * Recipe to be made that will fail due to not enough sugar ingredient
     */
    final RecipeDto           recipeDtoFail3 = new RecipeDto( 0L, "Coffee4", 50 );

    /**
     * Recipe to be made that will fail due to not enough chocolate ingredient
     */
    final RecipeDto           recipeDtoFail4 = new RecipeDto( 0L, "Coffee5", 50 );

    // once we switch this to the repository, we have to save this dto to the
    // repository
    InventoryDto              inventoryDto   = new InventoryDto( 1L );

    /**
     * Sets up the test case. We assume only one inventory row. Because
     * inventory is treated as a singleton (only one row), we must truncate for
     * auto increment on the id to work correctly.
     *
     * @throws java.lang.Exception
     *             if error
     */
    @BeforeEach
    void setUp () throws Exception {

        // DONE: instead of TRUNCATE TABLE, use the
        // inventoryRepository.deleteAll()
        // and the query above it like the inventory tests

        entityManager.createNativeQuery( "ALTER TABLE inventory AUTO_INCREMENT = 1" ).executeUpdate();
        inventoryService.deleteInventory();

        recipeDto.addIngredient( new Ingredient( "Coffee", 2 ) );
        recipeDto.addIngredient( new Ingredient( "Milk", 1 ) );
        recipeDto.addIngredient( new Ingredient( "Sugar", 1 ) );
        recipeDto.addIngredient( new Ingredient( "Chocolate", 0 ) );

        recipeDtoFail1.addIngredient( new Ingredient( "Coffee", 40 ) );
        recipeDtoFail1.addIngredient( new Ingredient( "Milk", 1 ) );
        recipeDtoFail1.addIngredient( new Ingredient( "Sugar", 1 ) );
        recipeDtoFail1.addIngredient( new Ingredient( "Chocolate", 0 ) );

        recipeDtoFail2.addIngredient( new Ingredient( "Coffee", 2 ) );
        recipeDtoFail2.addIngredient( new Ingredient( "Milk", 40 ) );
        recipeDtoFail2.addIngredient( new Ingredient( "Sugar", 1 ) );
        recipeDtoFail2.addIngredient( new Ingredient( "Chocolate", 0 ) );

        recipeDtoFail3.addIngredient( new Ingredient( "Coffee", 2 ) );
        recipeDtoFail3.addIngredient( new Ingredient( "Milk", 1 ) );
        recipeDtoFail3.addIngredient( new Ingredient( "Sugar", 40 ) );
        recipeDtoFail3.addIngredient( new Ingredient( "Chocolate", 0 ) );

        recipeDtoFail4.addIngredient( new Ingredient( "Coffee", 2 ) );
        recipeDtoFail4.addIngredient( new Ingredient( "Milk", 1 ) );
        recipeDtoFail4.addIngredient( new Ingredient( "Sugar", 1 ) );
        recipeDtoFail4.addIngredient( new Ingredient( "Chocolate", 40 ) );

        inventoryDto.addIngredient( new Ingredient( "Coffee", 35 ) );
        inventoryDto.addIngredient( new Ingredient( "Milk", 17 ) );
        inventoryDto.addIngredient( new Ingredient( "Sugar", 12 ) );
        inventoryDto.addIngredient( new Ingredient( "Chocolate", 14 ) );

    }

    // DONE: FOR ALL THESE TESTS, change it to using amountOfIngredient(String
    // name)
    // instead of the getCoffee and such

    @Test
    @Transactional
    void testMakeRecipe () {

        inventoryService.createInventory( inventoryDto );

        recipeRepository.deleteAll();

        recipeService.createRecipe( recipeDto ); // adds a recipe to the
                                                 // repository

        final boolean made = makeRecipeS.makeRecipe( inventoryDto, recipeDto );

        assertTrue( made ); // recipe should have been made

        // asserting that inventory has been deducted the correct amount for
        // recipeDto1
        final InventoryDto updatedInventoryDto = inventoryService.getInventory();
        assertAll( "InventoryDto contents after making", () -> assertEquals( 1L, updatedInventoryDto.getId() ),
                () -> assertEquals( 33, updatedInventoryDto.amountOfIngredient( "Coffee" ) ),
                () -> assertEquals( 16, updatedInventoryDto.amountOfIngredient( "Milk" ) ),
                () -> assertEquals( 11, updatedInventoryDto.amountOfIngredient( "Sugar" ) ),
                () -> assertEquals( 14, updatedInventoryDto.amountOfIngredient( "Chocolate" ) ) );

        final boolean made2 = makeRecipeS.makeRecipe( inventoryDto, recipeDtoFail1 );

        assertFalse( made2 ); // recipe should not have been made

        final InventoryDto updatedInventoryDto2 = inventoryService.getInventory();
        // assert inventory didn't change
        assertAll( "InventoryDto contents after making", () -> assertEquals( 1L, updatedInventoryDto2.getId() ),
                () -> assertEquals( 33, updatedInventoryDto.amountOfIngredient( "Coffee" ) ),
                () -> assertEquals( 16, updatedInventoryDto.amountOfIngredient( "Milk" ) ),
                () -> assertEquals( 11, updatedInventoryDto.amountOfIngredient( "Sugar" ) ),
                () -> assertEquals( 14, updatedInventoryDto.amountOfIngredient( "Chocolate" ) ) );

        final boolean made3 = makeRecipeS.makeRecipe( inventoryDto, recipeDtoFail2 );

        assertFalse( made3 ); // recipe should not have been made

        final InventoryDto updatedInventoryDto3 = inventoryService.getInventory();
        // assert inventory didn't change
        assertAll( "InventoryDto contents after making", () -> assertEquals( 1L, updatedInventoryDto3.getId() ),
                () -> assertEquals( 33, updatedInventoryDto.amountOfIngredient( "Coffee" ) ),
                () -> assertEquals( 16, updatedInventoryDto.amountOfIngredient( "Milk" ) ),
                () -> assertEquals( 11, updatedInventoryDto.amountOfIngredient( "Sugar" ) ),
                () -> assertEquals( 14, updatedInventoryDto.amountOfIngredient( "Chocolate" ) ) );

        final boolean made4 = makeRecipeS.makeRecipe( inventoryDto, recipeDtoFail3 );

        assertFalse( made4 ); // recipe should not have been made

        final InventoryDto updatedInventoryDto4 = inventoryService.getInventory();
        // assert inventory didn't change
        assertAll( "InventoryDto contents after making", () -> assertEquals( 1L, updatedInventoryDto4.getId() ),
                () -> assertEquals( 33, updatedInventoryDto.amountOfIngredient( "Coffee" ) ),
                () -> assertEquals( 16, updatedInventoryDto.amountOfIngredient( "Milk" ) ),
                () -> assertEquals( 11, updatedInventoryDto.amountOfIngredient( "Sugar" ) ),
                () -> assertEquals( 14, updatedInventoryDto.amountOfIngredient( "Chocolate" ) ) );

        final boolean made5 = makeRecipeS.makeRecipe( inventoryDto, recipeDtoFail4 );

        assertFalse( made5 ); // recipe should not have been made

        final InventoryDto updatedInventoryDto5 = inventoryService.getInventory();
        // assert inventory didn't change
        assertAll( "InventoryDto contents after making", () -> assertEquals( 1L, updatedInventoryDto5.getId() ),
                () -> assertEquals( 33, updatedInventoryDto.amountOfIngredient( "Coffee" ) ),
                () -> assertEquals( 16, updatedInventoryDto.amountOfIngredient( "Milk" ) ),
                () -> assertEquals( 11, updatedInventoryDto.amountOfIngredient( "Sugar" ) ),
                () -> assertEquals( 14, updatedInventoryDto.amountOfIngredient( "Chocolate" ) ) );

    }

}
