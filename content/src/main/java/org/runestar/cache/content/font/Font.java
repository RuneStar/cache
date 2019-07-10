package org.runestar.cache.content.font;

import org.runestar.cache.content.graphics.SpriteSheet;

import java.util.Objects;

public final class Font {

    private final SpriteSheet sprites;

    private final FontMetrics metrics;

    public Font(SpriteSheet sprites, FontMetrics metrics) {
        this.sprites = Objects.requireNonNull(sprites);
        this.metrics = Objects.requireNonNull(metrics);
    }
}
