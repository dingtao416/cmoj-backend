package com.cm.cmoj.utils.markdown.heimu.internal;

import com.cm.cmoj.utils.markdown.heimu.Heimu;
import org.commonmark.node.Node;
import org.commonmark.renderer.NodeRenderer;

import java.util.Collections;
import java.util.Set;

abstract class AbstractHeimuNodeRenderer implements NodeRenderer {
    @Override
    public Set<Class<? extends Node>> getNodeTypes() {
        return Collections.<Class<? extends Node>>singleton(Heimu.class);
    }
}
