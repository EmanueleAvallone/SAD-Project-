package com.musicplayer.controller;

import com.musicplayer.model.Track;
import com.musicplayer.model.engine.MediaPlayerEngine;
import com.musicplayer.model.engine.PlayerObserver;
import com.musicplayer.service.PlaybackService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.util.Duration;

import java.util.List;

/**
 * Controller responsabile della gestione dei comandi del player.
 *
 * Gestisce Play, Pause, Skip e aggiorna la sezione Simulated Playback.
 */
public class PlayerController implements PlayerObserver {

    @FXML
    private Label nowPlayingLabel;

    @FXML
    private Label elapsedTimeLabel;

    @FXML
    private Label totalTimeLabel;

    @FXML
    private ProgressBar playbackProgressBar;

    @FXML
    private Slider playbackSlider;

    @FXML
    private RadioButton sequentialModeRadio;

    @FXML
    private RadioButton shuffleModeRadio;

    @FXML
    private RadioButton loopModeRadio;



    private final PlaybackService playbackService;
    private Track selectedTrack;
    private String lastStatusMessage;
    private Timeline playbackTimeline;
    private List<Track> currentPlaylist;
    private List<Track> originalPlaylist;
    private Runnable playbackChangeListener;
    private boolean updatingSliderFromPlayback;

    /**
     * Crea un controller del player con un nuovo PlaybackService.
     */
    public PlayerController() {
        this.playbackService = new PlaybackService();
        this.lastStatusMessage = "";
    }

    /**
     * Inizializza la sezione del player.
     */

    @FXML
    private void initialize() {
        ToggleGroup playbackModeGroup = new ToggleGroup();
        sequentialModeRadio.setToggleGroup(playbackModeGroup);
        shuffleModeRadio.setToggleGroup(playbackModeGroup);
        loopModeRadio.setToggleGroup(playbackModeGroup);
        sequentialModeRadio.setSelected(true);

        playbackTimeline = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> {
                    if (!playbackService.isPlaying()) {
                        return;
                    }

                    playbackService.advanceOneSecond();
                    refreshPlaybackView();

                    if (!playbackService.isPlaying()
                            && playbackService.getCurrentTrack() != null
                            && playbackService.getCurrentTime() >= playbackService.getDuration()) {
                        handleTrackCompletion();
                    }
                })
        );
        playbackTimeline.setCycleCount(Timeline.INDEFINITE);
        configurePlaybackSlider();
        sequentialModeRadio.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (Boolean.TRUE.equals(newValue)) {
                if (originalPlaylist != null && !originalPlaylist.isEmpty()) {
                    currentPlaylist = new java.util.ArrayList<>(originalPlaylist);
                    playbackService.setCurrentQueue(currentPlaylist);
                    playbackService.setLoopEnabled(false);
                    updateStatus("Modalità sequenziale attivata.");
                    refreshPlaybackView();
                }
            }
        });
        shuffleModeRadio.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (Boolean.TRUE.equals(newValue)) {
                handleShuffleMode();
            }
        });
        loopModeRadio.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (Boolean.TRUE.equals(newValue)) {
                playbackService.setLoopEnabled(true);
                updateStatus("Modalità loop attivata.");
                refreshPlaybackView();
            }
        });

        refreshPlaybackView();
    }

    /**
     * Imposta la traccia attualmente selezionata.
     *
     * @param selectedTrack traccia selezionata, oppure {@code null}
     */
    public void setSelectedTrack(Track selectedTrack) {
        this.selectedTrack = selectedTrack;
    }

    /**
     * Restituisce la traccia attualmente selezionata.
     *
     * @return traccia selezionata, oppure {@code null}
     */
    public Track getSelectedTrack() {
        return selectedTrack;
    }

    /**
     * Avvia la riproduzione della traccia selezionata oppure riprende
     * la riproduzione della traccia corrente se il player è in pausa.
     * <p>
     * Se non è stata selezionata alcuna traccia, il metodo non avvia
     * la riproduzione e aggiorna soltanto il messaggio di stato.
     * Se è disponibile una playlist corrente, essa viene sincronizzata
     * con la coda del {@link PlaybackService} prima di eseguire il comando.
     * </p>
     * <p>
     * Quando la traccia selezionata è diversa da quella in riproduzione,
     * la riproduzione parte dall'inizio e la timeline del player viene
     * riavviata. Se invece la traccia è la stessa ma il player è in pausa,
     * la riproduzione viene ripresa dal tempo corrente.
     * </p>
     */
    @FXML
    public void handlePlay() {
        if (selectedTrack == null) {
            updateStatus("Seleziona una traccia da riprodurre.");
            refreshPlaybackView();
            return;
        }

        if (currentPlaylist != null && !currentPlaylist.isEmpty()) {
            playbackService.setCurrentQueue(currentPlaylist);
        }

        Track currentTrack = playbackService.getCurrentTrack();

        if (currentTrack == null || !currentTrack.equals(selectedTrack)) {
            playbackService.playTrack(selectedTrack);

            if (playbackTimeline != null) {
                playbackTimeline.stop();
                playbackTimeline.playFromStart();
            }

            updateStatus("Riproduzione avviata: " + selectedTrack.getTitle());
        } else if (!playbackService.isPlaying()) {
            playbackService.resumeTrack();

            if (playbackTimeline != null) {
                playbackTimeline.play();
            }

            updateStatus("Riproduzione ripresa: " + selectedTrack.getTitle());
        }

        refreshPlaybackView();
    }

    /**
     * Mette in pausa la riproduzione corrente.
     * <p>
     * Il metodo sospende sia lo stato logico gestito dal
     * {@link PlaybackService} sia la timeline che aggiorna
     * l'avanzamento simulato della riproduzione.
     * </p>
     * <p>
     * Se non è presente alcuna traccia attiva, il metodo mantiene
     * comunque coerente lo stato del controller e aggiorna la vista.
     * </p>
     */
    @FXML
    public void handlePause() {
        playbackService.pauseTrack();

        if (playbackTimeline != null) {
            playbackTimeline.pause();
        }

        updateStatus("Riproduzione sospesa.");
        refreshPlaybackView();
    }

    /**
     * Passa alla traccia successiva della playlist corrente, se disponibile.
     * <p>
     * Se non è in riproduzione alcuna traccia, il metodo non esegue
     * alcun avanzamento e aggiorna soltanto il messaggio di stato.
     * Se è disponibile una playlist corrente, essa viene sincronizzata
     * con la coda del {@link PlaybackService} prima di eseguire il comando.
     * </p>
     * <p>
     * Quando esiste una traccia successiva, essa diventa la nuova traccia
     * selezionata e la timeline viene riavviata dall'inizio. Se invece
     * il player si trova già sull'ultimo brano, il metodo mantiene la
     * traccia corrente, evita errori di indice fuori limite e aggiorna
     * il messaggio di stato con l'esito del comando.
     * </p>
     */
    @FXML
    public void handleSkip() {
        Track trackBeforeCommand = playbackService.getCurrentTrack();

        if (trackBeforeCommand == null) {
            updateStatus("Nessuna traccia in riproduzione.");
            refreshPlaybackView();
            return;
        }

        if (currentPlaylist != null && !currentPlaylist.isEmpty()) {
            playbackService.setCurrentQueue(currentPlaylist);
        }

        playbackService.nextTrack();
        Track currentTrack = playbackService.getCurrentTrack();

        if (playbackTimeline != null) {
            playbackTimeline.stop();
            if (playbackService.isPlaying()) {
                playbackTimeline.playFromStart();
            }
        }

        selectedTrack = currentTrack;

        if (currentTrack != null && !currentTrack.equals(trackBeforeCommand)) {
            updateStatus("Riproduzione passata a: " + currentTrack.getTitle());
        } else {
            updateStatus("Nessuna traccia successiva disponibile. Riproduzione fermata sull'ultimo brano.");
        }

        refreshPlaybackView();
    }
    /**
     * Gestisce l'azione Previous.
     *
     * Se esiste una traccia precedente nella playlist corrente,
     * passa a quella; altrimenti riporta il brano corrente all'inizio.
     */
    @FXML
    public void handlePrevious() {
        Track trackBeforeCommand = playbackService.getCurrentTrack();

        if (trackBeforeCommand == null) {
            updateStatus("Nessuna traccia in riproduzione.");
            refreshPlaybackView();
            return;
        }

        if (currentPlaylist != null && !currentPlaylist.isEmpty()) {
            playbackService.setCurrentQueue(currentPlaylist);
        }

        playbackService.previousTrack();
        Track currentTrack = playbackService.getCurrentTrack();

        if (playbackTimeline != null) {
            playbackTimeline.stop();
            if (playbackService.isPlaying()) {
                playbackTimeline.playFromStart();
            }
        }

        selectedTrack = currentTrack;

        if (currentTrack != null && !currentTrack.equals(trackBeforeCommand)) {
            updateStatus("Riproduzione tornata a: " + currentTrack.getTitle());
        } else {
            updateStatus("Nessuna traccia precedente disponibile. Brano riportato all'inizio.");
        }

        refreshPlaybackView();
    }
    /**
     * Aggiorna i controlli grafici della sezione Simulated Playback
     * in base allo stato corrente della riproduzione.
     * <p>
     * Il metodo legge la traccia attualmente gestita dal
     * {@link PlaybackService} e sincronizza le label informative,
     * la progress bar e lo slider con i dati correnti del player.
     * </p>
     * <p>
     * Se non è presente alcuna traccia in riproduzione, la vista
     * viene riportata allo stato iniziale mostrando valori di default
     * e azzerando gli indicatori di avanzamento.
     * </p>
     */
    public void refreshPlaybackView() {
        Track currentTrack = playbackService.getCurrentTrack();

        if (currentTrack == null) {
            setLabelText(nowPlayingLabel, "No track selected");
            setLabelText(elapsedTimeLabel, "00:00");
            setLabelText(totalTimeLabel, "00:00");
            setProgress(0.0);
            setSliderValue(0.0);
            notifyPlaybackChange();
            return;
        }

        setLabelText(nowPlayingLabel, buildNowPlayingText(currentTrack));
        setLabelText(elapsedTimeLabel, formatTime(playbackService.getCurrentTime()));
        setLabelText(totalTimeLabel, safeText(currentTrack.getLength(), "00:00"));
        setProgress(calculateProgress());
        setSliderValue(calculateSliderValue());
        notifyPlaybackChange();
    }

    /**
     * Interrompe la riproduzione se la traccia rimossa coincide con quella
     * attualmente riprodotta.
     * <p>
     * Questo metodo viene chiamato quando una traccia viene eliminata dalla
     * libreria. Se la traccia rimossa è quella corrente, la riproduzione viene
     * fermata, la timeline viene arrestata e l'interfaccia del player viene
     * riportata allo stato iniziale.
     * </p>
     *
     * @param removedTrack traccia rimossa dalla libreria
     */
    public void stopPlaybackIfCurrentTrackWasRemoved(Track removedTrack) {
        if (removedTrack == null) {
            return;
        }

        Track currentTrack = getCurrentTrack();

        if (currentTrack == null || !currentTrack.equals(removedTrack)) {
            return;
        }

        if (playbackTimeline != null) {
            playbackTimeline.stop();
        }

        playbackService.resetTrack();

        setSelectedTrack(null);
        refreshPlaybackView();

        updateStatus("Riproduzione interrotta: la traccia è stata rimossa.");
    }

    /**
     * Restituisce il PlaybackService usato dal controller.
     *
     * @return service della riproduzione simulata
     */
    public PlaybackService getPlaybackService() {
        return playbackService;
    }
    /**
     * Restituisce la traccia attualmente in riproduzione nel service.
     *
     * @return traccia corrente, oppure {@code null} se nessuna traccia è stata
     *         avviata
     */
    public Track getCurrentTrack() {
        return playbackService.getCurrentTrack();
    }

    /**
     * Registra una callback da eseguire quando cambia lo stato visivo
     * della riproduzione.
     * <p>
     * La callback viene usata dai controller della schermata principale
     * per aggiornare le tabelle e rimuovere/applicare l'evidenziazione
     * della traccia attualmente in riproduzione.
     * </p>
     *
     * @param playbackChangeListener callback da eseguire quando cambia la riproduzione
     */

    public void setPlaybackChangeListener(Runnable playbackChangeListener) {
        this.playbackChangeListener = playbackChangeListener;
    }


    /**
     * Indica se il player si trova attualmente nello stato di riproduzione.
     *
     * @return {@code true} se la riproduzione è attiva, {@code false} altrimenti
     */
    public boolean isPlaying() {
        return playbackService.isPlaying();
    }
    /**
     * Avvia la riproduzione di una traccia proveniente da una playlist.
     * <p>
     * Se la traccia fornita è valida, essa viene impostata come traccia
     * selezionata e la riproduzione simulata viene avviata dall'inizio.
     * Se il parametro è {@code null}, il metodo non avvia alcuna riproduzione
     * e aggiorna soltanto lo stato del controller.
     * </p>
     *
     * @param track traccia da riprodurre dalla playlist, oppure {@code null}
     */
    public void playTrackFromPlaylist(Track track) {
        if (track == null) {
            updateStatus("Nessuna traccia disponibile per continuare la riproduzione.");
            refreshPlaybackView();
            return;
        }

        if (currentPlaylist != null && !currentPlaylist.isEmpty()) {
            playbackService.setCurrentQueue(currentPlaylist);
        }

        this.selectedTrack = track;
        playbackService.playTrack(track);

        if (playbackTimeline != null) {
            playbackTimeline.stop();
            playbackTimeline.playFromStart();
        }

        updateStatus("Riproduzione continuata con: " + track.getTitle());
        refreshPlaybackView();
    }
    /**
     * Ferma completamente la riproduzione simulata.
     * <p>
     * Il metodo resetta lo stato del {@link PlaybackService}, arresta la
     * timeline del player, annulla la traccia selezionata e aggiorna la vista
     * riportandola alla condizione iniziale.
     * </p>
     */
    public void stopPlayback() {
        playbackService.resetTrack();

        if (playbackTimeline != null) {
            playbackTimeline.stop();
        }

        selectedTrack = null;
        updateStatus("Riproduzione fermata.");
        refreshPlaybackView();
    }


    /**
     * Restituisce l'ultimo messaggio di stato.
     *
     * @return ultimo messaggio prodotto dal controller
     */
    public String getLastStatusMessage() {
        return lastStatusMessage;
    }

    /**
     * Aggiorna l'ultimo messaggio di stato prodotto dal controller.
     *
     * @param message nuovo messaggio di stato
     */
    private void updateStatus(String message) {
        this.lastStatusMessage = message;
    }

    /**
     * Notifica alla schermata principale che lo stato della riproduzione
     * è cambiato.
     * <p>
     * Se una callback è stata registrata, viene eseguita per permettere
     * il refresh delle tabelle che evidenziano la traccia in esecuzione.
     * </p>
     */
    private void notifyPlaybackChange() {
        if (playbackChangeListener != null) {
            playbackChangeListener.run();
        }
    }

    /**
     * Costruisce il testo da mostrare nella label "Now playing".
     * <p>
     * Il testo include il titolo della traccia e lo stato corrente del player,
     * distinguendo tra riproduzione attiva e pausa.
     * </p>
     *
     * @param track traccia da rappresentare
     * @return testo descrittivo da visualizzare nella sezione "Now playing"
     */
    private String buildNowPlayingText(Track track) {
        String title = safeText(track.getTitle(), "Titolo sconosciuto");
        String author = safeText(track.getAuthor(), "Autore sconosciuto");
        String year = track.getYear() > 0 ? String.valueOf(track.getYear()) : "Anno sconosciuto";
        String length = safeText(track.getLength(), "Durata sconosciuta");
        String playbackState = playbackService.isPlaying() ? "In riproduzione" : "In pausa";

        return title
                + " - "
                + author
                + " | "
                + year
                + " | "
                + length
                + " | "
                + playbackState;
    }

    /**
     * Imposta il testo di una label solo se il riferimento grafico è disponibile.
     *
     * @param label label da aggiornare
     * @param text testo da assegnare
     */
    private void setLabelText(Label label, String text) {
        if (label != null) {
            label.setText(text);
        }
    }

    /**
     * Restituisce un testo sicuro da mostrare nell'interfaccia.
     * <p>
     * Se il valore ricevuto è null, vuoto o composto solo da spazi,
     * viene restituito il testo alternativo passato come fallback.
     * In caso contrario viene restituito il valore ripulito dagli spazi
     * iniziali e finali.
     * </p>
     *
     * @param value valore testuale da controllare
     * @param fallback testo alternativo da usare se il valore non è disponibile
     * @return il valore ripulito se valido, altrimenti il testo di fallback
     */
    private String safeText(String value, String fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }
        return value.trim();
    }

    /**
     * Aggiorna la progress bar del player.
     *
     * @param progress valore di avanzamento compreso normalmente tra {@code 0.0}
     *                 e {@code 1.0}
     */
    private void setProgress(double progress) {
        if (playbackProgressBar != null) {
            playbackProgressBar.setProgress(progress);
        }
    }

    /**
     * Aggiorna il valore dello slider di riproduzione.
     * <p>
     * Il valore viene aggiornato automaticamente dalla timeline del player.
     * Durante questo aggiornamento viene usato un flag interno per evitare
     * che il listener dello slider interpreti il cambiamento come un'azione
     * manuale dell'utente.
     * </p>
     *
     * @param value valore da assegnare allo slider
     */

    private void setSliderValue(double value) {
        if (playbackSlider == null) {
            return;
        }

        updatingSliderFromPlayback = true;
        playbackSlider.setValue(value);
        updatingSliderFromPlayback = false;
    }

    /**
     * Configura lo slider di avanzamento della riproduzione.
     * <p>
     * Lo slider rappresenta la posizione corrente della traccia in percentuale,
     * da 0 a 100. Quando l'utente trascina o rilascia lo slider, il tempo
     * corrente del {@link PlaybackService} viene aggiornato in base alla durata
     * della traccia attiva.
     * </p>
     * <p>
     * Se non è presente alcuna traccia oppure la durata non è valida,
     * l'interazione non produce effetti.
     * </p>
     */
    private void configurePlaybackSlider() {
        if (playbackSlider == null) {
            return;
        }

        playbackSlider.setMin(0.0);
        playbackSlider.setMax(100.0);
        playbackSlider.setValue(0.0);

        playbackSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (updatingSliderFromPlayback) {
                return;
            }
            updatePlaybackTimeFromSlider();
        });

    }

    /**
     * Aggiorna il tempo corrente della riproduzione usando il valore dello slider.
     * <p>
     * Il valore dello slider viene interpretato come percentuale di avanzamento
     * della traccia. Il metodo calcola il secondo corrispondente, aggiorna il
     * {@link PlaybackService} e sincronizza subito label, progress bar e slider.
     * </p>
     */
    private void updatePlaybackTimeFromSlider() {
        if (playbackSlider == null) {
            return;
        }

        if (playbackService.getCurrentTrack() == null) {
            return;
        }

        int duration = playbackService.getDuration();

        if (duration <= 0) {
            return;
        }

        int newCurrentTime = (int) Math.round((playbackSlider.getValue() / 100.0) * duration);

        playbackService.setCurrentTime(newCurrentTime);
        refreshPlaybackView();
    }

    /**
     * Calcola il progresso corrente della riproduzione in formato normalizzato.
     *
     * @return valore compreso tra {@code 0.0} e {@code 1.0}; restituisce
     *         {@code 0.0} se la durata della traccia non è valida
     */
    private double calculateProgress() {
        int duration = playbackService.getDuration();

        if (duration <= 0) {
            return 0.0;
        }

        return (double) playbackService.getCurrentTime() / duration;
    }
    /**
     * Calcola il valore dello slider come percentuale di avanzamento.
     *
     * @return percentuale di avanzamento compresa tra {@code 0.0} e {@code 100.0}
     */
    private double calculateSliderValue() {
        return calculateProgress() * 100.0;
    }
    /**
     * Converte un tempo espresso in secondi nel formato {@code mm:ss}.
     *
     * @param seconds numero totale di secondi da formattare
     * @return rappresentazione testuale del tempo nel formato minuti:secondi
     */
    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;

        return String.format("%02d:%02d", minutes, remainingSeconds);
    }
    /**
     * Imposta la playlist corrente utilizzata dal player per la riproduzione
     * sequenziale e per il passaggio alla traccia successiva.
     * <p>
     * Il metodo aggiorna anche la coda interna del {@link PlaybackService},
     * così che le operazioni di Play, Pause e Skip lavorino sulla stessa
     * sequenza di brani visualizzata nell'interfaccia.
     * </p>
     *
     * @param currentPlaylist lista di tracce attualmente disponibile per la riproduzione,
     *                        oppure {@code null} per rimuovere la playlist corrente
     */
    public void setCurrentPlaylist(List<Track> currentPlaylist) {
        this.originalPlaylist = currentPlaylist == null ? null : List.copyOf(currentPlaylist);
        this.currentPlaylist = currentPlaylist == null ? null : List.copyOf(currentPlaylist);
        playbackService.setCurrentQueue(this.currentPlaylist);
    }

    /**
     * Attiva la modalità di riproduzione casuale riorganizzando
     * le tracce rimanenti della coda corrente.
     * <p>
     * La traccia eventualmente già in riproduzione non viene modificata
     * né riavviata; viene aggiornata soltanto la struttura della coda
     * usata per i successivi comandi di avanzamento.
     * </p>
     */
    private void handleShuffleMode() {
        if (originalPlaylist == null || originalPlaylist.isEmpty()) {
            updateStatus("Nessuna playlist disponibile per la modalità casuale.");
            return;
        }

        currentPlaylist = new java.util.ArrayList<>(originalPlaylist);
        playbackService.setCurrentQueue(currentPlaylist);
        playbackService.shuffleRemainingQueue();
        currentPlaylist = playbackService.getCurrentQueue();

        updateStatus("Modalità casuale attivata.");
        refreshPlaybackView();
    }

    /**
     * Gestisce il completamento automatico della traccia corrente
     * applicando la modalità di riproduzione attiva.
     */
    private void handleTrackCompletion() {
        Track finishedTrack = playbackService.getCurrentTrack();

        if (finishedTrack == null) {
            refreshPlaybackView();
            return;
        }

        if (currentPlaylist != null && !currentPlaylist.isEmpty()) {
            playbackService.setCurrentQueue(currentPlaylist);
        }

        playbackService.nextTrack();
        Track currentTrack = playbackService.getCurrentTrack();

        if (currentTrack != null && !currentTrack.equals(finishedTrack) && playbackService.isPlaying()) {
            selectedTrack = currentTrack;

            if (playbackTimeline != null) {
                playbackTimeline.stop();
                playbackTimeline.playFromStart();
            }

            updateStatus("Riproduzione continuata con: " + currentTrack.getTitle());
        } else {
            selectedTrack = currentTrack;

            if (playbackTimeline != null) {
                playbackTimeline.stop();
            }

            updateStatus("Riproduzione terminata.");
        }

        refreshPlaybackView();
    }

    /**
     * Riceve un aggiornamento dal motore del player osservato.
     * <p>
     * Il metodo aggiorna il messaggio di stato del controller in base al nome
     * dello stato corrente del motore di riproduzione.
     * </p>
     *
     * @param engine motore del player che ha generato la notifica
     */
    @Override
    public void update(MediaPlayerEngine engine) {
        updateStatus("Stato player: " + engine.getCurrentStateName());
    }

    /**
     * Sincronizza la coda di riproduzione corrente con l'ordine aggiornato
     * di una playlist, assicurandosi che il brano in esecuzione non salti.
     * * Questo metodo viene chiamato dai controller visivi quando l'utente
     * sposta una traccia su o giù.
     *
     * @param updatedPlaylist la lista di tracce riordinata
     */
    public void syncQueueWithoutInterrupting(List<Track> updatedPlaylist) {
        if (updatedPlaylist == null || updatedPlaylist.isEmpty()) {
            return;
        }

        boolean isEditingCurrentPlaylist = false;

        if (this.currentPlaylist != null && this.currentPlaylist.size() == updatedPlaylist.size()
                && this.currentPlaylist.containsAll(updatedPlaylist)) {
            isEditingCurrentPlaylist = true;
        }

        if (isEditingCurrentPlaylist) {
            this.originalPlaylist = List.copyOf(updatedPlaylist);
            this.currentPlaylist = List.copyOf(updatedPlaylist);
            playbackService.setCurrentQueue(this.currentPlaylist);
            System.out.println("Coda di riproduzione aggiornata al nuovo ordine.");
        }
    }
}