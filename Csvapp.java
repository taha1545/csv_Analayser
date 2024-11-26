import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.util.ArrayList;
import java.util.stream.Collectors;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Csvapp {
    //component
    private App csvapp;
    private JTextArea Filename;
    private JButton selectButton;
    private JButton submitButton;
    private JTextArea textArea;
    private File selectedFile;
    private  JPanel badia;
    private JPanel namepanel ;
    private  JTextArea searchbyname;
    private  JButton searchbutton;
    private ArrayList<String> customerNames = new ArrayList<>();
    private JPopupMenu suggestionPopup = new JPopupMenu();

    public Csvapp(){

       csvapp =new App();
        // Create component
        Filename =new JTextArea();
        Filename.setEditable(false);
        Filename.setPreferredSize(new Dimension(450, 20));
        selectButton = new JButton("Select File");
        submitButton = new JButton("Analyse");
        submitButton.setPreferredSize(new Dimension(150,35));
        textArea = new JTextArea();
        textArea.setEditable(false);
         // select file panel and analyse button
        badia = new JPanel();
        badia.setLayout(new BorderLayout());
        JPanel Search = new JPanel();
        Search.add(new JLabel(" Csv File :"));
        Search.add(Filename);
        Search.add(selectButton);
        JPanel Submit = new JPanel();
        Submit.add(submitButton);
        badia.add(Search,BorderLayout.CENTER);
        badia.add(Submit,BorderLayout.SOUTH);
        csvapp.add(badia, BorderLayout.NORTH);
       csvapp.add(new JScrollPane(textArea), BorderLayout.CENTER);
        // action ta3 select file
        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectFile();
            }
        });
        //fonction fiha action ta3 analyse button
          submitfunction();
        //panel ta3 search b name jdida
        namepanel =new JPanel();
        searchbyname =new JTextArea();
        searchbyname.setEditable(true);
        searchbyname.setPreferredSize(new Dimension(450, 20));

        searchbyname.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                showSuggestions();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                showSuggestions();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                showSuggestions();
            }
        });
         searchbutton = new JButton("search");
        // action ta3 search
               searchname();
        namepanel.add(new JLabel(" search name :"));
        namepanel.add(searchbyname);
        namepanel.add(searchbutton);

        //end
        csvapp.setVisible(true);
    }

    // 9a3 les fonction
    private void selectFile() {
        // Create file chooser
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select CSV file");
        fileChooser.setPreferredSize(new Dimension(800, 500));

        // Customize the UI and set background color
        UIManager.put("FileChooser.background", Color.LIGHT_GRAY);

        // Set a filter for CSV files
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");
        fileChooser.setFileFilter(filter);

        // Map ESC key to cancel the selection
        InputMap inputMap = (InputMap) UIManager.get("FileChooser.ancestorInputMap");
        if (inputMap != null) {
            inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "cancelSelection");
        }

        // Show the file chooser dialog
        int returnValue = fileChooser.showOpenDialog(csvapp);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            Filename.setText(selectedFile.getName());
        }
    }
     private static HashMap<String, Integer> submitFile(String[] typesArray) {
         HashMap<String, Integer> typesCount = new HashMap<>();

         // Iterate over the array and count occurrences
         for (String type : typesArray) {
             // If the type already exists in the map, increment its count
             if (typesCount.containsKey(type)) {
                 typesCount.put(type, typesCount.get(type) + 1);
             } else {
                 // Otherwise, add the type to the map with a count of 1
                 typesCount.put(type, 1);
             }
         }

         return typesCount;
     }
    private void showsearch() {
     badia.setVisible(false);
        namepanel.setPreferredSize(new Dimension(200,65));
        csvapp.add(namepanel,BorderLayout.NORTH);
        Loadcustomer();
    }
    private void submitfunction(){
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedFile != null) {
                    showsearch();
                    try {
                        ArrayList<String> telecomTypes = new ArrayList<>();
                        BufferedReader br = new BufferedReader(new FileReader(selectedFile));
                        String line;
                        boolean isFirstLine = true;
                        while ((line = br.readLine()) != null) {
                            if (isFirstLine) {
                                isFirstLine = false; // Skip header
                                continue;
                            }
                            String[] values = line.split(";"); // Assuming CSV uses commas
                            String telecomType = values[3]; // TELECOM_TYPE is the 4th column
                            telecomTypes.add(telecomType);
                        }

                        // Convert to array and pass to submitFile
                        String[] telecomTypeArray = telecomTypes.toArray(new String[0]);
                        HashMap<String, Integer> result = submitFile(telecomTypeArray);

                        // Display the result
                        Font font = new Font("Arial", Font.PLAIN, 24); // Font name, style (PLAIN, BOLD, ITALIC), size
                        textArea.setFont(font);
                        textArea.setText(" les types des Telecome : \n"); // Clear previous results
                         font = new Font("Arial", Font.PLAIN, 18); // Font name, style (PLAIN, BOLD, ITALIC), size
                        textArea.setFont(font);
                        for (String key : result.keySet()) {

                            textArea.append( "     "+key + " " + result.get(key) + "\n");
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a CSV file first.");
                }
            }
        });
    }
    private void searchname() {
        searchbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedFile != null && !searchbyname.getText().isEmpty()) {
                    String searchName = searchbyname.getText().trim();
                    try {
                        ArrayList<String[]> matchedRows = new ArrayList<>(); // Store rows where the name matches
                        BufferedReader br = new BufferedReader(new FileReader(selectedFile));
                        String line;
                        boolean isFirstLine = true;

                        while ((line = br.readLine()) != null) {
                            if (isFirstLine) {
                                isFirstLine = false; // Skip header
                                continue;
                            }

                            String[] values = line.split(";"); // Assuming CSV uses semicolons
                            String customerName = values[0].trim(); // The customer name is in the first column

                            if (customerName.toLowerCase().contains(searchName.toLowerCase())) {
                                matchedRows.add(values); // Add the matched row for further processing
                            }
                        }

                        // Display the result
                        if (!matchedRows.isEmpty()) {
                            HashMap<String, ArrayList<String>> telecomInfo = new HashMap<>(); // To store telecom types and associated phone numbers
                            for (String[] row : matchedRows) {
                                String telecomType = row[3]; // Assuming TELECOM_TYPE is in the 4th column
                                String phoneNumber = row[6]; // Assuming SERVICE_NUMBER (phone number) is in the 7th column

                                if (!telecomInfo.containsKey(telecomType)) {
                                    telecomInfo.put(telecomType, new ArrayList<>());
                                }
                                telecomInfo.get(telecomType).add(phoneNumber); // Add phone number to corresponding telecom type
                            }

                            // Display the collected information
                            Font font = new Font("Arial", Font.PLAIN, 16); // Font for the results
                            textArea.setFont(font);
                            textArea.setText("Results for Customer: " + searchName + "\n\n");
                            for (String telecomType : telecomInfo.keySet()) {
                                textArea.append("Telecom Type: " + telecomType + "\n");
                                ArrayList<String> phoneNumbers = telecomInfo.get(telecomType);
                                textArea.append("Total Numbers for this Type: " + phoneNumbers.size() + "\n\n");
                            }
                        } else {
                            textArea.setText("No results found for customer: " + searchName);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a CSV file and enter a customer name.");
                }
            }
        });
    }
    private void Loadcustomer(){
        try{
        BufferedReader all = new BufferedReader(new FileReader(selectedFile));
        String line;
        boolean isFirstLine = true;
        while ((line = all.readLine()) != null) {
            if (isFirstLine) {
                isFirstLine = false; // Skip header
                continue;
            }
            String[] values = line.split(";"); // Assuming CSV uses commas
            String cusname = values[0]; // TELECOM_TYPE is the 4th column
            customerNames.add(cusname);
        }}catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    private void showSuggestions() {
        String input = searchbyname.getText();

        // Remove previous suggestions
        suggestionPopup.setVisible(false);
        suggestionPopup.removeAll();




        if (!input.isEmpty()) {
            ArrayList<String> suggestions = (ArrayList<String>) customerNames.stream()
                    .filter(name -> name.toLowerCase().startsWith(input.toLowerCase()))
                    .distinct()
                    .collect(Collectors.toList());

            for (String suggestion : suggestions) {
                JMenuItem item = new JMenuItem(suggestion);
                item.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        searchbyname.setText(suggestion); // Set the selected suggestion
                        suggestionPopup.setVisible(false); // Hide suggestions
                    }
                });
                suggestionPopup.add(item);
            }


            // Add the scroll pane to the popup
            if (suggestionPopup.getComponentCount() > 0) {
                // Show the popup directly under the search field
                suggestionPopup.show(searchbyname, 100, searchbyname.getHeight());
                searchbyname.requestFocusInWindow();  // Keep focus on the text field
            }
        }

    }}
    //

