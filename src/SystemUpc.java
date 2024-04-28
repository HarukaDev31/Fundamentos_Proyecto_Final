import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class SystemUpc {
    private JFrame mainContainer;
    private JPanel itemContainer;
    private JMenuBar menuBar;
    private JMenu file;
    private JMenuItem fmi1;

    public SystemUpc() {
        initSystem();
    }

    public void initSystem() {
        initBase();
        initMenu();

    }

    public void initBase() {
        // init main container
        this.mainContainer = new JFrame("System Upc");
        this.mainContainer.setSize(500, 500);
        this.mainContainer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // init item container
        this.itemContainer = new JPanel();
        this.itemContainer.setSize(300, 300);
        this.itemContainer.setLayout(new GridBagLayout());
        this.mainContainer.add(this.itemContainer);
    }

    public void initMenu() {
        // Create MenuBar
        this.menuBar = new JMenuBar();
        // Create Menu called File
        this.file = new JMenu("File");
        // Add file attr to menu bar
        this.menuBar.add(this.file);
        // Set MenuBar attr as container menubar
        this.mainContainer.setJMenuBar(this.menuBar);
        /// Create Menu Item and add it to menu
        this.fmi1 = new JMenuItem("Importar Empleados");
        this.file.add(this.fmi1);
        this.fmi1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openFileSelector();
            }
        });
        this.mainContainer.setVisible(true);

    }

    private void openFileSelector() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(mainContainer);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            System.out.println("Selected file: " + selectedFile.getName());
        }
    }

}