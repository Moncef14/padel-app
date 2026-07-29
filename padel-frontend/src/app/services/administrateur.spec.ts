import { TestBed } from '@angular/core/testing';

import { Administrateur } from './administrateur';

describe('Administrateur', () => {
  let service: Administrateur;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Administrateur);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
