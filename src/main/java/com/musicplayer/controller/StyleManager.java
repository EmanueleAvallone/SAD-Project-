package com.musicplayer.controller;

import javafx.scene.Scene;
import javafx.scene.control.Dialog;

import java.net.URL;

/**
 * Utility class dedicata all'applicazione uniforme del foglio di stile CSS.
 *
 * Questa classe centralizza il percorso del file CSS ed evita di ripetere
 * lo stesso codice in tutti i controller quando bisogna applicare lo stile
 * a scene secondarie, dialog o popup.
 */
public final class StyleManager {

    private static final String CSS_PATH = "/musicplayer/view/music-playlist-manager.css";

    /**
     * Costruttore privato perché la classe contiene solo metodi statici.
     */
    private StyleManager() {
    }

    /**
     * Applica il foglio di stile alla scena indicata.
     *
     * @param scene scena JavaFX a cui applicare il CSS
     */
    public static void applyToScene(Scene scene) {
        if (scene == null) {
            return;
        }

        URL cssUrl = StyleManager.class.getResource(CSS_PATH);

        if (cssUrl == null) {
            System.err.println("CSS non trovato: " + CSS_PATH);
            return;
        }

        String css = cssUrl.toExternalForm();

        if (!scene.getStylesheets().contains(css)) {
            scene.getStylesheets().add(css);
        }
    }

    /**
     * Applica il foglio di stile a un dialog JavaFX.
     *
     * Questo metodo serve per Alert, TextInputDialog e ChoiceDialog.
     *
     * @param dialog dialog a cui applicare il CSS
     */
    public static void applyToDialog(Dialog<?> dialog) {
        if (dialog == null || dialog.getDialogPane() == null) {
            return;
        }

        URL cssUrl = StyleManager.class.getResource(CSS_PATH);

        if (cssUrl == null) {
            System.err.println("CSS non trovato: " + CSS_PATH);
            return;
        }

        String css = cssUrl.toExternalForm();

        if (!dialog.getDialogPane().getStylesheets().contains(css)) {
            dialog.getDialogPane().getStylesheets().add(css);
        }

        if (!dialog.getDialogPane().getStyleClass().contains("app-root")) {
            dialog.getDialogPane().getStyleClass().add("app-root");
        }
    }
}