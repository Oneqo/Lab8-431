import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;

public class MyJavap {
    private JTextField fileChooserTextField;
    private JButton instantiateButton;
    private JButton executeButton;
    private JPanel mainPanel;
    private JTextArea outputs;
    private JList methodList;
    private JList constructorList;

    private Object objectUnderTesting;

    public MyJavap() {
        //Main JFrame
        JFrame frame = new JFrame();
        frame.add(mainPanel);
        frame.setSize(550,500);
        frame.setMinimumSize(new Dimension(550,500));
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
                    Class c = getClassFromPath(fileChooser.getSelectedFile());
                    populateConstructorList(c);
                    populateMethodList(c);
                }
            }
        });
        //Instantiates an object
        instantiateButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if(!constructorList.isSelectionEmpty()){
                    ConstructorWrapper constructor = (ConstructorWrapper) constructorList.getSelectedValue();
                    try{
                        objectUnderTesting = constructor.instatiate();
                    }catch (Exception er){
                        //If object creation fails for some reason, set the object reference to null.
                        System.err.println(er);
                        objectUnderTesting = null;
                    }
                }
            }
        });
        //Runs methods
        executeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                //TODO: Mikayla, place your code for invoking a selected method in here
                //You may refer to objectUnderTesting variable as the source object. (Make sure it's not null before using tho)
            }
        });
    }

    private Class getClassFromPath(File selectedFile) {
        //Set variables
        Class c = null;
        boolean isDone = false;
        //Get file name
        StringBuilder name = new StringBuilder();
        String fileName = selectedFile.getName();
        name.append(fileName);
        name.delete(fileName.length()-6, fileName.length());
        //Get URL to the directory
        File parent = selectedFile.getParentFile();
        URL[] classDirectoryURLs = new URL[1];
        do{
            try{
                classDirectoryURLs[0] = parent.toURI().toURL();
                URLClassLoader loader = new URLClassLoader(classDirectoryURLs);
                //Try to get a class object indicated by className
                c = Class.forName(name.toString(), true, loader);
                isDone = true;
            }catch (NoClassDefFoundError err){
                //Happens if URL is not valid, so we update the name, URL and try again
                if(parent.getParentFile()!=null){
                    name.insert(0,'.');
                    name.insert(0,parent.getName());
                    parent = parent.getParentFile();
                }else{
                    isDone = true;
                }
            }catch (Exception exc){
                System.err.println(exc);
            }
        }while(!isDone);
        return c;
    }

    //This function populates JList that shows constructors
    private void populateConstructorList(Class c){
        Object[] constructors = c.getConstructors();
        Object[] wrappedConstructors = new ConstructorWrapper[constructors.length];
        //Wrap Constructor objects before placing them into JList
        for(int i = 0; i != constructors.length; i++){
            wrappedConstructors[i] = new ConstructorWrapper((Constructor)constructors[i]);
        }
        constructorList.setListData(wrappedConstructors);
    }
    //This function populates JList that shows methods
    private void populateMethodList(Class c){
        //TODO: This is placeholder code. Feel free to change it
        Object[] methods = c.getDeclaredMethods();
        methodList.setListData(methods);
    }

    //Entry-point
    public static void main(String[] args) {
        MyJavap app = new MyJavap();
    }
}
