/**
 *
 */
package edu.ncsu.csc326.wolfcafe.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Status for an Order.
 *
 * @author Rohit Kunta
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table ( name = "orderstatuses" )
public class OrderStatus {
    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY )
    private Long   id;
    private String name;

}
