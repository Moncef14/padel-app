import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Matchs } from './matchs';

describe('Matchs', () => {
  let component: Matchs;
  let fixture: ComponentFixture<Matchs>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Matchs],
    }).compileComponents();

    fixture = TestBed.createComponent(Matchs);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
