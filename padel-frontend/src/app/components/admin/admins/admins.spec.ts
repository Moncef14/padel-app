import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Admins } from './admins';

describe('Admins', () => {
  let component: Admins;
  let fixture: ComponentFixture<Admins>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Admins],
    }).compileComponents();

    fixture = TestBed.createComponent(Admins);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
