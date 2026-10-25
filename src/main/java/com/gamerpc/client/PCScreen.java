package com.gamerpc.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

import java.net.URI;

/** Escritorio de la PC gamer. */
public class PCScreen extends Screen {
    private static final String YT = "https://www.youtube.com";
    private final boolean internet;

    public PCScreen(boolean internet) {
        super(Text.literal("Gamer PC"));
        this.internet = internet;
    }

    @Override
    protected void init() {
        int cx = width / 2;
        int y = height / 2 - 50;

        ButtonWidget yt = ButtonWidget.builder(Text.literal("YouTube (abre tu navegador)"), b -> openYoutube())
                .dimensions(cx - 90, y, 180, 20).build();
        yt.active = internet;
        addDrawableChild(yt);

        addDrawableChild(ButtonWidget.builder(Text.literal("Snake"),
                b -> client.setScreen(new SnakeScreen(this))).dimensions(cx - 90, y + 26, 180, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Pong"),
                b -> client.setScreen(new PongScreen(this))).dimensions(cx - 90, y + 52, 180, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Flappy"),
                b -> client.setScreen(new FlappyScreen(this))).dimensions(cx - 90, y + 78, 180, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Apagar"),
                b -> close()).dimensions(cx - 90, y + 104, 180, 20).build());
    }

    private void openYoutube() {
        client.setScreen(new ConfirmLinkScreen(ok -> {
            if (ok) Util.getOperatingSystem().open(URI.create(YT));
            client.setScreen(this);
        }, YT, true));
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fillGradient(0, 0, width, height, 0xFF3A0CA3, 0xFF4CC9F0);
        ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("GAMER PC OS"), width / 2, height / 2 - 80, 0xFFFFFF);
        ctx.fill(0, height - 18, width, height, 0xFF101018);
        String status = internet ? "Internet: conectado (router)" : "Sin Internet - coloca un router cerca";
        ctx.drawTextWithShadow(textRenderer, status, 6, height - 13, internet ? 0x55FF55 : 0xFF5555);
        super.render(ctx, mouseX, mouseY, delta);
    }
}
