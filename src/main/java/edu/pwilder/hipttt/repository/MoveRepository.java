package edu.pwilder.hipttt.repository;

import edu.pwilder.hipttt.domain.Move;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Move entity.
 */
@SuppressWarnings("unused")
public interface MoveRepository extends JpaRepository<Move,Long> {

}
