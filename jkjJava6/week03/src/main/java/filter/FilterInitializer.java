package filter;


import context.MyContext;

import java.util.ArrayList;
import java.util.List;

public class FilterInitializer {
    private static final FilterInitializer filterInitializer = new FilterInitializer();
    List<FilterNode> chains = new ArrayList<>();

    public static FilterInitializer getInstance() {
        return filterInitializer;
    }

    private FilterInitializer() {
        chains.add(new PreFilter());
        chains.add(new RouteFilter());
        chains.add(new PostFilter());
    }

    public void execute(MyContext ctx) {
        for (FilterNode node : this.chains) {
            node.doNext(ctx);
        }
    }
}
