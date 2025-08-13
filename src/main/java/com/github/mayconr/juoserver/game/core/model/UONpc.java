package com.github.mayconr.juoserver.game.core.model;

import com.github.mayconr.juoserver.game.core.ai.BankerAI;
import com.github.mayconr.juoserver.game.core.ai.NpcAI;
import com.github.mayconr.juoserver.game.core.prototype.NpcPrototype;
import lombok.Getter;

@Getter
public class UONpc extends UOMobile {
    private final NpcType type;
    private int speechHue;
    private int speechFont;
    private String ai;

    public UONpc(int serialId, NpcPrototype prototype, Location location) {
        super(serialId, prototype.getModelId(), location.getX(), location.getY(), location.getZ(), prototype.getName(),
                Direction.NORTH, prototype.getHue(), CharacterStatus.NORMAL, prototype.getNotoriety(), prototype.getRace(),
                prototype.getGender());
        this.type = prototype.getType();
        this.speechHue = prototype.getSpeechHue();
        this.speechFont = prototype.getSpeechFont();
        this.ai = prototype.getAi();
    }
}
