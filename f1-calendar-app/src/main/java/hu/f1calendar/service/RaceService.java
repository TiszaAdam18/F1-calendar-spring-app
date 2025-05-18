package hu.f1calendar.service;

import hu.f1calendar.model.Race;
import hu.f1calendar.model.Circuit;
import hu.f1calendar.repository.RaceRepository;
import hu.f1calendar.repository.CircuitRepository;
import hu.f1calendar.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class RaceService {

    private final RaceRepository raceRepository;
    private final CircuitRepository circuitRepository; // Hozzáadva a Circuit kezeléséhez

    @Autowired
    public RaceService(RaceRepository raceRepository, CircuitRepository circuitRepository) {
        this.raceRepository = raceRepository;
        this.circuitRepository = circuitRepository;
    }

    @Transactional(readOnly = true)
    public List<Race> getAllRaces() {
        return raceRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Race getRaceById(Long id) {
        return raceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Race not found with id: " + id));
    }

    @Transactional
    public Race createRace(Race race, Long circuitId) {
        Circuit circuit = circuitRepository.findById(circuitId)
                .orElseThrow(() -> new ResourceNotFoundException("Circuit not found with id: " + circuitId + " when creating race."));
        race.setCircuit(circuit);
        return raceRepository.save(race);
    }

    @Transactional
    public Race updateRace(Long id, Race raceDetails, Long circuitId) {
        Race existingRace = raceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Race not found with id: " + id));

        Circuit circuit = circuitRepository.findById(circuitId)
                .orElseThrow(() -> new ResourceNotFoundException("Circuit not found with id: " + circuitId + " when updating race."));

        existingRace.setName(raceDetails.getName());
        existingRace.setDate(raceDetails.getDate());
        existingRace.setStartTime(raceDetails.getStartTime());
        existingRace.setLaps(raceDetails.getLaps());
        existingRace.setCircuit(circuit);

        return raceRepository.save(existingRace);
    }

    @Transactional
    public void deleteRace(Long id) {
        if (!raceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Race not found with id: " + id);
        }
        raceRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Race> getRacesAfterDate(LocalDate date) {
        return raceRepository.findByDateAfter(date);
    }

    @Transactional(readOnly = true)
    public List<Race> getRacesByCircuitId(Long circuitId) {
        return raceRepository.findByCircuitId(circuitId);
    }
}

