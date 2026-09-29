import { Card, Column } from './models';

/**
 * Deterministic seed: fixed ids, fixed titles, fixed dates as plain strings.
 * The app always boots from this exact state and "Reset board" restores it.
 * Never derive anything here from Date.now(), Math.random() or the like.
 */
export const SEED_COLUMNS: readonly Column[] = [
  { id: 'backlog', title: 'Backlog' },
  { id: 'inprogress', title: 'In Progress' },
  { id: 'review', title: 'Review' },
  { id: 'done', title: 'Done' }
];

export const SEED_CARDS: readonly Card[] = [
  {
    id: 1,
    title: 'Setup repository',
    description: 'Create the git repository, add the base project scaffold and CI stub.',
    priority: 'high',
    assignee: 'Anna',
    tags: ['infra', 'setup'],
    columnId: 'backlog'
  },
  {
    id: 2,
    title: 'Write project brief',
    description: 'One page summary of goals, scope and constraints for the team.',
    priority: 'medium',
    assignee: 'Luca',
    tags: ['docs'],
    columnId: 'backlog'
  },
  {
    id: 3,
    title: 'Design login',
    description: 'Wireframe and visual design for the sign-in screen (deadline 2026-03-15).',
    priority: 'high',
    assignee: 'Marco',
    tags: ['design', 'ux'],
    columnId: 'inprogress'
  },
  {
    id: 4,
    title: 'Implement search',
    description: 'Full text filtering over card titles with instant results.',
    priority: 'medium',
    assignee: 'Anna',
    tags: ['feature'],
    columnId: 'inprogress'
  },
  {
    id: 5,
    title: 'Prepare test data',
    description: 'Fixed fixture set for the e2e suite, no random values.',
    priority: 'low',
    assignee: 'Sara',
    tags: ['testing', 'data'],
    columnId: 'backlog'
  },
  {
    id: 6,
    title: 'Fix header layout',
    description: 'The toolbar wraps badly under 900px, align badges and nav.',
    priority: 'medium',
    assignee: 'Sara',
    tags: ['bug', 'css'],
    columnId: 'review'
  },
  {
    id: 7,
    title: 'Refactor store',
    description: 'Split derived selectors into computed signals and add reset action.',
    priority: 'high',
    assignee: 'Marco',
    tags: ['refactor'],
    columnId: 'review'
  },
  {
    id: 8,
    title: 'Setup CI pipeline',
    description: 'Build + e2e on every push, artifacts kept for 7 days.',
    priority: 'medium',
    assignee: 'Luca',
    tags: ['infra', 'ci'],
    columnId: 'done'
  },
  {
    id: 9,
    title: 'Write README',
    description: 'Document routes, test attributes and the e2e scenarios.',
    priority: 'low',
    assignee: 'Anna',
    tags: ['docs'],
    columnId: 'done'
  },
  {
    id: 10,
    title: 'Update dependencies',
    description: 'Bump Angular minor versions and re-run the full suite.',
    priority: 'low',
    assignee: 'Luca',
    tags: ['infra'],
    columnId: 'backlog'
  }
];
