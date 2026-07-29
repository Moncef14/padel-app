import { TestBed } from '@angular/core/testing';

import { JourFermeture } from './jour-fermeture';

describe('JourFermeture', () => {
  let service: JourFermeture;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(JourFermeture);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
