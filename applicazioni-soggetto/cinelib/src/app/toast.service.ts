import { Injectable, signal } from '@angular/core';

export type ToastKind = 'success' | 'info' | 'danger';

export interface Toast {
  id: number;
  message: string;
  kind: ToastKind;
}

@Injectable({ providedIn: 'root' })
export class ToastService {
  private nextId = 1;
  readonly toasts = signal<Toast[]>([]);

  show(message: string, kind: ToastKind = 'info'): void {
    const toast: Toast = { id: this.nextId++, message, kind };
    this.toasts.update((arr) => [...arr, toast]);
    setTimeout(() => this.dismiss(toast.id), 3200);
  }

  success(message: string): void {
    this.show(message, 'success');
  }

  danger(message: string): void {
    this.show(message, 'danger');
  }

  dismiss(id: number): void {
    this.toasts.update((arr) => arr.filter((t) => t.id !== id));
  }
}
