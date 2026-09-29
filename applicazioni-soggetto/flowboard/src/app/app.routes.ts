import { Routes } from '@angular/router';
import { BoardComponent } from './board/board.component';
import { CardDetailComponent } from './card-detail/card-detail.component';
import { CardFormComponent } from './card-form/card-form.component';
import { StatsComponent } from './stats/stats.component';

export const routes: Routes = [
  { path: '', component: BoardComponent },
  { path: 'card/new', component: CardFormComponent },
  { path: 'card/:id/edit', component: CardFormComponent },
  { path: 'card/:id', component: CardDetailComponent },
  { path: 'stats', component: StatsComponent },
  { path: '**', redirectTo: '' }
];
