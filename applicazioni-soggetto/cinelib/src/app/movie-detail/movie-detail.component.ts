import { Component, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { MovieService } from '../movie.service';
import { ToastService } from '../toast.service';
import { CastRowComponent } from '../cast-row/cast-row.component';
import { ReviewsComponent } from '../reviews/reviews.component';
import { PanelComponent } from '../panel/panel.component';

@Component({
  selector: 'app-movie-detail',
  imports: [RouterLink, CastRowComponent, ReviewsComponent, PanelComponent],
  templateUrl: './movie-detail.component.html'
})
export class MovieDetailComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly movieService = inject(MovieService);
  private readonly toastService = inject(ToastService);
  private readonly router = inject(Router);

  readonly confirmOpen = signal(false);

  readonly movie = computed(() => {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    return this.movieService.movies().find((m) => m.id === id);
  });

  toggleFavorite(): void {
    const m = this.movie();
    if (!m) return;
    const isFav = this.movieService.toggleFavorite(m.id);
    this.toastService.show(
      isFav ? `Added “${m.title}” to favorites` : `Removed “${m.title}” from favorites`,
      isFav ? 'success' : 'info'
    );
  }

  openDelete(): void {
    this.confirmOpen.set(true);
  }

  cancelDelete(): void {
    this.confirmOpen.set(false);
  }

  confirmDelete(): void {
    const m = this.movie();
    if (m) {
      this.movieService.remove(m.id);
      this.toastService.show(`Deleted “${m.title}”`, 'danger');
      this.router.navigate(['/']);
    }
    this.confirmOpen.set(false);
  }
}
