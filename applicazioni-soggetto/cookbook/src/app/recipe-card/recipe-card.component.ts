import { Component, inject, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CookStore } from '../cook-store';
import { Recipe } from '../models';

@Component({
  selector: 'app-recipe-card',
  imports: [RouterLink],
  templateUrl: './recipe-card.component.html'
})
export class RecipeCardComponent {
  readonly store = inject(CookStore);
  readonly recipe = input.required<Recipe>();
}
