package org.commonmark.ext.gfm.tables.internal;

import org.commonmark.ext.gfm.tables.*;
import org.commonmark.node.Node;
import org.commonmark.renderer.html.HtmlNodeRendererContext;
import org.commonmark.renderer.html.HtmlWriter;

import java.util.Collections;
import java.util.Map;

public class TableHtmlNodeRenderer extends TableNodeRenderer {

    private final HtmlWriter htmlWriter;
    private final HtmlNodeRendererContext context;

    public TableHtmlNodeRenderer(HtmlNodeRendererContext context) {
        this.htmlWriter = context.getWriter();
        this.context = context;
    }

    protected void renderBlock(TableBlock tableBlock) {
        htmlWriter.line();
        htmlWriter.tag("table", getAttributes(tableBlock, "table"));
        renderChildren(tableBlock);
        htmlWriter.tag("/table");
        htmlWriter.line();
    }

    protected void renderHead(TableHead tableHead) {
        htmlWriter.line();
        htmlWriter.tag("thead", getAttributes(tableHead, "thead"));
        renderChildren(tableHead);
        htmlWriter.tag("/thead");
        htmlWriter.line();
    }

    protected void renderBody(TableBody tableBody) {
        // gfm-spec: If there are no rows in the body, no `<tbody>` is generated in HTML output
        if (tableBody.getFirstChild() == null) {
            return;
        }
        htmlWriter.line();
        htmlWriter.tag("tbody", getAttributes(tableBody, "tbody"));
        renderChildren(tableBody);
        htmlWriter.tag("/tbody");
        htmlWriter.line();
    }

    protected void renderRow(TableRow tableRow) {
        htmlWriter.line();
        htmlWriter.tag("tr", getAttributes(tableRow, "tr"));
        renderChildren(tableRow);
        htmlWriter.tag("/tr");
        htmlWriter.line();
    }

    protected void renderCell(TableCell tableCell) {
        String tagName = tableCell.isHeader() ? "th" : "td";
        htmlWriter.line();
        htmlWriter.tag(tagName, getCellAttributes(tableCell, tagName));
        renderChildren(tableCell);
        htmlWriter.tag("/" + tagName);
        htmlWriter.line();
    }

    private Map<String, String> getAttributes(Node node, String tagName) {
        return context.extendAttributes(node, tagName, Collections.<String, String>emptyMap());
    }

    private Map<String, String> getCellAttributes(TableCell tableCell, String tagName) {
        if (tableCell.getAlignment() != null) {
            return context.extendAttributes(tableCell, tagName, Collections.singletonMap("align", getAlignValue(tableCell.getAlignment())));
        } else {
            return context.extendAttributes(tableCell, tagName, Collections.<String, String>emptyMap());
        }
    }

    private static String getAlignValue(TableCell.Alignment alignment) {
        switch (alignment) {
            case LEFT:
                return "left";
            case CENTER:
                return "center";
            case RIGHT:
                return "right";
        }
        throw new IllegalStateException("Unknown alignment: " + alignment);
    }

    private void renderChildren(Node parent) {
        Node node = parent.getFirstChild();
        while (node != null) {
            Node next = node.getNext();
            context.render(node);
            node = next;
        }
    }
}
