package edu.ncsu.csc326.wolfcafe.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.ncsu.csc326.wolfcafe.dto.IngredientDto;
import edu.ncsu.csc326.wolfcafe.entity.Ingredient;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.mapper.IngredientMapper;
import edu.ncsu.csc326.wolfcafe.repository.IngredientRepository;
import edu.ncsu.csc326.wolfcafe.service.IngredientService;

@Service
public class IngredientServiceImpl implements IngredientService {

    @Autowired
    private IngredientRepository ingredientRepository;

    @Override
    public IngredientDto createIngredient ( final IngredientDto ingredientDto ) {
        final Ingredient ingredient = IngredientMapper.mapToIngredient( ingredientDto );
        final Ingredient savedIngredient = ingredientRepository.save( ingredient );
        return IngredientMapper.mapToIngredientDto( savedIngredient );
    }

    @Override
    public IngredientDto getIngredientById ( final Long ingredientId ) {
        final Ingredient ingredient = ingredientRepository.findById( ingredientId ).orElseThrow(
                () -> new ResourceNotFoundException( "Ingredient does not exist with id " + ingredientId ) );
        return IngredientMapper.mapToIngredientDto( ingredient );
    }

    public IngredientDto getIngredientByName ( final String ingredientName ) {
        final Ingredient ingredient = ingredientRepository.findByName( ingredientName ).orElseThrow(
                () -> new ResourceNotFoundException( "Ingredient does not exist with name " + ingredientName ) );
        return IngredientMapper.mapToIngredientDto( ingredient );
    }

    @Override
    public List<IngredientDto> getAllIngredients () {
        final List<Ingredient> ingredients = ingredientRepository.findAll();
        return ingredients.stream().map( IngredientMapper::mapToIngredientDto ).collect( Collectors.toList() );
    }

    @Override
    public void deleteIngredient ( final Long ingredientId ) {
        final Ingredient ingredient = ingredientRepository.findById( ingredientId ).orElseThrow(
                () -> new ResourceNotFoundException( "Ingredient does not exist with id " + ingredientId ) );

        ingredientRepository.delete( ingredient );
    }

    @Override
    public void deleteAllIngredients () {
        ingredientRepository.deleteAll();
    }
}
