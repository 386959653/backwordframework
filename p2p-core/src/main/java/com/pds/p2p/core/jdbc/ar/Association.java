package com.pds.p2p.core.jdbc.ar;

import java.util.Map;

import com.pds.p2p.core.jdbc.ar.ex.UndefinedAssociationException;
import com.pds.p2p.core.utils.StringUtils;

/**
 * 表之间的关联。
 *
 * @author redraiment
 * @since 1.0
 */
public final class Association {
    private final Map<String, Association> relations;
    private final boolean onlyOne;
    private final boolean ancestor;

    private Association assoc;

    String target;
    String targetKey;
    String sourceKey;

    Association(Map<String, Association> relations, String name, boolean onlyOne, boolean ancestor) {
        this.relations = relations;
        this.onlyOne = onlyOne;
        this.ancestor = ancestor;
        this.target = StringUtils.toUnderScoreCase(name);
        this.targetKey = this.target.concat("_id");
        this.assoc = null;
        this.sourceKey = "id";
    }

    public boolean isOnlyOneResult() {
        return onlyOne;
    }

    public boolean isAncestor() {
        return ancestor;
    }

    public boolean isCross() {
        return assoc != null;
    }

    public Association targetKey(String key) {
        this.targetKey = StringUtils.toUnderScoreCase(key);
        return this;
    }

    public Association sourceKey(String key) {
        this.sourceKey = StringUtils.toUnderScoreCase(key);
        return this;
    }

    public Association in(String table) {
        this.target = table;
        return this;
    }

    public Association through(String assoc) {
        assoc = DB.parseKeyParameter(assoc);
        if (relations.containsKey(assoc)) {
            this.assoc = relations.get(assoc);
        } else {
            throw new UndefinedAssociationException(assoc);
        }
        return this;
    }

    String assoc(String source, Number id) {
        String template = isAncestor() ? "%1$s on %2$s.%3$s = %1$s.%4$s" : "%1$s on %1$s.%3$s = %2$s.id";
        if (isCross()) {
            return String.format(template, assoc.target, target, targetKey).concat(" join ")
                    .concat(assoc.assoc(source, id));
        } else {
            return String.format(template.concat(" and %1$s.id = %5$d"), source, target, targetKey, sourceKey, id);
        }
    }
}
