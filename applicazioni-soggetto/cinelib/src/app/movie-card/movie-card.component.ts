import { Component, inject, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Movie } from '../movie.model';
import { MovieService } from '../movie.service';
import { ToastService } from '../toast.service';

@Component({
  selector: 'app-movie-card',
  imports: [RouterLink],
  templateUrl: './movie-card.component.html'
})
export class MovieCardComponent {
  private readonly movieService = inject(MovieService);
  private readonly toastService = inject(ToastService);

  readonly movie = input.required<Movie>();
  readonly view = input<'grid' | 'list'>('grid');

  toggleFavorite(): void {
    const m = this.movie();
    const isFav = this.movieService.toggleFavorite(m.id);
    this.toastService.show(
      isFav ? `Added “${m.title}” to favorites` : `Removed “${m.title}” from favorites`,
      isFav ? 'success' : 'info'
    );
  }
}
