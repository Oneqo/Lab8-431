import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class MyJavap {
    private JTextField fileChooserTextField;
    private JButton instantiateButton;
    private JButton executeButton;
    private JPanel mainPanel;
    private JLabel topPrompt;
    private JLabel constructorPrompt;
    private JLabel methodPrompt;
    private JTextArea outputs;
    private JList methodList;
    private JList constructorList;
    private File selectedFile;

    public MyJavap() {
        JFrame frame = new JFrame();
        frame.add(mainPanel);
        frame.setSize(550,500);
        frame.setMinimumSize(new Dimension(350,300));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        //Selector textbox
        fileChooserTextField.setEditable(true);
        fileChooserTextField.setEnabled(false);

        //Outputs
        outputs.setEditable(false);

        //Action listeners
        fileChooserTextField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Class file filter", "class");
                fileChooser.setFileFilter(filter);
                int returnValue = fileChooser.showOpenDialog(mainPanel);
                if(returnValue == JFileChooser.APPROVE_OPTION){
                    fileChooserTextField.setText(fileChooser.getSelectedFile().getPath());
                    selectedFile = fileChooser.getSelectedFile();
                    URL[] url = new URL[1];
                    Class c;
                    try{
                        url[0] = selectedFile.getParentFile().toURI().toURL();
                        URLClassLoader classLoader = new URLClassLoader(url);
                        String name = selectedFile.getName();
                        c = Class.forName(name.substring(0,name.length()-6),true, classLoader); //Name without ".class" at the end
                        populateConstructorList(c);
                        populateMethodList(c);
                    }catch (Exception exc){
                        System.out.println("ALARAMRAMARMA!.class");
                    }
                }
            }
        });
    }
    private void populateConstructorList(Class c){
        Object[] constructors = c.getConstructors();
        constructorList.setListData(constructors);
    }
    private void populateMethodList(Class c){
        Object[] methods = c.getDeclaredMethods();
        methodList.setListData(methods);
    }
}
