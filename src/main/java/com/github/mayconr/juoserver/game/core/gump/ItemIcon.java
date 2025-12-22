package com.github.mayconr.juoserver.game.core.gump;

class ItemIcon implements UIElement, Sized {

    private final int artId;
    private final int hue;
    private int x, y;

    ItemIcon(int artId, int hue) {
        this.artId = artId;
        this.hue = hue;
    }

    ItemIcon(int artId) {
        this(artId, 0);
    }

    @Override
    public void layout(LayoutContext ctx) {
        x = ctx.x;
        y = ctx.y;
    }

    @Override
    public void render(GumpBuilder g) {
        g.gumpPic(x, y, artId, hue);
    }

    @Override
    public int getWidth() { return 44; }

    @Override
    public int getHeight() { return 44; }
}

