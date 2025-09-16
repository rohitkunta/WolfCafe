package edu.ncsu.csc326.coffee_maker.repositories;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import edu.ncsu.csc326.coffee_maker.entity.Ingredient;
import edu.ncsu.csc326.coffee_maker.entity.Inventory;
import jakarta.persistence.EntityManager;

/**
 * Tests InventoryRepository. Uses the real database - not an embedded one.
 */
@DataJpaTest
@AutoConfigureTestDatabase ( replace = Replace.NONE )
public class InventoryRepositoryTest {

    /** Reference to inventory repository */
    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private EntityManager       entityManager;

    /** Reference to inventory */
    private Inventory           inventory;

    /**
     * Sets up the test case. We assume only one inventory row.
     *
     * @throws java.lang.Exception
     *             if error
     */
    @BeforeEach
    public void setUp () throws Exception {
        entityManager.createNativeQuery( "ALTER TABLE inventory AUTO_INCREMENT = 1" ).executeUpdate();
        inventoryRepository.deleteAll();

        // Make sure that Inventory always has an id of 1L.
        inventory = new Inventory( 1L );
        inventory.addIngredient( new Ingredient( "Coffee", 20 ) );
        inventory.addIngredient( new Ingredient( "Milk", 14 ) );
        inventory.addIngredient( new Ingredient( "Sugar", 32 ) );
        inventory.addIngredient( new Ingredient( "Chocolate", 10 ) );
        inventoryRepository.save( inventory );
    }

    /**
     * Test saving the inventory and retrieving from the repository.
     */
    @Test
    public void testSaveAndGetInventory () {
        final Inventory fetchedInventory = inventoryRepository.findById( 1L ).get();
        assertAll( "Inventory contents", () -> assertEquals( 1L, fetchedInventory.getId() ) );

        final Ingredient i1 = fetchedInventory.getIngredients().get( 0 );
        final Ingredient i2 = fetchedInventory.getIngredients().get( 1 );
        final Ingredient i3 = fetchedInventory.getIngredients().get( 2 );
        final Ingredient i4 = fetchedInventory.getIngredients().get( 3 );

        assertAll( "Ingredient contents", () -> assertEquals( "Coffee", i1.getName() ),
                () -> assertEquals( 20, i1.getAmount() ) );

        assertAll( "Ingredient contents", () -> assertEquals( "Milk", i2.getName() ),
                () -> assertEquals( 14, i2.getAmount() ) );

        assertAll( "Ingredient contents", () -> assertEquals( "Sugar", i3.getName() ),
                () -> assertEquals( 32, i3.getAmount() ) );

        assertAll( "Ingredient contents", () -> assertEquals( "Chocolate", i4.getName() ),
                () -> assertEquals( 10, i4.getAmount() ) );

    }

    /**
     * Tests updating the inventory
     */
    @Test
    public void testUpdateInventory () {
        final Inventory fetchedInventory = inventoryRepository.findById( 1L ).get();

        fetchedInventory.getIngredients().get( 0 ).setAmount( 13 );
        fetchedInventory.getIngredients().get( 1 ).setAmount( 14 );
        fetchedInventory.getIngredients().get( 2 ).setAmount( 27 );
        fetchedInventory.getIngredients().get( 3 ).setAmount( 23 );

        final Inventory updatedInventory = inventoryRepository.save( fetchedInventory );

        final Ingredient i1 = updatedInventory.getIngredients().get( 0 );
        final Ingredient i2 = updatedInventory.getIngredients().get( 1 );
        final Ingredient i3 = updatedInventory.getIngredients().get( 2 );
        final Ingredient i4 = updatedInventory.getIngredients().get( 3 );

        assertAll( "Ingredient contents", () -> assertEquals( "Coffee", i1.getName() ),
                () -> assertEquals( 13, i1.getAmount() ) );

        assertAll( "Ingredient contents", () -> assertEquals( "Milk", i2.getName() ),
                () -> assertEquals( 14, i2.getAmount() ) );

        assertAll( "Ingredient contents", () -> assertEquals( "Sugar", i3.getName() ),
                () -> assertEquals( 27, i3.getAmount() ) );

        assertAll( "Ingredient contents", () -> assertEquals( "Chocolate", i4.getName() ),
                () -> assertEquals( 23, i4.getAmount() ) );
    }
}
