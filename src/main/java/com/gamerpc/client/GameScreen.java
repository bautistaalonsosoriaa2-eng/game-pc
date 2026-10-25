package com.gamerpc.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

/** Base de los minijuegos: dibuja el marco y vuelve al escritorio con ESC. */
public abstract class GameScreen extends Screen {
    protected static final int W = 240;
    protected static final int H = 160;
    protected final Screen parent;
    protected int x0, y0;

    protected GameScreen(String title, Screen parent) {
        super(Text.literal(title));
        this.parent = parent;
    }

    @Override
    protected void init() {
        x0 = (width - W) / 2;
        y0 = (height - H) / 2;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void close() {
        client.setScreen(parent);
    }

    protected abstract void drawGame(DrawContext ctx);

    protected abstract String hint();

    protected void rect(DrawContext ctx, int x, int y, int w, int h, int color) {
        ctx.fill(x0 + x, y0 + y, x0 + x + w, y0 + y + h, color);
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        renderBackground(ctx);
        ctx.fill(x0 - 4, y0 - 4, x0 + W + 4, y0 + H + 4, 0xFF303040);
        ctx.fill(x0, y0, x0 + W, y0 + H, 0xFF000000);
        drawGame(ctx);
        ctx.drawCenteredTextWithShadow(textRenderer, Text.literal(title.getString()), width / 2, y0 - 16, 0xFFFFFF);
        ctx.drawCenteredTextWithShadow(textRenderer, Text.literal(hint()), width / 2, y0 + H + 10, 0xAAAAAA);
        super.render(ctx, mouseX, mouseY, delta);
    }
}
