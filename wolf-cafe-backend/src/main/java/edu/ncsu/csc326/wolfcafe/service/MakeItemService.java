package edu.ncsu.csc326.wolfcafe.service;

import edu.ncsu.csc326.wolfcafe.dto.InventoryDto;
import edu.ncsu.csc326.wolfcafe.dto.ItemDto;

/**
 * Interface defining the make recipe behaviors. Nothing to change here for UC6
 */
public interface MakeItemService {

    /**
     * Removes the ingredients used to make the specified item. Assumes that the
     * user has checked that there are enough ingredients to make
     *
     * @param inventoryDto
     *            current inventory
     * @param itemDto
     *            recipe to make
     * @return updated inventory
     */
    boolean makeItem ( InventoryDto inventoryDto, ItemDto itemDto );

}
