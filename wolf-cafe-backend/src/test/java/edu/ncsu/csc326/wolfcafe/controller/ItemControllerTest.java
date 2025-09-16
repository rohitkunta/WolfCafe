package edu.ncsu.csc326.wolfcafe.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.ncsu.csc326.wolfcafe.dto.ItemDto;
import edu.ncsu.csc326.wolfcafe.service.ItemService;

@SpringBootTest
@AutoConfigureMockMvc
public class ItemControllerTest {

    @Autowired
    private MockMvc                   mvc;

    @MockBean
    private ItemService               itemService;

    private static final ObjectMapper mapper           = new ObjectMapper();

    private static final String       API_PATH         = "/api/items";
    private static final String       ENCODING         = "utf-8";
    private static final String       ITEM_NAME        = "Coffee";
    private static final String       ITEM_DESCRIPTION = "Coffee is life";
    private static final double       ITEM_PRICE       = 3.25;

    @Test
    @WithMockUser ( username = "staff", roles = "STAFF" )
    public void testCreateItem () throws Exception {
        // Create ItemDto with all contents but the id
        final ItemDto itemDto = new ItemDto();
        itemDto.setName( ITEM_NAME );
        itemDto.setDescription( ITEM_DESCRIPTION );
        itemDto.setPrice( ITEM_PRICE );

        Mockito.when( itemService.addItem( ArgumentMatchers.any() ) ).thenReturn( itemDto );

        final String json = mapper.writeValueAsString( itemDto );

        // Set id for the response
        itemDto.setId( 57L );

        mvc.perform( post( API_PATH ).contentType( MediaType.APPLICATION_JSON ).characterEncoding( ENCODING )
                .content( json ).accept( MediaType.APPLICATION_JSON ) ).andExpect( status().isCreated() )
                .andExpect( jsonPath( "$.id", Matchers.equalTo( 57 ) ) )
                .andExpect( jsonPath( "$.name", Matchers.equalTo( ITEM_NAME ) ) )
                .andExpect( jsonPath( "$.description", Matchers.equalTo( ITEM_DESCRIPTION ) ) )
                .andExpect( jsonPath( "$.price", Matchers.equalTo( ITEM_PRICE ) ) );
    }

    @Test
    public void testCreateItemNotAdmin () throws Exception {
        // Create ItemDto with all contents but the id
        final ItemDto itemDto = new ItemDto();
        itemDto.setName( ITEM_NAME );
        itemDto.setDescription( ITEM_DESCRIPTION );
        itemDto.setPrice( ITEM_PRICE );

        Mockito.when( itemService.addItem( ArgumentMatchers.any() ) ).thenReturn( itemDto );

        final String json = mapper.writeValueAsString( itemDto );

        // Set id for the response
        itemDto.setId( 57L );

        mvc.perform( post( API_PATH ).contentType( MediaType.APPLICATION_JSON ).characterEncoding( ENCODING )
                .content( json ).accept( MediaType.APPLICATION_JSON ) ).andExpect( status().isUnauthorized() );
    }

    @Test
    @WithMockUser ( username = "staff", roles = "STAFF" )
    public void testGetItemById () throws Exception {
        final ItemDto itemDto = new ItemDto();
        itemDto.setId( 27L );
        itemDto.setName( ITEM_NAME );
        itemDto.setDescription( ITEM_DESCRIPTION );
        itemDto.setPrice( ITEM_PRICE );

        Mockito.when( itemService.getItemById( ArgumentMatchers.any() ) ).thenReturn( itemDto );
        final String json = "";

        mvc.perform( get( API_PATH + "/27" ).contentType( MediaType.APPLICATION_JSON ).characterEncoding( ENCODING )
                .content( json ).accept( MediaType.APPLICATION_JSON ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$.id", Matchers.equalTo( 27 ) ) )
                .andExpect( jsonPath( "$.name", Matchers.equalTo( ITEM_NAME ) ) )
                .andExpect( jsonPath( "$.description", Matchers.equalTo( ITEM_DESCRIPTION ) ) )
                .andExpect( jsonPath( "$.price", Matchers.equalTo( ITEM_PRICE ) ) );
    }
    
    @Test
    @WithMockUser(username = "staff", roles = "STAFF")
    public void testGetAllItems() throws Exception {
        ItemDto item1 = new ItemDto();
        item1.setId(1L);
        item1.setName("Latte");
        item1.setDescription("Creamy coffee");
        item1.setPrice(4.50);

        ItemDto item2 = new ItemDto();
        item2.setId(2L);
        item2.setName("Espresso");
        item2.setDescription("Strong coffee");
        item2.setPrice(2.75);

        List<ItemDto> items = List.of(item1, item2);

        Mockito.when(itemService.getAllItems()).thenReturn(items);

        mvc.perform(get(API_PATH)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", Matchers.equalTo(2)))
                .andExpect(jsonPath("$[0].name", Matchers.equalTo("Latte")))
                .andExpect(jsonPath("$[1].name", Matchers.equalTo("Espresso")));
    }
    
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testUpdateItem() throws Exception {
        ItemDto updatedItem = new ItemDto();
        updatedItem.setId(5L);
        updatedItem.setName("Green Tea");
        updatedItem.setDescription("Healthy and hot");
        updatedItem.setPrice(3.00);

        Mockito.when(itemService.updateItem(Mockito.eq(5L), ArgumentMatchers.any()))
               .thenReturn(updatedItem);

        String json = mapper.writeValueAsString(updatedItem);

        mvc.perform(put(API_PATH + "/5")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(ENCODING)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.equalTo(5)))
                .andExpect(jsonPath("$.name", Matchers.equalTo("Green Tea")))
                .andExpect(jsonPath("$.description", Matchers.equalTo("Healthy and hot")))
                .andExpect(jsonPath("$.price", Matchers.equalTo(3.00)));
    }

}
