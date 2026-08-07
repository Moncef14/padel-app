describe('Parcours complet — Inscription, connexion, réservation', () => {

  const timestamp = Date.now();
  const email = `cypress.test.${timestamp}@test.be`;
  const prenom = 'Cypress';
  const nom = 'Test';

  it('permet à un nouveau membre de s\'inscrire, se connecter et réserver un match', () => {

    // === ÉTAPE 1 : Inscription ===
    cy.visit('/register');

    cy.get('input[name="prenom"]').type(prenom);
    cy.get('input[name="nom"]').type(nom);
    cy.get('input[name="email"]').type(email);
    cy.get('input[name="motDePasse"]').type('TestPassword123');

    // Sélection du type de membre : LIBRE (pas besoin de choisir un site)
    cy.get('mat-select[name="type"]').click();
    cy.contains('mat-option', 'Libre').click();

    cy.get('button[type="submit"]').click();

    // === ÉTAPE 2 : Vérification de la redirection après inscription ===
    cy.url().should('include', '/membre/mes-matchs');
    cy.contains('Mes matchs').should('be.visible');

    // === ÉTAPE 3 : Aller réserver un match ===
    cy.visit('/membre/reserver');

    // Type de match
    cy.get('mat-select[name="type"]').click();
    cy.contains('mat-option', 'Public').click();

    // Site
    cy.get('mat-select[name="siteId"]').click();
    cy.get('mat-option').first().click();

    // Terrain (attendre que la liste se charge après sélection du site)
    cy.wait(500);
    cy.get('mat-select[name="terrainId"]').click();
    cy.get('mat-option').first().click();

    // Date — ouvrir le datepicker via son bouton (un clic sur l'input seul
    // n'ouvre pas le calendrier Angular Material) et choisir une date valide
    // suffisamment loin (LIBRE = 5 jours minimum, cf. dateFilter du composant)
    cy.get('mat-datepicker-toggle').click();
    cy.get('.mat-calendar-body-cell:not(.mat-calendar-body-disabled)')
      .first()
      .click();

    // Fermer le datepicker en cliquant en dehors, pour libérer le backdrop
    // qui bloquerait sinon le clic suivant sur le créneau horaire
    cy.get('body').click(0, 0);

    // Attendre le chargement des créneaux et sélectionner le premier disponible
    cy.wait(500);
    cy.get('.creneau-btn:not(.indisponible)').first().click();

    // Confirmer la réservation
    cy.contains('button', 'Confirmer la réservation').click();

    // === ÉTAPE 4 : Vérification dans Mes matchs ===
    cy.url().should('include', '/membre/mes-matchs');
    cy.contains('À venir').click();

    // Le nouveau match doit apparaître dans la liste des matchs actifs
    cy.get('.match-card').should('have.length.at.least', 1);
  });
});