import { Component, inject, input } from '@angular/core';
import { CookStore } from '../cook-store';
import { Step } from '../models';

@Component({
  selector: 'app-step-item',
  templateUrl: './step-item.component.html'
})
export class StepItemComponent {
  readonly store = inject(CookStore);
  readonly step = input.required<Step>();
}
