package edu.ncsu.csc326.wolfcafe.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import edu.ncsu.csc326.wolfcafe.dto.JwtAuthResponse;
import edu.ncsu.csc326.wolfcafe.dto.LoginDto;
import edu.ncsu.csc326.wolfcafe.dto.RegisterDto;
import edu.ncsu.csc326.wolfcafe.dto.UserDto;
import edu.ncsu.csc326.wolfcafe.entity.Role;
import edu.ncsu.csc326.wolfcafe.entity.User;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.exception.WolfCafeAPIException;
import edu.ncsu.csc326.wolfcafe.repository.RoleRepository;
import edu.ncsu.csc326.wolfcafe.repository.UserRepository;
import edu.ncsu.csc326.wolfcafe.security.JwtTokenProvider;
import edu.ncsu.csc326.wolfcafe.service.AuthService;
import lombok.AllArgsConstructor;

/**
 * Implemented AuthService
 */
@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository        userRepository;
    private final RoleRepository        roleRepository;
    private final PasswordEncoder       passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider      jwtTokenProvider;

    @Autowired
    private final ModelMapper           modelMapper;

    /**
     * Registers the given user
     *
     * @param registerDto
     *            new user information
     * @return message for success or failure
     */
    @Override
    public String register ( final RegisterDto registerDto ) {
        // Check for duplicates - username
        if ( userRepository.existsByUsername( registerDto.getUsername() ) ) {
            throw new WolfCafeAPIException( HttpStatus.BAD_REQUEST, "Username already exists." );
        }
        // Check for duplicates - email
        if ( userRepository.existsByEmail( registerDto.getEmail() ) ) {
            throw new WolfCafeAPIException( HttpStatus.BAD_REQUEST, "Email already exists." );
        }

        final User user = new User();
        user.setName( registerDto.getName() );
        user.setUsername( registerDto.getUsername() );
        user.setEmail( registerDto.getEmail() );
        user.setPassword( passwordEncoder.encode( registerDto.getPassword() ) );

        final Set<Role> roles = new HashSet<>();
        final Role userRole = roleRepository.findByName( "ROLE_CUSTOMER" );
        roles.add( userRole );

        user.setRoles( roles );

        userRepository.save( user );

        return "User registered successfully.";
    }

    /**
     * Creates a new user within the system, for any role
     *
     * @param registerDto
     *            new user information
     * @param role
     *            the role of the user [CUSTOMER, STAFF, ADMIN]
     * @return message for success or failure
     */
    @Override
    public String createUser ( final RegisterDto registerDto, final Role role ) {
        // Check for duplicates - username
        if ( userRepository.existsByUsername( registerDto.getUsername() ) ) {
            throw new WolfCafeAPIException( HttpStatus.BAD_REQUEST, "Username already exists." );
        }
        // Check for duplicates - email
        if ( userRepository.existsByEmail( registerDto.getEmail() ) ) {
            throw new WolfCafeAPIException( HttpStatus.BAD_REQUEST, "Email already exists." );
        }

        final User user = new User();
        user.setName( registerDto.getName() );
        user.setUsername( registerDto.getUsername() );
        user.setEmail( registerDto.getEmail() );
        user.setPassword( passwordEncoder.encode( registerDto.getPassword() ) );

        final Set<Role> roles = new HashSet<>();
        final Role userRole = roleRepository.findByName( role.getName() );
        roles.add( userRole );

        user.setRoles( roles );

        userRepository.save( user );

        return "User registered successfully.";

    }

    /**
     * Edits a given user within the system, with role specified.
     *
     * @param loginDto
     *            the username/email and password
     * @param registerDto
     *            the user information they want to edit
     * @param role
     *            the new role of the user
     * @return message stating whether edit was successful or not
     */
    @Override
    public String editUser ( final LoginDto loginDto, final RegisterDto registerDto, final Role role ) {
        // Check for duplicates - username and email. makes sure it already
        // exists for it to be edited.
        if ( !userRepository.existsByUsername( loginDto.getUsernameOrEmail() )
                && !userRepository.existsByEmail( loginDto.getUsernameOrEmail() ) ) {
            throw new WolfCafeAPIException( HttpStatus.BAD_REQUEST, "Username or Email does not already exist." );
        }

        final User user = userRepository
                .findByUsernameOrEmail( loginDto.getUsernameOrEmail(), loginDto.getUsernameOrEmail() )
                .orElseThrow( () -> new ResourceNotFoundException( "User not found with username " ) );
        user.setName( registerDto.getName() );
        user.setUsername( registerDto.getUsername() );
        user.setEmail( registerDto.getEmail() );
        user.setPassword( passwordEncoder.encode( registerDto.getPassword() ) );

        final Set<Role> roles = new HashSet<>();

        // Does not have admin check here, how would we do this? [SOLVED]
        // SOLUTION: @PreAuthorize ( "hasRole('ADMIN')" ) tag above the
        // Corresponding Controller class method
        if ( role == null ) { // if role was not specified
            // maintain the same role.
        }
        else {
            // changes the role
            final Role userRole = roleRepository.findByName( role.getName() );
            roles.add( userRole );

            user.setRoles( roles );
        }

        userRepository.save( user );

        return "User edited successfully.";
    }

    /**
     * Logins in the given user
     *
     * @param loginDto
     *            username/email and password
     * @return response with authenticated user
     */
    @Override
    public JwtAuthResponse login ( final LoginDto loginDto ) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken( loginDto.getUsernameOrEmail(), loginDto.getPassword() ) );

        SecurityContextHolder.getContext().setAuthentication( authentication );

        final String token = jwtTokenProvider.generateToken( authentication );

        final Optional<User> userOptional = userRepository.findByUsernameOrEmail( loginDto.getUsernameOrEmail(),
                loginDto.getUsernameOrEmail() );

        String role = null;
        if ( userOptional.isPresent() ) {
            final User loggedInUser = userOptional.get();
            final Optional<Role> optionalRole = loggedInUser.getRoles().stream().findFirst();

            if ( optionalRole.isPresent() ) {
                final Role userRole = optionalRole.get();
                role = userRole.getName();
            }
        }

        final JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();
        jwtAuthResponse.setRole( role );
        jwtAuthResponse.setAccessToken( token );

        return jwtAuthResponse;
    }

    /**
     * Deletes the given user by id
     *
     * @param id
     *            id of user to delete
     */
    @Override
    public void deleteUserByUserName(final String userName) {
        final User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with userName " + userName));

        user.getRoles().clear();
        userRepository.save(user); 

        userRepository.delete(user);  
    }


    /**
     * Returns the list of users in the repository, only can be done by ADMIN
     *
     * @return list of users Dtos
     */
    @Override
    public List<UserDto> getAllUsers () {
        final List<User> users = userRepository.findAll();

        return users.stream().map( ( user ) -> modelMapper.map( user, UserDto.class ) ).collect( Collectors.toList() );
    }
    
    @Override
    public void deleteUserByEmail(String email) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found."));

        if (currentUser.getEmail().equalsIgnoreCase(email)) {
            throw new WolfCafeAPIException(HttpStatus.FORBIDDEN, "Admins cannot delete their own account.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        userRepository.delete(user);
    }



}
