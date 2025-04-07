package CashFlowMinimizer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class CashFlowMinimizerGUI extends JFrame {

    private final JTextField numBanksField;
    private final JTextField numTransactionsField;
    private final JTextArea bankDetailsArea;
    private final JTextArea transactionsArea;
    private final JTextArea resultArea;

    public CashFlowMinimizerGUI() {
        setTitle("Cash Flow Minimizer");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(4, 2));

        inputPanel.add(new JLabel("Number of Banks:"));
        numBanksField = new JTextField();
        inputPanel.add(numBanksField);

        inputPanel.add(new JLabel("Number of Transactions:"));
        numTransactionsField = new JTextField();
        inputPanel.add(numTransactionsField);

        inputPanel.add(new JLabel("Bank Details (name numModes modes...):"));
        bankDetailsArea = new JTextArea(5, 30);
        inputPanel.add(new JScrollPane(bankDetailsArea));

        inputPanel.add(new JLabel("Transactions (debtor creditor amount):"));
        transactionsArea = new JTextArea(5, 30);
        inputPanel.add(new JScrollPane(transactionsArea));

        JButton calculateButton = new JButton("Calculate");
        calculateButton.addActionListener(new CalculateButtonListener());
        inputPanel.add(calculateButton);

        add(inputPanel, BorderLayout.NORTH);

        resultArea = new JTextArea();
        resultArea.setEditable(false);
        add(new JScrollPane(resultArea), BorderLayout.CENTER);
    }

    private class CalculateButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                int numBanks = Integer.parseInt(numBanksField.getText().trim());
                int numTransactions = Integer.parseInt(numTransactionsField.getText().trim());

                Bank[] banks = new Bank[numBanks];
                Map<String, Integer> indexOf = new HashMap<>();

                String[] bankLines = bankDetailsArea.getText().trim().split("\n");
                for (int i = 0; i < numBanks; i++) {
                    String[] details = bankLines[i].split(" ");
                    Bank bank = new Bank();
                    bank.name = details[0];
                    indexOf.put(bank.name, i);
                    int numTypes = Integer.parseInt(details[1]);
                    for (int j = 2; j < 2 + numTypes; j++) {
                        bank.types.add(details[j]);
                    }
                    banks[i] = bank;
                }

                int[][] graph = new int[numBanks][numBanks];
                String[] transactionLines = transactionsArea.getText().trim().split("\n");
                for (String line : transactionLines) {
                    String[] parts = line.split(" ");
                    String debtor = parts[0];
                    String creditor = parts[1];
                    int amount = Integer.parseInt(parts[2]);
                    graph[indexOf.get(debtor)][indexOf.get(creditor)] = amount;
                }

                // Call the minimizeCashFlow function and capture results
                CashFlowMinimizer.minimizeCashFlow(numBanks, banks, indexOf, numTransactions, graph, banks[0].types.size());

                // Output results to the result area
                resultArea.setText("Calculation complete. Check console for detailed output.");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(CashFlowMinimizerGUI.this, "Please enter valid numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(CashFlowMinimizerGUI.this, "Error processing input: " + ex.getMessage(), "Processing Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CashFlowMinimizerGUI().setVisible(true);
        });
    }
}
/*
class Bank {
    String name;
    int netAmount;
    Set<String> types = new HashSet<>();
}

class Pair {
    int index;
    String type;
    int amount;

    public Pair(int index, String type) {
        this.index = index;
        this.type = type;
        this.amount = 0;
    }
}
*/
