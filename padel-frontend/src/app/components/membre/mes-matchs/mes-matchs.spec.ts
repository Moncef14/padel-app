import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MesMatchs } from './mes-matchs';

describe('MesMatchs', () => {
  let component: MesMatchs;
  let fixture: ComponentFixture<MesMatchs>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MesMatchs],
    }).compileComponents();

    fixture = TestBed.createComponent(MesMatchs);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
