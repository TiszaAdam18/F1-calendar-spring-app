package hu.f1calendar.service;

import hu.f1calendar.model.Circuit;
import hu.f1calendar.repository.CircuitRepository;
import hu.f1calendar.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
public class CircuitService {

    private final CircuitRepository circuitRepository;


    @Autowired
    public CircuitService(CircuitRepository circuitRepository) {
        this.circuitRepository = circuitRepository;
    }

    @Transactional(readOnly = true)
    public List<Circuit> getAllCircuits() {
        return circuitRepository.findAll();
    }


    @Transactional(readOnly = true)
    public Circuit getCircuitById(Long id) {
        return circuitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Circuit not found with id: " + id));
    }


    @Transactional(readOnly = true)
    public Optional<Circuit> getCircuitByName(String name) {
        return circuitRepository.findByName(name);
    }


    @Transactional
    public Circuit createCircuit(Circuit circuit) {
        return circuitRepository.save(circuit);
    }

    @Transactional
    public Circuit updateCircuit(Long id, Circuit circuitDetails) {
        Circuit existingCircuit = circuitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Circuit not found with id: " + id));

        existingCircuit.setName(circuitDetails.getName());
        existingCircuit.setCountry(circuitDetails.getCountry());
        existingCircuit.setCity(circuitDetails.getCity());
        existingCircuit.setTrackLengthKm(circuitDetails.getTrackLengthKm());


        return circuitRepository.save(existingCircuit);
    }


    @Transactional
    public void deleteCircuit(Long id) {
        if (!circuitRepository.existsById(id)) {
            throw new ResourceNotFoundException("Circuit not found with id: " + id);
        }
        circuitRepository.deleteById(id);
    }
}

