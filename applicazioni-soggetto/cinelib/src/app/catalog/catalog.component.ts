import { Component, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MovieService } from '../movie.service';
import { ToastService } from '../toast.service';
import { MovieCardComponent } from '../movie-card/movie-card.component';

type SortKey = 'title' | 'year' | 'rating';
type ViewMode = 'grid' | 'list';

@Component({
  selector: 'app-catalog',
  imports: [RouterLink, MovieCardComponent],
  templateUrl: './catalog.component.html'
})
export class CatalogComponent {
  private readonly movieService = inject(MovieService);
  private readonly toastService = inject(ToastService);

  readonly query = signal('');
  readonly genre = signal('');
  readonly sort = signal<SortKey>('title');
  readonly view = signal<ViewMode>('grid');
  readonly favoritesOnly = signal(false);
  readonly resetOpen = signal(false);

  /** Genres actually present in the catalog, for the filter dropdown. */
  readonly genres = computed(() => {
    const set = new Set(this.movieService.movies().map((m) => m.genre));
    return Array.from(set).sort();
  });

  readonly favoriteCount = computed(() => this.movieService.favorites().length);

  readonly movies = computed(() => {
    const q = this.query().trim().toLowerCase();
    const genre = this.genre();
    const sort = this.sort();

    let result = this.movieService.movies();

    if (this.favoritesOnly()) {
      result = result.filter((m) => m.favorite);
    }
    if (q) {
      result = result.filter(
        (m) => m.title.toLowerCase().includes(q) || m.genre.toLowerCase().includes(q)
      );
    }
    if (genre) {
      result = result.filter((m) => m.genre === genre);
    }

    return [...result].sort((a, b) => {
      switch (sort) {
        case 'rating':
          return b.rating - a.rating;
        case 'year':
          return b.year - a.year;
        default:
          return a.title.localeCompare(b.title);
      }
    });
  });

  onSearch(value: string): void {
    this.query.set(value);
  }

  onGenre(value: string): void {
    this.genre.set(value);
  }

  onSort(value: string): void {
    this.sort.set(value as SortKey);
  }

  setView(mode: ViewMode): void {
    this.view.set(mode);
  }

  toggleFavoritesOnly(): void {
    this.favoritesOnly.update((v) => !v);
  }

  openReset(): void {
    this.resetOpen.set(true);
  }

  cancelReset(): void {
    this.resetOpen.set(false);
  }

  confirmReset(): void {
    this.movieService.reset();
    this.resetOpen.set(false);
    this.favoritesOnly.set(false);
    this.query.set('');
    this.genre.set('');
    this.toastService.success('Catalog restored to the sample data');
  }
}
