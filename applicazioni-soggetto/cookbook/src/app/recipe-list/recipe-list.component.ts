import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CookStore } from '../cook-store';
import { CourseFilter, SortKey } from '../models';
import { RecipeCardComponent } from '../recipe-card/recipe-card.component';

@Component({
  selector: 'app-recipe-list',
  imports: [RouterLink, RecipeCardComponent],
  templateUrl: './recipe-list.component.html'
})
export class RecipeListComponent {
  readonly store = inject(CookStore);

  onSearch(event: Event): void {
    this.store.search.set((event.target as HTMLInputElement).value);
  }

  onCourse(event: Event): void {
    this.store.courseFilter.set((event.target as HTMLSelectElement).value as CourseFilter);
  }

  onVegetarian(event: Event): void {
    this.store.vegetarianOnly.set((event.target as HTMLInputElement).checked);
  }

  onSort(event: Event): void {
    this.store.sortKey.set((event.target as HTMLSelectElement).value as SortKey);
  }
}
