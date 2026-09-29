import { Routes } from '@angular/router';
import { RecipeDetailComponent } from './recipe-detail/recipe-detail.component';
import { RecipeFormComponent } from './recipe-form/recipe-form.component';
import { RecipeListComponent } from './recipe-list/recipe-list.component';
import { ShoppingListComponent } from './shopping-list/shopping-list.component';
import { StatsComponent } from './stats/stats.component';

export const routes: Routes = [
  { path: '', component: RecipeListComponent },
  { path: 'recipe/new', component: RecipeFormComponent },
  { path: 'recipe/:id/edit', component: RecipeFormComponent },
  { path: 'recipe/:id', component: RecipeDetailComponent },
  { path: 'shopping', component: ShoppingListComponent },
  { path: 'stats', component: StatsComponent },
  { path: '**', redirectTo: '' }
];
