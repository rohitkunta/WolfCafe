/**
 *
 */
package edu.ncsu.csc326.wolfcafe.dto;

import java.util.Collection;

import edu.ncsu.csc326.wolfcafe.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * System User Data Transfer Object
 *
 * @author Rohit Kunta
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long             id;

    private String           name;

    private String           email;

    private String           password;

    private Collection<Role> roles;

}
