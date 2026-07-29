import { TestBed } from '@angular/core/testing';

import { Inscription } from './inscription';

describe('Inscription', () => {
  let service: Inscription;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Inscription);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
