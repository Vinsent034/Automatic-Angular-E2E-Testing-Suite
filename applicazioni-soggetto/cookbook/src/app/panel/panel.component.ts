import { Component, inject, input } from '@angular/core';
import { CookStore } from '../cook-store';

/**
 * Titled container with content projection. Pages wrap some of their sections
 * in <app-panel>: the projected elements then have a custom-component ancestor
 * inside the SAME template, which is what makes the fifth mutation role
 * (containing component) exist for them.
 */
@Component({
  selector: 'app-panel',
  templateUrl: './panel.component.html'
})
export class PanelComponent {
  readonly store = inject(CookStore);
  readonly heading = input('');
  readonly note = input('');
}
