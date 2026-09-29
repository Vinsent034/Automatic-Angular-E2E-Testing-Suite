export type Priority = 'low' | 'medium' | 'high';

export type ColumnId = 'backlog' | 'inprogress' | 'review' | 'done';

export interface Column {
  id: ColumnId;
  title: string;
}

export interface Card {
  id: number;
  title: string;
  description: string;
  priority: Priority;
  assignee: string;
  tags: string[];
  columnId: ColumnId;
}
