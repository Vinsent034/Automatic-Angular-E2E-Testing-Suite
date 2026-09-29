import { Component, OnInit, inject, input, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { BoardStore } from '../board-store';
import { PanelComponent } from '../panel/panel.component';
import { ColumnId, Priority } from '../models';

@Component({
  selector: 'app-card-form',
  imports: [FormsModule, RouterLink, PanelComponent],
  templateUrl: './card-form.component.html'
})
export class CardFormComponent implements OnInit {
  readonly store = inject(BoardStore);
  private readonly router = inject(Router);

  /** Route param on /card/:id/edit; undefined on /card/new. */
  readonly id = input<string>();

  readonly editing = signal(false);

  title = '';
  description = '';
  priority: Priority = 'medium';
  assignee = '';
  columnId: ColumnId = 'backlog';
  tagsText = '';

  ngOnInit(): void {
    const idParam = this.id();
    if (idParam !== undefined) {
      const card = this.store.cards().find(c => c.id === Number(idParam));
      if (card !== undefined) {
        this.editing.set(true);
        this.title = card.title;
        this.description = card.description;
        this.priority = card.priority;
        this.assignee = card.assignee;
        this.columnId = card.columnId;
        this.tagsText = card.tags.join(', ');
      }
    }
  }

  submit(): void {
    if (this.title.trim() === '') {
      return;
    }
    const data = {
      title: this.title.trim(),
      description: this.description.trim(),
      priority: this.priority,
      assignee: this.assignee.trim(),
      tags: this.tagsText
        .split(',')
        .map(t => t.trim())
        .filter(t => t !== ''),
      columnId: this.columnId
    };
    if (this.editing()) {
      this.store.updateCard(Number(this.id()), data);
    } else {
      this.store.addCard(data);
    }
    this.router.navigate(['/']);
  }
}
