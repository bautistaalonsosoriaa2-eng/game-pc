package com.gamerpc.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.Random;

public class PongScreen extends GameScreen {
    private static final int PH = 30, PW = 4, BALL = 4;
    private final Random rnd = new Random();
    private float py = H / 2f - PH / 2f, ay = py, bx, by, vx, vy;
    private int playerScore, aiScore;

    public PongScreen(Screen parent) {
        super("Pong", parent);
        resetBall();
    }

    private void resetBall() {
        bx = W / 2f;
        by = H / 2f;
        vx = (rnd.nextBoolean() ? 1 : -1) * 2.5f;
        vy = (rnd.nextFloat() - 0.5f) * 3f;
    }

    private static float clamp(float v, float lo, float hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    @Override
    public void tick() {
        long handle = client.getWindow().getHandle();
        if (InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_W) || InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_UP)) py -= 4;
        if (InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_S) || InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_DOWN)) py += 4;
        py = clamp(py, 0, H - PH);

        float center = ay + PH / 2f;
        if (center < by - 3) ay += 2.4f;
        else if (center > by + 3) ay -= 2.4f;
        ay = clamp(ay, 0, H - PH);

        bx += vx;
        by += vy;
        if (by < 0) { by = 0; vy = -vy; }
        if (by > H - BALL) { by = H - BALL; vy = -vy; }

        // paleta del jugador (izquierda)
        if (vx < 0 && bx <= 8 + PW && bx + BALL >= 8 && by + BALL >= py && by <= py + PH) {
            vx = clamp(-vx * 1.05f, -6, 6);
            vy += ((by + BALL / 2f) - (py + PH / 2f)) * 0.08f;
            bx = 8 + PW;
        }
        // paleta de la PC (derecha)
        if (vx > 0 && bx + BALL >= W - 8 - PW && bx <= W - 8 && by + BALL >= ay && by <= ay + PH) {
            vx = clamp(-vx * 1.05f, -6, 6);
            vy += ((by + BALL / 2f) - (ay + PH / 2f)) * 0.08f;
            bx = W - 8 - PW - BALL;
        }
        if (bx < -BALL) { aiScore++; resetBall(); }
        if (bx > W) { playerScore++; resetBall(); }
    }

    @Override
    protected void drawGame(DrawContext ctx) {
        for (int y = 0; y < H; y += 10) rect(ctx, W / 2 - 1, y, 2, 5, 0xFF444444);
        rect(ctx, 8, (int) py, PW, PH, 0xFF40E0FF);
        rect(ctx, W - 8 - PW, (int) ay, PW, PH, 0xFFFF4080);
        rect(ctx, (int) bx, (int) by, BALL, BALL, 0xFFFFFFFF);
        ctx.drawTextWithShadow(textRenderer, String.valueOf(playerScore), x0 + W / 2 - 30, y0 + 6, 0xFFFFFF);
        ctx.drawTextWithShadow(textRenderer, String.valueOf(aiScore), x0 + W / 2 + 24, y0 + 6, 0xFFFFFF);
    }

    @Override
    protected String hint() {
        return "W/S o flechas para mover | ESC: volver";
    }
}
