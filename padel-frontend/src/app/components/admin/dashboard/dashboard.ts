import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { StatsService } from '../../../services/stats';
import { Auth } from '../../../services/auth';
import { DashboardStats } from '../../../models/stats.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, MatCardModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss'
})
export class Dashboard implements OnInit {

  stats = signal<DashboardStats | null>(null);
  loading = signal(true);

  constructor(private statsService: StatsService, public auth: Auth) {}

  ngOnInit(): void {
    this.statsService.getDashboard().subscribe({
      next: (stats) => {
        this.stats.set(stats);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }
}