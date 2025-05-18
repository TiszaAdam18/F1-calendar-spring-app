package hu.f1calendar.service;

import hu.f1calendar.model.Circuit;
import hu.f1calendar.repository.CircuitRepository;
import hu.f1calendar.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CircuitServiceTest {

    @Mock
    private CircuitRepository circuitRepository;

    @InjectMocks
    private CircuitService circuitService;

    private Circuit circuit1;
    private Circuit circuit2;

    @BeforeEach
    void setUp() {
        circuit1 = new Circuit(1L, "Hungaroring", "Hungary", "Mogyoród", 4.381, null);
        circuit2 = new Circuit(2L, "Monza", "Italy", "Monza", 5.793, null);
    }

    @Test
    void getAllCircuits_shouldReturnListOfCircuits() {
        when(circuitRepository.findAll()).thenReturn(Arrays.asList(circuit1, circuit2));


        List<Circuit> circuits = circuitService.getAllCircuits();


        assertNotNull(circuits);
        assertEquals(2, circuits.size());
        assertEquals("Hungaroring", circuits.get(0).getName());
        assertEquals("Monza", circuits.get(1).getName());


        verify(circuitRepository, times(1)).findAll();
    }

    @Test
    void getCircuitById_whenCircuitExists_shouldReturnCircuit() {
        when(circuitRepository.findById(1L)).thenReturn(Optional.of(circuit1));

        Circuit foundCircuit = circuitService.getCircuitById(1L);

        assertNotNull(foundCircuit);
        assertEquals(circuit1.getName(), foundCircuit.getName());
        verify(circuitRepository, times(1)).findById(1L);
    }

    @Test
    void getCircuitById_whenCircuitDoesNotExist_shouldThrowResourceNotFoundException() {
        long nonExistentId = 99L;
        when(circuitRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            circuitService.getCircuitById(nonExistentId);
        });

        assertEquals("Circuit not found with id: " + nonExistentId, exception.getMessage());
        verify(circuitRepository, times(1)).findById(nonExistentId);
    }

    @Test
    void getCircuitByName_whenCircuitExists_shouldReturnCircuit() {
        when(circuitRepository.findByName("Hungaroring")).thenReturn(Optional.of(circuit1));

        Optional<Circuit> foundCircuitOptional = circuitService.getCircuitByName("Hungaroring");

        assertTrue(foundCircuitOptional.isPresent());
        assertEquals(circuit1.getName(), foundCircuitOptional.get().getName());
        verify(circuitRepository, times(1)).findByName("Hungaroring");
    }

    @Test
    void getCircuitByName_whenCircuitDoesNotExist_shouldReturnEmptyOptional() {

        String nonExistentName = "NonExistentTrack";
        when(circuitRepository.findByName(nonExistentName)).thenReturn(Optional.empty());


        Optional<Circuit> foundCircuitOptional = circuitService.getCircuitByName(nonExistentName);


        assertFalse(foundCircuitOptional.isPresent());
        verify(circuitRepository, times(1)).findByName(nonExistentName);
    }

    @Test
    void createCircuit_shouldSaveAndReturnCircuit() {

        Circuit newCircuit = new Circuit(null, "Silverstone", "UK", "Silverstone", 5.891, null);
        Circuit savedCircuit = new Circuit(3L, "Silverstone", "UK", "Silverstone", 5.891, null);
        when(circuitRepository.save(any(Circuit.class))).thenReturn(savedCircuit);


        Circuit createdCircuit = circuitService.createCircuit(newCircuit);


        assertNotNull(createdCircuit);
        assertEquals(savedCircuit.getName(), createdCircuit.getName());
        assertNotNull(createdCircuit.getId()); // Ellenőrizzük, hogy kapott-e ID-t
        assertEquals(3L, createdCircuit.getId());
        verify(circuitRepository, times(1)).save(newCircuit);
    }

    @Test
    void updateCircuit_whenCircuitExists_shouldUpdateAndReturnCircuit() {

        Long existingId = 1L;
        Circuit updatedDetails = new Circuit(null, "Hungaroring Updated", "Hungary", "Mogyorod City", 4.400, null);


        when(circuitRepository.findById(existingId)).thenReturn(Optional.of(circuit1));
        when(circuitRepository.save(any(Circuit.class))).thenAnswer(invocation -> invocation.getArgument(0));



        Circuit updatedCircuit = circuitService.updateCircuit(existingId, updatedDetails);


        assertNotNull(updatedCircuit);
        assertEquals(existingId, updatedCircuit.getId());
        assertEquals("Hungaroring Updated", updatedCircuit.getName());
        assertEquals("Mogyorod City", updatedCircuit.getCity());
        assertEquals(4.400, updatedCircuit.getTrackLengthKm());

        verify(circuitRepository, times(1)).findById(existingId);
        verify(circuitRepository, times(1)).save(any(Circuit.class));
    }


    @Test
    void updateCircuit_whenCircuitDoesNotExist_shouldThrowResourceNotFoundException() {
        Long nonExistentId = 99L;
        Circuit circuitDetails = new Circuit(null, "NonExistent", "N/A", "N/A", 0.0, null);
        when(circuitRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            circuitService.updateCircuit(nonExistentId, circuitDetails);
        });
        assertEquals("Circuit not found with id: " + nonExistentId, exception.getMessage());
        verify(circuitRepository, times(1)).findById(nonExistentId);
        verify(circuitRepository, never()).save(any(Circuit.class));
    }

    @Test
    void deleteCircuit_whenCircuitExists_shouldCallDeleteById() {

        Long existingId = 1L;

        when(circuitRepository.existsById(existingId)).thenReturn(true);
        doNothing().when(circuitRepository).deleteById(existingId);



        assertDoesNotThrow(() -> circuitService.deleteCircuit(existingId));


        verify(circuitRepository, times(1)).existsById(existingId);
        verify(circuitRepository, times(1)).deleteById(existingId);
    }

    @Test
    void deleteCircuit_whenCircuitDoesNotExist_shouldThrowResourceNotFoundException() {

        Long nonExistentId = 99L;
        when(circuitRepository.existsById(nonExistentId)).thenReturn(false);


        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            circuitService.deleteCircuit(nonExistentId);
        });

        assertEquals("Circuit not found with id: " + nonExistentId, exception.getMessage());
        verify(circuitRepository, times(1)).existsById(nonExistentId);
        verify(circuitRepository, never()).deleteById(nonExistentId);
    }
}

