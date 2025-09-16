package edu.ncsu.csc326.wolfcafe.controller;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc326.wolfcafe.TestUtils;
import edu.ncsu.csc326.wolfcafe.dto.CreateUserRequestDto;
import edu.ncsu.csc326.wolfcafe.dto.EditUserRequestDto;
import edu.ncsu.csc326.wolfcafe.dto.LoginDto;
import edu.ncsu.csc326.wolfcafe.dto.RegisterDto;
import edu.ncsu.csc326.wolfcafe.entity.Role;
import edu.ncsu.csc326.wolfcafe.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Value ( "${app.admin-user-password}" )
    private String         adminUserPassword;

    @Autowired
    private MockMvc        mvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    @Transactional
    public void testLoginAdmin () throws Exception {
        final LoginDto loginDto = new LoginDto( "admin", adminUserPassword );

        mvc.perform( post( "/api/auth/login" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( loginDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.tokenType" ).value( "Bearer" ) )
                .andExpect( jsonPath( "$.role" ).value( "ROLE_ADMIN" ) );
    }

    @Test
    @Transactional
    public void testCreateCustomerAndLogin () throws Exception {
        final RegisterDto registerDto = new RegisterDto( "Jordan Estes", "jestes", "vitae.erat@yahoo.edu",
                "JXB16TBD4LC" );

        mvc.perform( post( "/api/auth/register" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( registerDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isCreated() ).andExpect( content().string( "User registered successfully." ) );

        final LoginDto loginDto = new LoginDto( "jestes", "JXB16TBD4LC" );

        mvc.perform( post( "/api/auth/login" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( loginDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.tokenType" ).value( "Bearer" ) )
                .andExpect( jsonPath( "$.role" ).value( "ROLE_CUSTOMER" ) );
    }

    @Test
    @Transactional
    public void testCreateUser () throws Exception {

        final RegisterDto registerDto = new RegisterDto( "Jordan Estes", "jestes", "vitae.erat@yahoo.edu",
                "JXB16TBD4LC" );

        final Role role = new Role( 3L, "ROLE_STAFF" );

        // need to figure out how to pass this role object as well in the JSON,
        // never passed two variables before.
        // ANSWER: Use a class to encapsulate both, and then we can send that
        // one class in the request
        // Wrap both in a single object
        final CreateUserRequestDto request = new CreateUserRequestDto();
        request.setRegisterDto( registerDto );
        request.setRole( role );

        // so the gson object only converts one object to JSON string, and the
        // content method only takes one string
        mvc.perform( post( "/api/auth/createuser" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( request ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isCreated() ).andExpect( content().string( "User registered successfully." ) );

        // login test
        final LoginDto loginDto = new LoginDto( "jestes", "JXB16TBD4LC" );

        mvc.perform( post( "/api/auth/login" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( loginDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.tokenType" ).value( "Bearer" ) )
                .andExpect( jsonPath( "$.role" ).value( "ROLE_STAFF" ) );

    }

    @Test
    @Transactional
    public void testEditUser () throws Exception {

        final RegisterDto registerDto = new RegisterDto( "Jordan Estes", "jestes", "vitae.erat@yahoo.edu",
                "JXB16TBD4LC" );

        final Role role = new Role( 3L, "ROLE_STAFF" );

        // need to figure out how to pass this role object as well in the JSON,
        // never passed two variables before.
        // ANSWER: Use a class to encapsulate both, and then we can send that
        // one class in the request
        // Wrap both in a single object
        final CreateUserRequestDto request = new CreateUserRequestDto();
        request.setRegisterDto( registerDto );
        request.setRole( role );

        // so the gson object only converts one object to JSON string, and the
        // content method only takes one string
        mvc.perform( post( "/api/auth/createuser" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( request ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isCreated() ).andExpect( content().string( "User registered successfully." ) );

        // login test
        final LoginDto loginDto = new LoginDto( "jestes", "JXB16TBD4LC" );

        mvc.perform( post( "/api/auth/login" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( loginDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.tokenType" ).value( "Bearer" ) )
                .andExpect( jsonPath( "$.role" ).value( "ROLE_STAFF" ) );

        // NOW, we will update the information by changing his name, email,
        // username, and password and role
        final RegisterDto registerDtoNew = new RegisterDto( "Jordan Patel", "jpatel", "vitae.patel@yahoo.edu",
                "$1234567A" );

        final Role roleNew = new Role( 3L, "ROLE_CUSTOMER" );

        final EditUserRequestDto updateRequest = new EditUserRequestDto( loginDto, registerDtoNew, roleNew );

        mvc.perform( post( "/api/auth/edituser" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( updateRequest ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isCreated() ).andExpect( content().string( "User edited successfully." ) );

        // login test NEW
        final LoginDto loginDtoNew = new LoginDto( "jpatel", "$1234567A" );

        mvc.perform( post( "/api/auth/login" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( loginDtoNew ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.tokenType" ).value( "Bearer" ) )
                .andExpect( jsonPath( "$.role" ).value( "ROLE_CUSTOMER" ) );

    }

    @Test
    @Transactional
    public void testEditUserNoRole () throws Exception {

        final RegisterDto registerDto = new RegisterDto( "Jordan Estes", "jestes", "vitae.erat@yahoo.edu",
                "JXB16TBD4LC" );

        final Role role = new Role( 3L, "ROLE_STAFF" );

        // need to figure out how to pass this role object as well in the JSON,
        // never passed two variables before.
        // ANSWER: Use a class to encapsulate both, and then we can send that
        // one class in the request
        // Wrap both in a single object
        final CreateUserRequestDto request = new CreateUserRequestDto();
        request.setRegisterDto( registerDto );
        request.setRole( role );

        // so the gson object only converts one object to JSON string, and the
        // content method only takes one string
        mvc.perform( post( "/api/auth/createuser" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( request ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isCreated() ).andExpect( content().string( "User registered successfully." ) );

        // login test
        final LoginDto loginDto = new LoginDto( "jestes", "JXB16TBD4LC" );

        mvc.perform( post( "/api/auth/login" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( loginDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.tokenType" ).value( "Bearer" ) )
                .andExpect( jsonPath( "$.role" ).value( "ROLE_STAFF" ) );

        // NOW, we will update the information by changing his name, email,
        // username, and password and role
        final RegisterDto registerDtoNew = new RegisterDto( "Jordan Patel", "jpatel", "vitae.patel@yahoo.edu",
                "$1234567A" );

        // we will leave the role as null to test if that works
        // final Role roleNew = new Role( 3L, "ROLE_CUSTOMER" );

        final EditUserRequestDto updateRequest = new EditUserRequestDto( loginDto, registerDtoNew, null );

        mvc.perform( post( "/api/auth/edituser" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( updateRequest ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isCreated() ).andExpect( content().string( "User edited successfully." ) );

        // login test NEW
        final LoginDto loginDtoNew = new LoginDto( "jpatel", "$1234567A" );

        mvc.perform( post( "/api/auth/login" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( loginDtoNew ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.tokenType" ).value( "Bearer" ) )
                .andExpect( jsonPath( "$.role" ).value( "ROLE_STAFF" ) );

    }

    @Test
    @Transactional
    public void testEditUserInvalid () throws Exception {

        final RegisterDto registerDto = new RegisterDto( "Jordan Estes", "jestes", "vitae.erat@yahoo.edu",
                "JXB16TBD4LC" );

        final Role role = new Role( 3L, "ROLE_STAFF" );

        // need to figure out how to pass this role object as well in the JSON,
        // never passed two variables before.
        // ANSWER: Use a class to encapsulate both, and then we can send that
        // one class in the request
        // Wrap both in a single object
        final CreateUserRequestDto request = new CreateUserRequestDto();
        request.setRegisterDto( registerDto );
        request.setRole( role );

        // so the gson object only converts one object to JSON string, and the
        // content method only takes one string
        mvc.perform( post( "/api/auth/createuser" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( request ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isCreated() ).andExpect( content().string( "User registered successfully." ) );

        // login test that works
        final LoginDto loginDto = new LoginDto( "jestes", "JXB16TBD4LC" );

        mvc.perform( post( "/api/auth/login" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( loginDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.tokenType" ).value( "Bearer" ) )
                .andExpect( jsonPath( "$.role" ).value( "ROLE_STAFF" ) );

        // NOW, we will update the information by changing his name, email,
        // username, and password and role
        final RegisterDto registerDtoNew = new RegisterDto( "Jordan Patel", "jpatel", "vitae.patel@yahoo.edu",
                "$1234567A" );

        // we will leave the role as null to see if that works
        // final Role roleNew = new Role( 3L, "ROLE_CUSTOMER" );

        // we will make a false loginDto to simulate trying to edit a user that
        // does not exist.
        final LoginDto loginDtoFalse = new LoginDto( "rkunta", "HelloWorld1234$" );

        final EditUserRequestDto updateRequest = new EditUserRequestDto( loginDtoFalse, registerDtoNew, role );

        mvc.perform( post( "/api/auth/edituser" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( updateRequest ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().is4xxClientError() );

    }

    @Test
    @Transactional
    @WithMockUser ( username = "admin", roles = "ADMIN" )
    public void testDeleteUser () throws Exception {

        final RegisterDto registerDto = new RegisterDto( "Rohit Kunta", "rkunta", "rkunta@yahoo.edu", "$1234567A" );

        final Role role = new Role( 3L, "ROLE_CUSTOMER" );

        // need to figure out how to pass this role object as well in the JSON,
        // never passed two variables before.
        // ANSWER: Use a class to encapsulate both, and then we can send that
        // one class in the request
        // Wrap both in a single object
        final CreateUserRequestDto request = new CreateUserRequestDto();
        request.setRegisterDto( registerDto );
        request.setRole( role );

        // so the gson object only converts one object to JSON string, and the
        // content method only takes one string
        mvc.perform( post( "/api/auth/createuser" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( request ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isCreated() ).andExpect( content().string( "User registered successfully." ) );

        // login test
        final LoginDto loginDto = new LoginDto( "rkunta", "$1234567A" );

        mvc.perform( post( "/api/auth/login" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( loginDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.tokenType" ).value( "Bearer" ) )
                .andExpect( jsonPath( "$.role" ).value( "ROLE_CUSTOMER" ) );

        // OK, now user is successfully made.

        // now, have to delete the user.
        // logging in as admin explicitly
        final LoginDto adminLoginDto = new LoginDto( "admin", adminUserPassword );

        mvc.perform( post( "/api/auth/login" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( adminLoginDto ) ) ).andExpect( status().isOk() ).andReturn();

        assertTrue( userRepository.findByUsername( "rkunta" ).isPresent() );

        // first, have to find the ID of the user.
        final String path = "/api/auth/user/rkunta@yahoo.edu";
        mvc.perform( MockMvcRequestBuilders.delete( path ) )
                // .header("Authorization", "Bearer " + adminToken)) // Pass the
                // token here
                .andExpect( status().isOk() );

        // this should now be false since that user was deleted
        final String emptyUser = mvc
                .perform( post( "/api/auth/login" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( loginDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isUnauthorized() ).andReturn().getResponse().getContentAsString();

        assertFalse( emptyUser.contains( "ROLE_CUSTOMER" ) );

        assertFalse( userRepository.findByUsername( "rkunta" ).isPresent() );
    }

    @Test
    @Transactional
    @WithMockUser ( username = "admin", roles = "ADMIN" )
    public void testGetAllUsers () throws Exception {

        // first, create a user and ensure they exist:
        final RegisterDto registerDto = new RegisterDto( "Jordan Estes", "jestes", "vitae.erat@yahoo.edu",
                "JXB16TBD4LC" );

        final Role role = new Role( 3L, "ROLE_STAFF" );

        // need to figure out how to pass this role object as well in the JSON,
        // never passed two variables before.
        // ANSWER: Use a class to encapsulate both, and then we can send that
        // one class in the request
        // Wrap both in a single object
        final CreateUserRequestDto request = new CreateUserRequestDto();
        request.setRegisterDto( registerDto );
        request.setRole( role );

        // so the gson object only converts one object to JSON string, and the
        // content method only takes one string
        mvc.perform( post( "/api/auth/createuser" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( request ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isCreated() ).andExpect( content().string( "User registered successfully." ) );

        // login test
        final LoginDto loginDto = new LoginDto( "jestes", "JXB16TBD4LC" );

        mvc.perform( post( "/api/auth/login" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( loginDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.tokenType" ).value( "Bearer" ) )
                .andExpect( jsonPath( "$.role" ).value( "ROLE_STAFF" ) );

        // user 2:
        final RegisterDto registerDto2 = new RegisterDto( "Rohit Kunta", "rkunta", "rkunta@yahoo.edu", "$12345678" );

        final Role role2 = new Role( 4L, "ROLE_STAFF" );

        // need to figure out how to pass this role object as well in the JSON,
        // never passed two variables before.
        // ANSWER: Use a class to encapsulate both, and then we can send that
        // one class in the request
        // Wrap both in a single object
        final CreateUserRequestDto request2 = new CreateUserRequestDto();
        request2.setRegisterDto( registerDto2 );
        request2.setRole( role2 );

        // so the gson object only converts one object to JSON string, and the
        // content method only takes one string
        mvc.perform( post( "/api/auth/createuser" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( request2 ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isCreated() ).andExpect( content().string( "User registered successfully." ) );

        // login test
        final LoginDto loginDto2 = new LoginDto( "jestes", "JXB16TBD4LC" );

        mvc.perform( post( "/api/auth/login" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( loginDto2 ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.tokenType" ).value( "Bearer" ) )
                .andExpect( jsonPath( "$.role" ).value( "ROLE_STAFF" ) );

        // now, get all the users. ONLY DONE BY ADMIN.
        // logging in as admin explicitly
        final LoginDto adminLoginDto = new LoginDto( "admin", adminUserPassword );

        mvc.perform( post( "/api/auth/login" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( adminLoginDto ) ) ).andExpect( status().isOk() ).andReturn();

        // testing whether it contains the 2 objects
        final String item = mvc.perform( get( "/api/auth/users" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();
        assertTrue( item.contains( "Jordan" ) );
        assertTrue( item.contains( "Rohit" ) );

    }

}
