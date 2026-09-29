import { Component, computed, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MovieService } from '../movie.service';
import { PanelComponent } from '../panel/panel.component';

interface GenreStat {
  genre: string;
  count: number;
  share: number;
}

@Component({
  selector: 'app-stats',
  imports: [RouterLink, PanelComponent],
  templateUrl: './stats.component.html'
})
export class StatsComponent {
  private readonly movieService = inject(MovieService);

  readonly total = computed(() => this.movieService.movies().length);

  readonly favoriteCount = computed(() => this.movieService.favorites().length);

  readonly reviewCount = computed(() =>
    this.movieService.movies().reduce((sum, m) => sum + m.reviews.length, 0)
  );

  readonly averageRating = computed(() => {
    const movies = this.movieService.movies();
    if (movies.length === 0) return 0;
    const sum = movies.reduce((acc, m) => acc + m.rating, 0);
    return Math.round((sum / movies.length) * 10) / 10;
  });

  readonly averageRuntime = computed(() => {
    const movies = this.movieService.movies();
    if (movies.length === 0) return 0;
    const sum = movies.reduce((acc, m) => acc + m.runtime, 0);
    return Math.round(sum / movies.length);
  });

  readonly genreStats = computed<GenreStat[]>(() => {
    const movies = this.movieService.movies();
    const counts = new Map<string, number>();
    for (const m of movies) {
      counts.set(m.genre, (counts.get(m.genre) ?? 0) + 1);
    }
    const max = Math.max(1, ...counts.values());
    return Array.from(counts.entries())
      .map(([genre, count]) => ({ genre, count, share: Math.round((count / max) * 100) }))
      .sort((a, b) => b.count - a.count || a.genre.localeCompare(b.genre));
  });

  readonly topRated = computed(() =>
    [...this.movieService.movies()].sort((a, b) => b.rating - a.rating).slice(0, 3)
  );
}
