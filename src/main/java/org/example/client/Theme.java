package org.example.client;

/**
 * Provides centralized styling constants and methods for the application's UI.
 * Maintains consistent colors, fonts, and styles across all screens.
 */
public final class Theme {
    // Color Constants
    /** Primary blue color used for main buttons */
    public static final String PRIMARY_COLOR = "#3498db";
    /** Secondary green color used for success states */
    public static final String SECONDARY_COLOR = "#2ecc71";
    /** Accent red color used for important actions */
    public static final String ACCENT_COLOR = "#e74c3c";
    /** Warning orange color used for highlights */
    public static final String WARNING_COLOR = "#f39c12";
    /** Main purple gradient background */
    public static final String BACKGROUND_GRADIENT = "linear-gradient(to bottom, #6a11cb, #2575fc)";
    /** Secondary light gray background */
    public static final String SECONDARY_BACKGROUND = "#f8f9fa";
    /** Dark text color for light backgrounds */
    public static final String TEXT_COLOR = "#2c3e50";
    /** Light text color for dark backgrounds */
    public static final String LIGHT_TEXT = "#ecf0f1";
    /** Disabled control color */
    public static final String DISABLED_COLOR = "#95a5a6";

    // Font Constants
    /** Font family for titles and headings */
    public static final String TITLE_FONT = "Arial Rounded MT Bold";
    /** Font family for body text */
    public static final String BODY_FONT = "Segoe UI";

    /**
     * @return CSS style for primary buttons
     */
    public static String getPrimaryButtonStyle() {
        return String.format(
                "-fx-background-color: %s; " +
                        "-fx-text-fill: %s; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 5; " +
                        "-fx-padding: 8 16 8 16;",
                PRIMARY_COLOR, LIGHT_TEXT
        );
    }

    /**
     * @return CSS style for secondary buttons
     */
    public static String getSecondaryButtonStyle() {
        return String.format(
                "-fx-background-color: %s; " +
                        "-fx-text-fill: %s; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 5; " +
                        "-fx-padding: 8 16 8 16;",
                SECONDARY_COLOR, LIGHT_TEXT
        );
    }

    /**
     * @return CSS style for accent buttons
     */
    public static String getAccentButtonStyle() {
        return String.format(
                "-fx-background-color: %s; " +
                        "-fx-text-fill: %s; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 5; " +
                        "-fx-padding: 8 16 8 16;",
                ACCENT_COLOR, LIGHT_TEXT
        );
    }

    /**
     * @return CSS style for main gradient background
     */
    public static String getBackgroundStyle() {
        return "-fx-background-color: " + BACKGROUND_GRADIENT + ";";
    }

    /**
     * @return CSS style for secondary background
     */
    public static String getSecondaryBackgroundStyle() {
        return "-fx-background-color: " + SECONDARY_BACKGROUND + ";";
    }

    /**
     * @return CSS style for title text
     */
    public static String getTitleStyle() {
        return String.format(
                "-fx-font-family: '%s'; " +
                        "-fx-font-size: 24px; " +
                        "-fx-text-fill: %s; " +
                        "-fx-font-weight: bold;",
                TITLE_FONT, LIGHT_TEXT
        );
    }

    /**
     * @return CSS style for body text
     */
    public static String getBodyStyle() {
        return String.format(
                "-fx-font-family: '%s'; " +
                        "-fx-font-size: 14px; " +
                        "-fx-text-fill: %s;",
                BODY_FONT, TEXT_COLOR
        );
    }

    /**
     * @return CSS style for light body text (on dark backgrounds)
     */
    public static String getLightBodyStyle() {
        return String.format(
                "-fx-font-family: '%s'; " +
                        "-fx-font-size: 14px; " +
                        "-fx-text-fill: %s;",
                BODY_FONT, LIGHT_TEXT
        );
    }
}