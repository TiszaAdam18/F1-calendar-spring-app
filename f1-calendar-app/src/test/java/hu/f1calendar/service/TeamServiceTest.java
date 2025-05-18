package hu.f1calendar.service;

import hu.f1calendar.model.Team;
import hu.f1calendar.repository.TeamRepository;
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
class TeamServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @InjectMocks
    private TeamService teamService;

    private Team team1;
    private Team team2;

    @BeforeEach
    void setUp() {
        team1 = new Team(1L, "Mercedes-AMG PETRONAS F1 Team", "Brackley, UK", "Toto Wolff", "W15", null);
        team2 = new Team(2L, "Scuderia Ferrari", "Maranello, Italy", "Frédéric Vasseur", "SF-24", null);

    }

    @Test
    void getAllTeams_shouldReturnListOfTeams() {
        when(teamRepository.findAll()).thenReturn(Arrays.asList(team1, team2));

        List<Team> teams = teamService.getAllTeams();

        assertNotNull(teams);
        assertEquals(2, teams.size());
        assertEquals("Mercedes-AMG PETRONAS F1 Team", teams.get(0).getName());
        verify(teamRepository, times(1)).findAll();
    }

    @Test
    void getTeamById_whenTeamExists_shouldReturnTeam() {
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team1));


        Team foundTeam = teamService.getTeamById(1L);


        assertNotNull(foundTeam);
        assertEquals(team1.getName(), foundTeam.getName());
        verify(teamRepository, times(1)).findById(1L);
    }

    @Test
    void getTeamById_whenTeamDoesNotExist_shouldThrowResourceNotFoundException() {
        long nonExistentId = 99L;
        when(teamRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            teamService.getTeamById(nonExistentId);
        });
        assertEquals("Team not found with id: " + nonExistentId, exception.getMessage());
        verify(teamRepository, times(1)).findById(nonExistentId);
    }

    @Test
    void getTeamByName_whenTeamExists_shouldReturnTeam() {

        when(teamRepository.findByName("Scuderia Ferrari")).thenReturn(Optional.of(team2));


        Optional<Team> foundTeamOptional = teamService.getTeamByName("Scuderia Ferrari");

        // Assert
        assertTrue(foundTeamOptional.isPresent());
        assertEquals(team2.getName(), foundTeamOptional.get().getName());
        verify(teamRepository, times(1)).findByName("Scuderia Ferrari");
    }

    @Test
    void getTeamByName_whenTeamDoesNotExist_shouldReturnEmptyOptional() {

        String nonExistentName = "NonExistent Team";
        when(teamRepository.findByName(nonExistentName)).thenReturn(Optional.empty());


        Optional<Team> foundTeamOptional = teamService.getTeamByName(nonExistentName);


        assertFalse(foundTeamOptional.isPresent());
        verify(teamRepository, times(1)).findByName(nonExistentName);
    }

    @Test
    void createTeam_shouldSaveAndReturnTeam() {

        Team newTeam = new Team(null, "Red Bull Racing", "Milton Keynes, UK", "Christian Horner", "RB20", null);
        Team savedTeam = new Team(3L, "Red Bull Racing", "Milton Keynes, UK", "Christian Horner", "RB20", null);
        when(teamRepository.save(any(Team.class))).thenReturn(savedTeam);


        Team createdTeam = teamService.createTeam(newTeam);


        assertNotNull(createdTeam);
        assertEquals(savedTeam.getName(), createdTeam.getName());
        assertNotNull(createdTeam.getId());
        assertEquals(3L, createdTeam.getId());
        verify(teamRepository, times(1)).save(newTeam);
    }

    @Test
    void updateTeam_whenTeamExists_shouldUpdateAndReturnTeam() {
        Long existingId = 1L; // team1 ID-ja
        Team teamDetailsToUpdate = new Team(null, "Mercedes Updated", "Brackley V2, UK", "Toto G. Wolff", "W15 Evo", null);

        when(teamRepository.findById(existingId)).thenReturn(Optional.of(team1));
        when(teamRepository.save(any(Team.class))).thenAnswer(invocation -> invocation.getArgument(0));


        Team updatedTeam = teamService.updateTeam(existingId, teamDetailsToUpdate);


        assertNotNull(updatedTeam);
        assertEquals(existingId, updatedTeam.getId());
        assertEquals("Mercedes Updated", updatedTeam.getName());
        assertEquals("Brackley V2, UK", updatedTeam.getBase());
        assertEquals("Toto G. Wolff", updatedTeam.getTeamPrincipal());
        assertEquals("W15 Evo", updatedTeam.getCarName());
        verify(teamRepository, times(1)).findById(existingId);
        verify(teamRepository, times(1)).save(any(Team.class));
    }

    @Test
    void updateTeam_whenTeamDoesNotExist_shouldThrowResourceNotFoundException() {
        Long nonExistentId = 99L;
        Team teamDetails = new Team(null, "NonExistent", "N/A", "N/A", "N/A", null);
        when(teamRepository.findById(nonExistentId)).thenReturn(Optional.empty());


        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            teamService.updateTeam(nonExistentId, teamDetails);
        });
        assertEquals("Team not found with id: " + nonExistentId, exception.getMessage());
        verify(teamRepository, times(1)).findById(nonExistentId);
        verify(teamRepository, never()).save(any(Team.class));
    }

    @Test
    void deleteTeam_whenTeamExists_shouldCallDeleteById() {

        Long existingId = 1L;
        when(teamRepository.existsById(existingId)).thenReturn(true);
        doNothing().when(teamRepository).deleteById(existingId);


        assertDoesNotThrow(() -> teamService.deleteTeam(existingId));


        verify(teamRepository, times(1)).existsById(existingId);
        verify(teamRepository, times(1)).deleteById(existingId);
    }

    @Test
    void deleteTeam_whenTeamDoesNotExist_shouldThrowResourceNotFoundException() {

        Long nonExistentId = 99L;
        when(teamRepository.existsById(nonExistentId)).thenReturn(false);


        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            teamService.deleteTeam(nonExistentId);
        });
        assertEquals("Team not found with id: " + nonExistentId, exception.getMessage());
        verify(teamRepository, times(1)).existsById(nonExistentId);
        verify(teamRepository, never()).deleteById(nonExistentId);
    }
}
