import { ComponentFixture, TestBed } from '@angular/core/testing';
import { vi } from 'vitest';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

import { ConfirmPaymentDialog } from './confirm-payment-dialog';

describe('ConfirmPaymentDialog', () => {
  let component: ConfirmPaymentDialog;
  let fixture: ComponentFixture<ConfirmPaymentDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConfirmPaymentDialog],
      providers: [
        { provide: MatDialogRef, useValue: { close: vi.fn() } },
        { provide: MAT_DIALOG_DATA, useValue: { montant: 15 } },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ConfirmPaymentDialog);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
