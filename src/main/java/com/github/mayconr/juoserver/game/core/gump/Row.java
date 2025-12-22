package com.github.mayconr.juoserver.game.core.gump;

import java.util.List;

public class Row implements UIElement {

    private final List<UIElement> children;
    private final int gap;

    public Row(int gap, List<UIElement> children) {
        this.gap = gap;
        this.children = children;
    }

    public Row(List<UIElement> children) {
        this(0, children);
    }

    @Override
    public void layout(LayoutContext ctx) {
        int cursorX = ctx.x;
        int baseY = ctx.y;

        for (UIElement child : children) {

            int childWidth = 0;
            int childHeight = 0;

            if (child instanceof Sized s) {
                childWidth = s.getWidth();
                childHeight = s.getHeight();
            }

            LayoutContext childCtx =
                    new LayoutContext(cursorX, baseY, childWidth, childHeight);

            child.layout(childCtx);

            cursorX += childWidth + gap;
        }
    }

    @Override
    public void render(GumpBuilder g) {
        for (UIElement child : children) {
            child.render(g);
        }
    }
}
