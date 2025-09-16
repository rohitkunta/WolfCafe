package edu.ncsu.csc326.coffee_maker.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc326.coffee_maker.TestUtils;
import edu.ncsu.csc326.coffee_maker.dto.InventoryDto;
import edu.ncsu.csc326.coffee_maker.dto.RecipeDto;
import edu.ncsu.csc326.coffee_maker.entity.Ingredient;
import edu.ncsu.csc326.coffee_maker.repositories.RecipeRepository;
import edu.ncsu.csc326.coffee_maker.services.InventoryService;
import edu.ncsu.csc326.coffee_maker.services.RecipeService;
import jakarta.persistence.EntityManager;

@SpringBootTest
@AutoConfigureMockMvc
class MakeRecipeControllerTest {

    /** Mock MVC for testing controller */
    @Autowired
    private MockMvc          mvc;

    /** Reference to recipe repository */
    @Autowired
    private RecipeRepository recipeRepository;

    /** Reference to InventoryService (and InventoryServiceImpl). */
    @Autowired
    private InventoryService inventoryService;

    /** Reference to EntityManager */
    @Autowired
    private EntityManager    entityManager;

    @Autowired
    private RecipeService    recipeService;

    // DONE: change all these recipeDto's to work, look at Recipe*Test for
    // guidance

    /**
     * Recipe to be made that will work
     */
    final RecipeDto          recipeDto      = new RecipeDto( 0L, "Coffee", 50 );

    /**
     * Recipe to be made that will fail due to not enough coffee ingredient
     */
    final RecipeDto          recipeDtoFail1 = new RecipeDto( 0L, "Coffee2", 50 );

    /**
     * Recipe to be made that will fail due to not enough milk ingredient
     */
    final RecipeDto          recipeDtoFail2 = new RecipeDto( 0L, "Coffee3", 50 );

    /**
     * Recipe to be made that will fail due to not enough sugar ingredient
     */
    final RecipeDto          recipeDtoFail3 = new RecipeDto( 0L, "Coffee4", 50 );

    /**
     * Recipe to be made that will fail due to not enough chocolate ingredient
     */
    final RecipeDto          recipeDtoFail4 = new RecipeDto( 0L, "Coffee5", 50 );

    // once we switch this to the repository, we have to save this dto to the
    // repository
    InventoryDto             inventoryDto   = new InventoryDto( 1L );

    InventoryDto             inventoryDto2  = new InventoryDto( 1L );

    // will make the recipe
    Integer                  in             = 100;

    // will not make the recipe
    Integer                  in2            = 10;

    @BeforeEach
    void setUp () throws Exception {

        // DONE: instead of TRUNCATE TABLE, use the
        // inventoryRepository.deleteAll()
        // and the query above it like the inventory tests

        entityManager.createNativeQuery( "ALTER TABLE inventory AUTO_INCREMENT = 1" ).executeUpdate();
        inventoryService.deleteInventory();

        recipeRepository.deleteAll();

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

        inventoryDto2.addIngredient( new Ingredient( "Coffee", 0 ) );
        inventoryDto2.addIngredient( new Ingredient( "Milk", 0 ) );
        inventoryDto2.addIngredient( new Ingredient( "Sugar", 0 ) );
        inventoryDto2.addIngredient( new Ingredient( "Chocolate", 0 ) );

    }

    @Test
    @Transactional
    void testMakeRecipe () throws Exception {

        inventoryService.createInventory( inventoryDto );

        recipeRepository.deleteAll();

        final String recipe = mvc.perform( get( "/api/recipes" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();
        assertFalse( recipe.contains( "Mocha" ) );

        recipeService.createRecipe( recipeDto ); // adds a recipe to the
        // repository so we can make it

        // makes the recipe and checks if the change returned was correct
        final String change = mvc
                .perform( post( "/api/makerecipe/Coffee" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( in ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();

        assertEquals( change, "50" );

        // makes the recipe and checks if the change returned was correct
        final String change2 = mvc
                .perform( post( "/api/makerecipe/Coffee" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( in2 ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().is4xxClientError() ).andReturn().getResponse().getContentAsString();

        assertEquals( "10", change2 ); // the insufficient amount we put was
                                       // returned

        // makes the recipe and checks if the change returned was correct
        final String change3 = mvc
                .perform( post( "/api/makerecipe/Coffee" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( "Hello World" ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().is4xxClientError() ).andReturn().getResponse().getContentAsString();

        assertEquals( "", change3 ); // the insufficient amount we
                                     // put was
        // returned
        inventoryService.updateInventory( inventoryDto2 ); // contains 0 of
                                                           // everything, should
                                                           // invoke a Not
                                                           // Enough Inventory
                                                           // error

        // should return a 400 error saying not enough inventory
        final String change4 = mvc
                .perform( post( "/api/makerecipe/Coffee" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( in ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().is4xxClientError() ).andReturn().getResponse().getContentAsString();

        assertEquals( "100", change4 ); // the insufficient amount we
                                        // put was

    }

}
