package net.sf.juoserver.model;

import net.sf.juoserver.api.*;
import net.sf.juoserver.model.ai.WalkScript;

public class UONpcMobile extends UOMobile implements NpcMobile {

    private int templateId;
    private AIScript aiScript;

    public UONpcMobile(int serialId, String name, Point3D location, AIScript aiScript) {
        super(serialId,  0x9,name, 100, 100, false, StatusFlag.UOML, SexRace.MaleHuman, 100,50,100,1000,100,100,100,0,0,0,100,RaceFlag.Elf, location, Notoriety.Murderer, true);
        this.aiScript = aiScript;
    }

    public UONpcMobile() {
        super(true);
        this.aiScript = new WalkScript();
    }

    @Override
    public AIScript getAIScript() {
        return aiScript;
    }

    public void setAiScript(AIScript aiScript) {
        this.aiScript = aiScript;
    }

    @Override
    public int getTemplateId() {
        return templateId;
    }

    public void setTemplateId(int templateId) {
        this.templateId = templateId;
    }
}
