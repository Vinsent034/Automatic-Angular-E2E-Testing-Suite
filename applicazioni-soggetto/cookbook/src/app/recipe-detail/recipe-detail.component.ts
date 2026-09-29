import { Component, OnChanges, inject, input } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { CookStore } from '../cook-store';
import { IngredientRowComponent } from '../ingredient-row/ingredient-row.component';
import { PanelComponent } from '../panel/panel.component';
import { StepItemComponent } from '../step-item/step-item.component';

@Component({
  selector: 'app-recipe-detail',
  imports: [RouterLink, PanelComponent, IngredientRowComponent, StepItemComponent],
  templateUrl: './recipe-detail.component.html'
})
export class RecipeDetailComponent implements OnChanges {
  readonly store = inject(CookStore);
  private readonly router = inject(Router);

  /** Route param of /recipe/:id. */
  readonly id = input<string>();

  ngOnChanges(): void {
    this.store.select(Number(this.id()));
  }

  onServings(event: Event): void {
    this.store.setServings(Number((event.target as HTMLInputElement).value));
  }

  deleteAndGoBack(id: number): void {
    this.store.removeRecipe(id);
    this.router.navigateByUrl('/');
  }
}
