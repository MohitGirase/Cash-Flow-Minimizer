package CashFlowMinimizer;

import java.util.HashSet;
import java.util.Set;

class Bank {
    String name;
    int netAmount;
    Set<String> types;

    public Bank() {
        types = new HashSet<>();
    }
}
