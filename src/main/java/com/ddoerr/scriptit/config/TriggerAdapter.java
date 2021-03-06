package com.ddoerr.scriptit.config;

import com.ddoerr.scriptit.api.registry.ScriptItRegistry;
import com.ddoerr.scriptit.api.triggers.Trigger;
import com.ddoerr.scriptit.api.triggers.TriggerFactory;
import com.ddoerr.scriptit.fields.Field;
import com.google.gson.*;
import net.minecraft.util.Identifier;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class TriggerAdapter implements JsonDeserializer<Trigger>, JsonSerializer<Trigger> {
    private ScriptItRegistry registry;

    public TriggerAdapter(ScriptItRegistry registry) {
        this.registry = registry;
    }

    @Override
    public Trigger deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        String type = jsonObject.getAsJsonPrimitive("type").getAsString();
        Identifier identifier = new Identifier(type);

        Map<String, String> data = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : jsonObject.getAsJsonObject("data").entrySet()) {
            String value = entry.getValue().getAsString();
            data.put(entry.getKey(), value);
        }

        TriggerFactory triggerFactory = registry.triggers.get(identifier);
        Trigger trigger = triggerFactory.createTrigger(data);
        return trigger;
    }

    @Override
    public JsonElement serialize(Trigger src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();

        Map<String, Field<?>> fieldValues = src.getFields();
        Map<String, String> options = new HashMap<>();

        for (Map.Entry<String, Field<?>> entry : fieldValues.entrySet()) {
            options.put(entry.getKey(), entry.getValue().serialize());
        }

        obj.addProperty("type", src.getIdentifier().toString());
        obj.add("data", context.serialize(options));

        return obj;
    }
}
