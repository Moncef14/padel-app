USE padeldb;
GO

-- ============================================================
-- SITES (7 communes bruxelloises)
-- ============================================================
SET IDENTITY_INSERT sites ON;

INSERT INTO sites (id, nom, heure_ouverture, heure_fermeture, actif, annee) VALUES
    (1, 'Padel Ixelles',    '08:00:00', '22:00:00', 1, 2026),
    (2, 'Padel Anderlecht', '08:00:00', '22:00:00', 1, 2026),
    (3, 'Padel Uccle',      '08:00:00', '22:00:00', 1, 2026),
    (4, 'Padel Etterbeek',  '08:00:00', '22:00:00', 1, 2026),
    (5, 'Padel Woluwe',     '08:00:00', '22:00:00', 1, 2026),
    (6, 'Padel Watermael',  '08:00:00', '22:00:00', 1, 2026),
    (7, 'Padel Laeken',     '08:00:00', '22:00:00', 1, 2026);

SET IDENTITY_INSERT sites OFF;
GO

-- ============================================================
-- TERRAINS (2-3 terrains par site)
-- ============================================================
SET IDENTITY_INSERT terrains ON;

INSERT INTO terrains (id, numero, site_id) VALUES
    -- Ixelles : 3 terrains
    (1,  1, 1),
    (2,  2, 1),
    (3,  3, 1),
    -- Anderlecht : 2 terrains
    (4,  1, 2),
    (5,  2, 2),
    -- Uccle : 3 terrains
    (6,  1, 3),
    (7,  2, 3),
    (8,  3, 3),
    -- Etterbeek : 2 terrains
    (9,  1, 4),
    (10, 2, 4),
    -- Woluwe : 3 terrains
    (11, 1, 5),
    (12, 2, 5),
    (13, 3, 5),
    -- Watermael : 2 terrains
    (14, 1, 6),
    (15, 2, 6),
    -- Laeken : 2 terrains
    (16, 1, 7),
    (17, 2, 7);

SET IDENTITY_INSERT terrains OFF;
GO

-- ============================================================
-- MEMBRES (10 membres : GLOBAL, SITE, LIBRE)
-- ============================================================
SET IDENTITY_INSERT membres ON;

INSERT INTO membres (id, matricule, nom, prenom, email, type, site_id, solde_du, penalite_jusqu_au) VALUES
    (1,  'G0001', 'Dubois',      'Marie',    'marie.dubois@padel.be',         'GLOBAL', NULL, 0.00,  NULL),         -- belge
    (2,  'G0002', 'El Amrani',   'Karim',    'karim.elamrani@padel.be',       'GLOBAL', NULL, 15.00, NULL),         -- marocaine
    (3,  'S0003', 'Yilmaz',      'Elif',     'elif.yilmaz@padel.be',          'SITE',   1,    0.00,  NULL),         -- turque
    (4,  'S0004', 'Lukamba',     'Samuel',   'samuel.lukamba@padel.be',       'SITE',   1,    0.00,  NULL),         -- congolaise
    (5,  'S0005', 'Garcia',      'Sofia',    'sofia.garcia@padel.be',         'SITE',   3,    0.00,  '2026-09-01'), -- espagnole
    (6,  'S0006', 'Rossi',       'Marco',    'marco.rossi@padel.be',          'SITE',   5,    30.00, NULL),         -- italienne
    (7,  'G0007', 'Kowalska',    'Anna',     'anna.kowalska@padel.be',        'GLOBAL', NULL, 0.00,  NULL),         -- polonaise
    (8,  'L0008', 'Peeters',     'Thomas',   'thomas.peeters@padel.be',       'LIBRE',  NULL, 0.00,  NULL),         -- belge (flamand)
    (9,  'L0009', 'Nzuzi',       'Grace',    'grace.nzuzi@padel.be',          'LIBRE',  NULL, 0.00,  NULL),         -- congolaise
    (10, 'S0010', 'Ben Youssef', 'Fatima',   'fatima.benyoussef@padel.be',    'SITE',   2,    0.00,  NULL);         -- marocaine

SET IDENTITY_INSERT membres OFF;
GO

-- ============================================================
-- ADMINISTRATEURS (1 ADMIN_GLOBAL + 1 ADMIN_SITE)
-- ============================================================
SET IDENTITY_INSERT administrateurs ON;

INSERT INTO administrateurs (id, nom, prenom, email, mot_de_passe, role, site_id) VALUES
    (1, 'Ouassal',  'Moncef',  'moncef.ouassal@padelapp.be',   'admin123', 'ADMIN_GLOBAL', NULL),
    (2, 'Houllich', 'Soumaya', 'soumaya.houllich@padelapp.be', 'admin123', 'ADMIN_SITE',   1);

SET IDENTITY_INSERT administrateurs OFF;
GO

-- ============================================================
-- MATCHS (5 matchs : types et statuts variés)
-- ============================================================
SET IDENTITY_INSERT matchs ON;

INSERT INTO matchs (id, terrain_id, date_heure, type, organisateur_id, statut, montant_total, devenu_public_le) VALUES
    -- Match PUBLIC en attente de joueurs (terrain Ixelles)
    (1, 1,  '2026-06-10 10:00:00', 'PUBLIC',  1, 'EN_ATTENTE',    60.00, NULL),
    -- Match PRIVE complet (terrain Anderlecht)
    (2, 4,  '2026-06-12 14:00:00', 'PRIVE',   3, 'COMPLET',       60.00, NULL),
    -- Match PRIVE annulé (terrain Uccle)
    (3, 7,  '2026-06-15 18:00:00', 'PRIVE',   5, 'ANNULE',        60.00, NULL),
    -- Match PRIVE devenu public (terrain Woluwe)
    (4, 11, '2026-06-20 09:00:00', 'PRIVE',   6, 'DEVENU_PUBLIC', 60.00, '2026-06-18 08:00:00'),
    -- Match PUBLIC en attente (terrain Watermael)
    (5, 14, '2026-06-25 16:00:00', 'PUBLIC',  7, 'EN_ATTENTE',    60.00, NULL);

SET IDENTITY_INSERT matchs OFF;
GO

-- ============================================================
-- INSCRIPTIONS MATCHS
-- ============================================================
SET IDENTITY_INSERT inscription_matchs ON;

INSERT INTO inscription_matchs (id, match_id, membre_id, statut_paiement, montant_paye, date_paiement) VALUES
    -- Match 1 : PUBLIC EN_ATTENTE — Marie (org.) + Karim
    (1,  1, 1,  'PAYE',     15.00, '2026-06-08 11:00:00'),
    (2,  1, 2,  'NON_PAYE',  0.00, NULL),
    -- Match 2 : PRIVE COMPLET — Elif (org.) + 3 joueurs
    (3,  2, 3,  'PAYE',     15.00, '2026-06-10 09:00:00'),
    (4,  2, 4,  'PAYE',     15.00, '2026-06-10 10:30:00'),
    (5,  2, 8,  'PAYE',     15.00, '2026-06-11 08:00:00'),
    (6,  2, 9,  'PAYE',     15.00, '2026-06-11 08:30:00'),
    -- Match 3 : PRIVE ANNULE — Sofia (org.) seule inscrite
    (7,  3, 5,  'NON_PAYE',  0.00, NULL),
    -- Match 4 : DEVENU_PUBLIC — Marco (org.) + Anna
    (8,  4, 6,  'PAYE',     15.00, '2026-06-17 09:00:00'),
    (9,  4, 7,  'NON_PAYE',  0.00, NULL),
    -- Match 5 : PUBLIC EN_ATTENTE — Anna (org.) + Fatima
    (10, 5, 7,  'PAYE',     15.00, '2026-06-22 14:00:00'),
    (11, 5, 10, 'NON_PAYE',  0.00, NULL);

SET IDENTITY_INSERT inscription_matchs OFF;
GO

-- ============================================================
-- JOURS FERMETURE
-- ============================================================
SET IDENTITY_INSERT jours_fermeture ON;

INSERT INTO jours_fermeture (id, date, site_id) VALUES
    -- Jours fériés belges (fermeture globale, site_id NULL)
    (1, '2026-07-21', NULL),   -- Fête nationale belge
    (2, '2026-08-15', NULL),   -- Assomption
    (3, '2026-11-01', NULL),   -- Toussaint
    (4, '2026-12-25', NULL),   -- Noël
    -- Fermetures spécifiques à des sites
    (5, '2026-08-03', 1),      -- Ixelles : travaux de rénovation
    (6, '2026-08-03', 3);      -- Uccle : travaux de rénovation

SET IDENTITY_INSERT jours_fermeture OFF;
GO