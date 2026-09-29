import { Component, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { BoardStore } from './board-store';
import { HeaderComponent } from './header/header.component';
import { ToastComponent } from './toast/toast.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, HeaderComponent, ToastComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  readonly store = inject(BoardStore);
}
