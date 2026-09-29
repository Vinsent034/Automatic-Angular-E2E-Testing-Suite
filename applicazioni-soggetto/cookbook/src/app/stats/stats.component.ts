import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CookStore } from '../cook-store';
import { PanelComponent } from '../panel/panel.component';

@Component({
  selector: 'app-stats',
  imports: [RouterLink, PanelComponent],
  templateUrl: './stats.component.html'
})
export class StatsComponent {
  readonly store = inject(CookStore);
}
