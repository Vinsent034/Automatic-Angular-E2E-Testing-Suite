import { Component, inject } from '@angular/core';
import { CookStore } from '../cook-store';

/** Shows the last action message; it stays until replaced or dismissed (no timers). */
@Component({
  selector: 'app-toast',
  templateUrl: './toast.component.html'
})
export class ToastComponent {
  readonly store = inject(CookStore);
}
