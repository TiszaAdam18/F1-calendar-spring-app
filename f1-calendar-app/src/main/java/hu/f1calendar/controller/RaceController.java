package hu.f1calendar.controller;

import hu.f1calendar.dto.RaceDTO;
import hu.f1calendar.model.Circuit;
import hu.f1calendar.model.Race;
import hu.f1calendar.service.RaceService;
import hu.f1calendar.service.CircuitService;
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
@RequestMapping("/races")
public class RaceController {

    private final RaceService raceService;
    private final CircuitService circuitService;

    @Autowired
    public RaceController(RaceService raceService, CircuitService circuitService) {
        this.raceService = raceService;
        this.circuitService = circuitService;
    }

    private RaceDTO convertToDto(Race race) {
        RaceDTO raceDTO = new RaceDTO();
        raceDTO.setId(race.getId());
        raceDTO.setName(race.getName());
        raceDTO.setDate(race.getDate());
        raceDTO.setStartTime(race.getStartTime());
        raceDTO.setLaps(race.getLaps());
        if (race.getCircuit() != null) {
            raceDTO.setCircuitId(race.getCircuit().getId());
            raceDTO.setCircuitName(race.getCircuit().getName());
        }
        return raceDTO;
    }

    private Race convertToEntity(RaceDTO raceDTO) {
        Race race = new Race();
        race.setId(raceDTO.getId()); // Fontos az update-hez
        race.setName(raceDTO.getName());
        race.setDate(raceDTO.getDate());
        race.setStartTime(raceDTO.getStartTime());
        race.setLaps(raceDTO.getLaps());
        // A Circuit beállítása a service réteg feladata lesz a raceDTO.getCircuitId() alapján
        return race;
    }



    @GetMapping
    public String listRaces(Model model) {
        List<RaceDTO> raceDTOs = raceService.getAllRaces().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        model.addAttribute("races", raceDTOs);
        model.addAttribute("pageTitle", "Versenyek");
        return "races"; // templates/races.html
    }

    @GetMapping("/new")
    public String showCreateRaceForm(Model model) {
        model.addAttribute("raceDTO", new RaceDTO());
        model.addAttribute("circuits", circuitService.getAllCircuits()); // Pályák a legördülő listához
        model.addAttribute("pageTitle", "Új Verseny Létrehozása");
        model.addAttribute("formAction", "/races/save");
        return "race-form"; // templates/race-form.html
    }

    @PostMapping("/save")
    public String createRace(@Valid @ModelAttribute("raceDTO") RaceDTO raceDTO,
                             BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("circuits", circuitService.getAllCircuits());
            model.addAttribute("pageTitle", "Új Verseny Létrehozása");
            model.addAttribute("formAction", "/races/save");
            return "race-form";
        }
        try {
            Race race = convertToEntity(raceDTO);
            raceService.createRace(race, raceDTO.getCircuitId());
            redirectAttributes.addFlashAttribute("successMessage", "Verseny sikeresen létrehozva!");
            return "redirect:/races";
        } catch (ResourceNotFoundException e) { // Ha a circuitId érvénytelen
            model.addAttribute("circuits", circuitService.getAllCircuits());
            model.addAttribute("pageTitle", "Új Verseny Létrehozása");
            model.addAttribute("formAction", "/races/save");
            bindingResult.rejectValue("circuitId", "error.raceDTO", e.getMessage());
            return "race-form";
        } catch (Exception e) {
            model.addAttribute("circuits", circuitService.getAllCircuits());
            model.addAttribute("pageTitle", "Új Verseny Létrehozása");
            model.addAttribute("formAction", "/races/save");
            model.addAttribute("errorMessage", "Hiba történt a verseny létrehozása közben: " + e.getMessage());
            return "race-form";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditRaceForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Race race = raceService.getRaceById(id);
            RaceDTO raceDTO = convertToDto(race);
            model.addAttribute("raceDTO", raceDTO);
            model.addAttribute("circuits", circuitService.getAllCircuits());
            model.addAttribute("pageTitle", "Verseny Szerkesztése: " + race.getName());
            model.addAttribute("formAction", "/races/update/" + id);
            return "race-form";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "A keresett verseny nem található (ID: " + id + ")");
            return "redirect:/races";
        }
    }

    @PostMapping("/update/{id}")
    public String updateRace(@PathVariable Long id, @Valid @ModelAttribute("raceDTO") RaceDTO raceDTO,
                             BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("circuits", circuitService.getAllCircuits());
            model.addAttribute("pageTitle", "Verseny Szerkesztése");
            model.addAttribute("formAction", "/races/update/" + id);
            return "race-form";
        }
        try {
            Race race = convertToEntity(raceDTO);
            raceService.updateRace(id, race, raceDTO.getCircuitId());
            redirectAttributes.addFlashAttribute("successMessage", "Verseny sikeresen frissítve!");
            return "redirect:/races";
        } catch (ResourceNotFoundException e) { // Ha a raceId vagy circuitId érvénytelen
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/races"; // Vagy vissza az űrlapra specifikusabb hibával
        } catch (Exception e) {
            model.addAttribute("circuits", circuitService.getAllCircuits());
            model.addAttribute("pageTitle", "Verseny Szerkesztése");
            model.addAttribute("formAction", "/races/update/" + id);
            model.addAttribute("errorMessage", "Hiba történt a verseny frissítése közben: " + e.getMessage());
            return "race-form";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteRace(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            raceService.deleteRace(id);
            redirectAttributes.addFlashAttribute("successMessage", "Verseny sikeresen törölve (ID: " + id + ")");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "A törlendő verseny nem található (ID: " + id + ")");
        } catch (Exception e) { // Egyéb adatbázis hibák, pl. integritási megsértés
            redirectAttributes.addFlashAttribute("errorMessage", "Hiba történt a verseny törlése közben (ID: " + id + "): " + e.getMessage());
        }
        return "redirect:/races";
    }

    @GetMapping("/{id}")
    public String viewRaceDetails(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Race race = raceService.getRaceById(id);
            model.addAttribute("race", convertToDto(race));
            model.addAttribute("pageTitle", "Verseny Részletei: " + race.getName());
            return "race-details"; // templates/race-details.html
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "A keresett verseny részletei nem találhatóak (ID: " + id + ")");
            return "redirect:/races";
        }
    }
}
