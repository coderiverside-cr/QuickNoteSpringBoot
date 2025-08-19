SET search_path TO public;

INSERT INTO notes (id, title, content, type, creation_date, is_pinned, is_archived, color) VALUES (1, 'Grocery List', 'Milk, bread, eggs, cheese', 'text', '2025-08-12T14:30:00Z', true, false, 'yellow');
INSERT INTO notes (id, title, content, type, creation_date, is_pinned, is_archived, color) VALUES (2, 'Buy groceries', 'Milk, bread, eggs', 'text', '2025-08-16T10:00:00Z', false, false, 'white');
INSERT INTO notes (id, title, content, type, creation_date, is_pinned, is_archived, color) VALUES (3, 'Gym workout', '30 minutes cardio, 20 minutes weights', 'text', '2025-08-15T08:00:00Z', true, false, 'yellow');
INSERT INTO notes (id, title, content, type, creation_date, is_pinned, is_archived, color) VALUES (4, 'Meeting notes', 'Discuss project milestones and deadlines', 'text', '2025-08-14T09:00:00Z', false, false, 'white');
INSERT INTO notes (id, title, content, type, creation_date, is_pinned, is_archived, color) VALUES (5, 'Shopping list', 'To buy: apples, bananas, oranges', 'text', '2025-08-13T11:00:00Z', false, false, 'white');
INSERT INTO notes (id, title, content, type, creation_date, is_pinned, is_archived, color) VALUES (6, 'Project ideas', 'Brainstorming session notes', 'text', '2025-08-17T12:00:00Z', false, false, 'white');
INSERT INTO notes (id, title, content, type, creation_date, is_pinned, is_archived, color) VALUES (7, 'Book recommendations', '1. The Great Gatsby. To Kill a Mockingbird. 1984', 'text', '2025-08-18T09:00:00Z', false, false, 'white');

SELECT setval('notes_id_seq', (SELECT MAX(id) FROM notes));