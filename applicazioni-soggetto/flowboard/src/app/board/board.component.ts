import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { BoardStore } from '../board-store';
import { ColumnComponent } from '../column/column.component';

@Component({
  selector: 'app-board',
  imports: [RouterLink, ColumnComponent],
  templateUrl: './board.component.html'
})
export class BoardComponent {
  readonly store = inject(BoardStore);

  onSearch(event: Event): void {
    this.store.setSearch((event.target as HTMLInputElement).value);
  }

  onPriorityFilter(event: Event): void {
    this.store.setPriorityFilter(
      (event.target as HTMLSelectElement).value as 'all' | 'low' | 'medium' | 'high'
    );
  }

  onAssigneeFilter(event: Event): void {
    this.store.setAssigneeFilter((event.target as HTMLSelectElement).value);
  }
}
