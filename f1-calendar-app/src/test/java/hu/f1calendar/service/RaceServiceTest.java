package hu.f1calendar.service;


import hu.f1calendar.model.Race;
import hu.f1calendar.model.Circuit;
import hu.f1calendar.repository.RaceRepository;
import hu.f1calendar.repository.CircuitRepository;
import hu.f1calendar.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Collections;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class RaceServiceTest {

    @Mock
    private RaceRepository raceRepository;

    @Mock
    private CircuitRepository circuitRepository;

    @InjectMocks
    private RaceService raceService;

    private Race race1;
    private Race race2;
    private Circuit circuit1;
    private Circuit circuit2;

    @BeforeEach
    void setUp() {
        circuit1 = new Circuit(1L, "Hungaroring", "Hungary", "Mogyoród", 4.381, null);
        circuit2 = new Circuit(2L, "Monza", "Italy", "Monza", 5.793, null);

        race1 = new Race(1L, "Hungarian Grand Prix", LocalDate.of(2024, 7, 21), LocalTime.of(15, 0), 70, circuit1);
        race2 = new Race(2L, "Italian Grand Prix", LocalDate.of(2024, 9, 1), LocalTime.of(15, 0), 53, circuit2);
    }

    @Test
    void getAllRaces_shouldReturnListOfRaces() {
        when(raceRepository.findAll()).thenReturn(Arrays.asList(race1, race2));


        List<Race> races = raceService.getAllRaces();


        assertNotNull(races);
        assertEquals(2, races.size());
        assertEquals("Hungarian Grand Prix", races.get(0).getName());
        verify(raceRepository, times(1)).findAll();
    }

    @Test
    void getRaceById_whenRaceExists_shouldReturnRace() {
        when(raceRepository.findById(1L)).thenReturn(Optional.of(race1));

        Race foundRace = raceService.getRaceById(1L);

        assertNotNull(foundRace);
        assertEquals(race1.getName(), foundRace.getName());
        verify(raceRepository, times(1)).findById(1L);
    }

    @Test
    void getRaceById_whenRaceDoesNotExist_shouldThrowResourceNotFoundException() {
        long nonExistentId = 99L;
        when(raceRepository.findById(nonExistentId)).thenReturn(Optional.empty());


        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            raceService.getRaceById(nonExistentId);
        });
        assertEquals("Race not found with id: " + nonExistentId, exception.getMessage());
        verify(raceRepository, times(1)).findById(nonExistentId);
    }

    @Test
    void createRace_whenCircuitExists_shouldSaveAndReturnRace() {
        Race newRace = new Race(null, "New GP", LocalDate.now().plusDays(10), LocalTime.of(14,0), 60, null);
        Long circuitId = circuit1.getId();


        when(circuitRepository.findById(circuitId)).thenReturn(Optional.of(circuit1));

        when(raceRepository.save(any(Race.class))).thenAnswer(invocation -> {
            Race raceToSave = invocation.getArgument(0);
            raceToSave.setId(3L);
            return raceToSave;
        });

        Race createdRace = raceService.createRace(newRace, circuitId);


        assertNotNull(createdRace);
        assertEquals("New GP", createdRace.getName());
        assertNotNull(createdRace.getId());
        assertEquals(circuit1, createdRace.getCircuit());
        verify(circuitRepository, times(1)).findById(circuitId);
        verify(raceRepository, times(1)).save(any(Race.class));
    }

    @Test
    void createRace_whenCircuitDoesNotExist_shouldThrowResourceNotFoundException() {
        Race newRace = new Race(null, "New GP", LocalDate.now().plusDays(10), LocalTime.of(14,0), 60, null);
        Long nonExistentCircuitId = 99L;
        when(circuitRepository.findById(nonExistentCircuitId)).thenReturn(Optional.empty());


        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            raceService.createRace(newRace, nonExistentCircuitId);
        });
        assertEquals("Circuit not found with id: " + nonExistentCircuitId + " when creating race.", exception.getMessage());
        verify(circuitRepository, times(1)).findById(nonExistentCircuitId);
        verify(raceRepository, never()).save(any(Race.class));
    }

    @Test
    void updateRace_whenRaceAndCircuitExist_shouldUpdateAndReturnRace() {
        Long existingRaceId = 1L;
        Long existingCircuitId = circuit2.getId();
        Race raceDetailsToUpdate = new Race(null, "Hungarian GP Updated", LocalDate.of(2024,7,22), LocalTime.of(16,0), 65, null);

        when(raceRepository.findById(existingRaceId)).thenReturn(Optional.of(race1));
        when(circuitRepository.findById(existingCircuitId)).thenReturn(Optional.of(circuit2));
        when(raceRepository.save(any(Race.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Race updatedRace = raceService.updateRace(existingRaceId, raceDetailsToUpdate, existingCircuitId);


        assertNotNull(updatedRace);
        assertEquals(existingRaceId, updatedRace.getId());
        assertEquals("Hungarian GP Updated", updatedRace.getName());
        assertEquals(LocalDate.of(2024,7,22), updatedRace.getDate());
        assertEquals(circuit2, updatedRace.getCircuit());
        verify(raceRepository, times(1)).findById(existingRaceId);
        verify(circuitRepository, times(1)).findById(existingCircuitId);
        verify(raceRepository, times(1)).save(any(Race.class));
    }

    @Test
    void updateRace_whenRaceDoesNotExist_shouldThrowResourceNotFoundException() {

        Long nonExistentRaceId = 99L;
        Long existingCircuitId = circuit1.getId();
        Race raceDetailsToUpdate = new Race(null, "Update Fail", LocalDate.now(), LocalTime.now(), 10, null);
        when(raceRepository.findById(nonExistentRaceId)).thenReturn(Optional.empty());



        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            raceService.updateRace(nonExistentRaceId, raceDetailsToUpdate, existingCircuitId);
        });
        assertEquals("Race not found with id: " + nonExistentRaceId, exception.getMessage());
        verify(raceRepository, times(1)).findById(nonExistentRaceId);
        verify(circuitRepository, never()).findById(anyLong());
        verify(raceRepository, never()).save(any(Race.class));
    }

    @Test
    void updateRace_whenCircuitDoesNotExist_shouldThrowResourceNotFoundException() {
        Long existingRaceId = 1L;
        Long nonExistentCircuitId = 99L;
        Race raceDetailsToUpdate = new Race(null, "Update Fail Circuit", LocalDate.now(), LocalTime.now(), 10, null);

        when(raceRepository.findById(existingRaceId)).thenReturn(Optional.of(race1));
        when(circuitRepository.findById(nonExistentCircuitId)).thenReturn(Optional.empty());


        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            raceService.updateRace(existingRaceId, raceDetailsToUpdate, nonExistentCircuitId);
        });
        assertEquals("Circuit not found with id: " + nonExistentCircuitId + " when updating race.", exception.getMessage());
        verify(raceRepository, times(1)).findById(existingRaceId);
        verify(circuitRepository, times(1)).findById(nonExistentCircuitId);
        verify(raceRepository, never()).save(any(Race.class));
    }


    @Test
    void deleteRace_whenRaceExists_shouldCallDeleteById() {
        Long existingId = 1L;
        when(raceRepository.existsById(existingId)).thenReturn(true);
        doNothing().when(raceRepository).deleteById(existingId);


        assertDoesNotThrow(() -> raceService.deleteRace(existingId));


        verify(raceRepository, times(1)).existsById(existingId);
        verify(raceRepository, times(1)).deleteById(existingId);
    }

    @Test
    void deleteRace_whenRaceDoesNotExist_shouldThrowResourceNotFoundException() {
        Long nonExistentId = 99L;
        when(raceRepository.existsById(nonExistentId)).thenReturn(false);


        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            raceService.deleteRace(nonExistentId);
        });
        assertEquals("Race not found with id: " + nonExistentId, exception.getMessage());
        verify(raceRepository, times(1)).existsById(nonExistentId);
        verify(raceRepository, never()).deleteById(nonExistentId);
    }

    @Test
    void getRacesAfterDate_shouldReturnFilteredRaces() {
        LocalDate filterDate = LocalDate.of(2024, 8, 1);

        when(raceRepository.findByDateAfter(filterDate)).thenReturn(Collections.singletonList(race2));


        List<Race> races = raceService.getRacesAfterDate(filterDate);


        assertNotNull(races);
        assertEquals(1, races.size());
        assertEquals(race2.getName(), races.get(0).getName());
        verify(raceRepository, times(1)).findByDateAfter(filterDate);
    }

    @Test
    void getRacesByCircuitId_shouldReturnFilteredRaces() {
        Long circuitIdToFilter = circuit1.getId();
        when(raceRepository.findByCircuitId(circuitIdToFilter)).thenReturn(Collections.singletonList(race1));


        List<Race> races = raceService.getRacesByCircuitId(circuitIdToFilter);

        assertNotNull(races);
        assertEquals(1, races.size());
        assertEquals(race1.getName(), races.get(0).getName());
        assertEquals(circuit1, races.get(0).getCircuit());
        verify(raceRepository, times(1)).findByCircuitId(circuitIdToFilter);
    }
}

