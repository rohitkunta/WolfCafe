package edu.ncsu.csc326.wolfcafe.service.impl;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.ncsu.csc326.wolfcafe.dto.ItemDto;
import edu.ncsu.csc326.wolfcafe.entity.Ingredient;
import edu.ncsu.csc326.wolfcafe.entity.Item;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.repository.ItemRepository;
import edu.ncsu.csc326.wolfcafe.service.ItemService;
import lombok.AllArgsConstructor;

/**
 * Implemented item service
 */
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    @Autowired
    private final ItemRepository itemRepository;

    @Autowired
    private final ModelMapper    modelMapper;

    /**
     * Adds given item
     *
     * @param itemDto
     *            item to add
     * @return added item
     */
    @Override
    public ItemDto addItem ( final ItemDto itemDto ) {
        final Item item = modelMapper.map( itemDto, Item.class );
        final Item savedItem = itemRepository.save( item );
        return modelMapper.map( savedItem, ItemDto.class );
    }

    /**
     * Gets item by id
     *
     * @param id
     *            id of item to get
     * @return returned item
     */
    @Override
    public ItemDto getItemById ( final Long id ) {
        final Item item = itemRepository.findById( id )
                .orElseThrow( () -> new ResourceNotFoundException( "Item not found with id " + id ) );
        return modelMapper.map( item, ItemDto.class );
    }

    /**
     * Returns the recipe with the given name
     *
     * @param recipeName
     *            recipe's name
     * @return the recipe with the given name.
     * @throws ResourceNotFoundException
     *             if the recipe doesn't exist
     */
    @Override
    public ItemDto getItemByName ( final String itemName ) {
        final Item item = itemRepository.findByName( itemName )
                .orElseThrow( () -> new ResourceNotFoundException( "Item does not exist with name " + itemName ) );
        return modelMapper.map( item, ItemDto.class );
    }

    /**
     * Returns all items
     *
     * @return all items
     */
    @Override
    public List<ItemDto> getAllItems () {
        final List<Item> items = itemRepository.findAll();
        return items.stream().map( ( item ) -> modelMapper.map( item, ItemDto.class ) ).collect( Collectors.toList() );
    }

    @Override
    public boolean isDuplicateName ( final String itemName ) {
        try {
            getItemByName( itemName );
            return true;
        }
        catch ( final ResourceNotFoundException e ) {
            return false;
        }
    }

    /**
     * Updates the item with the given id
     *
     * @param id
     *            id of item to update
     * @param itemDto
     *            information of item to update
     * @return updated item
     */
    @Override
    public ItemDto updateItem ( final Long id, final ItemDto itemDto ) {
        final Item item = itemRepository.findById( id )
                .orElseThrow( () -> new ResourceNotFoundException( "Item not found with id " + id ) );
        item.setName( itemDto.getName() );
        item.setDescription( itemDto.getDescription() );
        item.setPrice( itemDto.getPrice() );
        final List<Ingredient> newIngredients = itemDto.getIngredients();
        final List<Ingredient> currentIngredients = item.getIngredients();
        for ( final Ingredient newIngredient : newIngredients ) {
            // Check to see if the list contains each ingredient in the updated
            // list, and adds new ingredients
            if ( !currentIngredients.contains( newIngredient ) ) {
                currentIngredients.add( newIngredient );
            }
            else {
                // If the ingredient is already in the recipe, update the unit
                // amount if needed
                final Ingredient currentIngredient = currentIngredients
                        .get( currentIngredients.indexOf( newIngredient ) );
                if ( currentIngredient.getAmount() != newIngredient.getAmount() ) {
                    currentIngredient.setAmount( newIngredient.getAmount() );
                }
            }
        }
        // Finally, remove any ingredients from the updated list as needed
        // Using an iterator to avoid concurrent modification of the ingredient
        // list
        final Iterator<Ingredient> iterator = currentIngredients.iterator();
        while ( iterator.hasNext() ) {
            final Ingredient currentIngredient = iterator.next();
            if ( !newIngredients.contains( currentIngredient ) ) {
                iterator.remove();
            }
        }

        final Item updatedItem = itemRepository.save( ( item ) );
        return modelMapper.map( updatedItem, ItemDto.class );
    }

    /**
     * Deletes the item with the given id
     *
     * @param id
     *            id of item to delete
     */
    @Override
    public void deleteItem ( final Long id ) {
        itemRepository.findById( id )
                .orElseThrow( () -> new ResourceNotFoundException( "Item not found with id " + id ) );
        itemRepository.deleteById( id );
    }
}
