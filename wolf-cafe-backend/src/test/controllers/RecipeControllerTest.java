package edu.ncsu.csc326.coffee_maker.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import com.jayway.jsonpath.JsonPath;

import edu.ncsu.csc326.coffee_maker.TestUtils;
import edu.ncsu.csc326.coffee_maker.dto.RecipeDto;
import edu.ncsu.csc326.coffee_maker.entity.Ingredient;
import edu.ncsu.csc326.coffee_maker.repositories.RecipeRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class RecipeControllerTest {
    /** Mock MVC for testing controller */
    @Autowired
    private MockMvc          mvc;

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
    }

    @Test
    @Transactional
    public void testGetRecipes () throws Exception {
        final String recipe = mvc.perform( get( "/api/recipes" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();
        assertFalse( recipe.contains( "Mocha" ) );

    }

    @Test
    @Transactional
    public void testCreateRecipe () throws Exception {
        final RecipeDto recipeDto = new RecipeDto( 0L, "Mocha", 200 );
        final Ingredient coffee = new Ingredient( "Coffee", 5 );
        final Ingredient milk = new Ingredient( "Milk", 3 );
        recipeDto.addIngredient( coffee );
        recipeDto.addIngredient( milk );

        mvc.perform( post( "/api/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( recipeDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.name" ).value( "Mocha" ) )
                .andExpect( jsonPath( "$.price" ).value( "200" ) );

        // Check that the correct recipe was created
        final String recipe = mvc.perform( get( "/api/recipes" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();
        assertTrue( recipe.contains( "Mocha" ) );
        assertTrue( recipe.contains( "\"price\":200" ) );
        assertTrue( recipe.contains( "\"name\":\"Coffee\",\"units\":null,\"amount\":5" ) );
        assertTrue( recipe.contains( "\"name\":\"Milk\",\"units\":null,\"amount\":3" ) );

        // Check that a duplicate named recipe can not be added
        final RecipeDto duplicateDto = new RecipeDto( 0L, "Mocha", 200 );
        duplicateDto.addIngredient( milk );

        mvc.perform( post( "/api/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( duplicateDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isConflict() );

        // Check that a maximum of 3 recipes can be added
        final RecipeDto recipeDto2 = new RecipeDto( 0L, "Cappuccino", 300 );
        final RecipeDto recipeDto3 = new RecipeDto( 0L, "Americano", 100 );
        final RecipeDto recipeDto4 = new RecipeDto( 0L, "Espresso", 200 );
        mvc.perform( post( "/api/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( recipeDto2 ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() );
        mvc.perform( post( "/api/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( recipeDto3 ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() );
        mvc.perform( post( "/api/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( recipeDto4 ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isInsufficientStorage() );
    }

    @Test
    @Transactional
    public void testGetRecipe () throws Exception {
        final RecipeDto recipeDto = new RecipeDto( 0L, "Mocha", 200 );
        final Ingredient coffee = new Ingredient( "Coffee", 5 );
        final Ingredient milk = new Ingredient( "Milk", 3 );
        recipeDto.addIngredient( coffee );
        recipeDto.addIngredient( milk );

        mvc.perform( post( "/api/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( recipeDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();

        final String recipe = mvc.perform( get( "/api/recipes/Mocha" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();
        assertTrue( recipe.contains( "Mocha" ) );
        assertTrue( recipe.contains( "\"price\":200" ) );
        assertTrue( recipe.contains( "\"name\":\"Coffee\",\"units\":null,\"amount\":5" ) );
        assertTrue( recipe.contains( "\"name\":\"Milk\",\"units\":null,\"amount\":3" ) );
        // Get a recipe that does not exist
        final String emptyRecipe = mvc.perform( get( "/api/recipes/Cappuccino" ) ).andDo( print() )
                .andExpect( status().isNotFound() ).andReturn().getResponse().getContentAsString();
        assertEquals( "", emptyRecipe );

    }

    @Test
    @Transactional
    public void testDeleteRecipe () throws Exception {
        final RecipeDto recipeDto = new RecipeDto( 0L, "Mocha", 200 );

        // Add a new recipe
        mvc.perform( post( "/api/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( recipeDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.name" ).value( "Mocha" ) )
                .andExpect( jsonPath( "$.price" ).value( "200" ) );

        final String recipe = mvc.perform( get( "/api/recipes/Mocha" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();
        // CHATGPT WAS USED for parsing the ID from the new recipe's JSON
        // Prompt: "mockmvc return part of a json path for example an id"
        final long id = JsonPath.parse( recipe ).read( "$.id", Long.class );

        final String path = "/api/recipes/" + id;
        mvc.perform( MockMvcRequestBuilders.delete( path ) );

        // There shouldn't be any recipes after deletion
        final String emptyRecipe = mvc.perform( get( "/api/recipes" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();
        assertFalse( emptyRecipe.contains( "Mocha" ) );

    }

    @Test
    @Transactional
    public void testUpdateRecipe () throws Exception {
        final RecipeDto recipeDto = new RecipeDto( 0L, "Mocha", 200 );
        final Ingredient coffee = new Ingredient( "Coffee", 5 );
        final Ingredient milk = new Ingredient( "Milk", 3 );
        recipeDto.addIngredient( coffee );
        recipeDto.addIngredient( milk );

        mvc.perform( post( "/api/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( recipeDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.name" ).value( "Mocha" ) )
                .andExpect( jsonPath( "$.price" ).value( "200" ) );

        final String oldRecipe = mvc.perform( get( "/api/recipes/Mocha" ) ).andDo( print() )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
        final long id = JsonPath.parse( oldRecipe ).read( "$.id", Long.class );

        final Ingredient sugar = new Ingredient( "Sugar", 2 );
        recipeDto.addIngredient( sugar );
        recipeDto.setId( id );

        mvc.perform( MockMvcRequestBuilders.put( "/api/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( recipeDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.name" ).value( "Mocha" ) )
                .andExpect( jsonPath( "$.price" ).value( "200" ) );

        final String newRecipe = mvc.perform( get( "/api/recipes/Mocha" ) ).andDo( print() )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
        assertTrue( newRecipe.contains( "Mocha" ) );
        assertTrue( newRecipe.contains( "\"price\":200" ) );
        assertTrue( newRecipe.contains( "\"name\":\"Coffee\",\"units\":null,\"amount\":5" ) );
        assertTrue( newRecipe.contains( "\"name\":\"Milk\",\"units\":null,\"amount\":3" ) );
        assertTrue( newRecipe.contains( "\"name\":\"Sugar\",\"units\":null,\"amount\":2" ) );

        recipeDto.removeIngredient( milk );
        mvc.perform( MockMvcRequestBuilders.put( "/api/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( recipeDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.name" ).value( "Mocha" ) )
                .andExpect( jsonPath( "$.price" ).value( "200" ) );
        final String newRecipe2 = mvc.perform( get( "/api/recipes/Mocha" ) ).andDo( print() )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
        assertTrue( newRecipe2.contains( "Mocha" ) );
        assertTrue( newRecipe2.contains( "\"price\":200" ) );
        assertTrue( newRecipe2.contains( "\"name\":\"Coffee\",\"units\":null,\"amount\":5" ) );
        assertFalse( newRecipe2.contains( "\"name\":\"Milk\",\"units\":null,\"amount\":3" ) );
        assertTrue( newRecipe2.contains( "\"name\":\"Sugar\",\"units\":null,\"amount\":2" ) );

    }
}
