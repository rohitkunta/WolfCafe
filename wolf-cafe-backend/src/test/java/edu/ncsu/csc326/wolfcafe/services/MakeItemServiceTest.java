package edu.ncsu.csc326.wolfcafe.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc326.wolfcafe.dto.InventoryDto;
import edu.ncsu.csc326.wolfcafe.dto.ItemDto;
import edu.ncsu.csc326.wolfcafe.entity.Ingredient;
import edu.ncsu.csc326.wolfcafe.repository.ItemRepository;
import edu.ncsu.csc326.wolfcafe.service.InventoryService;
import edu.ncsu.csc326.wolfcafe.service.ItemService;
import edu.ncsu.csc326.wolfcafe.service.MakeItemService;
import jakarta.persistence.EntityManager;

@SpringBootTest
class MakeItemServiceTest {

    /** Reference to InventoryService (and InventoryServiceImpl). */
    @Autowired
    private MakeItemService  makeItemS;

    /** Reference to InventoryService (and InventoryServiceImpl). */
    @Autowired
    private InventoryService inventoryService;

    /** Reference to EntityManager */
    @Autowired
    private EntityManager    entityManager;

    @Autowired
    private ItemService      itemService;

    /** Reference to item repository */
    @Autowired
    private ItemRepository   itemRepository;

    // DONE: change all these itemDto's to work, look at Item*Test for
    // guidance

    /**
     * Item to be made that will work
     */
    final ItemDto            itemDto      = new ItemDto();

    /**
     * Item to be made that will fail due to not enough coffee ingredient
     */
    final ItemDto            itemDtoFail1 = new ItemDto();

    /**
     * Item to be made that will fail due to not enough milk ingredient
     */
    final ItemDto            itemDtoFail2 = new ItemDto();

    /**
     * Item to be made that will fail due to not enough sugar ingredient
     */
    final ItemDto            itemDtoFail3 = new ItemDto();

    /**
     * Item to be made that will fail due to not enough chocolate ingredient
     */
    final ItemDto            itemDtoFail4 = new ItemDto();

    // once we switch this to the repository, we have to save this dto to the
    // repository
    InventoryDto             inventoryDto = new InventoryDto( 1L );

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

        itemDto.setName( "Coffee1" );
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

    }

    // DONE: FOR ALL THESE TESTS, change it to using amountOfIngredient(String
    // name)
    // instead of the getCoffee and such

    @Test
    @Transactional
    void testMakeItem () {

        inventoryService.createInventory( inventoryDto );

        itemRepository.deleteAll();

        itemService.addItem( itemDto ); // adds a item to the
                                        // repository

        final boolean made = makeItemS.makeItem( inventoryDto, itemDto );

        assertTrue( made ); // item should have been made

        // asserting that inventory has been deducted the correct amount for
        // itemDto1
        final InventoryDto updatedInventoryDto = inventoryService.getInventory();
        assertAll( "InventoryDto contents after making", () -> assertEquals( 1L, updatedInventoryDto.getId() ),
                () -> assertEquals( 33, updatedInventoryDto.amountOfIngredient( "Coffee" ) ),
                () -> assertEquals( 16, updatedInventoryDto.amountOfIngredient( "Milk" ) ),
                () -> assertEquals( 11, updatedInventoryDto.amountOfIngredient( "Sugar" ) ),
                () -> assertEquals( 14, updatedInventoryDto.amountOfIngredient( "Chocolate" ) ) );

        final boolean made2 = makeItemS.makeItem( inventoryDto, itemDtoFail1 );

        assertFalse( made2 ); // item should not have been made

        final InventoryDto updatedInventoryDto2 = inventoryService.getInventory();
        // assert inventory didn't change
        assertAll( "InventoryDto contents after making", () -> assertEquals( 1L, updatedInventoryDto2.getId() ),
                () -> assertEquals( 33, updatedInventoryDto.amountOfIngredient( "Coffee" ) ),
                () -> assertEquals( 16, updatedInventoryDto.amountOfIngredient( "Milk" ) ),
                () -> assertEquals( 11, updatedInventoryDto.amountOfIngredient( "Sugar" ) ),
                () -> assertEquals( 14, updatedInventoryDto.amountOfIngredient( "Chocolate" ) ) );

        final boolean made3 = makeItemS.makeItem( inventoryDto, itemDtoFail2 );

        assertFalse( made3 ); // item should not have been made

        final InventoryDto updatedInventoryDto3 = inventoryService.getInventory();
        // assert inventory didn't change
        assertAll( "InventoryDto contents after making", () -> assertEquals( 1L, updatedInventoryDto3.getId() ),
                () -> assertEquals( 33, updatedInventoryDto.amountOfIngredient( "Coffee" ) ),
                () -> assertEquals( 16, updatedInventoryDto.amountOfIngredient( "Milk" ) ),
                () -> assertEquals( 11, updatedInventoryDto.amountOfIngredient( "Sugar" ) ),
                () -> assertEquals( 14, updatedInventoryDto.amountOfIngredient( "Chocolate" ) ) );

        final boolean made4 = makeItemS.makeItem( inventoryDto, itemDtoFail3 );

        assertFalse( made4 ); // item should not have been made

        final InventoryDto updatedInventoryDto4 = inventoryService.getInventory();
        // assert inventory didn't change
        assertAll( "InventoryDto contents after making", () -> assertEquals( 1L, updatedInventoryDto4.getId() ),
                () -> assertEquals( 33, updatedInventoryDto.amountOfIngredient( "Coffee" ) ),
                () -> assertEquals( 16, updatedInventoryDto.amountOfIngredient( "Milk" ) ),
                () -> assertEquals( 11, updatedInventoryDto.amountOfIngredient( "Sugar" ) ),
                () -> assertEquals( 14, updatedInventoryDto.amountOfIngredient( "Chocolate" ) ) );

        final boolean made5 = makeItemS.makeItem( inventoryDto, itemDtoFail4 );

        assertFalse( made5 ); // item should not have been made

        final InventoryDto updatedInventoryDto5 = inventoryService.getInventory();
        // assert inventory didn't change
        assertAll( "InventoryDto contents after making", () -> assertEquals( 1L, updatedInventoryDto5.getId() ),
                () -> assertEquals( 33, updatedInventoryDto.amountOfIngredient( "Coffee" ) ),
                () -> assertEquals( 16, updatedInventoryDto.amountOfIngredient( "Milk" ) ),
                () -> assertEquals( 11, updatedInventoryDto.amountOfIngredient( "Sugar" ) ),
                () -> assertEquals( 14, updatedInventoryDto.amountOfIngredient( "Chocolate" ) ) );

    }

}
