package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentSearchDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;

import java.util.stream.Stream;

public class TournamentServiceImpl implements TournamentService {

    @Override
    public Stream<TournamentListDto> search(TournamentSearchDto searchParameters) {
        // Implement the search logic based on searchParameters
        // Return a Stream of TournamentListDto
        return Stream.empty(); // Placeholder, replace with actual implementation
    }

    @Override
    public TournamentDetailDto create(TournamentDetailDto tournament) throws ValidationException, ConflictException {
        // Implement the creation logic
        // Throw ValidationException if the input is invalid
        // Throw ConflictException if there is a conflict (e.g., duplicate tournament)
        // Return the created TournamentDetailDto
        return null; // Placeholder, replace with actual implementation
    }

    @Override
    public TournamentDetailDto update(TournamentDetailDto tournament) throws NotFoundException, ValidationException, ConflictException {
        // Implement the update logic
        // Throw NotFoundException if the tournament with the given ID is not found
        // Throw ValidationException if the input is invalid
        // Throw ConflictException if there is a conflict (e.g., duplicate tournament)
        // Return the updated TournamentDetailDto
        return null; // Placeholder, replace with actual implementation
    }

    @Override
    public TournamentDetailDto getById(long id) throws NotFoundException {
        // Implement logic to retrieve the tournament by ID
        // Throw NotFoundException if the tournament with the given ID is not found
        // Return the TournamentDetailDto
        return null; // Placeholder, replace with actual implementation
    }

    @Override
    public void deleteTournamentById(long id) throws NotFoundException {
        // Implement logic to delete the tournament by ID
        // Throw NotFoundException if the tournament with the given ID is not found
    }
}
