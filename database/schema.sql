-- Esquema SQLite utilizado por PermissionHistoryDb.
-- Android crea permission_history.db en tiempo de ejecución.

CREATE TABLE IF NOT EXISTS history (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    created_at TEXT NOT NULL,
    event TEXT NOT NULL
);
