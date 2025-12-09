SET search_path TO public;

INSERT INTO notes (id, title, content, type, creation_date, is_pinned, is_archived, color, owner) VALUES (1, 'Grocery List', 'Milk, bread, eggs, cheese', 'text', '2025-08-12T14:30:00Z', true, false, '#FFFF00', 'sophia');
INSERT INTO notes (id, title, content, type, creation_date, is_pinned, is_archived, color, owner) VALUES (2, 'Buy groceries', 'Milk, bread, eggs', 'text', '2025-08-16T10:00:00Z', false, false, '#FFFFFF', 'sophia');
INSERT INTO notes (id, title, content, type, creation_date, is_pinned, is_archived, color, owner) VALUES (3, 'Gym workout', '30 minutes cardio, 20 minutes weights', 'text', '2025-08-15T08:00:00Z', true, false, '#FFFF00', 'sophia');
INSERT INTO notes (id, title, content, type, creation_date, is_pinned, is_archived, color, owner) VALUES (4, 'Meeting notes', 'Discuss project milestones and deadlines', 'text', '2025-08-14T09:00:00Z', false, false, '#FFFFFF', 'gabriel');
INSERT INTO notes (id, title, content, type, creation_date, is_pinned, is_archived, color, owner) VALUES (5, 'Shopping list', 'To buy: apples, bananas, oranges', 'text', '2025-08-13T11:00:00Z', false, false, '#FFFFFF', 'gabriel');
INSERT INTO notes (id, title, content, type, creation_date, is_pinned, is_archived, color, owner) VALUES (6, 'Project ideas', 'Brainstorming session notes', 'text', '2025-08-17T12:00:00Z', false, false, '#FFFFFF', 'fabricio');
INSERT INTO notes (id, title, content, type, creation_date, is_pinned, is_archived, color, owner) VALUES (7, 'Book recommendations', '1. The Great Gatsby. To Kill a Mockingbird. 1984', 'text', '2025-08-18T09:00:00Z', false, false, '#FFFFFF', 'fabricio');
INSERT INTO notes (id, title, content, type, creation_date, is_pinned, is_archived, color, owner) VALUES (8, 'Test note for settings', 'This note is for settings creation test', 'text', '2025-08-19T10:00:00Z', false, false, '#FFFFFF', 'sophia');
INSERT INTO note_settings (note_id, is_locked, priority, enable_sharing) VALUES (1, false, 'Low', true);
INSERT INTO note_settings (note_id, is_locked, priority, enable_sharing) VALUES (2, false, 'Low', true);
INSERT INTO note_settings (note_id, is_locked, priority, enable_sharing) VALUES (3, false, 'Low', true);

SELECT setval('notes_id_seq', (SELECT MAX(id) FROM notes));

INSERT INTO labels (id, name, owner) VALUES (1, 'Work', 'sophia');
INSERT INTO labels (id, name, owner) VALUES (2, 'Personal', 'sophia');
INSERT INTO labels (id, name, owner) VALUES (3, 'Urgent', 'gabriel');
INSERT INTO labels (id, name, owner) VALUES (4, 'Ideas', 'fabricio');

SELECT setval('labels_id_seq', (SELECT MAX(id) FROM labels));


INSERT INTO reminders (id, remind_at, note_id, owner) VALUES (1, '2025-11-01T09:00:00', 1, 'sophia');
INSERT INTO reminders (id, remind_at, note_id, owner) VALUES (2, '2025-11-02T10:00:00', 2, 'sophia');
INSERT INTO reminders (id, remind_at, note_id, owner) VALUES (3, '2025-11-03T11:00:00', 3, 'sophia');
INSERT INTO reminders (id, remind_at, note_id, owner) VALUES (4, '2025-11-04T12:00:00', 4, 'gabriel');
INSERT INTO reminders (id, remind_at, note_id, owner) VALUES (5, '2025-11-05T13:00:00', 5, 'gabriel');
INSERT INTO reminders (id, remind_at, note_id, owner) VALUES (6, '2025-11-06T14:00:00', 6, 'fabricio');
INSERT INTO reminders (id, remind_at, note_id, owner) VALUES (7, '2025-11-07T15:00:00', 7, 'fabricio');

SELECT setval('reminders_id_seq', (SELECT MAX(id) FROM reminders));