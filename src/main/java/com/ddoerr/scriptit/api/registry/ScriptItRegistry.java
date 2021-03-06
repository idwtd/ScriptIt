package com.ddoerr.scriptit.api.registry;

import com.ddoerr.scriptit.ScriptItMod;
import com.ddoerr.scriptit.api.events.Event;
import com.ddoerr.scriptit.api.hud.HudElement;
import com.ddoerr.scriptit.api.hud.HudElementFactory;
import com.ddoerr.scriptit.api.languages.Language;
import com.ddoerr.scriptit.api.libraries.Model;
import com.ddoerr.scriptit.api.triggers.Trigger;
import com.ddoerr.scriptit.api.triggers.TriggerFactory;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;

import java.util.function.Supplier;

public class ScriptItRegistry extends SimpleRegistry<Registry<?>> {
    public final SimpleRegistry<Model> libraries = new SimpleRegistry<>();
    public final DefaultedRegistry<Language> languages = new DefaultedRegistry<>(ScriptItMod.MOD_NAME + ":text");
    public final SimpleRegistry<Event> events = new SimpleRegistry<>();
    public final SimpleRegistry<HudElementFactory> hudElements = new SimpleRegistry<>();
    public final SimpleRegistry<TriggerFactory> triggers = new SimpleRegistry<>();

    public ScriptItRegistry() {
        add(new Identifier(ScriptItMod.MOD_NAME, "library"), libraries);
        add(new Identifier(ScriptItMod.MOD_NAME, "language"), languages);
        add(new Identifier(ScriptItMod.MOD_NAME, "event"), events);
        add(new Identifier(ScriptItMod.MOD_NAME, "hud_element"), hudElements);
        add(new Identifier(ScriptItMod.MOD_NAME, "trigger"), triggers);
    }
}
