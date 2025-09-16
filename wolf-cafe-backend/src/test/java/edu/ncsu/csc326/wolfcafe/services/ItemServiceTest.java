/**
 *
 */
package edu.ncsu.csc326.wolfcafe.services;

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

import edu.ncsu.csc326.wolfcafe.dto.ItemDto;
import edu.ncsu.csc326.wolfcafe.entity.Ingredient;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.repository.ItemRepository;
import edu.ncsu.csc326.wolfcafe.service.ItemService;

/**
 *
 */
@SpringBootTest
public class ItemServiceTest {

    @Autowired
    private ItemService    itemService;

    /** Reference to item repository */
    @Autowired
    private ItemRepository itemRepository;

    /**
     * @throws java.lang.Exception
     */
    @BeforeEach
    void setUp () throws Exception {
        itemRepository.deleteAll();
    }

    /**
     * Test method for
     * {@link edu.ncsu.csc326.coffee_maker.services.ItemService#createItem(edu.ncsu.csc326.coffee_maker.dto.ItemDto)}.
     */
    @Test
    @Transactional
    void testCreateItem () {
        final ItemDto itemDto = new ItemDto();
        itemDto.setName( "Coffee" );
        itemDto.setPrice( 50 );
        final Ingredient milk = new Ingredient( "Milk", 3 );
        final Ingredient sugar = new Ingredient( "Sugar", 2 );
        itemDto.addIngredient( milk );
        itemDto.addIngredient( sugar );

        final ItemDto savedItem = itemService.addItem( itemDto );
        assertAll( "Item contents", () -> assertTrue( savedItem.getId() > 1L ),
                () -> assertEquals( "Coffee", savedItem.getName() ), () -> assertEquals( 50, savedItem.getPrice() ),
                () -> assertEquals( "Milk", savedItem.getIngredients().get( 0 ).getName() ),
                () -> assertEquals( 3, savedItem.getIngredients().get( 0 ).getAmount() ),
                () -> assertEquals( "Sugar", savedItem.getIngredients().get( 1 ).getName() ),
                () -> assertEquals( 2, savedItem.getIngredients().get( 1 ).getAmount() ) );

        final ItemDto retrievedItem = itemService.getItemById( savedItem.getId() );
        assertAll( "Item contents", () -> assertEquals( savedItem.getId(), retrievedItem.getId() ),
                () -> assertEquals( "Coffee", savedItem.getName() ), () -> assertEquals( 50, savedItem.getPrice() ),
                () -> assertEquals( "Milk", savedItem.getIngredients().get( 0 ).getName() ),
                () -> assertEquals( 3, savedItem.getIngredients().get( 0 ).getAmount() ),
                () -> assertEquals( "Sugar", savedItem.getIngredients().get( 1 ).getName() ),
                () -> assertEquals( 2, savedItem.getIngredients().get( 1 ).getAmount() ) );
        final ItemDto retrievedItem2 = itemService.getItemByName( savedItem.getName() );
        assertAll( "Item contents", () -> assertEquals( savedItem.getName(), retrievedItem2.getName() ),
                () -> assertEquals( "Coffee", savedItem.getName() ), () -> assertEquals( 50, savedItem.getPrice() ),
                () -> assertEquals( "Milk", savedItem.getIngredients().get( 0 ).getName() ),
                () -> assertEquals( 3, savedItem.getIngredients().get( 0 ).getAmount() ),
                () -> assertEquals( "Sugar", savedItem.getIngredients().get( 1 ).getName() ),
                () -> assertEquals( 2, savedItem.getIngredients().get( 1 ).getAmount() ) );
    }

    /**
     * Test method for
     * {@link edu.ncsu.csc326.coffee_maker.services.ItemService#isDuplicateName(java.lang.String)}.
     */
    @Test
    void testIsDuplicateName () {
        final ItemDto itemDto = new ItemDto();
        itemDto.setName( "Coffee" );
        itemDto.setPrice( 50 );
        final ItemDto savedItem = itemService.addItem( itemDto );
        assertTrue( itemService.isDuplicateName( savedItem.getName() ) );
        assertFalse( itemService.isDuplicateName( "flyers" ) );
    }

    /**
     * Test method for
     * {@link edu.ncsu.csc326.coffee_maker.services.ItemService#getAllItems()}.
     */
    @Test
    void testGetAllItems () {
        final ItemDto itemDto = new ItemDto();
        itemDto.setName( "Coffee" );
        itemDto.setPrice( 50 );
        final ItemDto savedItem = itemService.addItem( itemDto );
        final List<ItemDto> items = itemService.getAllItems();
        System.out.print( items.getFirst().getId() );
        assertEquals( "Coffee", items.getFirst().getName() );
        assertEquals( itemDto.getPrice(), items.getFirst().getPrice() );

    }

    /**
     * Test method for
     * {@link edu.ncsu.csc326.coffee_maker.services.ItemService#updateItem(java.lang.Long, edu.ncsu.csc326.coffee_maker.dto.ItemDto)}.
     */
    @Test
    void testUpdateItem () {
        final ItemDto initialItemDto = new ItemDto();
        initialItemDto.setName( "Coffee" );
        initialItemDto.setPrice( 50 );
        final ItemDto savedItem = itemService.addItem( initialItemDto );
        final ItemDto updatedItemDto = new ItemDto();
        updatedItemDto.setId( savedItem.getId() );
        updatedItemDto.setName( "Espresso" );
        updatedItemDto.setPrice( 60 );
        final ItemDto updatedItem = itemService.updateItem( savedItem.getId(), updatedItemDto );
        assertNotNull( updatedItem );
        assertEquals( savedItem.getId(), updatedItem.getId() );
        assertEquals( "Espresso", updatedItem.getName() );
        final ItemDto fetchedItem = itemService.getItemById( savedItem.getId() );
        assertEquals( "Espresso", fetchedItem.getName() );
    }

    /**
     * Test method for
     * {@link edu.ncsu.csc326.coffee_maker.services.ItemService#deleteItem(java.lang.Long)}.
     */
    @Test
    void testDeleteItem () {
        final ItemDto itemDto = new ItemDto();
        itemDto.setName( "Coffee" );
        itemDto.setPrice( 50 );
        final ItemDto savedItem = itemService.addItem( itemDto );
        itemService.deleteItem( savedItem.getId() );
        assertThrows( ResourceNotFoundException.class, () -> itemService.getItemById( savedItem.getId() ) );
        assertFalse( itemRepository.findById( savedItem.getId() ).isPresent() );
    }

}
