import { Component, inject, input } from '@angular/core';
import { CookStore } from '../cook-store';
import { Ingredient } from '../models';

@Component({
  selector: 'app-ingredient-row',
  templateUrl: './ingredient-row.component.html'
})
export class IngredientRowComponent {
  readonly store = inject(CookStore);
  readonly ingredient = input.required<Ingredient>();
}
