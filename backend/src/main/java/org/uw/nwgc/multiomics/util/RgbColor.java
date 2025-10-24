package org.uw.nwgc.multiomics.util;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public class RgbColor {
    private int red;
    private int green;
    private int blue;

    /**
     * Constructs an RgbColor object with the specified red, green, and blue values.
     * Values should be between 0 and 255.
     *
     * @param red   The red component (0-255).
     * @param green The green component (0-255).
     * @param blue  The blue component (0-255).
     * @throws IllegalArgumentException if any color component is outside the 0-255 range.
     */
    public RgbColor(int red, int green, int blue) {
        if (red < 0 || red > 255 || green < 0 || green > 255 || blue < 0 || blue > 255) {
            throw new IllegalArgumentException("RGB color components must be between 0 and 255.");
        }
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    /**
     * Returns a string representation of the RgbColor in hexadecimal format (e.g., "#RRGGBB").
     *
     * @return The hexadecimal string representation of the color.
     */
    @Override
    @JsonValue
    public String toString() {
        return String.format("#%02X%02X%02X", red, green, blue);
    }
}
