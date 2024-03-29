package at.ac.tuwien.sepr.assignment.individual.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import at.ac.tuwien.sepr.assignment.individual.TestBase;
import at.ac.tuwien.sepr.assignment.individual.dto.BreedDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Horse;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.exception.FailedToDeleteException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.mapper.HorseMapper;
import at.ac.tuwien.sepr.assignment.individual.persistence.impl.HorseJdbcDao;
import at.ac.tuwien.sepr.assignment.individual.service.BreedService;
import at.ac.tuwien.sepr.assignment.individual.service.HorseService;
import at.ac.tuwien.sepr.assignment.individual.service.HorseServiceImpl;
import at.ac.tuwien.sepr.assignment.individual.type.Sex;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({"test", "datagen"})
// enable "test" spring profile during test execution in order to pick up configuration from application-test.yml
@SpringBootTest
public class HorseDaoTest extends TestBase {

  @Autowired
  HorseDao horseDao;

  @Autowired
  HorseMapper horseMapper;

  @Autowired
  BreedService breedService;

  @Test
  public void deleteByIdThrowsNotFoundException() {
    assertThatThrownBy(() ->  horseDao.delete(1L))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("No horse with ID 1 found for deletion");
  }

  @Test
  public void deleteExistingHorse() throws NotFoundException {
    horseDao.delete(-10L);
    assertThatThrownBy(() ->  horseDao.delete(-10L))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("No horse with ID -10 found for deletion");
  }

  @Test
  public void deleteExistingHorseButHorseIsInTournament() {
    assertThatThrownBy(() -> horseDao.delete(-1L))
        .isInstanceOf(FailedToDeleteException.class)
        .hasMessageContaining("Failed to delete horse with ID -1");
  }

  @Test
  public void updateExistingHorse() throws NotFoundException {
    var horseDto = new HorseDetailDto(
        -2L,
        "Updated Hugo",
        Sex.MALE,
        LocalDate.of(2020, 1, 1),
        1.5f,
        500,
        new BreedDto(-1L, "Andalusian")
    );

    var horse = horseDao.update(horseDto);
    assertThat(horse)
        .usingRecursiveComparison()
        .isEqualTo(new Horse()
            .setId(-2L)
            .setName("Updated Hugo")
            .setSex(Sex.MALE)
            .setDateOfBirth(LocalDate.of(2020, 1, 1))
            .setHeight(1.5f)
            .setWeight(500)
            .setBreedId(-1L));
  }

  @Test
  public void updateNonexistentHorseThrowsNotFoundException() {
    var horseDto = new HorseDetailDto(
        -50L,
        "Nonexistent Horse",
        Sex.MALE,
        LocalDate.of(2020, 1, 1),
        1.5f,
        500,
        new BreedDto(-1L, "Some Breed")
    );

    assertThatThrownBy(() -> horseDao.update(horseDto))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("Could not update horse with ID -50, because it does not exist");
  }

  @Test
  public void searchByBreedWelFindsThreeHorses() {
    var searchDto = new HorseSearchDto(null, null, null, null, "Wel", null);
    var horses = horseDao.search(searchDto);
    assertNotNull(horses);
    assertThat(horses)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactlyInAnyOrder(
            (new Horse())
                .setId(-32L)
                .setName("Luna")
                .setSex(Sex.FEMALE)
                .setDateOfBirth(LocalDate.of(2018, 10, 10))
                .setHeight(1.62f)
                .setWeight(670)
                .setBreedId(-19L),
            (new Horse())
                .setId(-21L)
                .setName("Bella")
                .setSex(Sex.FEMALE)
                .setDateOfBirth(LocalDate.of(2003, 7, 6))
                .setHeight(1.50f)
                .setWeight(580)
                .setBreedId(-19L),
            (new Horse())
                .setId(-2L)
                .setName("Hugo")
                .setSex(Sex.MALE)
                .setDateOfBirth(LocalDate.of(2020, 2, 20))
                .setHeight(1.20f)
                .setWeight(320)
                .setBreedId(-20L));
  }

  @Test
  public void searchByBirthDateBetween2017And2018ReturnsFourHorses() {
    var searchDto = new HorseSearchDto(null, null,
        LocalDate.of(2017, 3, 5),
        LocalDate.of(2018, 10, 10),
        null, null);
    var horses = horseDao.search(searchDto);
    assertNotNull(horses);
    assertThat(horses)
        .hasSize(4)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactlyInAnyOrder(
            (new Horse())
                .setId(-24L)
                .setName("Rocky")
                .setSex(Sex.MALE)
                .setDateOfBirth(LocalDate.of(2018, 8, 19))
                .setHeight(1.42f)
                .setWeight(480)
                .setBreedId(-6L),
            (new Horse())
                .setId(-26L)
                .setName("Daisy")
                .setSex(Sex.FEMALE)
                .setDateOfBirth(LocalDate.of(2017, 12, 1))
                .setHeight(1.28f)
                .setWeight(340)
                .setBreedId(-9L),
            (new Horse())
                .setId(-31L)
                .setName("Leo")
                .setSex(Sex.MALE)
                .setDateOfBirth(LocalDate.of(2017, 3, 5))
                .setHeight(1.70f)
                .setWeight(720)
                .setBreedId(-8L),
            (new Horse())
                .setId(-32L)
                .setName("Luna")
                .setSex(Sex.FEMALE)
                .setDateOfBirth(LocalDate.of(2018, 10, 10))
                .setHeight(1.62f)
                .setWeight(670)
                .setBreedId(-19L));
  }

  @Test
  public void getHorseWithInvalidId() {
    long invalidId = 42069L;
    assertThatThrownBy(() -> horseDao.getById(invalidId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("No horse with ID " + invalidId + " found");
  }

  @Test
  public void getHorseWithValidId() throws NotFoundException {
    long validId = -32L;

    var horse = horseDao.getById(validId);
    assertThat(horse)
        .usingRecursiveComparison()
        .isEqualTo(new Horse()
            .setId(-32L)
            .setName("Luna")
            .setSex(Sex.FEMALE)
            .setDateOfBirth(LocalDate.of(2018, 10, 10))
            .setHeight(1.62f)
            .setWeight(670)
            .setBreedId(-19L));
  }

  @Test
  public void createValidHorse() {
    var horseDto = new HorseDetailDto(
        null,
        "Valid Hugo",
        Sex.MALE,
        LocalDate.of(2020, 1, 1),
        1.5f,
        500,
        new BreedDto(-1L, "Andalusian")
    );
    var horse = horseDao.create(horseDto);
    assertThat(horse)
        .usingRecursiveComparison()
        .isEqualTo(new Horse()
            .setId(1L)
            .setName("Valid Hugo")
            .setSex(Sex.MALE)
            .setDateOfBirth(LocalDate.of(2020, 1, 1))
            .setHeight(1.5f)
            .setWeight(500)
            .setBreedId(-1L));

  }
}
