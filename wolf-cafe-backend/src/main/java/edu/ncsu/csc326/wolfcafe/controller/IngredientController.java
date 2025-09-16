package edu.ncsu.csc326.wolfcafe.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc326.wolfcafe.dto.IngredientDto;
import edu.ncsu.csc326.wolfcafe.dto.InventoryDto;
import edu.ncsu.csc326.wolfcafe.entity.Ingredient;
import edu.ncsu.csc326.wolfcafe.mapper.IngredientMapper;
import edu.ncsu.csc326.wolfcafe.service.IngredientService;
import edu.ncsu.csc326.wolfcafe.service.InventoryService;

@CrossOrigin ( "*" )
@RestController
@RequestMapping ( "/api/ingredients" )
public class IngredientController {

    @Autowired
    private IngredientService ingredientService;

    @Autowired
    private InventoryService  inventoryService;

    @GetMapping ( "/{id}" )
    public ResponseEntity<IngredientDto> getIngredient ( @PathVariable ( "id" ) final String name ) {
        final IngredientDto ingredientDto = ingredientService.getIngredientByName( name );
        return ResponseEntity.ok( ingredientDto );
    }

    @PostMapping
    public ResponseEntity<IngredientDto> createIngredient ( @RequestBody final IngredientDto ingredientDto ) {
        try {
            final IngredientDto savedIngredientDto = ingredientService.createIngredient( ingredientDto );
            // Add the new ingredient to the inventory
            final InventoryDto inventoryDto = inventoryService.getInventory();
            final Ingredient savedIngredient = IngredientMapper.mapToIngredient( savedIngredientDto );
            inventoryDto.addIngredient( savedIngredient );
            inventoryDto.setId( 1L );
            inventoryService.updateInventory( inventoryDto );
            return ResponseEntity.ok( savedIngredientDto );
        }
        catch ( final Exception e ) {
            return new ResponseEntity<>( HttpStatus.BAD_REQUEST );
        }
    }

    @GetMapping
    public ResponseEntity<List<IngredientDto>> getAllIngredients () {
        final List<IngredientDto> ingredients = ingredientService.getAllIngredients();
        return ResponseEntity.ok( ingredients );
    }
}
