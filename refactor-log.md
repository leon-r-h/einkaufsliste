# Phase 1
Derzeit hängt die App an einer einzigen statischen Datenbankverbindung ab.  Das könnte bei UI + Background Threads zu Fehlern und Abstürzen führen; Sobald UI und Background-Threads parallel arbeiten oder das Netz wackelt, fliegt alles raus und es entstehen Leaks.
Fix: Wir ersetzen die Single-Connection durch ein Connection Pool und räumen die bereits vorhandenen Repositoryklassen auf.

Gleichzeitig optimieren wir die SQL-Anfragen, und beheben auch andere strukturelle, von der Datenbank abhängige Probleme, machen den Code eleganter und vereinfachen/entfernen einige Funktionen.

Hat bereits dramatisch Performance verbessert (Alle Tests laufen nun nur noch in 10 Sekunden ab, statt in Minuten wie vorhin).

Ein Haufen SQL-Befehle wurden optimiert, verändert und instabile SQL Methoden durch stabilere ersetzt.
Die Tests wurden angepasst und die Soundex-Implementierung sicher gemacht.
Allgemein mehrere Verbesserungen in den SQL-basierten Methoden.
