package edu.ncsu.csc326.wolfcafe.mapper;

import edu.ncsu.csc326.wolfcafe.dto.InventoryDto;
import edu.ncsu.csc326.wolfcafe.entity.Inventory;

/**
 * Converts between InventoryDto and Inventory entity.
 */
public class InventoryMapper {

    /**
     * Converts an Inventory entity to InventoryDto
     *
     * @param inventory
     *            Inventory to convert
     * @return InventoryDto object
     */
    public static InventoryDto mapToInventoryDto ( final Inventory inventory ) {
        // will need to change this when we add units
        final InventoryDto inventoryDto = new InventoryDto( inventory.getId() );

        for ( int i = 0; i < inventory.getIngredients().size(); i++ ) {
            inventoryDto.addIngredient( inventory.getIngredients().get( i ) );
        }

        return inventoryDto;

    }

    /**
     * Converts an InventoryDto to an Inventory entity
     *
     * @param inventoryDto
     *            InventoryDto to convert
     * @return Inventory entity
     */
    public static Inventory mapToInventory ( final InventoryDto inventoryDto ) {
        final Inventory inventory = new Inventory( inventoryDto.getId() );

        for ( int i = 0; i < inventoryDto.getIngredients().size(); i++ ) {
            inventory.addIngredient( inventoryDto.getIngredients().get( i ) );
        }

        return inventory;

    }
}
