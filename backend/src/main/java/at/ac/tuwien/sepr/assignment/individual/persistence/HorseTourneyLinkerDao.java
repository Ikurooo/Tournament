package at.ac.tuwien.sepr.assignment.individual.persistence;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.entity.Horse;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.FailedToCreateException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;

import java.util.List;

public interface HorseTourneyLinkerDao {

  /**
   * Create a new tournament with the provided data and store it in the persistent data store.
   *
   * @param tournament the tournament to create
   * @return the created tournament
   * @throws ValidationException if the provided tournament data is invalid
   * @throws ConflictException if there is a conflict when creating the tournament
   * @throws FailedToCreateException if the tournament creation process fails
   */
  Tournament create(TournamentDetailDto tournament) throws ValidationException, ConflictException, FailedToCreateException;

  /**
   * Retrieve the list of participants (horses) for the tournament with the specified ID.
   *
   * @param id the ID of the tournament
   * @return the list of participants for the tournament
   */
  List<Horse> findParticipantsByTournamentId(long id);
}
