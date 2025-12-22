package com.github.mayconr.juoserver.game.core.gump;

class Divider implements UIElement, Sized {

    private final int width;
    private int x, y;

    Divider(int width) {
        this.width = width;
    }

    @Override
    public void layout(LayoutContext ctx) {
        x = ctx.x;
        y = ctx.y;
    }

    @Override
    public void render(GumpBuilder g) {
        g.gumpPicTiled(x, y, width, 1, 2624);
    }

    @Override
    public int getWidth() { return width; }

    @Override
    public int getHeight() { return 1; }
}

