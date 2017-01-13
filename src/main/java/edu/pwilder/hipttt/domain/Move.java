package edu.pwilder.hipttt.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A Move.
 */
@Entity
@Table(name = "move")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Move implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "timestamp")
    private ZonedDateTime timestamp;

    @NotNull
    @Min(value = 0)
    @Column(name = "row", nullable = false)
    private Integer row;

    @NotNull
    @Min(value = 0)
    @Column(name = "col", nullable = false)
    private Integer col;

    @NotNull
    @Min(value = 0)
    @Column(name = "player", nullable = false)
    private Integer player;

    @ManyToOne
    private Game game;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public Move timestamp(ZonedDateTime timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public void setTimestamp(ZonedDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getRow() {
        return row;
    }

    public Move row(Integer row) {
        this.row = row;
        return this;
    }

    public void setRow(Integer row) {
        this.row = row;
    }

    public Integer getCol() {
        return col;
    }

    public Move col(Integer col) {
        this.col = col;
        return this;
    }

    public void setCol(Integer col) {
        this.col = col;
    }

    public Integer getPlayer() {
        return player;
    }

    public Move player(Integer player) {
        this.player = player;
        return this;
    }

    public void setPlayer(Integer player) {
        this.player = player;
    }

    public Game getGame() {
        return game;
    }

    public Move game(Game game) {
        this.game = game;
        return this;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Move move = (Move) o;
        if (move.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, move.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Move{" +
            "id=" + id +
            ", timestamp='" + timestamp + "'" +
            ", row='" + row + "'" +
            ", col='" + col + "'" +
            ", player='" + player + "'" +
            '}';
    }
}
