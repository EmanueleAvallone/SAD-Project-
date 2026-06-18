# Music Player
Un semplice music player desktop scritto in Java (JavaFX) per la gestione di tracce, playlist e modalità di riproduzione.

---

### Caratteristiche principali
- Gestione di tracce e playlist
- Interfaccia grafica basata su FXML e CSS
- Persistenza dello stato in `data/music-player-state.json`

---

### Funzionalità implementate (panoramica)
- Riproduzione: Play / Pause / Resume / Stop
- Skip: Next / Previous
- Modalità di riproduzione: Sequenziale, Shuffle , Loop
- Supporto di riproduzione reale: se il percorso audio (.mp3) è valido viene usato JavaFX MediaPlayer
- Libreria tracce:
  - Aggiunta, modifica e validazione dei metadati (titolo, autore, durata, genere, anno, percorso file .mp3)
  - Soft delete (rimozione temporanea) con snackbar Undo e conferma che sposta la traccia nel Cestino
  - Cestino (Trash): ripristino singolo e svuota permanente
  - Filtri per Tag (FAV, EXPLICIT, NEW) e ricerca testuale case-insensitive su titolo/autore
  - Sezione "Most Played" che mostra i brani più ascoltati e permette la riproduzione della classifica
- Playlist:
  - Creazione, rinomina, eliminazione (soft delete con undo)
  - Aggiunta / rimozione tracce, spostamento ordine (move up/down)
  - Generazione smart playlist per Tag, Genere e Anno
- Ordinamenti personalizzati nelle tabelle (titolo, autore, durata, anno, numero di riproduzioni)
- Persistenza: esportazione/importazione JSON dello stato completo (tracks, playlists, trash)

---

### Quick start
Prerequisiti: Java 11+ e Maven (o un IDE come IntelliJ IDEA).

1. Clona il repository

```powershell
git clone <url-del-repo>
cd SAD-Project-
```

2. Compilare e testare

```powershell
mvn clean package
mvn test
```

3. Eseguire

- In IDE: lancia la classe `com.musicplayer.MainApp`.

### Percorsi utili nel progetto
- `src/main/java` - codice sorgente Java
- `src/main/resources/musicplayer/view` - file FXML e risorse GUI
- `data/music-player-state.json` - file di stato usato per import/export

---