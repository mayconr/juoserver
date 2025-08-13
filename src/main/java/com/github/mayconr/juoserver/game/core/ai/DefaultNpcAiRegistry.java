package com.github.mayconr.juoserver.game.core.ai;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class DefaultNpcAiRegistry implements NpcAiRegistry {

    private final Map<String, Supplier<NpcAI>> npcAIMap = new HashMap<>();

    public void registerAI(String key, Supplier<NpcAI> supplier) {
        npcAIMap.put(key, supplier);
    }

    @Override
    public NpcAI create(String ai) {
        if (npcAIMap.containsKey(ai)) {
            return npcAIMap.get(ai).get();
        }
        throw new IllegalArgumentException("AI ["+ai+"] not found in registry");
    }
}
