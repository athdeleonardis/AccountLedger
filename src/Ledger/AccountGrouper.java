package Ledger;

import java.util.*;

public class AccountGrouper {
    Map<String, Set<String>> childToParentMap;

    public AccountGrouper() {
        childToParentMap = new HashMap<>();
    }

    public AccountGrouper setRelationship(String group, String account) {
        if (!childToParentMap.containsKey(account))
            addChild(account);
        childToParentMap.get(account).add(group);
        return this;
    }

    private void addChild(String account) {
        Set<String> newSet = new TreeSet<>();
        newSet.add(account);
        childToParentMap.put(account, newSet);
    }

    public Set<String> getGroups(String account) {
        if (!childToParentMap.containsKey(account))
            addChild(account);
        return new TreeSet<>(childToParentMap.get(account));
    }

    public static void removeCommonAccounts(Set<String> groupsA, Set<String> groupsB) {
        Set<String> common = new TreeSet<>(groupsA);
        common.retainAll(groupsB);
        groupsA.removeAll(common);
        groupsB.removeAll(common);
    }
}
