package hu.f1calendar.service;

import hu.f1calendar.model.Driver;
import hu.f1calendar.model.Team;
import hu.f1calendar.repository.DriverRepository;
import hu.f1calendar.repository.TeamRepository;
import hu.f1calendar.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class DriverServiceTest {

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private TeamRepository teamRepository;

    @InjectMocks
    private DriverService driverService;

    private Driver driver1;
    private Driver driver2;
    private Team team1;
    private Team team2;

    @BeforeEach
    void setUp() {
        team1 = new Team(1L, "Mercedes", "Brackley, UK", "Toto Wolff", "W15", null);
        team2 = new Team(2L, "Ferrari", "Maranello, Italy", "Frédéric Vasseur", "SF-24", null);

        driver1 = new Driver(1L, "Lewis", "Hamilton", "British", LocalDate.of(1985, 1, 7), 44, team1);
        driver2 = new Driver(2L, "Charles", "Leclerc", "Monégasque", LocalDate.of(1997, 10, 16), 16, team2);
    }

    @Test
    void getAllDrivers_shouldReturnListOfDrivers() {

        when(driverRepository.findAll()).thenReturn(Arrays.asList(driver1, driver2));


        List<Driver> drivers = driverService.getAllDrivers();


        assertNotNull(drivers);
        assertEquals(2, drivers.size());
        assertEquals("Hamilton", drivers.get(0).getLastName());
        verify(driverRepository, times(1)).findAll();
    }

    @Test
    void getDriverById_whenDriverExists_shouldReturnDriver() {

        when(driverRepository.findById(1L)).thenReturn(Optional.of(driver1));


        Driver foundDriver = driverService.getDriverById(1L);


        assertNotNull(foundDriver);
        assertEquals(driver1.getLastName(), foundDriver.getLastName());
        verify(driverRepository, times(1)).findById(1L);
    }

    @Test
    void getDriverById_whenDriverDoesNotExist_shouldThrowResourceNotFoundException() {

        long nonExistentId = 99L;
        when(driverRepository.findById(nonExistentId)).thenReturn(Optional.empty());


        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            driverService.getDriverById(nonExistentId);
        });
        assertEquals("Driver not found with id: " + nonExistentId, exception.getMessage());
        verify(driverRepository, times(1)).findById(nonExistentId);
    }

    @Test
    void createDriver_withExistingTeam_shouldSaveAndReturnDriverWithTeam() {

        Driver newDriver = new Driver(null, "George", "Russell", "British", LocalDate.of(1998,2,15), 63, null);
        Long teamId = team1.getId();

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team1));
        when(driverRepository.save(any(Driver.class))).thenAnswer(invocation -> {
            Driver driverToSave = invocation.getArgument(0);
            driverToSave.setId(3L);
            return driverToSave;
        });


        Driver createdDriver = driverService.createDriver(newDriver, teamId);


        assertNotNull(createdDriver);
        assertEquals("Russell", createdDriver.getLastName());
        assertNotNull(createdDriver.getId());
        assertEquals(team1, createdDriver.getTeam());
        verify(teamRepository, times(1)).findById(teamId);
        verify(driverRepository, times(1)).save(any(Driver.class));
    }

    @Test
    void createDriver_withoutTeam_shouldSaveAndReturnDriverWithNullTeam() {
        Driver newDriver = new Driver(null, "Nico", "Hulkenberg", "German", LocalDate.of(1987,8,19), 27, null);


        when(driverRepository.save(any(Driver.class))).thenAnswer(invocation -> {
            Driver driverToSave = invocation.getArgument(0);
            driverToSave.setId(4L);
            return driverToSave;
        });


        Driver createdDriver = driverService.createDriver(newDriver, null); // teamId is null


        assertNotNull(createdDriver);
        assertEquals("Hulkenberg", createdDriver.getLastName());
        assertNotNull(createdDriver.getId());
        assertNull(createdDriver.getTeam());
        verify(teamRepository, never()).findById(anyLong());
        verify(driverRepository, times(1)).save(any(Driver.class));
    }

    @Test
    void createDriver_withNonExistingTeam_shouldThrowResourceNotFoundException() {

        Driver newDriver = new Driver(null, "Test", "Driver", "TestNat", LocalDate.now(), 100, null);
        Long nonExistentTeamId = 99L;
        when(teamRepository.findById(nonExistentTeamId)).thenReturn(Optional.empty());


        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            driverService.createDriver(newDriver, nonExistentTeamId);
        });
        assertEquals("Team not found with id: " + nonExistentTeamId + " when creating driver.", exception.getMessage());
        verify(teamRepository, times(1)).findById(nonExistentTeamId);
        verify(driverRepository, never()).save(any(Driver.class));
    }


    @Test
    void updateDriver_withExistingTeam_shouldUpdateAndReturnDriver() {
        Long existingDriverId = 1L;
        Long targetTeamId = team2.getId();
        Driver driverDetailsToUpdate = new Driver(null, "Lewis", "Hamilton Updated", "British", LocalDate.of(1985,1,8), 45, null);

        when(driverRepository.findById(existingDriverId)).thenReturn(Optional.of(driver1));
        when(teamRepository.findById(targetTeamId)).thenReturn(Optional.of(team2));
        when(driverRepository.save(any(Driver.class))).thenAnswer(invocation -> invocation.getArgument(0));


        Driver updatedDriver = driverService.updateDriver(existingDriverId, driverDetailsToUpdate, targetTeamId);


        assertNotNull(updatedDriver);
        assertEquals(existingDriverId, updatedDriver.getId());
        assertEquals("Hamilton Updated", updatedDriver.getLastName());
        assertEquals(45, updatedDriver.getPermanentNumber());
        assertEquals(team2, updatedDriver.getTeam());
        verify(driverRepository, times(1)).findById(existingDriverId);
        verify(teamRepository, times(1)).findById(targetTeamId);
        verify(driverRepository, times(1)).save(any(Driver.class));
    }

    @Test
    void updateDriver_setTeamToNull_shouldUpdateAndReturnDriverWithNullTeam() {
        Long existingDriverId = 1L;
        Driver driverDetailsToUpdate = new Driver(null, "Lewis", "Hamilton", "British", LocalDate.of(1985,1,7), 44, null);


        when(driverRepository.findById(existingDriverId)).thenReturn(Optional.of(driver1));
        when(driverRepository.save(any(Driver.class))).thenAnswer(invocation -> invocation.getArgument(0));


        Driver updatedDriver = driverService.updateDriver(existingDriverId, driverDetailsToUpdate, null);


        assertNotNull(updatedDriver);
        assertNull(updatedDriver.getTeam());
        verify(driverRepository, times(1)).findById(existingDriverId);
        verify(teamRepository, never()).findById(anyLong());
        verify(driverRepository, times(1)).save(any(Driver.class));
    }

    @Test
    void updateDriver_whenDriverDoesNotExist_shouldThrowResourceNotFoundException() {
        Long nonExistentDriverId = 99L;
        Driver driverDetails = new Driver(null, "Test", "Test", "Test", LocalDate.now(), 1, null);
        when(driverRepository.findById(nonExistentDriverId)).thenReturn(Optional.empty());


        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            driverService.updateDriver(nonExistentDriverId, driverDetails, team1.getId());
        });
        assertEquals("Driver not found with id: " + nonExistentDriverId, exception.getMessage());
        verify(driverRepository, times(1)).findById(nonExistentDriverId);
        verify(teamRepository, never()).findById(anyLong());
        verify(driverRepository, never()).save(any(Driver.class));
    }

    @Test
    void updateDriver_whenTargetTeamDoesNotExist_shouldThrowResourceNotFoundException() {
        Long existingDriverId = 1L;
        Long nonExistentTeamId = 99L;
        Driver driverDetailsToUpdate = new Driver(null, "Lewis", "Hamilton", "British", LocalDate.of(1985,1,7), 44, null);

        when(driverRepository.findById(existingDriverId)).thenReturn(Optional.of(driver1));
        when(teamRepository.findById(nonExistentTeamId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            driverService.updateDriver(existingDriverId, driverDetailsToUpdate, nonExistentTeamId);
        });
        assertEquals("Team not found with id: " + nonExistentTeamId + " when updating driver.", exception.getMessage());
        verify(driverRepository, times(1)).findById(existingDriverId);
        verify(teamRepository, times(1)).findById(nonExistentTeamId);
        verify(driverRepository, never()).save(any(Driver.class));
    }


    @Test
    void deleteDriver_whenDriverExists_shouldCallDeleteById() {
        Long existingId = 1L;
        when(driverRepository.existsById(existingId)).thenReturn(true);
        doNothing().when(driverRepository).deleteById(existingId);


        assertDoesNotThrow(() -> driverService.deleteDriver(existingId));


        verify(driverRepository, times(1)).existsById(existingId);
        verify(driverRepository, times(1)).deleteById(existingId);
    }

    @Test
    void deleteDriver_whenDriverDoesNotExist_shouldThrowResourceNotFoundException() {
        Long nonExistentId = 99L;
        when(driverRepository.existsById(nonExistentId)).thenReturn(false);


        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            driverService.deleteDriver(nonExistentId);
        });
        assertEquals("Driver not found with id: " + nonExistentId, exception.getMessage());
        verify(driverRepository, times(1)).existsById(nonExistentId);
        verify(driverRepository, never()).deleteById(nonExistentId);
    }

    @Test
    void getDriversByNationality_shouldReturnFilteredDrivers() {
        String nationality = "British";
        when(driverRepository.findByNationality(nationality)).thenReturn(Collections.singletonList(driver1));


        List<Driver> drivers = driverService.getDriversByNationality(nationality);


        assertNotNull(drivers);
        assertEquals(1, drivers.size());
        assertEquals(driver1.getLastName(), drivers.get(0).getLastName());
        verify(driverRepository, times(1)).findByNationality(nationality);
    }

    @Test
    void getDriversByTeamId_shouldReturnFilteredDrivers() {

        Long teamId = team1.getId();
        when(driverRepository.findByTeamId(teamId)).thenReturn(Collections.singletonList(driver1));


        List<Driver> drivers = driverService.getDriversByTeamId(teamId);


        assertNotNull(drivers);
        assertEquals(1, drivers.size());
        assertEquals(driver1.getLastName(), drivers.get(0).getLastName());
        assertEquals(team1, drivers.get(0).getTeam());
        verify(driverRepository, times(1)).findByTeamId(teamId);
    }

    @Test
    void getDriverByPermanentNumber_whenDriverExists_shouldReturnDriver() {
        Integer permanentNumber = 44;
        when(driverRepository.findByPermanentNumber(permanentNumber)).thenReturn(Optional.of(driver1));

        Optional<Driver> foundDriverOpt = driverService.getDriverByPermanentNumber(permanentNumber);

        assertTrue(foundDriverOpt.isPresent());
        assertEquals(driver1.getLastName(), foundDriverOpt.get().getLastName());
        verify(driverRepository, times(1)).findByPermanentNumber(permanentNumber);
    }

    @Test
    void getDriverByPermanentNumber_whenDriverDoesNotExist_shouldReturnEmptyOptional() {
        Integer nonExistentNumber = 1000;
        when(driverRepository.findByPermanentNumber(nonExistentNumber)).thenReturn(Optional.empty());

        Optional<Driver> foundDriverOpt = driverService.getDriverByPermanentNumber(nonExistentNumber);

        assertFalse(foundDriverOpt.isPresent());
        verify(driverRepository, times(1)).findByPermanentNumber(nonExistentNumber);
    }
}
