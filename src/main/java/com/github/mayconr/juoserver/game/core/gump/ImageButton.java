package com.github.mayconr.juoserver.game.core.gump;

class ImageButton implements UIElement, Sized {

    private final int artId;
    private final int buttonId;
    private final int upId;
    private final int downId;
    private int x, y;

    ImageButton(int artId, int upId, int downId, int buttonId) {
        this.artId = artId;
        this.upId = upId;
        this.downId = downId;
        this.buttonId = buttonId;
    }

    @Override
    public void layout(LayoutContext ctx) {
        x = ctx.x;
        y = ctx.y;
    }

    @Override
    public void render(GumpBuilder g) {
        g.gumpPic(x, y, artId, 0);
        g.button(x, y, upId, downId, true, 0, buttonId);
    }

    @Override
    public int getWidth() { return 44; }

    @Override
    public int getHeight() { return 44; }
}

