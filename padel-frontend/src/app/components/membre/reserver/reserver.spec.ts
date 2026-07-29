import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Reserver } from './reserver';

describe('Reserver', () => {
  let component: Reserver;
  let fixture: ComponentFixture<Reserver>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Reserver],
    }).compileComponents();

    fixture = TestBed.createComponent(Reserver);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
