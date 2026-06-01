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
        playbackTimeline.pause();
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
            return;
        }

        setLabelText(nowPlayingLabel, buildNowPlayingText(currentTrack));
        setLabelText(elapsedTimeLabel, formatTime(playbackService.getCurrentTime()));
        setLabelText(totalTimeLabel, currentTrack.getLength());
        setProgress(calculateProgress());
        setSliderValue(calculateSliderValue());
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
        if (playbackService.isPlaying()) {
            return track.getTitle() + " - In riproduzione";
        }

        return track.getTitle() + " - In pausa";
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
     *
     * @param value valore da assegnare allo slider
     */
    private void setSliderValue(double value) {
        if (playbackSlider != null) {
            playbackSlider.setValue(value);
        }
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
     * Riceve un aggiornamento dal motore del player osservato.
     * <p>
     * Il metodo aggiorna il messaggio di stato del controller in base al nome
     * dello stato corrente del motore di riproduzione.
     * </p>
     *
     * @param engine motore del player che ha generato la notifica
     */
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
    @Override
    public void update(MediaPlayerEngine engine) {
        updateStatus("Stato player: " + engine.getCurrentStateName());
    }
}