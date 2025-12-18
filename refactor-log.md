# Bearbeitungslog

## Phase 1
Derzeit hängt die App an einer einzigen statischen Datenbankverbindung ab.  Das könnte bei UI + Background Threads zu Fehlern und Abstürzen führen; Sobald UI und Background-Threads parallel arbeiten oder das Netz wackelt, fliegt alles raus und es entstehen Leaks.
Fix: Wir ersetzen die Single-Connection durch ein Connection Pool und räumen die bereits vorhandenen Repositoryklassen auf.

Gleichzeitig optimieren wir die SQL-Anfragen, und beheben auch andere strukturelle, von der Datenbank abhängige Probleme, machen den Code eleganter und vereinfachen/entfernen einige Funktionen.

Durch immer neue Verbindungen wurde die Threadsicherheit in den Swing-Workern jetzt sichergestellt.

Hat bereits dramatisch Performance verbessert (Alle Tests laufen nun nur noch in 10 Sekunden ab, statt in Minuten wie vorhin).

Ein Haufen SQL-Befehle wurden optimiert, verändert und instabile SQL Methoden durch stabilere ersetzt.
Die Tests wurden angepasst und die Soundex-Implementierung sicher gemacht.
Allgemein mehrere Verbesserungen in den SQL-basierten Methoden.

## Phase 2

Die App hat sich beim Laden der Liste totgeladen, weil sie für jedes einzelne Item eine neue Anfrage an die Datenbank geschickt hat (das klassische N+1 Problem). Wenn du 50 Sachen auf der Liste hattest, gab es 51 Anrufe bei der DB. Das ist extrem dumm und langsam.

Fix: Wir haben das ShoppingListItem Record. Das ist ein Record, der Entry und Product direkt zusammenhält. Dank SQL-JOIN holen wir jetzt alles mit einer einzigen Anfrage aus der Datenbank. Die UI ist jetzt noch schneller.

Außerdem gab es ein großes Risiko bei den Kategorien. Die wurden bisher als Zahlen (Ordinals) gespeichert. Wenn man im Code die Reihenfolge der Enums geändert hätte, wäre in der Datenbank alles durcheinandergeflogen (aus Milch wird plötzlich Schnaps).
Fix: Wir speichern Enums jetzt als Strings ("DRINKS" statt 5). Das ist sicher, stabil und ich habe einen Migrator drüberlaufen lassen, der die alten Daten geradegezogen hat.

