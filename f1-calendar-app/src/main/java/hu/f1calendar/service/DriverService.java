package hu.f1calendar.service;

import hu.f1calendar.model.Driver;
import hu.f1calendar.model.Team;
import hu.f1calendar.repository.DriverRepository;
import hu.f1calendar.repository.TeamRepository;
import hu.f1calendar.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
public class DriverService {

    private final DriverRepository driverRepository;
    private final TeamRepository teamRepository;

    @Autowired
    public DriverService(DriverRepository driverRepository, TeamRepository teamRepository) {
        this.driverRepository = driverRepository;
        this.teamRepository = teamRepository;
    }

    @Transactional(readOnly = true)
    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Driver getDriverById(Long id) {
        return driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + id));
    }

    @Transactional
    public Driver createDriver(Driver driver, Long teamId) {
        if (teamId != null) {
            Team team = teamRepository.findById(teamId)
                    .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId + " when creating driver."));
            driver.setTeam(team);
        } else {
            driver.setTeam(null);
        }
        return driverRepository.save(driver);
    }

    @Transactional
    public Driver updateDriver(Long id, Driver driverDetails, Long teamId) {
        Driver existingDriver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + id));

        existingDriver.setFirstName(driverDetails.getFirstName());
        existingDriver.setLastName(driverDetails.getLastName());
        existingDriver.setNationality(driverDetails.getNationality());
        existingDriver.setDateOfBirth(driverDetails.getDateOfBirth());
        existingDriver.setPermanentNumber(driverDetails.getPermanentNumber());

        if (teamId != null) {
            Team team = teamRepository.findById(teamId)
                    .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId + " when updating driver."));
            existingDriver.setTeam(team);
        } else {
            existingDriver.setTeam(null);
        }

        return driverRepository.save(existingDriver);
    }

    @Transactional
    public void deleteDriver(Long id) {
        if (!driverRepository.existsById(id)) {
            throw new ResourceNotFoundException("Driver not found with id: " + id);
        }
        driverRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Driver> getDriversByNationality(String nationality) {
        return driverRepository.findByNationality(nationality);
    }

    @Transactional(readOnly = true)
    public List<Driver> getDriversByTeamId(Long teamId) {
        return driverRepository.findByTeamId(teamId);
    }

    @Transactional(readOnly = true)
    public Optional<Driver> getDriverByPermanentNumber(Integer permanentNumber) {
        return driverRepository.findByPermanentNumber(permanentNumber);
    }
}
