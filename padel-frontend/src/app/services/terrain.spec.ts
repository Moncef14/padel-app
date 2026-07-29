import { TestBed } from '@angular/core/testing';

import { Terrain } from './terrain';

describe('Terrain', () => {
  let service: Terrain;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Terrain);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
