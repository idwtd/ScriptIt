package com.ddoerr.scriptit.screens;

import com.ddoerr.scriptit.api.hud.HudElementProvider;
import com.ddoerr.scriptit.api.hud.HudHorizontalAnchor;
import com.ddoerr.scriptit.api.hud.HudVerticalAnchor;
import com.ddoerr.scriptit.api.util.Color;
import com.ddoerr.scriptit.api.util.geometry.Point;
import com.ddoerr.scriptit.callbacks.ConfigCallback;
import com.ddoerr.scriptit.dependencies.Resolver;
import com.ddoerr.scriptit.elements.HudElement;
import com.ddoerr.scriptit.elements.HudElementManager;
import com.ddoerr.scriptit.loader.HudElementLoader;
import com.ddoerr.scriptit.widgets.PanelWidget;
import com.ddoerr.scriptit.widgets.ValuesDropdownWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Window;
import net.minecraft.text.LiteralText;
import spinnery.client.BaseScreen;
import spinnery.widget.*;
import spinnery.widget.api.Position;
import spinnery.widget.api.Size;

import java.lang.reflect.Field;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class HudElementScreen extends AbstractHistoryScreen {
    HudElementProvider currentlyAdding;

    HudElementManager hudElementManager;
    HudElementLoader hudElementLoader;

    List<HudElement> hudElements;
    HudElement focusedHudElement;
    Instant lastTimeClicked = Instant.now();
    Duration durationBetweenClicks = Duration.ofMillis(200);

    static Map<InputUtil.KeyCode, Point> movementKeys = new HashMap<>();
    static List<InputUtil.KeyCode> removalKeys = new ArrayList<>();

    static {
        movementKeys.put(InputUtil.fromName("key.keyboard.up"), new Point(0, -1));
        movementKeys.put(InputUtil.fromName("key.keyboard.right"), new Point(1, 0));
        movementKeys.put(InputUtil.fromName("key.keyboard.down"), new Point(0, 1));
        movementKeys.put(InputUtil.fromName("key.keyboard.left"), new Point(-1, 0));

        removalKeys.add(InputUtil.fromName("key.keyboard.backspace"));
        removalKeys.add(InputUtil.fromName("key.keyboard.delete"));
    }

    ValuesDropdownWidget<String> dropdown;

    ScreenHistory history;

    public HudElementScreen() {
        super();

        hudElementManager = Resolver.getInstance().resolve(HudElementManager.class);
        hudElementLoader = Resolver.getInstance().resolve(HudElementLoader.class);
        history = Resolver.getInstance().resolve(ScreenHistory.class);

        hudElements = hudElementManager.getAll();

        setupWidgets();
    }

    private void setupWidgets() {
        WInterface mainInterface = getInterface();

        setupDropdown(mainInterface);
    }

    private void setupDropdown(WInterface mainInterface) {
        Map<String, HudElementProvider> providers = hudElementLoader.getProviders();

        dropdown = mainInterface.createChild(ValuesDropdownWidget.class, Position.ofBottomLeft(mainInterface).add(0, -22, 0), Size.of(200, 20));

        dropdown.centerX();
        dropdown.setDirection(ValuesDropdownWidget.DropdownDirection.Up);
        dropdown.addValues(providers.keySet());
        dropdown.setLabel("Add Hud Element");
        dropdown.setOnChange(key -> currentlyAdding = providers.get(key));
    }



    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (currentlyAdding != null) {
            HudElement hudElement = new HudElement(currentlyAdding, mouseX, mouseY);
            hudElementManager.add(hudElement);

            currentlyAdding = null;
            dropdown.setLabel(new LiteralText("Add Hud Element"));
            return true;
        }

        if (focusedHudElement != null && isMouseOver(focusedHudElement, mouseX, mouseY)) {
            Instant timeClicked = Instant.now();
            Duration duration = Duration.between(lastTimeClicked, timeClicked);

            if (duration.compareTo(durationBetweenClicks) < 0) {
                history.open(() -> new HudElementEditorScreen(focusedHudElement));
            }
        }

        lastTimeClicked = Instant.now();

        focusedHudElement = null;
        for (HudElement hudElement : hudElements) {
            if (isMouseOver(hudElement, mouseX, mouseY)) {
                focusedHudElement = hudElement;
                break;
            }
        }

        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void render(int mouseX, int mouseY, float tick) {
        for (HudElement hudElement : hudElements) {
            Point point = hudElement.getRealPosition();
            int x = (int) point.getX();
            int y = (int) point.getY();

            int width = hudElement.getWidth();
            int height = hudElement.getHeight();

            switch (hudElement.getVerticalAnchor()) {
                case MIDDLE:
                    y += height / 2;
                    break;
                case BOTTOM:
                    y += height - 1;
                    break;
            }

            switch (hudElement.getHorizontalAnchor()) {
                case CENTER:
                    x += width / 2;
                    break;
                case RIGHT:
                    x += width - 1;
                    break;
            }

            if (hudElement.equals(focusedHudElement)) {
                Color color = Color.WHITE;
                fill(x, y, x + width, y + 1, color.getValue());
                fill(x, y, x + 1, y + height, color.getValue());
                fill(x, y + height - 1, x + width, y + height,color.getValue());
                fill(x + width - 1, y, x + width, y + height, color.getValue());
            }

            fill(x - 1, y - 1, x + 2, y + 2, Color.RED.getValue());
        }

        super.render(mouseX, mouseY, tick);
    }

    @Override
    public boolean keyPressed(int character, int keyCode, int keyModifier) {
        if (focusedHudElement == null) {
            return super.keyPressed(character, keyCode, keyModifier);
        }

        InputUtil.KeyCode code = InputUtil.getKeyCode(character, keyCode);

        for (Map.Entry<InputUtil.KeyCode, Point> entry : movementKeys.entrySet()) {
            if (code == entry.getKey()) {
                Point point = entry.getValue();
                move(focusedHudElement, point.getX(), point.getY());
                return true;
            }
        }

        for (InputUtil.KeyCode removalKey : removalKeys) {
            if (code == removalKey) {
                hudElementManager.remove(focusedHudElement);

                ConfigCallback.EVENT.invoker().saveConfig(this.getClass());
                return true;
            }
        }

        return super.keyPressed(character, keyCode, keyModifier);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double deltaX, double deltaY) {
        if (focusedHudElement != null) {
            move(focusedHudElement, deltaX, deltaY);
        }

        return super.mouseDragged(mouseX, mouseY, mouseButton, deltaX, deltaY);
    }

    private void move(HudElement hudElement, double xDelta, double yDelta) {
        Point point = hudElement.getRealPosition();
        hudElement.setRealPosition(new Point(point.getX() + xDelta, point.getY() + yDelta));
    }

    public boolean isMouseOver(HudElement hudElement, double x, double y) {
        Point point = hudElement.getRealPosition();
        return x >= point.getX() && x <= point.getX() + hudElement.getWidth() && y >= point.getY() && y <= point.getY() + hudElement.getHeight();
    }
}
