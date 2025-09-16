/**
 *
 */
package edu.ncsu.csc326.wolfcafe.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc326.wolfcafe.entity.Order;

/**
 * Repository interface for Order objects.
 *
 * @author Rohit Kunta
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

}
