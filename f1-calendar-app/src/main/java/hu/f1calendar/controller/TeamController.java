package hu.f1calendar.controller;

import hu.f1calendar.dto.TeamDTO;
import hu.f1calendar.model.Team;
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
@RequestMapping("/teams")
public class TeamController {

    private final TeamService teamService;

    @Autowired
    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    private TeamDTO convertToDto(Team team) {
        TeamDTO dto = new TeamDTO();
        dto.setId(team.getId());
        dto.setName(team.getName());
        dto.setBase(team.getBase());
        dto.setTeamPrincipal(team.getTeamPrincipal());
        dto.setCarName(team.getCarName());
        return dto;
    }

    private Team convertToEntity(TeamDTO dto) {
        Team team = new Team();
        team.setId(dto.getId()); // Fontos az update-hez
        team.setName(dto.getName());
        team.setBase(dto.getBase());
        team.setTeamPrincipal(dto.getTeamPrincipal());
        team.setCarName(dto.getCarName());
        return team;
    }

    @GetMapping
    public String listTeams(Model model) {
        List<TeamDTO> teamDTOs = teamService.getAllTeams().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        model.addAttribute("teams", teamDTOs);
        model.addAttribute("pageTitle", "Csapatok");
        return "teams"; // templates/teams.html
    }

    @GetMapping("/new")
    public String showCreateTeamForm(Model model) {
        model.addAttribute("teamDTO", new TeamDTO());
        model.addAttribute("pageTitle", "Új Csapat Létrehozása");
        model.addAttribute("formAction", "/teams/save");
        return "team-form";
    }

    @PostMapping("/save")
    public String createTeam(@Valid @ModelAttribute("teamDTO") TeamDTO teamDTO,
                             BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Új Csapat Létrehozása");
            model.addAttribute("formAction", "/teams/save");
            return "team-form";
        }
        try {
            if (teamService.getTeamByName(teamDTO.getName()).isPresent()) {
                bindingResult.rejectValue("name", "error.teamDTO", "Már létezik csapat ezzel a névvel.");
                model.addAttribute("pageTitle", "Új Csapat Létrehozása");
                model.addAttribute("formAction", "/teams/save");
                return "team-form";
            }
            Team team = convertToEntity(teamDTO);
            teamService.createTeam(team);
            redirectAttributes.addFlashAttribute("successMessage", "Csapat sikeresen létrehozva!");
            return "redirect:/teams";
        } catch (Exception e) {
            model.addAttribute("pageTitle", "Új Csapat Létrehozása");
            model.addAttribute("formAction", "/teams/save");
            model.addAttribute("errorMessage", "Hiba történt a csapat létrehozása közben: " + e.getMessage());
            return "team-form";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditTeamForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Team team = teamService.getTeamById(id);
            TeamDTO teamDTO = convertToDto(team);
            model.addAttribute("teamDTO", teamDTO);
            model.addAttribute("pageTitle", "Csapat Szerkesztése: " + team.getName());
            model.addAttribute("formAction", "/teams/update/" + id);
            return "team-form";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "A keresett csapat nem található (ID: " + id + ")");
            return "redirect:/teams";
        }
    }

    @PostMapping("/update/{id}")
    public String updateTeam(@PathVariable Long id, @Valid @ModelAttribute("teamDTO") TeamDTO teamDTO,
                             BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Csapat Szerkesztése");
            model.addAttribute("formAction", "/teams/update/" + id);
            return "team-form";
        }
        try {
            // Ellenőrzés, hogy a módosított név nem ütközik-e más csapatéval
            Team existingTeamWithSameName = teamService.getTeamByName(teamDTO.getName()).orElse(null);
            if (existingTeamWithSameName != null && !existingTeamWithSameName.getId().equals(id)) {
                bindingResult.rejectValue("name", "error.teamDTO", "Már létezik másik csapat ezzel a névvel.");
                model.addAttribute("pageTitle", "Csapat Szerkesztése");
                model.addAttribute("formAction", "/teams/update/" + id);
                return "team-form";
            }

            Team team = convertToEntity(teamDTO);
            teamService.updateTeam(id, team);
            redirectAttributes.addFlashAttribute("successMessage", "Csapat sikeresen frissítve!");
            return "redirect:/teams";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/teams";
        } catch (Exception e) {
            model.addAttribute("pageTitle", "Csapat Szerkesztése");
            model.addAttribute("formAction", "/teams/update/" + id);
            model.addAttribute("errorMessage", "Hiba történt a csapat frissítése közben: " + e.getMessage());
            return "team-form";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteTeam(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            teamService.deleteTeam(id);
            redirectAttributes.addFlashAttribute("successMessage", "Csapat sikeresen törölve (ID: " + id + ")");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "A törlendő csapat nem található (ID: " + id + ")");
        } catch (Exception e) { // Pl. DataIntegrityViolationException
            redirectAttributes.addFlashAttribute("errorMessage", "Hiba történt a csapat törlése közben (ID: " + id + "): " + e.getMessage() + ". Lehetséges, hogy versenyzők vannak hozzárendelve.");
        }
        return "redirect:/teams";
    }

    @GetMapping("/{id}")
    public String viewTeamDetails(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Team team = teamService.getTeamById(id);
            model.addAttribute("team", convertToDto(team));
            model.addAttribute("pageTitle", "Csapat Részletei: " + team.getName());
            return "team-details"; // templates/team-details.html
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "A keresett csapat részletei nem találhatóak (ID: " + id + ")");
            return "redirect:/teams";
        }
    }
}

