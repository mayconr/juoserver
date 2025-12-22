package com.github.mayconr.juoserver.game.core.gump;

class Image implements UIElement, Sized {

    private final int gumpPicId;
    private final int hue;
    private int x, y;

    public Image(int gumpPicId) {
        this(gumpPicId, 0);
    }

    public Image(int gumpPicId, int hue) {
        this.gumpPicId = gumpPicId;
        this.hue = hue;
    }

    @Override
    public void layout(LayoutContext ctx) {
        this.x = ctx.x;
        this.y = ctx.y;
    }

    @Override
    public void render(GumpBuilder g) {
        g.gumpPic(x, y, gumpPicId, hue);
    }

    @Override
    public int getWidth() {
        return 44; // estimado (depende do gumpPicId)
    }

    @Override
    public int getHeight() {
        return 44;
    }
}
