package edu.ncsu.csc326.wolfcafe.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc326.wolfcafe.dto.InventoryDto;
import edu.ncsu.csc326.wolfcafe.entity.Ingredient;
import edu.ncsu.csc326.wolfcafe.service.InventoryService;
import jakarta.persistence.EntityManager;

/**
 * Tests InventoryServiceImpl.
 */
@SpringBootTest
public class InventoryServiceTest {

    /** Reference to InventoryService (and InventoryServiceImpl). */
    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private EntityManager    entityManager;

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
        inventoryService.deleteInventory();
    }

    /**
     * Tests InventoryService.createInventory().
     */
    @Test
    @Transactional
    public void testCreateInventory () {
        final InventoryDto inventoryDto = new InventoryDto( 1L );

        // add ingredients
        inventoryDto.addIngredient( new Ingredient( "Coffee", 5 ) );
        inventoryDto.addIngredient( new Ingredient( "Milk", 9 ) );
        inventoryDto.addIngredient( new Ingredient( "Sugar", 14 ) );
        inventoryDto.addIngredient( new Ingredient( "Chocolate", 23 ) );

        final InventoryDto createdInventoryDto = inventoryService.createInventory( inventoryDto );

        final Ingredient i1 = createdInventoryDto.getIngredients().get( 0 );
        final Ingredient i2 = createdInventoryDto.getIngredients().get( 1 );
        final Ingredient i3 = createdInventoryDto.getIngredients().get( 2 );
        final Ingredient i4 = createdInventoryDto.getIngredients().get( 3 );

        assertAll( "Ingredient contents", () -> assertEquals( "Coffee", i1.getName() ),
                () -> assertEquals( 5, i1.getAmount() ) );

        assertAll( "Ingredient contents", () -> assertEquals( "Milk", i2.getName() ),
                () -> assertEquals( 9, i2.getAmount() ) );

        assertAll( "Ingredient contents", () -> assertEquals( "Sugar", i3.getName() ),
                () -> assertEquals( 14, i3.getAmount() ) );

        assertAll( "Ingredient contents", () -> assertEquals( "Chocolate", i4.getName() ),
                () -> assertEquals( 23, i4.getAmount() ) );
    }

    /**
     * Tests InventoryService.updateInventory()
     */
    @Test
    @Transactional
    public void testUpdateInventory () {
        final InventoryDto inventoryDto2 = new InventoryDto( 1L );

        // add ingredients
        inventoryDto2.addIngredient( new Ingredient( "Coffee", 5 ) );
        inventoryDto2.addIngredient( new Ingredient( "Milk", 9 ) );
        inventoryDto2.addIngredient( new Ingredient( "Sugar", 14 ) );
        inventoryDto2.addIngredient( new Ingredient( "Chocolate", 23 ) );

        inventoryService.createInventory( inventoryDto2 );

        final InventoryDto inventoryDto = inventoryService.getInventory();

        inventoryDto.getIngredients().get( 0 ).setAmount( 35 );
        inventoryDto.getIngredients().get( 1 ).setAmount( 17 );
        inventoryDto.getIngredients().get( 2 ).setAmount( 12 );
        inventoryDto.getIngredients().get( 3 ).setAmount( 14 );

        final InventoryDto updatedInventoryDto = inventoryService.updateInventory( inventoryDto );

        final Ingredient i1 = updatedInventoryDto.getIngredients().get( 0 );
        final Ingredient i2 = updatedInventoryDto.getIngredients().get( 1 );
        final Ingredient i3 = updatedInventoryDto.getIngredients().get( 2 );
        final Ingredient i4 = updatedInventoryDto.getIngredients().get( 3 );

        assertAll( "Ingredient contents", () -> assertEquals( "Coffee", i1.getName() ),
                () -> assertEquals( 35, i1.getAmount() ) );

        assertAll( "Ingredient contents", () -> assertEquals( "Milk", i2.getName() ),
                () -> assertEquals( 17, i2.getAmount() ) );

        assertAll( "Ingredient contents", () -> assertEquals( "Sugar", i3.getName() ),
                () -> assertEquals( 12, i3.getAmount() ) );

        assertAll( "Ingredient contents", () -> assertEquals( "Chocolate", i4.getName() ),
                () -> assertEquals( 14, i4.getAmount() ) );
    }
}
