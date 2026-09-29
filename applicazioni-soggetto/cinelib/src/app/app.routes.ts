import { Routes } from '@angular/router';
import { CatalogComponent } from './catalog/catalog.component';
import { MovieDetailComponent } from './movie-detail/movie-detail.component';
import { MovieFormComponent } from './movie-form/movie-form.component';
import { StatsComponent } from './stats/stats.component';

export const routes: Routes = [
  { path: '', component: CatalogComponent },
  { path: 'stats', component: StatsComponent },
  { path: 'movie/new', component: MovieFormComponent },
  { path: 'movie/:id/edit', component: MovieFormComponent },
  { path: 'movie/:id', component: MovieDetailComponent },
  { path: '**', redirectTo: '' }
];
