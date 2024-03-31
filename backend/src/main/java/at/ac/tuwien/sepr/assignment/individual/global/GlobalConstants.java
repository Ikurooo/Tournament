package at.ac.tuwien.sepr.assignment.individual.global;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailParticipantDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Horse;
import at.ac.tuwien.sepr.assignment.individual.type.Sex;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * Constants used globally in the application.
 */
public class GlobalConstants {

  /**
   * The minimum allowed date in the application.
   */
  public static final LocalDate minDate = LocalDate.of(1900, 1, 1);

  /**
   * The list of expected participants in tournaments.
   * This list should be populated with the expected participants beforehand.
   * Exists for testing purposes.
   */
  public static final List<Horse> expectedHorses = Arrays.asList(
      new Horse()
          .setId(-1L)
          .setName("Wendy")
          .setSex(Sex.FEMALE)
          .setDateOfBirth(LocalDate.of(2019, 8, 5))
          .setHeight(1.40f)
          .setWeight(380)
          .setBreedId(-15L),

      new Horse()
          .setId(-2L)
          .setName("Hugo")
          .setSex(Sex.MALE)
          .setDateOfBirth(LocalDate.of(2020, 2, 20))
          .setHeight(1.20f)
          .setWeight(320)
          .setBreedId(-20L),

      new Horse()
          .setId(-3L)
          .setName("Bella")
          .setSex(Sex.FEMALE)
          .setDateOfBirth(LocalDate.of(2005, 4, 8))
          .setHeight(1.45f)
          .setWeight(550)
          .setBreedId(-1L),

      new Horse()
          .setId(-4L)
          .setName("Thunder")
          .setSex(Sex.MALE)
          .setDateOfBirth(LocalDate.of(2008, 7, 15))
          .setHeight(1.60f)
          .setWeight(600)
          .setBreedId(-2L),

      new Horse()
          .setId(-5L)
          .setName("Luna")
          .setSex(Sex.FEMALE)
          .setDateOfBirth(LocalDate.of(2012, 11, 22))
          .setHeight(1.65f)
          .setWeight(650)
          .setBreedId(-3L),

      new Horse()
          .setId(-6L)
          .setName("Apollo")
          .setSex(Sex.MALE)
          .setDateOfBirth(LocalDate.of(2003, 9, 3))
          .setHeight(1.52f)
          .setWeight(500)
          .setBreedId(-4L),

      new Horse()
          .setId(-7L)
          .setName("Sophie")
          .setSex(Sex.FEMALE)
          .setDateOfBirth(LocalDate.of(2010, 6, 18))
          .setHeight(1.70f)
          .setWeight(700)
          .setBreedId(-5L),


      new Horse()
          .setId(-8L)
          .setName("Max")
          .setSex(Sex.MALE)
          .setDateOfBirth(LocalDate.of(2006, 3, 27))
          .setHeight(1.55f)
          .setWeight(580)
          .setBreedId(-6L)
  );

  public static final List<TournamentDetailParticipantDto> expectedParticipants = Arrays.asList(
      new TournamentDetailParticipantDto()
          .setHorseId(-1L)
          .setName("Wendy")
          .setDateOfBirth(LocalDate.of(2019, 8, 5)),

      new TournamentDetailParticipantDto()
          .setHorseId(-2L)
          .setName("Hugo")
          .setDateOfBirth(LocalDate.of(2020, 2, 20)),

      new TournamentDetailParticipantDto()
          .setHorseId(-3L)
          .setName("Bella")
          .setDateOfBirth(LocalDate.of(2005, 4, 8)),

      new TournamentDetailParticipantDto()
          .setHorseId(-4L)
          .setName("Thunder")
          .setDateOfBirth(LocalDate.of(2008, 7, 15)),

      new TournamentDetailParticipantDto()
          .setHorseId(-5L)
          .setName("Luna")
          .setDateOfBirth(LocalDate.of(2012, 11, 22)),

      new TournamentDetailParticipantDto()
          .setHorseId(-6L)
          .setName("Apollo")
          .setDateOfBirth(LocalDate.of(2003, 9, 3)),

      new TournamentDetailParticipantDto()
          .setHorseId(-7L)
          .setName("Sophie")
          .setDateOfBirth(LocalDate.of(2010, 6, 18)),


      new TournamentDetailParticipantDto()
          .setHorseId(-8L)
          .setName("Max")
          .setDateOfBirth(LocalDate.of(2006, 3, 27))
  );
}
