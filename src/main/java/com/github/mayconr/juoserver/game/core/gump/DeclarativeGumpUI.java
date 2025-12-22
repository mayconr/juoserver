package com.github.mayconr.juoserver.game.core.gump;

import java.util.List;

public class DeclarativeGumpUI {


    private final Page root;


    public DeclarativeGumpUI(Page root) {
        this.root = root;
    }


    public void render(GumpBuilder g) {
        root.layout(new LayoutContext(0, 0, 0, 0));
        root.render(g);
    }


    /* =========================
     * DSL helpers
     * ========================= */


    public static Page Page(int index, UIElement... children) {
        Page p = new Page(index);
        for (UIElement e : children)
            p.add(e);
        return p;
    }

    public static Column Column(int spacing, UIElement... children) {
        Column c = new Column(spacing);
        for (UIElement e : children)
            c.add(e);
        return c;
    }


    public static Label Label(String text) {
        return new Label(text);
    }


    public static Button Button(String caption, int id) {
        return new Button(caption, id);
    }

    public static Image Image(int gumpPicId) {
        return new Image(gumpPicId);
    }

    public static Image Image(int gumpPicId, int hue) {
        return new Image(gumpPicId, hue);
    }

    public static Panel Panel(int width, int height, UIElement content) {
        return new Panel(width, height, content);
    }

    public static Panel Panel(int width, int height, int backgroundGumpPicId, UIElement content) {
        return new Panel(width, height, backgroundGumpPicId, content);
    }

    public static Panel Panel(int width, int height, int backgroundGumpPicId, int hue, UIElement content) {
        return new Panel(width, height, backgroundGumpPicId, hue, content);
    }

    public static Row Row(UIElement... children) {
        return new Row(List.of(children));
    }

    public static Row Row(int gap, UIElement... children) {
        return new Row(gap, List.of(children));
    }

    public static FlexForm Form(UIElement... children) {
        return new FlexForm(12, List.of(children));
    }

    public static FormField Field(UIElement label, UIElement field) {
        return FormField.stacked(label, field);
    }

    public static FormField InlineField(UIElement label, UIElement field) {
        return FormField.inline(label, field);
    }

    public static TextField TextField(int entryId, int width) {
        return new TextField(entryId, width);
    }

    public static TextField TextField(int entryId, int width, int height) {
        return new TextField(entryId, width, height);
    }

    public static Radio Radio(
            int uncheckedId,
            int checkedId,
            int switchId
    ) {
        return new Radio(uncheckedId, checkedId, switchId);
    }

    public static Radio Radio(
            int uncheckedId,
            int checkedId,
            int switchId,
            boolean checked
    ) {
        return new Radio(uncheckedId, checkedId, switchId, checked);
    }

    public static UIElement ItemIcon(int artId) {
        return new ItemIcon(artId);
    }

    public static UIElement ItemIcon(int artId, int hue) {
        return new ItemIcon(artId, hue);
    }

    public static UIElement ImageButton(
            int artId,
            int upId,
            int downId,
            int buttonId
    ) {
        return new ImageButton(artId, upId, downId, buttonId);
    }

    public static UIElement Checkbox(
            int uncheckedId,
            int checkedId,
            int switchId
    ) {
        return new Checkbox(uncheckedId, checkedId, switchId, false);
    }

    public static UIElement Checkbox(
            int uncheckedId,
            int checkedId,
            int switchId,
            boolean checked
    ) {
        return new Checkbox(uncheckedId, checkedId, switchId, checked);
    }

    public static UIElement TextArea(int entryId, int width, int height) {
        return new TextArea(entryId, width, height);
    }

    public static UIElement Divider(int width) {
        return new Divider(width);
    }

    public static UIElement ItemSlot(int size, UIElement content) {
        return new ItemSlot(size, content);
    }
}
