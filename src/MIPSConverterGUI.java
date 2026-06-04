import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class MIPSConverterGUI {

    private static final Map<String, String> registerMap = new HashMap<>();

    static {
        registerMap.put("$zero", "00000");
        registerMap.put("$t0", "01000"); registerMap.put("$t1", "01001");
        registerMap.put("$t2", "01010"); registerMap.put("$t3", "01011");
        registerMap.put("$t4", "01100"); registerMap.put("$t5", "01101");
        registerMap.put("$t6", "01110"); registerMap.put("$t7", "01111");
        registerMap.put("$s0", "10000"); registerMap.put("$s1", "10001");
        registerMap.put("$s2", "10010"); registerMap.put("$s3", "10011");
        registerMap.put("$s4", "10100"); registerMap.put("$s5", "10101");
        registerMap.put("$s6", "10110"); registerMap.put("$s7", "10111");
        registerMap.put("$a0", "00100"); registerMap.put("$a1", "00101");
        registerMap.put("$a2", "00110"); registerMap.put("$a3", "00111");
        registerMap.put("$v0", "00010"); registerMap.put("$v1", "00011");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MIPSConverterGUI::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("MIPS to Machine Code Converter");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 600);
        frame.setLayout(new BorderLayout());

        JLabel title = new JLabel("MIPS to Machine Code Converter", JLabel.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 22));
        frame.add(title, BorderLayout.NORTH);

        JPanel rowPanel = new JPanel();
        rowPanel.setLayout(new BoxLayout(rowPanel, BoxLayout.X_AXIS));
        rowPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextArea inputArea = new JTextArea();
        inputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane inputScroll = new JScrollPane(inputArea);
        inputScroll.setBorder(BorderFactory.createTitledBorder("MIPS Assembly"));

        JTextArea addressArea = new JTextArea();
        addressArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        addressArea.setEditable(false);
        JScrollPane addressScroll = new JScrollPane(addressArea);
        addressScroll.setBorder(BorderFactory.createTitledBorder("Address"));

        JTextArea machineArea = new JTextArea();
        machineArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        machineArea.setEditable(false);
        JScrollPane machineScroll = new JScrollPane(machineArea);
        machineScroll.setBorder(BorderFactory.createTitledBorder("Machine Code"));

        Dimension boxSize = new Dimension(300, 450);
        inputScroll.setPreferredSize(boxSize);
        addressScroll.setPreferredSize(boxSize);
        machineScroll.setPreferredSize(boxSize);

        JButton convertButton = new JButton("Convert");
        convertButton.setPreferredSize(new Dimension(90, 30));
        JPanel convertPanel = new JPanel(new GridBagLayout());
        convertPanel.add(convertButton);
        convertPanel.setMaximumSize(new Dimension(100, Integer.MAX_VALUE));
        convertPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        rowPanel.add(inputScroll);
        rowPanel.add(convertPanel);
        rowPanel.add(addressScroll);
        rowPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        rowPanel.add(machineScroll);

        frame.add(rowPanel, BorderLayout.CENTER);

        convertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] lines = inputArea.getText().split("\\n");
                Map<String, Integer> labelMap = new HashMap<>();
                List<String> instructions = new ArrayList<>();

                int currentAddress = 0x00400000;
                int lineAddress = currentAddress;

                for (String line : lines) {
                    line = line.trim();
                    if (line.isEmpty()) continue;
                    if (line.contains(":")) {
                        String[] split = line.split(":");
                        String label = split[0].trim();
                        labelMap.put(label, lineAddress);
                        if (split.length > 1 && !split[1].trim().isEmpty()) {
                            instructions.add(split[1].trim());
                            lineAddress += 4;
                        }
                    } else {
                        instructions.add(line);
                        lineAddress += 4;
                    }
                }

                StringBuilder addressResult = new StringBuilder();
                StringBuilder codeResult = new StringBuilder();
                lineAddress = currentAddress;

                for (int i = 0; i < instructions.size(); i++) {
                    String inst = instructions.get(i);
                    String binary = convertToBinary(inst, labelMap, i, currentAddress);
                    String hex = binaryToHex(binary);
                    addressResult.append(String.format("0x%08X\n", lineAddress));
                    codeResult.append(hex).append("\n");
                    lineAddress += 4;
                }

                addressArea.setText(addressResult.toString());
                machineArea.setText(codeResult.toString());
            }
        });

        frame.setVisible(true);
    }

    private static String convertToBinary(String instruction, Map<String, Integer> labelMap, int lineIndex, int baseAddress) {
        instruction = instruction.replace(",", "").replace("(", " ").replace(")", "");
        String[] parts = instruction.trim().split("\\s+");

        try {
            switch (parts[0]) {
                // R-type: rd, rs, rt
                case "add":
                case "sub":
                case "and":
                case "or": {
                    String rd = getReg(parts[1]);
                    String rs = getReg(parts[2]);
                    String rt = getReg(parts[3]);
                    String funct = switch (parts[0]) {
                        case "add" -> "100000";
                        case "sub" -> "100010";
                        case "and" -> "100100";
                        case "or"  -> "100101";
                        default -> "000000";
                    };
                    return "000000" + rs + rt + rd + "00000" + funct;
                }

                // Shift: rd, rt, shamt
                case "sll":
                case "srl": {
                    String rd = getReg(parts[1]);
                    String rt = getReg(parts[2]);
                    String shamt = toShift(parts[3]);
                    String funct = parts[0].equals("sll") ? "000000" : "000010";
                    return "00000000000" + rt + rd + shamt + funct;
                }

                // Variable shift: rd, rt, rs
                case "sllv":
                case "srlv": {
                    String rd = getReg(parts[1]);
                    String rt = getReg(parts[2]);
                    String rs = getReg(parts[3]);
                    String funct = parts[0].equals("sllv") ? "000100" : "000110";
                    return "000000" + rs + rt + rd + "00000" + funct;
                }

                // I-type: rt, rs, immediate
                case "addi":
                case "andi": {
                    String rt = getReg(parts[1]);
                    String rs = getReg(parts[2]);
                    String imm = toImm(parts[3]);
                    String opcode = parts[0].equals("addi") ? "001000" : "001100";
                    return opcode + rs + rt + imm;
                }

                case "lw":
                case "sw": {
                    String rt = getReg(parts[1]);
                    String offset = toImm(parts[2]);
                    String rs = getReg(parts[3]);
                    String opcode = parts[0].equals("lw") ? "100011" : "101011";
                    return opcode + rs + rt + offset;
                }

                // Branch: beq, bne 
                case "beq":
                case "bne": {
                    String rs = getReg(parts[1]);
                    String rt = getReg(parts[2]);
                    int current = baseAddress + lineIndex * 4 + 4;
                    int target = labelMap.getOrDefault(parts[3], 0);
                    int offset = (target - current) / 4;
                    String imm = toImm(String.valueOf(offset));
                    String opcode = parts[0].equals("beq") ? "000100" : "000101";
                    return opcode + rs + rt + imm;
                }

                // Jump and JAL
                case "j":
                case "jal": {
                    int target = labelMap.getOrDefault(parts[1], 0);
                    int wordAddr = (target >> 2) & 0x03FFFFFF;
                    String address = String.format("%26s", Integer.toBinaryString(wordAddr)).replace(' ', '0');
                    return (parts[0].equals("j") ? "000010" : "000011") + address;
                }
            }
        } catch (Exception e) {
            return "00000000000000000000000000000000";
        }

        return "00000000000000000000000000000000";
    }

    private static String getReg(String reg) {
        return registerMap.getOrDefault(reg, "00000");
    }

    private static String toImm(String val) {
        int imm = Integer.parseInt(val);
        return String.format("%16s", Integer.toBinaryString(imm & 0xFFFF)).replace(' ', '0');
    }

    private static String toShift(String val) {
        int imm = Integer.parseInt(val);
        return String.format("%05d", Integer.parseInt(Integer.toBinaryString(imm & 0x1F)));
    }

    private static String binaryToHex(String binary) {
        if (!binary.matches("[01]{32}")) return "Invalid";
        return "0x" + String.format("%08X", Long.parseLong(binary, 2));
    }
}
