import { Injectable, computed, signal } from '@angular/core';
import { Card, Column, ColumnId, Priority } from './models';
import { SEED_CARDS, SEED_COLUMNS } from './seed';

export type PriorityFilter = Priority | 'all';
export type AssigneeFilter = string; // assignee name or 'all'

/**
 * Single global signal store. Every component injects it as `store`, so any
 * template binding written as `store.something()` compiles in ANY component
 * template — this is what keeps store-bound elements safe for the "move an
 * element between two component templates" mutation operator (h).
 */
@Injectable({ providedIn: 'root' })
export class BoardStore {
  /** The four columns are fixed; they are never mutated. */
  readonly columns = signal<readonly Column[]>(SEED_COLUMNS);

  private readonly cardsSig = signal<readonly Card[]>(SEED_CARDS);
  readonly cards = this.cardsSig.asReadonly();

  /** Id of the card shown on the detail page, or null. */
  readonly selectedId = signal<number | null>(null);

  /** Toolbar filters. */
  readonly searchText = signal<string>('');
  readonly priorityFilter = signal<PriorityFilter>('all');
  readonly assigneeFilter = signal<AssigneeFilter>('all');

  /** Last action message, shown by the toast bar (no timeouts, deterministic). */
  readonly toast = signal<string>('Board loaded from seed');

  // ------------------------------------------------------------------
  // Derived state (computed)
  // ------------------------------------------------------------------

  readonly totalCards = computed(() => this.cards().length);

  readonly selectedCard = computed<Card | null>(
    () => this.cards().find(c => c.id === this.selectedId()) ?? null
  );

  /** Cards that pass search text + priority + assignee filters. */
  readonly filteredCards = computed(() => {
    const text = this.searchText().trim().toLowerCase();
    const priority = this.priorityFilter();
    const assignee = this.assigneeFilter();
    return this.cards().filter(card => {
      const matchesText = text === '' || card.title.toLowerCase().includes(text);
      const matchesPriority = priority === 'all' || card.priority === priority;
      const matchesAssignee = assignee === 'all' || card.assignee === assignee;
      return matchesText && matchesPriority && matchesAssignee;
    });
  });

  readonly filteredCount = computed(() => this.filteredCards().length);

  /** Per-column counts over ALL cards (not filtered) — used by column headers. */
  readonly backlogCount = computed(() => this.countIn('backlog'));
  readonly inProgressCount = computed(() => this.countIn('inprogress'));
  readonly reviewCount = computed(() => this.countIn('review'));
  readonly doneCount = computed(() => this.countIn('done'));

  /** Per-priority counts over ALL cards — used by the stats page. */
  readonly highCount = computed(() => this.cards().filter(c => c.priority === 'high').length);
  readonly mediumCount = computed(() => this.cards().filter(c => c.priority === 'medium').length);
  readonly lowCount = computed(() => this.cards().filter(c => c.priority === 'low').length);

  /** Distinct assignees, sorted — feeds the assignee filter dropdown. */
  readonly assignees = computed(() =>
    [...new Set(this.cards().map(c => c.assignee))].sort()
  );

  /** Filtered cards of one column, in seed/insertion order (deterministic). */
  readonly cardsByColumn = computed(() => {
    const filtered = this.filteredCards();
    return (colId: ColumnId) => filtered.filter(c => c.columnId === colId);
  });

  // ------------------------------------------------------------------
  // Actions
  // ------------------------------------------------------------------

  addCard(data: Omit<Card, 'id'>): number {
    const nextId = this.cards().reduce((max, c) => Math.max(max, c.id), 0) + 1;
    const card: Card = { ...data, id: nextId };
    this.cardsSig.update(cards => [...cards, card]);
    this.toast.set(`Card #${nextId} added`);
    return nextId;
  }

  updateCard(id: number, data: Omit<Card, 'id'>): void {
    this.cardsSig.update(cards =>
      cards.map(c => (c.id === id ? { ...data, id } : c))
    );
    this.toast.set(`Card #${id} updated`);
  }

  removeCard(id: number): void {
    this.cardsSig.update(cards => cards.filter(c => c.id !== id));
    if (this.selectedId() === id) {
      this.selectedId.set(null);
    }
    this.toast.set(`Card #${id} deleted`);
  }

  moveCard(id: number, colId: ColumnId): void {
    this.cardsSig.update(cards =>
      cards.map(c => (c.id === id ? { ...c, columnId: colId } : c))
    );
    const column = this.columns().find(c => c.id === colId);
    this.toast.set(`Card #${id} moved to ${column?.title ?? colId}`);
  }

  select(id: number | null): void {
    this.selectedId.set(id);
  }

  setSearch(text: string): void {
    this.searchText.set(text);
  }

  setPriorityFilter(priority: PriorityFilter): void {
    this.priorityFilter.set(priority);
  }

  setAssigneeFilter(assignee: AssigneeFilter): void {
    this.assigneeFilter.set(assignee);
  }

  /** Restore the exact initial state (seed + cleared filters/selection). */
  reset(): void {
    this.cardsSig.set(SEED_CARDS);
    this.selectedId.set(null);
    this.searchText.set('');
    this.priorityFilter.set('all');
    this.assigneeFilter.set('all');
    this.toast.set('Board reset to seed');
  }

  private countIn(colId: ColumnId): number {
    return this.cards().filter(c => c.columnId === colId).length;
  }
}
