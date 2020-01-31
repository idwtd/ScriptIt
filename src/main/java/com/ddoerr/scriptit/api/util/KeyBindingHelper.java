package com.ddoerr.scriptit.api.util;

import com.ddoerr.scriptit.bus.KeyBindingBusExtension;
import com.ddoerr.scriptit.dependencies.Resolver;
import com.ddoerr.scriptit.mixin.KeyBindingAccessor;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.mixin.client.keybinding.KeyCodeAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

import java.util.Map;

public class KeyBindingHelper {
    public static boolean hasConflict(InputUtil.KeyCode keyCode) {
        MinecraftClient minecraft = MinecraftClient.getInstance();

        for (KeyBinding keyBinding : minecraft.options.keysAll) {
            if (hasKeyBindingKeyCode(keyBinding, keyCode)) {
                return true;
            }
        }

        KeyBindingBusExtension keyBindingBusExtension = Resolver.getInstance().resolve(KeyBindingBusExtension.class);
        for (KeyBinding keyBinding : keyBindingBusExtension.getKeyBindings()) {
            if (hasKeyBindingKeyCode(keyBinding, keyCode)) {
                return true;
            }
        }

        return false;
    }

    private static boolean hasKeyBindingKeyCode(KeyBinding keyBinding, InputUtil.KeyCode keyCode) {
        return ((KeyCodeAccessor)keyBinding).getKeyCode().getName().equals(keyCode.getName());
    }

    public static KeyBinding create(Identifier identifier, InputUtil.KeyCode keyCode) {
        return FabricKeyBinding.Builder.create(
                identifier,
                keyCode.getCategory(),
                keyCode.getKeyCode(),
                "Scripts"
        ).build();
    }
    public static KeyBinding create(Identifier identifier) {
        return create(identifier, InputUtil.UNKNOWN_KEYCODE);
    }

    public static KeyBinding create(String id, InputUtil.KeyCode keyCode) {
        return new KeyBinding(
                id,
                keyCode.getCategory(),
                keyCode.getKeyCode(),
                "Scripts"
        );
    }

    public static void remove(KeyBinding keyBinding) {
        Map<String, KeyBinding> keyBindings = KeyBindingAccessor.getKeysById();
        keyBindings.remove(keyBinding.getId());
        KeyBinding.updateKeysByCode();
    }
}
