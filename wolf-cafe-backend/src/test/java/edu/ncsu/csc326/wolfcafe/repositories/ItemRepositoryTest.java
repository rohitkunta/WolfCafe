/**
 *
 */
package edu.ncsu.csc326.wolfcafe.repositories;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import edu.ncsu.csc326.wolfcafe.entity.Ingredient;
import edu.ncsu.csc326.wolfcafe.entity.Item;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.repository.ItemRepository;

/**
 * Tests Item repository
 */
@DataJpaTest
@AutoConfigureTestDatabase ( replace = Replace.NONE )
class ItemRepositoryTest {

    /** Reference to item repository */
    @Autowired
    private ItemRepository itemRepository;

    /**
     * Sets up the test case.
     *
     * @throws java.lang.Exception
     *             if error
     */
    @BeforeEach
    public void setUp () throws Exception {

        itemRepository.deleteAll();

        Item item1 = itemRepository.findByName("Coffee")
                .orElse(new Item());
        item1.setName("Coffee");
        item1.setPrice(50);
        
        
        Item item2 = itemRepository.findByName("Latte")
                .orElse(new Item());
        item2.setName("Latte");
        item2.setPrice(100);

        itemRepository.save( item1 );
        itemRepository.save( item2 );
    }

    @Test
    public void testGetItemByName () {
        final Item item = itemRepository.findByName( "Coffee" )
                .orElseThrow( () -> new ResourceNotFoundException( "Item not found " ) );
        final Item actualItem = item;
        assertAll( "Item contents", () -> assertEquals( "Coffee", actualItem.getName() ),
                () -> assertEquals( 50, actualItem.getPrice() ) );

        final Optional<Item> item2 = itemRepository.findByName( "Latte" );
        final Item actualItem2 = item2.get();
        assertAll( "Item contents", () -> assertEquals( "Latte", actualItem2.getName() ),
                () -> assertEquals( 100, actualItem2.getPrice() ) );
    }

    @Test
    public void testGetItemByNameInvalid () {
        final Optional<Item> item = itemRepository.findByName( "Unknown" );
        assertTrue( item.isEmpty() );
    }

    @Test
    public void testAddIngredients () {
        final Item item1 = new Item();
        item1.setId( 1L );
        item1.setName( "Pumpkin Spice Latte" );
        item1.setPrice( 500 );
        item1.addIngredient( new Ingredient( "Coffee", 3 ) );
        item1.addIngredient( new Ingredient( "Pumpkin Spice", 2 ) );
        item1.addIngredient( new Ingredient( "Milk", 1 ) );

        final Item savedItem = itemRepository.save( item1 );
        final Optional<Item> retrievedItem = itemRepository.findById( savedItem.getId() );
        assertAll( "Item contents", () -> assertEquals( savedItem.getId(), retrievedItem.get().getId() ),
                () -> assertEquals( "Pumpkin Spice Latte", retrievedItem.get().getName() ),
                () -> assertEquals( 500, retrievedItem.get().getPrice() ),
                () -> assertEquals( 3, retrievedItem.get().getIngredients().size() ) );

        final Ingredient i1 = retrievedItem.get().getIngredients().get( 0 );
        final Ingredient i2 = retrievedItem.get().getIngredients().get( 1 );
        final Ingredient i3 = retrievedItem.get().getIngredients().get( 2 );

        assertAll( "Ingredient contents", () -> assertEquals( "Coffee", i1.getName() ),
                () -> assertEquals( 3, i1.getAmount() ) );

        assertAll( "Ingredient contents", () -> assertEquals( "Pumpkin Spice", i2.getName() ),
                () -> assertEquals( 2, i2.getAmount() ) );

        assertAll( "Ingredient contents", () -> assertEquals( "Milk", i3.getName() ),
                () -> assertEquals( 1, i3.getAmount() ) );
    }

}
