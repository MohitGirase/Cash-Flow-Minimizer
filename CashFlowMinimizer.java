package CashFlowMinimizer;

import java.util.*;

class CashFlowMinimizer {

    public static int getMinIndex(Bank[] listOfNetAmounts, int numBanks) {
        int min = Integer.MAX_VALUE, minIndex = -1;
        for (int i = 0; i < numBanks; i++) {
            if (listOfNetAmounts[i].netAmount == 0) continue;

            if (listOfNetAmounts[i].netAmount < min) {
                minIndex = i;
                min = listOfNetAmounts[i].netAmount;
            }
        }
        return minIndex;
    }

    public static int getSimpleMaxIndex(Bank[] listOfNetAmounts, int numBanks) {
        int max = Integer.MIN_VALUE, maxIndex = -1;
        for (int i = 0; i < numBanks; i++) {
            if (listOfNetAmounts[i].netAmount == 0) continue;

            if (listOfNetAmounts[i].netAmount > max) {
                maxIndex = i;
                max = listOfNetAmounts[i].netAmount;
            }
        }
        return maxIndex;
    }

    public static Pair getMaxIndex(Bank[] listOfNetAmounts, int numBanks, int minIndex, Bank[] input, int maxNumTypes) {
        int max = Integer.MIN_VALUE;
        int maxIndex = -1;
        String matchingType = "";

        for (int i = 0; i < numBanks; i++) {
            if (listOfNetAmounts[i].netAmount == 0) continue;
            if (listOfNetAmounts[i].netAmount < 0) continue;

            Set<String> commonTypes = new HashSet<>(listOfNetAmounts[minIndex].types);
            commonTypes.retainAll(listOfNetAmounts[i].types);

            if (!commonTypes.isEmpty() && max < listOfNetAmounts[i].netAmount) {
                max = listOfNetAmounts[i].netAmount;
                maxIndex = i;
                matchingType = commonTypes.iterator().next();
            }
        }

        return new Pair(maxIndex, matchingType);
    }

    public static void printAns(List<List<Pair>> ansGraph, int numBanks, Bank[] input) {
        System.out.println("\nThe transactions for minimum cash flow are as follows : \n\n");
        for (int i = 0; i < numBanks; i++) {
            for (int j = 0; j < numBanks; j++) {
                if (i == j) continue;

                if (ansGraph.get(i).get(j).amount != 0 && ansGraph.get(j).get(i).amount != 0) {
                    if (ansGraph.get(i).get(j).amount == ansGraph.get(j).get(i).amount) {
                        ansGraph.get(i).get(j).amount = 0;
                        ansGraph.get(j).get(i).amount = 0;
                    } else if (ansGraph.get(i).get(j).amount > ansGraph.get(j).get(i).amount) {
                        ansGraph.get(i).get(j).amount -= ansGraph.get(j).get(i).amount;
                        ansGraph.get(j).get(i).amount = 0;
                        System.out.println(input[i].name + " pays Rs " + ansGraph.get(i).get(j).amount + " to " + input[j].name + " via " + ansGraph.get(i).get(j).type);
                    } else {
                        ansGraph.get(j).get(i).amount -= ansGraph.get(i).get(j).amount;
                        ansGraph.get(i).get(j).amount = 0;
                        System.out.println(input[j].name + " pays Rs " + ansGraph.get(j).get(i).amount + " to " + input[i].name + " via " + ansGraph.get(j).get(i).type);
                    }
                } else if (ansGraph.get(i).get(j).amount != 0) {
                    System.out.println(input[i].name + " pays Rs " + ansGraph.get(i).get(j).amount + " to " + input[j].name + " via " + ansGraph.get(i).get(j).type);
                } else if (ansGraph.get(j).get(i).amount != 0) {
                    System.out.println(input[j].name + " pays Rs " + ansGraph.get(j).get(i).amount + " to " + input[i].name + " via " + ansGraph.get(j).get(i).type);
                }
                ansGraph.get(i).get(j).amount = 0;
                ansGraph.get(j).get(i).amount = 0;
            }
        }
        System.out.println("\n");
    }

    public static void minimizeCashFlow(int numBanks, Bank[] input, Map<String, Integer> indexOf, int numTransactions, int[][] graph, int maxNumTypes) {
        Bank[] listOfNetAmounts = new Bank[numBanks];
        for (int b = 0; b < numBanks; b++) {
            listOfNetAmounts[b] = new Bank();
            listOfNetAmounts[b].name = input[b].name;
            listOfNetAmounts[b].types = input[b].types;

            int amount = 0;
            for (int i = 0; i < numBanks; i++) {
                amount += graph[i][b];
            }
            for (int j = 0; j < numBanks; j++) {
                amount -= graph[b][j];
            }
            listOfNetAmounts[b].netAmount = amount;
        }

        List<List<Pair>> ansGraph = new ArrayList<>(numBanks);
        for (int i = 0; i < numBanks; i++) {
            List<Pair> row = new ArrayList<>(Collections.nCopies(numBanks, new Pair(0, "")));
            ansGraph.add(row);
        }

        int numZeroNetAmounts = 0;
        for (Bank bank : listOfNetAmounts) {
            if (bank.netAmount == 0) numZeroNetAmounts++;
        }

        while (numZeroNetAmounts != numBanks) {
            int minIndex = getMinIndex(listOfNetAmounts, numBanks);
            Pair maxAns = getMaxIndex(listOfNetAmounts, numBanks, minIndex, input, maxNumTypes);
            int maxIndex = maxAns.index;

            if (maxIndex == -1) {
                ansGraph.get(minIndex).get(0).amount += Math.abs(listOfNetAmounts[minIndex].netAmount);
                ansGraph.get(minIndex).get(0).type = input[minIndex].types.iterator().next();

                int simpleMaxIndex = getSimpleMaxIndex(listOfNetAmounts, numBanks);
                ansGraph.get(0).get(simpleMaxIndex).amount += Math.abs(listOfNetAmounts[minIndex].netAmount);
                ansGraph.get(0).get(simpleMaxIndex).type = input[simpleMaxIndex].types.iterator().next();

                listOfNetAmounts[simpleMaxIndex].netAmount += listOfNetAmounts[minIndex].netAmount;
                listOfNetAmounts[minIndex].netAmount = 0;

                if (listOfNetAmounts[minIndex].netAmount == 0) numZeroNetAmounts++;
                if (listOfNetAmounts[simpleMaxIndex].netAmount == 0) numZeroNetAmounts++;
            } else {
                int transactionAmount = Math.min(Math.abs(listOfNetAmounts[minIndex].netAmount), listOfNetAmounts[maxIndex].netAmount);

                ansGraph.get(minIndex).get(maxIndex).amount += transactionAmount;
                ansGraph.get(minIndex).get(maxIndex).type = maxAns.type;

                listOfNetAmounts[minIndex].netAmount += transactionAmount;
                listOfNetAmounts[maxIndex].netAmount -= transactionAmount;

                if (listOfNetAmounts[minIndex].netAmount == 0) numZeroNetAmounts++;
                if (listOfNetAmounts[maxIndex].netAmount == 0) numZeroNetAmounts++;
            }
        }

        printAns(ansGraph, numBanks, input);
    }
}

/*
5
World_Bank 2 Google_Pay PayTM
Bank_B 1 Google_Pay
Bank_C 1 Google_Pay
Bank_D 1 PayTM
Bank_E 1 PayTM
4
Bank_B World_Bank 300
Bank_C World_Bank 700
Bank_D Bank_B 500
Bank_E Bank_B 500
 */