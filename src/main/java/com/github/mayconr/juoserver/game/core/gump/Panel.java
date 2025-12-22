package com.github.mayconr.juoserver.game.core.gump;

public class Panel implements UIElement {

    private final int width;
    private final int height;
    private final Integer backgroundGumpPicId; // null = sem background
    private final int hue;
    private final UIElement content;

    private int x, y;

    // Panel SEM background (container puro)
    public Panel(int width, int height, UIElement content) {
        this.width = width;
        this.height = height;
        this.backgroundGumpPicId = null;
        this.hue = 0;
        this.content = content;
    }

    // Panel COM background
    public Panel(int width, int height, int backgroundGumpPicId, UIElement content) {
        this(width, height, backgroundGumpPicId, 0, content);
    }

    public Panel(int width, int height, int backgroundGumpPicId, int hue, UIElement content) {
        this.width = width;
        this.height = height;
        this.backgroundGumpPicId = backgroundGumpPicId;
        this.hue = hue;
        this.content = content;
    }

    @Override
    public void layout(LayoutContext ctx) {
        this.x = ctx.x;
        this.y = ctx.y;

        // padding só existe se houver background
        int padding = backgroundGumpPicId != null ? 10 : 0;

        LayoutContext inner = ctx.child(
                x + padding,
                y + padding,
                width - padding * 2,
                height - padding * 2
        );

        content.layout(inner);
    }

    @Override
    public void render(GumpBuilder g) {
        if (backgroundGumpPicId != null) {
            g.resizePic(x, y, backgroundGumpPicId, width, height);
        }

        content.render(g);
    }
}
