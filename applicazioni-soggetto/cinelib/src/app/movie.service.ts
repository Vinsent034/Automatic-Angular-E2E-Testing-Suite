import { Injectable, computed, signal } from '@angular/core';
import { Movie, Review } from './movie.model';

const SEED: Movie[] = [
  {
    id: 1, title: 'The Grand Trip', year: 2021, genre: 'Adventure', rating: 4.5, runtime: 118,
    director: 'Lena Park', synopsis: 'A botanist and a pilot cross a continent to deliver a rare seed before the first frost.',
    favorite: true,
    cast: [
      { name: 'Mara Quinn', role: 'Dr. Aria Vale' },
      { name: 'Tom Reyes', role: 'Captain Hollis' },
      { name: 'Iris Cole', role: 'Sela' }
    ],
    reviews: [
      { id: 1, author: 'Greta', rating: 5, comment: 'Breathtaking landscapes and a warm story.', date: '2023-03-12' },
      { id: 2, author: 'Pavel', rating: 4, comment: 'Slow at first but worth it.', date: '2023-04-02' }
    ]
  },
  {
    id: 2, title: 'Neon Nights', year: 2019, genre: 'Sci-Fi', rating: 4.0, runtime: 102,
    director: 'Hugo Marsh', synopsis: 'In a rain-soaked city, a courier discovers the package she carries can rewrite a single memory.',
    favorite: false,
    cast: [
      { name: 'Dax Oren', role: 'Kit' },
      { name: 'Vera Lin', role: 'The Archivist' }
    ],
    reviews: [
      { id: 1, author: 'Noor', rating: 4, comment: 'Gorgeous visuals, thin plot.', date: '2022-11-20' }
    ]
  },
  {
    id: 3, title: 'Quiet Harbor', year: 2022, genre: 'Drama', rating: 4.8, runtime: 127,
    director: 'Sofia Bianchi', synopsis: 'Three siblings return to their fishing town to decide the fate of a boat their father built by hand.',
    favorite: true,
    cast: [
      { name: 'Nora Hale', role: 'Edie' },
      { name: 'Sam Whitfield', role: 'Joel' },
      { name: 'Pia Romano', role: 'Catherine' }
    ],
    reviews: [
      { id: 1, author: 'Dario', rating: 5, comment: 'Quietly devastating. A masterpiece.', date: '2023-01-08' },
      { id: 2, author: 'Lena', rating: 5, comment: 'The performances carry every scene.', date: '2023-02-14' },
      { id: 3, author: 'Mick', rating: 4, comment: 'Beautiful, if a little long.', date: '2023-05-30' }
    ]
  },
  {
    id: 4, title: 'Iron Valley', year: 2018, genre: 'Action', rating: 3.9, runtime: 109,
    director: 'Reece Dunn', synopsis: 'A retired engineer is pulled back to a mining town when the dam protecting it starts to fail.',
    favorite: false,
    cast: [
      { name: 'Cole Banner', role: 'Frank' },
      { name: 'Aya Mori', role: 'Mayor Sun' }
    ],
    reviews: []
  },
  {
    id: 5, title: 'Paper Moons', year: 2020, genre: 'Romance', rating: 4.2, runtime: 96,
    director: 'Marie Dubois', synopsis: 'Two pen-pals who have written for a decade finally agree to meet on the night of a lunar eclipse.',
    favorite: false,
    cast: [
      { name: 'Elsa Vance', role: 'June' },
      { name: 'Omar Reed', role: 'Theo' }
    ],
    reviews: [
      { id: 1, author: 'Yuki', rating: 4, comment: 'Tender and bittersweet.', date: '2022-07-19' }
    ]
  },
  {
    id: 6, title: 'Deep Signal', year: 2023, genre: 'Thriller', rating: 4.6, runtime: 134,
    director: 'Karl Stein', synopsis: 'A marine technician on a remote rig intercepts a transmission that should not exist.',
    favorite: true,
    cast: [
      { name: 'Wren Adler', role: 'Dr. Pike' },
      { name: 'Hana Goto', role: 'Lt. Mori' },
      { name: 'Bram Osei', role: 'Calloway' }
    ],
    reviews: [
      { id: 1, author: 'Tomas', rating: 5, comment: 'Held my breath for two hours.', date: '2023-09-01' }
    ]
  },
  {
    id: 7, title: 'Glass Garden', year: 2017, genre: 'Drama', rating: 4.1, runtime: 113,
    director: 'Sofia Bianchi', synopsis: 'A reclusive sculptor agrees to teach one last student before closing her studio forever.',
    favorite: false,
    cast: [
      { name: 'Pia Romano', role: 'Margot' },
      { name: 'Leo Frank', role: 'Daniel' }
    ],
    reviews: []
  },
  {
    id: 8, title: 'North Star', year: 2024, genre: 'Adventure', rating: 4.3, runtime: 105,
    director: 'Lena Park', synopsis: 'A teenager and her grandfather sail north chasing an aurora he promised her mother they would see.',
    favorite: false,
    cast: [
      { name: 'Mina Holt', role: 'Ada' },
      { name: 'George Vey', role: 'Elias' }
    ],
    reviews: [
      { id: 1, author: 'Ada', rating: 5, comment: 'Made me call my grandfather.', date: '2024-02-28' },
      { id: 2, author: 'Ben', rating: 3, comment: 'Predictable but charming.', date: '2024-03-15' }
    ]
  }
];

function clone(movies: Movie[]): Movie[] {
  return movies.map((m) => ({
    ...m,
    cast: m.cast.map((c) => ({ ...c })),
    reviews: m.reviews.map((r) => ({ ...r }))
  }));
}

@Injectable({ providedIn: 'root' })
export class MovieService {
  // Always start from the original sample data. Changes live only in memory for
  // the current session, so reloading the page resets the catalog to its seed —
  // this keeps every test run isolated, with no pre/post conditions to manage.
  private readonly _movies = signal<Movie[]>(clone(SEED));
  private nextId = SEED.length + 1;

  readonly movies = this._movies.asReadonly();

  /** Movies the user has marked as favorite. */
  readonly favorites = computed(() => this._movies().filter((m) => m.favorite));

  getById(id: number): Movie | undefined {
    return this._movies().find((m) => m.id === id);
  }

  add(data: Omit<Movie, 'id' | 'favorite' | 'reviews'>): Movie {
    const movie: Movie = { ...data, id: this.nextId++, favorite: false, reviews: [] };
    this._movies.update((arr) => [...arr, movie]);
    return movie;
  }

  update(id: number, data: Omit<Movie, 'id' | 'favorite' | 'reviews'>): void {
    this._movies.update((arr) =>
      arr.map((m) => (m.id === id ? { ...m, ...data, id } : m))
    );
  }

  remove(id: number): void {
    this._movies.update((arr) => arr.filter((m) => m.id !== id));
  }

  toggleFavorite(id: number): boolean {
    let result = false;
    this._movies.update((arr) =>
      arr.map((m) => {
        if (m.id !== id) return m;
        result = !m.favorite;
        return { ...m, favorite: result };
      })
    );
    return result;
  }

  addReview(movieId: number, data: Omit<Review, 'id' | 'date'>): void {
    this._movies.update((arr) =>
      arr.map((m) => {
        if (m.id !== movieId) return m;
        const nextId = m.reviews.reduce((max, r) => Math.max(max, r.id), 0) + 1;
        const review: Review = {
          ...data,
          id: nextId,
          date: new Date().toISOString().slice(0, 10)
        };
        return { ...m, reviews: [...m.reviews, review] };
      })
    );
  }

  removeReview(movieId: number, reviewId: number): void {
    this._movies.update((arr) =>
      arr.map((m) =>
        m.id === movieId
          ? { ...m, reviews: m.reviews.filter((r) => r.id !== reviewId) }
          : m
      )
    );
  }

  /** Restore the original sample catalog, discarding any in-session changes. */
  reset(): void {
    this.nextId = SEED.length + 1;
    this._movies.set(clone(SEED));
  }
}
