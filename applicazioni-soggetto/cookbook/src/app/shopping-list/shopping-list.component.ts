import { Component, inject } from '@angular/core';
import { CookStore } from '../cook-store';

@Component({
  selector: 'app-shopping-list',
  templateUrl: './shopping-list.component.html'
})
export class ShoppingListComponent {
  readonly store = inject(CookStore);
}
