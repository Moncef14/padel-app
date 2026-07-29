import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Fermetures } from './fermetures';

describe('Fermetures', () => {
  let component: Fermetures;
  let fixture: ComponentFixture<Fermetures>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Fermetures],
    }).compileComponents();

    fixture = TestBed.createComponent(Fermetures);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
