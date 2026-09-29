import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { BoardStore } from '../board-store';
import { PanelComponent } from '../panel/panel.component';

@Component({
  selector: 'app-stats',
  imports: [RouterLink, PanelComponent],
  templateUrl: './stats.component.html'
})
export class StatsComponent {
  readonly store = inject(BoardStore);
}
