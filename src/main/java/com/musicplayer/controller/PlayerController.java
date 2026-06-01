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
                    if (playbackService.isPlaying()) {
                        playbackService.advanceOneSecond();
                        refreshPlaybackView();
                    }
                })
        );
        playbackTimeline.setCycleCount(Timeline.INDEFINITE);

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
     * Gestisce l'azione Play.
     */
    @FXML
    public void handlePlay() {
        if (selectedTrack == null) {
            updateStatus("Seleziona una traccia da riprodurre.");
            refreshPlaybackView();
            return;
        }

        Track currentTrack = playbackService.getCurrentTrack();

        if (currentTrack == null || !currentTrack.equals(selectedTrack)) {
            playbackService.playTrack(selectedTrack);   // parte da 0 solo su nuova traccia
            playbackTimeline.stop();
            playbackTimeline.playFromStart();
            updateStatus("Riproduzione avviata: " + selectedTrack.getTitle());
        } else if (!playbackService.isPlaying()) {
            playbackService.resumeTrack();              // riprende dal tempo corrente
            playbackTimeline.play();                    // resume del timeline
            updateStatus("Riproduzione ripresa: " + selectedTrack.getTitle());
        }

        refreshPlaybackView();
    }

    /**
     * Gestisce l'azione Pause.
     */
    @FXML
    public void handlePause() {
        playbackService.pauseTrack();
        playbackTimeline.pause();
        updateStatus("Riproduzione sospesa.");
        refreshPlaybackView();
    }

    /**
     * Gestisce l'azione Skip.
     *
     * Per ora il comportamento non è ancora implementato.
     */
    @FXML
    public void handleSkip() {
        updateStatus("Skip non ancora implementato.");
    }

    /**
     * Aggiorna la sezione Simulated Playback.
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

        this.selectedTrack = track;
        playbackService.playTrack(track);
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

    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;

        return String.format("%02d:%02d", minutes, remainingSeconds);
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
}