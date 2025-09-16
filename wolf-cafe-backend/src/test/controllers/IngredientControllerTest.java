package edu.ncsu.csc326.coffee_maker.controllers;

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
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc326.coffee_maker.TestUtils;
import edu.ncsu.csc326.coffee_maker.dto.IngredientDto;
import edu.ncsu.csc326.coffee_maker.repositories.IngredientRepository;
import edu.ncsu.csc326.coffee_maker.repositories.InventoryRepository;
import jakarta.persistence.EntityManager;

@SpringBootTest
@AutoConfigureMockMvc
public class IngredientControllerTest {

    /** Mock MVC for testing controller */
    @Autowired
    private MockMvc              mvc;

    /** Reference to entity manager */
    @Autowired
    private EntityManager        entityManager;

    /** Reference to inventory repository */
    @Autowired
    private InventoryRepository  inventoryRepository;

    /** Reference to recipe repository */
    @Autowired
    private IngredientRepository ingredientRepository;

    /**
     * Sets up the test case.
     *
     * @throws java.lang.Exception
     *             if error
     */
    @BeforeEach
    public void setUp () throws Exception {
        ingredientRepository.deleteAll();
        entityManager.createNativeQuery( "ALTER TABLE inventory AUTO_INCREMENT = 1" ).executeUpdate();
        inventoryRepository.deleteAll();
    }

    @Test
    @Transactional
    void testCreateIngredient () throws Exception {
        final IngredientDto ingredient1 = new IngredientDto( "Coffee", 5 );

        System.out.print( TestUtils.asJsonString( ingredient1 ) );

        mvc.perform( post( "/api/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( ingredient1 ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.amount" ).value( "5" ) )
                .andExpect( jsonPath( "$.name" ).value( "Coffee" ) );

        mvc.perform( post( "/api/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( "hello" ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isBadRequest() );
    }

    @Test
    @Transactional
    void testGetIngredient () throws Exception {

        final IngredientDto ingredientDto = new IngredientDto( "Coffee", 5 );

        mvc.perform( post( "/api/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( ingredientDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.name" ).value( "Coffee" ) );

        final String ingredient = mvc.perform( get( "/api/ingredients/Coffee" ) ).andDo( print() )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();

        assertTrue( ingredient.contains( "Coffe" ) );
        assertTrue( ingredient.contains( "\"amount\":5" ) );

        final String ingredientAll = mvc.perform( get( "/api/ingredients" ) ).andDo( print() )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();

        System.out.print( ingredientAll );
    }

}
