import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Sites } from './sites';

describe('Sites', () => {
  let component: Sites;
  let fixture: ComponentFixture<Sites>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Sites],
    }).compileComponents();

    fixture = TestBed.createComponent(Sites);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
