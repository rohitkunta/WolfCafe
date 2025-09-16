/**
 *
 */
package edu.ncsu.csc326.wolfcafe.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc326.wolfcafe.entity.OrderStatus;

/**
 * Repository interface for Order statuses.
 *
 * @author Rohit Kunta
 */
public interface OrderStatusRepository extends JpaRepository<OrderStatus, Long> {

    OrderStatus findByName ( String name );

}
