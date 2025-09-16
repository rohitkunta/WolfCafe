package edu.ncsu.csc326.wolfcafe.controller;

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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc326.wolfcafe.TestUtils;
import edu.ncsu.csc326.wolfcafe.dto.InventoryDto;
import edu.ncsu.csc326.wolfcafe.dto.ItemDto;
import edu.ncsu.csc326.wolfcafe.entity.Ingredient;
import edu.ncsu.csc326.wolfcafe.repository.ItemRepository;
import edu.ncsu.csc326.wolfcafe.service.InventoryService;
import edu.ncsu.csc326.wolfcafe.service.ItemService;
import jakarta.persistence.EntityManager;

@SpringBootTest
@AutoConfigureMockMvc
class MakeItemControllerTest {

    /** Mock MVC for testing controller */
    @Autowired
    private MockMvc          mvc;

    /** Reference to item repository */
    @Autowired
    private ItemRepository   itemRepository;

    /** Reference to InventoryService (and InventoryServiceImpl). */
    @Autowired
    private InventoryService inventoryService;

    /** Reference to EntityManager */
    @Autowired
    private EntityManager    entityManager;

    @Autowired
    private ItemService      itemService;

    // DONE: change all these itemDto's to work, look at Item*Test for
    // guidance

    /**
     * Item to be made that will work
     */
    final ItemDto            itemDto       = new ItemDto();

    /**
     * Item to be made that will fail due to not enough coffee ingredient
     */
    final ItemDto            itemDtoFail1  = new ItemDto();

    /**
     * Item to be made that will fail due to not enough milk ingredient
     */
    final ItemDto            itemDtoFail2  = new ItemDto();

    /**
     * Item to be made that will fail due to not enough sugar ingredient
     */
    final ItemDto            itemDtoFail3  = new ItemDto();

    /**
     * Item to be made that will fail due to not enough chocolate ingredient
     */
    final ItemDto            itemDtoFail4  = new ItemDto();

    // once we switch this to the repository, we have to save this dto to the
    // repository
    InventoryDto             inventoryDto  = new InventoryDto( 1L );

    InventoryDto             inventoryDto2 = new InventoryDto( 1L );

    // will make the item
    Integer                  in            = 100;

    // will not make the item
    Integer                  in2           = 10;

    @BeforeEach
    void setUp () throws Exception {

        // DONE: instead of TRUNCATE TABLE, use the
        // inventoryRepository.deleteAll()
        // and the query above it like the inventory tests

        entityManager.createNativeQuery( "ALTER TABLE inventory AUTO_INCREMENT = 1" ).executeUpdate();
        inventoryService.deleteInventory();

        itemRepository.deleteAll();

        itemDto.setName( "Coffee" );
        itemDto.setPrice( 50 );
        itemDto.addIngredient( new Ingredient( "Coffee", 2 ) );
        itemDto.addIngredient( new Ingredient( "Milk", 1 ) );
        itemDto.addIngredient( new Ingredient( "Sugar", 1 ) );
        itemDto.addIngredient( new Ingredient( "Chocolate", 0 ) );

        itemDtoFail1.setName( "Coffee2" );
        itemDtoFail1.setPrice( 50 );
        itemDtoFail1.addIngredient( new Ingredient( "Coffee", 40 ) );
        itemDtoFail1.addIngredient( new Ingredient( "Milk", 1 ) );
        itemDtoFail1.addIngredient( new Ingredient( "Sugar", 1 ) );
        itemDtoFail1.addIngredient( new Ingredient( "Chocolate", 0 ) );

        itemDtoFail2.setName( "Coffee3" );
        itemDtoFail2.setPrice( 50 );
        itemDtoFail2.addIngredient( new Ingredient( "Coffee", 2 ) );
        itemDtoFail2.addIngredient( new Ingredient( "Milk", 40 ) );
        itemDtoFail2.addIngredient( new Ingredient( "Sugar", 1 ) );
        itemDtoFail2.addIngredient( new Ingredient( "Chocolate", 0 ) );

        itemDtoFail3.setName( "Coffee4" );
        itemDtoFail3.setPrice( 50 );
        itemDtoFail3.addIngredient( new Ingredient( "Coffee", 2 ) );
        itemDtoFail3.addIngredient( new Ingredient( "Milk", 1 ) );
        itemDtoFail3.addIngredient( new Ingredient( "Sugar", 40 ) );
        itemDtoFail3.addIngredient( new Ingredient( "Chocolate", 0 ) );

        itemDtoFail4.setName( "Coffee5" );
        itemDtoFail4.setPrice( 50 );
        itemDtoFail4.addIngredient( new Ingredient( "Coffee", 2 ) );
        itemDtoFail4.addIngredient( new Ingredient( "Milk", 1 ) );
        itemDtoFail4.addIngredient( new Ingredient( "Sugar", 1 ) );
        itemDtoFail4.addIngredient( new Ingredient( "Chocolate", 40 ) );

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
    @WithMockUser ( username = "staff", roles = "STAFF" )
    void testMakeItem () throws Exception {

        inventoryService.createInventory( inventoryDto );

        itemRepository.deleteAll();

        final String item = mvc.perform( get( "/api/items" ) ).andDo( print() ).andExpect( status().isOk() ).andReturn()
                .getResponse().getContentAsString();
        assertFalse( item.contains( "Mocha" ) );

        itemService.addItem( itemDto ); // adds a item to the
        // repository so we can make it

        // makes the item and checks if the change returned was correct
        final String change = mvc
                .perform( post( "/api/makeitem/Coffee" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( in ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();

        assertEquals( change, "50.0" );

        // makes the item and checks if the change returned was correct
        final String change2 = mvc
                .perform( post( "/api/makeitem/Coffee" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( in2 ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().is4xxClientError() ).andReturn().getResponse().getContentAsString();

        assertEquals( "10.0", change2 ); // the insufficient amount we put was
                                         // returned

        // makes the item and checks if the change returned was correct
        final String change3 = mvc
                .perform( post( "/api/makeitem/Coffee" ).contentType( MediaType.APPLICATION_JSON )
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
                .perform( post( "/api/makeitem/Coffee" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( in ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().is4xxClientError() ).andReturn().getResponse().getContentAsString();

        assertEquals( "100.0", change4 ); // the insufficient amount we
                                          // put was

    }

}
