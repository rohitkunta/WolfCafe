/**
 *
 */
package edu.ncsu.csc326.wolfcafe.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.ncsu.csc326.wolfcafe.TestUtils;
import edu.ncsu.csc326.wolfcafe.dto.InventoryDto;
import edu.ncsu.csc326.wolfcafe.dto.LoginDto;
import edu.ncsu.csc326.wolfcafe.dto.OrderDto;
import edu.ncsu.csc326.wolfcafe.entity.Ingredient;
import edu.ncsu.csc326.wolfcafe.entity.Item;
import edu.ncsu.csc326.wolfcafe.entity.TaxRate;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.repository.InventoryRepository;
import edu.ncsu.csc326.wolfcafe.repository.ItemRepository;
import edu.ncsu.csc326.wolfcafe.repository.OrderRepository;
import edu.ncsu.csc326.wolfcafe.repository.OrderStatusRepository;
import edu.ncsu.csc326.wolfcafe.repository.TaxRateRepository;
import jakarta.persistence.EntityManager;

/**
 * Test class for OrderController.
 *
 * @author Rohit Kunta
 * @author Riana Victoria
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OrderControllerTest {

    @Autowired
    private MockMvc                   mvc;

    @Value ( "${app.admin-user-password}" )
    private String                    adminUserPassword;

    /**
     * Connection to the repository to work with the DAO + database. Used for
     * fulfillOrder test method.
     */
    @Autowired
    private InventoryRepository       inventoryRepository;

    /**
     * Order status repository which contains available order statuses
     */
    @Autowired
    private OrderStatusRepository     orderStatusRepository;

    /** Reference to EntityManager */
    @Autowired
    private EntityManager             entityManager;

    @Autowired
    private OrderRepository           orderRepository;

    // this and the stuff we add to itemRepository is messing up
    // itemRepositoryTest as well.
    @Autowired
    private ItemRepository            itemRepository;

    /** Test Order Object. */
    // private final Long order_id = 3L;
    private final String              customerUsername = "jestes";

    // total price will be determined in the placeOrder method, by finding all
    // the Items in the ItemRepository using the list of names, and then summing
    // the price of all the items and then setting the field to that
    private final double              amountPaid       = 5.0;

    // need a to add all the items in the @BeforeEach method
    private List<String>              itemsOrdered;

    // need a to add all the items in the @BeforeEach method
    private List<String>              itemsOrderedInvalid;

    // used to get the tax rate to compute total price of order
    @Autowired
    private TaxRateRepository         taxRateRepository;

    // tipAmount in dollars
    private final double              tipAmountDollars = 1.00;

    // orderStatus is set in the placeOrder method, and it is set to a default
    // of PENDING, by first finding it in the OrderStatus Repository.

    private static final ObjectMapper mapper           = new ObjectMapper();

    Ingredient                        item1Coffee;

    Ingredient                        item2Coffee;

    Ingredient                        itemMilk;

    Ingredient                        itemSugar;

    /**
     * Adds items to ItemRepository before every test, so the placeOrder method
     * can work properly.
     *
     * @throws Exception
     */
    @BeforeEach
    @Transactional
    void setUp () throws Exception {

        // reset orderRepository and their Ids THIS IS BREAKING IT, CURRENT
        // PROBLEM IS THAT IDs are incrementing, need to find a way to solve
        // that like we did with inventory
        entityManager.createNativeQuery( "ALTER TABLE orders AUTO_INCREMENT = 1" ).executeUpdate();
        entityManager.createNativeQuery( "ALTER TABLE orderstatuses AUTO_INCREMENT = 1" ).executeUpdate();
        orderRepository.deleteAll();
        orderStatusRepository.deleteAll();
        taxRateRepository.deleteAll();

        // do placeOrder method can work properly.
        itemRepository.deleteAll();

        item1Coffee = new Ingredient( "Coffee", 5 );
        item2Coffee = new Ingredient( "Coffee", 5 );
        itemMilk = new Ingredient( "Milk", 1 );
        itemSugar = new Ingredient( "Sugar", 10 );

        final Item item1 = itemRepository.findByName( "Coffee" ).orElse( new Item() );
        item1.setName( "Coffee" );
        item1.setPrice( 50 );

        final ArrayList<Ingredient> item1Ingred = new ArrayList<>();
        item1Ingred.add( item1Coffee );
        item1.setIngredients( item1Ingred );
        itemRepository.save( item1 );

        final Item item2 = itemRepository.findByName( "Latte" ).orElse( new Item() );
        item2.setName( "Latte" );
        item2.setPrice( 100 );

        final ArrayList<Ingredient> item2Ingred = new ArrayList<>();
        item2Ingred.add( item2Coffee );
        item2Ingred.add( itemSugar );
        item2Ingred.add( itemMilk );
        item2.setIngredients( item2Ingred );
        itemRepository.save( item2 );

        itemsOrderedInvalid = new ArrayList<String>();
        itemsOrderedInvalid.add( "Chocolate Cake Sushi" );

        // valid case
        itemsOrdered = new ArrayList<String>();
        // to be used in creating orderDto
        itemsOrdered.add( "Coffee" );
        itemsOrdered.add( "Latte" );

        final TaxRate tax = new TaxRate();
        tax.setId( 1L );
        tax.setTaxRate( 0.02 ); // 2% tax
        taxRateRepository.save( tax );

        entityManager.createNativeQuery( "ALTER TABLE inventory AUTO_INCREMENT = 1" ).executeUpdate();
        inventoryRepository.deleteAll();
    }

    /**
     * Tests placeOrder method.
     *
     * @throws JsonProcessingException
     */
    @Test
    @WithMockUser ( username = "customer", roles = "CUSTOMER" )
    void testPlaceOrder () throws JsonProcessingException {

        // so, items are already there.

        final OrderDto orderDto = new OrderDto();

        final TaxRate rate = taxRateRepository.findById( 1L )
                .orElseThrow( () -> new ResourceNotFoundException( "Tax rate not found" ) );

        orderDto.setCustomerUsername( customerUsername );
        orderDto.setAmountPaid( amountPaid );
        orderDto.setItemsOrdered( itemsOrdered );
        orderDto.setTaxRate( rate.getTaxRate() );
        orderDto.setTipAmountDollars( tipAmountDollars );

        // now, convert to Json using gson and then send to correct endpoint in
        // backend.

        final String json = mapper.writeValueAsString( orderDto );

        try {
            mvc.perform( post( "/api/orders" ).contentType( MediaType.APPLICATION_JSON ).characterEncoding( "utf-8" )
                    .content( json ).accept( MediaType.APPLICATION_JSON ) ).andExpect( status().isCreated() )
                    .andExpect( jsonPath( "$.id", Matchers.equalTo( 1 ) ) )
                    .andExpect( jsonPath( "$.customerUsername", Matchers.equalTo( customerUsername ) ) )
                    .andExpect( jsonPath( "$.amountPaid", Matchers.equalTo( amountPaid ) ) )
                    .andExpect( jsonPath( "$.tipAmountDollars", Matchers.equalTo( tipAmountDollars ) ) )
                    .andExpect( jsonPath( "$.taxRate", Matchers.equalTo( rate.getTaxRate() ) ) )
                    .andExpect( jsonPath( "$.totalPrice", Matchers.equalTo( 153D ) ) )
                    .andExpect( jsonPath( "$.status.name", Matchers.equalTo( "PENDING" ) ) );
        }
        catch ( final Exception e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        final OrderDto orderDto2 = new OrderDto();

        orderDto2.setCustomerUsername( customerUsername );
        orderDto2.setAmountPaid( amountPaid );
        orderDto2.setItemsOrdered( itemsOrderedInvalid );
        orderDto2.setTaxRate( rate.getTaxRate() );
        orderDto2.setTipAmountDollars( tipAmountDollars );

        final String json2 = mapper.writeValueAsString( orderDto2 );

        try {
            mvc.perform( post( "/api/orders" ).contentType( MediaType.APPLICATION_JSON ).characterEncoding( "utf-8" )
                    .content( json2 ).accept( MediaType.APPLICATION_JSON ) ).andExpect( status().isCreated() )
                    .andExpect( jsonPath( "$.id", Matchers.equalTo( 2 ) ) )
                    .andExpect( jsonPath( "$.customerUsername", Matchers.equalTo( customerUsername ) ) )
                    .andExpect( jsonPath( "$.amountPaid", Matchers.equalTo( amountPaid ) ) )
                    .andExpect( jsonPath( "$.tipAmountDollars", Matchers.equalTo( tipAmountDollars ) ) )
                    .andExpect( jsonPath( "$.taxRate", Matchers.equalTo( rate.getTaxRate() ) ) )
                    .andExpect( jsonPath( "$.status.name", Matchers.equalTo( "INVALID" ) ) );
        }
        catch ( final Exception e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * Tests the pickupOrder method.
     *
     * @throws JsonProcessingException
     */
    @Test
    @WithMockUser ( username = "admin", roles = "ADMIN" )
    void testPickupOrder () throws JsonProcessingException {
        // creating order to modify
        final OrderDto orderDto = new OrderDto();

        final TaxRate rate = taxRateRepository.findById( 1L )
                .orElseThrow( () -> new ResourceNotFoundException( "Tax rate not found" ) );

        orderDto.setCustomerUsername( customerUsername );
        orderDto.setAmountPaid( amountPaid );
        orderDto.setItemsOrdered( itemsOrdered );
        orderDto.setTaxRate( rate.getTaxRate() );
        orderDto.setTipAmountDollars( tipAmountDollars );

        // now, convert to Json using gson and then send to correct endpoint in
        // backend.

        final String json = mapper.writeValueAsString( orderDto );

        try {
            mvc.perform( post( "/api/orders" ).contentType( MediaType.APPLICATION_JSON ).characterEncoding( "utf-8" )
                    .content( json ).accept( MediaType.APPLICATION_JSON ) ).andExpect( status().isCreated() )
                    .andExpect( jsonPath( "$.id", Matchers.equalTo( 1 ) ) )
                    .andExpect( jsonPath( "$.customerUsername", Matchers.equalTo( customerUsername ) ) )
                    .andExpect( jsonPath( "$.amountPaid", Matchers.equalTo( amountPaid ) ) )
                    .andExpect( jsonPath( "$.tipAmountDollars", Matchers.equalTo( tipAmountDollars ) ) )
                    .andExpect( jsonPath( "$.taxRate", Matchers.equalTo( rate.getTaxRate() ) ) )
                    .andExpect( jsonPath( "$.totalPrice", Matchers.equalTo( 153D ) ) )
                    .andExpect( jsonPath( "$.status.name", Matchers.equalTo( "PENDING" ) ) );
        }
        catch ( final Exception e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // now, test the pickUp order method.
        try {
            mvc.perform( put( "/api/orders/pickup/1" ).contentType( MediaType.APPLICATION_JSON )
                    .characterEncoding( "utf-8" ).content( json ).accept( MediaType.APPLICATION_JSON ) )
                    .andExpect( status().isOk() )
                    .andExpect( content().string( "Order picked up successfully, order completed." ) );
        }
        catch ( final Exception e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // now, make sure the orderStatus actually got changed, need to be an
        // admin or STAFF to do this.

        // logging in as admin explicitly
        final LoginDto adminLoginDto = new LoginDto( "admin", adminUserPassword );

        try {
            mvc.perform( post( "/api/auth/login" ).contentType( MediaType.APPLICATION_JSON )
                    .content( TestUtils.asJsonString( adminLoginDto ) ) ).andExpect( status().isOk() ).andReturn();
        }
        catch ( final Exception e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            mvc.perform( get( "/api/orders/1" ).contentType( MediaType.APPLICATION_JSON ).characterEncoding( "utf-8" )
                    .content( json ).accept( MediaType.APPLICATION_JSON ) ).andExpect( status().isOk() )
                    .andExpect( jsonPath( "$.id", Matchers.equalTo( 1 ) ) )
                    .andExpect( jsonPath( "$.customerUsername", Matchers.equalTo( customerUsername ) ) )
                    .andExpect( jsonPath( "$.amountPaid", Matchers.equalTo( amountPaid ) ) )
                    .andExpect( jsonPath( "$.tipAmountDollars", Matchers.equalTo( tipAmountDollars ) ) )
                    .andExpect( jsonPath( "$.taxRate", Matchers.equalTo( rate.getTaxRate() ) ) )
                    .andExpect( jsonPath( "$.totalPrice", Matchers.equalTo( 153D ) ) )
                    .andExpect( jsonPath( "$.status.name", Matchers.equalTo( "COMPLETE" ) ) );
        }
        catch ( final Exception e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * Test method for
     * {@link edu.ncsu.csc326.wolfcafe.controller.OrderController#viewOrders()}.
     *
     * @throws JsonProcessingException
     */
    @Test
    @Transactional
    @WithMockUser ( username = "admin", roles = "ADMIN" )
    void testViewOrders () throws JsonProcessingException {
        final TaxRate rate = taxRateRepository.findById( 1L )
                .orElseThrow( () -> new ResourceNotFoundException( "Tax rate not found" ) );
        // Create the first order
        final OrderDto orderDto = new OrderDto();

        orderDto.setCustomerUsername( customerUsername );
        orderDto.setAmountPaid( amountPaid );
        orderDto.setItemsOrdered( itemsOrdered );
        orderDto.setTaxRate( rate.getTaxRate() );
        orderDto.setTipAmountDollars( tipAmountDollars );

        final String json = mapper.writeValueAsString( orderDto );

        try {
            mvc.perform( post( "/api/orders" ).contentType( MediaType.APPLICATION_JSON ).characterEncoding( "utf-8" )
                    .content( json ).accept( MediaType.APPLICATION_JSON ) ).andExpect( status().isCreated() )
                    .andExpect( jsonPath( "$.id", Matchers.equalTo( 1 ) ) )
                    .andExpect( jsonPath( "$.customerUsername", Matchers.equalTo( customerUsername ) ) )
                    .andExpect( jsonPath( "$.amountPaid", Matchers.equalTo( amountPaid ) ) )
                    .andExpect( jsonPath( "$.tipAmountDollars", Matchers.equalTo( tipAmountDollars ) ) )
                    .andExpect( jsonPath( "$.taxRate", Matchers.equalTo( rate.getTaxRate() ) ) )
                    .andExpect( jsonPath( "$.totalPrice", Matchers.equalTo( 153D ) ) )
                    .andExpect( jsonPath( "$.status.name", Matchers.equalTo( "PENDING" ) ) );
        }
        catch ( final Exception e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Create a second order with some different fields
        final OrderDto orderDto2 = new OrderDto();

        orderDto2.setCustomerUsername( "neander2" );
        orderDto2.setAmountPaid( 100.0 );
        orderDto2.setItemsOrdered( itemsOrdered );
        orderDto2.setTaxRate( rate.getTaxRate() );
        orderDto2.setTipAmountDollars( tipAmountDollars );

        final String json2 = mapper.writeValueAsString( orderDto2 );
        try {
            mvc.perform( post( "/api/orders" ).contentType( MediaType.APPLICATION_JSON ).characterEncoding( "utf-8" )
                    .content( json2 ).accept( MediaType.APPLICATION_JSON ) ).andExpect( status().isCreated() )
                    .andExpect( jsonPath( "$.id", Matchers.equalTo( 2 ) ) )
                    .andExpect( jsonPath( "$.customerUsername", Matchers.equalTo( "neander2" ) ) )
                    .andExpect( jsonPath( "$.amountPaid", Matchers.equalTo( 100.0 ) ) )
                    .andExpect( jsonPath( "$.tipAmountDollars", Matchers.equalTo( tipAmountDollars ) ) )
                    .andExpect( jsonPath( "$.taxRate", Matchers.equalTo( rate.getTaxRate() ) ) )
                    .andExpect( jsonPath( "$.totalPrice", Matchers.equalTo( 153D ) ) )
                    .andExpect( jsonPath( "$.status.name", Matchers.equalTo( "PENDING" ) ) );
        }
        catch ( final Exception e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Log in as admin to use the viewOrders() endpoint
        final LoginDto adminLoginDto = new LoginDto( "admin", adminUserPassword );

        try {
            mvc.perform( post( "/api/auth/login" ).contentType( MediaType.APPLICATION_JSON )
                    .content( TestUtils.asJsonString( adminLoginDto ) ) ).andExpect( status().isOk() ).andReturn();
        }
        catch ( final Exception e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Once logged in as admin, get the list of orders
        try {
            final String ordersJson = mvc.perform(
                    get( "/api/orders" ).contentType( MediaType.APPLICATION_JSON ).characterEncoding( "utf-8" ) )
                    .andReturn().getResponse().getContentAsString();
            // USED CHATGPT for mapping the response to a list of orderDtos
            final ObjectMapper orderListMapper = new ObjectMapper();
            final List<OrderDto> orders = orderListMapper.readValue( ordersJson, new TypeReference<List<OrderDto>>() {
            } );

            assertEquals( 2, orders.size() );
            final OrderDto listOrderDto = orders.get( 0 );
            assertEquals( customerUsername, listOrderDto.getCustomerUsername() );
            assertEquals( amountPaid, listOrderDto.getAmountPaid() );
            assertEquals( itemsOrdered, listOrderDto.getItemsOrdered() );
            assertEquals( rate.getTaxRate(), listOrderDto.getTaxRate() );
            assertEquals( tipAmountDollars, listOrderDto.getTipAmountDollars() );

            final OrderDto listOrderDto2 = orders.get( 1 );
            assertEquals( "neander2", listOrderDto2.getCustomerUsername() );
            assertEquals( 100.0, listOrderDto2.getAmountPaid() );
            assertEquals( itemsOrdered, listOrderDto2.getItemsOrdered() );
            assertEquals( rate.getTaxRate(), listOrderDto2.getTaxRate() );
            assertEquals( tipAmountDollars, listOrderDto2.getTipAmountDollars() );

            // Lastly, test the viewOrder by ID endpoint
            final String orderDtoJson = mvc.perform(
                    get( "/api/orders/1 " ).contentType( MediaType.APPLICATION_JSON ).characterEncoding( "utf-8" ) )
                    .andReturn().getResponse().getContentAsString();
            final ObjectMapper orderMapper = new ObjectMapper();
            final OrderDto returnedOrderDto = orderMapper.readValue( orderDtoJson, new TypeReference<OrderDto>() {
            } );
            assertEquals( customerUsername, returnedOrderDto.getCustomerUsername() );
            assertEquals( amountPaid, returnedOrderDto.getAmountPaid() );
            assertEquals( itemsOrdered, returnedOrderDto.getItemsOrdered() );
            assertEquals( 0.02, returnedOrderDto.getTaxRate() );
            assertEquals( tipAmountDollars, returnedOrderDto.getTipAmountDollars() );

            final String orderDtoJson2 = mvc.perform(
                    get( "/api/orders/2 " ).contentType( MediaType.APPLICATION_JSON ).characterEncoding( "utf-8" ) )
                    .andReturn().getResponse().getContentAsString();
            final OrderDto returnedOrderDto2 = orderMapper.readValue( orderDtoJson2, new TypeReference<OrderDto>() {
            } );
            assertEquals( "neander2", returnedOrderDto2.getCustomerUsername() );
            assertEquals( 100.0, returnedOrderDto2.getAmountPaid() );
            assertEquals( itemsOrdered, returnedOrderDto2.getItemsOrdered() );
            assertEquals( rate.getTaxRate(), returnedOrderDto2.getTaxRate() );
            assertEquals( tipAmountDollars, returnedOrderDto2.getTipAmountDollars() );

        }
        catch ( final Exception e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * Test method for
     * {@link edu.ncsu.csc326.wolfcafe.controller.OrderController#fulfillOrder(java.lang.Long)}.
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "admin", roles = "ADMIN" )
    void testFulfillOrder () throws Exception {

        // Step 1: Create a new order via POST /api/orders

        final InventoryDto expectedInventory = new InventoryDto( 1L );

        mvc.perform( get( "/api/inventory" ) )
                .andExpect( content().string( TestUtils.asJsonString( expectedInventory ) ) )
                .andExpect( status().isOk() );

        final InventoryDto updatedInventory = new InventoryDto( 1L );

        updatedInventory.addIngredient( new Ingredient( "Coffee", 50 ) );
        updatedInventory.addIngredient( new Ingredient( "Milk", 50 ) );
        updatedInventory.addIngredient( new Ingredient( "Sugar", 50 ) );

        mvc.perform( put( "/api/inventory" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( updatedInventory ) ).accept( MediaType.APPLICATION_JSON ) );

        mvc.perform( get( "/api/inventory" ) ).andExpect( jsonPath( "$.ingredients[0].name" ).value( "Coffee" ) )
                .andExpect( jsonPath( "$.ingredients[0].amount" ).value( 50 ) )
                .andExpect( jsonPath( "$.ingredients[1].name" ).value( "Milk" ) )
                .andExpect( jsonPath( "$.ingredients[1].amount" ).value( 50 ) )
                .andExpect( jsonPath( "$.ingredients[2].name" ).value( "Sugar" ) )
                .andExpect( jsonPath( "$.ingredients[2].amount" ).value( 50 ) );

        final OrderDto orderDto = new OrderDto();

        final TaxRate rate = taxRateRepository.findById( 1L )
                .orElseThrow( () -> new ResourceNotFoundException( "Tax rate not found" ) );

        orderDto.setCustomerUsername( customerUsername );
        orderDto.setAmountPaid( amountPaid );
        orderDto.setItemsOrdered( itemsOrdered );
        orderDto.setTaxRate( rate.getTaxRate() );
        orderDto.setTipAmountDollars( tipAmountDollars );

        final String json = mapper.writeValueAsString( orderDto );

        mvc.perform( post( "/api/orders" ).contentType( MediaType.APPLICATION_JSON ).characterEncoding( "utf-8" )
                .content( json ).accept( MediaType.APPLICATION_JSON ) ).andExpect( status().isCreated() )
                .andExpect( jsonPath( "$.id", Matchers.equalTo( 1 ) ) )
                .andExpect( jsonPath( "$.status.name", Matchers.equalTo( "PENDING" ) ) );

        // Step 2: Call PUT /api/orders/fulfill/1 to fulfill the order
        mvc.perform( put( "/api/orders/fulfill/1" ).contentType( MediaType.APPLICATION_JSON )
                .accept( MediaType.APPLICATION_JSON ) ).andExpect( status().isOk() )
                .andExpect( content().string( "Order now ready for pickup." ) );

        // Step 3: Retrieve the order again and confirm status is FULFILLED
        mvc.perform(
                get( "/api/orders/1" ).contentType( MediaType.APPLICATION_JSON ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.id", Matchers.equalTo( 1 ) ) )
                .andExpect( jsonPath( "$.status.name", Matchers.equalTo( "READY_FOR_PICKUP" ) ) );

        mvc.perform( get( "/api/inventory" ) ).andExpect( jsonPath( "$.ingredients[0].name" ).value( "Coffee" ) )
                .andExpect( jsonPath( "$.ingredients[0].amount" ).value( 40 ) )
                .andExpect( jsonPath( "$.ingredients[1].name" ).value( "Milk" ) )
                .andExpect( jsonPath( "$.ingredients[1].amount" ).value( 49 ) )
                .andExpect( jsonPath( "$.ingredients[2].name" ).value( "Sugar" ) )
                .andExpect( jsonPath( "$.ingredients[2].amount" ).value( 40 ) );

    }
}
