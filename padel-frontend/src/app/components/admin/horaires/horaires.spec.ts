import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Horaires } from './horaires';

describe('Horaires', () => {
  let component: Horaires;
  let fixture: ComponentFixture<Horaires>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Horaires],
    }).compileComponents();

    fixture = TestBed.createComponent(Horaires);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
