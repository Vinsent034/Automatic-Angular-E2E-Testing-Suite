import { Component, inject, input } from '@angular/core';
import { BoardStore } from '../board-store';
import { CardComponent } from '../card/card.component';
import { ColumnId } from '../models';

/**
 * Renders the card list of one column. The column titles and counts live in
 * the board template (static / store-bound, h-safe); this component only owns
 * the @for over the filtered cards. `colId` is always given as a static
 * attribute (e.g. <app-column colId="backlog" />), never as a dynamic binding.
 */
@Component({
  selector: 'app-column',
  imports: [CardComponent],
  templateUrl: './column.component.html'
})
export class ColumnComponent {
  readonly store = inject(BoardStore);
  readonly colId = input.required<ColumnId>();
}
