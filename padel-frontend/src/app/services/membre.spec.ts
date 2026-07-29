import { TestBed } from '@angular/core/testing';

import { Membre } from './membre';

describe('Membre', () => {
  let service: Membre;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Membre);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
