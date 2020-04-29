package com.ddoerr.scriptit.api.registry;

import com.ddoerr.scriptit.ScriptItMod;
import com.ddoerr.scriptit.api.events.Event;
import com.ddoerr.scriptit.api.hud.HudElement;
import com.ddoerr.scriptit.api.languages.Language;
import com.ddoerr.scriptit.api.libraries.Model;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.SimpleRegistry;

public class ScriptItRegistry {
    public SimpleRegistry<Model> libraries = new SimpleRegistry<>();
    public DefaultedRegistry<Language> languages = new DefaultedRegistry<>(ScriptItMod.MOD_NAME + ":lua");
    public SimpleRegistry<Event> events = new SimpleRegistry<>();
    public SimpleRegistry<HudElement> hudElements = new SimpleRegistry<>();
}