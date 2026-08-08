import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { StatsService } from '../../../services/stats';
import { Auth } from '../../../services/auth';
import { DashboardStats, StatsParSite } from '../../../models/stats.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, MatCardModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss'
})
export class Dashboard implements OnInit {

  stats = signal<DashboardStats | null>(null);
  statsParSite = signal<StatsParSite[]>([]);
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

    // évite un appel voué à échouer : /api/stats/par-site est réservé ADMIN_GLOBAL côté backend (@PreAuthorize),
    // un ADMIN_SITE recevrait un 403 — la comparaison entre sites n'a de toute façon pas de sens pour lui
    if (this.auth.isAdminGlobal()) {
      this.statsService.getStatsParSite().subscribe({
        next: (statsSites) => this.statsParSite.set(statsSites)
      });
    }
  }
}