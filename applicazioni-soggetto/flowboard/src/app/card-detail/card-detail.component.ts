import { Component, effect, inject, input } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { BoardStore } from '../board-store';
import { PanelComponent } from '../panel/panel.component';

@Component({
  selector: 'app-card-detail',
  imports: [RouterLink, PanelComponent],
  templateUrl: './card-detail.component.html'
})
export class CardDetailComponent {
  readonly store = inject(BoardStore);
  private readonly router = inject(Router);

  /** Route param, bound via withComponentInputBinding(). */
  readonly id = input.required<string>();

  constructor() {
    effect(() => {
      this.store.select(Number(this.id()));
    });
  }

  deleteAndGoBack(): void {
    const card = this.store.selectedCard();
    if (card !== null) {
      this.store.removeCard(card.id);
      this.router.navigate(['/']);
    }
  }
}
