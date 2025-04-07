package CashFlowMinimizer;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static CashFlowMinimizer.CashFlowMinimizer.minimizeCashFlow;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n\t\t\t\t********************* Welcome to CASH FLOW MINIMIZER SYSTEM ***********************\n\n\n");
        System.out.println("This system minimizes the number of transactions among multiple banks in the different corners of the world that use different modes of payment. There is one world bank (with all payment modes) to act as an intermediary between banks that have no common mode of payment. \n\n");
        System.out.println("Enter the number of banks participating in the transactions.");
        int numBanks = sc.nextInt();

        Bank[] input = new Bank[numBanks];
        Map<String, Integer> indexOf = new HashMap<>();

        System.out.println("Enter the details of the banks and transactions as stated:");
        System.out.println("Bank name, number of payment modes it has, and the payment modes.");
        System.out.println("Bank name and payment modes should not contain spaces");

        int maxNumTypes = 0;
        for (int i = 0; i < numBanks; i++) {
            if (i == 0) {
                System.out.print("World Bank : ");
            } else {
                System.out.print("Bank " + i + " : ");
            }

            input[i] = new Bank();
            input[i].name = sc.next();
            indexOf.put(input[i].name, i);

            int numTypes = sc.nextInt();
            if (i == 0) maxNumTypes = numTypes;

            for (int j = 0; j < numTypes; j++) {
                String type = sc.next();
                input[i].types.add(type);
            }
        }

        System.out.println("Enter number of transactions.");
        int numTransactions = sc.nextInt();

        int[][] graph = new int[numBanks][numBanks];

        System.out.println("Enter the details of each transaction as stated:");
        System.out.println("Debtor Bank, creditor Bank, and amount");
        System.out.println("The transactions can be in any order");
        for (int i = 0; i < numTransactions; i++) {
            System.out.print(i + " th transaction : ");
            String debtor = sc.next();
            String creditor = sc.next();
            int amount = sc.nextInt();

            graph[indexOf.get(debtor)][indexOf.get(creditor)] = amount;
        }

        minimizeCashFlow(numBanks, input, indexOf, numTransactions, graph, maxNumTypes);
    }
}
