import { Component, inject, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { BoardStore } from '../board-store';
import { Card, ColumnId } from '../models';

@Component({
  selector: 'app-card',
  imports: [RouterLink],
  templateUrl: './card.component.html'
})
export class CardComponent {
  readonly store = inject(BoardStore);
  readonly card = input.required<Card>();

  onMove(event: Event): void {
    const value = (event.target as HTMLSelectElement).value as ColumnId | '';
    if (value !== '' && value !== this.card().columnId) {
      this.store.moveCard(this.card().id, value);
    }
  }
}
