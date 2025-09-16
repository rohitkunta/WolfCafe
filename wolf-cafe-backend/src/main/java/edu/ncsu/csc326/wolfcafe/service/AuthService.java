package edu.ncsu.csc326.wolfcafe.service;

import java.util.List;

import edu.ncsu.csc326.wolfcafe.dto.JwtAuthResponse;
import edu.ncsu.csc326.wolfcafe.dto.LoginDto;
import edu.ncsu.csc326.wolfcafe.dto.RegisterDto;
import edu.ncsu.csc326.wolfcafe.dto.UserDto;
import edu.ncsu.csc326.wolfcafe.entity.Role;

/**
 * Authorization service
 */
public interface AuthService {
    /**
     * Registers the given user [CUSTOMER ONLY]
     *
     * @param registerDto
     *            new user information
     * @return message for success or failure
     */
    String register ( RegisterDto registerDto );

    /**
     * Creates a new user within the system, for any role
     *
     * @param registerDto
     *            new user information
     * @param role
     *            the role of the user [CUSTOMER, STAFF, ADMIN]
     * @return message for success or failure
     */
    String createUser ( RegisterDto registerDto, Role role );

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
    String editUser ( LoginDto loginDto, RegisterDto registerDto, Role role );

    /**
     * Logins in the given user
     *
     * @param loginDto
     *            username/email and password
     * @return response with authenticated user
     */
    JwtAuthResponse login ( LoginDto loginDto );

    /**
     * Deletes the given user by id
     *
     * @param id
     *            id of user to delete
     */
    void deleteUserByUserName ( String userName );

    /**
     * Returns the list of users in the repository, only can be done by ADMIN
     *
     * @return list of users Dtos
     */
    List<UserDto> getAllUsers ();

	void deleteUserByEmail(String email);
}
