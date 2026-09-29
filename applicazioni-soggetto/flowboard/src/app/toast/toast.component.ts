import { Component, inject } from '@angular/core';
import { BoardStore } from '../board-store';

@Component({
  selector: 'app-toast',
  templateUrl: './toast.component.html'
})
export class ToastComponent {
  readonly store = inject(BoardStore);
}
