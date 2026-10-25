package com.gamerpc.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

public class SnakeScreen extends GameScreen {
    private static final int CELL = 8, COLS = W / CELL, ROWS = H / CELL;
    private final Deque<int[]> snake = new ArrayDeque<>();
    private final Random rnd = new Random();
    private int dx, dy, nx, ny, foodX, foodY, score, timer;
    private boolean dead;

    public SnakeScreen(Screen parent) {
        super("Snake", parent);
        reset();
    }

    private void reset() {
        snake.clear();
        snake.add(new int[]{COLS / 2, ROWS / 2});
        dx = nx = 1;
        dy = ny = 0;
        score = 0;
        dead = false;
        placeFood();
    }

    private boolean onSnake(int x, int y) {
        for (int[] c : snake) if (c[0] == x && c[1] == y) return true;
        return false;
    }

    private void placeFood() {
        do {
            foodX = rnd.nextInt(COLS);
            foodY = rnd.nextInt(ROWS);
        } while (onSnake(foodX, foodY));
    }

    @Override
    public void tick() {
        if (dead || ++timer < 3) return;
        timer = 0;
        dx = nx;
        dy = ny;
        int[] head = snake.peekFirst();
        int x = head[0] + dx, y = head[1] + dy;
        if (x < 0 || y < 0 || x >= COLS || y >= ROWS || onSnake(x, y)) {
            dead = true;
            return;
        }
        snake.addFirst(new int[]{x, y});
        if (x == foodX && y == foodY) {
            score++;
            placeFood();
        } else {
            snake.pollLast();
        }
    }

    @Override
    public boolean keyPressed(int key, int scan, int mods) {
        if ((key == GLFW.GLFW_KEY_UP || key == GLFW.GLFW_KEY_W) && dy == 0) { nx = 0; ny = -1; return true; }
        if ((key == GLFW.GLFW_KEY_DOWN || key == GLFW.GLFW_KEY_S) && dy == 0) { nx = 0; ny = 1; return true; }
        if ((key == GLFW.GLFW_KEY_LEFT || key == GLFW.GLFW_KEY_A) && dx == 0) { nx = -1; ny = 0; return true; }
        if ((key == GLFW.GLFW_KEY_RIGHT || key == GLFW.GLFW_KEY_D) && dx == 0) { nx = 1; ny = 0; return true; }
        if (key == GLFW.GLFW_KEY_SPACE && dead) { reset(); return true; }
        return super.keyPressed(key, scan, mods);
    }

    @Override
    protected void drawGame(DrawContext ctx) {
        rect(ctx, foodX * CELL, foodY * CELL, CELL - 1, CELL - 1, 0xFFFF3030);
        boolean first = true;
        for (int[] c : snake) {
            rect(ctx, c[0] * CELL, c[1] * CELL, CELL - 1, CELL - 1, first ? 0xFF7CFF7C : 0xFF20C020);
            first = false;
        }
        ctx.drawTextWithShadow(textRenderer, "Puntos: " + score, x0 + 4, y0 + 4, 0xFFFFFF);
        if (dead) ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("GAME OVER - Espacio para reiniciar"),
                x0 + W / 2, y0 + H / 2, 0xFF5555);
    }

    @Override
    protected String hint() {
        return "Flechas o WASD | ESC: volver";
    }
}
