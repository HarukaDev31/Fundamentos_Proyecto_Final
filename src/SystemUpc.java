import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.ss.usermodel.*;
import java.io.FileNotFoundException;
import org.json.*;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import java.util.Iterator;

/**
 * Este metodo es usado para la inicializacion de la GUI.
 * Si deseas agregar nuevos componentes se tienen que agregar en esta clase
 */
public class SystemUpc {
    private JFrame mainContainer;
    private JPanel itemContainer,emptabNomAp,emptabempCode,emptabAfi;
    private JMenuBar menuBar;
    private JMenu file,config;
    private JMenuItem fmi1,cmi1;
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
        initFile(empJsonUrl);
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
        this.mainContainer = new JFrame(    "System Upc");
        this.mainContainer.setSize(800, 500);
        this.mainContainer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // init item container
        this.itemContainer = new JPanel();
        this.itemContainer.setSize(500, 500);
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

        /// Crear Menu,añadir submenu,agregar funcionalidad
        this.file = new JMenu("File");
        this.fmi1 = new JMenuItem("Importar Empleados");
        this.file.add(this.fmi1);
        this.fmi1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                JDialog dialog=createModal(new JLabel("Label 1"), new JTextField("Text 1"), new JLabel("Label 2"), new JTextField("Text 2"));
                dialog.setVisible(true);
                //openFileSelector();
            }
        });
        // Añadir menu a la barra de menu
        this.menuBar.add(this.file);
        this.mainContainer.setJMenuBar(this.menuBar);
        this.mainContainer.setVisible(true);

    }

    private void initTabbedPane() {
        this.tabbedPane = new JTabbedPane();
        this.tabbedPane.setTabPlacement(JTabbedPane.TOP);
        this.empTab = createTab("emp.png", "Empleados");
        this.payrollTab = createTab("emp.png", "Boletas");
        this.itemContainer.add(this.tabbedPane);
        this.emptabNomAp = createComboBox(new String[]{"Nombre asdasd asdasdasdsadasda","Apellido"},new JLabel("Filtrar por Nombre y Apellido:"));
        this.emptabempCode = createComboBox(new String[]{"Nombre","Apellido"},new JLabel("Filtrar por Codigo de Empleado:"));
        this.emptabAfi = createComboBox(new String[]{"Nombre","Apellido"},new JLabel("Filtrar por Afiliacion:"));
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(this.emptabNomAp);
        panel.add(this.emptabempCode);
        panel.add(this.emptabAfi);
        this.empTab.add(panel);
        getEmployees();

    }

    /**
     * Este metodo crea tabs y los añade al tab que inicializado al principio
     *
     * @param iconUrl  -La ruta de la imagen que se mostrara en el tab
     * @param tabName     -El titulo que se mostrara en el tab
     */
    private JComponent createTab(String iconUrl, String tabName) {
        ImageIcon icon = createImageIcon(iconUrl);
        ImageIcon resizedIcon = resizeImageIcon(icon, 50, 50);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JComponent component = makeTextPanel(tabName);

        panel.add(component);

        // Añade cualquier otro componente aquí, uno debajo del otro

        JScrollPane scrollPane = new JScrollPane(panel); // Si hay demasiados componentes para que quepan en la pantalla
        this.tabbedPane.addTab(tabName, resizedIcon, scrollPane, "Does nothing");
        return panel;
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
    private void initFile(String filePath) {
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

    private void getEmployees(){
        JTable table = new JTable();

        // Crear un DefaultTableModel para el JTable
        DefaultTableModel model = new DefaultTableModel(){
            @Override
            public boolean isCellEditable(int row, int column) {
                // Hacer que todas las celdas sean no editables
                return false;
            }
        };

        // Asignar el modelo al JTable
        table.setModel(model);
        //Read Json file
        try (FileReader reader = new FileReader(this.empJsonUrl)) {
            // Create JSON array from JSON file
            JSONArray jsonArray = new JSONArray(new JSONTokener(reader));

            // Get the first JSON object to set column names
            JSONObject firstObject = jsonArray.getJSONObject(0);

            // Set column names based on keys in the first JSON object
            Iterator<String> keys = firstObject.keys();
            while (keys.hasNext()) {
                String columnName = keys.next();
                model.addColumn(columnName);
            }

            // Fill the table model with data from JSON array
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject rowData = jsonArray.getJSONObject(i);
                Object[] row = new Object[model.getColumnCount()];
                for (int j = 0; j < model.getColumnCount(); j++) {
                    row[j] = rowData.get(model.getColumnName(j));
                }
                model.addRow(row);
            }

            JScrollPane scrollPane = new JScrollPane(table);
            this.empTab.add(scrollPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JPanel createComboBox(String[] items,JLabel label){

        JPanel panel = new JPanel();
        JComboBox comboBox = new JComboBox(items);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(label);
        panel.add(comboBox);
        return panel;
    }
    /**
     * Este metodo se encarga de crear un modal y añadirle los componentes que se le pasen
     * de forma vertical uno debajo de otro
     * @param components -Los componentes que se mostraran en el modal
     * @return JDialog -El modal creado
     * Uso:
     * JDialog dialog=createModal(new JLabel("Label 1"), new JTextField("Text 1"), new JLabel("Label 2"), new JTextField("Text 2"));
     * dialog.setVisible(true);
     *
     */
    private JDialog createModal(JComponent ...components){
        JDialog dialog = new JDialog();
        dialog.setSize(300,300);
        dialog.setModal(true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.Y_AXIS));
        for (JComponent component : components) {
            dialog.add(component);
        }
        return dialog;
    }
}