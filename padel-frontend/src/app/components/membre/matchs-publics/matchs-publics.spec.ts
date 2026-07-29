import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MatchsPublics } from './matchs-publics';

describe('MatchsPublics', () => {
  let component: MatchsPublics;
  let fixture: ComponentFixture<MatchsPublics>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MatchsPublics],
    }).compileComponents();

    fixture = TestBed.createComponent(MatchsPublics);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
