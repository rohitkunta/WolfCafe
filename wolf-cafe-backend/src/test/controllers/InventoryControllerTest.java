/**
 *
 */
package edu.ncsu.csc326.coffee_maker.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
import edu.ncsu.csc326.coffee_maker.dto.InventoryDto;
import edu.ncsu.csc326.coffee_maker.entity.Ingredient;
import edu.ncsu.csc326.coffee_maker.repositories.InventoryRepository;
import jakarta.persistence.EntityManager;

/**
 *
 */
@SpringBootTest
@AutoConfigureMockMvc
public class InventoryControllerTest {

    /** Mock MVC for testing controller */
    @Autowired
    private MockMvc             mvc;

    @Autowired
    private EntityManager       entityManager;

    /** Reference to EntityManager */
    @Autowired
    private InventoryRepository inventoryRepository;

    /**
     * Sets up the test case. We assume only one inventory row. Because
     * inventory is treated as a singleton (only one row), we must truncate for
     * auto increment on the id to work correctly.
     *
     * @throws java.lang.Exception
     *             if error
     */
    @BeforeEach
    public void setUp () throws Exception {

        entityManager.createNativeQuery( "ALTER TABLE inventory AUTO_INCREMENT = 1" ).executeUpdate();
        inventoryRepository.deleteAll();
    }

    /**
     * Tests the GET /api/inventory endpoint.
     *
     * @throws Exception
     *             if issue when running the test.
     */
    @Test
    @Transactional
    public void testGetInventory () throws Exception {
        final InventoryDto expectedInventory = new InventoryDto( 1L );

        mvc.perform( get( "/api/inventory" ) )
                .andExpect( content().string( TestUtils.asJsonString( expectedInventory ) ) )
                .andExpect( status().isOk() );
    }

    /**
     * Tests the PUT /api/inventory endpoint.
     *
     * @throws Exception
     *             if issue when running the test.
     */
    @Test
    @Transactional
    public void testUpdateInventory () throws Exception {
        final InventoryDto expectedInventory = new InventoryDto( 1L );

        mvc.perform( get( "/api/inventory" ) )
                .andExpect( content().string( TestUtils.asJsonString( expectedInventory ) ) )
                .andExpect( status().isOk() );

        final InventoryDto updatedInventory = new InventoryDto( 1L );

        updatedInventory.addIngredient( new Ingredient( "Coffee", 5 ) );
        updatedInventory.addIngredient( new Ingredient( "Milk", 10 ) );
        updatedInventory.addIngredient( new Ingredient( "Sugar", 15 ) );
        updatedInventory.addIngredient( new Ingredient( "Chocolate", 20 ) );

        System.out.print( TestUtils.asJsonString( updatedInventory ) );

        mvc.perform( put( "/api/inventory" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( updatedInventory ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( jsonPath( "$.ingredients[0].name" ).value( "Coffee" ) )
                .andExpect( jsonPath( "$.ingredients[0].amount" ).value( 5 ) )
                .andExpect( jsonPath( "$.ingredients[1].name" ).value( "Milk" ) )
                .andExpect( jsonPath( "$.ingredients[1].amount" ).value( 10 ) );

    }

}
