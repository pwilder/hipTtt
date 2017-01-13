package edu.pwilder.hipttt.service;

import edu.pwilder.hipttt.domain.Move;
import edu.pwilder.hipttt.repository.MoveRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

/**
 * Service Implementation for managing Move.
 */
@Service
@Transactional
public class MoveService {

    private final Logger log = LoggerFactory.getLogger(MoveService.class);
    
    @Inject
    private MoveRepository moveRepository;

    /**
     * Save a move.
     *
     * @param move the entity to save
     * @return the persisted entity
     */
    public Move save(Move move) {
        log.debug("Request to save Move : {}", move);
        Move result = moveRepository.save(move);
        return result;
    }

    /**
     *  Get all the moves.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<Move> findAll(Pageable pageable) {
        log.debug("Request to get all Moves");
        Page<Move> result = moveRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one move by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public Move findOne(Long id) {
        log.debug("Request to get Move : {}", id);
        Move move = moveRepository.findOne(id);
        return move;
    }

    /**
     *  Delete the  move by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Move : {}", id);
        moveRepository.delete(id);
    }
}
