package com.gamerpc.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FlappyScreen extends GameScreen {
    private static final int BX = 50, BS = 8, GAP = 48, PWID = 20;
    private final Random rnd = new Random();
    private final List<int[]> pipes = new ArrayList<>();
    private float by, vy;
    private int timer, score;
    private boolean dead;

    public FlappyScreen(Screen parent) {
        super("Flappy", parent);
        reset();
    }

    private void reset() {
        pipes.clear();
        by = H / 2f;
        vy = 0;
        timer = 0;
        score = 0;
        dead = false;
    }

    private void flap() {
        if (dead) reset();
        else vy = -4.8f;
    }

    @Override
    public void tick() {
        if (dead) return;
        vy += 0.45f;
        by += vy;
        if (by < 0) { by = 0; vy = 0; }
        if (by + BS > H) { dead = true; return; }

        if (timer++ % 60 == 0) pipes.add(new int[]{W, 20 + rnd.nextInt(H - GAP - 40), 0});
        for (int[] p : pipes) {
            p[0] -= 2;
            if (BX + BS > p[0] && BX < p[0] + PWID && (by < p[1] || by + BS > p[1] + GAP)) dead = true;
            if (p[2] == 0 && p[0] + PWID < BX) { p[2] = 1; score++; }
        }
        pipes.removeIf(p -> p[0] < -PWID);
    }

    @Override
    public boolean keyPressed(int key, int scan, int mods) {
        if (key == GLFW.GLFW_KEY_SPACE || key == GLFW.GLFW_KEY_UP || key == GLFW.GLFW_KEY_W) {
            flap();
            return true;
        }
        return super.keyPressed(key, scan, mods);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        flap();
        return true;
    }

    @Override
    protected void drawGame(DrawContext ctx) {
        for (int[] p : pipes) {
            int xa = Math.max(0, p[0]), xb = Math.min(W, p[0] + PWID);
            if (xa >= xb) continue;
            rect(ctx, xa, 0, xb - xa, p[1], 0xFF30B030);
            rect(ctx, xa, p[1] + GAP, xb - xa, H - p[1] - GAP, 0xFF30B030);
        }
        rect(ctx, BX, (int) by, BS, BS, 0xFFFFE030);
        ctx.drawTextWithShadow(textRenderer, "Puntos: " + score, x0 + 4, y0 + 4, 0xFFFFFF);
        if (dead) ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("GAME OVER - Espacio para reiniciar"),
                x0 + W / 2, y0 + H / 2, 0xFF5555);
    }

    @Override
    protected String hint() {
        return "Espacio o clic para saltar | ESC: volver";
    }
}
