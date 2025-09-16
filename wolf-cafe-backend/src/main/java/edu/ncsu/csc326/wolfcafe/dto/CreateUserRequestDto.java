/**
 *
 */
package edu.ncsu.csc326.wolfcafe.dto;

import edu.ncsu.csc326.wolfcafe.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This class exists solely for sending the registerDto and Role objects in one
 * single HTTP request, for creating the user while specifying the role
 *
 * @author Rohit Kunta
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequestDto {

    private RegisterDto registerDto;
    private Role        role;

}
