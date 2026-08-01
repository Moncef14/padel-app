import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfirmPaymentDialog } from './confirm-payment-dialog';

describe('ConfirmPaymentDialog', () => {
  let component: ConfirmPaymentDialog;
  let fixture: ComponentFixture<ConfirmPaymentDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConfirmPaymentDialog],
    }).compileComponents();

    fixture = TestBed.createComponent(ConfirmPaymentDialog);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
