import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.ss.usermodel.*;
import java.io.FileNotFoundException;
import org.json.*;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
/**
 * Este metodo es usado para la inicializacion de la GUI.
 * Si deseas agregar nuevos componentes se tienen que agregar en esta clase
 */
public class SystemUpc {
    private JFrame mainContainer;
    private JPanel itemContainer;
    private JMenuBar menuBar;
    private JMenu file;
    private JMenuItem fmi1;
    private JTabbedPane tabbedPane;
    private JComponent empTab, payrollTab;
    private String empJsonUrl;

    /**
     * Este es el constructor de clase, cuando se instancia un objeto de esta clase
     * se llama a esto
     *
     * @param empJsonUrl -La ruta donde estara ubicado el archivo json con la
     *                   informacion de los empleados
     */
    public SystemUpc(String empJsonUrl) {
        this.empJsonUrl = empJsonUrl;
        initSystem();
    }
    /**
     * Este metodo se encarga de inicializar la GUI y sus componentes
     * Ademas de inicializar los eventos de los componentes
     * y crear el json de empleados
     */
    public void initSystem() {
        initEmployeesFile(empJsonUrl);
        initBase();
        initMenu();
        initTabbedPane();
        this.mainContainer.setVisible(true);

    }
    /**
     * Este metodo se encarga de crear el contenedor principal y un panel donde iran
     * todos nuestros componentes
     */
    public void initBase() {
        // init main container
        this.mainContainer = new JFrame("System Upc");
        this.mainContainer.setSize(500, 500);
        this.mainContainer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // init item container
        this.itemContainer = new JPanel();
        this.itemContainer.setSize(300, 300);
        this.itemContainer.setLayout(new BorderLayout());
        this.mainContainer.add(this.itemContainer);
    }
    /**
     * Este metodo se encarga de inicializar el menu y sus submenus
     */
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

    private void initTabbedPane() {
        this.tabbedPane = new JTabbedPane();
        this.tabbedPane.setTabPlacement(JTabbedPane.TOP);
        createTab("emp.png", "Empleados", this.empTab);
        createTab("emp.png", "Boletas", this.payrollTab);
        this.itemContainer.add(this.tabbedPane);
    }

    /**
     * Este metodo crea tabs y los añade al tab que inicializado al principio
     *
     * @param iconUrl  -La ruta de la imagen que se mostrara en el tab
     * @param tabName     -El titulo que se mostrara en el tab
     * @param component -El componente que se mostrara en el tab
     */
    private void createTab(String iconUrl, String tabName, JComponent component) {
        ImageIcon icon = createImageIcon(iconUrl);
        ImageIcon resizedIcon = resizeImageIcon(icon, 50, 50);
        component = makeTextPanel(tabName);
        this.tabbedPane.addTab(tabName, resizedIcon, component,
                "Does nothing");

    }

    private ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = getClass().getClassLoader().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    private JPanel makeTextPanel(String text) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setLayout(new GridLayout(1, 1));
        return panel;
    }

    private ImageIcon resizeImageIcon(ImageIcon icon, int width, int height) {
        Image image = icon.getImage();
        Image newImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(newImage);
    }

    /**
     * Este metodo se añade como respuesta a la accion de importar empleados
     * Abre un FileChooser para seleccionar el archivo json de empleados
     * Esta validado para que solo se pueda seleccionar un archivo xlsx(Excel)
     */
    private void openFileSelector() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(mainContainer);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            System.out.println(selectedFile.getAbsolutePath());
            String fileExtension = getFileNameExtension(selectedFile.getName());
            /*if (!fileExtension.equals("xlsx")) {
                JOptionPane.showMessageDialog(null, "Tipo de Archivo no valido");
                return;
            }*/
            processExcelFile(selectedFile);
        }
    }
    private String getFileNameExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1)
            return "";
        return filename.substring(lastDotIndex + 1);
    }
    /**
     * Este metodo se encarga de procesar el archivo xlsx seleccionado
     * Si el archivo es valido se procesa para llenar el json de empleados de la
     * ruta definida en el constructor
     *
     * @param excelFile -El archivo seleccionado
     * @throws IOException           -Si el archivo no es valido
     * @throws FileNotFoundException -Si el archivo no es encontrado
     *
     */
    private void processExcelFile(File excelFile) {
        try {
            FileInputStream inputStream = new FileInputStream(excelFile);
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < sheet.getLastRowNum(); i++) {
                if (i == 0)
                    continue;
                if (sheet.getRow(i).getCell(0).getCellType() == CellType.BLANK)
                    break;
                JSONObject jo = new JSONObject();
                for (int j = 0; j < sheet.getRow(i).getLastCellNum(); j++) {
                    Cell cell = sheet.getRow(i).getCell(j);
                    jo.put(sheet.getRow(0).getCell(j).getStringCellValue(), getCellValue(cell));
                }
                jsonArray.put(jo);
            }
            FileWriter empWriter = new FileWriter(this.empJsonUrl);
            empWriter.write(jsonArray.toString());
            empWriter.flush();
            empWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private String getCellValue(Cell cell) {
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue();
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf(cell.getNumericCellValue());
        }
        if (cell.getCellType() == CellType.BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        }
        return "";
    }
    private void initEmployeesFile(String filePath) {
        File file = new File(filePath);

        // Check if the file exists
        if (!file.exists()) {
            try {
                // Create the file
                boolean fileCreated = file.createNewFile();
                if (fileCreated) {
                    System.out.println("File created successfully: " + filePath);
                } else {
                    System.out.println("Failed to create the file: " + filePath);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("File already exists: " + filePath);
        }
    }

}