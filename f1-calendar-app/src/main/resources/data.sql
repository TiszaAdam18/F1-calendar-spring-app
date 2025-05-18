
-- Versenypályák (Circuits)
INSERT INTO CIRCUIT (name, country, city, track_length_km) VALUES
('Bahrain International Circuit', 'Bahrain', 'Sakhir', 5.412),
('Jeddah Street Circuit', 'Saudi Arabia', 'Jeddah', 6.174),
('Albert Park Circuit', 'Australia', 'Melbourne', 5.278),
('Suzuka International Racing Course', 'Japan', 'Suzuka', 5.807),
('Shanghai International Circuit', 'China', 'Shanghai', 5.451),
('Miami International Autodrome', 'USA', 'Miami Gardens', 5.412),
('Autodromo Enzo e Dino Ferrari', 'Italy', 'Imola', 4.909),
('Circuit de Monaco', 'Monaco', 'Monte Carlo', 3.337),
('Circuit Gilles Villeneuve', 'Canada', 'Montreal', 4.361),
('Circuit de Barcelona-Catalunya', 'Spain', 'Montmeló', 4.657),
('Red Bull Ring', 'Austria', 'Spielberg', 4.318),
('Silverstone Circuit', 'UK', 'Silverstone', 5.891),
('Hungaroring', 'Hungary', 'Mogyoród', 4.381),
('Circuit de Spa-Francorchamps', 'Belgium', 'Stavelot', 7.004),
('Zandvoort Circuit', 'Netherlands', 'Zandvoort', 4.259);

-- Csapatok (Teams)
INSERT INTO TEAM (name, base, team_principal, car_name) VALUES
('Oracle Red Bull Racing', 'Milton Keynes, UK', 'Christian Horner', 'RB20'),
('Mercedes-AMG PETRONAS F1 Team', 'Brackley, UK', 'Toto Wolff', 'W15'),
('Scuderia Ferrari', 'Maranello, Italy', 'Frédéric Vasseur', 'SF-24'),
('McLaren Formula 1 Team', 'Woking, UK', 'Andrea Stella', 'MCL38'),
('Aston Martin Aramco F1 Team', 'Silverstone, UK', 'Mike Krack', 'AMR24');

-- Versenyzők (Drivers) - Néhány példa, team_id-val
INSERT INTO DRIVER (first_name, last_name, nationality, date_of_birth, permanent_number, team_id) VALUES
('Max', 'Verstappen', 'Dutch', '1997-09-30', 1, 1), -- Red Bull
('Sergio', 'Pérez', 'Mexican', '1990-01-26', 11, 1), -- Red Bull
('Lewis', 'Hamilton', 'British', '1985-01-07', 44, 2), -- Mercedes
('George', 'Russell', 'British', '1998-02-15', 63, 2), -- Mercedes
('Charles', 'Leclerc', 'Monégasque', '1997-10-16', 16, 3), -- Ferrari
('Carlos', 'Sainz Jr.', 'Spanish', '1994-09-01', 55, 3), -- Ferrari
('Lando', 'Norris', 'British', '1999-11-13', 4, 4), -- McLaren
('Oscar', 'Piastri', 'Australian', '2001-04-06', 81, 4), -- McLaren
('Fernando', 'Alonso', 'Spanish', '1981-07-29', 14, 5), -- Aston Martin
('Lance', 'Stroll', 'Canadian', '1998-10-29', 18, 5); -- Aston Martin


-- Versenyek (Races) - Néhány példa, circuit_id-val
INSERT INTO RACE (name, date, start_time, laps, circuit_id) VALUES
('Bahrain Grand Prix', '2024-03-02', '18:00:00', 57, 1),
('Saudi Arabian Grand Prix', '2024-03-09', '20:00:00', 50, 2),
('Australian Grand Prix', '2024-03-24', '06:00:00', 58, 3),
('Japanese Grand Prix', '2024-04-07', '07:00:00', 53, 4),
('Hungarian Grand Prix', '2024-07-21', '15:00:00', 70, 13);

