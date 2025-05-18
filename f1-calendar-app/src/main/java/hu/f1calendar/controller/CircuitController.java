package hu.f1calendar.controller;

import hu.f1calendar.dto.CircuitDTO;
import hu.f1calendar.model.Circuit;
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
@RequestMapping("/circuits")
public class CircuitController {

    private final CircuitService circuitService;

    @Autowired
    public CircuitController(CircuitService circuitService) {
        this.circuitService = circuitService;
    }


    private CircuitDTO convertToDto(Circuit circuit) {
        CircuitDTO dto = new CircuitDTO();
        dto.setId(circuit.getId());
        dto.setName(circuit.getName());
        dto.setCountry(circuit.getCountry());
        dto.setCity(circuit.getCity());
        dto.setTrackLengthKm(circuit.getTrackLengthKm());
        return dto;
    }


    private Circuit convertToEntity(CircuitDTO dto) {
        Circuit circuit = new Circuit();
        circuit.setId(dto.getId());
        circuit.setName(dto.getName());
        circuit.setCountry(dto.getCountry());
        circuit.setCity(dto.getCity());
        circuit.setTrackLengthKm(dto.getTrackLengthKm());
        return circuit;
    }


    @GetMapping
    public String listCircuits(Model model) {
        List<CircuitDTO> circuitDTOs = circuitService.getAllCircuits().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        model.addAttribute("circuits", circuitDTOs);
        model.addAttribute("pageTitle", "Versenypályák");
        return "circuits";
    }


    @GetMapping("/new")
    public String showCreateCircuitForm(Model model) {
        model.addAttribute("circuitDTO", new CircuitDTO());
        model.addAttribute("pageTitle", "Új Versenypálya Létrehozása");
        model.addAttribute("formAction", "/circuits/save");
        return "circuit-form";
    }


    @PostMapping("/save")
    public String createCircuit(@Valid @ModelAttribute("circuitDTO") CircuitDTO circuitDTO,
                                BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Új Versenypálya Létrehozása");
            model.addAttribute("formAction", "/circuits/save");
            return "circuit-form";
        }
        try {
            if (circuitService.getCircuitByName(circuitDTO.getName()).isPresent()) {
                bindingResult.rejectValue("name", "error.circuitDTO", "Már létezik pálya ezzel a névvel.");
                model.addAttribute("pageTitle", "Új Versenypálya Létrehozása");
                model.addAttribute("formAction", "/circuits/save");
                return "circuit-form";
            }
            Circuit circuit = convertToEntity(circuitDTO);
            circuitService.createCircuit(circuit);
            redirectAttributes.addFlashAttribute("successMessage", "Versenypálya sikeresen létrehozva!");
            return "redirect:/circuits";
        } catch (Exception e) {
            model.addAttribute("pageTitle", "Új Versenypálya Létrehozása");
            model.addAttribute("formAction", "/circuits/save");
            model.addAttribute("errorMessage", "Hiba történt a pálya létrehozása közben: " + e.getMessage());
            return "circuit-form";
        }
    }


    @GetMapping("/edit/{id}")
    public String showEditCircuitForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Circuit circuit = circuitService.getCircuitById(id);
            CircuitDTO circuitDTO = convertToDto(circuit);
            model.addAttribute("circuitDTO", circuitDTO);
            model.addAttribute("pageTitle", "Versenypálya Szerkesztése: " + circuit.getName());
            model.addAttribute("formAction", "/circuits/update/" + id);
            return "circuit-form";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "A keresett versenypálya nem található (ID: " + id + ")");
            return "redirect:/circuits";
        }
    }


    @PostMapping("/update/{id}")
    public String updateCircuit(@PathVariable Long id, @Valid @ModelAttribute("circuitDTO") CircuitDTO circuitDTO,
                                BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Versenypálya Szerkesztése");
            model.addAttribute("formAction", "/circuits/update/" + id);
            return "circuit-form";
        }
        try {
            Circuit existingCircuitWithSameName = circuitService.getCircuitByName(circuitDTO.getName()).orElse(null);
            if (existingCircuitWithSameName != null && !existingCircuitWithSameName.getId().equals(id)) {
                bindingResult.rejectValue("name", "error.circuitDTO", "Már létezik másik pálya ezzel a névvel.");
                model.addAttribute("pageTitle", "Versenypálya Szerkesztése");
                model.addAttribute("formAction", "/circuits/update/" + id);
                return "circuit-form";
            }

            Circuit circuit = convertToEntity(circuitDTO);
            circuitService.updateCircuit(id, circuit);
            redirectAttributes.addFlashAttribute("successMessage", "Versenypálya sikeresen frissítve!");
            return "redirect:/circuits";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/circuits";
        } catch (Exception e) {
            model.addAttribute("pageTitle", "Versenypálya Szerkesztése");
            model.addAttribute("formAction", "/circuits/update/" + id);
            model.addAttribute("errorMessage", "Hiba történt a pálya frissítése közben: " + e.getMessage());
            return "circuit-form";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteCircuit(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            circuitService.deleteCircuit(id);
            redirectAttributes.addFlashAttribute("successMessage", "Versenypálya sikeresen törölve (ID: " + id + ")");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "A törlendő versenypálya nem található (ID: " + id + ")");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Hiba történt a pálya törlése közben (ID: " + id + "): " + e.getMessage() + ". Lehetséges, hogy versenyek vannak hozzárendelve.");
        }
        return "redirect:/circuits";
    }


    @GetMapping("/{id}")
    public String viewCircuitDetails(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Circuit circuit = circuitService.getCircuitById(id);
            model.addAttribute("circuit", convertToDto(circuit));
            model.addAttribute("pageTitle", "Versenypálya Részletei: " + circuit.getName());
            return "circuit-details";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "A keresett versenypálya részletei nem találhatóak (ID: " + id + ")");
            return "redirect:/circuits";
        }
    }
}

