package com.github.mayconr.juoserver.game.core.gump;

class Button implements UIElement, Sized {
    private final String caption;
    private final int buttonId;
    private int x, y;


    public Button(String caption, int buttonId) {
        this.caption = caption;
        this.buttonId = buttonId;
    }


    @Override
    public void layout(LayoutContext ctx) {
        this.x = ctx.x;
        this.y = ctx.y;
    }


    @Override
    public void render(GumpBuilder g) {
        g.button(x, y, 4005, 4007, true, 0, buttonId)
                .text(x + 35, y + 2, 1152, caption);
    }


    @Override
    public int getWidth() {
        return 80;
    }


    @Override
    public int getHeight() {
        return 22;
    }
}
