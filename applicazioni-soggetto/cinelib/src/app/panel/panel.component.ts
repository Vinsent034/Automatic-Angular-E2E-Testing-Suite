import { Component, input } from '@angular/core';

/**
 * Titled container with content projection. Some page sections are wrapped in
 * <app-panel>: the projected elements then have a custom-component ancestor inside the
 * SAME template, which is what makes the fifth mutation role (containing component)
 * exist for them. Same design as CookBook's and FlowBoard's panel.
 */
@Component({
  selector: 'app-panel',
  templateUrl: './panel.component.html'
})
export class PanelComponent {
  readonly heading = input('');
  readonly note = input('');
}
