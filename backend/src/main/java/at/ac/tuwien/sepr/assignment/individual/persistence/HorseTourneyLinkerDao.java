package at.ac.tuwien.sepr.assignment.individual.persistence;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.FailedToCreateException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;

public interface HorseTourneyLinkerDao {

  /**
   * Create a new horse with the provided data and store it in the persistent data store.
   *
   * @param tournament the horse to create
   * @return the created horse
   */
  // TODO: this thingy
  Tournament create(TournamentDetailDto tournament) throws ValidationException, ConflictException, FailedToCreateException;
}
