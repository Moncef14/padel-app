import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Membres } from './membres';

describe('Membres', () => {
  let component: Membres;
  let fixture: ComponentFixture<Membres>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Membres],
    }).compileComponents();

    fixture = TestBed.createComponent(Membres);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
