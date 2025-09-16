package edu.ncsu.csc326.wolfcafe.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc326.wolfcafe.dto.CreateUserRequestDto;
import edu.ncsu.csc326.wolfcafe.dto.EditUserRequestDto;
import edu.ncsu.csc326.wolfcafe.dto.JwtAuthResponse;
import edu.ncsu.csc326.wolfcafe.dto.LoginDto;
import edu.ncsu.csc326.wolfcafe.dto.RegisterDto;
import edu.ncsu.csc326.wolfcafe.dto.UserDto;
import edu.ncsu.csc326.wolfcafe.entity.Role;
import edu.ncsu.csc326.wolfcafe.service.AuthService;
import lombok.AllArgsConstructor;

/**
 * Controller for authentication functionality.
 */
@CrossOrigin ( "*" )
@RestController
@RequestMapping ( "/api/auth" )
@AllArgsConstructor
public class AuthController {

    /** Link to AuthService */
    private final AuthService authService;

    /**
     * Registers a new customer user with the system.
     *
     * @param registerDto
     *            object with registration info
     * @return response indicating success or failure
     */
    @PostMapping ( "/register" )
    public ResponseEntity<String> register ( @RequestBody final RegisterDto registerDto ) {
        final String response = authService.register( registerDto );
        return new ResponseEntity<>( response, HttpStatus.CREATED );
    }

    /**
     * Logs in the given user
     *
     * @param loginDto
     *            user information for login
     * @return object representing the logged in user
     */
    // @PostMapping ( "/login" )
    // public ResponseEntity<JwtAuthResponse> login ( @RequestBody final
    // LoginDto loginDto ) {
    // final JwtAuthResponse jwtAuthResponse = authService.login( loginDto );
    // return new ResponseEntity<>( jwtAuthResponse, HttpStatus.OK );
    // }
    @PostMapping ( "/login" )
    public ResponseEntity< ? > login ( @RequestBody final LoginDto loginDto ) {
        try {
            final JwtAuthResponse jwtAuthResponse = authService.login( loginDto );
            return ResponseEntity.ok( jwtAuthResponse );
        }
        catch ( final BadCredentialsException e ) {
            return ResponseEntity.status( HttpStatus.UNAUTHORIZED ).body( "Bad Credentials" );
        }
    }

    // /**
    // * Deletes the given user. Requires the ADMIN role.
    // *
    // * @param id
    // * id of user to delete
    // * @return response indicating success or failure
    // */
    // @PreAuthorize ( "hasRole('ADMIN')" )
    // @DeleteMapping ( "/user/{id}" )
    // public ResponseEntity<String> deleteUser ( @PathVariable ( "id" ) final
    // Long id ) {
    // authService.deleteUserById( id );
    // return ResponseEntity.ok( "User deleted successfully." );
    // }

    /**
     * Deletes the given user. Requires the ADMIN role.
     *
     * @param id
     *            id of user to delete
     * @return response indicating success or failure
     */
    @DeleteMapping("/user/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable("email") final String email) {
        authService.deleteUserByEmail(email);
        return ResponseEntity.ok("User deleted successfully.");
    }


    /**
     * Registers a new customer user with the system.
     *
     * @param request
     *            the class containing the registerDto and the role of the new
     *            user. the registerDto contains the information for that user.
     * @return response indicating success or failure
     */
    @PostMapping ( "/createuser" )
    public ResponseEntity<String> createUser ( @RequestBody final CreateUserRequestDto request ) {

        // extracts the individual objects from the request
        final RegisterDto registerDto = request.getRegisterDto();
        final Role role = request.getRole();

        final String response = authService.createUser( registerDto, role );
        return new ResponseEntity<>( response, HttpStatus.CREATED );
    }

    /**
     * Registers a new customer user with the system.
     *
     * @param request
     *            the class containing the loginDto, registerDto, and the role
     *            of the new user. the registerDto contains the new information
     *            for that user, the loginDto identifies the user being edited,
     *            and the role is specified if it is being changed in the user.
     *            Else, the role will be null
     * @return response indicating success or failure
     */
    @PostMapping ( "/edituser" )
    public ResponseEntity<String> editUser ( @RequestBody final EditUserRequestDto request ) {

        // extracts the individual objects from the request
        final LoginDto loginDto = request.getLoginDto();
        final RegisterDto registerDto = request.getRegisterDto();
        final Role role = request.getRole();

        final String response = authService.editUser( loginDto, registerDto, role );
        return new ResponseEntity<>( response, HttpStatus.CREATED );
    }

    /**
     * Returns the list of users in the repository, only can be done by ADMIN
     *
     * @return ResponseEntity containing list of users Dtos
     */
    @PreAuthorize ( "hasRole('ADMIN')" )
    @GetMapping ( "/users" )
    public ResponseEntity<List<UserDto>> getAllUsers () {

        final List<UserDto> users = authService.getAllUsers();
        return new ResponseEntity<>( users, HttpStatus.OK );

    }

}