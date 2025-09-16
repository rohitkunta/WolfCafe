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
 * This class exists solely to encapsulate the multiple Java Objects that are
 * sent in the HTTP request for EditUser (loginDto, registerDto, and Role)
 *
 * @author Rohit Kunta
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EditUserRequestDto {

    private LoginDto    loginDto;
    private RegisterDto registerDto;
    private Role        role;

}
