package edu.pwilder.hipttt.repository;

import edu.pwilder.hipttt.domain.Game;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Game entity.
 */
@SuppressWarnings("unused")
public interface GameRepository extends JpaRepository<Game,Long> {

    @Query("select game from Game game where game.user.login = ?#{principal.username}")
    List<Game> findByUserIsCurrentUser();

}
