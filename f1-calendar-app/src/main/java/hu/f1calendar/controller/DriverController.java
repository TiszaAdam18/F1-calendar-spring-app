package hu.f1calendar.controller;

import hu.f1calendar.dto.DriverDTO;
import hu.f1calendar.model.Driver;
import hu.f1calendar.model.Team;
import hu.f1calendar.service.DriverService;
import hu.f1calendar.service.TeamService;
import hu.f1calendar.exception.ResourceNotFoundException;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/drivers")
public class DriverController {

    private final DriverService driverService;
    private final TeamService teamService;

    @Autowired
    public DriverController(DriverService driverService, TeamService teamService) {
        this.driverService = driverService;
        this.teamService = teamService;
    }


    private DriverDTO convertToDto(Driver driver) {
        DriverDTO dto = new DriverDTO();
        dto.setId(driver.getId());
        dto.setFirstName(driver.getFirstName());
        dto.setLastName(driver.getLastName());
        dto.setNationality(driver.getNationality());
        dto.setDateOfBirth(driver.getDateOfBirth());
        dto.setPermanentNumber(driver.getPermanentNumber());
        if (driver.getTeam() != null) {
            dto.setTeamId(driver.getTeam().getId());
            dto.setTeamName(driver.getTeam().getName());
        } else {
            dto.setTeamId(null);
            dto.setTeamName("Nincs csapata");
        }
        return dto;
    }


    private Driver convertToEntity(DriverDTO dto) {
        Driver driver = new Driver();
        driver.setId(dto.getId());
        driver.setFirstName(dto.getFirstName());
        driver.setLastName(dto.getLastName());
        driver.setNationality(dto.getNationality());
        driver.setDateOfBirth(dto.getDateOfBirth());
        driver.setPermanentNumber(dto.getPermanentNumber());
        return driver;
    }


    @GetMapping
    public String listDrivers(Model model) {
        List<DriverDTO> driverDTOs = driverService.getAllDrivers().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        model.addAttribute("drivers", driverDTOs);
        model.addAttribute("pageTitle", "Versenyzők");
        return "drivers"; // templates/drivers.html
    }


    @GetMapping("/new")
    public String showCreateDriverForm(Model model) {
        model.addAttribute("driverDTO", new DriverDTO());
        model.addAttribute("teams", teamService.getAllTeams()); // Csapatok a legördülő listához
        model.addAttribute("pageTitle", "Új Versenyző Létrehozása");
        model.addAttribute("formAction", "/drivers/save");
        return "driver-form"; // templates/driver-form.html
    }


    @PostMapping("/save")
    public String createDriver(@Valid @ModelAttribute("driverDTO") DriverDTO driverDTO,
                               BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (driverDTO.getPermanentNumber() != null && driverService.getDriverByPermanentNumber(driverDTO.getPermanentNumber()).isPresent()) {
            bindingResult.rejectValue("permanentNumber", "error.driverDTO", "Ez a rajtszám már foglalt.");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("teams", teamService.getAllTeams());
            model.addAttribute("pageTitle", "Új Versenyző Létrehozása");
            model.addAttribute("formAction", "/drivers/save");
            return "driver-form";
        }
        try {
            Driver driver = convertToEntity(driverDTO);
            driverService.createDriver(driver, driverDTO.getTeamId());
            redirectAttributes.addFlashAttribute("successMessage", "Versenyző sikeresen létrehozva!");
            return "redirect:/drivers";
        } catch (ResourceNotFoundException e) {
            model.addAttribute("teams", teamService.getAllTeams());
            model.addAttribute("pageTitle", "Új Versenyző Létrehozása");
            model.addAttribute("formAction", "/drivers/save");
            bindingResult.rejectValue("teamId", "error.driverDTO", e.getMessage());
            return "driver-form";
        } catch (Exception e) {
            model.addAttribute("teams", teamService.getAllTeams());
            model.addAttribute("pageTitle", "Új Versenyző Létrehozása");
            model.addAttribute("formAction", "/drivers/save");
            model.addAttribute("errorMessage", "Hiba történt a versenyző létrehozása közben: " + e.getMessage());
            return "driver-form";
        }
    }


    @GetMapping("/edit/{id}")
    public String showEditDriverForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Driver driver = driverService.getDriverById(id);
            DriverDTO driverDTO = convertToDto(driver);
            model.addAttribute("driverDTO", driverDTO);
            model.addAttribute("teams", teamService.getAllTeams());
            model.addAttribute("pageTitle", "Versenyző Szerkesztése: " + driver.getFirstName() + " " + driver.getLastName());
            model.addAttribute("formAction", "/drivers/update/" + id);
            return "driver-form";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "A keresett versenyző nem található (ID: " + id + ")");
            return "redirect:/drivers";
        }
    }


    @PostMapping("/update/{id}")
    public String updateDriver(@PathVariable Long id, @Valid @ModelAttribute("driverDTO") DriverDTO driverDTO,
                               BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (driverDTO.getPermanentNumber() != null) {
            driverService.getDriverByPermanentNumber(driverDTO.getPermanentNumber()).ifPresent(existingDriver -> {
                if (!existingDriver.getId().equals(id)) {
                    bindingResult.rejectValue("permanentNumber", "error.driverDTO", "Ez a rajtszám már foglalt egy másik versenyző által.");
                }
            });
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("teams", teamService.getAllTeams());
            model.addAttribute("pageTitle", "Versenyző Szerkesztése");
            model.addAttribute("formAction", "/drivers/update/" + id);
            return "driver-form";
        }
        try {
            Driver driver = convertToEntity(driverDTO);
            driverService.updateDriver(id, driver, driverDTO.getTeamId());
            redirectAttributes.addFlashAttribute("successMessage", "Versenyző sikeresen frissítve!");
            return "redirect:/drivers";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/drivers";
        } catch (Exception e) {
            model.addAttribute("teams", teamService.getAllTeams());
            model.addAttribute("pageTitle", "Versenyző Szerkesztése");
            model.addAttribute("formAction", "/drivers/update/" + id);
            model.addAttribute("errorMessage", "Hiba történt a versenyző frissítése közben: " + e.getMessage());
            return "driver-form";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteDriver(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            driverService.deleteDriver(id);
            redirectAttributes.addFlashAttribute("successMessage", "Versenyző sikeresen törölve (ID: " + id + ")");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "A törlendő versenyző nem található (ID: " + id + ")");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Hiba történt a versenyző törlése közben (ID: " + id + "): " + e.getMessage());
        }
        return "redirect:/drivers";
    }

    @GetMapping("/{id}")
    public String viewDriverDetails(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Driver driver = driverService.getDriverById(id);
            model.addAttribute("driver", convertToDto(driver));
            model.addAttribute("pageTitle", "Versenyző Részletei: " + driver.getFirstName() + " " + driver.getLastName());
            return "driver-details";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "A keresett versenyző részletei nem találhatóak (ID: " + id + ")");
            return "redirect:/drivers";
        }
    }
}

